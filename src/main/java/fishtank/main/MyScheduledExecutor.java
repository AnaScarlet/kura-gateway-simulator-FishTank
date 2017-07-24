/*******************************************************************************
 * Copyright (c) 2017 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package main.java.fishtank.main;

import java.util.concurrent.*;
import java.util.logging.Level;
import java.util.logging.Logger;

import main.java.fishtank.devices.DevicesCentral;
import main.java.fishtank.environment.Environment;

public class MyScheduledExecutor {

	private static final Logger LOGGER = Logger.getLogger(MyScheduledExecutor.class.getName());
	private ScheduledExecutorService scheduledExecutorService;
	private Environment env;
	private DevicesCentral devicesCentral;
	
	public MyScheduledExecutor(Environment env, DevicesCentral devicesCentral) {
		this.env = env;
		this.devicesCentral = devicesCentral;
		this.scheduledExecutorService = Executors.newScheduledThreadPool(2);
	}
	
	public MyScheduledExecutor(Environment env) {
		this.env = env;
		this.scheduledExecutorService = Executors.newScheduledThreadPool(2);
	}

	public void schedule() {
		env.calculateInterval();
		scheduledExecutorService.scheduleAtFixedRate(new Runnable() {
	        public void run() {
	        	env.callElements();
	        	LOGGER.info("Environment Executed!");
	        }
	    }, 0, env.getInterval(), TimeUnit.MILLISECONDS);
		
		if (this.devicesCentral != null) {
			scheduledExecutorService.scheduleAtFixedRate(new Runnable() {
				        public void run() {
				        	devicesCentral.runDevices();
				        	LOGGER.info(devicesCentral.getDevicesDataAsString());
				        	LOGGER.info("Devices Executed!");
				        }
				    }, 0, devicesCentral.getTimeInterval(), TimeUnit.MILLISECONDS);
		}
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
			env.closeWriter();
		} while (!this.scheduledExecutorService.isShutdown());
	}

	
}
