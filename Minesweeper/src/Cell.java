public class Cell {

    private boolean mine;
    private boolean covered;
    private boolean flagged;

    public Cell() {
        mine = false;
        covered = true;
        flagged = false;
    }

    public Cell(boolean mine) {
        this.mine = mine;
        covered = true;
        flagged = false;
    }

    public Cell(boolean mine, boolean covered) {
        this.mine = mine;
        this.covered = covered;
        flagged = false;
    }

    public boolean isFlagged() {
        return flagged;
    }

    public void setFlagged(boolean flagged) {
        this.flagged = flagged;
    }

    public boolean isMine() {
        return mine;
    }

    public boolean isCovered() {
        return covered;
    }

    public void setMine(boolean mine) {
        this.mine = mine;
    }

    public void setCovered(boolean covered) {
        this.covered = covered;
    }

    public String toString() {
        if (flagged) return "F";
        if (covered) {
            return "#";
        } else if (mine) {
            return "X";
        } else {
            return ".";
        }
    }


}
