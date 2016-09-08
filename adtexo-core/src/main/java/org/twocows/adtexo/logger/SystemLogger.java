package org.twocows.adtexo.logger;

public class SystemLogger extends AbstractLogger {

	public SystemLogger(final String id) {
		super(id);
		System.out.println(toString());
	}

	@Override
	protected void log(String text) {
		System.out.println(text);
	}

	
}
