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

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.junit.Test;

import resources.MyLogger;

public class ALoggerTest {

	 private final static Logger logger = Logger.getLogger(ALoggerTest.class.getName());
	
	@Test
	public void main(){
		try {
			MyLogger.setup();
		} catch (IOException e) {
			e.printStackTrace();
		}
		this.testLogs();
	}
	
	@Test
	public void testLogs() {
		logger.info("Hi from logger test");
		logger.log(Level.FINEST, "Hi from logger test");
	}
}
