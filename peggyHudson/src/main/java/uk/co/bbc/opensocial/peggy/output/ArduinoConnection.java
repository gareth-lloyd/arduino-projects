package uk.co.bbc.opensocial.peggy.output;

import gnu.io.CommPortIdentifier;
import gnu.io.NoSuchPortException;
import gnu.io.PortInUseException;
import gnu.io.SerialPort;
import gnu.io.UnsupportedCommOperationException;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import uk.co.bbc.opensocial.peggy.config.ConfigurationInfo;
import uk.co.bbc.opensocial.peggy.config.Configurator;


/**
 * Class to wrap a gnu.io.SerialPort connected to
 * an Arduino microcontroller board and allow clients
 * to write to the Arduino.
 *
 * @author glloyd
 */
public class ArduinoConnection {
    private InputStream input;
    private OutputStream output;
    private SerialPort port;

    private static final String IMAGE = "ArduinoIcon.png";
    private static final String NAME = "Arduino";
    
    /**
     * Set up a new arduino connection on the port specified
     * 
     * @param portName
     * 	Name of the port
     * 
     */
    private ArduinoConnection(String portName) 
            throws NoSuchPortException, PortInUseException, UnsupportedCommOperationException, IOException {
        CommPortIdentifier portId = CommPortIdentifier.getPortIdentifier(portName);
        
        port = (SerialPort) portId.open("serial talk", 4000);

        input = port.getInputStream();
        output = port.getOutputStream();

        port.setSerialPortParams(9600, SerialPort.DATABITS_8, SerialPort.STOPBITS_1, SerialPort.PARITY_NONE);
    }

    public static ArduinoConnection getNewConnection() throws DisplayConfigurationException {
    	Enumeration<CommPortIdentifier> ports = CommPortIdentifier.getPortIdentifiers();
		Set<String> portNames = new HashSet<String>();
		Map<String, ConfigurationInfo> portConfig = new HashMap<String, ConfigurationInfo>();
		
		while (ports.hasMoreElements()) {
			CommPortIdentifier p = ports.nextElement();
			portNames.add(p.getName());
		}

		ConfigurationInfo portConfigInfo = new ConfigurationInfo("Select Port:", "Select port", portNames);
		portConfig.put("portConfig", portConfigInfo);
		Configurator c = new Configurator();
		boolean success = c.getInfoFromUser(NAME, IMAGE, portConfig);
		if (!success) {
			throw new DisplayConfigurationException("User Cancelled configuration");
		}
		
		try {
			return new ArduinoConnection(portConfig.get("portConfig").getValue());
		} catch (NoSuchPortException ex) {
			throw new DisplayConfigurationException("Port does not exist");
		} catch (PortInUseException ex) {
			throw new DisplayConfigurationException("Port is in use");
		} catch (UnsupportedCommOperationException ex) {
			throw new DisplayConfigurationException("Port could not be configured");
		} catch (IOException ex) {
			throw new DisplayConfigurationException("Port could not be accessed");
		}
    }

    /**
     * write a byte to the arduino.
     * 
     * @param outByte
     */
    public void writeToArduino(int outByte) {
        try {
			output.write(outByte);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NullPointerException e) {
			e.printStackTrace();
		}
    }

    /**
     * Write an array of bytes to the Arduino.
     * 
     * @param outBytes
     */
    public void writeToArduino(byte outBytes[]) {
    	try {
			output.write(outBytes);
		} catch (IOException e) {
			e.printStackTrace();
		}
    }
    
    
    
    /**
     * Write a String to the arduino
     * @param outputString
     */
    public void writeToArduino(String outputString) {
        System.out.println(outputString);
    	writeToArduino(outputString.getBytes());
    }

    /**
     * Close streams and port
     */
    public void close() {
        try {
            if (input != null) {
            	input.close();
            }
            if (output != null){
            	output.close();
            }
        } catch (IOException e) {
          e.printStackTrace();
        }
        input = null;
        output = null;

        if (port != null) {
        	port.close();
        }

    }
}
