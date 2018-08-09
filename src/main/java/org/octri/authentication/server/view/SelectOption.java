package org.octri.authentication.server.view;

import java.util.Collection;

import org.octri.authentication.server.security.entity.AbstractEntity;

/**
 * Used for UI select inputs. Wraps the choice along with its selected status.
 * 
 * @author lawhead
 *
 * @param <T>
 */
public class SelectOption<T extends AbstractEntity & Labelled> {

	private T choice;
	private Long value;
	private String label;
	private Boolean selected;

	/**
	 * Constructor
	 * 
	 * @param choice
	 */
	public SelectOption(T choice) {
		super();
		this.choice = choice;
		this.label = choice.getLabel();

		this.selected = false;
	}

	/**
	 * Constructor
	 * 
	 * @param choice
	 *            - lookup list item
	 * @param selected
	 *            - The selected item; may be null
	 */
	public SelectOption(T choice, T selected) {
		super();
		this.choice = choice;
		this.label = choice.getLabel();

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
		super();
		this.choice = choice;
		this.label = choice.getLabel();

		this.selected = selected != null && selected.contains(choice);
	}

	public T getChoice() {
		return choice;
	}

	public Long getId() {
		return choice.getId();
	}

	public void setChoice(T choice) {
		this.choice = choice;
	}

	public Long getValue() {
		return value;
	}

	public void setValue(Long value) {
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
