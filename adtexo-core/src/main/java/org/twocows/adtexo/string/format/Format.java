package org.twocows.adtexo.string.format;

public interface Format {

	Format add(String key, Object value);
	
	Format remove(String key);
	
	default String apply(final String format, final Object...args) {
		return String.format(format, args);
	}
}
