package uk.co.bbc.opensocial.peggy.output;

import org.apache.commons.lang.StringUtils;

public class HudsonDisplay implements Display {
    private ArduinoConnection arduino = null;
    
    private final String NAME = "Peggy";
    private final String IMAGE_NAME = "PeggyIcon.png";

    public static final int MAX_MESSAGE_LENGTH = 60;

    private static final String INPUT_SEPARATOR = "&";

    private static final char SUN_CODE = 'A';
    private static final char SUN_CLOUD_CODE = 'B';
    private static final char CLOUD_CODE = 'C';    
    private static final char RAIN_CODE = 'D';
    private static final char LIGHTNING_CODE = 'E';
    
    private static final String INPUT_TERMINATOR = ";";
    
    /** {@inheritDoc} */
    public void activate() {
    }
    
    /**
     * Configure this display, including setting up the arduino connection.
     * 
     * @see info.ragtag.peggy.displays.Display#configure()
     */
    public void configure() {
        try {
            arduino = ArduinoConnection.getNewConnection();
        } catch (DisplayConfigurationException e) {
            throw new RuntimeException(e);
        }
    }
    
    /**
     * @see DisplayController.ragtag.peggy.gui.SocialDisplayController#deactivate().
     */
    public void deactivate() {
        if (arduino != null) {
            arduino.close();    
        }
        arduino = null;
    }

    /** {@inheritDoc} */
    public String getImageName() {
        return IMAGE_NAME;
    }

    /** {@inheritDoc} */
    public String getName() {
        return NAME;
    }

    /** {@inheritDoc} */
    public void suspend() {
    }

    /**
     * produce the 'sun' output
     * @param projectName
     */
    public void sun(String projectName) {
        projectName = validateArduinoInput(projectName);
        arduino.writeToArduino(assembleOutputString(projectName, SUN_CODE));
    }

    /**
     * produce the 'cloud' output.
     * 
     * @param projectName
     */
    public void cloud(String projectName) {
        projectName = validateArduinoInput(projectName);
        arduino.writeToArduino(assembleOutputString(projectName, CLOUD_CODE));            
    }
    
    /**
     * produce the 'lightning' output.
     * 
     * @param projectName
     */
    public void lightning(String projectName) {
        projectName = validateArduinoInput(projectName);
        arduino.writeToArduino(assembleOutputString(projectName, LIGHTNING_CODE));
    }
    
    /**
     * produce the 'sun and cloud' output.
     * @param projectName
     */
    public void sunCloud(String projectName) {
        projectName = validateArduinoInput(projectName);
        arduino.writeToArduino(assembleOutputString(projectName, SUN_CLOUD_CODE));
    }
    
    /**
     * produce the rain output
     * @param projectName
     */
    public void rain(String projectName) {
        projectName = validateArduinoInput(projectName);
        arduino.writeToArduino(assembleOutputString(projectName, RAIN_CODE));
    }
    
    private String validateArduinoInput(String input) {
        input = input.toUpperCase();
        input = input.replaceAll("[^:><=;@0-9A-Z" + INPUT_SEPARATOR + INPUT_TERMINATOR + " ]", "");
        if (input.length() > (MAX_MESSAGE_LENGTH - 7)) {
            input = input.substring(0, MAX_MESSAGE_LENGTH);
        }
        input = StringUtils.rightPad(input, MAX_MESSAGE_LENGTH);
        return input;
    }
    
    
    private String assembleOutputString(String projectName, char icondCode) {
        return projectName + INPUT_SEPARATOR + icondCode + INPUT_TERMINATOR;
    }
}
