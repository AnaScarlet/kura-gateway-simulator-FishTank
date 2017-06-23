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
