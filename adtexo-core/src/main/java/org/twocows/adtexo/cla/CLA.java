package org.twocows.adtexo.cla;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;
import java.util.function.Function;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import org.twocows.adtexo.node.adtexo.AdtexoNode;

public class CLA implements Function<String[], Properties>{

	public static final String KEY_PREFIX = AdtexoNode.createKey(CLA.class.getSimpleName() + ".");
	
	public static final String ARGUMENTS_KEY = createKey("Arguments");

	public static final String VALUE_PREFIX = createKey("Value.");
	
	public static final String DEFAULT_OPTION_PREFIX = "-";
	
	public static final String DEFAULT_QUOTE = "\"";
	
	public static final String DEFAULT_INLINE_VALUE_SUFFIX = "+";
	
	public static final String createKey(final String suffix) {
		return KEY_PREFIX + suffix;
	}
	
	private String optionPrefix;

	private String inlineValueSuffix;
	
	private String quote;
	
	private final Map<String, Boolean> options;
	
	public CLA() {
		optionPrefix = DEFAULT_OPTION_PREFIX;
		inlineValueSuffix = DEFAULT_INLINE_VALUE_SUFFIX;
		quote = DEFAULT_QUOTE;
		options = new HashMap<>();
	}
	
	public void addOption(final String name, final Boolean value) {
		options.put(name, value);
	}

	@Override
	public Properties apply(final String[] t) {
		Integer valueIndex = 0;
		final Properties properties = new Properties();
    	final Iterator<String> iterator = Arrays.asList(t).iterator();
    	while (iterator.hasNext()) {
    		final String text = iterator.next();
    		if (text.startsWith(optionPrefix)) {
				if (text.endsWith(this.inlineValueSuffix)) {
    				if (!iterator.hasNext()) {
    					throw new UnsupportedOperationException("Option [" + text + "] requires a value");
    				}
    				properties.setProperty(removeInlineValueSuffix(removeOptionPrefix(text)), removeQuotes(iterator.next()));
				} else {
	    			final Boolean value = options.get(text);
	    			if (value == null) {
	    				throw new UnsupportedOperationException("Unknown option [" + text + "]");
	    			} else {
		    			if (value) {
		    				if (!iterator.hasNext()) {
		    					throw new UnsupportedOperationException("Option [" + text + "] requires a value");
		    				}
		    				properties.setProperty(removeOptionPrefix(text), removeQuotes(iterator.next()));
		    			} else {
		    				properties.setProperty(removeOptionPrefix(text), "");
		    			}
	    			}
				}
    		} else {
    			properties.setProperty(VALUE_PREFIX + ++valueIndex, removeQuotes(text));
    		}
    	}
    	properties.setProperty(ARGUMENTS_KEY, Arrays.stream(t).collect(Collectors.joining(" ")));
		return properties;
	}
	
	protected String removeOptionPrefix(final String text) {
		return text.substring(this.optionPrefix.length());
	}
	
	protected String removeInlineValueSuffix(final String text) {
		return text.substring(0, text.length() - inlineValueSuffix.length());
	}
	
    protected String removeQuotes(final String text) {
		if (text.startsWith(this.quote) && text.endsWith(this.quote)) {
			return text.substring(1, text.length()-1);
		} else {
			return text;
		}
    }
}
