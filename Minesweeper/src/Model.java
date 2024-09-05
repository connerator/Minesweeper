import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;

public class Model {

    private Cell[][] board;
    private int numRows;
    private int numCols;
    private int numMines;

    private boolean firstMove = true;

    public Model(int difficulty) {
        if (difficulty == 1) {
            numRows = 8;
            numCols = 10;
            numMines = 10;
        } else if (difficulty == 2) {
            numRows = 14;
            numCols = 18;
            numMines = 40;
        } else {
            numRows = 20;
            numCols = 24;
            numMines = 99;
        }
        board = new Cell[numRows][numCols];
        for (int r = 0; r < numRows; r++) {
            for (int c = 0; c < numCols; c++) {
                board[r][c] = new Cell();
            }
        }

    }

    public void generateMines(int r, int c) {
        for (int i = 0; i < numMines; i++) {
            int randR = (int) (Math.random() * numRows);
            int randC = (int) (Math.random() * numCols);
            if (board[randR][randC].isMine() || Math.abs(r - randR) <= 1 && Math.abs(c - randC) <= 1) {
                i--;
                continue;
            }
            board[randR][randC].setMine(true);
        }
    }

    public int getNumRows() {
        return numRows;
    }

    public int getNumCols() {
        return numCols;
    }

    public Cell[][] getBoard() {
        return board;
    }

    public String toString() {
        StringBuilder output = new StringBuilder();
        for (int r = 0; r < numRows; r++) {
            for (int c = 0; c < numCols; c++) {
                if (!board[r][c].isCovered() && !board[r][c].isMine() && neighbors(r, c) != 0) {
                    output.append(neighbors(r, c)).append("  ");
                } else {
                    output.append(board[r][c]).append("  ");
                }
            }
            output.append("\n");
        }
        return output.toString();
    }

    public String rawBoard() {
        Cell[][] rawBoard = new Cell[numRows][numCols];
        for (int r = 0; r < numRows; r++) {
            for (int c = 0; c < numCols; c++) {
                rawBoard[r][c] = new Cell(board[r][c].isMine());
                rawBoard[r][c].setCovered(false);
            }
        }

        StringBuilder output = new StringBuilder();
        for (int r = 0; r < numRows; r++) {
            for (int c = 0; c < numCols; c++) {
                output.append(rawBoard[r][c]).append("  ");
            }
            output.append("\n");
        }
        return output.toString();
    }

    public boolean isValid(int r, int c) {
        return r >= 0 && r < numRows && c >= 0 && c < numCols;
    }

    public int neighbors(int r, int c) {
        int mines = 0;
        for (int row = r - 1; row <= r + 1; row++) {
            for (int col = c - 1; col <= c + 1; col++) {
                if ((row != r || col != c) && isValid(row, col) && board[row][col].isMine()) {
                    mines++;
                }
            }
        }
        return mines;
    }

    public boolean play(int r, int c) {
        if (!isValid(r, c)) return false;
        if (firstMove) {
            generateMines(r, c);
            firstMove = false;
            play(r, c);
        }
        if (board[r][c].isCovered()) {
            carveSpace(r, c);
            uncoverEdges();
            board[r][c].setCovered(false);
            return true;
        }
        return false;
    }

    public boolean getFirstMove() {
        return firstMove;
    }

    public void carveSpace(int r, int c) {
        if (isValid(r, c) && !board[r][c].isMine() && board[r][c].isCovered()) {
            if (neighbors(r, c) == 0) {
                board[r][c].setCovered(false);
                carveSpace(r + 1, c);
                carveSpace(r - 1, c);
                carveSpace(r, c + 1);
                carveSpace(r, c - 1);
                /*
                carveSpace(r + 1, c + 1);
                carveSpace(r + 1, c - 1);
                carveSpace(r - 1, c + 1);
                carveSpace(r - 1, c - 1);
                 */


            }
        }
    }

    public void uncoverEdges() {
        for (int r = 0; r < numRows; r++) {
            for (int c = 0; c < numCols; c++) {
                if (board[r][c].isCovered() && !board[r][c].isMine() && neighbors(r, c) > 0) {
                    loop1:
                    for (int r1 = r - 1; r1 <= r + 1; r1++) {
                        for (int c1 = c - 1; c1 <= c + 1; c1++) {
                            if ((r1 != r || c1 != c) && isValid(r1, c1) && !board[r1][c1].isCovered() && !board[r1][c1].isMine() && neighbors(r1, c1) == 0) {
                                board[r][c].setCovered(false);
                                break loop1;
                            }
                        }
                    }
                }
            }
        }
    }

    public int gameOver() {
        int count = 0;
        for (Cell[] r : board) {
            for (Cell c : r) {
                if (!c.isCovered()) {
                    if (c.isMine())
                        return -1;
                    count++;
                }
            }
        }
        if (count == (numRows * numCols) - numMines) return 1;
        else return 0;

    }

    public boolean aiMove() {
        if (firstMove) {
            int r = (int) (Math.random() * numRows);
            int c = (int) (Math.random() * numCols);
            generateMines(r, c);
            firstMove = false;
            play(r, c);
        }
        int row = -1;
        int col = -1;
        Cell cell;
        /*
        while (!play(row, col) && count < 10) {
            count++;
            loop1:
            for (int r = 0; r < numRows; r++) {
                for (int c = 0; c < numCols; c++) {
                    cell = board[r][c];
                    int mines = neighbors(r, c);
                    int grasses = 0;
                    if (!cell.isCovered() && mines != 0) { // if it's a numbered cell
                        for (int r1 = r - 1; r1 <= r + 1; r1++) {
                            for (int c1 = c - 1; c1 <= c + 1; c1++) {
                                if ((r1 != r || c1 != c) && isValid(r1, c1) && board[r1][c1].isCovered()) {
                                    grasses++;
                                }
                            }
                        }
                        int flagged = countFlagged(r, c);
                        if (mines == grasses) {
                            for (int r1 = r - 1; r1 <= r + 1; r1++) {
                                for (int c1 = c - 1; c1 <= c + 1; c1++) {
                                    if ((r1 != r || c1 != c) && isValid(r1, c1) && board[r1][c1].isCovered()) {
                                        board[r1][c1].setFlagged(true);
                                    }
                                }
                            }
                        } else if (grasses > mines && mines == flagged) {
                            for (int r1 = r - 1; r1 <= r + 1; r1++) {
                                for (int c1 = c - 1; c1 <= c + 1; c1++) {
                                    if ((r1 != r || c1 != c) && isValid(r1, c1) && board[r1][c1].isCovered() && !board[r1][c1].isFlagged()) {
                                        row = r1;
                                        col = c1;
                                        break loop1;
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
         */
        flag();
        loop1:
        for (int r = 0; r < numRows; r++) {
            for (int c = 0; c < numCols; c++) {
                cell = board[r][c];
                int mines = neighbors(r, c);
                if (!cell.isCovered() && mines != 0) {
                    int flags = countFlagged(r, c);
                    if (mines == flags) {
                        for (int r1 = r - 1; r1 <= r + 1; r1++) {
                            for (int c1 = c - 1; c1 <= c + 1; c1++) {
                                if ((r1 != r || c1 != c) && isValid(r1, c1) && board[r1][c1].isCovered() && !board[r1][c1].isFlagged()) {
                                    row = r1;
                                    col = c1;
                                    break loop1;
                                }
                            }
                        }
                    }

                }

            }
        }
        //System.out.println(row + " " + col);
        play(row, col);
        return row != -1;

    }

    public ArrayList<Cell> unflaggedList(int r, int c) {
        ArrayList<Cell> list = new ArrayList<>();
        for (int r1 = r - 1; r1 <= r + 1; r1++) {
            for (int c1 = c - 1; c1 <= c + 1; c1++) {
                if ((r1 != r || c1 != c) && isValid(r1, c1) && board[r1][c1].isCovered() && !board[r1][c1].isFlagged()) {
                    list.add(board[r1][c1]);
                }
            }
        }
        return list;
    }

    public int minesLeft(int r, int c) {
        int uncoveredMines = 0;
        for (int r1 = r - 1; r1 <= r + 1; r++) {
            for (int c1 = c - 1; c1 <= c + 1; c++) {
                if (isValid(r1, c1) && board[r1][c1].isCovered() && board[r1][c1].isFlagged()) {
                    uncoveredMines++;
                }
            }
        }
        return neighbors(r, c) - uncoveredMines;
    }

    public void advancedAIMove() {

        for (int r = 0; r < numRows; r++) {
            for (int c = 0; c < numCols; c++) {
                Cell cell = board[r][c];
                int mines = neighbors(r, c);
                if (!cell.isCovered() && mines != 0) {
                    ArrayList<Cell> list = unflaggedList(r, c);
                    int minesLeft = minesLeft(r, c);
                    for (int r1 = r - 2; r1 <= r + 2; r++) {
                        for (int c1 = c - 2; c1 <= c + 2; c++) {
                            if (isValid(r1, c1) && !board[r1][c1].isCovered() && neighbors(r1, c1) != 0) {
                                ArrayList<Cell> compareList = unflaggedList(r1, c1);
                                int compareMinesLeft = minesLeft(r1, c1);
                                

                            }
                        }
                    }
                }

            }
        }

    }

    public void flag() {
        Cell cell;
        for (int r = 0; r < numRows; r++) {
            for (int c = 0; c < numCols; c++) {
                cell = board[r][c];
                int mines = neighbors(r, c);
                int grasses = 0;
                if (!cell.isCovered() && mines != 0) { // if it's a numbered cell
                    for (int r1 = r - 1; r1 <= r + 1; r1++) {
                        for (int c1 = c - 1; c1 <= c + 1; c1++) {
                            if ((r1 != r || c1 != c) && isValid(r1, c1) && board[r1][c1].isCovered()) {
                                grasses++;
                            }
                        }
                    }
                    if (mines == grasses) {
                        for (int r1 = r - 1; r1 <= r + 1; r1++) {
                            for (int c1 = c - 1; c1 <= c + 1; c1++) {
                                if ((r1 != r || c1 != c) && isValid(r1, c1) && board[r1][c1].isCovered()) {
                                    board[r1][c1].setFlagged(true);
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    public int countFlagged(int r, int c) {
        int flagged = 0;
        for (int r1 = r - 1; r1 <= r + 1; r1++) {
            for (int c1 = c - 1; c1 <= c + 1; c1++) {
                if ((r1 != r || c1 != c) && isValid(r1, c1) && board[r1][c1].isCovered() && board[r1][c1].isFlagged()) {
                    flagged++;
                }
            }
        }
        return flagged;
    }

    public static void main(String[] args) {
        Model board;
        int wins = 0;
        int n = 100000;
        Instant start = Instant.now();
        Instant end;
        Duration time;

        for (int i = 0; i < n; i++) {
            board = new Model(2);
            while (board.gameOver() == 0 && board.aiMove()) {
                //System.out.println(board);
            }
            if (board.gameOver() == 1) {
                wins++;
                break;
            }
        }
        end = Instant.now();
        time = Duration.between(start, end);
        System.out.println(((double) wins / n) * 100 + "%");
        System.out.println(time);

        /*
        % solved:
        Easy: 53.18%, n = 1,000,000, t = 5 min, 5 sec
        Medium: 18.02%, n = 500,000, t = 17 min, 42 sec
        Hard: 0.1300%, n = 100,000, t = 7 min, 54 s

       Time to solve 1:
       Easy: ~0.01s
       Medium:~0.1s
       Hard: ~2-9s
         */
    }

}
