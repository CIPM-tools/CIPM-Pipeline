package dmodel.pipeline.rt.pcm.repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.tuple.Pair;
import org.cache2k.Cache;
import org.cache2k.Cache2kBuilder;
import org.palladiosimulator.pcm.core.composition.AssemblyContext;
import org.palladiosimulator.pcm.resourceenvironment.ResourceContainer;
import org.palladiosimulator.pcm.seff.AbstractBranchTransition;
import org.palladiosimulator.pcm.seff.ResourceDemandingSEFF;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import dmodel.pipeline.core.facade.IRuntimeEnvironmentQueryFacade;
import dmodel.pipeline.core.facade.pcm.IAllocationQueryFacade;
import dmodel.pipeline.core.facade.pcm.IRepositoryQueryFacade;
import dmodel.pipeline.monitoring.records.BranchRecord;
import dmodel.pipeline.monitoring.records.LoopRecord;
import dmodel.pipeline.monitoring.records.PCMContextRecord;
import dmodel.pipeline.monitoring.records.ResourceUtilizationRecord;
import dmodel.pipeline.monitoring.records.ResponseTimeRecord;
import dmodel.pipeline.monitoring.records.ServiceCallRecord;
import dmodel.pipeline.monitoring.util.ServiceParametersWrapper;
import dmodel.pipeline.rt.runtimeenvironment.REModel.RuntimeResourceContainer;
import dmodel.pipeline.vsum.facade.ISpecificVsumFacade;
import lombok.extern.java.Log;

@Log
public class MonitoringDataSet {
	private List<PCMContextRecord> rawData;

	private Map<String, Map<String, List<ResponseTimeRecord>>> demandResponseTimes;
	private List<ResponseTimeRecord> responseTimes;
	private Map<String, List<ResponseTimeRecord>> serviceExecutionTimes;

	private List<ServiceCallRecord> serviceCalls;

	private List<ResourceUtilizationRecord> resourceUtilizations;
	private Map<String, List<ResourceUtilizationRecord>> resourceUtilizationMapping;
	private Map<String, Map<String, SortedMap<Long, Double>>> resourceUtilziationsSorted;

	private Map<String, List<BranchRecord>> branchMapping;
	private Map<String, List<LoopRecord>> loopMapping;

	private Map<String, ServiceParametersWrapper> parameterMapping;

	private ISpecificVsumFacade mapping;

	private IAllocationQueryFacade allocationModel;
	private IRepositoryQueryFacade repositoryModel;
	private IRuntimeEnvironmentQueryFacade rem;

	private Cache<String, String> containerCache = new Cache2kBuilder<String, String>() {
	}.expireAfterWrite(5, TimeUnit.MINUTES).resilienceDuration(30, TimeUnit.SECONDS).refreshAhead(false).build();
	private Cache<Pair<String, String>, String> assemblyCache = new Cache2kBuilder<Pair<String, String>, String>() {
	}.expireAfterWrite(5, TimeUnit.MINUTES).resilienceDuration(30, TimeUnit.SECONDS).refreshAhead(false).build();

	public MonitoringDataSet(List<PCMContextRecord> records, ISpecificVsumFacade mapping,
			IRuntimeEnvironmentQueryFacade rem, IAllocationQueryFacade allocationModel,
			IRepositoryQueryFacade repositoryModel) {
		this.rawData = records;
		this.mapping = mapping;
		this.allocationModel = allocationModel;
		this.repositoryModel = repositoryModel;
		this.rem = rem;

		prepare(records);
	}

	public List<PCMContextRecord> getRaw() {
		return rawData;
	}

	public String getAssemblyContextId(ServiceCallRecord serviceCall) {
		Pair<String, String> queuingPair = Pair.of(serviceCall.getServiceId(),
				resolveContainerId(serviceCall.getHostId()));
		if (assemblyCache.containsKey(queuingPair)) {
			return assemblyCache.get(queuingPair);
		} else {
			ResourceDemandingSEFF seff = repositoryModel.getServiceById(serviceCall.getServiceId());
			RuntimeResourceContainer remContainer = rem.getContainerById(serviceCall.getHostId());
			ResourceContainer envContainer = mapping.getCorrespondingResourceContainer(remContainer).orElse(null);

			if (envContainer != null) {
				List<AssemblyContext> assemblyCtx = allocationModel
						.getDeployedAssembly(seff.getBasicComponent_ServiceEffectSpecification(), envContainer);
				if (assemblyCtx.size() == 1) {
					assemblyCache.put(queuingPair, assemblyCtx.get(0).getId());
					return assemblyCtx.get(0).getId();
				}
			} else {
				log.warning("Failed to resolve mapped container. The previous transformations maybe failed.");
			}

			return null;
		}
	}

	private void prepare(List<PCMContextRecord> records) {
		prepareResponseTimes(records);
		prepareServiceCalls(records);
		prepareResourceUtilizations(records);
		prepareBranches(records);
		prepareLoops(records);
	}

	private void prepareLoops(List<PCMContextRecord> records) {
		loopMapping = Maps.newHashMap();

		records.stream().filter(f -> f instanceof LoopRecord).map(LoopRecord.class::cast).forEach(e -> {
			if (!loopMapping.containsKey(e.getLoopId())) {
				loopMapping.put(e.getLoopId(), Lists.newArrayList());
			}
			loopMapping.get(e.getLoopId()).add(e);
		});
	}

	private void prepareBranches(List<PCMContextRecord> records) {
		branchMapping = Maps.newHashMap();

		records.stream().filter(f -> f instanceof BranchRecord).map(BranchRecord.class::cast).forEach(e -> {
			AbstractBranchTransition transition = repositoryModel.getBranchTransition(e.getExecutedBranchId());
			String correspondingBranchId = transition.getBranchAction_AbstractBranchTransition().getId();
			if (!branchMapping.containsKey(correspondingBranchId)) {
				branchMapping.put(correspondingBranchId, Lists.newArrayList());
			}
			branchMapping.get(correspondingBranchId).add(e);
		});
	}

	private String resolveContainerId(String hostId) {
		String containerId;
		if (containerCache.containsKey(hostId)) {
			containerId = containerCache.get(hostId);
		} else {
			RuntimeResourceContainer belContainer = resolveRuntimeResourceContainer(hostId);
			ResourceContainer belEnvContainer = mapping.getCorrespondingResourceContainer(belContainer).orElse(null);

			containerCache.put(hostId, belEnvContainer.getId());
			containerId = belEnvContainer.getId();
		}
		return containerId;
	}

	private RuntimeResourceContainer resolveRuntimeResourceContainer(String hostId) {
		return rem.getContainerById(hostId);
	}

	private void prepareResourceUtilizations(List<PCMContextRecord> records) {
		resourceUtilizations = new ArrayList<>();
		resourceUtilizationMapping = Maps.newHashMap();
		resourceUtilziationsSorted = Maps.newHashMap();

		records.stream().filter(f -> f instanceof ResourceUtilizationRecord).map(ResourceUtilizationRecord.class::cast)
				.forEach(ru -> {
					resourceUtilizations.add(ru);

					String containerId = resolveContainerId(ru.getHostId());
					if (containerId != null) {
						if (!resourceUtilizationMapping.containsKey(containerId)) {
							resourceUtilizationMapping.put(containerId, Lists.newArrayList());
						}
						resourceUtilizationMapping.get(containerId).add(ru);
					}
				});

		resourceUtilizationMapping.entrySet().stream().forEach(entry -> {
			if (!resourceUtilziationsSorted.containsKey(entry.getKey())) {
				resourceUtilziationsSorted.put(entry.getKey(), Maps.newHashMap());
			}
			entry.getValue().stream().forEach(ru -> {
				Map<String, SortedMap<Long, Double>> inner = resourceUtilziationsSorted.get(entry.getKey());
				if (!inner.containsKey(ru.getResourceId())) {
					inner.put(ru.getResourceId(), Maps.newTreeMap());
				}
				inner.get(ru.getResourceId()).put(ru.getTimestamp(), ru.getUtilization());
			});
		});
	}

	private void prepareServiceCalls(List<PCMContextRecord> records) {
		serviceCalls = Lists.newArrayList();
		parameterMapping = Maps.newHashMap();

		records.stream().filter(f -> f instanceof ServiceCallRecord).map(ServiceCallRecord.class::cast).forEach(sc -> {
			serviceCalls.add(sc);
			parameterMapping.put(sc.getServiceExecutionId(),
					ServiceParametersWrapper.buildFromJson(sc.getParameters()));
		});
	}

	private void prepareResponseTimes(List<PCMContextRecord> records) {
		responseTimes = Lists.newArrayList();
		serviceExecutionTimes = Maps.newHashMap();
		demandResponseTimes = Maps.newHashMap();

		records.stream().filter(f -> f instanceof ResponseTimeRecord).map(ResponseTimeRecord.class::cast)
				.forEach(resp -> {
					responseTimes.add(resp);
					if (!demandResponseTimes.containsKey(resp.getInternalActionId())) {
						demandResponseTimes.put(resp.getInternalActionId(), new HashMap<>());
					}
					if (!demandResponseTimes.get(resp.getInternalActionId()).containsKey(resp.getResourceId())) {
						demandResponseTimes.get(resp.getInternalActionId()).put(resp.getResourceId(),
								new ArrayList<>());
					}
					demandResponseTimes.get(resp.getInternalActionId()).get(resp.getResourceId()).add(resp);

					if (!serviceExecutionTimes.containsKey(resp.getServiceExecutionId())) {
						serviceExecutionTimes.put(resp.getServiceExecutionId(), Lists.newArrayList());
					}
					serviceExecutionTimes.get(resp.getServiceExecutionId()).add(resp);
				});
	}

	public ResponseTimes getResponseTimes() {
		return new ResponseTimes();
	}

	public ServiceCalls getServiceCalls() {
		return new ServiceCalls();
	}

	public ResourceUtilizations getResourceUtilizations() {
		return new ResourceUtilizations();
	}

	public Loops getLoops() {
		return new Loops();
	}

	public Branches getBranches() {
		return new Branches();
	}

	public class ResponseTimes {
		public List<ResponseTimeRecord> getResponseTimes(String serviceExecutionId) {
			return serviceExecutionTimes.get(serviceExecutionId);
		}
	}

	public class ServiceCalls {
		public List<ServiceCallRecord> getServiceCalls() {
			return serviceCalls;
		}

		public ServiceParametersWrapper getParametersOfServiceCall(String serviceExecutionId) {
			return parameterMapping.get(serviceExecutionId);
		}
	}

	public class ResourceUtilizations {

		public Map<String, SortedMap<Long, Double>> getContainerUtilization(String containerId) {
			return resourceUtilziationsSorted.get(containerId);
		}
	}

	public class Loops {

		public Set<String> getLoopIds() {
			return loopMapping.keySet();
		}

		public List<LoopRecord> getLoopRecords(String loopId) {
			return loopMapping.get(loopId);
		}
	}

	public class Branches {

		public String getBranchNotExecutedId() {
			return BranchRecord.EXECUTED_BRANCH_ID;
		}

		public List<BranchRecord> getBranchRecords(String branchId) {
			return branchMapping.get(branchId);
		}

		public Set<String> getBranchIds() {
			return branchMapping.keySet();
		}
	}

}
