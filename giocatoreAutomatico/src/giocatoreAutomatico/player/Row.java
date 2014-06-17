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
 */ 

package giocatoreAutomatico.player;

import java.util.ArrayList;

/**
 * This class provides a Row object representing an array of ordered tiles. 
 * It's intended to be used in a player-side re-implementation of the grid, in
 * order to calculate movements for the IA. It may represent a row or a column
 * of the grid. This code has not been optimized, ArrayList should be 
 * substituted with simple array, for performace purpose and cleaner coding.
 * @author Martino Pilia
 */
public class Row {

    ArrayList<Integer> tiles;
    boolean empty;
    int gridSize = 4;

    /**
     * This is the constructorn for the Row class. It builds an empty row.
     */
    public Row() {
        this.tiles = new ArrayList<>(gridSize);
        empty = true;
    }

    /**
     * This is the constructor for the Row class. It builds a row initialized
     * with the tiles passed in the argument.
     * @param row An ArrayList containing the tiles for the row.
     */
    public Row(ArrayList<Integer> row) {
        this.tiles = new ArrayList<>(gridSize);
        this.tiles.addAll(row);
        empty = true;
        for (int i = 0; i < gridSize; i++) {
            if (!this.tiles.get(i).equals(-1)) {
                empty = false;
            }
        }
        //System.out.println(this.tiles.toString());
    }

    /**
     * This method checks if a move is valid along the row.
     * @param dir <code>0</code> for up or left, <code>1</code> for 
     * down or right.
     * @return <code>true</code> if the move is valid,
     * <code>false</code> otherwise.
     */
    public boolean validRowMove(int dir) {
        if (empty) {
            return false;
        }

        for (int i = 0; i < gridSize; i++) {
            if (!tiles.get(i).equals(-1)) {
                if (this.hasSameTile(i, dir, tiles.get(i))) {
                    return true;
                }
            }
        }

        for (int i = 0; i < gridSize; i++) {
            if (!this.tiles.get(i).equals(-1)) {
                if (this.hasFreeSpace(i, dir)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * This method verifies if a tile has a free space to move where in the 
     * desired direction.
     * @param pos Index representing the tile to be moved in the row.
     * @param dir <code>0</code> for up or left, <code>1</code> for 
     * down or right.
     * @return <code>true</code> if the move is valid,
     * <code>false</code> otherwise.
     */
    public boolean hasFreeSpace(int pos, int dir) {        
        /*
         * Be careful, in the next block the search goes on opposite direction
         * than the previous case: for dir = 0 you search from gridSize -1 to 0
         * (and vice versa) because you are searching backward for free 
         * space *before* the tiles (previously you was searching forward
         * for joinable tiles
         */
        if (dir == 1) { // down/right
            if (pos < gridSize - 1 && tiles.get(pos + 1).equals(-1))
                return true;
        } else { // up/left
            if (pos > 0 && tiles.get(pos - 1).equals(-1))
                return true;
        }
        return false;
    }


    /**
     * This method verifies if a tile has a same tile to merge with in the 
     * next tile along the desired direction.
     * @param pos Index representing the tile to be moved in the row.
     * @param dir <code>0</code> for up or left, <code>1</code> for 
     * down or right.
     * @return <code>true</code> if the merge is possible,
     * <code>false</code> otherwise.
     */
    public boolean hasSameTile(int pos, int dir, int val) {
        if (dir == 0) { // down/right
            for (int i = pos + 1; i < gridSize; i++) {
                if (tiles.get(i).equals(-1)) {
                    continue;
                }
                if (tiles.get(i).equals(val)) {
                    return true;
                } else {
                    return false;
                }
            }
        } else { // up/left
            for (int i = pos - 1; i >= 0; i--) {
                if (tiles.get(i).equals(-1)) {
                    continue;
                }
                if (tiles.get(i).equals(val)) {
                    return true;
                } else {
                    return false;
                }
            }
        }
        return false;
    }

    /**
     * This method compacts the tiles along the desired direction.
     * @param dir <code>0</code> for up or left, <code>1</code> for 
     * down or right.
     */
    public void compact(int dir) {
        int lastFree = gridSize;
        if (dir == 0) { // to down/right
            for (int i = 0; i < gridSize; i++) {
                if (tiles.get(i).equals(-1)) {
                    lastFree = i;
                    break;
                }
            }
            if (lastFree == gridSize) {
                return;
            }
            for (int i = lastFree; i < gridSize; i++) {
                if (!tiles.get(i).equals(-1)) {
                    tiles.add(lastFree, tiles.get(i));
                    tiles.remove(lastFree + 1);
                    tiles.add(i, -1);
                    tiles.remove(i + 1);
                    this.compact(dir); // recursion ftw!
                    return;
                }
            }
        } else { // to down/right
            for (int i = gridSize - 1; i > 0; i--) {
                if (tiles.get(i).equals(-1)) {
                    lastFree = i;
                    break;
                }
            }
            if (lastFree == gridSize) {
                return;
            }
            for (int i = lastFree; i >= 0; i--) {
                if (!tiles.get(i).equals(-1)) {
                    tiles.add(lastFree, tiles.get(i));
                    tiles.remove(lastFree + 1);
                    tiles.add(i, -1);
                    tiles.remove(i + 1);
                    this.compact(dir); // recursion ftw!
                    return;
                }
            }
        }
    }

    /**
     * This method merge all the avaible tiles along the desired direction,
     * if possible.
     * @param dir <code>0</code> for up or left, <code>1</code> for 
     * down or right.
     */
    public void join(int dir) {
        if (dir == 0) { // right/down
            for (int i = 0; i < gridSize - 1; i++) {
                if (!tiles.get(i).equals(-1)
                        && tiles.get(i).equals(tiles.get(i + 1))) {
                    tiles.add(i, 2 * tiles.get(i));
                    tiles.remove(i + 1); // the previous left a surplus of 1 tile
                    tiles.remove(i + 1);
                    tiles.add(i + 1, -1);
                }
            }
        } else { // left/up
            for (int i = gridSize - 1; i > 0; i--) {
                if (!tiles.get(i).equals(-1)
                        && tiles.get(i).equals(tiles.get(i - 1))) {
                    tiles.add(i, 2 * tiles.get(i));
                    tiles.remove(i + 1); // idem
                    tiles.remove(i - 1);
                    tiles.add(i - 1, -1);
                }
            }
        }
    }

    /**
     * This method moves a row in the desired direction, following the 2048 game
     * rulws. The movement is obtained as a combination of a compact, a merge
     * (done only if possible) and another compact to fill void cells eventually
     * left by the merge.
     * @param dir <code>0</code> for up or left, <code>1</code> for 
     * down or right.
     */
    void moveRow(int dir) {
        if (this.validRowMove(dir)) {
            this.compact(dir);
            this.join(dir);
            this.compact(dir);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return this.tiles.toString();
    }

    /**
     * This method adds a tile with the desired value at the end of the row.
     * @param i Value of the tile to be added.
     */
    public void add(Integer i) {
        this.tiles.add(i);
        if (!i.equals(-1)) {
            this.empty = false;
        }
    }

    /**
     * This method gets the value of the i<sup>th</sup> tile in the row.
     * @param i Index of the desired tile.
     * @return The Integer value of the desired tile, or <code>null</code> if
     * the desired tile does not exists.
     */
    public Integer get(int i) {
        return this.tiles.get(i);
    }
}
