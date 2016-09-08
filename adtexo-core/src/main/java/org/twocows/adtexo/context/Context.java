package org.twocows.adtexo.context;

import java.io.Serializable;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Function;

public class Context implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * Return a new Context from the given Properties using the given Map.  
	 * @param properties
	 * @param transform
	 * @return
	 */
	public static Context valueOf(final Properties properties, final Map<String, Function<String, Object>> transform) {
		try {
			final Context context = new Context();
			final Enumeration<?> enumeration = properties.propertyNames();
			while (enumeration.hasMoreElements()) {
				final String key = (String) enumeration.nextElement();
				final String value = properties.getProperty(key);
				if (value != null) {
					final Function<String, Object> function = transform.get(key);
					context.put(key, (function == null ? value : function.apply(value)));
				}
			}
			return context;
		} catch (final Throwable throwable) {
			throw new UnsupportedOperationException(throwable);
		}
	}
	
	/**
	 * Return a new Context which is the result of merging the given Properties into the given Context using the given Map.
	 * @param context
	 * @param properties
	 * @param transform
	 * @return
	 */
	public static Context merge(final Context context, final Properties properties, final Map<String, Function<String, Object>> transform) {
		try {
			final Context result = new Context();
			context.entries.forEach((k, v) -> result.put(k, v));
			final Enumeration<?> enumeration = properties.propertyNames();
			while (enumeration.hasMoreElements()) {
				final String key = (String) enumeration.nextElement();
				final String value = properties.getProperty(key);
				if (value != null) {
					final Function<String, Object> function = transform.get(key);
					context.put(key, (function == null ? value : function.apply(value)));
				}
			}
			return context;
		} catch (final Throwable throwable) {
			throw new UnsupportedOperationException(throwable);
		}
	}

	public static Context copy(final Context context) {
		final Context copy = new Context();
		context.entries.forEach((key, value) -> copy.put(key, value));
		return copy;
	}
	
	/**
	 * Return a new Context which is the result of adding all the entries from the given Context where the key starts with the given prefix. 
	 * @param context
	 * @param prefix
	 * @return
	 */
	public static Context keyStartsWith(final Context context, final String prefix) {
		try {
			final Context subset = new Context();
			context.entries.entrySet().stream().filter(entry -> entry.getKey().startsWith(prefix)).forEach(entry -> subset.put(entry.getKey(), entry.getValue()));
			return subset;
		} catch (final Throwable throwable) {
			throw new UnsupportedOperationException(throwable);
		}
	}
	
	private final ConcurrentMap<String, Object> entries;

	public Context() {
		this(new ConcurrentHashMap<>(512));
	}
	
	public Context(final ConcurrentMap<String, Object> entries) {
		this.entries = entries;
	}

	@SuppressWarnings("unchecked")
	public <T> T get(final String key, final T value) {
		return (T) entries.getOrDefault(key, value);
	}

	@SuppressWarnings("unchecked")
	public <T> T require(final String key) {
		return Objects.requireNonNull((T) entries.get(key), "Undefined key [" + key + "]");
	}
	
	public Context put(final String key, final Object value) {
		entries.put(key, value);
		return this;
	}
	
	public Context put(final Context context) {
		context.entries.forEach((key, value) -> put(key, value));
		return this;
	}
	
	public Context remove(final String key) {
		this.entries.remove(key);
		return this;
	}
	
	public Set<String> keys() {
		return Collections.unmodifiableSet(entries.keySet());
	}
	
	public Set<Entry<String, Object>> entries() {
		return Collections.unmodifiableSet(entries.entrySet());
	}
}
