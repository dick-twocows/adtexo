package org.twocows.adtexo.grammar;

import java.util.function.Function;
import java.util.stream.Collectors;

import org.twocows.adtexo.context.Context;

public class GrammarHandles {

	private final Context handles;

	public GrammarHandles() {
		super();
		this.handles = new Context();
	}
	
	public Function<Context, Object> get(final String key) {
		return handles.require(key);
	}
	
	public GrammarHandles put(final String key, final Function<Context, Object> handle) {
		handles.put(key, handle);
		return this;
	}

	public Object apply(final String key, final Context context) {
		return get(key).apply(context);
	}
	
	@Override
	public String toString() {
		return handles.keys().stream().sorted().collect(Collectors.joining(","));
	}
}
