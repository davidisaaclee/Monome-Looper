package org.aewofij.monomeLooper;

import java.util.*;

public class ClipGrid {
	private final List<Track> _grid;
	private final MLEngine _output;

	private LooperView _view;

	public ClipGrid(MLEngine output) {
		_grid = new ArrayList<Track>();
		_output = output;
	}

	protected void addView(LooperView view) {
		_view = view;
	}

	/* * * * * PLAYBACK * * * * */

	public void triggerClip(int trackNumber, int clipIdx) {
		Track t = getTrack(trackNumber);
		t.triggerClip(clipIdx);

		_view.updateDisplay();
	}


	/** Plays the specified clip.
	 *
	 *	@param 	trackNumber 	the track index of the clip to be played.
	 *	@param 	clipIdx 		the clip index of the clip (within the track) to be played.
	 *
	 * 	@returns 	true if successful, else false
	 */
	@Deprecated
	public boolean playClip(int trackNumber, int clipIdx) {
		return playClip(getClip(trackNumber, clipIdx));
	}

	/** Plays the specified clip.
	 *
	 *	@param 	c 	the Clip to be played.
	 *
	 * 	@returns 	true if successful, else false
	 */
	protected boolean playClip(Clip c) {
		c.play();

		_view.updateDisplay();

		return true;
	}

	/** Stops the specified clip.
	 *
	 *	@param 	trackNumber 	the track index of the clip to be stopped.
	 *	@param 	clipIdx 		the clip index of the clip (within the track) to be stopped.
	 *
	 * 	@returns 	true if successful, else false
	 */
	@Deprecated
	public boolean stopClip(int trackNumber, int clipIdx) {
		return stopClip(getClip(trackNumber, clipIdx));
	}

	/** Stops the specified clip.
	 *
	 *	@param 	c 	the Clip to be stopped.
	 *
	 * 	@returns 	true if successful, else false
	 */
	protected boolean stopClip(Clip c) {
		c.stop();

		_view.updateDisplay();

		return true;
	}

	/** Records to the specified clip.
	 *
	 *	@param 	trackNumber 	the track index of the clip to be recorded.
	 *	@param 	clipIdx 		the clip index of the clip (within the track) to be recorded.
	 *
	 * 	@returns 	true if successful, else false
	 */
	@Deprecated
	public boolean recordClip(int trackNumber, int clipIdx) {
		return recordClip(getClip(trackNumber, clipIdx));
	}

	/** Records to the specified clip.
	 *
	 *	@param 	c 	the Clip to be stopped.
	 *
	 * 	@returns 	true if successful, else false
	 */
	protected boolean recordClip(Clip c) {
		c.record();

		_view.updateDisplay();

		return true;
	}

	/** Toggles playback of the specified clip.
	 *
	 *	@param 	trackNumber 	the track index of the clip to be toggled.
	 *	@param 	clipIdx 		the clip index of the clip (within the track) to be toggled.
	 */
	@Deprecated
	public void toggleClip(int trackNumber, int clipIdx) {
		toggleClip(getClip(trackNumber, clipIdx));
	}

	/** Toggles playback of the specified clip.
	 *
	 *	@param 	c 		the Clip to be toggled.
	 */
	@Deprecated
	protected void toggleClip(Clip c) {
		if (c.getState() == ClipState.PLAY) {
			stopClip(c);
		} else {
			playClip(c);
		}
	}


	/* * * * * ACCESS * * * * */


	/** Gets the specified Clip.
	 *
	 *	@param 	trackNumber 	the track index of the clip.
	 *	@param 	clipIdx 		the clip index of the clip (within the track).
	 *
	 * 	@returns 	the Clip, or null if no such clip
	 */
	protected Clip getClip(int trackNumber, int clipIdx) {
		// if (isValidClip(trackNumber, clipIdx)) {
		// 	Clip found = _grid.get(trackNumber).get(clipIdx);

		// 	if (found == null) {
		// 		// If there's no such clip, make it.
		// 		return makeClip(trackNumber, clipIdx);
		// 	} else {
		// 		return found;
		// 	}
		// } else {
		// 	// Invalid clip, return null.
		// 	return null;
		// }

		return getTrack(trackNumber).getClip(clipIdx);
	}

	/** Gets the specified Track, making one if it does not exist already.
	 *
	 *	@param 	trackNumber 	the index of the desired Track.
	 *
	 * 	@returns 	the Track.
	 */
	protected Track getTrack(int trackNumber) {
		if (trackNumber >= _grid.size()) {
			for (int i = _grid.size(); i <= trackNumber; i++) {
				_grid.add(new Track(i, _output));
			}
		}

		return _grid.get(trackNumber);
	}

	protected void deleteClip(int trackNumber, int clipIdx) {
		getClip(trackNumber, clipIdx).delete();

		_view.updateDisplay();
	}

	@Deprecated
	protected Clip makeClip(int trackNumber, int clipIdx) {
		return getTrack(trackNumber).newClip(clipIdx);
	}

	/* * * * * HELPERS * * * * */

	/** Checks if the provided clip index is valid.
	 *
	 *	@param 	trackNumber 	the track index of the clip to be toggled.
	 *	@param 	clipIdx 		the clip index of the clip (within the track) to be toggled.
	 *
	 *	@returns 				true if clip exists there, else false.
	 */
	@Deprecated
	private boolean isValidClip(int trackNumber, int clipIdx) {
		if (_grid.get(trackNumber) == null) {
			return false;
		}

		if (_grid.get(trackNumber).getClip(clipIdx) == null) {
			return false;
		}

		return true;
	}
}