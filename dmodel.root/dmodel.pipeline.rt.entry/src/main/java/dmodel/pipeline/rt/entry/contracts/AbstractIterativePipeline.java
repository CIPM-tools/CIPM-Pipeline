package dmodel.pipeline.rt.entry.contracts;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import dmodel.pipeline.rt.entry.contracts.annotation.InputPort;
import dmodel.pipeline.rt.entry.contracts.annotation.OutputPort;
import dmodel.pipeline.rt.entry.contracts.annotation.PipelineEntryPoint;

// TODO refactor
public abstract class AbstractIterativePipeline<S, B> {
	private static final Logger LOG = LoggerFactory.getLogger(AbstractIterativePipelinePart.class);

	private List<NodeInformation> entryPoints;
	private Map<NodeInformation, List<Pair<NodeInformation, Integer>>> successorMapping;
	private Map<Class<?>, AbstractIterativePipelinePart<?>> instanceMapping;
	private Map<Method, NodeInformation> nodeInformationMapping;

	protected B blackboard;

	// queuing mechanism
	private boolean running;
	private int endPoints;
	private LinkedList<S> queue;
	private AtomicInteger reachedEndpoints;

	public AbstractIterativePipeline() {
		this.successorMapping = new HashMap<>();
		this.instanceMapping = new HashMap<>();
		this.nodeInformationMapping = new HashMap<>();
		this.entryPoints = new ArrayList<>();

		this.queue = new LinkedList<>();
		this.reachedEndpoints = new AtomicInteger();
	}

	public abstract void initBlackboard();

	protected abstract void onIterationFinished();

	public void triggerPipeline(S data) {
		if (!running) {
			// reset pipeline states
			running = true;
			reachedEndpoints.set(0);
			this.reset();

			// trigger entries
			for (NodeInformation entry : entryPoints) {
				entry.setInput(data, 0);
			}
		} else {
			this.queue.add(data);
		}
	}

	protected void triggerEndpoint() {
		if (reachedEndpoints.incrementAndGet() == this.endPoints) {
			// we finished
			running = false;
			onIterationFinished();

			// check queue
			if (this.queue.size() > 0) {
				this.triggerPipeline(this.queue.removeFirst());
			}
		}
	}

	protected void buildPipeline(Class<? extends AbstractIterativePipelinePart<B>> entryPointClass)
			throws InstantiationException, IllegalAccessException {
		long startMs = System.currentTimeMillis();
		LOG.info("Start building dModel Pipeline.");

		// reset
		this.successorMapping.clear();
		this.nodeInformationMapping.clear();
		this.running = false;
		this.queue.clear();
		this.endPoints = 0;
		this.entryPoints.clear();

		if (entryPointClass.isAnnotationPresent(PipelineEntryPoint.class)) {
			// create entry instance
			AbstractIterativePipelinePart<B> entry = entryPointClass.newInstance();
			entry.setBlackboard(blackboard);

			// search start ports
			for (Method method : entryPointClass.getMethods()) {
				if (method.isAnnotationPresent(OutputPort.class)) {
					if (method.getParameterCount() == 1) {
						// add object entry
						instanceMapping.put(method.getDeclaringClass(), entry);

						NodeInformation nodeInfo = new NodeInformation(method, Executors.newSingleThreadExecutor(),
								new PartInputProxy(1));

						// build tree
						buildSubTree(nodeInfo, method.getAnnotation(OutputPort.class));

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
	private void buildSubTree(NodeInformation parent, OutputPort outputPort)
			throws InstantiationException, IllegalAccessException {

		List<Pair<NodeInformation, Integer>> successors = new ArrayList<>();
		Class<?> enclosingPart = parent.method.getDeclaringClass();

		for (Class<? extends AbstractIterativePipelinePart<?>> sub : outputPort.to()) {
			for (Method method : sub.getMethods()) {
				if (method.isAnnotationPresent(InputPort.class)) {
					// resolve subpart
					AbstractIterativePipelinePart<?> subPartInstance;
					if (instanceMapping.containsKey(method.getDeclaringClass())) {
						subPartInstance = instanceMapping.get(method.getDeclaringClass());
					} else {
						subPartInstance = sub.newInstance();
						instanceMapping.put(method.getDeclaringClass(), subPartInstance);
						((AbstractIterativePipelinePart<B>) subPartInstance).setBlackboard(blackboard);
					}

					// get inputport
					InputPort inputPort = method.getAnnotation(InputPort.class);
					int inputIndex = containsClass(inputPort, enclosingPart);

					if (inputIndex >= 0) {
						NodeInformation currentInfo;
						if (nodeInformationMapping.containsKey(method)) {
							currentInfo = nodeInformationMapping.get(method);
						} else {
							PartInputProxy proxy = new PartInputProxy(inputPort.from().length);
							ExecutorService executor = outputPort.async() ? Executors.newSingleThreadExecutor()
									: parent.executor;
							currentInfo = new NodeInformation(method, executor, proxy);
							nodeInformationMapping.put(method, currentInfo);
						}

						// connect
						successors.add(Pair.of(currentInfo, inputIndex));

						// recursion
						if (method.isAnnotationPresent(OutputPort.class)) {
							buildSubTree(currentInfo, method.getAnnotation(OutputPort.class));
						} else {
							// this is an end point
							this.endPoints++;
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

	private int containsClass(InputPort port, Class<?> clz) {
		for (int i = 0; i < port.from().length; i++) {
			if (port.from()[i].equals(clz)) {
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
			inputs.set(place, input);
			return currentSize.incrementAndGet() == targetSize;
		}

		private void reset() {
			currentSize.set(0);
			inputs = Arrays.asList(new Object[targetSize]);
		}

	}

}
