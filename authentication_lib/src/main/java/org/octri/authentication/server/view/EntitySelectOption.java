package org.octri.authentication.server.view;

import java.util.Collection;

/**
 * Used for UI select inputs. Wraps the choice along with its selected status.
 * 
 * @author lawhead
 *
 * @param <T>
 */
public class EntitySelectOption<T extends Identified & Labelled> extends SelectOption<T> {

	/**
	 * Constructor
	 * 
	 * @param choice
	 *            - lookup list item
	 * @param selected
	 *            - The selected item; may be null
	 */
	public EntitySelectOption(T choice, T selected) {
		super(choice, selected);
		this.setLabel(choice.getLabel());
		this.setValue(choice.getId().toString());
	}

	/**
	 * Constructor used for an option in a multi-select.
	 * 
	 * @param choice
	 *            - lookup list item
	 * @param selected
	 *            - collection of selected items
	 */
	public EntitySelectOption(T choice, Collection<T> selected) {
		super(choice, selected);
		this.setLabel(choice.getLabel());
		this.setValue(choice.getId().toString());
	}

	public Long getId() {
		return this.getChoice().getId();
	}
}
