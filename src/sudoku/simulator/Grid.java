package sudoku.simulator;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import javax.swing.border.CompoundBorder;

public class Grid extends JLabel implements FocusListener {
	
	public int w, x, y, z;		// WARNING: DO NOT CHANGE THE VALUES OF THESE VARIABLES...!!! READ ONLY ACCESS...!!!
	private static final int BORDER_WIDTH = 3;
	private static final long serialVersionUID = -493280532824123771L;
	
	private static Grid tempGrid;
	public static Grid[][][][] grids = new Grid[Main.GRID_SIZE][Main.GRID_SIZE][Main.GRID_SIZE][Main.GRID_SIZE];
	
	private static final Color COLOR_FOCUSED = new Color(230, 230, 255),
			COLOR_HIGHLIGHT = new Color(193, 240, 193),
			COLOR_INVALID = new Color(255, 204, 204),
			COLOR_INITIAL_VALUE = new Color(146, 66, 1);
	
	public Grid(int w, int x, int y, int z) {
		this.w = w;
		this.x = x;
		this.y = y;
		this.z = z;
		
		initialize();
	}
	
	private void initialize() {
		int bottom = -1, right = -1, referenceValue = Main.GRID_SIZE - 1;
		
		setOpaque(true);
		setBackground(Color.WHITE);
		setForeground(Color.DARK_GRAY);
		setBorder(BorderFactory.createDashedBorder(null, 5, 5));
		
		if (w != referenceValue && x == referenceValue) {
			bottom = BORDER_WIDTH;
		}
		
		if (y != referenceValue && z == referenceValue) {
			right = BORDER_WIDTH;
		}
		
		if (bottom > -1 || right > -1) {
			setBorder(new CompoundBorder(getBorder(), BorderFactory.createMatteBorder(-1, -1, bottom, right, Color.BLACK)));
		}
		
		setFont(new Font("Ariel", Font.PLAIN, 18));
		setHorizontalAlignment(SwingConstants.CENTER);
		addFocusListener(this);
	}
	
	public char getDigit() {
		String text = getText();
		
		switch (text.length()) {
		case 0:
		default:
			return '0';
		case 1:
			return text.charAt(0);
		}
	}
	
	public void setDigit(char digit) {
		if (digit == '0') {
			setText("");
		}
		else {
			setText("" + digit);
		}
	}
	
	public void highlight() {
		if (tempGrid != null) {
			tempGrid.setBackground(Color.WHITE);
		}
		
		setBackground(COLOR_HIGHLIGHT);
		
		tempGrid = this;
	}
	
	public void setValid() {
		setBackground(Color.WHITE);
	}
	
	public void setInvalid(boolean global) {
		if (global) {
			if (tempGrid != null) {
				tempGrid.setBackground(Color.WHITE);
			}
			
			tempGrid = this;
		}
		
		setBackground(COLOR_INVALID);
	}
	
	public void markAsInitialValue(boolean flag) {
		if (flag) {
			setForeground(COLOR_INITIAL_VALUE);
		}
		else {
			setForeground(Color.DARK_GRAY);
		}
	}
	
	@Override
	public void focusGained(FocusEvent event) {
		setBackground(COLOR_FOCUSED);
	}

	@Override
	public void focusLost(FocusEvent event) {
		setBackground(Color.WHITE);
	}
	
	@Override
	public String toString() {
		return "[ " + w + ", " + x + ", " + y + ", " + z + " ]";		// position of the grid in the board...
	}
	
	public static Grid getNextGrid(int direction, Grid currentGrid) {
		int w = currentGrid.w, x = currentGrid.x, y = currentGrid.y, z = currentGrid.z;
		
		switch (direction) {
		case 37:		// LEFT
			z--;
			
			if (z < 0) {
				z = Main.GRID_SIZE - 1;
				y--;
				
				if (y < 0) {
					y = z;
				}
			}
			
			break;
		case 38:		// UP
			x--;
			
			if (x < 0) {
				x = Main.GRID_SIZE - 1;
				w--;
				
				if (w < 0) {
					w = x;
				}
			}
			
			break;
		case 39:		// RIGHT
			z++;
			
			if (z == Main.GRID_SIZE) {
				z = 0;
				y++;
				
				if (y == Main.GRID_SIZE) {
					y = z;
				}
			}
			
			break;
		case 40:		// DOWN
			x++;
			
			if (x == Main.GRID_SIZE) {
				x = 0;
				w++;
				
				if (w == Main.GRID_SIZE) {
					w = x;
				}
			}
			
			break;
		}
		
		return grids[w][x][y][z];
	}
	
	public static Grid getNextGrid(boolean withinSameCell, Grid currentGrid) {
		int w = currentGrid.w, x = currentGrid.x, y = currentGrid.y, z = currentGrid.z + 1;		// z++ selection moves right...
		
		if (z == Main.GRID_SIZE) {
			z = 0;
			x++;		// selection moves down...
			
			if (x == Main.GRID_SIZE) {
				x = 0;
				
				if (!withinSameCell) {
					y++;		// selection moves to the next (right) grid...
					
					if (y == Main.GRID_SIZE) {
						y = 0;
						w++;		// selection moves down...
						
						if (w == Main.GRID_SIZE) {
							w = 0;
						}
					}
				}
			}
		}
		
		return grids[w][x][y][z];
	}
	
}