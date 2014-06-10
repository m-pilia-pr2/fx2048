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

/**
 * This class represents a location in the game grid.
 * @author bruno.borges@oracle.com
 */
public class Location {

    private final int x;
    private final int y;

    /**
     * This is the constructor for a Location object.
     * @param x The x coord.
     * @param y The y coord.
     */
    public Location(int x, int y) {
        this.x = x;
        this.y = y;
    }

    /**
     * Returns a new location, located at a specified distance from the
     * current location.
     * @param direction The desired direction for the new location.
     * @return A new Location object, located at a specified distance from the
     * current location.
     */
    public Location offset(Direction direction) {
        return new Location(x + direction.getX(), y + direction.getY());
    }

    /**
     * Get the x coord for current location.
     * @return The x coord for current location.
     */
    public int getX() {
        return x;
    }

    /**
     * Get the y coord for current location.
     * @return The y coord for current location.
     */
    public int getY() {
        return y;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return "Location{" + "x=" + x + ", y=" + y + '}';
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        int hash = 7;
        hash = 97 * hash + this.x;
        hash = 97 * hash + this.y;
        return hash;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Location other = (Location) obj;
        if (this.x != other.x) {
            return false;
        }
        return this.y == other.y;
    }

    /**
     * Return the vertical cell position in the game layout (layoutY 
     * property). The position is automatically computed, based on the 
     * Location's position in the game grid and on the size of the cells,
     * passed as a parameter.
     * @param CELL_SIZE Desired cell size.
     * @return The cell vertical position in the game layout 
     * (layoutY property). 
     */
    public double getLayoutY(int CELL_SIZE) {
        if (y == 0) {
            return CELL_SIZE / 2;
        }
        return (y * CELL_SIZE) + CELL_SIZE / 2;
    }

    /**
     * Return the horizontal cell position in the game layout (layoutX 
     * property). The position is automatically computed, based on the 
     * Location's position in the game grid and on the size of the cells,
     * passed as a parameter.
     * @param CELL_SIZE Desired cell size.
     * @return The cell horizontal position in the game layout 
     * (layoutX property). 
     */
    public double getLayoutX(int CELL_SIZE) {
        if (x == 0) {
            return CELL_SIZE / 2;
        }
        return (x * CELL_SIZE) + CELL_SIZE / 2;
    }

    /**
     * Validates the gridSize parameter.
     * @param gridSize Desired value for the grid size.
     * @return <code>true</code> if the argument is valid, 
     * <code>false</code> otherwise.
     */
    public boolean isValidFor(int gridSize) {
        return x >= 0 && x < gridSize && y >= 0 && y < gridSize;
    }

    /**
     * Validates a movement on the gaming grid.
     * @param direction Desired direction vector for the movement.
     * @param gridSize Size of the current gaming grid.
     * @return <code>true</code> if the movement is valid, 
     * <code>false</code> otherwise.
     */
    public boolean validFor(Direction direction, int gridSize) {
        switch (direction) {
            case UP:
                return x >= 0;
            case RIGHT:
                return y < gridSize;
            case DOWN:
                return x < gridSize;
            case LEFT:
                return y >= 0;
        }
        return false;
    }

}
