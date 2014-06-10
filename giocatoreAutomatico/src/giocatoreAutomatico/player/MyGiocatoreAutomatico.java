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

import giocatoreAutomatico.*;

import game2048.Location;
import java.util.Random;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class implements the interface GiocatoreAutomatico, providing objects
 * able to play 2048. This player is able to play with 3 differents styles. The
 * default style uses a search algorithm to evaluate the position. The search 
 * depth and the playing style are chosen passing opportune values though the 
 * grid. The style should saved in Location(-1, -1) (1 = random, 2 = blind,
 * 3 = minimax) and the search depth in Location(-1, -2) (int value for depth).
 * If the GUI does not provide theese values or provides invalid values,
 * default settings are used (minimax with depth = 6).
 * @author martino
 */
public class MyGiocatoreAutomatico implements GiocatoreAutomatico {
    
    private int searchDepth;
    private int gridSize = 4; // provisional hard-coded grid size
    private double evalBase = 0.25; // provisional
    private int mode = 3; // provisional hard-coded selector
    private int naiveStep = 2;
    private Random rand = new Random();
    private final int defaultStyle = 3;
    private final int defaultDepth = 6;
    private final Location PLAYING_STYLE_LOCATION = new Location(-1, -1);
    private final Location DEPTH_LOCATION = new Location(-1, -2);
    
    private final Logger log = Logger.getGlobal();
    
    private MyGriglia griglia;
    
    /**
     * This is the constructor for the class. The player is initialized with a
     * void grid.
     */
    public MyGiocatoreAutomatico() {
        this.griglia = new MyGriglia();
    }
    
    /**
     * This is the constructor for the class. The player is initialized with 
     * the providen grid.
     * @param g Grid for the player.
     */
    public MyGiocatoreAutomatico(Griglia g) {
        this();
        this.griglia.putAll(g);
    }
    
    /* With Java8 static method, this implementation is not needed. */
    /*public static GiocatoreAutomatico getGiocatoreAutomatico(Griglia g) {
        return new MyGiocatoreAutomatico(g);
    }*/
    
    /**
     * This method caluclates the next move to be made in the grid provided as
     * argument.
     * @param g Current game grid.
     * @return 0=ALTO; 1=DX; 2=BASSO; 3=SX
     */
    @Override
    public int prossimaMossa(Griglia g){
        
        this.griglia = new MyGriglia();
        this.griglia.putAll(g);
        
        log.log(Level.INFO, "Grid size: {0}", griglia.size());
        
        int style = 0;
        
        if (griglia.get(PLAYING_STYLE_LOCATION) != null)
            style = griglia.get(PLAYING_STYLE_LOCATION);
        else
            style = defaultStyle; // default style is minimax
        
        int dir; // value for the move selected by AI
        switch (style) {
            case 1: 
                do {
                    dir = this.nextMoveRand();
                } while (!griglia.isValida(dir));
                break;
            case 2: 
                dir = this.nextMoveBlind(); 
                break;
            default: 
                if (this.griglia.get(DEPTH_LOCATION) != null
                        && this.griglia.get(DEPTH_LOCATION) > 0)
                    searchDepth = this.griglia.get(DEPTH_LOCATION);
                else
                    searchDepth = defaultDepth;
                log.log(Level.INFO, "Depth: {0}", searchDepth);
                dir = this.nextMoveMinimax(griglia, searchDepth);
                break;
            //default: throw new IllegalStateException("Wrong value!");
        }
        //System.out.println("" + dir + griglia.isValida(dir));
        griglia.move(dir);
        log.log(Level.INFO, "Griglia:\n{0}", griglia.toString());
        return dir;
    }
    
    /**
     * This method provides a random move.
     * @return 0=ALTO; 1=DX; 2=BASSO; 3=SX
     */
    private int nextMoveRand() {
        return rand.nextInt(4);
    }
    
    /**
     * This method provides a move though a blind strategy.
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
     * Another blind strategy, but trying every time a down or right move 
     * if possible
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
     * This method provides a move though a simple implementation of a search
     * algorithm, similar to a minimax.
     * @param grid The current grid.
     * @param depth Depth for the search.
     * @return 0=ALTO; 1=DX; 2=BASSO; 3=SX
     */
    private int nextMoveMinimax(MyGriglia grid, int depth) {
        double[] res; // move, score
        res = this.recursiveSearch(grid, depth, depth, 0.9);
        return (int) res[0];
    }

    /* // first AI without estimation on new random tiles
    private double[] nextMoveRecur(MyGriglia grid, int depth, int maxDepth, double base) {
        double bestScore = -1;
        double bestMove = 0;
        for (int m = 0; m < 4; m++) {
            double score = 0;
            if (grid.isValida(m)) {
                MyGriglia newBoard = new MyGriglia(grid);
                newBoard.move(m);

                score = this.evaluate(newBoard);
                if (depth != 0) {
                    double[] res;
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
    }*/
    
    /**
     * This is a recursive method used in the research for the best move. It 
     * tries all legal moves, and for each evaluates all the possible tile
     * addings in the next ply. It chooses the best move against the worst
     * possible adding.
     * @param grid The current grid.
     * @param depth Depth for the search.
     * @param maxDepth Maximum depth search.
     * @param base Base for the exponential ammortization of the position score.
     * @return An array containing the best move in position <code>0</code>
     * and it's relative score in position <code>1</code>.
     */
    private double[] recursiveSearch(MyGriglia grid, int depth, int maxDepth, double base) {
        double bestScore = -1;
        double bestMove = 0;
        
        for (int m = 0; m < 4; m++) {
            double score = 0;
            if (grid.isValida(m)) {
                MyGriglia newBoard = new MyGriglia(grid);
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
                    // 4 random tile prevision (get the game worse...)
                    /*for (Location l : newBoard.freeLocations()) {
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
                    }*/
                    
                    score = this.evaluate(newBoard);
                    
                    //System.out.println(newBoard.toString());
                    newBoard.add(worstAdd, worstValue);
                    //System.out.println("Adding in " + worstAdd.toString());

                    res = this.recursiveSearch(newBoard, depth - 1, maxDepth, 0.9);
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

    /**
     * This method implements the evaluation funtion. It's the core of the
     * IA, and it's used to evaluate each position found by the search 
     * algorithm. This particular implementation is based on a positional 
     * evaluation, where the score of a position is provided by the sum 
     * of the value of each tile, multiplied by a coefficient. Each position
     * in the grid has a different coefficient, and the value of the 
     * coefficients decreases exponentially along a path, designed in order to 
     * mantain the grid as clean as possible.
     * @param newBoard Grid to evaluate.
     * @return Value for the position.
     */
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
    
    /**
     * Variant of the evaluation function, trying a correction in particular
     * circumstances. Seems not good.
     * @param newBoard
     * @return 
     */
    /*private double evaluate(MyGriglia newBoard) {
        double evaluation = 0;
        int exp = 0; // exponent
        for (int i = 0; i < gridSize; i++) {
            if (newBoard.get(new Location(0,3))
                    .compareTo(newBoard.get(new Location(1,2))) < 0) {
                if (i % 2 == 0 & i != 2) {
                    for (int j = 0; j < gridSize; j++) {
                        int val = newBoard.get(new Location(i, j));
                        if (val != -1) {
                            evaluation += val * Math.pow(evalBase, exp);
                        }
                        exp++;
                    }
                } else {
                    for (int j = gridSize - 1; j >= 0; j--) {
                        int val = newBoard.get(new Location(i, j));
                        if (i == 1 && j == 3)
                            exp -= 2;
                        if (val != -1) {
                            evaluation += val * Math.pow(evalBase, exp);
                        }
                        exp += 2;
                        exp++;
                    }

                }
            } else {
                if (i % 2 == 0) {
                    for (int j = 0; j < gridSize; j++) {
                        int val = newBoard.get(new Location(i, j));
                        if (val != -1) {
                            evaluation += val * Math.pow(evalBase, exp);
                        }
                        exp++;
                    }
                } else {
                    for (int j = gridSize - 1; j >= 0; j--) {
                        int val = newBoard.get(new Location(i, j));
                        if (val != -1) {
                            evaluation += val * Math.pow(evalBase, exp);
                        }
                        exp++;
                    }

                }
            }
        }
        return evaluation;
    }*/
    
    /**
     * Attempt to implement an evaluation function based on more than one 
     * possible path.
     * @param newBoard Current grid.
     * @return Value ofthe position.
     */
    private double multiPathEvaluate(MyGriglia newBoard) {
        double evaluation = 0;
        double maxEvaluation = -1;
        int exp; // exponent
        
        // path 1
        exp = 0;
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
        if (evaluation > maxEvaluation)
            maxEvaluation = evaluation;
        
        // path 2 (same path, opposite direction)
        exp = gridSize * gridSize - 1;
        for (int i = 0; i < gridSize; i++) {
            if (i % 2 == 0) {
                for (int j = 0; j < gridSize; j++) {
                    int val = newBoard.get(new Location(i,j));
                    if (val != -1)
                        evaluation += val * Math.pow(evalBase, exp);
                    exp--;
                }
            } else {
                for (int j = gridSize - 1; j >= 0; j--) {
                    int val = newBoard.get(new Location(i,j));
                    if (val != -1)
                        evaluation += val * Math.pow(evalBase, exp);
                    exp--;
                }
                
            }
        }
        if (evaluation > maxEvaluation)
            maxEvaluation = evaluation;
        
        // path 3 (transpose of path 1)
        exp = 0;
        for (int i = 0; i < gridSize; i++) {
            if (i % 2 == 0) {
                for (int j = 0; j < gridSize; j++) {
                    int val = newBoard.get(new Location(j,i));
                    if (val != -1)
                        evaluation += val * Math.pow(evalBase, exp);
                    exp++;
                }
            } else {
                for (int j = gridSize - 1; j >= 0; j--) {
                    int val = newBoard.get(new Location(j,i));
                    if (val != -1)
                        evaluation += val * Math.pow(evalBase, exp);
                    exp++;
                }
                
            }
        }
        if (evaluation > maxEvaluation)
            maxEvaluation = evaluation;
        
        // path 4 (transpose of path 2)
        exp = gridSize * gridSize - 1;
        for (int i = 0; i < gridSize; i++) {
            if (i % 2 == 0) {
                for (int j = 0; j < gridSize; j++) {
                    int val = newBoard.get(new Location(j,i));
                    if (val != -1)
                        evaluation += val * Math.pow(evalBase, exp);
                    exp--;
                }
            } else {
                for (int j = gridSize - 1; j >= 0; j--) {
                    int val = newBoard.get(new Location(j,i));
                    if (val != -1)
                        evaluation += val * Math.pow(evalBase, exp);
                    exp--;
                }
                
            }
        }
        if (evaluation > maxEvaluation)
            maxEvaluation = evaluation;
        
        // path 5 (inverse of path 1)
        exp = 0;
        for (int i = 0; i < gridSize; i++) {
            if (i % 2 != 0) {
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
        if (evaluation > maxEvaluation)
            maxEvaluation = evaluation;
        
        // path 6 (inverse of path 2)
        exp = gridSize * gridSize - 1;
        for (int i = 0; i < gridSize; i++) {
            if (i % 2 != 0) {
                for (int j = 0; j < gridSize; j++) {
                    int val = newBoard.get(new Location(i,j));
                    if (val != -1)
                        evaluation += val * Math.pow(evalBase, exp);
                    exp--;
                }
            } else {
                for (int j = gridSize - 1; j >= 0; j--) {
                    int val = newBoard.get(new Location(i,j));
                    if (val != -1)
                        evaluation += val * Math.pow(evalBase, exp);
                    exp--;
                }
                
            }
        }
        if (evaluation > maxEvaluation)
            maxEvaluation = evaluation;
        
        // path 7 (inverse of path 3)
        exp = 0;
        for (int i = 0; i < gridSize; i++) {
            if (i % 2 != 0) {
                for (int j = 0; j < gridSize; j++) {
                    int val = newBoard.get(new Location(j,i));
                    if (val != -1)
                        evaluation += val * Math.pow(evalBase, exp);
                    exp++;
                }
            } else {
                for (int j = gridSize - 1; j >= 0; j--) {
                    int val = newBoard.get(new Location(j,i));
                    if (val != -1)
                        evaluation += val * Math.pow(evalBase, exp);
                    exp++;
                }
                
            }
        }
        if (evaluation > maxEvaluation)
            maxEvaluation = evaluation;
        
        // path 8 (inverse of path 4)
        exp = gridSize * gridSize - 1;
        for (int i = 0; i < gridSize; i++) {
            if (i % 2 != 0) {
                for (int j = 0; j < gridSize; j++) {
                    int val = newBoard.get(new Location(j,i));
                    if (val != -1)
                        evaluation += val * Math.pow(evalBase, exp);
                    exp--;
                }
            } else {
                for (int j = gridSize - 1; j >= 0; j--) {
                    int val = newBoard.get(new Location(j,i));
                    if (val != -1)
                        evaluation += val * Math.pow(evalBase, exp);
                    exp--;
                }
                
            }
        }
        if (evaluation > maxEvaluation)
            maxEvaluation = evaluation;
        
        return maxEvaluation;
    }
}
