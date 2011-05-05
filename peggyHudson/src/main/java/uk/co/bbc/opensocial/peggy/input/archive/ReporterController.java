package uk.co.bbc.opensocial.peggy.input.archive;

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.JOptionPane;

import uk.co.bbc.opensocial.peggy.input.Reporter;

/**
 * MVC Controller for Reporter class.
 * @author gl8279
 *
 */
public final class ReporterController {
	private Reporter mySR;
	private ReporterState state = ReporterState.UNCONFIGURED;
	
	private final static long UPDATE_INTERVAL = 60000;
	
	private Timer tmr = new Timer();
	
	/**
	 * Sole Constructor. Sets up view, listener and 
	 * relationships. 
	 * 
	 * @param sr
	 		The Reporter to control
	 */
	public ReporterController(Reporter sr) {
		mySR = sr;
	}
	
	/**
	 * Perform the configuration steps.
	 * 
	 * Get input from the 
	 */
	public boolean configureReporter() throws AuthenticationException {
		if (state != ReporterState.UNCONFIGURED) {
			return true;
		}
		
		// Tell Reporter to go ahead with config
		if (!mySR.configure()) {
			// User cancelled
			return false;
		}
		
		// We are now configured, but not yet enabled.
		state = ReporterState.DISABLED;
		return true;
	}
	
	/**
	 * Start the reporter operation by setting up a 
	 * new timer task which will regularly call its
	 * update() function.
	 */
	public void enableReporter() {
		// if already enabled, do nothing
		if (state == ReporterState.ENABLED) {
			return;
		}
		// FIRST_FAIL state implies Reporter is already running. 
		// Do nothing.
		if (state == ReporterState.FIRST_FAIL) {
			return;
		}
		// if UNCONFIGURED, configure it before enabling
		if (state == ReporterState.UNCONFIGURED) {
			try {
				if (!configureReporter()) {
					// user cancelled
					return;
				}
			} catch (AuthenticationException e) {
				e.printStackTrace();
				JOptionPane.showMessageDialog(null, "Configuration failed. This service cannot " +
					"be used until configured.", "Authentication Error", 
					JOptionPane.ERROR_MESSAGE);
				return;
			}
		}
		
		ReporterTask srt = new ReporterTask(mySR);
		tmr.schedule(srt, (long) 1000, UPDATE_INTERVAL);
		mySR.enable();
		state = ReporterState.ENABLED;
	}
	
	/**
	 * Stop the reporter by cancelling the old timer and 
	 * replacing it with a new one which has no tasks.
	 */
	public void disableReporter() {
		if (state == ReporterState.UNCONFIGURED) {
			// DISABLED implies configured, but not actively working
			// so we should not allow an UNCONFIGURED Reporter
			// to progress directly to DISABLED.
			return;
		}
		if (state == ReporterState.DISABLED) {
			return;
		}
		mySR.disable();
		tmr.cancel();
		tmr = new Timer();
		state = ReporterState.DISABLED;
	}
	
	/**
	 * Getter for state.
	 * 
	 * @return
	 * 	The current state
	 */
	public ReporterState getState() {
		return state;
	}
		
	/**
	 * A task for a Reporter is an execution of its update()
	 * method. This subtype of TimerTask can be added to a timer
	 * to perform this task regularly.
	 * 
	 * @author glloyd
	 *
	 */
	private class ReporterTask extends TimerTask
	{
		Reporter sr;
		
		public ReporterTask(Reporter sr) {
			this.sr = sr;
		}
		
		public void run() {
			try {
				sr.update();
				if (state == ReporterState.FIRST_FAIL) {
					// If previously failed, but now succeeding, return to 
					// ENABLED state.
					state = ReporterState.ENABLED;
				}
			} catch (Exception e) {
				e.printStackTrace();
				if (state == ReporterState.ENABLED) {
					System.err.println("\n\nWARNING: Failed to retrieve update.");
					state = ReporterState.FIRST_FAIL;
				}
				// If the reporter has failed once before, disable it
				// so that it stops updating, warn the user, and then 
				// set to UNCONFIGURED to force reconfiguration
				else if (state == ReporterState.FIRST_FAIL) {
					disableReporter();
					state = ReporterState.UNCONFIGURED;
				}
			}
		}
	}
}
