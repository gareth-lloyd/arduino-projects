package uk.co.bbc.opensocial.peggy.input.archive;

public enum ReporterState {
	/* 
	 * Unconfigured SocialReporters are not active, and 
	 * are not ready to retrieve data.
	 */
	UNCONFIGURED, 
	
	/*
	 * enabled SocialReporters are configured, and are
	 * actively retrieving data.
	 */
	ENABLED, 
	
	/*
	 * A SocialReporter in 'First Fail' state has suffered
	 * an exception while performing its update() method, 
	 * but has not been disabled. 
	 */
	FIRST_FAIL, 
	
	/*
	 * A Disabled SocialReporter has been configured, but is
	 * not actively retrieving information.
	 */
	DISABLED;
}
