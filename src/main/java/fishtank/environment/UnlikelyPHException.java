package main.java.fishtank.environment;

public class UnlikelyPHException extends Exception {

	private static final long serialVersionUID = 1L;

	public UnlikelyPHException() {
		super("Given pH has an unlikely value.");
	}
	
	public UnlikelyPHException(String message) {
		super(message);
	}
}
