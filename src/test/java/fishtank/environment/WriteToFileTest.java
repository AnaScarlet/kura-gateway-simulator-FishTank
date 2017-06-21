package test.java.fishtank.environment;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

import org.junit.Assert;
import org.junit.Test;

import main.java.fishtank.environment.WriteToFile;

public class WriteToFileTest {

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
			System.out.println("File not found.");
		}
		Assert.assertEquals(data, outputData);
	}
}
