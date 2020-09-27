package sharedData;

import java.util.HashMap;

public class Globe {
	private static Globe globe;
	private HashMap<String, Context> contextCollection;

	private Globe() {
		contextCollection = new HashMap<>();
	}

	public static Globe getGlobe() {
		if (globe == null)
			globe = new Globe();
		return globe;
	}

	public Context getContext(String key) {
		Context context = contextCollection.get(key);
		return context;
	}

	public void putContext(String key, Context context) {
		contextCollection.put(key, context);
	}

	public void removeContext(String key) {
		contextCollection.remove(key);
	}

	public void emptyGlobe() {
		contextCollection.clear();
	}

	public static <T> T getStateItem(Class<T> returnClass, String contextKey, String stateName, String itemKey) {
		try {
			return returnClass.cast(Globe.getGlobe().getContext(contextKey).getState(stateName).getItem(itemKey));
		} catch (ClassCastException e) {
			return null;
		} catch (NullPointerException e) {
			return null;
		}
	}

	public static State getState(String contextKey, String stateName) {
		return Globe.getGlobe().getContext(contextKey).getState(stateName);
	}
}