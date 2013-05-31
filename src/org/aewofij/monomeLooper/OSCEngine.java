package org.aewofij.monomeLooper;

import java.net.*;

import java.util.*;
import java.io.IOException;

import com.illposed.osc.*;

public class OSCEngine implements MLEngine {
	private final String _address;
	private final OSCPortOut _oscOut;

	public OSCEngine(String oscAddress, int port) throws UnknownHostException, 
															SocketException {
		this(oscAddress, port, InetAddress.getLocalHost());
	}

	public OSCEngine(String oscAddress, int port, InetAddress netAddress) throws SocketException {
		_address = oscAddress;
		_oscOut = new OSCPortOut(netAddress, port);

		sendMessage("ready", 1);
	}

	public void playClip(Clip c) {
		sendMessage("clip/" + c.getID(), "start");
	}

 	public void stopClip(Clip c) {
 		sendMessage("clip/" + c.getID(), "stop");
 	}

 	public void recordClip(Clip c) {
 		sendMessage("clip/" + c.getID(), "record");
 	}

 	public void removeClip(Clip c) {
 		sendMessage("clip/" + c.getID(), "remove");
 	}

 	private void sendMessage(String address, Object singleArg) {
 		Collection<Object> args = new LinkedList<Object>();
 		args.add(singleArg);

 		try {
	 		_oscOut.send(new OSCMessage(_address + address, args));
	 		System.out.println("OSC OUT:\t" + _address + address + " " + singleArg);
	 	} catch (IOException e) {
	 		System.err.println("ERROR: " + e.getMessage());
	 	}
 	}
}