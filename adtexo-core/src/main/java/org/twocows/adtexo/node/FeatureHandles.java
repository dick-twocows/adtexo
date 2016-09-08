package org.twocows.adtexo.node;

import java.util.function.Function;

import org.twocows.adtexo.context.Context;

public class FeatureHandles {

	private final Context entries;
	
	public FeatureHandles() {
		this.entries = new Context();
	}
	
	public FeatureHandles put(final String key, Function<Context, Object> handle) {
		entries.put(key, handle);
		return this;
	}

	/**
	 * Return the feature handle for the given key.
	 * @param key
	 * @return
	 */
	public Function<Context, Object> get(final String key) {
		return entries.require(key);
	}
	
	@SuppressWarnings("unchecked")
	public <R> R apply(final String key, final Context context) {
		return (R) get(key).apply(context);
	}
}
