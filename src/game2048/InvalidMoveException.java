/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package game2048;

/**
 *
 * @author Martino Pilia
 */
class InvalidMoveException extends RuntimeException {

    public InvalidMoveException() {
    }
    
    public InvalidMoveException(String msg) {
        super(msg);
    }
    
}
