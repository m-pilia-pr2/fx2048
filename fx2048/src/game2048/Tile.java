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

import java.util.Random;
import javafx.geometry.Pos;
import javafx.scene.control.Label;

/**
 * This class describes a tile for the game interface. Each tile is 
 * characterizad by a position and a value.
 * @author bruno.borges@oracle.com
 */
public class Tile extends Label {

    private Integer value;
    private Location location;
    private Boolean merged;

    /**
     * This is the constructor for the Tile class. Creates a new tile with the 
     * desired value.
     * @param value Desired value.
     */
    private Tile(Integer value) {
        // TODO adjust size to be more... err... responsive? :)
        final int squareSize = GameManager.CELL_SIZE - 13;
        setMinSize(squareSize, squareSize);
        setMaxSize(squareSize, squareSize);
        setPrefSize(squareSize, squareSize);
        setAlignment(Pos.CENTER);

        this.value = value;
        this.merged = false;
        setText(value.toString());
        getStyleClass().add("tile-" + value);
    }

    /**
     * This method returns a new tile with a random value. The chanches are 90%
     * for 2 value and 10% for 4 value.
     * @return A new tile with random value.
     */
    public static Tile newRandomTile() {
        int value = new Random().nextDouble() < 0.9 ? 2 : 4;
        return new Tile(value);
    }

    /**
     * This method returns a new tile filled with the desired value.
     * @param value Desired value.
     * @return A new tile with the desired value.
     */
    public static Tile newTile(int value) {
        return new Tile(value);
    }

    /**
     * Method to merge two tiles, updating the css style and the value.
     * @param another Other tile to be merged with.
     */
    public void merge(Tile another) {
        getStyleClass().remove("tile-" + value);
        this.value += another.getValue();
        setText(value.toString());
        merged = true;
        getStyleClass().add("tile-" + value);
    }

    /**
     * This method returns the tile value.
     * @return The tile value.
     */
    public Integer getValue() {
        return value;
    }

    /**
     * This method returns the tile location.
     * @return The tile location.
     */
    public Location getLocation() {
        return location;
    }

    /**
     * This method sets the tile location.
     * @param location The location desired for the tile.
     */
    public void setLocation(Location location) {
        this.location = location;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return "Tile{" + "value=" + value + ", location=" + location + '}';
    }

    /**
     * This method returns a boolean describing the tile merge status. It 
     * @return <code>true</code> if the tile is merged, 
     * <code>false</code> otherwise.
     */
    public boolean isMerged() {
        return merged;
    }

    /**
     * Set the merge status for the current tile as false.
     */
    public void clearMerge() {
        merged = false;
    }

    /**
     * This method controls if two tile can be merged.
     * @param anotherTile Other tile to be merged with.
     * @return <code>true</code> if the tiles can be merged, 
     * <code>false</code> otherwise.
     */
    public boolean isMergeable(Tile anotherTile) {
        return anotherTile != null && getValue().equals(anotherTile.getValue());
    }
}
