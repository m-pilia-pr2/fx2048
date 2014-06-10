/* GiocatoreAutomatico.java */
package giocatoreAutomatico;

public interface GiocatoreAutomatico {

    /** restituisce un oggetto GiocatoreAutomatico su cui si potrà chiedere che mosse fare.  */
    /*public static GiocatoreAutomatico getGiocatoreAutomatico(Griglia g) {
        return null;
    }*/

    /** restituisce 0=ALTO; 1=DX; 2=BASSO; 3=SX , ovvero la mossa che il giocatore automatico intende fare.
        In input prende una locazione (x,y) ed un valore. Ad esempio (0,0) e 16 se nella posizione (0,0) 
        è stato inserito casualmente il valore 16*/
    public int prossimaMossa(game2048.Location xy, int v);


}