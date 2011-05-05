package uk.co.bbc.opensocial.peggy;

import java.util.List;

import uk.co.bbc.opensocial.peggy.input.Reporter;
import uk.co.bbc.opensocial.peggy.input.ServiceStatusReporter;

public class ServiceStatusClientFactory extends ReporterFactory {

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
            throw new RuntimeException("ServiceStatusReporter definitions must be in format <name> <endpoint url>");
        }
        reporters.add(new ServiceStatusReporter(lineParts[0], lineParts[1]));
        return true;
    }

}
