package org.twocows.adtexo.cli;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.util.Collections;
import java.util.Comparator;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.twocows.adtexo.cli.grammar.CLIGrammar;
import org.twocows.adtexo.context.Context;
import org.twocows.adtexo.grammar.Grammar;
import org.twocows.adtexo.grammar.GrammarHandles;
import org.twocows.adtexo.logger.Logger;
import org.twocows.adtexo.logger.SystemLogger;
import org.twocows.adtexo.node.Node;
import org.twocows.adtexo.node.adtexo.AdtexoNode;
import org.twocows.adtexo.properties.PropertiesUtils;

public class CLI {
	
	private static final Logger LOGGER = new SystemLogger(CLI.class.getSimpleName());
	
	public static final String KEY_PREFIX = CLI.class.getSimpleName();
	
	public static final String INPUT_STREAM_KEY = createKey("InputStream");
	
	public static final String OUTPUT_STREAM_KEY = createKey("OutputStream");

	public static final String CREATE_ADTEXO_KEY = createKey("CreateAdtexo");
	
	public static final String SHOW_KEY = createKey("Show");
	
	public static final String SHUTDOWN_KEY = createKey("Shutdown");
	
	public static final String START_ADTEXO_KEY = createKey("StartAdtexo");
	
	public static String createKey(final String suffix) {
		return KEY_PREFIX + "." + suffix;
	}

	public static void main(final String[] args) {
		LOGGER.log(Logger.INFO, "JVM %s %s %s", System.getProperty("java.version"), System.getProperty("java.vendor"), System.getProperty("java.home"));
		
		final Context cliContext = Context.valueOf(PropertiesUtils.valueOf(CLI.class), Collections.emptyMap());
		
		final CLI cli = new CLI(new Context().put(CLI.INPUT_STREAM_KEY, System.in).put(CLI.OUTPUT_STREAM_KEY, System.out));

		final Context text = Context.keyStartsWith(cliContext, Grammar.TEXT_KEY + ".");
		text.entries().stream().sorted(new Comparator<Entry<String, Object>>() {
			@Override
			public int compare(Entry<String, Object> o1, Entry<String, Object> o2) {
				return o1.getKey().compareTo(o2.getKey());
			}
		}).forEach(entry -> LOGGER.log(Logger.INFO, Objects.toString(cli.apply((String) entry.getValue()))));

		cli.loop();
		LOGGER.log(Logger.INFO, "Shutdown");
	}

	private final Context context;
	
	private final BufferedReader reader;
	
	private final PrintStream writer;
	
	private final Grammar grammar;
	
	private final GrammarHandles grammarHandles;

	private Boolean loop;
	
	private final Context instances;
	
	public CLI(final Context context) {
		this.context = context;
		this.reader = new BufferedReader(new InputStreamReader(context.require(INPUT_STREAM_KEY)));
		this.writer = context.require(OUTPUT_STREAM_KEY);
		this.grammar = new CLIGrammar();
		this.grammarHandles = new GrammarHandles();
		
		this.grammarHandles.put(AdtexoNode.KEY_PREFIX, new Function<Context, Object>() {
			@Override
			public Object apply(final Context context) {
				try {
					final AdtexoNode adtexo = instances.require(context.require(Node.ID_KEY));
					final Context cliContext = new Context();
					cliContext.put(Node.FEATURE_KEY, Grammar.TEXT_KEY);
					cliContext.put(Grammar.TEXT_KEY, context.require(Grammar.TEXT_KEY));
					return adtexo.apply(cliContext);
				} catch (final Throwable throwable) {
					throw new UnsupportedOperationException(throwable);
				}
			}
		});
		
		this.grammarHandles.put(CREATE_ADTEXO_KEY, new Function<Context, Object>() {
			@Override
			public Object apply(final Context context) {
				try {
					final AdtexoNode adtexo = new AdtexoNode(context);
					instances.put(adtexo.getID(), adtexo);
					return adtexo;
				} catch (final Throwable throwable) {
					throw new UnsupportedOperationException(throwable);
				}
			}
		});
		
		this.grammarHandles.put(START_ADTEXO_KEY, new Function<Context, Object>() {
			@Override
			public Object apply(final Context context) {
				try {
					final AdtexoNode adtexo = instances.require(context.require(Node.ID_KEY));
					adtexo.start();
					return adtexo;
				} catch (final Throwable throwable) {
					throw new UnsupportedOperationException(throwable);
				}
			}
		});

		this.grammarHandles.put(SHOW_KEY, new Function<Context, Object>() {
			@Override
			public Object apply(final Context t) {
				return instances.keys().stream().sorted().collect(Collectors.joining(","));
			}
		});

		this.grammarHandles.put(SHUTDOWN_KEY, new Function<Context, Object>() {
			@Override
			public Object apply(final Context t) {
				CLI.this.loop = false;
				return "Shutting down";
			}
		});
		
		this.instances = new Context();
	}

	public synchronized Object apply(final String text) {
		final Context context = grammar.parse(text);
		return grammarHandles.apply(context.require(Grammar.ROOT_KEY), context);
	}
	
	public synchronized void loop() {
		this.loop = true;
		while (loop) {
			try {
				this.writer.print("# ");
				this.writer.println("= " + Objects.toString(apply(reader.readLine())));
			} catch (final Throwable throwable) {
				LOGGER.log(Logger.WARN, throwable.getMessage());
			}
		}
	}
}
