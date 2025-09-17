package org.octri.authentication.server.view;

/**
 * Interface used for identifying an object in the UI.
 *
 * @deprecated
 *             This interface will be removed along with
 *             {@link org.octri.authentication.server.security.entity.AbstractEntity}. Consuming applications should use
 *             {@link org.octri.common.domain.AbstractEntity} and related methods from
 *             <a href="https://github.com/OHSU-OCTRI/common-lib">OCTRI common-lib</a> instead.
 *
 * @author yateam
 *
 */
@Deprecated(since = "2.3.0", forRemoval = true)
public interface Identified {

	/**
	 *
	 * @return the identifier for the object
	 */
	public abstract Long getId();

}
