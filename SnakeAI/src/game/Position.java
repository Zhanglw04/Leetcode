package game;

public class Position {
    // x and y represent the row and column numbers of the point in the map array.
    public int x;
    public int y;

    public Position(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getX() {
        return x;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getY() {
        return y;
    }

    public Position sub(Position p) {
        return new Position(this.x-p.x, this.y-p.y);
    }

    public Position add(Position p) {
        return new Position(this.x+p.x, this.y+p.y);
    }

    public boolean equal(Position p) {
        return p.x == this.x && p.y == this.y;
    }
}
