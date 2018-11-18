import plateau.Case;
import plateau.Domino;
import plateau.Terrain;
import util.CSVParser;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Main {

    public static void main(String[] args) throws Exception {
        List<String[]> dominos = CSVParser.parse("./dominos.csv", ",", true);
        List<Domino> deckDominos = new ArrayList<Domino>();

        for(String[] strs : dominos) {
            Case case1 = new Case(Integer.parseInt(strs[0]), Terrain.getTerrain(strs[1]));
            Case case2 = new Case(Integer.parseInt(strs[2]), Terrain.getTerrain(strs[3]));
            Domino domino = new Domino(case1,case2,Integer.parseInt(strs[4]));
            deckDominos.add(domino);
            System.out.println(domino.toString());
            // System.out.println(Arrays.toString(strs));
        }
    }
}
