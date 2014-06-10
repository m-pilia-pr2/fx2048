/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package giocatoreAutomatico;

import java.util.Random;

/**
 *
 * @author martino
 */
public class MyGiocatoreAutomatico implements GiocatoreAutomatico {
    
    private final Griglia griglia;
    
    public MyGiocatoreAutomatico(Griglia g) {
        this.griglia = g;
    }
    
    public static GiocatoreAutomatico getGiocatoreAutomatico(Griglia g) {
        return new MyGiocatoreAutomatico(g);
    }
    
    @Override
    public int prossimaMossa(game2048.Location xy, int v){
        Random rand = new Random();
        return rand.nextInt(4);
    }
}
