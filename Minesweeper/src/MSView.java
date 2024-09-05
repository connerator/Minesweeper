import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.Duration;
import java.time.Instant;

public class MSView extends JFrame {

    private Model board;
    private int difficulty;

    private ColorPanel[][] panels;
    private Container pane;
    private JPanel boardPanel;
    private JPanel controlPanel;
    private JPanel botPanel;
    private JButton easyGameButton;
    private JButton mediumGameButton;
    private JButton hardGameButton;
    private JButton botMove;
    private JButton hint;

    private boolean hintsOn;

    private Instant start;
    private Instant end;
    private Duration time;


    public MSView(int difficulty) {
        hintsOn = false;
        board = new Model(difficulty);
        this.difficulty = difficulty;
        panels = new ColorPanel[board.getNumRows()][board.getNumCols()];
        for (int r = 0; r < board.getNumRows(); r++) {
            for (int c = 0; c < board.getNumCols(); c++) {
                panels[r][c] = new ColorPanel(Color.GREEN, r, c, this);
                panels[r][c].setSize(100, 100);
            }
        }
        boardPanel = new JPanel();
        boardPanel.setBackground(new Color(20, 80, 20));
        boardPanel.setLayout(new GridLayout(board.getNumRows(), board.getNumCols()));
        for (int r = 0; r < board.getNumRows(); r++) {
            for (int c = 0; c < board.getNumCols(); c++)
                boardPanel.add(panels[r][c]);
        }

        controlPanel = new JPanel();
        controlPanel.setBackground(new Color(20, 80, 20));

        botPanel = new JPanel();
        botPanel.setBackground(new Color(20, 80, 20));

        easyGameButton = new JButton("EASY");
        easyGameButton.addActionListener(new ButtonListener());
        mediumGameButton = new JButton("MEDIUM");
        mediumGameButton.addActionListener(new ButtonListener());
        hardGameButton = new JButton("HARD");
        hardGameButton.addActionListener(new ButtonListener());
        botMove = new JButton("BOT MOVE");
        botMove.addActionListener(new ButtonListener());
        hint = new JButton("HINTS: OFF");
        hint.addActionListener(new ButtonListener());

        controlPanel.add(easyGameButton);
        controlPanel.add(mediumGameButton);
        controlPanel.add(hardGameButton);
        botPanel.add(botMove);
        botPanel.add(hint);

        setTitle("Minesweeper");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        pane = getContentPane();
        pane.add(boardPanel, BorderLayout.CENTER);
        pane.add(controlPanel, BorderLayout.NORTH);
        pane.add(botPanel, BorderLayout.SOUTH);


        pack();
        resetAllPanels();
        setVisible(true);
        System.out.println(board);

    }

    public int getDifficulty() {
        return difficulty;
    }


    public void play(int r, int c) {
        if (board.getFirstMove()) {
            start = Instant.now();
        }
        board.play(r, c);
        if (hintsOn) flag();
        resetAllPanels();
        repaint();
        System.out.println(board);
        checkGameOver();
    }

    public boolean checkGameOver() {
        if (board.gameOver() == 1) {
            end = Instant.now();
            time = Duration.between(start, end);
            if (hintsOn) toggleHints();
            resetAllPanels();
            uncoverMines();
            JOptionPane.showMessageDialog(null, "You won :)\nTime: " + time.getSeconds() + "s");
            newGame(difficulty);
            return true;
        } else if (board.gameOver() == -1) {
            if (hintsOn) toggleHints();
            resetAllPanels();
            uncoverMines();
            JOptionPane.showMessageDialog(null, "You lost :(");
            newGame(difficulty);
            return true;
        }
        return false;
    }

    public void uncoverMines() {
        for (int r = 0; r < board.getNumRows(); r++) {
            for (int c = 0; c < board.getNumCols(); c++) {
                if (board.getBoard()[r][c].isMine()) {
                    board.getBoard()[r][c].setCovered(false);
                    panels[r][c].setPanel(r, c, board.neighbors(r, c));
                }
            }
        }
        repaint();
    }

    public void resetAllPanels() {
        for (int r = 0; r < board.getNumRows(); r++) {
            for (int c = 0; c < board.getNumCols(); c++) {
                panels[r][c].setPanel(r, c, board.neighbors(r, c));
                if (board.getFirstMove() || !hintsOn)
                    panels[r][c].setBorder(null);
                else flag();
            }
        }
        repaint();
        System.out.println(board);
    }

    public void newGame(int difficulty) {
        if (this.difficulty != difficulty) {
            setVisible(false);
            new MSView(difficulty);
        } else {
            repaint();
            board = new Model(difficulty);
            if (hintsOn) toggleHints();
            resetAllPanels();
        }
    }

    public void flag() {
        board.flag();
        for (int r = 0; r < board.getNumRows(); r++) {
            for (int c = 0; c < board.getNumCols(); c++) {
                if (board.getBoard()[r][c].isFlagged())
                    panels[r][c].flag();
            }
        }
    }

    public boolean getHintsOn() {
        return hintsOn;
    }

    public void toggleHints() {
        if (hintsOn) {
            hint.setText("HINTS: OFF");
            hintsOn = !hintsOn;
            resetAllPanels();
        } else {
            hint.setText("HINTS: ON");
            hintsOn = !hintsOn;
            flag();
        }
    }

    private class ButtonListener implements ActionListener {

        public void actionPerformed(ActionEvent e) {
            Object source = e.getSource();
            if (source.equals(easyGameButton))
                newGame(1);
            else if (source.equals(mediumGameButton)) {
                newGame(2);
            } else if (source.equals(hardGameButton)) {
                newGame(3);
            } else if (source.equals(botMove)) {
                if (board.getFirstMove()) {
                    start = Instant.now();
                }
                board.aiMove();
                resetAllPanels();
                checkGameOver();
            } else if (source.equals(hint)) {
                toggleHints();
            }
        }
    }

    public Model getBoard() {
        return board;
    }

    public static void main(String[] args) {
        MSView theGUI = new MSView(1);
    }

}
