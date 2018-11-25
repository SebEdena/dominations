package plateau;

import exceptions.TuileException;

import java.util.ArrayList;
import java.util.List;

public class Plateau {
    private int NB_COL_LIG;

    private int minX, minY, maxX, maxY;
    private Case[][] tableau;
    private List<IDomino> dominos;
    private boolean tuileAjoutee = false;

    public Plateau(int NB_COL_LIG){
        this.NB_COL_LIG = NB_COL_LIG;
        this.tableau = new Case[NB_COL_LIG][NB_COL_LIG];
        this.dominos = new ArrayList<IDomino>();
    }

    public void addDomino(IDomino d, int xCase, int yCase, int indexCase, Orientation sens) throws TuileException {
        if(d instanceof Tuile){
            if(!tuileAjoutee){
                dominos.add(d);
                tableau[xCase][yCase] = d.getCases()[indexCase];
                minX = xCase;
                minY = yCase;
                maxX = xCase;
                maxY = yCase;
            }else{
                throw new TuileException("La tuile du chateau est déjà placée");
            }
        }else{
            int indexAutreCase = Math.abs(indexCase - 1);
            dominos.add(d);
            Case[] tmpCases = d.getCases();

            tableau[xCase][yCase] = tmpCases[indexCase];
            tableau[xCase + sens.getOffsetX()][yCase + sens.getOffsetY()] = tmpCases[indexAutreCase];
        }
    }

    public String affichePlateau(){
        List<String[]> cases = new ArrayList<String[]>();
        StringBuilder sb = new StringBuilder();
        String separateurCase = Case.getSeparateurPlateau();
        for(int i = 0; i < NB_COL_LIG; i++){
            for(int j = 0; j < NB_COL_LIG; j++){
                if(tableau[i][j] == null){
                    cases.add(("    " + separateurCase + "    ").split(separateurCase));
                }else{
                    cases.add(tableau[i][j].affichagePlateau().split(separateurCase));
                }
            }
            for(int j = 0; j < 2*NB_COL_LIG; j++){
                String casePlateau = cases.get(j % NB_COL_LIG)[j/NB_COL_LIG];
                sb.append(casePlateau);
                if(j % NB_COL_LIG != NB_COL_LIG - 1){
                    sb.append("\t");
                }else{
                    sb.append("\n");
                }
            }
            sb.append("\n");
            cases.clear();
        }
        return sb.toString();
    }

}
