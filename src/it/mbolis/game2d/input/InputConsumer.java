package it.mbolis.game2d.input;

import static java.lang.Integer.bitCount;
import static java.lang.System.arraycopy;
import it.mbolis.game2d.input.Input.InputState;
import it.mbolis.hero.Board.KeyCombination;

import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.concurrent.Callable;

public class InputConsumer {

	private static class Combination implements Comparable<Combination> {

		final int value;
		final int modifiers;

		Combination(int keyCode, int modifiers) {
			this.value = keyCode;
			this.modifiers = modifiers;
		}

		@Override
		public int compareTo(Combination that) {
			int codeCompare = this.value - that.value;
			return codeCompare != 0 ? codeCompare : bitCount(this.modifiers) - bitCount(that.modifiers);
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + value;
			result = prime * result + modifiers;
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			KeyCombination that = (KeyCombination) obj;
			if (this.value != that.keyCode)
				return false;
			if (this.modifiers != that.modifiers)
				return false;
			return true;
		}

	}

	private static class CombinationMapper extends TreeMap<Combination, Integer> {

		private static final long serialVersionUID = 1L;
	}
	
	private final Input input;
	private final Map<Integer, CombinationMapper> keyMappers = new HashMap<>();
	private final Map<Integer, CombinationMapper> mouseMappers = new HashMap<>();
	
	private final Map<String, Integer> actionNames = new TreeMap<>();
	private Callable<?>[] actions = new Callable[10];
	private int nextAction = 0;
	
	public InputConsumer(Input input) {
		this.input = input;
	}
	
	public List<?> update() {
		List<Object> result = new LinkedList<>();

		InputState state = input.getState();
		Input.Keyboard keyboard = state.keyboard;
		int modifiers = keyboard.modifiers;
		
		for (Integer k : keyboard.keyPresses) {
			SortedMap<Combination, Integer> cs = keyMappers.get(k);
			for (Combination c : cs.keySet()) {
				if ((c.modifiers & modifiers) == modifiers) {
					Integer actionCode = cs.get(c);
					Callable<?> action = actions[actionCode];
					if (action != null) {
						try {
							result.add(action.call());
						} catch (Exception exc) {
						}
					}
					break;
				}
			}
		}
		
		Input.Mouse mouse = state.mouse;
		for (Integer b : mouse.buttonPresses) {
			SortedMap<Combination, Integer> cs = mouseMappers.get(b);
			for (Combination c : cs.keySet()) {
				if ((c.modifiers & modifiers) == modifiers) {
					Integer actionCode = cs.get(c);
					Callable<?> action = actions[actionCode];
					if (action != null) {
						try {
							result.add(action.call());
						} catch (Exception exc) {
						}
					}
					break;
				}
			}
		}
		
		return result;
	}
	
	public void addKeyMapper(int keyCode, int modifiers, String action) {
		CombinationMapper mapper = keyMappers.get(keyCode);
		if (mapper == null) {
			mapper = new CombinationMapper();
			keyMappers.put(keyCode, mapper);
		}
		Integer actionCode = actionNames.get(action);
		if (actionCode == null) {
			actionCode = nextAction++;
			actionNames.put(action, actionCode);
		}
		mapper.put(new Combination(keyCode, modifiers), actionCode);
	}
	
	public void addMouseButtonMapper(int button, int modifiers, String action) {
		CombinationMapper mapper = mouseMappers.get(button);
		if (mapper == null) {
			mapper = new CombinationMapper();
			mouseMappers.put(button, mapper);
		}
		Integer actionCode = actionNames.get(action);
		if (actionCode == null) {
			actionCode = nextAction++;
			actionNames.put(action, actionCode);
		}
		mapper.put(new Combination(button, modifiers), actionCode);
	}
	
	public void addAction(String name, Callable<?> action) {
		Integer actionCode = actionNames.get(name);
		if (actionCode == null) {
			actionCode = nextAction++;
			actionNames.put(name, actionCode);
		}
		if (actions.length <= actionCode) {
			Callable<?>[] newActions = new Callable[actions.length * 2];
			arraycopy(actions, 0, newActions, 0, actions.length);
			actions = newActions;
		}
		actions[actionCode] = action;
	}
}
