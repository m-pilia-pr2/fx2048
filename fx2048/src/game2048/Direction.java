/* 
 * This file is part of 2048FXAuto
 * Copyright (C) 2014 Martino Pilia <git.m.pilia@gmail.com>
 *
 *   This program is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU General Public License as published by
 *   the Free Software Foundation, either version 3 of the License, or
 *   (at your option) any later version.
 *
 *   This program is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU General Public License for more details.
 *
 *   You should have received a copy of the GNU General Public License
 *   along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * 
 * This file incorporates work from the project 2048FX 
 * https://github.com/brunoborges/fx2048
 * covered by the following copyright and permission notice:
 * 
 *   Copyright (C) 2014 Bruno Borges <bruno.borges@oracle.com>
 *
 *   This program is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU General Public License as published by
 *   the Free Software Foundation, either version 3 of the License, or
 *   (at your option) any later version.
 *
 *   This program is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU General Public License for more details.
 *
 *   You should have received a copy of the GNU General Public License
 *   along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */ 

package game2048;

import javafx.scene.input.KeyCode;

/**
 * This is an enum providing directions for the game interface.
 * @author bruno.borges@oracle.com
 */
public enum Direction {

    UP(0, -1), RIGHT(1, 0), DOWN(0, 1), LEFT(-1, 0);

    private final int y;
    private final int x;

    /**
     * This is the enum constructor. Each direction is represented as a couple 
     * of components along x and y axises.
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
     * @return The x component of the direction vector.
     */
    public int getX() {
        return x;
    }

    /**
     * @return the y component of the direction vector.
     */
    public int getY() {
        return y;
    }

    @Override
    public String toString() {
        return "Direction{" + "y=" + y + ", x=" + x + '}' + name();
    }

    /**
     * Return a direction vector oppostite to the selected object
     * @return The opposite direction.
     */
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

    /**
     * Returns the value for the KeyCode passed as parameter.
     * @param keyCode Reference to a javafx.scene.input.KeyCode object.
     * @return The value of the keyCode passed as a parameter.
     */
    public static Direction valueFor(KeyCode keyCode) {
        return valueOf(keyCode.name());
    }
}
