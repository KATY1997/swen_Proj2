package exceptions;

public class UnsupportedModeException extends Exception {
	public UnsupportedModeException() {
		System.out.println("Fuel conservation mode not supported! Quitting...");
	}
}
