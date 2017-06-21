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

import org.junit.Assert;
import org.junit.Test;

public class WriteToJSONFileTest {

	private Object monitor = new Object();
	private ExampleDevice device = new ExampleDevice(monitor);

	@Test
	public void main() {
		synchronized (monitor) {
			try {
				monitor.wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			Assert.assertTrue(device.writeToFile());
		}
	}

	@Test
	public void testErrorLogPath() {
		Assert.assertEquals("C:\\Users\\Owner\\git\\kura-gateway-simulator-FishTank\\src\\resources\\ExampleErrorLog.txt",
				device.getErrorLogFile());
	}
	
}
