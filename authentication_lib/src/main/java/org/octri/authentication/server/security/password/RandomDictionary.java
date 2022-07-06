package org.octri.authentication.server.security.password;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

import org.springframework.util.Assert;

/**
 * Dictionary used to find random words.
 *
 * @author lawhead
 *
 */
public class RandomDictionary {

	private Map<Integer, List<String>> wordsBySize;
	// Attempts to find a word by a random size in the given range.
	private Integer maxAttempts = 10;

	/**
	 * Create a new dictionary from the given wordlist.
	 *
	 * @param wordList
	 */
	public RandomDictionary(List<String> wordList) {
		Assert.notNull(wordList, "Wordlist must not be null");
		Assert.isTrue(!wordList.isEmpty(), "Word list must contain words");
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

		// Try a given number of times to find a word using a random size in the provided range.
		int wordSize = ThreadLocalRandom.current().nextInt(minSize, maxSize + 1);
		int attempt = 0;
		while (!wordsBySize.containsKey(wordSize) && attempt < maxAttempts) {
			attempt += 1;
			wordSize = ThreadLocalRandom.current().nextInt(minSize, maxSize + 1);
		}

		if (wordsBySize.containsKey(wordSize)) {
			List<String> words = wordsBySize.get(wordSize);
			int randIndex = ThreadLocalRandom.current().nextInt(0, words.size());
			return words.get(randIndex);
		} else {
			throw new RuntimeException("Words were not found in the given range (" + minSize + " to " + maxSize
					+ "). Please adjust the configured range or add a new dictionary");
		}
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

	public Integer getMaxAttempts() {
		return maxAttempts;
	}

	public void setMaxAttempts(Integer attempts) {
		this.maxAttempts = attempts;
	}
}
