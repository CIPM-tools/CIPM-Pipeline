package dmodel.pipeline.dt.system.pcm;

import dmodel.pipeline.dt.system.pcm.data.ConnectionConflict;

public interface IConnectionConflictListener {

	public void conflict(ConnectionConflict conflict);

}
