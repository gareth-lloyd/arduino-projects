package uk.co.bbc.opensocial.peggy.output;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.JOptionPane;


/**
 * An MVC controller that governs a SocialDisplay and 
 * interacts with a SocialDisplayView.
 * 
 * @author glloyd
 *
 */
public class DisplayController {
	private final Display myDisplay;
	private DisplayState state = DisplayState.DEACTIVATED;
	
	/**
	 * Sole Constructor: set up relationship with Social Display.
	 * 
	 * @param display
	 		THe social display to control
	 */
	public DisplayController(Display display) {
		this.myDisplay = display;
	}
	
	/**
	 * Configure a currently deactivated display.
	 * 
	 * @throws DisplayConfigurationException
	 */
	public void configureSocialDisplay() {
		if (state != DisplayState.DEACTIVATED) {
			deactivateSocialDisplay();
		}
		try {
			myDisplay.configure();
			state = DisplayState.SUSPENDED;
		}
		catch (RuntimeException e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(null, "Could not configure "
					+ myDisplay.getName(), "Display Error", 
					JOptionPane.ERROR_MESSAGE);
		}
	}
	
	/**
	 * If state is suspended, i.e. display is configured
	 * and ready for activation, activate it. 
	 */
	public void activateSocialDisplay() {
		if (state == DisplayState.ACTIVATED) {
			return;
		}
		if (state == DisplayState.DEACTIVATED) {
			configureSocialDisplay();
			if (state != DisplayState.SUSPENDED) {
				return;
			}
		}
		try {
			myDisplay.activate();
			state = DisplayState.ACTIVATED;
		}
		catch (RuntimeException e) {
			JOptionPane.showMessageDialog(null, "Could not activate display", 
					"Display Error", JOptionPane.ERROR_MESSAGE);
		}
	}

	/**
	 * If Activated or Suspended, deactivate the 
	 * social display
	 */
	public void deactivateSocialDisplay() {
		if (state == DisplayState.DEACTIVATED) {
			return;
		}
		
		myDisplay.deactivate();
		state = DisplayState.DEACTIVATED;		
	}

	/**
	 * If currently activated, suspend the social display.
	 */
	public void suspendSocialDisplay() {
		if (state != DisplayState.ACTIVATED) {
			return;
		}
		myDisplay.suspend();
		state = DisplayState.SUSPENDED;		
	}
}
