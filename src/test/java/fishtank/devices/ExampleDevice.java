package test.java.fishtank.devices;

import java.util.ArrayList;

import main.java.fishtank.devices.FishTankDevice;
import main.java.fishtank.devices.WriteToFile;

public class ExampleDevice implements FishTankDevice{
	private boolean isRunning;
	private final String id = "ABC123";
	private final String name = "The Device";
	private final String manufacturer = "This is Us";
	private final String model = "X";
	private final Object monitor;
	public static final String errorLogFile = "C:/Users/Owner/git/kura-gateway-simulator-FishTank/src/resources/ExampleErrorLog.txt";
	private ArrayList<Integer> data = new ArrayList<Integer>();
	
	public ExampleDevice(Object monitor) {
		this.createThread(this, "Example Device Thread");
		this.monitor = monitor;
	}
 	
	public void run(){
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
			(new WriteToFile()).writeToFile(this);
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
