package org.aewofij.monomeLooper;

/** States for any Clip. 
 *
 *	PLAY 	clip is playing
 *	STOP 	clip is stopped (and rewound to start) 
 *	RECORD 	clip is being recorded
 * 	NONE 	clip has no data
 */
public enum ClipState {
	PLAY, STOP, RECORD, NONE
}