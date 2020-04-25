package dmodel.designtime.system.pcm.data;

public abstract class AbstractConflict<T> {

	private T solution;
	private boolean solved;

	protected AbstractConflict() {
		this.solved = false;
	}

	public T getSolution() {
		return solution;
	}

	public void setSolution(T solution) {
		this.solution = solution;
	}

	public boolean isSolved() {
		return solved;
	}

	public void setSolved(boolean solved) {
		this.solved = solved;
	}

}
