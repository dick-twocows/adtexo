package org.twocows.adtexo.logger;

import org.twocows.adtexo.node.adtexo.AdtexoNode;
import org.twocows.adtexo.util.Symbol;

public interface Logger {

	public static final String ROOT_KEY = AdtexoNode.class.getSimpleName();
	
	public static final String LEVEL_KEY = createKey("Level");
			
	public static String createKey(final String suffix) {
		return ROOT_KEY + "." + suffix;
	}
	
	public static final Symbol ERROR = new Symbol("ERROR", 100);

	public static final Symbol WARN = new Symbol("WARN", 200);
	
	public static final Symbol INFO = new Symbol("INFO", 300);
	
	public static final Symbol DEBUG = new Symbol("DEBUG", 400);
	
	public static final Symbol TRACE = new Symbol("TRACE", 500);

	String getID();
	
	Symbol getLevel();
	
	void setLevel(Symbol level);
	
	void log(Symbol level, String format, Object...values);
}
