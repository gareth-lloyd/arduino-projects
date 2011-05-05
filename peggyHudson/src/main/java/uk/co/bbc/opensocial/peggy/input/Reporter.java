package uk.co.bbc.opensocial.peggy.input;



public interface Reporter {
	
    /**
	 * Abstract method to perform the update operation for
	 * which the class is configured
	 */
	public void update ();	
	
	/**
	 * Method which must be implemented to set up access 
	 * to the service. 
	 *
	 */
	public boolean configure();	
	
	/**
	 * Function called when the reporter is activated.
	 */
	public void enable();
	
	/**
	 * function called when the reporter is disabled.
	 */
	public void disable();
}
