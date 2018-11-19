package plateau;

import java.util.ArrayList;
import java.util.List;

public class Plateau {
    private int NB_COL_LIG;
    private Case[][] tableau;
    private List<Domino> dominos;

    public Plateau(int NB_COL_LIG){
        this.NB_COL_LIG = NB_COL_LIG;
        this.tableau = new Case[NB_COL_LIG][NB_COL_LIG];
        this.dominos = new ArrayList<Domino>();
    }

    public void addDomino(Domino d, int indexCase, int xCase, int yCase, Orientation sens){

    }
}
