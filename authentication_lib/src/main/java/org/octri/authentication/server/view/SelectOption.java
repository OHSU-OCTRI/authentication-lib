package org.octri.authentication.server.view;

import java.util.Collection;

/**
 * Used for UI select inputs. Wraps the choice along with its selected status. Value and label is the choice.
 * 
 * @author lawhead
 *
 * @param <T>
 */
public class SelectOption<T> {

	protected T choice;
	protected String value;
	protected String label;
	protected Boolean selected;

	/**
	 * Constructor. The label and value will be set to choice.toString().
	 * 
	 * @param choice
	 *            - lookup list item
	 * @param selected
	 *            - The selected item; may be null
	 */
	public SelectOption(T choice, T selected) {
		this.choice = choice;
		this.label = choice.toString();
		this.value = choice.toString();
		this.selected = choice.equals(selected);
	}
	
	/**
	 * Constructor used for an option in a multi-select.
	 * 
	 * @param choice
	 *            - lookup list item
	 * @param selected
	 *            - collection of selected items
	 */
	public SelectOption(T choice, Collection<T> selected) {
		this.choice = choice;
		this.label = choice.toString();
		this.value = choice.toString();
		this.selected = selected != null && selected.contains(choice);
	}

	public T getChoice() {
		return choice;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public Boolean getSelected() {
		return selected;
	}

	public void setSelected(Boolean selected) {
		this.selected = selected;
	}
}
