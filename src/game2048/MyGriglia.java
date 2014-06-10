/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package game2048;

import giocatoreAutomatico.Griglia;
import java.util.HashMap;

/**
 *
 * @author martino
 */
class MyGriglia extends HashMap<game2048.Location,Integer> implements Griglia {

    private final int gridSize;
    public MyGriglia() {
        this.gridSize = 4;
    }
    
    public MyGriglia(int i) {
        super(i);
        this.gridSize = (int) Math.sqrt(i);
    }
    
    @Override
    public String toString() {
        String out = "";
        for (int j = 0; j < gridSize; j++ ) {       // rows
            for (int i = 0; i < gridSize; i++) {    // column
                out += "\t" + this.get(new Location(i, j)).toString();
            }
            out += "\n";
        }
        return out;
    }
    
}
