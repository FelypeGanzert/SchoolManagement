package sharedData;

import java.util.HashMap;

public class State {
	private String stateName;
	private HashMap<String, Object> items;

	public State() {
		items = new HashMap<>();
	}

	public Object getItem(String key) {
		Object item = items.get(key);
		return item;
	}

	public void putItem(String key, Object item) {
		items.put(key, item);
	}

	public void removeItem(String key) {
		items.remove(key);
	}

	public void emptyState() {
		items.clear();
	}
}