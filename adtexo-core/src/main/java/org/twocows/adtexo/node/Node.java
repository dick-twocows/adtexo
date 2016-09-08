package org.twocows.adtexo.node;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.server.UnicastRemoteObject;
import java.time.Instant;
import java.util.UUID;
import java.util.function.Function;

import org.twocows.adtexo.context.Context;
import org.twocows.adtexo.grammar.Grammar;
import org.twocows.adtexo.grammar.GrammarHandles;
import org.twocows.adtexo.grammar.Grammars;
import org.twocows.adtexo.lifecycle.ServiceLifecycle;
import org.twocows.adtexo.logger.Logger;
import org.twocows.adtexo.logger.SystemLogger;
import org.twocows.adtexo.node.grammar.NodeGrammar;
import org.twocows.adtexo.util.Symbol;

public class Node implements ServiceLifecycle {

	private static final Logger LOGGER = new SystemLogger(Node.class.getSimpleName());
	
	public static final String KEY_PREFIX = Node.class.getSimpleName();
	
	public static final String PROPERTIES_URL_KEY = createKey("PropertiesURL");
	
	public static final String CLASS_KEY = createKey("Class");
	
	public static final String ID_KEY = createKey("ID");
	
	public static final String FEATURE_KEY = createKey("Feature");
	
	public static final String SHOW_KEY = createKey("Show");
	
	public static final String START_KEY = createKey("Start");
	
	public static final String STOP_KEY = createKey("Stop");
	
	public static final String RMI_ENABLED = "adtexo/node/rmi/enabled";

	public static final String RMI_REGISTRY_HOST = "adtexo/node/rmi/registry/host";

	public static final String RMI_REGISTRY_HOST_DEFAULT = null;

	public static final String RMI_REGISTRY_PORT = "adtexo/node/rmi/registry/port";

	public static final Integer RMI_REGISTRY_PORT_DEFAULT = 0;

	public static final String RMI_EXPORT_PORT = "adtexo/node/rmi/export/port";

	public static final Integer RMI_EXPORT_PORT_DEFAULT = 0;

	private static final ThreadGroup shutdownHookThreadGroup = new ThreadGroup(createKey("shutdownHook"));
	
	public static String createKey(final String suffix) {
		return KEY_PREFIX + "." + suffix;
	}
	
	public static String createID() {
		return UUID.randomUUID().toString();
	}	

	/*
	 * Instance.
	 */
	
	private final Instant timestamp;
	
	private final Context context;
	
	private final String id;

	private final Integer hashcode;
	
	private NodeRemote rmiApply = null;
	
	private Symbol state;
	
	private final Grammars grammars;
	
	private final GrammarHandles grammarHandles;
	
	protected FeatureHandles featureHandles;
	
	private final Thread shutdownHookThread;
	
	public Node(final Context context) {
		this.state = CREATED;
		
		this.timestamp = Instant.now();
		
		this.context = context;
		
		this.id = context.require(ID_KEY);
		this.hashcode = this.id.hashCode();

		this.grammars = new Grammars();
		this.grammars.add(new NodeGrammar());
		
		this.grammarHandles = new GrammarHandles();
		this.grammarHandles.put(Node.SHOW_KEY, new Function<Context, Object>() {
			@Override
			public Object apply(final Context context) {
				return Node.this.toString();
			}
		});
		
		this.featureHandles = new FeatureHandles();
		this.featureHandles.put(Grammar.TEXT_KEY, new Function<Context, Object>() {
			@Override
			public Object apply(final Context context) {
				final Context parseContext = getGrammars().apply(context.require(Grammar.TEXT_KEY));
				return getGrammarHandles().apply(parseContext.require(Grammar.ROOT_KEY), parseContext);
			}
		});
		
		this.shutdownHookThread = new Thread(
			shutdownHookThreadGroup,
			new Runnable() {
				@Override
				public void run() {
					close();
				}
			},
			getClass().getSimpleName() + " " + this.id + " shutdown"
		);
		this.shutdownHookThread.setDaemon(true);
		Runtime.getRuntime().addShutdownHook(shutdownHookThread);
	}

	protected Grammars getGrammars() {
		return this.grammars;
	}
	
	protected GrammarHandles getGrammarHandles() {
		return this.grammarHandles;
	}
	
	protected FeatureHandles getFeatureHandles() {
		return this.featureHandles;
	}
	
	@Override
	public int hashCode() {
		return this.hashcode;
	}

	@Override
	public boolean equals(Object other) {
		return (other != null && other instanceof Node && ((Node) other).getID().equals(this.id));
	}

	@Override
	public String toString() {
		return this.id + " " + this.timestamp + " " + this.state.getName();
	}
	
	public String getID() {
		return this.id;
	}

	public <R> R apply(final Context context) {
		return this.featureHandles.apply(context.require(Node.FEATURE_KEY), context);
	}

	@Override
	public synchronized void start() {
		if (this.state.equals(CREATED) || this.state.equals(STOPPED)) {
			if (context.get(RMI_ENABLED, false)) {
				try {
					this.rmiApply = new NodeRemote() {
						@Override
						public <R> R apply(final Context context) throws RemoteException {
							return Node.this.apply(context);
						}
					};
					LocateRegistry.getRegistry(context.get(RMI_REGISTRY_HOST, RMI_REGISTRY_HOST_DEFAULT), context.get(RMI_REGISTRY_PORT, RMI_REGISTRY_PORT_DEFAULT)).bind(this.id, UnicastRemoteObject.exportObject(this.rmiApply, context.get(RMI_EXPORT_PORT, RMI_EXPORT_PORT_DEFAULT)));
				} catch (final Throwable throwable) {
					throw new UnsupportedOperationException(throwable);
				}
			}
			this.state = STARTED;
		}
	}

	@Override
	public synchronized void stop() {
		if (this.state.equals(STARTED)) {
			if (context.get(RMI_ENABLED, false)) {
				try {
					LocateRegistry.getRegistry().unbind(this.id);
					UnicastRemoteObject.unexportObject(this.rmiApply, false);
				} catch (final Throwable throwable) {
					throw new UnsupportedOperationException(throwable);
				}
			}
			this.state = STOPPED;
		}
	}

	@Override
	public synchronized void close() {
		if (!this.state.equals(CLOSED)) {
			stop();
			this.state = CLOSED;
		}
	}
}
