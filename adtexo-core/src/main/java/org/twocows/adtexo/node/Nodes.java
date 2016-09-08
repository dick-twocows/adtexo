package org.twocows.adtexo.node;

import org.twocows.adtexo.context.Context;

public class Nodes {

	private final Context entries;

	public Nodes() {
		super();
		entries = new Context();
	}
	
	public <T extends Node> T require(final String key) {
		return entries.require(key);
	}
	
	public Nodes put(final String key, final Node value) {
		this.entries.put(key, value);
		return this;
	}
	
	public Nodes remove(final String key) {
		this.entries.remove(key);
		return this;
	}
}
