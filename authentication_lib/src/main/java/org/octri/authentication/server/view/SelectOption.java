package org.octri.authentication.server.view;

import java.util.Collection;

/**
 * Used for UI select inputs. Wraps the choice along with its selected status. Value and label is the choice.
 *
 * @author lawhead
 *
 * @param <T>
 *            the type of object wrapped by the option
 */
public class SelectOption<T> {

	/**
	 * The object wrapped by the select option.
	 */
	protected T choice;

	/**
	 * The value of the option element's <code>value</code> attribute when rendered to HTML.
	 */
	protected String value;

	/**
	 * The text of the option element when rendered to HTML.
	 */
	protected String label;

	/**
	 * Whether the option is currently selected.
	 */
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

	/**
	 * Gets the item wrapped by the select option.
	 *
	 * @return the select option item
	 */
	public T getChoice() {
		return choice;
	}

	/**
	 * Gets the string used as the value of the option tag's <code>value</code> attribute.
	 *
	 * @return the option tag value
	 */
	public String getValue() {
		return value;
	}

	/**
	 * Sets the string used as the option tag's <code>value</code> attribute.
	 *
	 * @param value
	 *            the option tag value
	 */
	public void setValue(String value) {
		this.value = value;
	}

	/**
	 * Gets the string used as the text of the option tag.
	 *
	 * @return the option tag label
	 */
	public String getLabel() {
		return label;
	}

	/**
	 * Sets the string used as the text of the option tag.
	 *
	 * @param label
	 *            the option tag label
	 */
	public void setLabel(String label) {
		this.label = label;
	}

	/**
	 * Whether the item is currently selected.
	 *
	 * @return true if selected
	 */
	public Boolean getSelected() {
		return selected;
	}

	/**
	 * Sets whether the item is currently selected.
	 *
	 * @param selected
	 *            true if selected
	 */
	public void setSelected(Boolean selected) {
		this.selected = selected;
	}
}
