package org.aewofij.monomeLooper;

import org.aewofij.monome.Monome;

import java.util.*;

public class Looper {
	private final Monome _monome;
	private final LooperView _mainView;
	private final MLEngine _engine;
	private ClipGrid _clipGrid;

	public Looper(Monome monome, MLEngine engine) {
		_monome = monome;
		_engine = engine;
		
		// Initialize clip grid.
		_clipGrid = new ClipGrid(_engine);

		_mainView = new LooperView(_monome, _clipGrid);
	}
}