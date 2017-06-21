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
package test.java.fishtank.devices;

import java.io.File;
import java.util.ArrayList;

import main.java.fishtank.devices.FishTankDevice;
import main.java.fishtank.devices.WriteToJSONFile;

public class ExampleDevice implements FishTankDevice{
	private boolean isRunning;
	private final String id = "ABC123";
	private final String name = "The Device";
	private final String manufacturer = "This is Us";
	private final String model = "X";
	private final Object monitor;
	public static String errorLogFile = "";
	private ArrayList<Integer> data = new ArrayList<Integer>();
	
	public ExampleDevice(Object monitor) {
		File errorLog = new File("src/resources/ExampleErrorLog.txt");
		ExampleDevice.errorLogFile = errorLog.getAbsolutePath();
		this.createThread(this, "Example Device Thread");
		this.monitor = monitor;
	}
 	
	public void run(){
		this.isRunning = true;
		for (int i = 0; i < 5; i++) {
			data.add(i);
			System.out.println(i);
		}
		this.isRunning = false;
		synchronized(monitor) {
			monitor.notifyAll();
		}
	}
	
	public boolean writeToFile() {
		if (!this.isRunning) {
			(new WriteToJSONFile()).writeToFile(this);
			return true;
		} return false;
	}
	
	private void createThread(Runnable obj, String threadName) {
		Thread t = new Thread(obj, threadName);
		t.start();
	}
	
	public boolean isRunning() {
		return this.isRunning;
	}
	public String getID() {
		return this.id;
	}
	public String getName() {
		return this.name;
	}
	public String getManufacturer() {
		return this.manufacturer;
	}
	public String getModel() {
		return this.model;
	}
	public String getErrorLogFile() {
		return errorLogFile;
	}
	public Integer[] getData() {
		Integer[] a = {1};
		return (Integer[]) this.data.toArray(a);
	}
	@Override
	public String toString() {
		return "ID: " + this.getID() + " Name: " + this.getName() + " Manufacturer: " 
				+ this.getManufacturer() + " Model: " + this.getModel();
	}
}
