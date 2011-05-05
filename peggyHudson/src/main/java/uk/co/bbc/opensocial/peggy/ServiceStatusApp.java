package uk.co.bbc.opensocial.peggy;

import uk.co.bbc.opensocial.peggy.input.ReporterRotation;
import uk.co.bbc.opensocial.peggy.link.ServiceStatusLink;
import uk.co.bbc.opensocial.peggy.output.HudsonDisplay;

public class ServiceStatusApp {
    public static void main(String[] args) {
        // set up display side:
        HudsonDisplay display = new HudsonDisplay();
        display.configure();
        ServiceStatusLink link = new ServiceStatusLink(display);
        EventSwitch.getInstance().registerConsumer(link);
        link.start();
        link.setRotationDelayInSeconds(10);
        
        // set up input side
        ServiceStatusClientFactory factory = new ServiceStatusClientFactory();
        ReporterRotation rotation = new ReporterRotation(30, factory.loadClients("/service_status_endpoints.txt"));
        rotation.start();
    }
}
