/* Griglia.java */
package giocatoreAutomatico;

import java.util.Map;

/** le classi che implementano questa interfaccia devono contenere esattamente 16 chiavi.
Alla Location (0,0), dovrà essere associato il numero (2, oppure 4, oppure 8, ...) associato a quella casella.
Qualora nella posizione non ci siano numeri, sarà associato il valore -1
*/
public interface Griglia extends Map<game2048.Location,Integer>  {


}