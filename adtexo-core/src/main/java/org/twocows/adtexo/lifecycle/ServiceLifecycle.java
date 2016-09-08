package org.twocows.adtexo.lifecycle;

import org.twocows.adtexo.util.Symbol;

public interface ServiceLifecycle extends Lifecycle {

	public static final Symbol CREATED = new Symbol("CREATED", null);

	public static final Symbol STARTED = new Symbol("STARTED", null);

	public static final Symbol STOPPED = new Symbol("STOPPED", null);

	public static final Symbol CLOSED = new Symbol("CLOSED", null);
	
	void start();
	
	void stop();
	
	void close();
}
