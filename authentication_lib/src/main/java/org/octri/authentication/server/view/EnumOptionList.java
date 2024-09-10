package org.octri.authentication.server.view;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/**
 * Used in mustache views for pulldown lists.
 *
 * @author lawhead
 *         TODO: move into a library
 */
public class EnumOptionList {

	/**
	 * Given a collection of Enum values and the selected value, provides a list of objects that can be used directly by
	 * mustachejs for rendering.
	 *
	 * @param iter
	 * @param selected
	 * @return
	 */
	public static <T extends Enum<T> & Labelled> List<EnumSelectOption<T>> fromEnum(Iterable<T> iter, T selected) {
		return StreamSupport.stream(iter.spliterator(), false)
				.map(item -> new EnumSelectOption<T>(item, selected))
				.collect(Collectors.toList());
	}

}
