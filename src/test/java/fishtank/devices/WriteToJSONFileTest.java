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

import java.util.logging.Level;
import java.util.logging.Logger;

import org.junit.Assert;
import org.junit.Test;

import main.java.fishtank.environment.Environment;

public class WriteToJSONFileTest {

	private static final Logger LOGGER = Logger.getLogger(Environment.class.getName());
	private Object monitor = new Object();
	
	@Test
	public void main() {
		synchronized (monitor) {
			ExampleDevice device = new ExampleDevice(monitor);
			try {
				monitor.wait();
			} catch (InterruptedException e) {
				LOGGER.log(Level.SEVERE, e.toString(), e);
			}
			Assert.assertTrue(device.writeToFile());
		}
	}

}
