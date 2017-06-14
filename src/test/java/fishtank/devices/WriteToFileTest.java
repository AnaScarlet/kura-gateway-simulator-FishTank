package test.java.fishtank.devices;

import java.util.ArrayList;

import org.junit.Assert;
import org.junit.Test;

import main.java.fishtank.devices.*;

public class WriteToFileTest {

	private Object monitor = new Object();

	@Test
	public void main() {
		ExampleDevice device = new ExampleDevice(monitor);
		synchronized (monitor) {
			try {
				monitor.wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			if (!device.isRunning())
				device.writeToFile();
		}
	}

}
