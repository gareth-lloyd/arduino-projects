package uk.co.bbc.opensocial.peggy.acceptance;


import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import uk.co.bbc.opensocial.peggy.EventSwitch;
import uk.co.bbc.opensocial.peggy.input.HudsonReporter;
import uk.co.bbc.opensocial.peggy.input.Reporter;
import uk.co.bbc.opensocial.peggy.input.ReporterRotation;
import uk.co.bbc.opensocial.peggy.link.PeggyHealthReportLink;
import uk.co.bbc.opensocial.peggy.output.HudsonDisplay;

public class HandleHudsonClientReport {

    @Before
    public void setUp() throws Exception {
    }

    @Test 
    public void testHudsonClient() {
        List<Reporter> reporters = new ArrayList<Reporter>();
        HudsonReporter hudsonClient = new HudsonReporter("https://ci.int.bbc.co.uk/hudson-int-app", "/view/SocialServices/job/Social%20API%20Client");
        reporters.add(hudsonClient);
        
        ReporterRotation rotation = new ReporterRotation(3, reporters);
        
        HudsonDisplay hudsonDisplay = new HudsonDisplay();
        hudsonDisplay.configure();
        
        PeggyHealthReportLink linkUp = new PeggyHealthReportLink(hudsonDisplay);
        EventSwitch.getInstance().registerConsumer(linkUp);
        
        
        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
