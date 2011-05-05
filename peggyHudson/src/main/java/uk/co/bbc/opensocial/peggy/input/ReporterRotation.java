package uk.co.bbc.opensocial.peggy.input;

import java.util.Collection;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Class to contain a circular queue of reporters each called on to
 * update in turn at a specified interval. 
 * @author glloyd
 *
 */
public class ReporterRotation {

    private Queue<Reporter> circle = new ConcurrentLinkedQueue<Reporter>();
    private final long interval;
    
    private RotationRunnable rotationRunnable = null;
    private Object rotationRunnableLock = new Object();
    
    /**
     * constructor that sets interval, and doesn't add
     * any reporters 
     * 
     * @param interval
     *  time between rotations
     */
    public ReporterRotation(int rotationIntervalInSeconds) {
        this(rotationIntervalInSeconds, null);
    }

    /**
     * constructor that sets interval, and doesn't add
     * any reporters 
     * 
     * @param interval
     *  time between rotations
     * @param reportersToAdd
     *  Collection of reporter objects that should be added to queue
     *  
     */
    public ReporterRotation(int rotationIntervalInSeconds, Collection<Reporter> reportersToAdd) {
        this.interval = rotationIntervalInSeconds * 1000L;
        
        if (reportersToAdd != null) {
            for (Reporter reporter : reportersToAdd) {
                circle.add(reporter);
            }
        }
    }
    
    /**
     * Start the rotation.
     * 
     * There are two valid states for rotationRunnable: started, 
     * in which case it's not null, or stopped, when it is null.
     * However, transferring between these states is a compound 
     * operation, so we have concurrency issues.
     * 
     * To ensure we transition between states without interference
     * we synchronize on a lock object that governs access to
     * rotationRunnable.
     * 
     */
    public void start() {
        synchronized (rotationRunnableLock) {
            if (rotationRunnable != null) {
                // already started
                return;
            }
            rotationRunnable = new RotationRunnable();
            new Thread(rotationRunnable).start();
        }
    }
    
    /**
     * stop the rotation
     */
    public void stop() {
        synchronized (rotationRunnableLock) {
            if (rotationRunnable == null) {
                // already stopped
                return;
            }
            // put the runnable in a state where it will return from
            // its run() method cleanly:
            rotationRunnable.stop();
            // prevent any further reference to the stopped runnable:
            rotationRunnable = null;
        }
    }
    
    /**
     * Class to perform the rotation.
     * 
     * @author glloyd
     *
     */
    private class RotationRunnable implements Runnable {
        private boolean keepRunning = true;

        public void stop() {
            keepRunning = false;
        }
        
        /*
         * Wait for interval milliseconds, then perform
         * the next update. Rinse and repeat.
         */
        public void run() {
            while (keepRunning) {
                nextUpdate();
                try {
                    Thread.sleep(interval);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }    
    
    /**
     * enqueue a reporter
     * @param r
     */
    public void addReporter(Reporter r) {
        circle.add(r);
    }
    
    /**
     * Tell the next reporter to update, then put it 
     * at the back of the queue.
     */
    private void nextUpdate() {
        Reporter reporter = circle.poll();
        
        if (reporter != null) {
            // use the reporter
            reporter.update();
            // put back to end of queue
            circle.add(reporter);
        }
    }
}
