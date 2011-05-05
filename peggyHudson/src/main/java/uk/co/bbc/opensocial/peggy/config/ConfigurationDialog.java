package uk.co.bbc.opensocial.peggy.config;

import java.awt.Dimension;
import java.awt.event.ActionListener;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

/**
 * The Configuration dialog initializes a text entry
 * field for each item of ConfigurationInfo and presents
 * them to the user for input.
 * 
 * @author gl8279
 *
 */
public class ConfigurationDialog extends JDialog {

	private List<InputPanel> inputPanels = new ArrayList<InputPanel>();
	
	/**
	 * Sole Constructor. Sets up the dialog box. 
	 * 
	 * @param configName
	 * 		The name of this configuration screen
	 * @param image
	 * 		filename of image to display
	 * @param config
	 * 		Map of configuration info 
	 * @param submitListener
	 * 		Listener for submit button
	 * @param cancelListener
	 * 		Listener for cancel button
	 */
	public ConfigurationDialog(String configName, String image, Map<String, ConfigurationInfo> config, 
			ActionListener submitListener, ActionListener cancelListener) {

		// dialog box's inner panel
		JPanel configPanel = new JPanel();
		configPanel.setLayout(new BoxLayout(configPanel, BoxLayout.Y_AXIS));
		configPanel.add(Box.createVerticalStrut(10));
		
		JPanel headPanel = new JPanel();
		headPanel.setLayout(new BoxLayout(headPanel, BoxLayout.X_AXIS));
		headPanel.add(Box.createHorizontalStrut(10));
		if (image != null) {
		}
		headPanel.add(Box.createHorizontalStrut(10));
		if (configName != null) {
			headPanel.add(new JLabel("Enter Configuration for " + configName));
		}
		else {
			headPanel.add(new JLabel("Enter Configuration info:"));
		}
		headPanel.add(Box.createHorizontalStrut(10));
		configPanel.add(headPanel);
		configPanel.add(Box.createVerticalStrut(10));
		
		// Set up input controls
		for (String name : config.keySet()) {
			ConfigurationInfo thisInfo = config.get(name);
			if (thisInfo.hasOptions()) {
				configPanel.add(new ConfigComboBoxInput(name, thisInfo));
			}
			else {
				configPanel.add(new ConfigTextInput(name, thisInfo));
			}			
			configPanel.add(Box.createVerticalStrut(10));
		}

		JPanel buttons = new JPanel();
		buttons.setLayout(new BoxLayout(buttons, BoxLayout.X_AXIS));
		JButton submit = new JButton("Submit");
		submit.addActionListener(submitListener);
		buttons.add(submit);
		
		JButton cancel = new JButton("Cancel");
		cancel.addActionListener(cancelListener);
		buttons.add(cancel);
		configPanel.add(buttons);
		
		configPanel.add(Box.createVerticalStrut(10));
		this.setModalityType(ModalityType.APPLICATION_MODAL);
		this.add(configPanel);
		this.pack();
		
	}
	
	/**
	 * Private inner class represents a text input.
	 */
	private class ConfigTextInput extends JPanel implements InputPanel {
		/**
         * 
         */
        private static final long serialVersionUID = 1L;
        JTextField textField;
		
		public ConfigTextInput(String name, ConfigurationInfo info) {
			setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
			add(Box.createHorizontalStrut(10));
	
			JLabel label = new JLabel(info.getFieldTitle());
			label.setPreferredSize(new Dimension(100, 20));
			add(label);
				
			textField = (info.isPassword()) ? new JPasswordField() : new JTextField();
			textField.setName(name);
			textField.setColumns(10);
			add(textField);
			
			add(Box.createHorizontalStrut(10));
			
			inputPanels.add(this);
		}

		public String getInputValue() {
			return textField.getText();
		}
		
		public String getName() {
			return textField.getName();
		}
	}
		
	/**
	 * Private inner class representing a combo box input.
	 */
	private class ConfigComboBoxInput extends JPanel implements InputPanel {
		JComboBox combo;
		
		ConfigComboBoxInput(String name, ConfigurationInfo info) {
			setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
			add(Box.createHorizontalStrut(10));
	
			JLabel label = new JLabel(info.getFieldTitle());
			label.setPreferredSize(new Dimension(100, 20));
			add(label);
			
			String[] options = info.getOptionValues();
			combo = new JComboBox(options);
			combo.setName(name);
			add(combo);
			
			add(Box.createHorizontalStrut(10));
			
			inputPanels.add(this);
		}
		
		public String getInputValue() {
			return (String)combo.getSelectedItem();
		}
		
		public String getName() {
			return combo.getName();
		}
		
	}
	
	
	/**
	 * Get the List of text field objects.
	 * 
	 * @return
	 * 		list of text fields
	 */
	public List<InputPanel> getInputPanels() {
		return inputPanels;
	}
	
}
