package sudoku.simulator;

public class Simulator implements Runnable {
	
	private boolean backtrackingVisibile;
	private char flag = 0;		// initially flag is false...
	
	private SimulationListener simulationListener;
	
	private Grid initialGrid;
	
	public Simulator(boolean backtrackingVisible, Grid initialGrid) {
		this.backtrackingVisibile = backtrackingVisible;
		this.initialGrid = initialGrid;
	}
	
	public void setBacktrackingVisibility(boolean backtrackingVisible) {
		this.backtrackingVisibile = backtrackingVisible;
	}
	
	public void stop() {
		flag = 2;
		
		simulationListener.simulationStopped();
	}
	
	public void addSimulationListener(SimulationListener simulationListener) {
		this.simulationListener = simulationListener;
	}
	
	private boolean isValid(char digit, Grid currentGrid) {
		Grid tempGrid = currentGrid;
		
		for (int i = 39; i < 42; i++) {		// checking row and column for same value...
			do {
				if (i == 41) {
					tempGrid = Grid.getNextGrid(true, tempGrid);
				}
				else {
					tempGrid = Grid.getNextGrid(i, tempGrid);
				}
				
				if (digit == tempGrid.getDigit()) {
					return false;
				}
			}
			while (!tempGrid.equals(currentGrid));
		}
		
		return true;
	}
	
	public boolean areValuesValid() {
		boolean valid = true;
		int counter = 0;
		
		for (int w = 0; w < Main.GRID_SIZE; w++) {
			for (int x = 0; x < Main.GRID_SIZE; x++) {
				for (int y = 0; y < Main.GRID_SIZE; y++) {
					for (int z = 0; z < Main.GRID_SIZE; z++) {
						Grid grid = Grid.grids[w][x][y][z];
						
						char digit = grid.getDigit();
						boolean containsValue = (digit != '0');
						
						grid.markAsInitialValue(containsValue);
						
						if (containsValue) {
							counter++;
							
							grid.setDigit('0');
							
							if (isValid(digit, grid)) {
								grid.setValid();
							}
							else {
								valid = false;
								
								grid.setInvalid(false);
							}
							
							grid.setDigit(digit);
						}
					}
				}
			}
		}
		
		if (!valid) {
			simulationListener.validationFailed();
		}
		
		if (counter == Main.BOARD_SIZE * Main.BOARD_SIZE) {		// all grids are filled...
			simulationListener.noEmptyGrid();
			
			return false;
		}
		
		return valid;
	}
	
	public boolean simulate(Grid currentGrid) {
		if (flag == 2 || (flag == 1 && currentGrid.w == 0 && currentGrid.w == currentGrid.x &&
				currentGrid.x == currentGrid.y && currentGrid.y == currentGrid.z)) {
			return true;
		}
		
		flag = 1;		// this flag is used to identify if this method has been called once...
		
		if (backtrackingVisibile) {		// if backtracking is not visible, no thread sleep needed...
			try {
				Thread.sleep(20);
			}
			catch (Exception exception) {
				// don't need to handle this exception...
			}
		}
		
		if (currentGrid.getDigit() != '0') {
			return simulate(Grid.getNextGrid(false, currentGrid));
		}
		
		for (char digit = '1'; digit <= '9'; digit++) {
			if (isValid(digit, currentGrid)) {
				currentGrid.setDigit(digit);
				
				if (backtrackingVisibile) {		// if backtracking is not visible, no need to show colors...
					currentGrid.highlight();
				}
				
				if (simulate(Grid.getNextGrid(false, currentGrid))) {
					return true;
				}
			}
			else if (backtrackingVisibile) {		// if backtracking is not visible, no need to show colors...
				currentGrid.setInvalid(true);
			}
		}
		
		currentGrid.setDigit('0');		// clearing the grid for backtracking...
		
		return false;
	}
	
	@Override
	public void run() {
		if (simulate(initialGrid)) {
			simulationListener.simulationStopped();
		}
		else {
			simulationListener.simulationFailed();
		}
	}
	
}