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

import game2048.Location;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

/**
 *
 * @author martino
 */
public class QuickGrid extends HashMap<game2048.Location,Integer> implements Griglia {

    private int gridSize;
    
    public QuickGrid() {
        this.gridSize = 4;
    }
    
    public QuickGrid(int i) {
        super(i * i);
        this.gridSize = i;
    }

    /**
     * Copy constructor
     * @param griglia Grid to be copied.
     */
    public QuickGrid(QuickGrid griglia) {
        this.gridSize = griglia.gridSize;
        this.putAll(griglia);
    }
    
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
     * 
     * @param m 0=ALTO; 1=DX; 2=BASSO; 3=SX
     * @return true if the input direction provides a valid move
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
     * 
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
    
    public Set<Location> freeLocations() {
        Set<Location> locations = new HashSet<>();
        for (Location l : this.keySet()) {
            if (this.get(l).equals(-1))
                locations.add(l);
        }
        return locations;
    }
    
    public void add(Location l, Integer value) {
        if (this.get(l) != null && !this.get(l).equals(-1))
            throw new RuntimeException("The location " + l.toString()
                    + " is already taken and contains " + this.get(l));
        this.put(l, value);
    }
    
}
