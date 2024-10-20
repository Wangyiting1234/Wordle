package game;

public class Main {
    public static void main(String[] args) {
            javax.swing.SwingUtilities.invokeLater(
                    new Runnable() {
                        public void run() {
                            createAndShowGUI();
                        }
                    }
            );
    }
    public static void createAndShowGUI() {
        IWordleModel model = new WordleModel();
        WordleController controller = new WordleController(model);
        WordleView view = new WordleView(model, controller);
    }
}