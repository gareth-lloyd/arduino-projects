/**
 * 
 */
package uk.co.bbc.opensocial.peggy.config;

import java.util.Set;

/**
 * Class to encapsulate an item of configuration info. 
 * Records the name of the item, whether it has a set of 
 * predefined optional values, and the value that can 
 * be given by the user.
 * 
 * @author gl8279
 *
 */

public class ConfigurationInfo {
	private final String fieldTitle;
	private final String userMessage;
	//TODO display messages to user
	
	private final boolean isPassword;
	private String[] options = null;
	
	private String value = null;
	
	
	/**
	 * Simplified constructor, requiring only title and 
	 * message
	 * 
	 * @param fieldTitle
	 * @param userMessage
	 */
	public ConfigurationInfo(String fieldTitle, String userMessage) {
		this.fieldTitle = fieldTitle;
		this.userMessage = userMessage;
		this.isPassword = false;
	}
	
	/**
	 * Constructor allowing user to specify if the text
	 * field in question should have its input
	 * obscured, like a password entry field. 
	 * 
	 * @param fieldTitle
	 * @param userMessage
	 * @param isPassword
	 */
	public ConfigurationInfo(String fieldTitle, String userMessage, boolean isPassword) {
		this.fieldTitle = fieldTitle;
		this.userMessage = userMessage;
		this.isPassword = isPassword;
	}
	
	public ConfigurationInfo(String fieldTitle, String userMessage, Set<String> optionSet) {
		this.fieldTitle = fieldTitle;
		this.userMessage = userMessage;
		this.isPassword = false;
		
		options = new String[optionSet.size()];
		
		int i = 0;
		for (String option : optionSet) {
			options[i++] = option;
		}
	}
	
	/**
	 * Does this ConfigurationInfo have options?
	 * @return
	 */
	public boolean hasOptions() {
		return (options != null);
	}
	
	/**
	 * Is this a password field?
	 * 
	 * @return
	 */
	public boolean isPassword() {
		return isPassword;
	}
	
	/**
	 * Getter for field title.
	 * 
	 * @return
	 * 	The title
	 */
	public String getFieldTitle() {
		return fieldTitle;
	}
	
	/**
	 * Get set of option values; may return null.
	 * @return
	 * 	option values
	 */
	public String[] getOptionValues() {
		return options;
	}
	
	/**
	 * Getter for user message
	 * 
	 * @return
	 */
	public String getUserMessage() {
		return userMessage;
	}
	/**
	 * Getter for value of user entry.
	 * @return
	 */
	public String getValue() {
		return value;
	}
	/**
	 * Setter to record user entry.
	 * 
	 * @param value
	 */
	public void setValue(String value) {
		this.value = value;
	}
}
