package sudoku.simulator;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.io.File;
import java.io.PrintWriter;
import java.util.Scanner;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

public class Frame extends JFrame implements ActionListener, MouseListener, MouseWheelListener, KeyListener, SimulatorListener {
	
	private static final long serialVersionUID = 6210472781464718706L;
	
	private Grid selectedGrid;
	private JCheckBox checkBoxShowBacktracking;
	private JButton buttonSimulate, buttonLoad, buttonSave;
	
	private Simulator simulator;
	
	public Frame(int width, int height, String title) {
		initialize(width, height, title);
		load();
	}
	
	private void initialize(int width, int height, String title) {
		setIconImage(new ImageIcon(this.getClass().getResource("/icons/icon.png")).getImage());
		setTitle(title);
		setSize(width, height);
		setLocationRelativeTo(null);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		JPanel contentPane = new JPanel(new BorderLayout());
		setContentPane(contentPane);
		
		JPanel board = new JPanel(new GridLayout(Main.BOARD_SIZE, Main.BOARD_SIZE));
		contentPane.add(board, BorderLayout.CENTER);
		
		for (int w = 0; w < Main.GRID_SIZE; w++) {
			for (int x = 0; x < Main.GRID_SIZE; x++) {
				for (int y = 0; y < Main.GRID_SIZE; y++) {
					for (int z = 0; z < Main.GRID_SIZE; z++) {
						Grid grid = new Grid(w, x, y, z);
						// grid.setText(grid.toString());		// FOR DEBUGGING PURPOSE...
						grid.setFocusable(true);
						grid.addMouseListener(this);
						grid.addMouseWheelListener(this);
						grid.addKeyListener(this);
						board.add(grid);
						
						Grid.grids[w][x][y][z] = grid;
					}
				}
			}
		}
		
		selectedGrid = Grid.grids[0][0][0][0];		// top left grid is initially selected...
		
		JPanel controlPanel = new JPanel(new BorderLayout());
		contentPane.add(controlPanel, BorderLayout.SOUTH);
		
		JPanel controlPanelCenter = new JPanel(new FlowLayout(FlowLayout.LEFT));
		controlPanel.add(controlPanelCenter, BorderLayout.CENTER);
		
		checkBoxShowBacktracking = new JCheckBox("Show backtracking");
		checkBoxShowBacktracking.addActionListener(this);
		controlPanelCenter.add(checkBoxShowBacktracking);
		
		JPanel controlPanelEast= new JPanel();
		controlPanel.add(controlPanelEast, BorderLayout.EAST);
		
		buttonSimulate = new JButton("Simulate");
		buttonSimulate.addActionListener(this);
		controlPanelEast.add(buttonSimulate);
		
		buttonLoad = new JButton("Load");
		buttonLoad.addActionListener(this);
		controlPanelEast.add(buttonLoad);
		
		buttonSave = new JButton("Save");
		buttonSave.addActionListener(this);
		controlPanelEast.add(buttonSave);
	}
	
	private void enableControls(boolean enable) {
		buttonLoad.setEnabled(enable);
		buttonSave.setEnabled(enable);
	}
	
	private void load() {
		File file = new File("saved-data.sudoku");
		
		if (!file.exists()) {
			return;
		}
		
		try {
			Scanner scanner = new Scanner(file);
			
			for (int w = 0; w < Main.GRID_SIZE; w++) {
				for (int x = 0; x < Main.GRID_SIZE && scanner.hasNextLine(); ) {
					String line = scanner.nextLine();
					
					if (line.length() == 0) {
						continue;
					}
					
					char[] charArray = line.toCharArray();
					
					for (int y = 0, i = 0; y < Main.GRID_SIZE; y++) {
						for (int z = 0; z < Main.GRID_SIZE; i++) {
							if (charArray[i] >= '0' && charArray[i] <= '9') {
								Grid grid = Grid.grids[w][x][y][z];
								
								grid.setDigit(charArray[i]);
								grid.markAsInitialValue(charArray[i] != '0');
								grid.setValid();
								
								z++;
							}
						}
					}
					
					x++;
				}
			}
			
			scanner.close();
		}
		catch (Exception exception) {
			JOptionPane.showMessageDialog(this, "An exception occured while loading saved data.", getTitle() + " - Load", JOptionPane.ERROR_MESSAGE);
		}
	}
	
	private void save() {
		StringBuilder stringBuilder = new StringBuilder(1024);
		
		for (int w = 0; w < Main.GRID_SIZE; w++) {
			for (int x = 0; x < Main.GRID_SIZE; x++) {
				for (int y = 0; y < Main.GRID_SIZE; y++) {
					for (int z = 0; z < Main.GRID_SIZE; z++) {
						Grid grid = Grid.grids[w][x][y][z];
						
						char digit = grid.getDigit();
						
						grid.markAsInitialValue(digit != '0');
						stringBuilder.append(digit);
						
						if (y != 2 && z == 2) {
							stringBuilder.append("    ");
						}
						else if (z < Main.GRID_SIZE - 1) {
							stringBuilder.append(", ");
						}
					}
				}
				
				if (w != 2 && x == 2) {
					stringBuilder.append("\n\n");
				}
				else if (x < Main.GRID_SIZE - 1) {
					stringBuilder.append('\n');
				}
			}
		}
		
		try {
			PrintWriter printWriter = new PrintWriter(new File("saved-data.sudoku"));
			printWriter.print(stringBuilder.toString());
			printWriter.flush();
			printWriter.close();
			
			JOptionPane.showMessageDialog(this, "Successfully saved data.", getTitle() + " - Save", JOptionPane.INFORMATION_MESSAGE);
		}
		catch (Exception exception) {
			JOptionPane.showMessageDialog(this, "An exception occured while saving data.", getTitle() + " - Save", JOptionPane.ERROR_MESSAGE);
		}
	}
	
	@Override
	public void actionPerformed(ActionEvent event) {
		if (event.getSource().equals(checkBoxShowBacktracking)) {
			if (simulator != null) {
				simulator.setBacktrackingVisibility(checkBoxShowBacktracking.isSelected());
			}
		}
		else {
			JButton button = (JButton)event.getSource();
			String text = button.getText();
			
			if (button.equals(buttonSimulate)) {
				if (text.equals("Simulate")) {
					button.setText("Stop");
					enableControls(false);
					
					simulator = new Simulator(checkBoxShowBacktracking.isSelected(), Grid.grids[0][0][0][0]);		// we will always start from the first grid of the board...
					simulator.addSimulatorListener(this);
					
					if (simulator.areValuesValid()) {
						new Thread(simulator).start();
					}
				}
				else {
					simulator.stop();
				}
			}
			else if (text.equals("Load")) {
				load();
			}
			else if (text.equals("Save")) {
				save();
			}
		}
	}
	
	@Override
	public void mouseWheelMoved(MouseWheelEvent event) {
		if (simulator != null) {
			return;
		}
		
		selectedGrid = (Grid)event.getSource();
		
		char digit = selectedGrid.getDigit();
		int clicks = event.getWheelRotation();
		
		if (digit < '9' && clicks < 0) {
			digit++;
		}
		else if (digit > '0' && clicks > 0) {
			digit--;
		}
		
		selectedGrid.setDigit(digit);
		selectedGrid.requestFocusInWindow();
	}

	@Override
	public void mouseClicked(MouseEvent event) {
		if (simulator != null) {
			return;
		}
		
		selectedGrid = (Grid)event.getSource();
		selectedGrid.requestFocusInWindow();
	}
	
	@Override
	public void keyTyped(KeyEvent event) {
		if (simulator != null) {
			return;
		}
		
		char digit = event.getKeyChar();
		
		if (digit >= '0' && digit <= '9') {
			selectedGrid.setDigit(digit);
			
			selectedGrid = Grid.getNextGrid(false, selectedGrid);
			selectedGrid.requestFocusInWindow();
		}
	}

	@Override
	public void keyPressed(KeyEvent event) {
		if (simulator != null) {
			return;
		}
		
		switch (event.getKeyCode()) {
		case KeyEvent.VK_BACK_SPACE:		// won't break this case because we want to move to next grid automatically...
			selectedGrid.setDigit('0');			// clear selected grid...
		
		case KeyEvent.VK_ENTER:
			selectedGrid = Grid.getNextGrid(false, selectedGrid);
			
			break;
		default:
			selectedGrid = Grid.getNextGrid(event.getKeyCode(), selectedGrid);
			
			break;
		}
		
		selectedGrid.requestFocusInWindow();
	}
	
	@Override
	public void simulationStopped() {
		simulator = null;
		
		enableControls(true);
		buttonSimulate.setText("Simulate");
	}
	
	@Override
	public void simulationFailed() {
		simulationStopped();
		
		JOptionPane.showMessageDialog(this, "Simulation failed. Please check initially provided values.", getTitle() + " - Simulation", JOptionPane.ERROR_MESSAGE);
	}
	
	@Override
	public void validationFailed() {
		simulationStopped();
		
		JOptionPane.showMessageDialog(this, "Please verify marked grids and retry.", getTitle() + " - Simulation", JOptionPane.ERROR_MESSAGE);
	}
	
	@Override
	public void noEmptyGrid() {
		simulationStopped();
	}
	
	@Override
	public void mousePressed(MouseEvent event) { }

	@Override
	public void mouseReleased(MouseEvent event) { }

	@Override
	public void mouseEntered(MouseEvent event) { }

	@Override
	public void mouseExited(MouseEvent event) { }
	
	@Override
	public void keyReleased(KeyEvent event) { }
	
}