package org.twocows.adtexo.grammar;

import java.util.LinkedList;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import org.twocows.adtexo.context.Context;

public class Grammars {

	private final List<Grammar> entries;
	
	public Grammars() {
		this.entries = new LinkedList<>();
	}
	
	public Grammars add(final Grammar grammar) {
		entries.add(grammar);
		return this;
	}
	
	public Context apply(final String text) {
		final List<String> messages = new LinkedList<>();
		return entries
			.stream()
			.map(new Function<Grammar, Context>() {
				@Override
				public Context apply(final Grammar grammar) {
					try {
						return grammar.parse(text);
					} catch (final Throwable throwable) {
						messages.add(grammar.getClass().getSimpleName() + " " + throwable.getMessage());
						return new Context();
					}
				}
			})
			.filter(context -> (context.get(Grammar.ROOT_KEY, null) != null))
			.findFirst()
			.orElseThrow(new Supplier<UnsupportedOperationException>() {
				@Override
				public UnsupportedOperationException get() {
					return new UnsupportedOperationException("Grammar issue with [" + text + "]\n" + messages.stream().collect(Collectors.joining("\n")));
				}
			});
	}
}
