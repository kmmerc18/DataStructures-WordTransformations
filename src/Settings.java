import gnu.getopt.Getopt;
import gnu.getopt.LongOpt;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Locale;
import java.util.Scanner;

/**
 * This class is used in conjunction with Main.java in project1 to interpret and store various characteristics
 * entered by a user into the command line including an ArrayList of strings known as a dictionary,
 * an ArrayList of Strings containing types of transformations that can be applied in another method to
 * terms in the dictionary, two words (startWord and endWord) that are members of that dictionary, what the output
 * format for the final product will be (represented by a String), how far the user wants the program to run
 * (the checkpoint represented by a String), and the order that words are processed (dequeType represented by a String).
 */
public class Settings {
    // instance variables
    private String startWord;   // the word Letterman starts with
    private String endWord;     // the word Letterman tries to transform startWord into
    private String outputType;  // the type of output the program gives at the end ('W' word format or 'M' modification format)
    private String dequeType;   // the order dictionary Strings are assessed: as a queue 'q' or a stack 's'
    private String checkpoint;  // the user-specified checkpoint (used in Main to determine what methods to run on the user input)
    private int startEntry;
    private int endEntry;
    private final ArrayList<dictEntry> dictionary; // the words Letterman can create in the path from startWord to endWord
    private ArrayList<Boolean> used;    // specifies if a given dictEntry has been used in a morph in Main

    // the actions Letterman can use to transmute one word into another
    private boolean swapMode;
    private boolean lengthMode;
    private boolean changeMode;

    // used by getOpt to tell us which flag is being processed
    private int choice;
    private LinkedList<Integer> path;
    private String solution;

    // specify all of the long options for this program
    // these are flags the user picks from to set the above instance variables
    LongOpt[] longOptions = {
            new LongOpt("stack", LongOpt.NO_ARGUMENT, null, 's'),
            new LongOpt("queue", LongOpt.NO_ARGUMENT, null, 'q'),
            new LongOpt("change", LongOpt.NO_ARGUMENT, null, 'c'),
            new LongOpt("swap", LongOpt.NO_ARGUMENT, null, 'p'),
            new LongOpt("length", LongOpt.NO_ARGUMENT, null, 'l'),
            new LongOpt("output", LongOpt.REQUIRED_ARGUMENT, null, 'o'),
            new LongOpt("begin", LongOpt.REQUIRED_ARGUMENT, null, 'b'),
            new LongOpt("end", LongOpt.REQUIRED_ARGUMENT, null, 'e'),
            new LongOpt("help", LongOpt.NO_ARGUMENT, null, 'h'),
            new LongOpt("checkpoint1", LongOpt.NO_ARGUMENT, null, 'x'),
            new LongOpt("checkpoint2", LongOpt.NO_ARGUMENT, null, 'y')
    };

    /**
     * Settings takes the command line arguments when Main.java is run, as well
     * as the String contents of each line in the txt file passed to main in the format
     * of an ArrayList
     * @param args String[] of the command line arguments when Main.java is run
     * //@param words ArrayList of Strings, each index a line from the txt file passed in from main in Main.java
     **/
    // constructor
    public Settings(String[] args) {

        // initialize instance variables to empty types
        this.changeMode = false;
        this.lengthMode = false;
        this.swapMode = false;
        this.dictionary = new ArrayList<dictEntry>();
        this.startWord = "";
        this.startEntry = -1;
        this.endEntry = -1;
        this.endWord = "";
        this.outputType = "";
        this.dequeType = "";
        this.checkpoint = "";
        this.used = new ArrayList<>();
        this.solution = "No solution";

        // create the object that processes command line arguments
        Getopt g = new Getopt("Project1", args, "b:ce:hlo:pqsxy", this.longOptions);
        g.setOpterr(true);      // prints err messages for us when we make a mistake

        // extract the individual dictionary terms from System.in, then add them to this.dictionary
        writeDictionary();


        /* for (int t = 0; t < this.dictionary.size(); t++) {
            used.add(false);
        } */

        // process one command line argument at a time until there are none left
        // g.getopt() returns an int representing the short flag or -1 if we are done
        while ((this.choice = g.getopt()) != -1) {
            // we need to process the command line argument stored in choice
            switch (this.choice) {
                // set the beginning term to be the String following the "-b" flag
                case 'b':
                    // check if beginning term is already set
                    if (!this.startWord.isBlank()) {
                        System.err.println("Error: beginning term already set to " + this.startWord);
                        System.exit(1);
                    }
                    // get the term the user wants to set startWord to
                    String start = g.getOptarg();
                    // check if the term is in the dictionary
                    for (dictEntry e : this.dictionary) {
                        if (e.getWord().equals(start)) {
                            this.startWord = start;
                            break;
                        }
                    }
                    if(this.startWord.isBlank()) {
                        System.err.println("Error: -b argument " + start + " not in dictionary");
                        System.exit(1);
                    }
                    break;

                // add the String "change" to the actions ArrayList
                case 'c':
                    // check to see if another of the same flag has been processed already
                    if (this.changeMode) {
                        // no need to stop the program for this error; notify the user of their repeat choice
                        System.err.println("Change mode already accepted; program will continue");
                        break;
                    }
                    // if it is not already in actions, add "change" to the ArrayList actions
                    this.changeMode = true;
                    break;

                // set the final term to be the String following the "-e" flag
                case 'e':
                    // check if end term is already set
                    if (!this.endWord.isBlank()) {
                        System.err.println("Error: end term already set to" + this.endWord);
                        System.exit(1);
                    }
                    // put the user's choice of final term into a temporary variable
                    String end = g.getOptarg();
                    // check if the desired end term is in the dictionary 'words'
                    for (dictEntry e : this.dictionary) {
                        if (e.getWord().equals(end)) {
                            this.endWord = e.getWord();
                            break;
                        }
                    }
                    if(this.endWord.isBlank()) {
                        System.err.println("Error: -e argument " + end + " not in dictionary");
                        System.exit(1);
                    }
                    break;

                // print an explanation of the program for the user (see printHelp() method)
                case 'h':
                    printHelp();
                    System.exit(0);

                // add the String "length" to the actions ArrayList
                case 'l':
                    // check to see if another of the same flag was already processed
                    if (this.lengthMode) {
                        // no need to stop the program for this error; notify the user of their repeat choice
                        System.err.println("Length mode already accepted; program will continue");
                        break;
                    }
                    // if not already there, add "length" to the actions ArrayList
                    this.lengthMode = true;
                    break;

                // specify the final output type as either 'W' for word format or 'M' for modification format
                case 'o':
                    // check to see if the format has already been specified
                    if (!this.outputType.isBlank()) {
                        System.err.println("Error: output type already set to " + this.outputType);
                        System.exit(1);
                    }
                    // check to see if the user's choice of format is valid ('W' or 'M')
                    String output = g.getOptarg();
                    if (!output.equals("W") && !output.equals("M")) {
                        System.err.println("Error: invalid output type");
                        System.exit(1);
                    }
                    // if not already set, set the output format to the user's choice
                    this.outputType = output;
                    break;

                // add String "swap" to the actions ArrayList
                case 'p':
                    // check to see if another of the same flag was already processed
                    if (this.swapMode) {
                        // no need to stop the program for this error; notify the user of their repeat choice
                        System.err.println("Swap mode already accepted; program will continue");
                        break;
                    }
                    // if not already in the ArrayList, add "swap" to actions
                    this.swapMode = true;
                    break;

                // specify the order with which terms will be processed as 'q' for queue
                case 'q':
                    // check to see if the processing order has already been set
                    if(this.dequeType.equals("q")) {
                        System.err.println("Queue mode already accepted; program will continue");
                        break;
                    } else if(!this.dequeType.isBlank()){
                        System.err.println("Error: deque type already processed");
                        System.exit(1);
                    }
                    // if not already set, make the deque type the String "queue"
                    this.dequeType = "queue";
                    break;

                // specify the order with which terms will be processed as 's' for stack
                case 's':
                    // check to see if the processing order has already been set
                    if(this.dequeType.equals("s")) {
                        System.err.println("Stack mode already accepted; program will continue");
                        break;
                    } else if(!this.dequeType.isBlank()) {
                        System.err.println("Error: deque type already processed");
                        System.exit(1);
                    }
                    // if not already set, make the deque type the String "stack"
                    this.dequeType = "stack";
                    break;

                // set the user preference for what checkpoint to run the program to: in this case, checkpoint 1
                case 'x':
                    // check to see if the checkpoint has already been set
                    if (!this.checkpoint.isBlank()) {
                        System.err.println("Checkpoint already established; program will continue");
                        break;
                    }
                    // if not already set, make the checkpoint preference "x" for checkpoint 1
                    this.checkpoint = "x";
                    break;

                // set the user preference for what checkpoint to run the program to: in this case, checkpoint 2
                case 'y':
                    // check to see if the checkpoint has already been set
                    if (!this.checkpoint.isBlank()) {
                        System.err.println("Checkpoint already established; program will continue");
                        break;
                    }
                    // if not already set, make the checkpoint preference "y" for checkpoint 2
                    this.checkpoint = "y";
                    break;

                // if the user attempts to enter a flag not handled by a case above, alert them of an error and exit program
                default:
                    System.err.println("Error: invalid option");
                    System.exit(1);
            }
        }


        // alert the user of an error if they do not specify a deque type, start word, end word, or any actions and exit program
        if (this.dequeType.isBlank() || this.startWord.isBlank() || this.endWord.isBlank() || !(this.swapMode || this.changeMode || this.lengthMode)) {
            System.err.println("Error: not enough specifications");
            System.exit(1);
        }

        // if the user does not specify an output type, as a default set it to "W"
        if (this.outputType.isBlank()) {
            this.outputType = "W";
        }

        // if the startWord and the endWord are different lengths, and the user does not put length in the actions
        // ArrayList, it is not possible for the program to transform the startWord into the endWord using the specified
        // actions; alert the user of this and exit the program
        if(startWord.length() != endWord.length() && !this.lengthMode) {
            System.err.println("Error: impossible path from start word to end word");
            System.exit(1);
        }
    }

    // instance methods
// prints out useful information about how to use Main.java and inputs it can take
    public static void printHelp() {
        System.out.println("Usage: java [options] main [-s], [-q], [-c], [-p], [-l], [-o W|M], [-b]<word>, [-e<word>], [-h], [-x], [-y]");
        System.out.println("This program takes either a simple or complex dictionary, and finds a path from the beginning " +
                "word (-b) to the end word (-e) through the user-selected morph modes (see below)\n ●--stack, -s: If this flag is set, use the stack-based routing scheme.\n" +
                " ●--queue, -q: If this flag is set, use the queue-based routing scheme.\n" +
                " ●--change, -c: If this flag is set, Letterman is allowed to change one\n" +
                "        letter into another.\n" +
                " ●--swap, -p: If this flag is set, Letterman is allowed to\n" +
                "        swap any two adjacent characters.\n" +
                " ●--length, -l: If this flag is set, Letterman is allowed to modify\n" +
                "        the length of a word, by inserting or deleting a single letter.\n" +
                " ●--output (W|M),  -o (W|M): Indicates the output file format by\n" +
                "        following the flag with a W(word format) or M (modification format).\n" +
                "         If the --output option is not specified, default to word output format (W).\n" +
                "         If --output is specified on the command line, the argument(either W or M)\n" +
                "         to it is required. \n" +
                " ●--begin <word>, -b <word>: This specifies the word that Letterman starts with.\n" +
                "        This flag must be specified on the command line, and when it is specified a word\n" +
                "        must follow it.\n" +
                " ●--end <word>, -e <word>: This specifies the word that Letterman must reach.\n" +
                "        This flag must be specified on the command line, and when it is\n" +
                "        specified a word must follow it.\n" +
                " ●--help, -h: If this switch is set, the program should print a brief help message\n" +
                "        which describes what the program does and what each of the flags are.\n" +
                "        The program should then System.exit(0) or return from main().\n" +
                " ●--checkpoint1, -x: stop the program after completing checkpoint 1 (see Grading)\n" +
                " ●--checkpoint2, -y: stop the program after completing checkpoint 2 (see Grading)");
    }

    // getters and setters that return the value of each instance variable in a Settings object
    public String getStart() {
        return this.startWord;
    }
    public int getStartEntry() {
        return this.startEntry;
    }
    public int getEndEntry() {
        return this.endEntry;
    }
    public void setStartEntry(int i) {
        this.startEntry = i;
    }
    public void setEndEntry(int i) {
        this.endEntry = i;
    }
    public String getEnd() {
        return this.endWord;
    }
    public String getOutputType() {
        return this.outputType;
    }
    public String getDequeType() {
        return this.dequeType;
    }
    public String getCheckpoint() {
        return this.checkpoint;
    }
    public ArrayList<dictEntry> getDictionary() {
        return this.dictionary;
    }
    public Boolean getLengthMode() {
        return this.lengthMode;
    }
    public Boolean getSwapMode() {
        return this.swapMode;
    }
    public Boolean getChangeMode() {
        return this.changeMode;
    }
    public String getSolution() {return this.solution;}
    public void setSolution(String s) {this.solution = s;}

    // method that transforms the ArrayList of lines from the txt file passed in main in Main.java
// into an ArrayList of the actually words that Letterman can use
    public void writeDictionary() {
        // process command line arguments to get dictionary words
        Scanner in = new Scanner(v
        // add each line from the text file to the words ArrayList
        String dictionaryType = in.nextLine();
        String dictionaryLength = in.nextLine();
        // the first line in the file of words is the complexity, 'S' for simple or 'C' for complex
        // the second line describes how many words are in the list (for complex dictionaries, this is prior to
        // simplifying the terms)
        if (dictionaryType.equals("S")) {
            while (in.hasNextLine()) {
                String tempTerm = in.nextLine();
                // check to see if the line is a comment
                if (!tempTerm.contains("/")) {
                    // if the line is not a comment, we are guaranteed it is a word; add the word to dictionary
                    this.dictionary.add(new dictEntry(tempTerm));
                }
            }
            // check to see if the number of words added to the dictionary matches the number
            // we were told to expect by the second line of the text file
            if (this.dictionary.size() != Integer.parseInt(dictionaryLength)) {
                System.err.println("Error: number of words in dictionary does not match source file");
                System.exit(1);
            }
        } else if (dictionaryType.equals("C")) {
            // if the dictionary type is complex, use the complexDictionary method to decipher it
            //complexDictionary(terms, dict);
            // this count will be used to compare to the number of terms we expect to parse (terms.get(1))
            // at the end of the parsing
            int words = 0;

            // the terms at index 0 and 1 are the type and size of the dictionary;
            // cycle through the remaining lines to pull out the words we want to add to the dictionary
            while (in.hasNext()) {
                // w is the String in the line we are looking at
                String tempTerm = in.nextLine();

                // check to ensure the line is not a comment
                if (!tempTerm.contains("/")) {
                    // if the word includes an ampersand, then both the word and its reverse
                    // are added to the dictionary
                    if (tempTerm.contains("&")) {
                        // increment the valid word counter
                        words++;
                        // convert the term to an array of characters
                        char[] term = tempTerm.toCharArray();
                        // make a String out of all the characters except the last (the ampersand) and add it to the dictionary
                        this.dictionary.add(new dictEntry(String.copyValueOf(term, 0, tempTerm.length() - 1)));
                        // create a character array the same size as term (sans an index for the ampersand)
                        char[] revTerm = new char[tempTerm.length() - 1];
                        // copy the characters in term into rev term's indices in reverse order
                        for (int n = tempTerm.length() - 1; n > 0; n--) {
                            revTerm[tempTerm.length() - n - 1] = term[n - 1];
                        }
                        // make a String out of the reversed word and add it to the dictionary
                        this.dictionary.add(new dictEntry(String.copyValueOf(revTerm, 0, revTerm.length)));
                    } else if (tempTerm.contains("[")) {
                        // increment the valid word counter
                        words++;
                        // if the term includes square brackets, find where they are in the line
                        int b = tempTerm.indexOf('[');
                        int e = tempTerm.indexOf(']');
                        // convert the term to an array of characters
                        char[] term = tempTerm.toCharArray();
                        // make a String out of the characters that come before the square brackets
                        String prefix = String.copyValueOf(term, 0, b);
                        // make a String out of the characters that come after the square brackets
                        String suffix = String.copyValueOf(term, e + 1, tempTerm.length() - e - 1);

                        // for all the characters between the square brackets, concatenate the prefix,
                        // the character, and the suffix together and add the resultant String to the dictionary
                        for (int n = 0; n < (e - b - 1); n++) {
                            this.dictionary.add(new dictEntry(prefix + tempTerm.charAt(b + n + 1) + suffix));
                        }
                    } else if (tempTerm.contains("!")) {
                        // increment the valid word counter
                        words++;
                        // if the term includes a bang, find the index of it
                        int a = tempTerm.indexOf('!');
                        // convert the term to a character array
                        char[] term = tempTerm.toCharArray();
                        // create a String from all the characters up to the index two before the bang
                        String prefix = String.copyValueOf(term, 0, a - 2);

                        // create a String from the two characters preceding the bang, and those two in reverse order
                        String infix1 = String.copyValueOf(term, a - 2, 1) + String.copyValueOf(term, a - 1, 1);
                        String infix2 = String.copyValueOf(term, a - 1, 1) + String.copyValueOf(term, a - 2, 1);

                        // create a String form all the characters after the bang
                        String suffix = String.copyValueOf(term, a + 1, tempTerm.length() - (a + 1));
                        // concatenate the prefix, first infix, and suffix and add the resultant String to the dictionary
                        // repeat the process for the second infix
                        this.dictionary.add(new dictEntry(prefix + infix1 + suffix));
                        this.dictionary.add(new dictEntry(prefix + infix2 + suffix));
                    } else if (tempTerm.contains("?")) {
                        // increment the valid word counter
                        words++;
                        // if the term contains a question mark, find the index of the question mark
                        int q = tempTerm.indexOf('?');
                        // convert the term to a character array
                        char[] term = tempTerm.toCharArray();
                        // create a String from the characters preceding the question mark
                        String prefix = (String.copyValueOf(term, 0, q));
                        // create a String from the character after the question mark
                        String suffix = String.copyValueOf(term, q + 1, tempTerm.length() - (q + 1));
                        // concatenate the prefix and suffix and add the resultant string to the dictionary
                        this.dictionary.add(new dictEntry(prefix + suffix));
                        // create the same word as above, but with two of the character preceding the bang
                        this.dictionary.add(new dictEntry(prefix + tempTerm.charAt(q - 1) + suffix));
                    } else {
                        // if the term does not contain any of the above special characters
                        // add it directly to the dictionary
                        this.dictionary.add(new dictEntry(tempTerm));
                        // increment the valid word counter
                        words++;
                    }
                }
            }
            // compare the number of lines parsed from the txt file to the number expected
            // if it doesn't match, inform the user and exit the program
            if (words != Integer.parseInt(dictionaryLength)) {
                System.err.println("Error: number of words in dictionary does not match source file");
                System.exit(1);
            }
        } else {
            // if the dictionary type is undefined, let the user know and exit the program
            System.err.println("Error: Dictionary type not defined");
            System.exit(1);
        }
    }
}
