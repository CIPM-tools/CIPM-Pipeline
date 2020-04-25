package dmodel.base.vsum.util;

import org.palladiosimulator.pcm.resourcetype.ProcessingResourceType;
import org.palladiosimulator.pcm.resourcetype.ResourceRepository;
import org.palladiosimulator.pcm.resourcetype.SchedulingPolicy;

import dmodel.base.shared.pcm.util.PCMUtils;

public class ReactionHelper {
	private static final ResourceRepository DEFAULT_RESOURCE_REPO = PCMUtils.getDefaultResourceRepository();
	private static final ProcessingResourceType CPU_PROC_TYPE = PCMUtils.getElementById(DEFAULT_RESOURCE_REPO,
			ProcessingResourceType.class, "_oro4gG3fEdy4YaaT-RYrLQ");
	private static final SchedulingPolicy CPU_SHARING_POLICY = PCMUtils.getElementById(DEFAULT_RESOURCE_REPO,
			SchedulingPolicy.class, "ProcessorSharing");
	
	public static ProcessingResourceType getCPUProcessingResourceType() {
		return CPU_PROC_TYPE;
	}
	
	public static SchedulingPolicy getProcessSharingSchedulingPolicy() {
		return CPU_SHARING_POLICY;
	}
}
