package org.twocows.adtexo;

import static org.junit.Assert.*;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.twocows.adtexo.util.Symbol;

public class SymbolTests {
	
	@Rule
	public final ExpectedException exception = ExpectedException.none();
	
	@Test
	public void symbol() {
		final Symbol s = new Symbol("s", 0);
		assertTrue("s".equals(s.getName()));
		assertTrue(((Integer) 0).equals(s.getValue()));
	}
	
	@Test
	public void duplicateSymbol() {
		final Symbol x = new Symbol("x", 0);
		exception.expect(UnsupportedOperationException.class);
		final Symbol y = new Symbol("x", 0);
	}
}
