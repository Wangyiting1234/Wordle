package game;

import java.io.*;
import java.util.*;

public class WordleModel extends Observable implements IWordleModel {
    private final List<String> RANDOM_WORDS = getRandomWord();// word list to store the word from "comment.txt" and "words.txt"
    private final List<String> INPUT_WORDS = getValidWord();// word list to store the word from "comment.txt"
    private String TargetWord;
    private StringBuilder CurrentWord;// store the guess process while users guess correct letters
    private Integer[] GuessResult = new Integer[5];
    private char[] CurrentChar;
    private Integer AttemptNumber;
    private boolean GameWon;
    private boolean GameOver;
    private boolean NotShown;//FR3: flag to show or hidden the target word
    private boolean NotRandom;//FR3: flag to random or fix the target word
    private boolean NotExist;//FR3: flag to know word exists or not
    private Integer randomIndex;

    //
    public WordleModel() {
    }

    /*@ requires MAX_ATTEMPTS >0;
        @ ensures TargetWord.length() > 0;
        @ ensures CurrentWord.length() == TargetWord.length();
        @ ensures CurrentWord == \old(CurrentWord) + "_"
        @ ensures AttemptNumber == MAX_ATTEMPTS;
        @ ensures GameWon == false;
     @*/
    @Override
    /**
     * Initializes the game state by generating a random target word, setting the initial current word, and
     * initializing the game parameters and variables.
     */
    public void initialize() {
        // generate a random target word
        initializeTargetWord();
        // assert that the target word is not empty
        assert !this.TargetWord.isEmpty();

        // initialize the format of current word with underscores
        this.CurrentWord = new StringBuilder(this.TargetWord.length());
        CurrentWord.append("_".repeat(this.TargetWord.length()));
        // assert that the length of current word matches the target word
        assert CurrentWord.length() == this.TargetWord.length();

        //
        NotRandom = false;
        NotShown = false;
        // initialize the game parameters and variables
        this.AttemptNumber = MAX_ATTEMPTS;
        this.GameWon = false;
        this.GameOver = false;
        this.CurrentChar = new char[5];
        Arrays.fill(GuessResult, 0);

        // notify the observers that the game state has changed
        this.setChanged();
        this.notifyObservers();
    }

    //@ requires None
    public void initializeGame() {
        this.initialize();
    }

    /*@ requires The RANDOM_WORDS list and the RandomWord method to be correctly defined.
      @ assignable this.TargetWord
      @ ensures this.TargetWord = RANDOM_WORDS.get(randomIndex)
    @*/
    public void initializeTargetWord(){
        if(NotRandom){
            this.TargetWord = RANDOM_WORDS.get(randomIndex);
        }
        else{
            this.TargetWord = RandomWord();
            assert this.TargetWord!=null;
        }
    }


    /*@ requires RANDOM_WORDS !=null
      @ assignable randomIndex
      @ ensures randomIndex = rand.nextInt(RANDOM_WORDS.size());
      @ ensures \result = RANDOM_WORDS.get(randomIndex);
     @*/
    /**
     * to get target randomly from guessing list
     * @return a target word
     */
    public String RandomWord() {
        Random rand = new Random();
        randomIndex = rand.nextInt(RANDOM_WORDS.size());//get the random number
        assert randomIndex>=0&&randomIndex<RANDOM_WORDS.size(): "random index is out of the bound";
        return RANDOM_WORDS.get(randomIndex);
    }

    /*@ requires None
      @ ensures  (\existsNotShown==true) <==> \result == ""
      @ ensures  (\existsNotShown==true) <==> \result == this.TargetWord
     @*/
    public String ShownOrNot(){
        //not show the target word
        if(NotShown){
            return "";
        }else{
            //show the target word
            return this.TargetWord;
        }
    }


     /*@ requires GuessWord != null.
       @ assignable this.AttemptNumber, this.GuessResult, this.CurrentChar, this.CurrentWord, and this.GameWon.
       @ ensures  (\exists GuessWord.equals(this.TargetWord)) <==> \result == true
       @ ensures (\exists TargetLetter == GuessLetter) <=> CurrentWord[i] == \old(CurrentWord).setCharAt(i,TargetLetter) && this.GuessResult[i] = 2 && this.CurrentChar[i] = GuessLetter;
     @*/
    /**
     * compare the target word and guess word
     * update the attempt number and guess result
     * @param GuessWord input word that user guesses
     * @return true: guess word is processed successfully
     * false: guess word is not processed successfully
     */
    @Override
    public boolean processWord(String GuessWord) {
        assert GuessWord!=null;
        GuessWord = GuessWord.toLowerCase();
        //Update the attempt number
        --this.AttemptNumber;
        //Determines whether the input word is equal to the target word
        if (GuessWord.equals(this.TargetWord)) {
            this.GameWon = true;
            return this.GameWon;
        }
        //Reset the array of guessing result
        Arrays.fill(this.GuessResult, 0);
        //Reset the array of current letters
        Arrays.fill(this.CurrentChar, '\u0000');
        //start to compare the guess word and target word
        //compare result : 0:wrong letter 1:exist letter 2:correct letter
        for (int i = 0; i < this.TargetWord.length(); i++) {
            //get each letter of target word
            char TargetLetter = this.TargetWord.charAt(i);
            //get each letter of guess word
            char GuessLetter = GuessWord.charAt(i);
            if (this.TargetWord.contains(String.valueOf(GuessLetter))) {
                String s =  String.valueOf(this.CurrentChar);
                // Use regular expressions to replace characters other than the target letter
                String filteredStr = s.replaceAll("[^" + GuessLetter + "]", "");
                // Count the number of times the target letter appears
                int count = filteredStr.length();
                String input = this.TargetWord.replaceAll("[^" + GuessLetter + "]", "");
                int length = input.length();
                if (count == length) {
                    continue;
                }
                this.GuessResult[i] = 1;
            }
            if (TargetLetter == GuessLetter) {
                this.CurrentChar[i] = GuessLetter;
                //replace the char from "_" to correct char
                 this.CurrentWord.setCharAt(i,TargetLetter);
                 this.GuessResult[i] = 2;
            }
        }
        this.setChanged();
        this.notifyObservers();
        return true;
    }


    /*@ requires GuessWord is not null.
      @ ensures (\exists GuessWord.length()!=5) <==> \result == 0
      @ ensures (\exists isNotLetter(GuessWord)) <==> \result == 1
      @ ensures (\exists isNotExist(GuessWord)) <==> \result == 2,
      @ ensures (\exists isNotValidWord(GuessWord)) <==> \result == 4
      @ ensures !(\exists GuessWord.length()!=5) && !(\exists isNotLetter(GuessWord))
      && !(\exists isNotExist(GuessWord)) && !(\exists isNotValidWord(GuessWord)) <==> \result == 3
    @*/
    /**
     * Validates the input guess word and returns an integer code representing the result of the validation.
     */
    public Integer ValidSubmit(String GuessWord){
         /* @pre GuessWord != null
          * @post return value is 0 if GuessWord.length() != 5;
          * return value is 1 if isNotLetter(GuessWord) == true;
          * return value is 2 if isNotExist(GuessWord) == true;
          * return value is 3 if isNotExist(GuessWord) == false and isNotValidWord(GuessWord) == false;
          * return value is 4 if isNotExist(GuessWord) == false and isNotValidWord(GuessWord) == true.
          */
        assert GuessWord != null : "Input 'GuessWord' cannot be null.";

        // validate the input guess word
        if (GuessWord.length()!=5){
            return 0; //the length of inputting word is not 5
        }
        else if(isNotLetter(GuessWord)){
            return 1; //the input word is not all letters
        }

        else if(isNotExist(GuessWord)){
            return 2;//the input word is not includes in list loaded from common.txt and words.txt
        }
        else if(isNotValidWord(GuessWord)){
            return 4;//the input word is not includes in list loaded from common.txt
        }
        assert ValidSubmit(GuessWord) >= 0 && ValidSubmit(GuessWord) <= 4 : "ValidSubmit() method returned incorrect value.";
        return 3;//the input word is valid
    }

    /*@ requires input != null;
      @ ensures !(\exists int i; 0 <= i && i < input.length(); !((‘A’ <= input.charAt(i) && input.charAt(i) <= ‘Z’) || (‘a’ <= input.charAt(i) && input.charAt(i) <= ‘z’))) <==> \result == false;
      @ ensures (\exists int i; 0 <= i && i < input.length(); !((‘A’ <= input.charAt(i) && input.charAt(i) <= ‘Z’) || (‘a’ <= input.charAt(i) && input.charAt(i) <= ‘z’))) <==> \result == true;
    @*/
    /**
     * using to check input word whether only contains letters
     * @param input input text
     * @return true: not all letters; false: all letters
     */
    private boolean isNotLetter(String input){
        /* @pre input != null
         * @post isNotLetter(input) == true if input contains non-letter characters; otherwise, false
         */
        assert input!=null : "Input 'input' cannot be null.";
        for(int i=0;i<input.length();i++){
            if(!((input.charAt(i)>= 'A' && input.charAt(i) <= 'Z') || (input.charAt(i) >= 'a' && input.charAt(i) <= 'z'))){
                return true;
            }
        }
        assert isNotLetter(input) == true : "isNotLetter() method returned incorrect value.";
        return false;
    }

    /*@ requires GuessWord != null;
      @ ensures GuessWord = \old(GuessWord).toLowerCase();
      @ ensures !(\exists INPUT_WORDS.contains(GuessWord)) <==> \result == false;
      @ ensures (\exists INPUT_WORDS.contains(GuessWord)) <==> \result == true;
     @*/
    /**
     * to confirm guess word whether includes in common.txt and words.txt
     * @param GuessWord word that player inputs
     * @return true: guess word does not include in common.txt and words.txt
     * false: guess word does include in common.txt and words.txt
     */
    public boolean isNotExist(String GuessWord) {
         /* @pre GuessWord != null
          * @post isNotExist(GuessWord) == !getInputWords().contains(GuessWord.toLowerCase())
          */
        assert GuessWord!=null : "Input 'GuessWord' cannot be null.";
        GuessWord = GuessWord.toLowerCase();//convert all letters into low case
        NotExist = !INPUT_WORDS.contains(GuessWord);
        assert isNotExist(GuessWord) == !INPUT_WORDS.contains(GuessWord.toLowerCase()) : "isNotExist() method returned incorrect value.";
        return NotExist;
    }

    /*@ requires GuessWord != null;
      @ requires notValid == true || notValid == false;
      @ ensures GuessWord = \old(GuessWord).toLowerCase();
      @ ensures !(\exists RANDOM_WORDS.contains(GuessWord)) <==> \result == false;
      @ ensures (\exists RANDOM_WORDS.contains(GuessWord)) <==> \result == true;
     */
    /**
     * to confirm guess word whether includes in common.txt
     * @param GuessWord word that player inputs
     * @return true: guess word does not include in common.txt
     *      * false: guess word does include in common.txt
     */
    public boolean isNotValidWord(String GuessWord){
        /* @pre GuessWord != null
         * @post (isNotValidWord(GuessWord) && GuessWord != null) ==> RANDOM_WORDS.contains(GuessWord) == false
         *       (!isNotValidWord(GuessWord) && GuessWord != null) ==> RANDOM_WORDS.contains(GuessWord) == true
         */
        assert GuessWord!=null : "Input 'GuessWord' cannot be null.";
        GuessWord = GuessWord.toLowerCase();
        boolean notValid = !RANDOM_WORDS.contains(GuessWord);
        if (notValid) {
            assert RANDOM_WORDS.contains(GuessWord) == false : "The input word is not valid, and it is found in the random words.";
        }
        else {
            assert RANDOM_WORDS.contains(GuessWord) == true : "The input word is valid, but it is not found in the random words.";
        }
        return notValid;
    }

    //@ ensures \result.equals(this.TargetWord.toString());
    public String getTargetWord() {
        return this.TargetWord;
    }

    /*@ requires TargetWord != null;
      @ assignable this.TargetWord;
      @ ensures this.TargetWord == TargetWord;
     @*/
    public void setTargetWord(String TargetWord) {
        /* @pre TargetWord != null
         * @post getTargetWord().equals(TargetWord)
         */
        assert TargetWord!=null: "Input 'TargetWord' cannot be null";
        this.TargetWord = TargetWord;
        assert this.TargetWord!=null : "After calling 'setTargetWord', target word should not be null.";
        assert this.TargetWord.equals(TargetWord) : "After calling 'setTargetWord', target word is not correctly set.";
    }

    //@ ensures \result.equals(this.CurrentWord.toString())
    public StringBuilder getCurrentWord() {
        return this.CurrentWord;
    }

    //@ ensures \result == GuessResult
    public Integer[] getGuess() {
        return GuessResult;
    }

    //@ ensures \result == this.AttemptNumber;
    public Integer getAttemptNumber() {
        return this.AttemptNumber;
    }

    /*@ requires notShown == true || notShown == false;
      @ assignable this.NotShown;
      @ ensures this.NotShown == notShown;
     @*/
    public void setNotShown(boolean notShown) {
        /** @pre notShown != null
         * @post NotShown == notShown
         */
        assert this.NotShown == false : "Initially, NotShown field should be false.";
        this.NotShown = notShown;
        assert this.NotShown == notShown : "Post-condition violated: setNotShown() should return " + notShown;
    }

    /*@ requires notRandom == true || notRandom == false;;
      @ assignable this.NotRandom;
      @ ensures this.NotRandom == NotRandom;
     @*/
    public void setNotRandom(boolean notRandom) {
        /** @pre notRandom != null
         * @post NotRandom() == notRandom
         */
        assert this.NotRandom=false : "Initially, NotRandom field should be false.";
        this.NotRandom = notRandom;
        assert this.NotRandom == notRandom : "Post-condition violated: setNotRandom() should return " + notRandom;
    }

    //@ ensures \result == GameWon;
    public boolean isWon() {
        return this.GameWon;
    }

    //@ ensures \result == AttemptNumber <= 0 || GameWon();
    public boolean isOver() {
        if(this.AttemptNumber <= 0 || this.GameWon){
            this.GameOver = true;
            return this.GameOver;
        }
        return this.GameOver;
    }

    /*@ requires path!=null
      @ ensures (\exists reader.readLine()!=null) <==>  line = reader.readLine();
      @ ensures strList = old(strList).add(line);
      @ ensures \result = strList
    @*/
    /**
    This method loads the content of a file given its path into a String list.
    @param path The path of the file to be loaded.
    @return A list of Strings containing the file content.
     */
    public static List loadFileContent(String path) {
        assert path != null; // Assert that the path is not null.
        List strList = new ArrayList(); // Create a new ArrayList to hold the file content.
        File file = new File(path); // Create a new File object with the specified path.
        InputStreamReader read = null; // Create a new InputStreamReader for the file.
        BufferedReader reader = null; // Create a new BufferedReader for the file.
        try {
            read = new InputStreamReader(new FileInputStream(file), "utf-8"); // Initialize the InputStreamReader with a FileInputStream and "utf-8" encoding.
            reader = new BufferedReader(read); // Initialize the BufferedReader with the InputStreamReader.
            String line;
            while ((line = reader.readLine()) != null) { // Read each line of the file until the end is reached.
                strList.add(line); // Add the line to the ArrayList.
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace(); // Print the stack trace if "utf-8" encoding is not supported.
        } catch (FileNotFoundException e) {
            e.printStackTrace(); // Print the stack trace if the file is not found.
        } catch (IOException e) {
            e.printStackTrace(); // Print the stack trace if an error occurs while reading the file.
        } finally {
            if (read != null) {
                try {
                    read.close(); // Close the InputStreamReader.
                } catch (IOException e) {
                    e.printStackTrace(); // Print the stack trace if an error occurs while closing the InputStreamReader.
                }
            }
            if (reader != null) {
                try {
                    reader.close(); // Close the BufferedReader.
                } catch (IOException e) {
                    e.printStackTrace(); // Print the stack trace if an error occurs while closing the BufferedReader.
                }
            }
        }
        return strList; // Return the ArrayList containing the file content.
    }


    /*@ ensures \result != null;
      @ ensures \forall String word; \result.contains(word);
      @ ensures loadFileContent(“words.txt”).contains(word);
    @*/
    /**
     * to load the target word list
     * @return target word list
     */
    public List<String> getRandomWord(){
        List<String> wordList = loadFileContent("common.txt");
        assert wordList!=null;
        return wordList;
    }

    /*@ ensures \result != null;
      @ ensures \forall String word; \result.contains(word);
      @ (loadFileContent(“words.txt”).contains(word) || loadFileContent(“common.txt”).contains(word));
     @*/
    /**
     * to load the valid word list
     * @return valid word list
     */
    public List<String> getValidWord(){
        List<String> wordList = loadFileContent("words.txt");
        List<String> commonList = loadFileContent("common.txt");
        List<String> validList = new ArrayList<String>();
        //add content from "words.txt" and "common.txt" to validList
        validList.addAll(wordList);
        validList.addAll(commonList);
        assert validList!=null;
        return validList;
    }

}
