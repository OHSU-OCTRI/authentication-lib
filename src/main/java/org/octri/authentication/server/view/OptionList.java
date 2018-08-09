package org.octri.authentication.server.view;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.octri.authentication.server.security.entity.AbstractEntity;

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
	public static <T extends AbstractEntity & Labelled> List<SelectOption<T>> fromSearch(Iterable<T> iter, T selected) {
		return StreamSupport.stream(iter.spliterator(), false)
				.map(area -> new SelectOption<>(area, selected))
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
	public static <T extends AbstractEntity & Labelled> List<SelectOption<T>> multiFromSearch(Iterable<T> iter,
			Collection<T> selected) {
		return StreamSupport.stream(iter.spliterator(), false)
				.map(area -> new SelectOption<>(area, selected))
				.collect(Collectors.toList());
	}
}
