package dmodel.pipeline.rt.pipeline;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Sets;

import dmodel.pipeline.rt.pipeline.annotation.EntryInputPort;
import dmodel.pipeline.rt.pipeline.annotation.InputPorts;
import dmodel.pipeline.rt.pipeline.annotation.OutputPort;
import dmodel.pipeline.rt.pipeline.annotation.OutputPorts;
import dmodel.pipeline.rt.pipeline.annotation.PipelineEntryPoint;
import dmodel.pipeline.rt.pipeline.blackboard.RuntimePipelineBlackboard;

// TODO refactor
public abstract class AbstractIterativePipeline<S, B extends RuntimePipelineBlackboard> {
	private static final Logger LOG = LoggerFactory.getLogger(AbstractIterativePipelinePart.class);

	private List<NodeInformation> entryPoints;
	private Map<NodeInformation, List<Pair<NodeInformation, Integer>>> successorMapping;
	private Map<Class<?>, AbstractIterativePipelinePart<?>> instanceMapping;
	private Map<Method, NodeInformation> nodeInformationMapping;

	protected B blackboard;

	// queuing mechanism
	private boolean running;
	private Set<Method> endPoints;
	private LinkedList<S> queue;
	private AtomicInteger reachedEndpoints;
	private Optional<S> currentData;

	public AbstractIterativePipeline() {
		this.successorMapping = new HashMap<>();
		this.instanceMapping = new HashMap<>();
		this.nodeInformationMapping = new HashMap<>();
		this.entryPoints = new ArrayList<>();

		this.queue = new LinkedList<>();
		this.reachedEndpoints = new AtomicInteger();
	}

	public abstract void initBlackboard();

	protected abstract void onIterationFinished(S data);

	public void triggerPipeline(S data) {
		if (!running) {
			// reset pipeline states
			running = true;
			reachedEndpoints.set(0);
			this.reset();
			currentData = Optional.of(data);

			// trigger entries
			for (NodeInformation entry : entryPoints) {
				entry.setInput(data, 0);
			}
		} else {
			this.queue.add(data);
		}
	}

	protected synchronized void triggerEndpoint() {
		if (reachedEndpoints.incrementAndGet() == this.endPoints.size()) {
			// we finished
			running = false;
			onIterationFinished(currentData.get());

			// check queue
			if (this.queue.size() > 0) {
				this.triggerPipeline(this.queue.removeFirst());
			}
		}
	}

	protected void buildPipeline(Class<? extends AbstractIterativePipelinePart<B>> entryPointClass,
			IPipelineClassProvider<B> classProvider) throws InstantiationException, IllegalAccessException,
			IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException {
		long startMs = System.currentTimeMillis();
		LOG.info("Start building dModel Pipeline.");

		// reset
		this.successorMapping.clear();
		this.nodeInformationMapping.clear();
		this.running = false;
		this.queue.clear();
		this.endPoints = Sets.newHashSet();
		this.entryPoints.clear();

		if (entryPointClass.isAnnotationPresent(PipelineEntryPoint.class)) {
			// create entry instance
			AbstractIterativePipelinePart<B> entry;
			if (classProvider.contains(entryPointClass)) {
				entry = classProvider.provide(entryPointClass);
			} else {
				entry = entryPointClass.getConstructor().newInstance();
			}
			entry.setBlackboard(blackboard);

			// search start ports
			for (Method method : entryPointClass.getMethods()) {
				if (method.isAnnotationPresent(EntryInputPort.class)) {
					if (method.getParameterCount() == 1) {
						// add object entry
						instanceMapping.put(method.getDeclaringClass(), entry);

						NodeInformation nodeInfo = new NodeInformation(method, Executors.newFixedThreadPool(1),
								new PartInputProxy(1));

						// build tree
						if (method.isAnnotationPresent(OutputPorts.class)) {
							buildSubTree(nodeInfo, method.getAnnotation(OutputPorts.class), classProvider);
						} else {
							this.endPoints.add(method);
						}

						// add entry point
						nodeInformationMapping.put(method, nodeInfo);
						this.entryPoints.add(nodeInfo);
					}
				}
			}
		}

		// finished
		LOG.info("Finshed building dModel Pipeline (needed " + (System.currentTimeMillis() - startMs) + " ms).");
	}

	private void reset() {
		// reset blackboard
		AbstractIterativePipeline.this.initBlackboard();

		// reset all nodeinfos
		this.nodeInformationMapping.values().forEach(ni -> ni.reset());
	}

	@SuppressWarnings("unchecked") // maybe improve this later
	private void buildSubTree(NodeInformation parent, OutputPorts outputPorts, IPipelineClassProvider<B> classProvider)
			throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException,
			NoSuchMethodException, SecurityException {
		List<Pair<NodeInformation, Integer>> successors = new ArrayList<>();

		for (OutputPort sub : outputPorts.value()) {
			Class<? extends AbstractIterativePipelinePart<B>> subClass = (Class<? extends AbstractIterativePipelinePart<B>>) sub
					.to();
			for (Method method : subClass.getMethods()) {
				if (method.isAnnotationPresent(InputPorts.class)) {
					// resolve subpart
					AbstractIterativePipelinePart<?> subPartInstance;
					if (instanceMapping.containsKey(method.getDeclaringClass())) {
						subPartInstance = instanceMapping.get(method.getDeclaringClass());
					} else {
						if (classProvider.contains(subClass)) {
							subPartInstance = classProvider.provide(subClass);
						} else {
							subPartInstance = subClass.getConstructor().newInstance();
						}
						instanceMapping.put(method.getDeclaringClass(), subPartInstance);
						((AbstractIterativePipelinePart<B>) subPartInstance).setBlackboard(blackboard);
					}

					// get inputport
					InputPorts inputPorts = method.getAnnotation(InputPorts.class);
					int inputIndex = containsClass(inputPorts, sub.id());

					if (inputIndex >= 0) {
						NodeInformation currentInfo;
						if (nodeInformationMapping.containsKey(method)) {
							currentInfo = nodeInformationMapping.get(method);
						} else {
							PartInputProxy proxy = new PartInputProxy(inputPorts.value().length);
							ExecutorService executor = sub.async() ? Executors.newFixedThreadPool(1) : parent.executor;
							currentInfo = new NodeInformation(method, executor, proxy);
							nodeInformationMapping.put(method, currentInfo);
						}

						// connect
						successors.add(Pair.of(currentInfo, inputIndex));

						// recursion
						if (method.isAnnotationPresent(OutputPorts.class)) {
							buildSubTree(currentInfo, method.getAnnotation(OutputPorts.class), classProvider);
						} else {
							this.endPoints.add(method);
						}
					}
				}
			}
		}

		successorMapping.put(parent, successors);
	}

	protected void passOutput(Object result, NodeInformation nodeInformation) {
		if (this.successorMapping.containsKey(nodeInformation)) {
			List<Pair<NodeInformation, Integer>> successors = this.successorMapping.get(nodeInformation);
			if (successors.size() > 0) {
				successors.forEach(ni -> {
					ni.getLeft().setInput(result, ni.getRight());
				});
			} else {
				this.triggerEndpoint();
			}
		} else {
			this.triggerEndpoint();
		}
	}

	private int containsClass(InputPorts port, String id) {
		for (int i = 0; i < port.value().length; i++) {
			if (port.value()[i].value().equals(id)) {
				return i;
			}
		}
		return -1;
	}

	private class NodeInformation {
		private ExecutorService executor;
		private Method method;
		private Object instance;

		private PartInputProxy inputProxy;

		private NodeInformation(Method method, ExecutorService service, PartInputProxy proxy) {
			this.executor = service;
			this.method = method;
			this.inputProxy = proxy;

			this.instance = instanceMapping.get(method.getDeclaringClass());
		}

		private void setInput(Object input, int index) {
			if (inputProxy.setInput(input, index)) {
				// we can execute
				executor.submit(() -> {
					try {
						Object result = method.invoke(instance, inputProxy.getInputs());
						passOutput(result, NodeInformation.this);
					} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
						e.printStackTrace();
					}
				});
			}
		}

		private void reset() {
			// reset input
			inputProxy.reset();
		}
	}

	private class PartInputProxy {
		private List<Object> inputs;
		private int targetSize;
		private AtomicInteger currentSize;

		private PartInputProxy(int size) {
			targetSize = size;
			currentSize = new AtomicInteger(0);

			reset();
		}

		public Object[] getInputs() {
			return inputs.parallelStream().filter(i -> i != null).toArray();
		}

		private boolean setInput(Object input, int place) {
			if (input != null && place < inputs.size()) {
				inputs.set(place, input);
			}
			return currentSize.incrementAndGet() == targetSize;
		}

		private void reset() {
			currentSize.set(0);
			inputs = Arrays.asList(new Object[targetSize]);
		}

	}

}
