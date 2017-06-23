package test.java.fishtank.environment;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.junit.Assert;
import org.junit.Test;

import main.java.fishtank.environment.Environment;
import main.java.fishtank.environment.WriteToFile;

public class WriteToFileTest {
	
	private static final Logger LOGGER = Logger.getLogger(Environment.class.getName());

	@Test
	public void main() {
		WriteToFile writer = new WriteToFile("testData.txt");
		String data = "Test my test";
		writer.writeToFile(data);
		writer.done();
		
		String outputData = "";
		try (Scanner scanner = new Scanner(new File(writer.getAbsoluteFilePath()))) {
			while (scanner.hasNextLine()){
				outputData = scanner.nextLine();
			} 
			scanner.close();
		} catch (FileNotFoundException e) {
			LOGGER.log(Level.SEVERE, e.toString(), e);
		}
		Assert.assertEquals(data, outputData);
	}
}
