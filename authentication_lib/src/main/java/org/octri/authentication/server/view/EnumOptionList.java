package org.octri.authentication.server.view;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/**
 * Used in mustache views for pulldown lists.
 *
 * @author lawhead
 */
public class EnumOptionList {

	/**
	 * Given a collection of Enum values and the selected value, provides a list of objects that can be used directly by
	 * mustachejs for rendering.
	 *
	 * @param <T>
	 *            an enum type
	 * @param iter
	 *            collection of enum values
	 * @param selected
	 *            the currently-selected value (possibly null)
	 * @return a list with an {@link EnumSelectOption} for each enum value
	 */
	public static <T extends Enum<T> & Labelled> List<EnumSelectOption<T>> fromEnum(Iterable<T> iter, T selected) {
		return StreamSupport.stream(iter.spliterator(), false)
				.map(item -> new EnumSelectOption<T>(item, selected))
				.collect(Collectors.toList());
	}

}
