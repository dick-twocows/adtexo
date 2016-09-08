package org.twocows.adtexo.logger;

import java.util.Objects;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import org.twocows.adtexo.util.Symbol;

public abstract class AbstractLogger implements Logger {

	private final String id;
	
	private final Integer hashcode;
	
	private volatile Symbol level;
	
	private final Thread shutdown;
	
	private final BlockingQueue<String> queue;
	
	private final Thread worker;
	
	public AbstractLogger(final String id) {
		this.id = id;
		this.hashcode = id.hashCode();

		this.level = Logger.INFO;
		
		this.shutdown = new Thread(new Runnable() {
			@Override
			public void run() {
				AbstractLogger.this.log(INFO, "Shutting down...");
				AbstractLogger.this.worker.interrupt();
				try {
					AbstractLogger.this.worker.join();
				} catch (InterruptedException e) {
				}
			}
		});
		this.shutdown.setName("Logger " + this.id + " shutdown");
		this.shutdown.setDaemon(true);
		
		this.queue = new LinkedBlockingQueue<>();
		
		this.worker = new Thread(new Runnable() {
			@Override
			public void run() {
				while (true) {
					try {
						AbstractLogger.this.log(AbstractLogger.this.queue.take());
					} catch (final InterruptedException interruptedException) {
						break;
					} catch (final Throwable throwable) {
						System.err.println(throwable.getMessage());
					}
				}
				AbstractLogger.this.log(INFO, "Shutdown");
			}
		});
		this.worker.setName("Logger " + this.id + " worker");
		this.worker.setDaemon(true);
		this.worker.start();
	}
	
	@Override
	public String getID() {
		return this.id;
	}
	
	@Override
	public Symbol getLevel() {
		return this.level;
	}

	@Override
	public void setLevel(final Symbol level) {
		this.level = level;		
	}

	@Override
	public void log(final Symbol level, final String format, final Object... values) {
		if (level.getValue() <= getLevel().getValue()) {
			if (values.length == 0) {
				this.queue.add(level.getName() + " " + this.id + " " + Objects.toString(format));
			} else {
				this.queue.add(level.getName() + " " + this.id + " " + String.format(format, values));
			}
		}
	}

	protected abstract void log(String text);

	@Override
	public int hashCode() {
		return this.hashcode;
	}

	@Override
	public boolean equals(Object obj) {
		return (obj != null && obj instanceof Logger && ((Logger) obj).getID().equals(this.id));
	}

	@Override
	public String toString() {
		return this.id + " " + this.level;
	}
}
