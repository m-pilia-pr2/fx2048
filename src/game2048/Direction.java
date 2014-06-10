package game2048;

import javafx.scene.input.KeyCode;

/**
 * This is an enum providing directions.
 * @author bruno.borges@oracle.com
 * @version 0.1a
 */
public enum Direction {

    UP(0, -1), RIGHT(1, 0), DOWN(0, 1), LEFT(-1, 0);

    private final int y;
    private final int x;

    /*
     * This is the enum constructor, each direction is represented 
     * as a couple of components along x and y axises.
     * @param x number of moves along x axis
     * @param y number of moves along y axis
     * @author bruno
     * @version 0.1a
     */
    Direction(int x, int y) {
        this.x = x;
        this.y = y;
    }

    /**
     * 
     */
    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    @Override
    public String toString() {
        return "Direction{" + "y=" + y + ", x=" + x + '}' + name();
    }

    public Direction goBack() {
        switch (this) {
            case UP:
                return DOWN;
            case DOWN:
                return UP;
            case LEFT:
                return RIGHT;
            case RIGHT:
                return LEFT;
        }
        return null;
    }

    public static Direction valueFor(KeyCode keyCode) {
        return valueOf(keyCode.name());
    }
}
