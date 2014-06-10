/* 
 * This file is part of 2048FXAuto
 * Copyright (C) 2014 Martino Pilia <m.pilia@gmail.com>
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

package giocatoreAutomatico;

import java.util.ArrayList;

/**
 *
 * @author martino
 */
public class Row {

    ArrayList<Integer> tiles;
    boolean empty;
    int gridSize = 4;

    /**
     * row with the tile values in order
     *
     * @param row
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
     *
     */
    public Row() {
        this.tiles = new ArrayList<>(gridSize);
        empty = true;
    }

    boolean validRowMove(int dir) {
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

    private boolean hasFreeSpace(int pos, int dir) {        
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

    private boolean hasSameTile(int pos, int dir, int val) {
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

    void compact(int dir) {
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

    void join(int dir) {
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
     * @param dir Direction to move: 0 for left (or up), 1 for right (or down).
     */
    void moveRow(int dir) {
        if (this.validRowMove(dir)) {
            this.compact(dir);
            this.join(dir);
            this.compact(dir);
        }
    }

    @Override
    public String toString() {
        return this.tiles.toString();
    }

    /**
     *
     * @param i
     */
    public void add(Integer i) {
        this.tiles.add(i);
        if (!i.equals(-1)) {
            this.empty = false;
        }
    }

    /**
     *
     * @param i
     * @return
     */
    public Integer get(int i) {
        return this.tiles.get(i);
    }
}
