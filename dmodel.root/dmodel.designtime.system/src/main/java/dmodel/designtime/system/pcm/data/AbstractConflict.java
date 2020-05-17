package dmodel.designtime.system.pcm.data;

import lombok.Data;

/**
 * Abstract class that is used to represent a conflict which should be resolved.
 * 
 * @author David Monschein
 *
 * @param <T> type of the solution
 */
@Data
public abstract class AbstractConflict<T> {

	private T solution;
	private boolean solved;

	/**
	 * Creates a new and unsolved conflict.
	 */
	protected AbstractConflict() {
		this.solved = false;
	}

}
