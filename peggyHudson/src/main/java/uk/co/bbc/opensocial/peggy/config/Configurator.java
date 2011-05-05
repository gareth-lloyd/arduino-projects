package uk.co.bbc.opensocial.peggy.config;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.Map;

import javax.swing.JPanel;

/**
 * Gets configuration input from a user.
 * 
 * @author gl8279
 *
 */
public class Configurator extends JPanel {
	private ConfigurationDialog cp;
	private Map<String, ConfigurationInfo> config;
	boolean submitted = false;
	
	/**
	 * Get a set of information from the user
	 *
	 * @param name
	 * 		A name for the config dialog 
	 * @param imagePath 
	 * 		Image to display
	 * @param config
	 * 		The information to gather. Each item of ConfigurationInfo
	 * 		is also used to contain the data entered by the user.
	 * @return
	 * 		True if the user selected 'submit', false if 'cancel'
	 */
	public boolean getInfoFromUser(String name, String imagePath, Map<String, ConfigurationInfo> config) {
		// If no configuration info, nothing to do, so return:
		if (null == config || config.size() == 0)
			return true;
		
		this.config = config;
		
		cp = new ConfigurationDialog(name, imagePath, config, new SubmitListener(), new CancelListener());
		cp.setVisible(true);
		cp.addWindowListener(new QuitListener());
		
		// After setVisible is called, this method blocks until 
		// user submits or cancels
		
		return submitted;
	}
	
	/**
	 * Minimal form of the method which does not demand
	 * an image or name for the configuration dialog.
	 * 
	 * @param config
	 * 		The information to gather. Each item of ConfigurationInfo
	 * 		is also used to contain the data entered by the user.
	 * @return
	 * 		True if the user selected 'submit', false if 'cancel'
	 */
	public boolean getInfoFromUser(Map<String, ConfigurationInfo> config) {
		return getInfoFromUser(null, null, config);
	}
	
	
	/**
	 * Inner class to listen to the Submit button
	 */
	private class SubmitListener implements ActionListener {
		/**
		 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
		 */
		public void actionPerformed(ActionEvent e) {
			// get information from text fields into ConfigurationInfo
			for (InputPanel ip : cp.getInputPanels()) {
				ConfigurationInfo info = config.get(ip.getName());
				
				if (null != info) {
					info.setValue(ip.getInputValue());
				}
			}
			submitted = true;
			cp.setVisible(false);
			cp.dispose();
		}
	}
	
	/**
	 * Inner class to listen to the cancel button
	 */
	private class CancelListener implements ActionListener {
		/**
		 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
		 */
		public void actionPerformed(ActionEvent e) {
			submitted = false;
			cp.setVisible(false);
			cp.dispose();
		}
	}
	
	private class QuitListener implements WindowListener {
		public void windowActivated(WindowEvent e) {
			// TODO Auto-generated method stub
		}
		public void windowClosed(WindowEvent e) {
			submitted = false;
		}
		public void windowClosing(WindowEvent e) {
			// TODO Auto-generated method stub
		}
		public void windowDeactivated(WindowEvent e) {
			// TODO Auto-generated method stub
		}
		public void windowDeiconified(WindowEvent e) {
			// TODO Auto-generated method stub
		}
		public void windowIconified(WindowEvent e) {
			// TODO Auto-generated method stub
		}
		public void windowOpened(WindowEvent e) {
			// TODO Auto-generated method stub
		}
	}
}
