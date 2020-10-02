package sharedData;

import java.util.HashMap;

public class Globe {
	private static Globe globe;
	private HashMap<String, Object> items;
	
	private Globe() {
		items = new HashMap<>();
	}

	public static Globe getGlobe() {
		if (globe == null) {
			globe = new Globe();
		}
		return globe;
	}

	public Object getItem(String key) {
		Object item = items.get(key);
		return item;
	}
	
	public <T> T getItem(Class<T> returnClass, String key) {
		Object item = items.get(key);
		return returnClass.cast(item);
	}

	public void putItem(String key, Object item) {
		items.put(key, item);
	}

	public void removeItem(String key) {
		items.remove(key);
	}

	public void emptyState() {
		items.clear();
	};

	

}