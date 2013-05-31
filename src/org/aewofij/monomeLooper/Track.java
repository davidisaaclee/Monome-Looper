package org.aewofij.monomeLooper;

import java.util.*;

public class Track {
	private List<Clip> _clips;
	private MLEngine _output;
	private int _trackIndex;

	public Track(int trackIndex, MLEngine output) {
		_clips = new ArrayList<Clip>();
		_trackIndex = trackIndex;

		_output = output;
	}

	@Deprecated
	public Clip get(int clipIdx) {
		if (clipIdx >= _clips.size()) {
			for (int i = _clips.size(); i <= clipIdx; i++) {
				_clips.add(new BasicClip(makeClipID(clipIdx), _output));
			}
		}
		return _clips.get(clipIdx);
	}

	public Clip getClip(int clipIdx) {
		if (clipIdx >= _clips.size()) {
			for (int i = _clips.size(); i <= clipIdx; i++) {
				_clips.add(new BasicClip(makeClipID(clipIdx), _output));
			}
		}
		return _clips.get(clipIdx);
	}

	public void deleteClip(int clipIdx) {
		if (clipIdx < _clips.size()) {
			_clips.get(clipIdx).delete();
		}
	}

	public Clip newClip(int clipIdx) {
		Clip c = new BasicClip(makeClipID(clipIdx), _output);
		_clips.set(clipIdx, c);

		return c;
	}

	public void triggerClip(int clipIdx) {
		Clip theClip = get(clipIdx);

		if (theClip == null) {
			return;
		}

		stopAllClips();

		if ((theClip.getState() == ClipState.STOP) || (theClip.getState() == ClipState.PLAY)) {
			// queue playback
			theClip.play();
		} else if (theClip.getState() == ClipState.NONE) {
			// Start recording
			theClip.record();
		} else if (theClip.getState() == ClipState.RECORD) {
			// stop recording, start playback
			theClip.play();
		}
	}

	/* Helpers */

	private String makeClipID(int index) {
		return _trackIndex + "/" + index;
	}

	private void stopAllClips() {
		for (Clip c : _clips) {
			if (c.getState() == ClipState.PLAY || c.getState() == ClipState.RECORD)  {
				c.stop();
			}
		}
	}
}