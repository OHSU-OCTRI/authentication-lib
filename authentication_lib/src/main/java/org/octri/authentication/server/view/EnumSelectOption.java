package org.octri.authentication.server.view;

import java.util.Collection;

/**
 * Used for UI select inputs. Wraps the choice along with its selected status.
 *
 * @author harrelst
 *
 * @param<T>
 *
 * TODO:
 *               Move into a stand alone library.
 */
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
