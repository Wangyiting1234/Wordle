package game;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
class WordleModelTest {
    static WordleModel model;

    @BeforeEach
    void setUp(){
        //Arrange: Set up the model
        model = new WordleModel();
        //Arrange: initialize the Wordle
        model.initialize();
        //Arrange: set up the target word
        model.setTargetWord("cleat");
    }
    //Test scenario 1
    @Test
    public void TestScenario1() {
        //Scenario 1: Test the results returned by the model for various irregular inputs
        // and test whether the corresponding results produced by processing different input words three times after the specification of the input results satisfy expectations

        setUp();
        //Act: Perform the input validation and set up the input word as "hat"
        //Assert: Verify if method returns 0
        assertEquals(0, model.ValidSubmit("hat"));

        //Act: Perform the input validation and set up the input word as smooth
        //Assert: Verify if method returns 0
        assertEquals(0,  model.ValidSubmit("smooth"));

        //Act: Perform the input validation and set up the input word as wo123
        //Assert: Verify if method returns 1
        assertEquals(1,model.ValidSubmit("wo123"));

        //Act: Perform the input validation and set up the input word as AQAQA
        //Assert: Verify if method returns 2
        assertEquals(2,model.ValidSubmit("AQAQA"));

        //Act: Perform the input validation and set up the input word as aahed
        //Assert: Verify if method returns 4
        assertEquals(4,model.ValidSubmit("aahed"));

        //Act: Perform the input validation and set up the input word as "cigar"
        //Assert: Verify if method returns 3
        assertEquals(3,model.ValidSubmit("cigar"));

        //Act: Perform the processing word and set up the guess word as "cigar" for the first time
        model.processWord("cigar");
        //Assert: Verify if guess result equals "[2, 0, 0, 2, 0]"
        assertEquals("[2, 0, 0, 2, 0]", Arrays.toString(model.getGuess()));
        //Assert: Verify if attempt number remains 5
        assertEquals(5, model.getAttemptNumber());
        //Assert: Verify if the current word is set up to "c__a_"
        assertEquals("c__a_", model.getCurrentWord().toString());
        //Assert: Verify if game is over
        assertFalse(model.isOver());

        //Act: Perform the processing word and set up the guess word as "carat" for the first time
        model.processWord("carat");
        //Assert: Verify if guess result equals "[2, 0, 0, 2, 2]"
        assertEquals("[2, 1, 0, 2, 2]", Arrays.toString(model.getGuess()));
        //Assert: Verify if attempt number remains 4
        assertEquals(4, model.getAttemptNumber());
        //Assert: Verify if the current word is set up to "c__at"
        assertEquals("c__at", model.getCurrentWord().toString());
        //Assert: Verify if game is over
        assertFalse(model.isOver());

        //Act: Perform the processing word and set up the guess word as "eclat" for the first time
        model.processWord("eclat");
        //Assert: Verify if guess result equals "[1, 1, 1, 2, 2]"
        assertEquals("[1, 1, 1, 2, 2]", Arrays.toString(model.getGuess()));
        //Assert: Verify if attempt number remains 3
        assertEquals(3, model.getAttemptNumber());
        //Assert: Verify if game is over
        assertFalse(model.isOver());

        //Act: Perform the processing word and set up the guess word as "cleat" for the first time
        model.processWord("cleat");
        //Assert: Verify if attempt number remains 2
        assertEquals(2, model.getAttemptNumber());
        //Assert: Verify if game is win
        assertTrue(model.isWon());
        //Assert: Verify if game is over
        assertTrue(model.isOver());
    }

    //Test scenario 2
    @Test
    public void TestScenario2() {
        //Scenario 2: Test the model results for different occurrences of the target letter in the input word
        // and for updating the parameters and game state

        setUp();
        //Input that a letter in guess word does occur at one correct location and does occur in another incorrect location for the first time
        //Act: Perform the processing word and set up the guess word as "queue"
        model.processWord("queue");
        //Assert: Verify if guess result equals "[2, 0, 2, 0, 0]"
        assertEquals("[0, 0, 2, 0, 0]", Arrays.toString(model.getGuess()));
        //Assert: Verify if current word equals "__e__"
        assertEquals("__e__", model.getCurrentWord().toString());


        //Act: Perform the processing word and set up the guess word as "swear" for the second time
        model.processWord("swear");
        //Assert: Verify if guess word is valid
        assertFalse(model.isNotValidWord("swear"));
        //Assert: Verify if guess result equals "[0, 1, 2, 1, 0]"
        assertEquals("[0, 0, 2, 2, 0]", Arrays.toString(model.getGuess()));
        //Assert: Verify if attempt number remains 4
        assertEquals(4, model.getAttemptNumber());


        //Act: Perform the processing word and set up the guess word as "scent" for the third time
        model.processWord("scent");
        //Assert: Verify if guess result equals "[0, 1, 2, 0, 2]"
        assertEquals("[0, 1, 2, 0, 2]", Arrays.toString(model.getGuess()));
        //Assert: Verify if attempt number remains 4
        assertEquals(3, model.getAttemptNumber());

        //Act: Perform the processing word and set up the guess word as "elect" for the fourth time
        model.processWord("elect");
        //Assert: Verify if guess result equals "[0, 1, 2, 1, 2]"
        assertEquals("[1, 2, 2, 1, 2]", Arrays.toString(model.getGuess()));
        //Assert: Verify if attempt number remains 3
        assertEquals(2, model.getAttemptNumber());
        assertFalse(model.isOver());

        //Act: Perform the processing word and set up the guess word as "cleat" for the fifth time
        model.processWord("cleat");
        //Assert: Verify if attempt number remains 2
        assertEquals(1, model.getAttemptNumber());
        //Assert: Verify if game is win
        assertTrue(model.isWon());
        //Assert: Verify if game is over
        assertTrue(model.isOver());
    }



    //Test scenario 3
    @Test
    public void TestScenario3() {
        //Scenario 3: Test the model with processing of the words
        // and the game state after each input of a different word,
        // and test the state of the game after a total of six unsuccessful attempts by the model to process the word

        setUp();
        //Input that each letter in guess word does not occur in target word for the first time
        //Act: Perform the processing word and set up the guess word as "sword"
        model.processWord("sword");
        //Assert: Verify if guess result equals "[0, 0, 0, 0, 0]"
        assertEquals("[0, 0, 0, 0, 0]", Arrays.toString(model.getGuess()));
        //Assert: Verify if current word equals "_____"
        assertEquals("_____", model.getCurrentWord().toString());

        //Input that subletter in guess word does occur in target word but not at correct location for the second time
        //Act: Perform the processing word and set up the guess word as "dance"
        model.processWord("dance");
        //Assert: Verify if guess result equals "[0, 1, 0, 1, 1]"
        assertEquals("[0, 1, 0, 1, 1]", Arrays.toString(model.getGuess()));
        //Assert: Verify if current word equals "_____"
        assertEquals("_____", model.getCurrentWord().toString());

        //Input that two subletter in guess word does occur in target word but not at correct location
        // and one subletter in guess word does occur in correct location for the third time
        //Act: Perform the processing word and set up the guess word as "cease"
        model.processWord("cease");
        //Assert: Verify if guess result equals "[2, 1, 1, 0, 1]"
        assertEquals("[2, 1, 1, 0, 1]", Arrays.toString(model.getGuess()));
        //Assert: Verify if current word equals "c____"
        assertEquals("c____", model.getCurrentWord().toString());


        //Input that two subletter in guess word does occur at correct location
        // and subletter in guess word does occur in target word but not at correct location for the fourth time
        //Act: Perform the processing word and set up the guess word as "clash"
        model.processWord("clash");
        //Assert: Verify if guess result equals "[2, 2, 1, 0, 0]"
        assertEquals("[2, 2, 1, 0, 0]", Arrays.toString(model.getGuess()));
        //Assert: Verify if current word equals "cl___"
        assertEquals("cl___", model.getCurrentWord().toString());


        //Input that four subletter in guess word does occur at correct location
        // and one subletter in guess word does not occur in target word for the fifth time
        //Act: Perform the processing word and set up the guess word as "clear"
        model.processWord("clear");
        //Assert: Verify if guess result equals "[2, 2, 2, 2, 0]"
        assertEquals("[2, 2, 2, 2, 0]", Arrays.toString(model.getGuess()));
        //Assert: Verify if current word equals "clea_"
        assertEquals("clea_", model.getCurrentWord().toString());


        //Input that subletter in guess word does occur at correct location and subletter in guess word does occur in target word but not at correct location for the fifth time
        //Act: Perform the processing word and set up the guess word as "clash"
        model.processWord("clean");
        //Assert: Verify if guess result equals "[2, 2, 2, 2, 0]"
        assertEquals("[2, 2, 2, 2, 0]", Arrays.toString(model.getGuess()));
        //Assert: Verify if current word equals "clea_"
        assertEquals("clea_", model.getCurrentWord().toString());
        //Assert: Verify if the attempt number remains 0
        assertEquals(0, model.getAttemptNumber());
        //Assert: Verify if game is win
        assertFalse(model.isWon());
        //Assert: Verify if game is over
        assertTrue(model.isOver());

    }


}