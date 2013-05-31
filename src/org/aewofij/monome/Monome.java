package org.aewofij.monome;

import java.util.LinkedList;
import java.util.Collection;
import java.io.IOException;

import com.illposed.osc.*;

public class Monome {
	/** Name of this Monome. */
	private String 		_name;
	/** Internal representation of Monome display. */
	private int[][] 	_display;
	/** OSC port for incoming messages. */
	private OSCPortIn 	_portIn;
	/** OSC port for outgoing messages. */
	private OSCPortOut 	_portOut;

	// Uses "monome", localhost
	public Monome(int rows, int cols, int inPort, int outPort) throws java.net.SocketException, 
																	java.net.UnknownHostException {
		this("monome", rows, cols, inPort, outPort, java.net.InetAddress.getLocalHost());
	}

	public Monome(int rows, int cols, int inPort, int outPort, java.net.InetAddress address) throws java.net.SocketException {
		this("monome", rows, cols, inPort, outPort, address);
	}

	/** 
	 *	@param 	name 	the name of this Monome to be used in OSC messages.
	 *	@param 	rows 	the number of rows this Monome will have.
	 *	@param 	cols 	the number of columns this Monome will have.
	 *	@param 	inPort 	the port for incoming messages from this Monome.
	 *	@param 	outPort the port for outgoing messages to this Monome.
	 *	@param 	address the address for outgoing messages to this Monome.
	 */
	public Monome(String name, int rows, int cols, int inPort, int outPort, java.net.InetAddress address) throws java.net.SocketException {
		_name = name;

		// Initialize OSC ports.
		_portIn = new OSCPortIn(inPort);
		System.out.println("Input port is " + inPort);
		_portOut = new OSCPortOut(address, outPort);
		System.out.println("Output port is " + outPort + " (" + address.getHostAddress() + ")");

		// Initialize internal representation.
		_display = new int[rows][cols];
		for (int[] row : _display) {
			for (int cell : row) {
				cell = 0;
			}
		}

		// Fancy turn-on thing.
		// try {
		// 	int iterations = 50;

		// 	for (int i = 0; i < iterations; i++) {
		// 		int[][] toDisplay = new int[16][8];
		// 		int counter = i;

		// 		for (int x = 0; x < toDisplay.length; x++) {
		// 			for (int y = 0; y < toDisplay[x].length; y++) {
		// 				toDisplay[x][y] = (int)Math.abs(((Math.sin((double)(counter++) / 3.14159) * 16.) % 16) 
		// 									* (i < (iterations / 2.) ? (double)(i) : (double)(iterations-i)) / ((double)iterations / 2.));
		// 			}
		// 		}

		// 		set(toDisplay);

		// 		Thread.sleep(40);
		// 	}

		// 	clear();
		// } catch (InterruptedException e) {
		// 	System.err.println("ERROR: " + e.getMessage());
		// }
		try {
			setAll(15);
			Thread.sleep(50);
			setAll(0);
			Thread.sleep(50);
			setAll(15);
			Thread.sleep(50);
			setAll(0);
		} catch (InterruptedException e) {
			System.err.println("ERROR: " + e.getMessage());
		}

		System.out.println("Monome with " + numberOfRows() + " rows, " + numberOfColumns() + " columns.");
	}

	/** Adds an OSCListener the OSCPortIn to listen for incoming messages. 
	 *
	 *	@param 	listener 	the OSCListener to handle incoming messages.
	 */
	public void addListener(OSCListener listener) {
		System.out.println("adding to  " + "/" + _name + "/grid/key");
		_portIn.addListener("/" + _name + "/grid/key", listener);
	}

	public void startListening() {
		_portIn.startListening();
	}

	public void stopListening() {
		_portIn.stopListening();
	}

	public int numberOfRows() {
		return _display.length;
	}

	public int numberOfColumns() {
		return _display[0].length;
	}

	public void rotate(int degrees) {
		if ((degrees % 90) != 0) return;

		Collection<Object> args = new LinkedList<Object>();
		args.add(degrees);

		try {
			_portOut.send(new OSCMessage("/sys/rotation", args));
		} catch (IOException e) {
			System.err.println("ERROR: " + e.getMessage());
		}
	}

	/** Sets the specified cell to the specified value. 
	 * 
	 *	@param 	row 	the row of the cell to be set.
	 *	@param 	col 	the column of the cell to be set.
	 *	@param 	val 	the desired value of the cell.
	 */
	public void set(int row, int col, int val) {
		if (isValidCell(row, col)) {
			_display[row][col] = val;

			flush(row, col);
		}
	}

	/** Displays the provided matrix on the Monome. If the provided matrix
	 * 	 is smaller than this Monome, nothing happens. If the provided matrix
	 *	 is larger than this Monome, truncates to a submatrix.
	 *
	 *	@param 	display 	the matrix to be displayed on the Monome.
	 */
	public void set(int[][] display) {
		if ((display.length < _display.length) || (display[0].length < _display[0].length)) {
			// Input matrix is smaller than Monome, return.
			System.out.println("ERROR: Input matrix smaller than monome display, returning.");
			return;
		}

		for (int x = 0; x < _display.length; x++) {
			for (int y = 0; y < _display[x].length; y++) {
				_display[x][y] = display[x][y];
			}
		}

		flush();
	}

	/** Sets all cell values to the specified value. 
	 *
	 *	@param 	val 	the value to which all cells will be set.
	 */
	public void setAll(int val) {
		for (int i = 0; i < _display.length; i++) {
			for (int j = 0; j < _display[i].length; j++) {
				_display[i][j] = val;

				if (_display[i][j] != val) {
					System.out.println("BAD");
				}
			}
		}

		try {
			Collection<Object> args = new LinkedList<Object>();
			args.add(new Integer(val));

			_portOut.send(new OSCMessage("/" + _name + "/grid/led/level/all", args));
		} catch (IOException e) {
			System.err.println("ERROR: " + e.getMessage());
		}
	}

	public void toggle(int row, int col, int val) {
		if (isValidCell(row, col)) {
			set(row, col, (_display[row][col] > 0) ? 0 : val);
		}
	}

	/** Clears the display of the Monome. */
	public void clear() {
		setAll(0);
	}

	/* LAZY SETTERS: Use these to change a lot on the display at once.
	 *
	 * NOTE: These setters will not update the display until flush() is called. This is 
	 * 	to reduce the number of OSC messages being sent to the Monome. */

	/** Lazily sets the specified cell to the specified value.
	 * 
	 *	@param 	row 	the row of the cell to be set.
	 *	@param 	col 	the column of the cell to be set.
	 *	@param 	val 	the desired value of the cell.
	 */
	public void setLazy(int row, int col, int val) {
		if (isValidCell(row, col)) {
			_display[row][col] = val;
		}
	}

	/** Lazily sets a column of cells. If the value list is smaller than a column of this Monome, 
	 * 	 the remainder of the column is padded with 0s. If the value list is larger than a column of
	 *	 this Monome, the list is truncated.
	 *
	 *	@param 	xOffset 	the index of the column to write.
	 *	@param 	values 		an array indicating the desired values of the cells from top to bottom.
	 */
	public void setColumnLazy(int xOffset, int[] values) {
		for (int y = 0; y < _display[xOffset].length; y++) {
			_display[xOffset][y] = y < values.length ? values[y] : 0;
		}
	}

	/** Lazily sets a row of cells. If the value list is smaller than a row of this Monome, 
	 * 	 the remainder of the row is padded with 0s. If the value list is larger than a row of
	 *	 this Monome, the list is truncated.
	 *
	 *	@param 	yOffset 	the index of the row to write.
	 *	@param 	values 		an array indicating the desired values of the cells from left to right.
	 */
	public void setRowLazy(int yOffset, int[] values) {
		for (int x = 0; x < _display.length; x++) {
			_display[yOffset][x] = x < values.length ? values[x] : 0;
		}
	}

	/** Writes entire internal representation to Monome display. */
	public void flush() {
		Collection<Object> args = null;

		// We can only send 8x8 blocks at a time.
		for (int xOffset = 0; xOffset < (_display.length / 8); xOffset++) {
			for (int yOffset = 0; yOffset < (_display[0].length / 8); yOffset++) {
				args = new LinkedList<Object>();

				// Set offsets.
				args.add(new Integer(xOffset * 8));
				args.add(new Integer(yOffset * 8));

				for (int y = 0; y < 8; y++) {
					for (int x = 0; x < 8; x++) {
						args.add(new Integer(_display[x + (xOffset * 8)][y + (yOffset * 8)]))	;
					}
				}

				// send
				try {
					_portOut.send(new OSCMessage("/" + _name + "/grid/led/level/map", args));
				} catch (IOException e) {
					System.err.println("ERROR: " + e.getMessage());
				}
			}
		}
	}

	/** Writes the specified cell to Monome display, making it mirror
	 *	 the internal representation.
	 *
	 *	@param 	row 	the row of the cell to be updated.
	 * 	@param 	col 	the column of the cell to be updated.
	 */
	public void flush(int row, int col) {
		if (isValidCell(row, col)) {
			try {
				Collection<Object> args = new LinkedList<Object>();
				args.add(new Integer(row));
				args.add(new Integer(col));
				args.add(new Integer(_display[row][col]));

				_portOut.send(new OSCMessage("/" + _name + "/grid/led/level/set", args));
			} catch (IOException e) {
				System.err.println("ERROR: " + e.getMessage());
			}
		}
	}

	/** Tests if the specified cell is valid for this Monome.
	 *
	 *	@param 	row 	the row of the cell to be tested.
	 *	@param 	col 	the column of the cell to be tested.
	 */
	private boolean isValidCell(int row, int col) {
		return (row >= 0) && (col >= 0) && (row < _display.length) && (col < _display[0].length);
	}
}