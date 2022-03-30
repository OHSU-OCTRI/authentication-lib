package org.octri.authentication.server.view;

/**
 * Interface used for identifying an object in the UI.
 * 
 * @author yateam
 *
 */
public interface Identified {
	
	/**
	 * 
	 * @return the identifier for the object
	 */
	public abstract Long getId();

}
