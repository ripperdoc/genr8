
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.FileNotFoundException;

import java.util.List;
import java.util.ArrayList;
import java.lang.Character;

/**
 *
 */
public class WordMain {

	/**
	 * The character that starts a commented line (that should be ignored).
	 */
	private static String COMMENT_CHAR;

	/**
	 * The maximum amount of tries to generate a word.
	 */
	private static int MAX_TRIES = 200;

	/**
	 * The character that delimits words.
	 */
	private static final String LINE_FEED = "\n";

	/**
	 * The character(s) that should replace all line feeds in the output.
	 * Is used to output different line feeds for Unix, Windows and Mac.
	 */
	private static String LINE_FEED_OUT;

	/**
	 * The level of complexity of the analysis done by the Markov Chain.
	 * Should not be larger than the minimum word length.
	 */
	private static int level;

	// Variables
	private static int wordsToGenerate;
	private static int maxWordLength;
	private static int minWordLength;
	private static boolean printAsList = true;
	private static boolean printAsNames = false;
	private static boolean lowerCaseMode = true;
	private static String inputFile;
	private static String outputFile;
	private static String configFile;

	/**
	 *
	 */
	public static void main(String[] args) {

		// Process command line arguments
		for(int i=0; i<args.length; i++) {
			if(args[i].startsWith("in=") && args[i].length() > 3) {
				inputFile = args[i].substring(3);
			} else if (args[i].startsWith("out=") && args[i].length() > 4) {
				outputFile = args[i].substring(4);
			} else if (args[i].startsWith("cfg=") && args[i].length() > 4) {
				configFile = args[i].substring(4);
			}
		}

		// If any of the arguments were null, error and exit
		if(inputFile == null || outputFile == null || configFile == null) {
			System.out.println("Usage: java WordMain in=filname out=filename cfg=filename");
			System.exit(0);
		}

		// Read configuration file
		try {
			BufferedReader cfgReader = new BufferedReader(new FileReader(configFile));

			String line;

			while((line = cfgReader.readLine()) != null) {
				if(line.startsWith("Complexity level            =[")) {
					level = Integer.parseInt(line.substring(30,line.lastIndexOf("]")));
				} else if(line.startsWith("Number of words to generate =[")) {
					wordsToGenerate = Integer.parseInt(line.substring(30,line.lastIndexOf("]")));
				} else if(line.startsWith("Maximum allowed word length =[")) {
					maxWordLength = Integer.parseInt(line.substring(30,line.lastIndexOf("]")));
				} else if(line.startsWith("Minimum allowed word length =[")) {
					minWordLength = Integer.parseInt(line.substring(30,line.lastIndexOf("]")));
				} else if(line.startsWith("Print as list               =[") ){
					String value = line.substring(30,line.lastIndexOf("]"));
					if(value.equals("1")) {
						printAsList = true;
					} else if(value.equals("0")) {
						printAsList = false;
					}
				} else if(line.startsWith("Print as names              =[")) {
					String value = line.substring(30,line.lastIndexOf("]"));
					if(value.equals("1")) {
						printAsNames = true;
					} else if(value.equals("0")) {
						printAsNames = false;
					}
				} else if(line.startsWith("Make all text lower case    =[")) {
					String value = line.substring(30,line.lastIndexOf("]"));
					if(value.equals("1")) {
						lowerCaseMode = true;
					} else if(value.equals("0")) {
						lowerCaseMode = false;
					}
				} else if(line.startsWith("Comment character           =[")) {
					// Should only be one character long
					COMMENT_CHAR = line.substring(30,31);
				} else if(line.startsWith("Line feed style             =[")) {
					String value = line.substring(30,31);
					if(value.equals("U")) {
						LINE_FEED_OUT = "\n";
					} else if(value.equals("W")) {
						LINE_FEED_OUT = "\r\n";
					} else if(value.equals("M")) {
						LINE_FEED_OUT = "\r";
					}
				}
			}
			cfgReader.close();
			if(level < 1 || wordsToGenerate < 1 || maxWordLength < 1 ||
					minWordLength < 1) {
				System.out.println("Incorrect config file; zero or negative values.");
				System.exit(0);
			}
		// File not found
		} catch (FileNotFoundException fnfe) {
			System.out.println("Configuration file not found at specified location.");
			System.exit(0);
		// Error reading file
		} catch (IOException ioe) {
			System.out.println("Configuration file incorrect or unreadable.");
			System.exit(0);
		// Could not parse the string to a number
		} catch (NumberFormatException nfe) {
			System.out.println("Incorrect config file; could not parse the numbers.");
			System.exit(0);
		} catch (IndexOutOfBoundsException ioobe) {
			System.out.println("Incorrect config file; configuration lines altered.");
			System.exit(0);
		}

		/*
		The appearence of a config file.
		012345678901234567890123456789
		Complexity level            =[]
		Number of words to generate =[]
		Maximum allowed word length =[]
		Minimum allowed word length =[]
		Print as list               =[]
		Print as names              =[]
		Make all text lower case    =[]
		Comment character           =[]
		Line feed style             =[]
		*/

		List charList = new ArrayList();
		List whitespaces = new ArrayList();

		try {

			BufferedReader reader = new BufferedReader(new FileReader(inputFile));

			int rd;
			boolean whitespace;
			boolean comment;

			// Begin the list with a line feed and therefore set whitespace to true.
			charList.add(LINE_FEED);
			whitespace = true;
			comment = false;

			while( ((rd = reader.read()) != -1) ) {
				// If it's a line feed or carriage return
				if(rd == 0x000A || rd == 0x000D) {
					if(!whitespace) {
						charList.add(LINE_FEED);
					}
					whitespaces.add(LINE_FEED);
					whitespace = true;
					comment = false;
				// If it's a blank space
				} else if (rd == 0x0020) {
					if(!whitespace) {
						charList.add(" ");
					}
					whitespaces.add(" ");
					whitespace = true;
				// If it's a comment character
				} else if (rd == (int)COMMENT_CHAR.charAt(0)) {
					whitespace = false;
					comment = true;
				// If it's an allowed character (above the 20 first ASCII:s)
				} else if (rd > 0x0020) {
					String character = "" + (char)rd;
					if(lowerCaseMode) {
						character = character.toLowerCase();
					}
					charList.add(character);
					whitespace = false;
				// Else ignore
				} else {
					whitespace = false;
				}
			}
			reader.close();

		} catch (FileNotFoundException fnfe) {
			System.out.println("Input file not found at specified location.");
			System.exit(0);
		} catch (IOException ioe) {
			System.out.println("Input file incorrect or unreadable.");
			System.exit(0);
		}

		if(charList.size() < 2) {
			throw new IllegalArgumentException("Incorrect input file");
		}

		// If the last character isn't a line feed, make it so.
		if(!charList.get(charList.size()-1).equals(LINE_FEED)) {
			charList.add(LINE_FEED);
		}

		WordGenerator wordGen = new WordGenerator(charList, level, whitespaces);

		try {

			PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter(outputFile)));

			// Sets the "last" word to a LF to simulate the start of a sentence.
			String lastWord = LINE_FEED;

			for(int i = 1; i <= wordsToGenerate; i++) {
				int tries = 0;
				String word = null;
				do {
					String seed;
					if(printAsList) {
						// Choose one of the whitespace characters randomly
						seed = (String)whitespaces.get((int)Math.round(Math.random()*(whitespaces.size()-1)));
					} else {
						// If the amount of chars in lastWord is fewer than the level, trim
						// the offset to the lastWords length, to avoid exception.
						int offset = level;
						if(offset > lastWord.length()) {
							offset = lastWord.length();
						}

						seed = lastWord.substring(lastWord.length()-offset,lastWord.length());
					}
					// Generate the word with the given seed and set it to generate an ending
					// whitespace if printAsList = false.
					word = wordGen.generateWord(seed,!printAsList);
					tries++;
				} while((word == null || word.length() < minWordLength ||
						word.length() > maxWordLength) && tries < MAX_TRIES);
				if(tries >= MAX_TRIES) {
					i++;
					System.out.println("Could not generate word #" + i +
							", try a lower level.");
				} else {
					if(printAsNames) {
						word = word.substring(0,1).toUpperCase() + word.substring(1);
					}
					if(printAsList) {
						word += LINE_FEED;
					}
					lastWord = word;
					// Replaces all line feeds with the correct output line feed, if they are different.
					if(!LINE_FEED.equals(LINE_FEED_OUT)) {
						word = word.replaceAll(LINE_FEED,LINE_FEED_OUT);
					}
					writer.print(word);

				}
			}

			writer.close();
		} catch (IOException ioe) {
			System.out.println("Could not write to output file. Check file permissions.");
			System.exit(0);
		}
		System.out.println();
		System.out.println("Input file: " + inputFile + ", " + charList.size() + " characters read.");
		System.out.println("Output file: " + outputFile + ", " + wordsToGenerate + " words written.");
		System.out.println("Configuration file: " + configFile);
		System.out.println("Complexity level: " + level);
		System.out.println("Maxmimum word length: " + maxWordLength);
		System.out.println("Minimum word length: " + minWordLength);
		System.out.println("Print as list: " + printAsList);
		System.out.println("Print as names: " + printAsNames);
		System.out.println("Make all text lower case: " + lowerCaseMode);
		System.out.println("Comment character: " + COMMENT_CHAR);
		System.out.println();
		System.out.println("Thank you for using WordGenerator 1.00b.");
		System.out.println("(C) Martin \"RipperDoc\" Frojdh, ripperdoc@telia.com");

		try {
			PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter("debug.txt")));
			writer.print(wordGen.getAnalyzeInfo());
			writer.close();
		} catch (IOException ioe) {
			System.out.println("Could not write to debug file. Check file permissions.");
			System.exit(0);
		}
	}
}