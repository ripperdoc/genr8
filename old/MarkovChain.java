import java.io.FileWriter;
import java.io.BufferedWriter;
import java.io.PrintWriter;
import java.io.IOException;

import java.util.List;
import java.util.Set;
import java.util.Iterator;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

/**
 * The MarkovChain is the class that represents a simple mathematical markov chain.
 * The class takes an input list (any ordered sequence of Objects) and analyzes it
 * according to the given Markov level. The level defines how complex the analyze
 * will be. The level is the amount of following elements that would affect the
 * prediction of the next element in the list. For example, when having the string
 * "banan" and the prediction is of the character following the final "n". If the
 * Markov level is three the string "nan" will be considered when predicting the
 * next element. If the level would have been 1, only the "n" would be used for the
 * prediction, but it is easily understood that it's easier to predict the following
 * character for "nan" than for "n", because "nan" has less possible followers than "n".
 *
 * The class can be used for predicting words or characters, or for predicting numbers
 * in a squence of numbers. Bare in mind that the larger the input the better prediction,
 * and also that the choosing of the Markov level is vital to the result. If the level
 * is high, the processing will be slow, but the prediction will be better. But if the
 * level is too high related to the input size, the prediction wouldn't be useful.
 * A low level makes faster processing but not as good prediction capabilites.
 */
public class MarkovChain {

	/**
	 * A list of objects that is the input for the MarkovChain. The objects must
	 * implement equals().
	 */
	private List inputList;

	/**
	 * The delimiter between parts of the chain. If null it isn't used.
	 */
	private Object delimiter = (Object)" ";

	/**
	 * Defines if the Markov chain should loop around.
	 */
	private boolean loopAround;

	/**
	 * The level of the MarkovChain. Level 1 means that only the last element
	 * in a list affects the probability of the prediction of the next element.
	 * A level of three would mean that the three last elements of the list would
	 * affect the probability of the next element.
	 */
	private int markovLevel;

	/**
	 * The MarkovChain as a HashMap. Contains a key for each combination of elements
	 * of the maximum length of inputLevel found in the input list and a value in
	 * form of another HashMap that holds statistics of each possible object that
	 * follows the current object.
	 */
	private Map markovChain;

	/**
	 *
	 * @param inputList a List consisting of the input to the MarkovChain.
	 * @param markovLevel the level of the MarkovChain (higher level means better
	 * prediction).
	 */
	public MarkovChain(List inputList, int markovLevel) {
		this.inputList = inputList;
		this.markovLevel = markovLevel;
		loopAround = true;
		markovChain = new HashMap();
		processChain();
	}

	/**
	 * Processes the chain by looping through the whole inputList and registering
	 * all the next following elements for each combination of elements of the
	 * of a size up to or equal to the markovLevel.
	 */
	private void processChain() {
		List objectCombo = new ArrayList();
		// Loops through the whole inputList and adds it to the MarkovChain
		for(int i = 0; i < inputList.size() -1; i++) {

			Object element = inputList.get(i);

			if(i < markovLevel) {
				// If still under the level, just build up the combo
				objectCombo.add(element);
			} else {
				// Add one, remove the first
				objectCombo.remove(0);
				objectCombo.add(element);
			}

			int size = objectCombo.size();
			List tmpList = new ArrayList(objectCombo);
			for(int j = 0; j < size; j++) {
				addToMap(tmpList.subList(j,size),i);
			}

		}
	}

	private void addToMap(List objectCombo, int i) {
		if(objectCombo.size() <= 0) {
			throw new NullPointerException("Combination list was empty");
		} else {
			int nextElementIndex = i + 1;
			List tmpList;

			if(markovChain.containsKey(objectCombo)) {
				// Gets the old list to update it
				tmpList = (List)markovChain.get(objectCombo);
			} else {
				// Creates a new list
				tmpList = new ArrayList();
			}

			// Adds the next element from the input list.
			tmpList.add(inputList.get(nextElementIndex));
			// Re-adds the list to the map
			markovChain.put(objectCombo, tmpList);
		}
	}


	/**
	 * Returns a prediction of the next element by making a random draw
	 * amongst the registered follower objects of the specified seed.
	 *
	 * @param seed a list of of objects that the next element
	 * will be predicted from.
	 * @return an object representing the random based prediction of the next element.
	 */
	public Object predictNext(List seed) {
		if(markovChain.containsKey(seed)) {
			List possibleNextElements = (List)markovChain.get(seed);
			int randomIndex = (int)Math.round(Math.random()*(possibleNextElements.size()-1));
			return possibleNextElements.get(randomIndex);
		} else {
			return null;
		}
	}

	public String toString() {
		TreeMap sortedKeys = new TreeMap();
		List sortedList = new ArrayList();
		Set keySet = markovChain.keySet();
		Iterator it = keySet.iterator();
		while(it.hasNext()) {
			List next = (List)it.next();
			sortedKeys.put(MarkovChain.listToString(next), next);
		}
		String returnString = "";
		Set sortedKeyStrings = sortedKeys.keySet();
		Iterator it2 = sortedKeyStrings.iterator();
		while(it2.hasNext()) {
			String keyString = (String)it2.next();
			returnString += keyString + ":";
			returnString += MarkovChain.listToString((List)markovChain.get(sortedKeys.get(keyString)));
			returnString += "\n";
		}
		return returnString;
	}

	/**
	 * Converts a String to a List (ArrayList).
	 *
	 * @param string the string to be converted,
	 * @return the string as a list, where each character of the given string is
	 * represented as a String in the list.
	 */
	public static List stringToList(String string) {
		List returnList = new ArrayList();
		for(int i =0;i<string.length();i++) {
			returnList.add(string.substring(i,i+1));
		}
		return returnList;
	}

	/**
	 * Converts a List to a String.
	 *
	 * @param list the list to be converted,
	 * @return the list as a String, where each element of the given list is
	 * represented as a part of the returned String.
	 */
	public static String listToString(List list) {
		String returnString = "";
		for(int i =0;i<list.size();i++) {
			String currentChar = (String)list.get(i);
			if(currentChar.equals("\n")) {
				currentChar = "\\n";
			}
			returnString += currentChar;
		}
		return returnString;
	}

	/*

	private void printChain() {

		try {



			Set keySet = markovChain.keySet();
			Iterator it = keySet.iterator();
			while(it.hasNext()) {
				String key = (String)it.next();
				writer.print(key);
				writer.print(": ");

				List values = (List)markovChain.get(key);
				for(int i=0;i<values.size();i++) {
					writer.print(values.get(i));
					writer.print(",");
				}
				writer.println();
			}
			writer.close();

		} catch (IOException ioe) {
			System.err.println("File not found");
			System.exit(0);
		}
	}
	*/
}
