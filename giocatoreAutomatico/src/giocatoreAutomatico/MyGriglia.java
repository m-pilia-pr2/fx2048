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

package giocatoreAutomatico;

import game2048.Location;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

/**
 * This class represents a game grid. It's intended to be used in a player-side 
 * re-implementation of the grid, in order to calculate movements for the IA. 
 * The grid is transformated in an array of Row objects when needed.
 * @author Martino Pilia
 */
public class MyGriglia extends HashMap<game2048.Location,Integer> implements Griglia {

    private int gridSize;
    
    /**
     * This is the constructor for the class. The default size for
     * the grid is 4.
     */
    public MyGriglia() {
        this.gridSize = 4;
    }
    
    /**
     * This is the constructor for the class. Builds a grid of the 
     * desired size.
     * @param i An <code>int</code> describing the grid size.
     */
    public MyGriglia(int i) {
        super(i * i);
        this.gridSize = i;
    }

    /**
     * This is a copy constructor for the class. It makes a shallow
     * copy of the grid.
     * @param griglia Grid to be copied.
     */
    public MyGriglia(MyGriglia griglia) {
        this.gridSize = griglia.gridSize;
        this.putAll(griglia);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        String out = "";
        for (int j = 0; j < gridSize; j++ ) {       // rows
            for (int i = 0; i < gridSize; i++) {    // column
                String val = this.get(new Location(i, j)).toString();
                out += "\t" + ((val.equals("-1")) ? "0" : val);
            }
            out += "\n";
        }
        return out;
    }

    /**
     * This method verifies if a move in the desired direction is valid.
     * @param m 0=ALTO; 1=DX; 2=BASSO; 3=SX
     * @return <code>true</code> if the move is valid,
     * <code>false</code> otherwise.
     */
    public boolean isValida(int m) {
        Row[] rows = new Row[gridSize]; // array of rows...
        for (int i = 0; i < gridSize; i++) {
            rows[i] = new Row();
        }
        // initialize rows
        if (m == 0 || m == 2) { // ...each representing a column
            for (int i = 0; i < gridSize; i++) { // lines
                for (int j = 0; j < gridSize; j++) { // columns
                    rows[i].add(this.get(new Location(i, j)));
                }
            }
        } else { // ...each representing a line
            for (int i = 0; i < gridSize; i++) { // columns
                for (int j = 0; j < gridSize; j++) { // lines
                    rows[i].add(this.get(new Location(j, i)));
                }
            }
        }
        
        // set direction
        int move = 0;
        if (m == 0 || m == 2) // up or down
            move = (m == 0) ? 0 : 1; // 0 for up, 1 for down
        else // left or right
            move = (m == 3) ? 0 : 1; // 0 for left, 1 for right
        
        boolean result = false;
        for (int i = 0; i < gridSize; i++)
            result |= rows[i].validRowMove(move);
        return result;
    }

    /**
     * This method does a move in the desired direction, following the 
     * game rules.
     * @param m 0=ALTO; 1=DX; 2=BASSO; 3=SX
     */
    public void move(int m) {
        Row[] rows = new Row[gridSize]; // array of rows...
        for (int i = 0; i < gridSize; i++) {
            rows[i] = new Row();
        }
        // initialize rows
        if (m == 0 || m == 2) { // ...each representing a column
            for (int i = 0; i < gridSize; i++) { // lines
                for (int j = 0; j < gridSize; j++) { // columns
                    rows[i].add(this.get(new Location(i, j)));
                }
            }
        } else { // ...each representing a line
            for (int i = 0; i < gridSize; i++) { // columns
                for (int j = 0; j < gridSize; j++) { // lines
                    rows[i].add(this.get(new Location(j, i)));
                }
            }
        }
        
        // set direction
        int move = 0;
        if (m == 0 || m == 2) // up or down
            move = (m == 0) ? 0 : 1; // 0 for up, 1 for down
        else // left or right
            move = (m == 3) ? 0 : 1; // 0 for left, 1 for right
        
        // move
        for (int i = 0; i < gridSize; i++) {
            rows[i].moveRow(move);
        }
                
        // update grid
        if (m == 0 || m == 2) { // ...each representing a column
            for (int i = 0; i < gridSize; i++) { // lines
                for (int j = 0; j < gridSize; j++) { // columns
                    this.put(new Location(i, j), rows[i].get(j));
                }
            }
        } else { // ...each representing a line
            for (int i = 0; i < gridSize; i++) { // columns
                for (int j = 0; j < gridSize; j++) { // lines
                    this.put(new Location(j, i), rows[i].get(j));
                }
            }
        }
    }
    
    /**
     * This method returns a set containing free locations in the game grid.
     * Useful for the AI, ehich needs to evaluate the worst possible add in 
     * the next move.
     * @return A <code>Set</code> of <code>Location</code> containing the 
     * free tiles.
     */
    public Set<Location> freeLocations() {
        Set<Location> locations = new HashSet<>();
        for (Location l : this.keySet()) {
            if (this.get(l).equals(-1))
                locations.add(l);
        }
        return locations;
    }
    
    /**
     * This method adds a tile with the desired value in the desired location.
     * @param l Location for the new tile.
     * @param value Value of the new tile.
     */
    public void add(Location l, Integer value) {
        if (this.get(l) != null && !this.get(l).equals(-1))
            throw new RuntimeException("The location " + l.toString()
                    + " is already taken and contains " + this.get(l));
        this.put(l, value);
    }
    
    /**
     * Return a matrix representation of the grid. The matrix is intended as an
     * array of int arrays.
     * @return An array of arrays of int, representing the grid.
     */
    public int[][] toMatrix() {
        int[][] matrix = new int[gridSize][gridSize];
        for (int i = 0; i < gridSize; i++) {
            for (int j = 0; j < gridSize; j++)
                matrix[i][j] = this.get(new Location(i, j));
                
        }
        return matrix;
    }
    
}
