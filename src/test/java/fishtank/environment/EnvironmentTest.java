/*******************************************************************************
 * Copyright (c) 2017 Anastasiya Lazarenko
 *******************************************************************************/
package test.java.fishtank.environment;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.junit.Test;

import main.java.fishtank.environment.Environment;
import main.java.fishtank.main.MyScheduledExecutor;

public class EnvironmentTest {
	
	private static final Logger LOGGER = Logger.getLogger(Environment.class.getName());
	
	@Test
	public void main() {
		Environment env = new Environment(0, 14, 15, 10, 3, 5, 7, 2, 10, 0, 0, 20);
		MyScheduledExecutor executor = new MyScheduledExecutor(env);
		executor.schedule();
		try {
			Thread.sleep(60000);
			} catch (InterruptedException e) {
			LOGGER.log(Level.SEVERE, e.toString(), e);
		}
		executor.shutdownExecutor();
		
		LOGGER.log(Level.FINE, "Everything is done and cleaned.");
	}
}
