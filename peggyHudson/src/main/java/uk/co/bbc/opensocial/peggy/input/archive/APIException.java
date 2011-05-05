package uk.co.bbc.opensocial.peggy.input.archive;

/**
 * General Exception that indicates a problem running
 * an API method.
 * 
 * @author gl8279
 *
 */
public class APIException extends Exception {

	/**
	 * no-arg constructor.
	 */
	public APIException() {
		super();
	}

	/**
	 * Constructor with message specified. 
	 * 
	 * @param message
	 */
	public APIException(String message) {
		super(message);
	}

	/**
	 * Constructor with message and cause specified.  
	 * 
	 * @param message
	 * @param cause
	 */
	public APIException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * Constructor with cause specified. 
	 * @param cause
	 */
	public APIException(Throwable cause) {
		super(cause);
	}
}
