package sudoku.simulator;

public class Main {
	
	public static final int GRID_SIZE = 3, BOARD_SIZE = GRID_SIZE * GRID_SIZE;
	
	private static final String TITLE = "Sudoku Simulator";
	
	public static void main(String[] args) {
		new Frame(600, 600, TITLE).setVisible(true);
	}
	
}