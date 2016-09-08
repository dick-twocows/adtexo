package org.twocows.adtexo.node.adtexo;

import java.util.function.Function;

import org.twocows.adtexo.context.Context;
import org.twocows.adtexo.logger.Logger;
import org.twocows.adtexo.logger.SystemLogger;
import org.twocows.adtexo.node.Node;
import org.twocows.adtexo.node.Nodes;
import org.twocows.adtexo.node.adtexo.grammar.AdtexoNodeGrammar;

public class AdtexoNode extends Node {

	private static final Logger LOGGER = new SystemLogger(AdtexoNode.class.getSimpleName());

	public static final String KEY_PREFIX = AdtexoNode.class.getSimpleName();
	
	public static final String CREATE_NODE_KEY = createKey("CreateNode");
	
	public static String createKey(final String suffix) {
		return KEY_PREFIX + "." + suffix;
	}

	private final Nodes nodes;
	
	public AdtexoNode(Context context) {
		super(context);

		getGrammars().add(new AdtexoNodeGrammar());
		
		getGrammarHandles().put(CREATE_NODE_KEY, new Function<Context, Object>() {
			@Override
			public Object apply(final Context context) {
				try {
					final Node node = (Node) Class.forName(context.require(Node.CLASS_KEY)).getConstructor(Context.class).newInstance(context);
					AdtexoNode.this.nodes.put(node.getID(), node);
					return node;
				} catch (final Throwable throwable) {
					return throwable;
				}
			}
		});

		this.nodes = new Nodes();
	}

	
}
