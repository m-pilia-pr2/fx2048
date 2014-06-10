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

/**
 * This class provides a RuntimeException subclass. It's used when an automatic
 * player provides an invalid move value. 
 * @see GiocatoreAutomatico
 * @author Martino Pilia
 */
class InvalidMoveException extends RuntimeException {

    /**
     * This is the constructor for the InvalidMoveException.
     */
    public InvalidMoveException() {
    }
    
    /**
     * This is the constructor for the InvalidMoveException.
     * @param msg A String message with detailed infos about the circumstances 
     * generating the error.
     */
    public InvalidMoveException(String msg) {
        super(msg);
    }
    
}
