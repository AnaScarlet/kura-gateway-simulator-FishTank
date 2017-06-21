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
