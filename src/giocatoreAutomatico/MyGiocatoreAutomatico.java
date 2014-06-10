/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package giocatoreAutomatico;

import game2048.Location;
import java.util.Random;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author martino
 */
public class MyGiocatoreAutomatico implements GiocatoreAutomatico {
    
    private final int SEARCH_DEPTH = 6;
    private int gridSize = 4; // provisional hard-coded grid size
    private double evalBase = 0.25; // provisional
    private int mode = 2; // provisional hard-coded selector
    private int naiveStep = 2;
    private Random rand = new Random();
    
    private Logger log = Logger.getGlobal();
    
    private final MyGriglia griglia;
    
    public MyGiocatoreAutomatico(Griglia g) {
        this.griglia = new MyGriglia();
        this.griglia.putAll(g);
    }
    
    public static GiocatoreAutomatico getGiocatoreAutomatico(Griglia g) {
        return new MyGiocatoreAutomatico(g);
    }
    
    /**
     * 
     * @param xy
     * @param v
     * @return 0=ALTO; 1=DX; 2=BASSO; 3=SX
     */
    @Override
    public synchronized int prossimaMossa(game2048.Location xy, int v){
        griglia.put(xy, v);
        int dir; // provisional hard-coded selector
        switch (mode) {
            case 0: dir = this.nextMoveRand(); break;
            case 1: dir = this.nextMoveBlind(); break;
            case 2: dir = this.nextMoveBlind2(); break;
            default: throw new IllegalStateException("Something is broken!");
        }
        //System.out.println("" + dir + griglia.isValida(dir));
        griglia.move(dir);
        log.log(Level.INFO, "Griglia:\n{0}", griglia.toString());
        return dir;
    }
    
    /**
     * 
     * @return 0=ALTO; 1=DX; 2=BASSO; 3=SX
     */
    private int nextMoveRand() {
        return rand.nextInt(4);
    }
    
    /**
     * 
     * @return 0=ALTO; 1=DX; 2=BASSO; 3=SX
     */
    private int nextMoveBlind() {
        switch (naiveStep) {
            case 0: 
                if (griglia.isValida(3)) {
                    naiveStep = 1;
                    return 3;
                }
                if (griglia.isValida(1)) {
                    naiveStep = 2;
                    return 1;
                }
                if (griglia.isValida(2)) {
                    naiveStep = 3;
                    return 2;
                }
                naiveStep = 1; // in order to return down
                return 0;
            case 1:
                if (griglia.isValida(2)) {
                    naiveStep = 2;
                    return 2;
                }
                if (griglia.isValida(3)) {
                    naiveStep = 1;
                    return 3;
                }
                if (griglia.isValida(1)) {
                    naiveStep = 3;
                    return 1;
                }
                naiveStep = 4; // unnormal!
                return 0;
            case 2:
                if (griglia.isValida(1)) {
                    naiveStep = 3;
                    return 1;
                }
                if (griglia.isValida(3)) {
                    naiveStep = 1;
                    return 3;
                }
                if (griglia.isValida(2)) {
                    naiveStep = 3;
                    return 2;
                }
                naiveStep = 1; // in order to return down
                return 0;
            case 3:
                if (griglia.isValida(2)) {
                    naiveStep = 0;
                    return 2;
                }
                if (griglia.isValida(3)) {
                    naiveStep = 1;
                    return 3;
                }
                if (griglia.isValida(1)) {
                    naiveStep = 3;
                    return 1;
                }
                naiveStep = 1; // in order to return down
                return 0;
            default: throw new IllegalStateException("Something is broken!");
        }
    }
    
    /**
     * Like the previous, but trying every time a down or right move if possible
     * @return 0=ALTO; 1=DX; 2=BASSO; 3=SX
     */
    private int nextMoveBlind2() {
        switch (naiveStep) {
            case 2: // try down first
                naiveStep = 1;
                if (griglia.isValida(2)) 
                    return 2;
                if (griglia.isValida(1))
                    return 1;
                if (griglia.isValida(3)) {
                    naiveStep = 2; // the next try will be for down
                    return 3;
                }
                if (griglia.isValida(0)) {
                    naiveStep = 2; // the next try will be for down
                    return 0;
                }
            case 1: // try right first
                naiveStep = 2;
                if (griglia.isValida(1)) 
                    return 1;
                if (griglia.isValida(2))
                    return 2;
                if (griglia.isValida(3)) {
                    naiveStep = 2; // the next try will be for down
                    return 3;
                }
                if (griglia.isValida(0)) {
                    naiveStep = 2; // the next try will be for down
                    return 0;
                }
            default: throw new IllegalStateException("Something is broken around here!");
        }
    }
}
