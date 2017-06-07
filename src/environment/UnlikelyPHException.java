package environment;

public class UnlikelyPHException extends Exception {
	public UnlikelyPHException() {
		super("Given pH has an unlikely value.");
	}
	
	public UnlikelyPHException(String message) {
		super(message);
	}
}
