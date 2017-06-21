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
package test.java.fishtank.environment;

import org.junit.Test;

import main.java.fishtank.environment.Environment;

public class EnvironmentTest {
	
	@Test
	public void main() {
		Environment env = new Environment(0, 14, 15, 10, 3, 5, 7, 2, 10, 0, 0, 20);
		try {
			env.start();
			Thread.sleep(60000);
			env.stopThreads();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		env = null;
		System.gc();
		
		System.out.println("Everything is done and cleaned.");
	}
}