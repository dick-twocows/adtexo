package org.twocows.adtexo.node.jetty;

import org.twocows.adtexo.context.Context;
import org.twocows.adtexo.grammar.Grammar;
import org.twocows.adtexo.grammar.GrammarHandles;
import org.twocows.adtexo.node.Node;
import org.twocows.adtexo.node.jetty.grammar.JettyNodeGrammar;

public class JettyNode extends Node {
	
	public static final String KEY_PREFIX = JettyNode.class.getSimpleName();
	
	public static final String ADD_CONNECTION_KEY = createKey("AddConnection");
	
	public static final String ADD_SERVLET_KEY = createKey("AddServlet");
	
	public static String createKey(final String suffix) {
		return KEY_PREFIX + "." + suffix;
	}
	
	private final Grammar grammar;
	
	private final GrammarHandles grammarHandles;

	public JettyNode(final Context context) {
		super(context);
		this.grammar = new JettyNodeGrammar();
		this.grammarHandles = new GrammarHandles();
	}

}
