package org.twocows.adtexo.properties;

import java.io.InputStream;
import java.net.URL;
import java.util.Properties;

public class PropertiesUtils {

	public static Properties valueOf(final Class<?> c) {
		try {
			return valueOf(new URL("file://" + System.getProperty("user.dir") + "/" + c.getSimpleName() + ".properties"));
		} catch (final Throwable throwable) {
			System.err.println(throwable.getMessage());
			return new Properties();
		}
	}

	public static Properties valueOf(final URL url) {
		try {
			final Properties properties = new Properties();
			try (final InputStream inputStream = url.openStream()) {
				properties.load(inputStream);
			}
			return properties;
		} catch (final Throwable throwable) {
			throw new UnsupportedOperationException(throwable);
		}
	}
}
