package uk.co.bbc.opensocial.peggy.config;

/**
 * Interface to minimally characterize a method of taking
 * input from a user which has a name and a String value.
 * 
 * @author glloyd
 *
 */
public interface InputPanel {
	
	/**
	 * Get whatever has been input as a string.
	 * 
	 * @return
	 * 	the input
	 */
	public String getInputValue();
	
	/**
	 * GEt the name of this input
	 * 
	 * @return
	 */
	public String getName();
}
