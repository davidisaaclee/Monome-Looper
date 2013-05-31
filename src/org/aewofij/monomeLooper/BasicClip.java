package org.aewofij.monomeLooper;

public class BasicClip implements Clip {
	private final MLEngine _output;
	private final String _id;
	private ClipState _state;

	public BasicClip(String id, MLEngine output) {
		_id = id;
		_output = output;

		setState(ClipState.NONE);
	}

	public ClipState getState() {
		return _state;
	}

	public void setState(ClipState newState) {
		_state = newState;
	}

	public String getID() {
		return _id;
	}
	
	public void play() {
		_output.playClip(this);
		setState(ClipState.PLAY);
	}

	public void stop() {
		_output.stopClip(this);
		setState(ClipState.STOP);
	}

	public void record() {
		_output.recordClip(this);
		setState(ClipState.RECORD);
	}

	public void delete() {
		_output.removeClip(this);
		setState(ClipState.NONE);
	}

	public String toString() {
		return "Clip " + _id;
	}
}