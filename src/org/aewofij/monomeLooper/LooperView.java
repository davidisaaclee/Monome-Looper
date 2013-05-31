package org.aewofij.monomeLooper;

import java.util.Date;

import com.illposed.osc.*;

import org.aewofij.monome.Monome;

public class LooperView {
	private final int GRID_ROWS = 7;
	private final int GRID_COLS = 15; 

	private boolean shiftDown = false;

	private class MLKeyListener implements OSCListener {
		public void acceptMessage(Date time, OSCMessage message) {
			// key: [track, clip, on/off]

			Object[] args = message.getArguments();

			if (args.length != 3) {
				System.err.println("ERROR: Invalid key input from Monome.");
			}

			Integer[] intArgs = new Integer[3];
			for (int i = 0; i < args.length; i++) {
				intArgs[i] = (Integer)args[i];
			}

			if (isInGrid(intArgs) && isTouchdown(intArgs)) {
				// If it's a touchdown in the grid...
				if (shiftDown) {
					_clipGrid.deleteClip(intArgs[0] + yOffset, intArgs[1] + xOffset);
				} else {
					_clipGrid.triggerClip(intArgs[0] + yOffset, intArgs[1] + xOffset);
				}
			} else if (isShift(intArgs)) {
				shiftDown = (intArgs[2] == 1);
				updateDisplay();
			} else if (isSceneLaunch(intArgs)) {
				// scene launch
				System.out.println("TODO:\tShould launch scene " + intArgs[1]);
			} else if (isArrowKey(intArgs) && isTouchdown(intArgs)) {
				if (intArgs[0] == 12 && intArgs[1] == 5) {
					yOffset++;
				} else if (intArgs[0] == 14 && intArgs[1] == 5 && yOffset != 0) {
					yOffset--;
				} else if (intArgs[0] == 13 && intArgs[1] == 4) {
					xOffset++;
				} else if (intArgs[0] == 13 && intArgs[1] == 6 && xOffset != 0) {
					xOffset--;
				} else if (intArgs[0] == 13 && intArgs[1] == 5) {
					xOffset = yOffset = 0;
				}

				updateDisplay();

				System.out.println("display at [" + xOffset + ", " + yOffset + "]");
			} else if (isMenuButton(intArgs)) {
				// do menu button stuff
				// if (shiftDown && isTouchdown(intArgs)) {
				// 	if ((intArgs[0] == 0) && (xOffset != 0)) {
				// 		xOffset--;
				// 	} else if (intArgs[0] == 1) {
				// 		xOffset++;
				// 	} else if ((intArgs[0] == 2) && (yOffset != 0)) {
				// 		yOffset--;
				// 	} else if (intArgs[0] == 3) {
				// 		yOffset++;
				// 	} else {
				// 		System.out.println("TODO:\tShould perform menu action " + intArgs[0]);
				// 	}
				// }

				System.out.println("TODO:\tShould perform menu action " + intArgs[0]);

				updateDisplay();
			}
		}

		private boolean isArrowKey(Integer[] args) {
			return shiftDown && 
					((args[0] == 12 && args[1] == 5) ||
					(args[0] == 14 && args[1] == 5) ||
					(args[0] == 13 && args[1] == 4) ||
					(args[0] == 13 && args[1] == 6) ||
					(args[0] == 13 && args[1] == 5));
		}

		// returns true if key touch in clip grid, else false
		private boolean isInGrid(Integer[] args) {
			return !isArrowKey(args) && (args[0] != 15) && (args[1] != 7);
		}

		// returns true if key touch down, else false
		private boolean isTouchdown(Integer[] args) {
			return args[2] == 1;
		}

		private boolean isShift(Integer[] args) {
			return (args[0] == 15) && (args[1] == 7);
		}

		private boolean isSceneLaunch(Integer[] args) {
			return args[0] == 15;
		}

		private boolean isMenuButton(Integer[] args) {
			return args[1] == 7;
		}
	}

	private final Monome _monome;
	private final ClipGrid _clipGrid;

	// View offsets.
	private int xOffset = 0;
	private int yOffset = 0;

	public LooperView(Monome monome, ClipGrid clipGrid) {
		_monome = monome;
		_clipGrid = clipGrid;
		_clipGrid.addView(this);

		// Add listeners to monome.
		_monome.addListener(new MLKeyListener());
		_monome.startListening();

		updateDisplay();
	}

	public void moveWindow(int dX, int dY) {
		xOffset += dX;
		yOffset += dY;

		updateDisplay();
	}

	protected void updateDisplay() {
		int[][] display = new int[16][8];

		// Populate grid.
		for (int trackNum = 0; trackNum < GRID_COLS; trackNum++) {
			Track track = _clipGrid.getTrack(trackNum + yOffset);

			for (int clipNum = 0; clipNum < GRID_ROWS; clipNum++) {
				Clip clip = track.getClip(clipNum + xOffset);

				if (clip.getState() == ClipState.NONE) {
					display[trackNum][clipNum] = 0;
				} else if (clip.getState() == ClipState.PLAY) {
					display[trackNum][clipNum] = 10;
				} else if (clip.getState() == ClipState.STOP) {
					display[trackNum][clipNum] = 3;
				} else if (clip.getState() == ClipState.RECORD) {
					display[trackNum][clipNum] = 15;
				}
			}
		}

		if (shiftDown) {
			// Build menubar.
			// display[0][7] = (xOffset == 0) ? 6 : 10;
			// display[1][7] = 10;
			// display[2][7] = (yOffset == 0) ? 6 : 10;;
			// display[3][7] = 10;

			// Alternative arrows?
			display[14][5] = (xOffset == 0) ? 6 : 10;
			display[12][5] = 10;
			display[13][6] = (yOffset == 0) ? 6 : 10;
			display[13][4] = 10;
		} else {
			// Build track edit bar.
			for (int i = 0; i < display.length; i++) {
				display[i][7] = 6;
			}
		}

		_monome.set(display);
	}
}