package main.java.fishtank.main;

import java.io.IOException;

import resources.MyLogger;

public class FishTank {

	public static void main(String[] args) {
		try {
			MyLogger.setup();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

}
