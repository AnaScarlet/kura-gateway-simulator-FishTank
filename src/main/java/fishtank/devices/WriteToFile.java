package main.java.fishtank.devices;

import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;

import com.google.gson.Gson;

import main.java.fishtank.environment.Environment;

public class WriteToFile {
	
	public static final String dataFilePath = "C:/Users/Owner/git/kura-gateway-simulator-FishTank/src/resources/jsonData.json";

	public void writeToFile (FishTankDevice obj) {
		Gson gson = new Gson();
		String gsonRepr = gson.toJson(obj);
		System.out.println(gsonRepr);
		
		try {
			System.setErr(new PrintStream(obj.getErrorLogFile()));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
		try {
			FileWriter writer = new FileWriter(WriteToFile.dataFilePath);
			writer.write(gsonRepr);
			writer.close();
		} catch (IOException e) {
			System.err.println(e);
		}
	}

}
