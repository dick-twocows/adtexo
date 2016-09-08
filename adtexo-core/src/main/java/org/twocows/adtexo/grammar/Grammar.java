package org.twocows.adtexo.grammar;

import java.io.Reader;
import java.io.StringReader;

import org.twocows.adtexo.context.Context;

public interface Grammar {
	
	static final String KEY_PREFIX = Grammar.class.getSimpleName();

	static final String ROOT_KEY = createKey("Root"); 

	static final String TEXT_KEY = createKey("Text"); 
			
	static String createKey(final String suffix) {
		return KEY_PREFIX + "." + suffix;
	}
	
	default Context parse(final String text) {
        ReInit(new StringReader(text));
        try {
        	return root();
        } catch (final Throwable throwable) {
        	throw new UnsupportedOperationException(throwable);
        }
	}
	
	/**
	 * We need a placeholder for the Context parse(String).
	 * @param reader
	 */
	void ReInit(Reader reader);

	/**
	 * Implementing classes will override Exception via JavaCC with ParseException.
	 * @return
	 * @throws Exception
	 */
	Context root() throws Exception;
}
