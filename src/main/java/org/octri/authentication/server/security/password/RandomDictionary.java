package org.octri.authentication.server.security.password;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

/**
 * Dictionary used to find random words.
 * 
 * @author lawhead
 *
 */
@Component
public class RandomDictionary {

	private Map<Integer, List<String>> wordsBySize;

	/**
	 * Create a new dictionary from the given wordlist.
	 * 
	 * @param wordList
	 */
	public RandomDictionary(List<String> wordList) {
		this.initialize(wordList);
	}

	/**
	 * Get a random word from the dictionary with a length between minSize and maxSize inclusive.
	 * 
	 * @param minSize
	 * @param maxSize
	 * @return
	 */
	public String getRandom(int minSize, int maxSize) {
		int wordSize = ThreadLocalRandom.current().nextInt(minSize, maxSize + 1);

		List<String> words = wordsBySize.get(wordSize);
		// TODO: may not be any words of a given size
		int randIndex = ThreadLocalRandom.current().nextInt(0, words.size());
		return words.get(randIndex);
	}

	/**
	 * 
	 * @param size
	 * @return number of words of a given length; useful for calculating password entropy.
	 */
	public Integer wordsOfLength(int size) {
		return wordsBySize.getOrDefault(size, new ArrayList<>()).size();
	}

	/**
	 * 
	 * @param minSize
	 * @param maxSize
	 * @return number of words in a given length range (inclusive)
	 */
	public Integer wordsInRange(int minSize, int maxSize) {
		Assert.isTrue(minSize <= maxSize, "Min size must be less than or equal to max");

		Integer count = 0;
		for (int i = minSize; i <= maxSize; i++) {
			count += wordsOfLength(i);
		}
		return count;
	}

	/**
	 * Given the wordlist, initialize the underlying data structure.
	 * 
	 * @param wordList
	 */
	private void initialize(List<String> wordList) {
		wordsBySize = new HashMap<>();
		for (String word : wordList) {
			wordsBySize.putIfAbsent(word.length(), new ArrayList<String>());
			List<String> words = wordsBySize.get(word.length());
			words.add(word);
		}
	}
}
