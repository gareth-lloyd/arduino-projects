package uk.co.bbc.opensocial.peggy.input.archive;

/**
 * General exception that indicates that an error
 * has occurred during authentication with a web
 * service.
 * 
 * @author gl8279
 *
 */
public class AuthenticationException extends Exception {
	/**
	 * no-arg constructor.
	 */
	public AuthenticationException() {
		super();
	}

	/**
	 * Constructor with message specified. 
	 * 
	 * @param message
	 */
	public AuthenticationException(String message) {
		super(message);
	}

	/**
	 * Constructor with message and cause specified.  
	 * 
	 * @param message
	 * @param cause
	 */
	public AuthenticationException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * Constructor with cause specified. 
	 * @param cause
	 */
	public AuthenticationException(Throwable cause) {
		super(cause);
	}
}
