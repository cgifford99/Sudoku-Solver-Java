package sudoku;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.*;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;
import javax.swing.undo.UndoManager;
import java.awt.*;
import java.awt.event.*;
import java.util.Arrays;

@SuppressWarnings({"FieldCanBeLocal", "WeakerAccess"})
public class SudokuGUI extends JFrame {
    private static final int B_WIDTH = SudokuSolver.BOARD_WIDTH;
    private static final int B_HEIGHT = SudokuSolver.BOARD_HEIGHT;
    private boolean crosshairEnabled = true;
    private boolean restrictKeyboard = false;
    private boolean validation = true;

    private Color happyColor = new Color(215, 235, 255);
    private Color errorColor = new Color(255, 200, 200);
    private Color selectColor = happyColor;

    private UndoManager undo = new UndoManager();

    private JPanel board = new JPanel(new GridLayout(3, 3, 1, 1));
    private JPanel[] boardArr = new JPanel[9];
    private JPanel control = new JPanel(new FlowLayout(FlowLayout.LEADING));
    private JPanel game = new JPanel(new FlowLayout(FlowLayout.LEADING));
    private JTextField lastField = new JTextField();
    private JFrame helpFrame = new JFrame();
    private JPanel helpPanel = new JPanel();
    private JLabel helpLabel = new JLabel();

    private JMenuBar mBar = new JMenuBar();
    private JMenu fileMenu = new JMenu("File");
    private JMenu editMenu = new JMenu("Edit");
    private JMenu helpMenu = new JMenu("Help");
    private JMenuItem solveItem = new JMenuItem("Solve Board");
    private JMenuItem resetItem = new JMenuItem("Reset Board");
    private JMenuItem exitItem = new JMenuItem("Exit");
    private JMenuItem undoItem = new JMenuItem("Undo");
    private JMenuItem redoItem = new JMenuItem("Redo");
    private JMenuItem helpItem = new JMenuItem("Help");
    private JMenuItem aboutItem = new JMenuItem("About");
    private JDialog aboutDialog = new JDialog();
    private JLabel aboutLabel = new JLabel();

    //TODO: User-solved game mode. Timer, auto-generated difficulties, etc. <---- BIG TODO (Take several months for this one).

    public SudokuGUI() {
        JTextField[][] tfCells = new JTextField[B_HEIGHT][B_WIDTH];
        for (int i = 0; i < boardArr.length; i++) {
            boardArr[i] = new JPanel(new GridLayout(3, 3));
            boardArr[i].setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));
            boardArr[i].setBackground(Color.BLACK);
            board.add(boardArr[i]);
        }

        Board sudoBoard = new Board(SudokuSolver.BOARD_WIDTH, SudokuSolver.BOARD_HEIGHT, "Input by User",
                SudokuSolver.boardInput);
        // set all cells to zero
        for (int i = 0; i < 9; i++) {
            Arrays.fill(sudoBoard.getBoard()[i], 0);
        }

        for (int i = 0; i < B_HEIGHT; i++) {
            for (int j = 0; j < B_WIDTH; j++) {
                final int altI = i;
                final int altJ = j;
                tfCells[i][j] = new LimitedTextField(1); // Calls overwritten TextField class. Used for limiting length

                // Various aesthetic and functional changes
                tfCells[i][j].setHorizontalAlignment(JTextField.CENTER);
                tfCells[i][j].setFont(tfCells[i][j].getFont().deriveFont(40F));
                tfCells[i][j].setRequestFocusEnabled(true);
                tfCells[i][j].setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));
                tfCells[i][j].getDocument().addUndoableEditListener(undo);

                // Character insertion event listener
                tfCells[i][j].getDocument().addDocumentListener(new DocumentListener() {
                    @Override
                    public void insertUpdate(DocumentEvent e) {
                        //noinspection CatchMayIgnoreException
                        try {
                            if (validation) numValidation(sudoBoard, tfCells, altI, altJ);
                            sudoBoard.setBoardIndex(altI, altJ, sudoBoard.getBoard()[altI][altJ]
                                    = Integer.parseInt(tfCells[altI][altJ].getText()));
                        } catch (NumberFormatException ex) {
                            System.err.println(ex);
                        }
                    }

                    @Override
                    public void removeUpdate(DocumentEvent e) {
                        // cursor must be after value
                        // can create errors and the delete key works instead
                        // if cursor is before value, backspace doesn't work but delete works
                        try {
                            if (validation) numValidation(sudoBoard, tfCells, altI, altJ);
                            sudoBoard.setBoardIndex(altI, altJ, sudoBoard.getBoard()[altI][altJ] = 0);
                        } catch (NumberFormatException ex) {
                            System.err.println(ex);
                        }
                    }

                    @Override
                    public void changedUpdate(DocumentEvent e) {
                        //noinspection CatchMayIgnoreException
                        try {
                            if (validation) numValidation(sudoBoard, tfCells, altI, altJ);
                            sudoBoard.setBoardIndex(altI, altJ, sudoBoard.getBoard()[altI][altJ]
                                    = Integer.parseInt(tfCells[altI][altJ].getText()));
                        } catch (NumberFormatException ex) {
                            System.err.println(ex);
                        }
                    }
                });

                // Sudoku board cell focus listener
                tfCells[i][j].addFocusListener(new FocusAdapter() {
                    @Override
                    public void focusGained(FocusEvent e) {
                        setCellColor(selectColor, tfCells, altI, altJ);
                        tfCells[altI][altJ].setCaretPosition(tfCells[altI][altJ].getText().length());
                    }
                    @Override
                    public void focusLost(FocusEvent e) {
                        setCellColor(Color.WHITE, tfCells, altI, altJ);
                    }
                });

                // Keyboard event listener
                tfCells[i][j].addKeyListener(new KeyAdapter() {
                    @Override
                    public void keyPressed(KeyEvent e) {
                        //noinspection CatchMayIgnoreException
                        try {
                            if (!restrictKeyboard || e.getKeyCode() == 8) {
                                if (e.getKeyCode() == KeyEvent.VK_UP) {
                                    tfCells[altI - 1][altJ].requestFocusInWindow();
                                } else if (e.getKeyCode() == KeyEvent.VK_DOWN) {
                                    tfCells[altI + 1][altJ].requestFocusInWindow();
                                } else if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
                                    tfCells[altI][altJ + 1].requestFocusInWindow();
                                } else if (e.getKeyCode() == KeyEvent.VK_LEFT) {
                                    tfCells[altI][altJ - 1].requestFocusInWindow();
                                }
                            }
                        } catch (ArrayIndexOutOfBoundsException ex) {
                            System.err.println(ex);
                        }
                    }
                });

                // TextField Caret modifier
                Caret blank = new DefaultCaret() {

                    @Override
                    public void paint(Graphics g) {
                    }

                    @Override
                    public boolean isVisible() {
                        return false;
                    }

                    @Override
                    public boolean isSelectionVisible() {
                        return false;
                    }

                };
                tfCells[i][j].setCaret(blank);

                int idx = ((j * 3) / 9) + (i / 3) * 3;
                // System.out.println(idx);
                boardArr[idx].add(tfCells[i][j]);
            }
        }

        board.setBackground(Color.BLACK);
        board.setSize(new Dimension(500, 500));


        // Creates and modifies Sudoku Solver game
        // TODO: Move difficulty button to settings and generate JDialog
        String[] difficulty = {"Easy", "Medium", "Hard"};
        JButton difficultyButton = new JButton();
        Action difficultyAction = new AbstractAction("Difficulty: " + difficulty[0]) {
            @Override
            public void actionPerformed(ActionEvent e) {
                int index = 0;
                for(int i = 0; i < difficulty.length; i++) {
                    if(difficultyButton.getText().split(":")[1].trim().contains(difficulty[i])){
                        index = i;
                    }
                }
                try {
                    difficultyButton.setText("Difficulty: " + difficulty[index+1]);
                } catch (ArrayIndexOutOfBoundsException e1) {
                    difficultyButton.setText("Difficulty: " + difficulty[0]);
                    System.err.println(e1);
                }
            }
        };
        difficultyButton.setAction(difficultyAction);
        difficultyButton.setFont(difficultyButton.getFont().deriveFont(18F)); // Grab default windows font to size 18
        game.add(difficultyButton);

        JLabel timerLabel = new JLabel("Timer 0:00");
        timerLabel.setFont(timerLabel.getFont().deriveFont(18F)); // Grab default windows font to size 18
        Timer timer = new Timer(1000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String[] timerStringArr = timerLabel.getText().split(" ")[1].trim().split(":");
                int minutes = Integer.parseInt(timerStringArr[0]);
                int seconds = Integer.parseInt(timerStringArr[1])+1;
                String timerString = "";
                if(seconds >= 60){
                    minutes += 1;
                    seconds = 0;
                }
                if(seconds < 10){
                    timerString = minutes + ":0" + seconds;
                }else{
                    timerString = minutes + ":" + seconds;
                }
                timerLabel.setText("Timer " + timerString);
            }
        });

        JButton playButton = new JButton();
        Action playAction = new AbstractAction("Begin game!") {
            @Override
            public void actionPerformed(ActionEvent e) {
                //TODO: Generate a board based on difficulty using techniques from sudokuoftheday.com/techniques
                if(timer.isRunning()){
                    // Stop the game
                    timer.stop();
                    playButton.setText("Begin game!");
                }else{
                    // Check board empty
                    boolean boardEmpty = sudoBoard.isEmpty(sudoBoard.getBoard());
                    boolean canSolve = sudoBoard.canSolve();
                    if(boardEmpty) {
                        // if board is entirely empty
                        JDialog generator = new JDialog();
                        generator.setLayout(new BorderLayout());

                        JPanel genDialog = new JPanel(new FlowLayout(FlowLayout.CENTER));
                        JPanel genButtons = new JPanel(new FlowLayout(FlowLayout.CENTER));

                        JLabel genConfirm = new JLabel("Board is empty. Generate new board?");
                        genConfirm.setFont(genConfirm.getFont().deriveFont(Font.PLAIN, 16F));
                        genDialog.add(genConfirm);

                        JButton generate = new JButton("Generate");
                        generate.setFont(generate.getFont().deriveFont(Font.PLAIN, 16F));
                        Action generateAction = new AbstractAction("Generate") {
                            @Override
                            public void actionPerformed(ActionEvent e) {
                                // TODO: Generate board based on difficulty

                                generator.dispose();
                                timer.start();
                                playButton.setText("Stop");
                            }
                        };
                        generate.setAction(generateAction);
                        genButtons.add(generate);

                        JButton cancel = new JButton("Cancel");
                        cancel.setFont(cancel.getFont().deriveFont(Font.PLAIN, 16F));
                        Action cancelAction = new AbstractAction("Cancel") {
                            @Override
                            public void actionPerformed(ActionEvent e) {
                                generator.dispose();
                            }
                        };
                        cancel.setAction(cancelAction);
                        genButtons.add(cancel);

                        generator.add(genDialog, BorderLayout.CENTER);
                        generator.add(genButtons, BorderLayout.SOUTH);

                        generator.pack();
                        generator.setLocationRelativeTo(null);
                        generator.setVisible(true);
                    }else if(!canSolve){
                        System.out.println("Not enough nums");
                    }else{
                        timer.start();
                        playButton.setText("Stop");
                    }
                    // create number of potential candidates for each cell and solve board.
                    // one of many solutions from sudokuoftheday.com
                    // SudokuSolver.singleCandidate();
                    // Start the game
                }
            }
        };
        playButton.setAction(playAction);
        playButton.setFont(playButton.getFont().deriveFont(18F));
        game.add(playButton);
        game.add(timerLabel);

        // Solve button and event listener. Calls Board.solve() from SudokuSolver.java
        JButton solveBtn = new JButton("Solve");
        solveBtn.setFont(solveBtn.getFont().deriveFont(18F));
        Action solveAction = new AbstractAction("Solve") {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(!restrictKeyboard){
                    validation = false;
                    sudoBoard.setGUI(tfCells);
                    sudoBoard.solve(sudoBoard.getBoard());
                    validation = true;
                }
                lastField.setBackground(selectColor);
                lastField.requestFocus();
            }
        };
        solveBtn.setAction(solveAction);
        control.add(solveBtn);

        // Board reset button and event listener
        JButton resetBtn = new JButton("Reset");
        resetBtn.setFont(resetBtn.getFont().deriveFont(Font.PLAIN, 18F));

        //Keyboard shortcut for reset button(Ctrl+R)
        Action resetAction = new AbstractAction("Reset") {
            @Override
            public void actionPerformed(ActionEvent e) {
                validation = false;
                for (JTextField[] tfCell : tfCells) {
                    for (JTextField aTfCell : tfCell) {
                        aTfCell.setText("");
                    }
                }
                validation = true;
                selectColor = happyColor;
                restrictKeyboard = false;
                lastField.setText("");
                lastField.setBackground(selectColor);
                lastField.requestFocus();
                playButton.setText("Begin game!");
                timer.stop();
                timerLabel.setText("Timer 0:00");
            }
        };
        resetBtn.setAction(resetAction);
        control.add(resetBtn);

        // Crosshair enabler button and event listener
        // TODO: Move crosshair button to settings
        JButton crosshairBtn = new JButton("Crosshair: Enabled ");
        crosshairBtn.setFont(crosshairBtn.getFont().deriveFont(Font.PLAIN, 18F));
        crosshairBtn.addActionListener(e -> {
            if (!crosshairEnabled) {
                crosshairBtn.setText("Crosshair: Enabled ");
                crosshairEnabled = true;
                lastField.requestFocus();
            } else {
                crosshairBtn.setText("Crosshair: Disabled");
                crosshairEnabled = false;
                lastField.setBackground(selectColor);
                lastField.requestFocus();
            }
        });

        control.add(crosshairBtn);

        // Menu modifiers
        // Solve action/menuItem declaration and modification
        solveAction.putValue(Action.NAME, "Solve Board");
        solveItem.setAction(solveAction);
        solveItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, KeyEvent.CTRL_DOWN_MASK));
        fileMenu.add(solveItem);

        // Reset action/menuItem declaration and modification
        resetAction.putValue(Action.NAME, "Reset Board");
        resetItem.setAction(resetAction);
        resetItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_R, KeyEvent.CTRL_DOWN_MASK));
        fileMenu.add(resetItem);

        // Exit action/menuItem declaration and modification
        Action exitAction = new AbstractAction("Exit") {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        };
        exitItem.setAction(exitAction);
        exitItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Q, KeyEvent.CTRL_DOWN_MASK));
        fileMenu.add(exitItem);

        // Undo action/menuItem declaration and modification
        Action undoAction = new AbstractAction("Undo") {
            @Override
            public void actionPerformed(ActionEvent e) {
                //noinspection CatchMayIgnoreException
                try {
                    undo.undo();
                    lastField.requestFocus();
                    lastField.setBackground(selectColor);
                } catch (CannotUndoException ex) {
                    System.err.println(ex);
                }
            }
        };
        undoItem.setAction(undoAction);
        undoItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Z, KeyEvent.CTRL_DOWN_MASK));
        editMenu.add(undoItem);

        // Redo action/menuItem declaration and modification
        Action redoAction = new AbstractAction("Redo") {
            @Override
            public void actionPerformed(ActionEvent e) {
                //noinspection CatchMayIgnoreException
                try {
                    undo.redo();
                    lastField.requestFocus();
                    lastField.setBackground(selectColor);
                } catch (CannotRedoException ex) {
                    System.err.println(ex);
                }
            }
        };
        redoItem.setAction(redoAction);
        redoItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Y, KeyEvent.CTRL_DOWN_MASK));
        editMenu.add(redoItem);

        // Help action/menu declaration and modification
        helpFrame.setAlwaysOnTop(true);
        helpFrame.setUndecorated(true);
        helpFrame.setBackground(new Color(0, 0, 0, 0));
        helpPanel.setBackground(new Color(0, 0, 0, 0));
        helpLabel.setOpaque(false);
        helpLabel.setText("Press Key");
        helpLabel.setFont(helpLabel.getFont().deriveFont(Font.BOLD, 20));
        helpLabel.setForeground(Color.BLACK);
        helpPanel.add(helpLabel);
        helpFrame.add(helpPanel);
        helpFrame.pack();
        helpFrame.setLocationRelativeTo(null);
        Action helpAction = new AbstractAction("Help") {
            @Override
            public void actionPerformed(ActionEvent e) {
                helpFrame.setVisible(true);
                helpFrame.requestFocus();
                helpFrame.addKeyListener(new KeyAdapter() {
                    @Override
                    public void keyTyped(KeyEvent e) {
                        helpFrame.setVisible(false);
                        requestFocus();
                    }
                });
            }
        };
        helpItem.setAction(helpAction);
        helpItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_H, KeyEvent.ALT_DOWN_MASK));
        helpMenu.add(helpItem);

        // About action/menu declaration and modification
        Action aboutAction = new AbstractAction("About"){
            @Override
            public void actionPerformed(ActionEvent e) {
                aboutLabel.setText("Made by Christopher Gifford 2018");
                aboutLabel.setFont(aboutLabel.getFont().deriveFont(Font.BOLD, 16));
                aboutDialog.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 18));
                aboutDialog.setSize(60, 100);
                aboutDialog.setLocationRelativeTo(null);
                aboutDialog.add(aboutLabel);
                aboutDialog.setVisible(true);
            }
        };
        aboutItem.setAction(aboutAction);
        aboutItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_A, KeyEvent.ALT_DOWN_MASK));
        helpMenu.add(aboutItem);
        mBar.add(fileMenu);
        mBar.add(editMenu);
        mBar.add(helpMenu);
        setJMenuBar(mBar);

        setTitle("Chris Gifford Sudoku Solver"); // Window name
        setSize(new Dimension(board.getWidth(), board.getHeight() + 80)); // Frame dimension adjustment
        setLocationRelativeTo(null); // Opens window in center of screen
        setLayout(new BorderLayout());
        add(board, BorderLayout.CENTER); // Sudoku board insertion
        add(control, BorderLayout.SOUTH); // Control panel insertion
        add(game, BorderLayout.NORTH); // Game insertion
        requestFocus();
    }

    // Crosshair/cell selection color modifier
    public void setCellColor(Color color, JTextField[][] tfCells, int altI, int altJ) {
        if (crosshairEnabled) {
            for (JTextField[] tfCell : tfCells) {
                tfCell[altJ].setBackground(color);
            }
            for (int j = 0; j < tfCells[0].length; j++) {
                tfCells[altI][j].setBackground(color);
            }
        } else {
            tfCells[altI][altJ].setBackground(color);
        }
        lastField = tfCells[altI][altJ];
    }

    // Keyboard limiter. If inserted number is invalid, all keyboard events are interrupted
    public void numValidation(Board sudoBoard, JTextField[][] tfCells, int altI, int altJ) {
        if (!tfCells[altI][altJ].getText().isEmpty()) {
            int num = 0;
            //noinspection CatchMayIgnoreException
            try {
                num = Integer.parseInt(tfCells[altI][altJ].getText());
            } catch (NumberFormatException ex) {
                System.err.println(ex);
            }

            if (sudoBoard.numValidation(sudoBoard.getBoard(), altI, altJ, num)) {
                selectColor = happyColor;
                setCellColor(selectColor, tfCells, altI, altJ);
                restrictKeyboard = false;

            } else {
                selectColor = errorColor;
                setCellColor(selectColor, tfCells, altI, altJ);
                restrictKeyboard = true;

            }
        } else if (tfCells[altI][altJ].getText().isEmpty()) {
            selectColor = happyColor;
            setCellColor(selectColor, tfCells, altI, altJ);
            restrictKeyboard = false;
        }
    }

    public static void main(String[] args) {
        SudokuGUI gui = new SudokuGUI();
        gui.setVisible(true);
    }
}

// TextField length limiter.
// If the limit is reached and subsequent character insertion occurs, the current cell is overwritten
class LimitedTextField extends JTextField {
    private int limit;

    LimitedTextField(int limit) {
        super();
        this.limit = limit;
    }

    @Override
    protected Document createDefaultModel() {
        return new UpperCaseDocument(this);
    }

    private static class UpperCaseDocument extends PlainDocument {
        LimitedTextField tf;

        UpperCaseDocument(LimitedTextField tf) {
            this.tf = tf;
        }

        @Override
        public void insertString(int offs, String str, AttributeSet a)
                throws BadLocationException {
            if (str == null)
                return;

            if ((getLength() + str.length()) <= tf.limit) {
                if (Character.isDigit(str.charAt(0)) && !str.equals("0")) {
                    super.insertString(offs, str, a);
                }
            } else if ((getLength() + str.length()) > tf.limit) {
                tf.setText(str);
            }
        }
    }
}