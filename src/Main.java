import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;

public class Main {
    public static void main(String[] args) {
        // use the Settings class to determine what settings the user gives in the command line
        // and what words are contained in the dictionary for Letterman to work with
        Settings settings = new Settings(args);

        // run the methods associated with the user-specified checkpoint
        if (settings.getCheckpoint().equals("x")) {
            checkpoint1(settings);
        } else if (settings.getCheckpoint().equals("y")) {
            checkpoint2(settings);
        } else {
            finalCheckpoint(settings);
        }
    }

    // methods to allow program to operate each checkpoint
    private static void checkpoint1(Settings s) {
        // checkpoint1 prints the number of words in the dictionary
        System.out.println("Words in dictionary: " + s.getDictionary().size());
    }

    private static void checkpoint2(Settings s) {
        // checkpoint2 prints the number of words in the dictionary, whether there's a solution, and
        // how many words are checked via either the stack or queue method to find that out
        checkpoint1(s);
        dequeMethod(s);
    }

    private static void finalCheckpoint(Settings s) {
        // print the number of words in dictionary, whether there's a solution,
        // and how many words are processed and put in the deque during this process;
        checkpoint2(s);

        // if there is a solution, then we need to print the words in the morph based on the
        // user-specified output mode stored in Settings s
        if (s.getSolution().equals("Solution")) {
            // toPrint is the ArrayList of words that lead directly from the start to end words
            // which will only be assessed for the word output
            ArrayList<dictEntry> toPrint = new ArrayList<>();

            // toCheck is the ArrayList of morphs applied to lead from start to end words
            // which will only be assessed for the modification output
            ArrayList<String> toCheck = new ArrayList<>();

            // start following the path from the end word backwards, p the index of this term in the dictionary
            int p = s.getEndEntry();

            // word output
            if (s.getOutputType().equals("W") && s.getDictionary().get(p).getPrev() != null) {
                // the start word has null for previous, so assess each word last to first of the path leading to it
                while (s.getDictionary().get(p).getPrev() != null) {
                    // add the term to the path ArrayList
                    toPrint.add(s.getDictionary().get(p));
                    // reassign p to be the word that came prior to the one just assessed in the path
                    p = s.getDictionary().indexOf(s.getDictionary().get(p).getPrev());
                }
            } else {
                // modification output
                // the start word has null for previous, so assess each word last to first of the path leading to it
                while (s.getDictionary().get(p).getPrev() != null) {
                    dictEntry currentTerm = s.getDictionary().get(p);
                    String current = s.getDictionary().get(p).getWord();
                    String previous = currentTerm.getPrev().getWord();

                    // terms are related by length morph
                    if (current.length() > previous.length()) {
                        int i = 0;
                        // find the index and the character that was inserted at that index
                        while (i < previous.length()) {
                            if (current.charAt(i) != previous.charAt(i)) {
                                toCheck.add("i," + i + "," + current.charAt(i));
                                break;
                            }
                            i++;
                        }
                        // if we reached the end of the short term and there was no difference, then
                        // the insertion was the last character of the long term (current)
                        if (i == previous.length()) {
                            toCheck.add("i," + (current.length() - 1) + "," + current.charAt(current.length() - 1));
                        }
                    } else if (current.length() < previous.length()) {
                        int i = 0;
                        // find the index and the character that was inserted at that index
                        while (i < current.length()) {
                            if (current.charAt(i) != previous.charAt(i)) {
                                toCheck.add("d," + i);
                                break;
                            }
                            i++;
                        }
                        // if we reached the end of the short term and there was no difference, then
                        // the deletion was the last character of the long term
                        if (i == current.length()) {
                            toCheck.add("d," + (previous.length() - 1));
                        }

                    } else {    // terms related by either swap or change
                        for(int c = 0; c < current.length(); c++) {
                            // find the character and index where the strings differ
                            if(current.charAt(c) != previous.charAt(c)) {
                                // if swap
                                if((c+1) < current.length() && current.charAt(c+1) == previous.charAt(c) &&
                                        current.charAt(c) == previous.charAt(c+1) ) {
                                    toCheck.add("s," + c);
                                } else { // if change
                                    toCheck.add("c," + c + "," + current.charAt(c));
                                }
                                break;
                            }
                        }
                    }
                    p = s.getDictionary().indexOf(currentTerm.getPrev());
                }
            }

            // print out the results in the desired format
            if (s.getOutputType().equals("W")) {
                // add the start word to the toPrint ArrayList (this was skipped in the loop
                // to prevent a null-pointer error because the term has no previous
                toPrint.add(s.getDictionary().get(s.getStartEntry()));
                System.out.print("Words in morph: " + toPrint.size() + "\n");
                // print the terms from the toPrint ArrayList in reverse order
                for (int a = toPrint.size() - 1; a >= 0; a--) {
                    System.out.print(toPrint.get(a).getWord() + "\n");
                }
            } else {
                // add the start word to the toCheck ArrayList(this was skipped in the loop to prevent a
                // null-pointer exception because the term has no previous
                toCheck.add(s.getStart());
                System.out.print("Words in morph: " + toCheck.size() + "\n");
                System.out.print(s.getStart() + "\n");
                // print the terms from the toCheck ArrayList in reverse order
                for (int a = toCheck.size() - 2; a >= 0; a--) {
                    System.out.print(toCheck.get(a) + "\n");
                }
            }
        }
    }

    private static void dequeMethod (Settings s){
        // start the deque analysis
        Deque<Integer> deque = new ArrayDeque<>();

        // search the dictionary for the index of the start and end words
        int begin = -1;
        int finalIndex = -1;
        int z = 0;
        for (dictEntry d : s.getDictionary()) {
            if (d.getWord().equals(s.getStart())) {
                begin = z;
                s.setStartEntry(z);
            } else if (d.getWord().equals(s.getEnd())) {
                finalIndex = z;
                s.setEndEntry(z);
            }
            // if both the start and end words have been found, break the loop
            if (finalIndex >= 0 && begin >= 0) {
                break;
            }
            // increment the index of the dictionary entry being assessed
            z++;
        }

        // add the start word to the deque and mark it as used
        deque.add(begin);
        s.getDictionary().get(begin).setUsed();

        // initialize the current word being evaluated as an empty String
        dictEntry currentTerm;
        // String currentWord;
        // initialize the current term being compared to as an empty dictEntry
        dictEntry prev;

        // number of words the program has checked in the journey to finding a solution
        int checked = 1;

        // looping through all possible terms in the dictionary until we reach the end
        while (!s.getSolution().equals("Solution") && !deque.isEmpty()) {
            // take the current word off the deque from the correct area depending if in queue or stack mode
            if (s.getDequeType().equals("queue")) {
                currentTerm = s.getDictionary().get(deque.remove());
            } else {
                currentTerm = s.getDictionary().get(deque.pop());
            }
            // set the current word being taken off the deque as the prev
            prev = currentTerm;

            // loop through all the terms in the dictionary
            for (int i = 0; i < s.getDictionary().size(); i++) {
                // if the dictionary term hasn't been processed, compare it to the currentWord
                if (!s.getDictionary().get(i).getUsed() && checkSim(s, currentTerm, s.getDictionary().get(i))) {
                    // if similar, add the dictionary term to the deque
                    if (s.getDequeType().equals("queue")) {
                        deque.add(i);
                    } else {
                        deque.push(i);
                    }

                    // increment the counter for how many terms are being checked
                    checked++;
                    // set the term's previous to currentWord and mark the term as used
                    s.getDictionary().get(i).setPrev(prev);
                    s.getDictionary().get(i).setUsed();

                    // if the term added to the deque is actually the solution, set the solution
                    // this will break the loop of comparisons and make the function print and finish
                    if (i == s.getEndEntry()) {
                        s.setSolution("Solution");
                        break;
                    }
                }
            }
        }
        // print if there was a solution found, and how many words were checked to find this
        System.out.println(s.getSolution() + ", " + checked + " words checked.");
    }

    private static boolean checkSwap(dictEntry currentTerm, dictEntry nextTerm) {
        // set temporary variables for the words belonging to each dictEntry for comparison
        String currentWord = currentTerm.getWord();
        String nextWord = nextTerm.getWord();
        // if the terms are not the same length, they cannot be related by the swap morph
        if (currentWord.length() != nextWord.length()) {
            return false;
        }
        // begin a counter for how many character swaps must occur for the terms to match
        int swap = 0;

        // for each character in each term, compare the characters
        for (int i = 0; i < currentWord.length(); i++) {
            if (currentWord.charAt(i) == nextWord.charAt(i)) continue;
                // if the two arrays are similar but differ due to the necessity to swap characters between the current
                // index and the next index, increment the swap counter
            else if (currentWord.length() > (i+1) && currentWord.charAt(i) == nextWord.charAt(i + 1) && currentWord.charAt(i + 1) == nextWord.charAt(i)) {
                swap++;
                i++;
                // if the terms cannot be the same via one swap morph, they are too different; return false
                if (swap > 1) {
                    return false;
                }
            }
            // if the arrays differ but a swap cannot resolve this difference, return false
            else return false;
        }
        // if no swaps are necessary for the two terms to match, return false (they are the same word)
        return swap != 0;
    }

    private static boolean checkLength(dictEntry currentTerm, dictEntry nextTerm) {
        // set temporary variables for the words belonging to each dictEntry for comparison
        String currentWord = currentTerm.getWord();
        String nextWord = nextTerm.getWord();

        // if the two terms are too different in length, or are the same length, return false
        if((Math.abs(currentWord.length()-nextWord.length()) != 1)) {
            return false;
        }
        // n is a counter for the index being reviewed in the longer of the two words
        int n = 0;
        // change is a counter for how many differences there are between the two terms
        int change = 0;
        String longWord;
        String shortWord;

        // convert the two terms to indexed character arrays
        if(currentWord.length() > nextWord.length()) {
            longWord = currentWord;
            shortWord = nextWord;
        } else {
            shortWord = currentWord;
            longWord = nextWord;
        }
        // loop through the characters in the shorter word and compare them to those of the same index as the longer
        for(int i = 0; i < shortWord.length(); i++) {
            // if the characters don't match but longer word still has a later index, look to see if the next index
            // of the bigger term matches the same index of the smaller
            if ((longWord.charAt(n) != shortWord.charAt(i)) && ((n+1) < longWord.length()) ) {
                if (longWord.charAt(n + 1) == shortWord.charAt(i)) {
                    // if losing longTerm[n] would make the terms similar, increment the counter
                    // for the number of changes
                    change++;

                    // add the morph to the nextTerm's data. If it's longer, this is insertion. If not, deletion
                    /*if(longWord.equals(nextWord)) {
                        nextTerm.setMorph("i," + i + "," + longWord.charAt(i));
                    } else {
                        nextTerm.setMorph("d," + i);
                    }
                     */

                    // increment the counter for the index of the longTerm char to skip over the
                    // one that matches shortTerm[i]
                    n++;
                }
                // if they don't match, the terms cannot be related with the length morph
                else {
                    // reset the nextTerm morph to a blank string
                    // nextTerm.setMorph("");
                    return false;
                }
            }
            // if the two terms have an irreconcilable difference in characters, return false
            else if (longWord.charAt(n) != shortWord.charAt(i)) {
                // reset the nextTerm morph to a blank string
                // nextTerm.setMorph("");
                return false;
            }
            // if the terms cannot be related by a single length morph; return false
            if (change > 1) {
                // reset the nextTerm morph to a blank string
                // nextTerm.setMorph("");
                return false;
            }
            // increment the index of the longer term to keep up with the index of the shorter
            n++;
        }

        // if it gets here, this means that one length morph was required to make the terms the same
        return true;
    }

    private static boolean checkChange(dictEntry currentTerm, dictEntry nextTerm) {
        // set temporary variables for the words belonging to each dictEntry for comparison
        String currentWord = currentTerm.getWord();
        String nextWord = nextTerm.getWord();

        // two words can only be morphed by change to match if they are the same length
        if(currentWord.length() != nextWord.length()) {
            return false;
        }
        // create a counter for the number of characters that differ between the two words
        int wrongChar = 0;

        // loop through the letters in each word to find differences
        for(int i = 0; i < currentWord.length(); i++) {
            if(currentWord.charAt(i) != nextWord.charAt(i)) {
                wrongChar++;
                // if more than one character differs between the words, return false
                if(wrongChar > 1) {
                    return false;
                }
            }
        }
        return true;
    }

    private static boolean checkSim(Settings set, dictEntry currentTerm, dictEntry previousTerm) {
        // check to see if currentWord and s are strings comparable via the actions
        // specified in Settings set
        if(set.getSwapMode() && checkSwap(currentTerm, previousTerm)) {
            return true;
        }
        else if(set.getChangeMode() && checkChange(currentTerm, previousTerm)) {
            return true;
        }
        else return set.getLengthMode() && checkLength(currentTerm, previousTerm);
    }
}
