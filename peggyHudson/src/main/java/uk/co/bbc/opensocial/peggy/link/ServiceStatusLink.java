package uk.co.bbc.opensocial.peggy.link;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import uk.co.bbc.opensocial.peggy.event.ServiceStatus;
import uk.co.bbc.opensocial.peggy.output.HudsonDisplay;

/**
 * Displays a steadily rotating set of statuses.
 * 
 * @author glloyd
 *
 */
public class ServiceStatusLink {

    private int rotationDelayInSeconds = 10;
    private final HudsonDisplay hudsonDisplay;
    private ServiceStatusRotator statusRotator = null;
    private final Object statusRotatorLock = new Object();
    
    /** create a synchronized set to prevent conflict between adding and copying operations */
    private Set<ServiceStatus> statuses = Collections.synchronizedSet(new HashSet<ServiceStatus>());
    
    
    public ServiceStatusLink(HudsonDisplay hudsonDisplay) {
        this.hudsonDisplay = hudsonDisplay;
    }
    
    /**
     * This is the method registered with the event switch
     * @param status
     */
    public void produceOutput(ServiceStatus status) {
        statuses.add(status);
    }
    
    /**
     * @param rotationDelay the rotationDelay to set
     */
    public void setRotationDelayInSeconds(int rotationDelay) {
        this.rotationDelayInSeconds = rotationDelay;
    }

    /**
     * @return the rotationDelay
     */
    public int getRotationDelayInSeconds() {
        return rotationDelayInSeconds;
    }

    /**
     * Do the actual display bit for a given status
     * @param status
     */
    private void doDisplay(ServiceStatus status) {
        String statusString = status.getServiceName() + ": " + status.getStatusName();
        if (status.isWorking()) {
            hudsonDisplay.sun(statusString);
        }
        else {
            hudsonDisplay.lightning(statusString);
        }
    }
    
    /**
     * Runnable class that repeatedly makes a copy of the 
     * parent class's statuses (and sorts them in their natural 
     * order), and then displays them one by one. 
     * 
     * The statuses displayed may therefore become slightly
     * out of date, but will be accurate at the start of 
     * each display rotation.
     */
    private class ServiceStatusRotator implements Runnable {
        private boolean keepGoing = true;
        
        public void stop() {
            this.keepGoing = false;
        }
        
        public void run() {
            while (keepGoing) {
                // copy and sort statuses to a local var
                SortedSet<ServiceStatus> sortedStatuses = new TreeSet<ServiceStatus>();
                
                // obtain lock on statuses, because the addAll method iterates over the 
                // statuses collection
                synchronized(statuses) {
                    sortedStatuses.addAll(statuses);
                }
                for (ServiceStatus status : sortedStatuses) {
                    doDisplay(status);
                    try {Thread.sleep(1000 * rotationDelayInSeconds);} 
                    catch (InterruptedException e) {throw new RuntimeException("unexpected interruption");}
                
                }
            }
        }        
    }
    
    /**
     * Start the rotation.
     * 
     * There are two valid states for statusRotator: started, 
     * in which case it's not null, or stopped, when it is null.
     * However, transferring between these states is a compound 
     * operation, so we have concurrency issues.
     * 
     * To ensure we transition between states without interference
     * we synchronize on a lock object that governs access to
     * statusRotator.
     * 
     */
    public void start() {
        synchronized (statusRotatorLock) {
            if (statusRotator != null) {
                // already started
                return;
            }
            statusRotator = new ServiceStatusRotator();
            new Thread(statusRotator).start();
        }
    }
    
    /**
     * stop the rotation
     */
    public void stop() {
        synchronized (statusRotatorLock) {
            if (statusRotator == null) {
                // already stopped
                return;
            }
            // put the runnable in a state where it will return from
            // its run() method cleanly:
            statusRotator.stop();
            // prevent any further reference to the stopped runnable:
            statusRotator = null;
        }
    }
}
