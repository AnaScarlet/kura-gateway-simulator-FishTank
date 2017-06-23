package resources;

import java.io.IOException;
import java.util.logging.*;

public class MyLogger {

	private static FileHandler handler;
	static private SimpleFormatter formatter;
	
	static public void setup() throws IOException {
		Logger logger = Logger.getLogger("");
		 
		logger.setLevel(Level.ALL);
		handler = new FileHandler("src/resources/Log.log");
		formatter = new SimpleFormatter();
		handler.setFormatter(formatter);
		logger.addHandler(handler);
	
	}

}
