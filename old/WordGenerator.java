import java.util.List;
import java.util.ArrayList;
import java.lang.Character;

/**
 *
 */
public class WordGenerator {

	/**
	 * The level of complexity of the analysis done by the Markov Chain.
	 * Should not be larger than the minimum word length.
	 */
	private int level;

	/**
	 * The Markov chain used for the word generation.
	 */
	private MarkovChain markovChain;

	/**
	 * A list consisting of one-character Strings that is defined as word separators.
	 */
	private List wordSeparators;

	/**
	 * Constructs a new WordGenerator.
	 *
	 * @param input a List of one-character Strings that should be used to generate words
	 * from.
	 * @param level the level of complexity of the generation. A value from 1 to n.
	 * @param wordSeparators a list of one-character Strings that is meant to separate
	 * words.
	 */
	public WordGenerator(List input, int level, List wordSeparators) {
		if(level < 1) {
			throw new IllegalArgumentException("Level below 1 given; incorrect");
		}
		this.level = level;
		this.wordSeparators = wordSeparators;
		markovChain = new MarkovChain(input,level);
	}

	/**
	 * Generates a word using the Markov chain created from the WordGenerators given input
	 * list. The generation is based on a seed that is primarily supposed to be a word
	 * separator. The method returns a word with a word length given indirectly (but
	 * randomly) from the word lengths of the input. If no word can be generated from
	 * the starting seed, or from the seed during the generation, null is returned.
	 *
	 * @param seed a String that acts as seed for the beginning of the word.
	 * @param generateWhitespace a boolean that states if the method should append
	 * a whitespace to the end of the generated word. "Whitespace" is defined from the
	 * list given to the WordGenerator constructor and the specific type of whitespace
	 * will be predicted from the Markov chain in the same way as other characters.
	 * @return a word of a random length (but proportional to word lengths in the input).
	 */
	public String generateWord(String seed, boolean generateWhitespace) {
		// Cuts the seed to be of a maximum length of level.
		if(seed.length() > level) {
			seed = seed.substring(seed.length()-level,seed.length());
		}
		String combo = seed;
		String word = "";

		while(true) {
			String nextChar = (String)markovChain.predictNext(MarkovChain.stringToList(combo));
			if(nextChar == null) {
				if(combo.length() > 1) {
					combo.substring(1);
				} else {
					return null;
				}
			} else if(wordSeparators.contains(nextChar)) {
				// If defined so, the found whitespace will be appended to the returned word.
				if(generateWhitespace) {
					word += nextChar;
				}
				return word;
			} else {
				word += nextChar;
				combo += nextChar;
				// Remove beginning of combo only if the length is above the level
				if(combo.length() > level) {
					combo = combo.substring(1);
				}
			}
		}
	}

	public String getAnalyzeInfo() {
		return markovChain.toString();
	}


}