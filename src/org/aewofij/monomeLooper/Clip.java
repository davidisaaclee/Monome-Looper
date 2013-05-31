package org.aewofij.monomeLooper;

public interface Clip {
	public ClipState getState();
	public void setState(ClipState newState);

	public String getID();
	
	public void play();
	public void stop();
	public void record();

	public void delete();
}