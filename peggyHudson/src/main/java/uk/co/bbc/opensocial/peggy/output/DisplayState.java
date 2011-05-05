package uk.co.bbc.opensocial.peggy.output;

public enum DisplayState {
	/* 
	 * A DEACTIVATED display cannot produce output
	 * and it cannot be activated without first being
	 * configured.
	 */
	DEACTIVATED,
	
	/*
	 * A SUSPENDED display is ready to produce output
	 * but is not currently doing so.
	 */
	SUSPENDED,
	
	/*
	 * An activated display is actively producing output
	 */
	ACTIVATED;
}
