package dmodel.pipeline.dt.system.pcm;

import dmodel.pipeline.dt.system.pcm.data.AssemblyConflict;

public interface IAssemblySelectionListener {

	public void conflict(AssemblyConflict conflict);

}
