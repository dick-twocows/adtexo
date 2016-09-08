package org.twocows.adtexo.util;

import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class Symbol {

	private static final Set<Symbol> defined = ConcurrentHashMap.newKeySet(1024);
	
	public static Set<Symbol> getDefined() {
		return Collections.unmodifiableSet(Symbol.defined);
	}
	
	private final String name;
	
	private final Integer value;

	private final Integer hashcode;
	
	public Symbol(final String name, final Integer index) {
		super();
		this.name = name;
		this.value = index;
		this.hashcode = name.hashCode();
		if (!Symbol.defined.add(this)) {
			throw new UnsupportedOperationException("Symbol [" + toString() + "] already defined");
		}
	}

	public String getName() {
		return this.name;
	}

	public Integer getValue() {
		return this.value;
	}

	@Override
	public int hashCode() {
		return this.hashcode;
	}

	@Override
	public boolean equals(final Object other) {
		return (other != null && other instanceof Symbol && ((Symbol) other).name.equals(this.name));
	}

	@Override
	protected void finalize() throws Throwable {
		Symbol.defined.remove(this);
		super.finalize();
	}

	@Override
	public String toString() {
		return name + ":" + value;
	}
}
