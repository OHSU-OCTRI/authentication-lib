package org.octri.authentication.server.view;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.StreamSupport;

/**
 * Used for rendering mustache templates. Helper functions for creating a list of select input options.
 * 
 * @author lawhead
 * @param <T>
 *
 */
public class OptionList<T> {

	/**
	 * Given a Repository search result of lookups and the selected lookup item, provides a list of objects that can be
	 * used directly by mustachejs for rendering.
	 * 
	 * @param iter
	 * @param selected
	 * @return
	 */
	public static <T extends Identified & Labelled> List<EntitySelectOption<T>> fromSearch(Iterable<T> iter,
			T selected) {
		return StreamSupport.stream(iter.spliterator(), false)
				.map(area -> new EntitySelectOption<T>(area, selected))
				.collect(Collectors.toList());
	}

	/**
	 * Used for multi-selects. Given a Repository search result of lookups and a list of selected lookup, provides a
	 * list of objects that can be used directly by mustachejs for rendering.
	 * 
	 * @param iter
	 * @param selected
	 * @return
	 */
	public static <T extends Identified & Labelled> List<EntitySelectOption<T>> multiFromSearch(Iterable<T> iter,
			Collection<T> selected) {
		return StreamSupport.stream(iter.spliterator(), false)
				.map(area -> new EntitySelectOption<T>(area, selected))
				.collect(Collectors.toList());
	}

	/**
	 * Generates a list of integers in the given range from which to choose.
	 * 
	 * @param start
	 * @param end
	 * @param selected
	 * @return
	 */
	public static List<SelectOption<Integer>> forRange(Integer start, Integer end, Integer selected) {
		return IntStream.rangeClosed(start, end)
				.mapToObj(i -> new SelectOption<Integer>(new Integer(i), selected))
				.collect(Collectors.toList());
	}
	
}
