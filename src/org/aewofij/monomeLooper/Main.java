package org.aewofij.monomeLooper;

import java.util.Scanner;

import org.aewofij.monome.Monome;

public class Main {
	private static Looper _theLooper;
	private static Monome _monome;

	public static void main(String[] args) {
		Scanner scanner = new Scanner(System.in);
		String buffer = null;

		try {
			_monome = new Monome(16, 8, 8000, 8001);
			_monome.rotate(0);
			_theLooper = new Looper(_monome, new OSCEngine("/looper/", 8002));
		} catch (Exception e) {
			System.out.println("ERROR: " + e.getMessage());
			System.exit(1);
		}

		System.out.println("- - - MonomeLooper - - \nMonome should have cable on left side.\nInput an empty line to exit.");

		do {
			buffer = scanner.nextLine();
		} while (buffer.length() > 0);

		_monome.clear();
		_monome.stopListening();
		
		System.out.println("Exiting...");
	}
}