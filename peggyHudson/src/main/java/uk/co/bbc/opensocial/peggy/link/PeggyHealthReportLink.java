package uk.co.bbc.opensocial.peggy.link;

import uk.co.bbc.opensocial.peggy.event.HealthReport;
import uk.co.bbc.opensocial.peggy.output.HudsonDisplay;

public class PeggyHealthReportLink {
    private final HudsonDisplay hudsonDisplay;
    
    public PeggyHealthReportLink(HudsonDisplay hudsonDisplay) {
        this.hudsonDisplay = hudsonDisplay;
    }
    
    public void produceOutput(HealthReport report) {
        int score = report.getScore();
        
        if (score <= 20) {
            hudsonDisplay.lightning(report.getProjectName());
        }
        else if (score <= 40) {
            hudsonDisplay.rain(report.getProjectName());
        }
        else if (score <= 60) {
            hudsonDisplay.cloud(report.getProjectName());
        }
        else if (score <= 80) {
            hudsonDisplay.sunCloud(report.getProjectName());
        }
        else {
            hudsonDisplay.sun(report.getProjectName());
        }
    }
    
}
