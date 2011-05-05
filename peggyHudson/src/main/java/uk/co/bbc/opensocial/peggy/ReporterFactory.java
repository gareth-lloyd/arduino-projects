package uk.co.bbc.opensocial.peggy;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import uk.co.bbc.opensocial.peggy.input.Reporter;

public abstract class ReporterFactory {
    public static final String FILE_COMMENT = "#";
    
    /**
     * Process the lines of a text file defining reporters into 
     * a list of reporters.
     * 
     * @param textFile
     * @return
     */
    public List<Reporter> loadClients(String fileLocation) {
        InputStream is = this.getClass().getResourceAsStream(fileLocation);
        
        List<Reporter> clients = new ArrayList<Reporter>();
        BufferedReader reader;
        reader = new BufferedReader(new InputStreamReader(is));
        
        try {
            while (processLine(reader.readLine(), clients));
            
        } catch (IOException e) {
            throw new RuntimeException("Error while processing reporter definition file");
        }
        return clients;
    }
    
    /**
     * Given a line (which may be null), try to create a reporter and 
     * add it to the list passed. 
     * 
     * If parameter 'line' is null, the method MUST return false;
     * 
     * @param line
     * @param reporters
     * @return true if line is not null, otherwise false
     */
    public abstract boolean processLine(String line, List<Reporter> reporters); 
}
