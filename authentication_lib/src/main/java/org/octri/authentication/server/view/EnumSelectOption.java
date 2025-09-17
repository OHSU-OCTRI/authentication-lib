package org.octri.authentication.server.view;

import java.util.Collection;

/**
 * Used for UI select inputs. Wraps the choice along with its selected status.
 *
 *
 * @deprecated
 *             Use {@link org.octri.common.view.EnumSelectOption} from
 *             <a href="https://github.com/OHSU-OCTRI/common-lib">OCTRI common-lib</a> instead.
 *
 * @author harrelst
 *
 * @param <T>
 *            the enum type wrapped by the option
 */
@Deprecated(since = "2.3.0", forRemoval = true)
public class EnumSelectOption<T extends Enum<T> & Labelled> extends SelectOption<T> {

	/**
	 * Constructor
	 *
	 * @param choice
	 *            - Enum item
	 * @param selected
	 *            - The selected item; may be null
	 */
	public EnumSelectOption(T choice, T selected) {
		super(choice, selected);
		this.setLabel(choice.getLabel());
	}

	/**
	 * Constructor used for an option in a multi-select.
	 *
	 * @param choice
	 *            - Enum item
	 * @param selected
	 *            - collection of selected it
	 */
	public EnumSelectOption(T choice, Collection<T> selected) {
		super(choice, selected);
		this.setLabel(choice.getLabel());
	}

}
