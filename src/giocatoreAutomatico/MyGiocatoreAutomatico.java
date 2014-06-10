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
    private int mode = 3; // provisional hard-coded selector
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
            case 3: dir = this.nextMoveMinimax(griglia, SEARCH_DEPTH); break;
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
    
    /**
     * 
     * @param board
     * @param recursion_depth
     * @return 0=ALTO; 1=DX; 2=BASSO; 3=SX
     */
    private int nextMoveMinimax(MyGriglia board, int recursion_depth) {
        double[] res; // move, score
        res = this.nextMoveRecur(board, recursion_depth, recursion_depth, 0.9);
        return (int) res[0];
    }
    
    private double[] nextMoveRecur(MyGriglia board, int depth, int maxDepth, double base) {
        double bestScore = -1;
        double bestMove = 0;
        
        for (int m = 0; m < 4; m++) {
            double score = 0;
            if (board.isValida(m)) {
                MyGriglia newBoard = new MyGriglia(board);
                newBoard.move(m);

                if (depth != 0) {
                    double[] res = null;
                    
                    double pessimisticScore = -1;
                    double worstScore = Double.MAX_VALUE;
                    Location worstAdd = null;
                    Integer worstValue = -1;
                    
                    // 2 random tile prevision
                    for (Location l : newBoard.freeLocations()) {
                        //System.out.print(l);
                        MyGriglia newBoardAdded = new MyGriglia(newBoard);
                        
                        newBoardAdded.add(l, 2);
                        
                        pessimisticScore = this.evaluate(newBoardAdded);
                        //System.out.println(" pessimistic score: " + pessimisticScore);
                        
                        if (pessimisticScore < worstScore) {
                            worstScore = pessimisticScore;
                            worstAdd = l;
                            worstValue = 2;
                        }
                    }
                    // 4 random tile prevision
                    for (Location l : newBoard.freeLocations()) {
                        //System.out.print(l);
                        MyGriglia newBoardAdded = new MyGriglia(newBoard);
                        
                        newBoardAdded.add(l, 4);
                        
                        pessimisticScore = this.evaluate(newBoardAdded);
                        //System.out.println(" pessimistic score: " + pessimisticScore);
                        
                        if (pessimisticScore < worstScore) {
                            worstScore = pessimisticScore;
                            worstAdd = l;
                            worstValue = 4;
                        }
                    }
                    
                    score = this.evaluate(newBoard);
                    
                    //System.out.println(newBoard.toString());
                    newBoard.add(worstAdd, worstValue);
                    //System.out.println("Adding in " + worstAdd.toString());

                    res = this.nextMoveRecur(newBoard, depth - 1, maxDepth, 0.9);
                    score += res[1] * Math.pow(base, maxDepth - depth + 1);
                }

                if (score > bestScore) {
                    bestMove = m;
                    bestScore = score;
                }
            }
        }
        double[] out = {bestMove, bestScore};
        return out;
    }

    private double evaluate(MyGriglia newBoard) {
        double evaluation = 0;
        int exp = 0; // exponent
        for (int i = 0; i < gridSize; i++) {
            if (i % 2 == 0) {
                for (int j = 0; j < gridSize; j++) {
                    int val = newBoard.get(new Location(i,j));
                    if (val != -1)
                        evaluation += val * Math.pow(evalBase, exp);
                    exp++;
                }
            } else {
                for (int j = gridSize - 1; j >= 0; j--) {
                    int val = newBoard.get(new Location(i,j));
                    if (val != -1)
                        evaluation += val * Math.pow(evalBase, exp);
                    exp++;
                }
                
            }
        }
        return evaluation;
    }
}
