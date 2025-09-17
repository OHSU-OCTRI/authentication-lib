package org.octri.authentication.server.view;

/**
 * Interface used for labelling an object in the UI.
 *
 * @deprecated
 *             Use {@link org.octri.common.view.Labelled} from <a href="https://github.com/OHSU-OCTRI/common-lib">OCTRI
 *             common-lib</a> instead.
 *
 * @author lawhead
 *
 */
@Deprecated(since = "2.3.0", forRemoval = true)
public interface Labelled {

	/**
	 * @return Label presented to the user in the UI.
	 */
	public abstract String getLabel();
}
