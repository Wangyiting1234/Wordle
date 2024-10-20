package game;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Objects;
import java.util.Observable;
import java.util.Observer;

public class WordleView extends JFrame implements Observer {
    private final IWordleModel model;
    private WordleController controller;
    private JButton[] keyboardRow1Button;
    private JButton[] keyboardRow2Button;
    private JButton[] keyboardRow3Button;
    private JLabel CurrentWordLabel;
    private JTextField InputWordField;
    private JTextField GuessWordField;
    private JLabel attemptsLabel;
    private JPanel keyboardPanel;
    private JButton submitButton;
    private JButton enter;
    private JButton delete;
    private JButton[] gameProcess;
    private JPanel keyboardRow1Panel;
    private JPanel keyboardRow2Panel;
    private JPanel keyboardRow3Panel;
    private final String[] firstRow = {"Q", "W", "E", "R", "T", "Y", "U", "I", "O", "P"};
    private final String[] secondRow = {"A", "S", "D", "F", "G", "H", "J", "K", "L"};
    private final String[] thirdRow = {"Z", "X", "C", "V", "B", "N", "M"};
    private boolean NotVisible = true;

    /**
     * Initializes the WordleView by setting up the controller, model, and game interface.
     */
    public WordleView(IWordleModel model, WordleController controller) {
        // set up the controller and model
        this.controller = controller;
        this.model = model;

        // initialize the game state and register as an observer of the model
        controller.initializeGame();
        ((WordleModel) this.model).addObserver(this);
        initialize();

        // set the view for the controller and update the model
        this.controller.setView(this);
        update((WordleModel) this.model, null);
    }

    /**
     * initializes the User Interface
     */
    public void initialize() {
        // set up the frame parameters
        this.setTitle("Wordle");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setLocation(300, 300);
        this.setPreferredSize(new Dimension(800, 720));
        this.setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        //set up the top design of panel : the guess result, input bar and attempt numbers
        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEADING, 100, 20));

        //set up the panel for displaying the current word state
        CurrentWordLabel = new JLabel("_____");
        CurrentWordLabel.setFont(new Font("SansSerif", Font.BOLD, 24));
        top.add(CurrentWordLabel);

        //set up the panel for inputting the guess word and submit button
        setUpInputFiled(top);

        //set up the panel for displaying the remaining attempts number
        JPanel attemptsPanel = new JPanel();
        attemptsLabel = new JLabel("Attempts: " + controller.getAttemptNumber());
        attemptsPanel.add(attemptsLabel);
        top.add(attemptsPanel);
        add(top, BorderLayout.NORTH);

        //set up the center panel
        JPanel center = new JPanel();
        add(center);

        //set up the keyboard
        setUpKeyboard(center);

        //set up the panel of displaying the process of guess
        JPanel process = new JPanel();
        process.setLayout(new GridLayout(6, 5, 10, 10));
        gameProcess = new JButton[30];

        for (int i = 0; i < gameProcess.length; i++) {
            gameProcess[i] = new JButton();
            gameProcess[i].setText("");
            gameProcess[i].setFont(new Font("SansSerif", Font.BOLD, 20));
            gameProcess[i].setPreferredSize(new Dimension(60, 60));
            gameProcess[i].setBackground(Color.decode("#dce1ed"));
            gameProcess[i].setBorderPainted(false);
            process.add(gameProcess[i]);
        }
        center.add(process);
        //Set up the guess word
        JPanel bottom = new JPanel();
        JPanel guess = new JPanel();
        JButton show = new JButton();
        show.setText("Target Word");

        showWord(show);

        GuessWordField = new JTextField();
        GuessWordField.setPreferredSize(new Dimension(100, 30));
        guess.add(GuessWordField);
        guess.add(show);
        bottom.add(guess, BorderLayout.WEST);
        add(bottom, BorderLayout.SOUTH);
        //Set up the restart button
        JButton restart = new JButton();
        bottom.add(restart, BorderLayout.CENTER);
        restart.setText("New Game");
        restart(restart);
        setVisible(true);
        setLocationRelativeTo(null);
    }

    public void restart(JButton restart) {
        restart.addActionListener(e -> {
            if (controller.getAttemptNumber() < 6) {
                int option = JOptionPane.showOptionDialog(null, "Random the target word", "Select Mode"
                        , JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE, null, new String[]{"No", "Yes"}, "Yes");
                //ensure panel can not be change if user cancel "restart"
                this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
                //Click the "No"
                if (option == 0) {
                    controller.setNotRandom(true);
                    controller.initializeGame();
                    cleanPanel();
                    setAbleKeyboard();
                }
                //Click the "Yes"
                else if (option == 1) {
                    controller.setNotRandom(false);
                    controller.initializeGame();
                    cleanPanel();
                    setAbleKeyboard();
                }
            } else {
                JOptionPane.showMessageDialog(this, "You should attempt at least one time");
            }
        });
    }

    /**
     * Sets up the input filed with sumbit button for the user to input letters.
     * @param topPanel the top location of the whole panel
     */
    private void setUpInputFiled(JPanel topPanel){
        //set up the panel for inputting the guess word and submit button
        JPanel inputPanel = new JPanel();
        InputWordField = new JTextField();
        InputWordField.setPreferredSize(new Dimension(100, 30));
        inputPanel.add(InputWordField);
        submitButton = new JButton("Submit");
        submit(submitButton);
        inputPanel.add(submitButton);
        topPanel.add(inputPanel);
    }

    /**
     * Sets up the keyboard panel with buttons for the user to input letters.
     * @param center the center panel in the whole panel
     */
    private void setUpKeyboard(JPanel center){
        keyboardPanel = new JPanel();
        center.add(keyboardPanel, BorderLayout.NORTH);
        keyboardPanel.setLayout(new GridLayout(3, 1, 10, 10));
        pack();

        //set up the first row of keyboard
        keyboardRow1Button = new JButton[firstRow.length];
        keyboardRow1Panel = new JPanel(new GridLayout(1, firstRow.length, 10, 10));
        for (int i = 0; i < firstRow.length; i++) {
            JButton b = new JButton(firstRow[i]);
            // set button appearance
            b.setBorderPainted(false);
            b.setBackground(Color.decode("#dce1ed"));
            keyboardRow1Button[i] = b;
            keyboardRow1Button[i].setName(firstRow[i]);
            b.setFont(new Font("SansSerif", Font.BOLD, 20));
            b.addActionListener(e -> {
                InputWordField.setText(InputWordField.getText() + b.getText());
            });
            //add action listener to append the clicked letter to the input word field
            b.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    super.mouseEntered(e);
                    if(Objects.equals(b.getBackground(), Color.decode("#dce1ed"))){
                        b.setBackground(Color.decode("#c4cbdd"));
                    }
                }
            });
            //add mouse listeners to provide visual
            b.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseExited(MouseEvent e) {
                    super.mouseExited(e);
                    if(Objects.equals(b.getBackground(), Color.decode("#c4cbdd"))){
                        b.setBackground(Color.decode("#dce1ed"));
                    }
                }
            });
            keyboardRow1Panel.add(keyboardRow1Button[i]);
        }

        //set up the second row of keyboard
        keyboardRow2Button = new JButton[secondRow.length];
        keyboardRow2Panel = new JPanel(new GridLayout(1, secondRow.length, 10, 10));
        for (int i = 0; i < secondRow.length; i++) {
            JButton b = new JButton(secondRow[i]);
            b.setBorderPainted(false);
            b.setBackground(Color.decode("#dce1ed"));
            keyboardRow2Button[i] = b;
            keyboardRow2Button[i].setName(secondRow[i]);
            keyboardRow2Button[i].setFont(new Font("SansSerif", Font.BOLD, 20));
            b.addActionListener(e -> {
                InputWordField.setText(InputWordField.getText() + b.getText());
            });
            b.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    super.mouseEntered(e);
                    if(Objects.equals(b.getBackground(), Color.decode("#dce1ed"))){
                        b.setBackground(Color.decode("#c4cbdd"));
                    }
                }
            });
            b.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseExited(MouseEvent e) {
                    super.mouseExited(e);
                    if(Objects.equals(b.getBackground(), Color.decode("#c4cbdd"))){
                        b.setBackground(Color.decode("#dce1ed"));
                    }
                }
            });
            keyboardRow2Panel.add(keyboardRow2Button[i]);
        }

        //set up the third row of keyboard
        keyboardRow3Button = new JButton[thirdRow.length];
        keyboardRow3Panel = new JPanel(new GridLayout(1, thirdRow.length, 10, 10));
        delete = new JButton();
        //set up the function while user click "Enter"
        delete.addActionListener(e ->
                InputWordField.setText(
                        InputWordField.getText().length() != 0 ?
                                InputWordField.getText().substring(0, InputWordField.getText().length() - 1) : ""));
        delete.setFont(new Font("SansSerif", Font.BOLD, 15));
        delete.setBackground(Color.decode("#dce1ed"));
        delete.setBorderPainted(false);
        delete.setName("<<<");
        delete.setText("<<<");
        keyboardRow3Panel.add(delete);
        for (int i = 0; i < thirdRow.length; i++) {
            JButton b = new JButton(thirdRow[i]);
            b.setBackground(Color.decode("#dce1ed"));
            b.setBorderPainted(false);
            keyboardRow3Button[i] = b;
            keyboardRow3Button[i].setName(thirdRow[i]);
            b.addActionListener(e -> {
                InputWordField.setText(InputWordField.getText() + b.getText());
            });
            b.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    super.mouseEntered(e);
                    if(Objects.equals(b.getBackground(), Color.decode("#dce1ed"))){
                        b.setBackground(Color.decode("#c4cbdd"));
                    }

                }
            });
            b.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseExited(MouseEvent e) {
                    super.mouseExited(e);
                    if(Objects.equals(b.getBackground(), Color.decode("#c4cbdd"))){
                        b.setBackground(Color.decode("#dce1ed"));
                    }

                }
            });
            keyboardRow3Button[i].setFont(new Font("SansSerif", Font.BOLD, 20));
            keyboardRow3Panel.add(keyboardRow3Button[i]);
        }

        //set up the key of "Enter"
        enter = new JButton();
        submit(enter);

        //set up the style of "Enter" button
        enter.setFont(new Font("SansSerif", Font.BOLD, 15));
        enter.setBackground(Color.decode("#dce1ed"));
        enter.setBorderPainted(false);
        enter.setText("Enter");
        enter.setName("Enter");
        keyboardRow3Panel.add(enter);
        keyboardPanel.add(keyboardRow1Panel);
        keyboardPanel.add(keyboardRow2Panel);
        keyboardPanel.add(keyboardRow3Panel);
    }

    /**
     * Set up the button of show or hidden target word
     * @param show button to click
     */
    public void showWord(JButton show) {
        show.addActionListener(e -> {
            if (!NotVisible) {//not show the target word
                controller.setNotShown(true);
                GuessWordField.setText(controller.ShownOrNot());
            } else {
                controller.setNotShown(false);
                GuessWordField.setText(controller.ShownOrNot());
            }
            //set up the button name
            show.setText(!NotVisible ? "Show" : "Hidden");
            NotVisible =!NotVisible;
        });
    }

    /**
     * adds an action listener to the "submit" button to verify the guess word whether is valid
     * @param submit button to submit the input word
     */
    public void submit(JButton submit) {
        submit.addActionListener(e -> {//adds an action listener to the "submit" button.
            String input = InputWordField.getText();
            Integer hint = controller.ValidSubmit(input);//validate the input
            //judge the inputting word whether is 5 bits
            if (hint == 0) {
                JOptionPane.showMessageDialog(this, "The input must be 5 letters");
                InputWordField.setText("");
            }
            else if(hint==1){
                JOptionPane.showMessageDialog(this, "Please input letter only");
                InputWordField.setText("");
            }
            //judge the inputting word whether is in the guess list
            else if(hint==2){
                JOptionPane.showMessageDialog(this, "This word does not exist in guess list");
                InputWordField.setText("");
            }
            else {
                if(hint==4){
                    JOptionPane.showMessageDialog(this, "This word does valid but not in target word list");
                }
                controller.processWord(InputWordField.getText());
                isWin();
            }
            InputWordField.setText("");
        });

    }


    public void update() {
        //update the guess process
        CurrentWordLabel.setText(controller.getCurrentWord().toString());
        attemptsLabel.setText("Attempts: " + controller.getAttemptNumber());
        isOver();
        if (controller.getAttemptNumber() < 6) {
            //update the color of keyboard
            updateKeyBoard(keyboardRow1Panel);
            updateKeyBoard(keyboardRow2Panel);
            updateKeyBoard(keyboardRow3Panel);
            //update the color and text of guess result
            updateProcess();
        }
    }

    /**
     * Set up the panel of guess process while user type the word each time
     */
    public void updateProcess() {
        Integer[] guess = controller.getGuess();// the guess result processed by model
        String word = InputWordField.getText();
        int number = controller.getAttemptNumber();
        if (number == 5) {
            for (int i = 0; i < 5; i++) {
                gameProcess[i].setText(String.valueOf(word.charAt(i)));
//                gameProcess[i].setForeground(Color.WHITE);
                //inputting letter not contains in target word
                if (guess[i] == 0) {
                    gameProcess[i].setBackground(Color.decode("#a4aec4"));
                }
                //inputting letter contains in target word
                else if (guess[i] == 1) {
                    gameProcess[i].setBackground(Color.decode("#f3c237"));
                }
                //inputting letter matches with target letter
                else {
                    gameProcess[i].setBackground(Color.decode("#79b851"));
                }
            }
        }
        if (number == 4) {
            for (int i = 0; i < 5; i++) {
                gameProcess[i + 5].setText(String.valueOf(word.charAt(i)));
//                gameProcess[i].setForeground(Color.WHITE);
                if (guess[i] == 0) {
                    gameProcess[i + 5].setBackground(Color.decode("#a4aec4"));
                } else if (guess[i] == 1) {
                    gameProcess[i + 5].setBackground(Color.decode("#f3c237"));
                }
                //inputting letter matches with target letter
                else {
                    gameProcess[i + 5].setBackground(Color.decode("#79b851"));
                }
            }
        }
        if (number == 3) {
            for (int i = 0; i < 5; i++) {
                gameProcess[i + 10].setText(String.valueOf(word.charAt(i)));
//                gameProcess[i].setForeground(Color.WHITE);
                if (guess[i] == 0) {
                    gameProcess[i + 10].setBackground(Color.decode("#a4aec4"));
                } else if (guess[i] == 1) {
                    gameProcess[i + 10].setBackground(Color.decode("#f3c237"));
                } else {
                    gameProcess[i + 10].setBackground(Color.decode("#79b851"));
                }
            }

        }
        if (number == 2) {
            for (int i = 0; i < 5; i++) {
                gameProcess[i + 15].setText(String.valueOf(word.charAt(i)));
//                gameProcess[i].setForeground(Color.WHITE);
                if (guess[i] == 0) {
                    gameProcess[i + 15].setBackground(Color.decode("#a4aec4"));
                } else if (guess[i] == 1) {
                    gameProcess[i + 15].setBackground(Color.decode("#f3c237"));
                }
                //inputting letter matches with target letter
                else {
                    gameProcess[i + 15].setBackground(Color.decode("#79b851"));
                }
            }

        }
        if (number == 1) {
            for (int i = 0; i < 5; i++) {
                gameProcess[i + 20].setText(String.valueOf(word.charAt(i)));
//                gameProcess[i].setForeground(Color.WHITE);
                if (guess[i] == 0) {
                    gameProcess[i + 20].setBackground(Color.decode("#a4aec4"));
                } else if (guess[i] == 1) {
                    gameProcess[i + 20].setBackground(Color.decode("#f3c237"));

                }
                //inputting letter matches with target letter
                else {
                    gameProcess[i + 20].setBackground(Color.decode("#79b851"));
                }
            }
        }
        if (number == 0) {
            for (int i = 0; i < 5; i++) {
                gameProcess[i + 25].setText(String.valueOf(word.charAt(i)));
//                gameProcess[i].setForeground(Color.WHITE);
                if (guess[i] == 0) {
                    gameProcess[i + 25].setBackground(Color.decode("#a4aec4"));
                } else if (guess[i] == 1) {
                    gameProcess[i + 25].setBackground(Color.decode("#f3c237"));

                }
                //inputting letter matches with target letter
                else {
                    gameProcess[i + 25].setBackground(Color.decode("#79b851"));
                }
            }
        }
    }

    /**
     * use to guess the right word the first time
     */
    public void isWin() {
        if (controller.isWon()) {
            setEnableKeyboard();
            JOptionPane.showMessageDialog(this, "Congratulations! You won!");
        }
    }

    /**
     * Sets up the keyboard and button of submit can be not clicked
     */
    public void setEnableKeyboard() {
        for (JButton button : keyboardRow1Button) {
            button.setEnabled(false);
        }
        for (JButton button : keyboardRow2Button) {
            button.setEnabled(false);
        }
        for (JButton button : keyboardRow3Button) {
            button.setEnabled(false);
        }
        enter.setEnabled(false);
        delete.setEnabled(false);
        submitButton.setEnabled(false);
    }
    /**
     * Sets up the keyboard and button of submit can be clicked
     */
    public void setAbleKeyboard() {
        for (JButton button : keyboardRow1Button) {
            button.setEnabled(true);
        }
        for (JButton button : keyboardRow2Button) {
            button.setEnabled(true);
        }
        for (JButton button : keyboardRow3Button) {
            button.setEnabled(true);
        }
        enter.setEnabled(true);
        delete.setEnabled(true);
        submitButton.setEnabled(true);
    }

    /**
     * clean the color and text of panel of guess process
     * clean the color of panel of keyboard
     */
    public void cleanPanel(){
        for(JButton button : keyboardRow1Button) {
            button.setBackground(Color.decode("#dce1ed"));
        }
        for (JButton button : keyboardRow2Button) {
            button.setBackground(Color.decode("#dce1ed"));
        }
        for (JButton button : keyboardRow3Button) {
            button.setBackground(Color.decode("#dce1ed"));
        }
        for(JButton button : gameProcess){
            button.setText("");
            button.setBackground(Color.decode("#dce1ed"));
        }
        GuessWordField.setText("");
    }

    /**
     * This method checks if the game is over, meaning either the player has won or lost.
     * If the game is over, the keyboard is enabled and a message is displayed to the user.
     * If the player has won, the method "isWin" is called.
     * If the player has lost, a message is displayed showing the correct word that the player failed to guess.
     */
    public void isOver() {
        if (controller.isOver()) {//while user lost or won the game
            setEnableKeyboard();// keyboard can be not press
            if (controller.isWon()) {
                isWin();//notify user won the game
            } else {
                JOptionPane.showMessageDialog(this, "Sorry! You lost! The word is " + controller.getGuessWord());
            }
        }
    }



    /**
     * Sets up to update the color of each key in keyboard
     * @param keyboardPanel
     */

    public void updateKeyBoard(JPanel keyboardPanel) {
        //to get the guess result
        Integer[] guess = controller.getGuess();
        String word = InputWordField.getText();
        for (int i = 0; i < guess.length; i++) {
            //input letter not includes in guess word
            if (guess[i] == 0) {
                Component key = findKey(keyboardPanel, String.valueOf(word.charAt(i)));
                if (key != null && !Objects.equals(key.getBackground(), Color.decode("#79b851"))) {
                    key.setBackground(Color.decode("#a4aec4"));
                }
            }
            //inputting letter includes in guess word
            else if (guess[i] == 1) {
                Component key = findKey(keyboardPanel, String.valueOf(word.charAt(i)));
                if (key != null) {
                    if(!Objects.equals(key.getBackground(), Color.decode("#79b851"))) {
                        key.setBackground(Color.decode("#f3c237"));
                    }
                }
            }
            //input letter matches with target letter
            else {
                Component key = findKey(keyboardPanel, String.valueOf(word.charAt(i)));
                if (key != null) {
                    key.setBackground(Color.decode("#79b851"));
                }
            }
        }
    }

    /**
     * find whether the component exits in target panel
     * @param keyPanel target panel for finding component
     * @param name     name of target component
     * @return finding result:has: component, has not: null
     */
    public Component findKey(JPanel keyPanel, String name) {
        int count = keyPanel.getComponentCount();
        Component result = null;
        for (int i = 0; i < count; i++) {
            //get each component from keyboard
            Component key = keyPanel.getComponent(i);
            if (key.getName().equals(name.toUpperCase())) {
                result = key;
                return result;
            }
        }
        return result;
    }

    public void update(Observable o, Object arg) {
        this.update();
    }
}
