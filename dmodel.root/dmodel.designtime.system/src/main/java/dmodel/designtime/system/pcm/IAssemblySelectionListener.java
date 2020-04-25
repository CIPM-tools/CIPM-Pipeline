package dmodel.designtime.system.pcm;

import dmodel.designtime.system.pcm.data.AssemblyConflict;

public interface IAssemblySelectionListener {

	public void conflict(AssemblyConflict conflict);

}
