package main.java.fishtank.main;

import java.util.concurrent.*;
import java.util.logging.Level;
import java.util.logging.Logger;

import main.java.fishtank.environment.Environment;
import test.java.fishtank.devices.MakingDevicesTest;

public class MyScheduledExecutor {

	private static final Logger LOGGER = Logger.getLogger(MyScheduledExecutor.class.getName());
	private ScheduledExecutorService scheduledExecutorService;
	private Environment env;
	
	public MyScheduledExecutor(Environment env) {
		this.env = env;
		this.scheduledExecutorService = Executors.newScheduledThreadPool(2);
	}

	public void schedule() {
		env.calculateInterval();
		final ScheduledFuture<?> scheduledFuture =
	    scheduledExecutorService.scheduleAtFixedRate(new Runnable() {
	        public void run() {
	        	env.callElements();
	            System.out.println("Executed!");
	        }
	    }, 0, env.getInterval(), TimeUnit.MILLISECONDS);
		
	}
	
	public void shutdownExecutor(){
		do {
			this.scheduledExecutorService.shutdown();
			LOGGER.info("Executor is shutting down...");
			try {
				Thread.sleep(4000);
			} catch (InterruptedException e) {
				LOGGER.log(Level.SEVERE, e.toString(), e);
			}
		} while (!this.scheduledExecutorService.isShutdown());
	}

	
}
