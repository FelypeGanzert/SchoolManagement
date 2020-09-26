package sharedData;

import java.util.HashMap;

public class Context {
	private HashMap<String, State> states;

	public Context() {
		states = new HashMap<>();
	}

	public void putState(String stateName, State state) {
		states.put(stateName, state);
	}

	public State getState(String stateName) {
		State state = states.get(stateName);
		return state;
	}

	public void removeState(String stateName) {
		states.remove(stateName);
	}
}