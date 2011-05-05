package uk.co.bbc.opensocial.peggy;

import java.util.List;

import uk.co.bbc.opensocial.peggy.input.HudsonReporter;
import uk.co.bbc.opensocial.peggy.input.Reporter;

public class HudsonReporterFactory extends ReporterFactory {
    /**
     * 
     * @param line
     * @param clients TODO
     * @return whether there is more to process
     */
    @Override
    public boolean processLine(String line, List<Reporter> reporters) {
        if (line == null) {
            return false;
        }
        if (line.startsWith(FILE_COMMENT)) {
            return true;
        }
        String[] lineParts = line.split(" ", 2);
        if (lineParts.length < 2) {
            throw new RuntimeException("Hudson jobs must be in format <server> <job>");
        }
        reporters.add(new HudsonReporter(lineParts[0], lineParts[1]));
        return true;
    }

}
