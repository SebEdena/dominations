import com.sun.xml.internal.bind.v2.model.core.ID;
import plateau.*;
import util.CSVParser;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MainSeb {

    public static final int NB_COL = 5;
    public static List<IDomino> deckDominos = new ArrayList<IDomino>();

    public static void main(String[] args) throws Exception {
        List<String[]> exPlateau = CSVParser.parse("./test_plateau.csv", ",", true);
        fillDeck("./dominos.csv");

        Plateau p = new Plateau(NB_COL);

        for(String[] strs : exPlateau){
            if(Integer.parseInt(strs[0]) <= 0 ){
                p.addDomino(new Tuile(), Integer.parseInt(strs[1]),
                        Integer.parseInt(strs[2]), 0, null);
            }else{
                p.addDomino(getDomino(Integer.parseInt(strs[0])),
                        Integer.parseInt(strs[1]), Integer.parseInt(strs[2]),
                        Integer.parseInt(strs[3]), Orientation.getOrientation(strs[4]));
            }
            System.out.println(p.affichePlateau());
            System.out.println("__________________");

        }
        System.out.println(p.affichePlateau());
    }

    public static IDomino getDomino(int id){
        for(IDomino d : deckDominos){
            if(d.getIdentifiant() == id){
                return d;
            }
        }
        return null;
    }

    public static void fillDeck(String path) throws IOException {
        List<String[]> dominos = CSVParser.parse(path, ",", true);

        for(String[] strs : dominos) {
            Case case1 = new Case(Integer.parseInt(strs[0]), Terrain.getTerrain(strs[1]));
            Case case2 = new Case(Integer.parseInt(strs[2]), Terrain.getTerrain(strs[3]));
            IDomino domino = new Domino(case1,case2,Integer.parseInt(strs[4]));
            deckDominos.add(domino);
        }
    }

}
