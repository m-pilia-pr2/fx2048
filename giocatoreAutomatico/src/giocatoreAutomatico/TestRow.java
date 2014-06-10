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

import java.util.ArrayList;

/**
 *
 * @author martino
 */
public class TestRow {
    public static void main(String[] args) {
        ArrayList<Integer> i = new ArrayList<>(4);
        i.add(4);
        i.add(4);
        i.add(2);
        i.add(-1);
        
        Row r = new Row(i);
        System.out.println(r.toString());
        /*System.out.println(r.validRowMove(0));
        System.out.println(r.validRowMove(1));
        r.compact(0);
        System.out.println(r.toString());
        r.compact(0);
        System.out.println(r.toString());
        System.out.println(r.validRowMove(0));
        
        r.compact(1);
        System.out.println(r.toString());
        r.compact(1);
        System.out.println(r.toString());
        r.compact(1);
        System.out.println(r.toString());*/
        
        
        
        /*r.moveRow(1);
        System.out.println(r.toString());*/
        //System.out.println(r.validRowMove(1));
        
        
        
        r.compact(1);
        System.out.println(r.toString());
        //System.out.println(r.validRowMove(0));
        r.join(1);
        System.out.println(r.toString());
        r.compact(1);
        System.out.println(r.toString());
        //System.out.println(r.validRowMove(0));
        
        
    }
}
