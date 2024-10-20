package game;

import java.util.*;

public class CLMain  {
    private static final IWordleModel model=new WordleModel();
    private static Integer attemptNumber;
    private static List<Character> correctChar = new ArrayList<>();
    private static List<Character> includeChar= new ArrayList<>();
    private static List<Character> falseChar= new ArrayList<>();
    private static List<Character> remainChar= new ArrayList<>();


    public static void main(String[] args) {
        Scanner console = new Scanner(System.in);
        Start(console);
        console.close();

    }
    public static void Start(Scanner console){
        model.initializeGame();
        attemptNumber = model.getAttemptNumber();
        //initialize the array of remain letters
        for (int i = 0;i!=26; i++){
            remainChar.add((char) ('a' + i));
        }
        StartWord(console);
    }

    /**
     * initialize the game state, parameters and clear the content
     */
    public static void Restart(){
        model.initializeGame();
        attemptNumber = model.getAttemptNumber();
        //reset the four arrays to empty
        correctChar.clear();
        falseChar.clear();
        includeChar.clear();
        remainChar.clear();
        for (int i = 0;i!=26; i++){
            remainChar.add((char) ('a' + i));
        }
    }
    public static void StartNewGame(Scanner console){
        String lastWord = model.getTargetWord();
        System.out.println("Please Select model: Type F: word is fixed; Type R: word is random");
        while(console.hasNextLine()){
            String input = console.nextLine();
            // set up to fix the guess word and start a new game
            if(Objects.equals(input,"F")){
                model.setNotRandom(true);
                Restart();
                model.setTargetWord(lastWord);
                StartWord(console);
                break;
            }
            //set up to random word and start a new game
            else if(Objects.equals(input,"R")){
                Restart();
                StartWord(console);
            }
            //input is not valid
            else {
                System.out.println("Please confirm you input letter is F or R");
            }
        }


    }

    /**
     * set up the game rules and provide choices for user
     * @param console A Scanner object for reading user input
     */
    public static void StartWord(Scanner console){
        System.out.println("-----Welcome to play Wordle-----");
        System.out.println("1. Input Requirement: 5-letter English word");
        System.out.println("2. You will have six attempts in each round");
        System.out.println("3. You can choose to continue the game or restart it after the first valid attemp");
        System.out.println("4. You can select fixed or random words to re-guess after restart the Wordle");
        System.out.println("The above are the rules of the game, good luck and have fun playing Wordle!");
        System.out.println("Type 0 : Show the target word");
        System.out.println("Type 1 : Start Wordle");
        while(console.hasNextLine()) {
            String input = console.nextLine();
            if (Objects.equals(input, "0")) {
                System.out.println("Target Word : " + model.getTargetWord());
            }
            else if(Objects.equals(input, "1")) {
                processGuessWord(console);
                break;
            }
            else {
                System.out.println("Please input 0 or 1");
            }
        }

    }

    /**
     Take a user's input for a guess word, processes it using the Hangman model, and updates the game status accordingly.
     @param console A Scanner object for reading user input.
     */
    public static void processGuessWord(Scanner console){
        while (attemptNumber > 0 ||!model.isOver()) { // While there are still attempts left or the game is not over
            String guessWord = readGuess(console);
            model.processWord(guessWord);
            if (model.isWon()) {
                System.out.println("Congratulations! You won!");
                break;
            } else { // If the guessed word is incorrect
                updateWord(guessWord); // Update the secret word with the user's guessed letter.
            }
            if (model.isOver()) {
                if(model.isWon()){
                    System.out.println("Congratulations! You won!");
                }
                else{
                    System.out.println("Sorry! You lost! The word is "+ model.getTargetWord());
                }
                break;
            }
            readChoice(console); // Ask the user if they want to continue playing.
        }
    }

    /**
     * Use to prompt the user to choose whether to continue the game or start a new one.
     * @param console  A Scanner object for reading user input.
     */
    public static void readChoice(Scanner console){
        System.out.println("Continue the game or Start a new game");
        System.out.println("Type C : continue the game");
        System.out.println("Type N : Start a new game");
        while(console.hasNextLine()) {
            String choice = console.nextLine();
            if (Objects.equals(choice, "N")||Objects.equals(choice, "n")) {// If the user chooses to start a new game
                StartNewGame(console);
                break;
            }
            else if (Objects.equals(choice, "C")||Objects.equals(choice, "c")) {// If user chooses to continue the game
                break;
            }
            else {
                System.out.println("Please input N or C");
            }
        }
    }
    public static String readGuess(Scanner console) {
            String guess;
            System.out.println("----------------------------");
            do {
                System.out.print("Guess Word :");
                guess = console.next();
            }while(!validWord(guess));//read user input until input word conforms to input standards
        return guess.toLowerCase();
    }

    /**
     Check if the guessed word is valid.
     @param guess The guessed word.
     @return Whether the word is valid or not (true/false).
     */
    public static boolean validWord(String guess){
        // Check if the word is five letters long.
        Integer valid = model.ValidSubmit(guess);
        if(valid == 0){
            System.out.println("The inputting word should be 5 letters long");
        }

        // Check if the word contains only letters.
        else if(valid == 1){
            System.out.println("Confirm inputting word is all letters");
        }

        // Check if the word exists in the list of target words.
        else if(valid == 2){
            System.out.println("The inputting word not exists in list");
            System.out.println("Please guess again");
        }

        else {
            // Check if the word is valid but not in the list of target words.
            if(valid == 4){
                System.out.println("This word does valid but not in target word list");
            }
            // If the word is valid, return true.
            return true;
        }
        // If the word is not valid, return false.
        return false;
    }
    /**

     This method updates the word process based on the user's guess word and displays the result to the user.
     @param word The user's guessed word.
     */
    public static void updateWord(String word){
        Integer[] guess = model.getGuess(); // Get the array of correct,incorrect and includes guessed letters.
        System.out.println("Current Guess Result : "+model.getCurrentWord().toString());
        System.out.println("Attempt remains: "+ model.getAttemptNumber());
        for(int i=0;i<guess.length;i++){
            if(guess[i] == 0){ // If the letter at index i was not guessed correctly
                if(!falseChar.contains(word.charAt(i))&&!correctChar.contains(word.charAt(i))){// If the letter has not already been guessed incorrectly or correctly
                    falseChar.add(word.charAt(i));
                }
                if(remainChar.contains(word.charAt(i))){
                    remainChar.remove(remainChar.indexOf(word.charAt(i)));// Remove the letter from the list of remaining letters to guess.
                }
            }
            else if(guess[i] == 1){
                // If the letter at index i was guessed correctly, but not at the correct position…
                if(!includeChar.contains(word.charAt(i))){
                    includeChar.add(word.charAt(i));
                }
                if(remainChar.contains(word.charAt(i))){
                    remainChar.remove(remainChar.indexOf(word.charAt(i)));
                }
            }
            else{// If the letter at index i was guessed correctly and at the correct position
                if(includeChar.contains(word.charAt(i))){
                    includeChar.remove(includeChar.indexOf(word.charAt(i)));// Remove the letter from the list of possible matches for the word.
                }
                if(!correctChar.contains(word.charAt(i))){
                    correctChar.add(word.charAt(i));
                }
                if(remainChar.contains(word.charAt(i))){
                    remainChar.remove(remainChar.indexOf(word.charAt(i)));
                }

            }

        }
        System.out.println("letters does occur at correct location : "+correctChar);
        System.out.println("letters does occur in target word but not at correct location : "+includeChar);
        System.out.println("letters does not occur in target word : "+falseChar);
        System.out.println("letters remains : "+remainChar);
    }

}
