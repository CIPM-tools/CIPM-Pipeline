package dmodel.pipeline.shared.pipeline;

public interface PortIDs {
	String T_BUILD_SERVICECALL_TREE = "sc";

	String T_VAL_PRE = "validation-pre";

	String T_SC_PCM_RESENV = "sc->resenv";
	String T_SC_PCM_ALLOCATION = "sc->allocation";
	String T_RESENV_PCM_ALLOCATION = "resenv->allocation";

	String T_ALLOCATION_PCM_SYSTEM = "allocation->system";
	String T_SC_PCM_SYSTEM = "sc->system";

	String T_SYSTEM_ROUTER = "system->router";
	String T_SC_ROUTER = "sc->router";
	String T_RAW_ROUTER = "raw->router";

	String T_FINAL_VALIDATION = "model->final";
	String T_RAW_FINAL_VALIDATION = "raw->final";
}
