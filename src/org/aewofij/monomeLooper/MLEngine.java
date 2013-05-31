package org.aewofij.monomeLooper;

/** Interface between the main looper brain and the
 * 	 sound generation engine. */
public interface MLEngine {
 	public void playClip(Clip c);
 	public void stopClip(Clip c);
 	public void recordClip(Clip c);
 	public void removeClip(Clip c);
}