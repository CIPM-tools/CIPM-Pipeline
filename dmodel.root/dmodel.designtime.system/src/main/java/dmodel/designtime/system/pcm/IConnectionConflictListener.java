package dmodel.designtime.system.pcm;

import dmodel.designtime.system.pcm.data.ConnectionConflict;

public interface IConnectionConflictListener {

	public void conflict(ConnectionConflict conflict);

}
