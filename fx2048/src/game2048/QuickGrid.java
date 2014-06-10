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

package game2048;

import game2048.Location;
import giocatoreAutomatico.Griglia;
import java.util.HashMap;

/**
 * This class represents a game grid. It's intended to be used in a player-side 
 * re-implementation of the grid, in order to calculate movements for the IA. 
 * The grid is transformated in an array of Row objects when needed.
 * @author Martino Pilia
 */
public class QuickGrid extends HashMap<game2048.Location,Integer> 
    implements Griglia {

    private int gridSize;
    
    /**
     * This is the constructor for the QuickGrid class. The default size for
     * the grid is 4.
     */
    public QuickGrid() {
        this.gridSize = 4;
    }
     
    /**
     * This is the constructor for the QuickGrid class. Builds a grid of the 
     * desired size.
     * @param i An <code>int</code> describing the grid size.
     */
    public QuickGrid(int i) {
        super(i * i);
        this.gridSize = i;
    }

    /**
     * This is a copy constructor for the QuickGrid class. It makes a shallow
     * copy of the grid.
     * @param griglia Grid to be copied.
     */
    public QuickGrid(Griglia griglia) {
        this.putAll(griglia);
        this.gridSize = (int) Math.sqrt(this.size());
    }
    
    /**
     * @inheritDoc
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
}
