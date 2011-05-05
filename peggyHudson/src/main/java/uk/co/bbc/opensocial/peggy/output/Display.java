package uk.co.bbc.opensocial.peggy.output;

/**
 * Defines the contract that must be fulfilled by a 
 * display object.
 * 
 * @author glloyd
 *
 */
public interface Display {

	/**
	 * Configure display.
	 * 
	 * After this method is called, the display must be
	 * ready for activation.
	 */
	public void configure();
	
	/**
	 * Activate display; start producing output.
	 */
	public void activate();

	/**
	 * Stop producing output, but do not destroy state
	 * information or free up any resources held.
	 * 
	 * After this method is completed, the display must be
	 * ready to receive further calls to activate();
	 */
	public void suspend();

	/**
	 * Stop the production of output, and free any
	 * resources on the assumption that the dipslay
	 * will not be activated again until 'configure()'
	 * has been called.
	 */
	public void deactivate();
	
	/**
	 * Get the name of the display.
	 * 
	 * @return The name
	 */
	public String getName();
	
	/**
	 * Get the image name.
	 * @return
	 		The image name
	 */
	public String getImageName();
}
