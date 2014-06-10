/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
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
