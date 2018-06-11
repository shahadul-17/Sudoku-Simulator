package sudoku.simulator;

public interface SimulatorListener {
	
	public void simulationStopped();
	public void simulationFailed();
	public void validationFailed();
	public void noEmptyGrid();
	
}