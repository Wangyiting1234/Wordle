package game;


public class WordleController {
    private IWordleModel model;
    private WordleView view;
    public WordleController(IWordleModel model) {
        this.model = model;
    }
    public void setView(WordleView view) {
        this.view = view;
    }
    public void initializeGame(){
        this.model.initializeGame();
    }
    public void processWord(String word){
        this.model.processWord(word);
    }
    public int getAttemptNumber(){
        return this.model.getAttemptNumber();
    }
    public Integer[] getGuess(){
        return this.model.getGuess();
    }
    public StringBuilder getCurrentWord(){
        return this.model.getCurrentWord();
    }
    public String getGuessWord(){
        return this.model.getTargetWord();
    }
    public boolean isWon(){
        return this.model.isWon();
    }
    public  boolean isOver(){
        return this.model.isOver();
    }
    public void setNotRandom(boolean notRandom) {
        this.model.setNotRandom(notRandom);
    }
    public String ShownOrNot(){
        return this.model.ShownOrNot();
    }
    public void setNotShown(boolean notShown){
        this.model.setNotShown(notShown);
    }
    public Integer ValidSubmit(String word){
        return this.model.ValidSubmit(word);
    }
}
