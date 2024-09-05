import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class ColorPanel extends JPanel {

    private int row;
    private int col;
    Color bgColor;
    MSView theView;
    private JLabel num;

    public ColorPanel(Color bgColor, int row, int col, MSView theView) {
        this.bgColor = bgColor;
        setBackground(bgColor);
        this.row = row;
        this.col = col;
        this.theView = theView;
        this.num = new JLabel("");
        setPreferredSize(new Dimension(100, 100));
        num.setHorizontalAlignment(JLabel.CENTER);
        num.setVerticalAlignment(JLabel.CENTER);
        num.setForeground(Color.GRAY);
        if (theView.getDifficulty() == 1)
            num.setFont(new Font("Arial", Font.BOLD, 50));
        else if (theView.getDifficulty() == 2) {
            num.setFont(new Font("Arial", Font.BOLD, 35));
        } else {
            num.setFont(new Font("Arial", Font.BOLD, 25));
        }

        setLayout(new GridLayout(1, 1));
        add(this.num);
        this.addMouseListener(new PanelListener());
    }

    public void paintComponent(Graphics g) {
        setBackground(bgColor);
        super.paintComponent(g);
    }

    public void setPanel(int r, int c, int neighbors) {
        Cell cell = theView.getBoard().getBoard()[r][c];
        if (cell.isCovered()) {
            if ((r + c) % 2 == 0)
                bgColor = new Color(117, 235, 0);
            else
                bgColor = new Color(127, 245, 0);
            num.setText("");
        } else if (cell.isMine()) {
            if ((r + c) % 2 == 0)
                bgColor = new Color(235, 0, 0);
            else
                bgColor = new Color(255, 0, 0);
            num.setText("");
        } else {
            if ((r + c) % 2 == 0)
                bgColor = new Color(225, 225, 225);
            else
                bgColor = new Color(235, 235, 235);
            num.setText("");
            if (neighbors > 0) {
                num.setText(String.valueOf(neighbors));
                num.setForeground(setTextColor(neighbors));
            }
        }
        repaint();
    }

    public Color setTextColor(int neighbors) {
        if (neighbors == 1) {
            return new Color(65, 105, 225); // blue
        }
        if (neighbors == 2) {
            return new Color(54, 155, 54); // green
        }
        if (neighbors == 3) {
            return Color.RED; // red
        }
        return new Color(139, 0, 139); // purple
    }

    public void flag() {
        /*
        int d1 = 4;
        int d2 = 4;
        int d3 = 4;
        int d4 = 4;
        Cell[][] board = theView.getBoard().getBoard();
        if (theView.getBoard().isValid(row - 1, col) && board[row - 1][col].isFlagged()) d1 = 2;
        if (theView.getBoard().isValid(row, col - 1) && board[row][col - 1].isFlagged()) d2 = 2;
        if (theView.getBoard().isValid(row + 1, col) && board[row + 1][col].isFlagged()) d3 = 2;
        if (theView.getBoard().isValid(row, col + 1) && board[row][col + 1].isFlagged()) d4 = 2;
        setBorder(BorderFactory.createMatteBorder(d1, d2, d3, d4, new Color(255, 0, 0)));
         */
        int thickness = 0;
        if (theView.getDifficulty() == 1) thickness = 10;
        else if (theView.getDifficulty() == 2) thickness = 7;
        else if (theView.getDifficulty() == 3) thickness = 5;
        if ((row + col) % 2 == 0)
            setBorder(BorderFactory.createLineBorder(new Color(235, 0, 0), thickness));
        else
            setBorder(BorderFactory.createLineBorder(new Color(255, 0, 0), thickness));

    }

    private class PanelListener extends MouseAdapter {
        boolean flagged = false;

        public void mousePressed(MouseEvent e) {
            theView.play(row, col);
            repaint();
        }

        public void mouseEntered(MouseEvent e) {
            if (getBorder() == null) {
                setBorder(BorderFactory.createLineBorder(new Color(20, 80, 20)));
                flagged = false;
            } else
                flagged = true;

        }

        public void mouseExited(MouseEvent e) {
            if (!flagged) {
                setBorder(null);
            }

        }

    }
}
