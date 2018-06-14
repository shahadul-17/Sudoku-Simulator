package sudoku.simulator;

public interface SimulationListener {
	
	public void simulationStopped();
	public void simulationFailed();
	public void validationFailed();
	public void noEmptyGrid();
	
}