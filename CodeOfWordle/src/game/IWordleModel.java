package game;

import java.util.List;

public interface IWordleModel {
    int MAX_ATTEMPTS = 6;
    void initialize();
    boolean processWord(String word);
    List<String> getRandomWord();
    List<String> getValidWord();
    boolean isWon();
    boolean isOver();
    Integer getAttemptNumber();
    Integer[] getGuess();
    StringBuilder getCurrentWord();
    boolean isNotExist(String GuessWord);
    public boolean isNotValidWord(String GuessWord);
    String getTargetWord();
    void setTargetWord(String targetWord);
    void setNotRandom(boolean notRandom);
    String ShownOrNot();
    void setNotShown(boolean notShown);
    void initializeGame();
    Integer ValidSubmit(String word);
}
