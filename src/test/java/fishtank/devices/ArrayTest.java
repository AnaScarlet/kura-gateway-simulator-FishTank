package test.java.fishtank.devices;

import org.junit.Assert;
import org.junit.Test;

public class ArrayTest {
	
	private Object monitor = new Object();

	@Test
	public void main() {
		ExampleDevice device = new ExampleDevice(monitor);
		Integer[] array = {0, 1, 2, 3, 4};
		synchronized (monitor) {
			try {
				monitor.wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			if (!device.isRunning())
				Assert.assertArrayEquals(array, device.getData());
		}
	}
}
