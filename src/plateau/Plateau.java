package plateau;

import exceptions.DominoException;
import exceptions.TuileException;
import javafx.util.Pair;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
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

    public void addDomino(IDomino d, int xCase, int yCase, int indexCase, Orientation sens) throws TuileException, DominoException {
        String erreur;
        if(d instanceof Tuile){
            if(!tuileAjoutee){
                tableau[xCase][yCase] = d.getCases()[indexCase];
                minX = xCase;
                minY = yCase;
                maxX = xCase;
                maxY = yCase;
                dominos.add(d);
                tuileAjoutee = true;
            }else{
                throw new TuileException("La tuile du chateau est déjà placée");
            }
        }else{
            if(tuileAjoutee){
                if((erreur = placementValide(d, xCase, yCase, indexCase, sens)) != null){
                    throw new DominoException(erreur);
                }else{
                    int indexAutreCase = Math.abs(indexCase - 1);
                    int[] translation = calculTranslation(d, xCase, yCase, indexCase, sens);
                    if(translation[0] != 0 && translation[1] != 0){
                        xCase += translation[0];
                        yCase += translation[1];
                        translationPlateau(translation);
                    }

                    Case[] tmpCases = d.getCases();
                    tableau[xCase][yCase] = tmpCases[indexCase];
                    tableau[xCase + sens.getOffsetX()][yCase + sens.getOffsetY()] = tmpCases[indexAutreCase];
                    if(xCase < minX) minX = xCase;
                    if(xCase > maxX) maxX = xCase;
                    if(yCase < minY) minY = yCase;
                    if(yCase > maxY) maxY = yCase;
                    dominos.add(d);
                }
            }else{
                throw new TuileException("La tuile du chateau n'a pas encore été placée !");
            }
        }
    }

    private String placementValide(IDomino d, int xCase, int yCase, int indexCase, Orientation sens){
        if(xCase < -1 || xCase > NB_COL_LIG + 1 || yCase < -1 || yCase > NB_COL_LIG + 1 ||
                (xCase==-1 && yCase==-1) ||
                (xCase==-1 && yCase==NB_COL_LIG)||
                (xCase==NB_COL_LIG && yCase==-1) ||
                (xCase==NB_COL_LIG && yCase==NB_COL_LIG)){
            return "Placement invalide du domino pour " +
                    "[x:" + xCase +", y:" + yCase + ", sens:" + sens.getText()+"]";
        }
        if(((maxX - minX) + sens.getOffsetX()) > NB_COL_LIG || ((maxY - minY) + sens.getOffsetY()) > NB_COL_LIG) {
            return "Impossible de placer le domino car la longueur ou largeur dépasserait la " +
                    "limite autorisée de " + NB_COL_LIG;
        }
        if(tableau[xCase][yCase] != null || tableau[xCase+sens.getOffsetX()][yCase+sens.getOffsetY()] != null){
            return "Au moins une des deux cases où placer le domino est déjà remplie";
        }

        boolean[] isValid = {false, false};
        int xCase2 = xCase + sens.getOffsetX(), yCase2 = yCase + sens.getOffsetY();
        int indexAutreCase = (indexCase - 1) % d.getNbCases();
        for(int i = 0; i < d.getNbCases(); i++){
            for(Orientation o : Orientation.values()){
                if(indexCase == i){
                    if(!o.equals(sens) &&
                            tableau[xCase+o.getOffsetX()][yCase+o.getOffsetY()].getTerrain()
                                    .equals(d.getCases()[indexCase].getTerrain())){
                        isValid[indexCase] = true;
                    }
                } else {
                    if(!o.equals(sens.getOppose())&&
                            tableau[xCase2+o.getOffsetX()][yCase2+o.getOffsetY()].getTerrain()
                                    .equals(d.getCases()[indexAutreCase].getTerrain())){
                        isValid[indexAutreCase] = true;
                    }
                }
            }
        }

        if(!isValid[0] && !isValid[1]){
            return "Aucune des deux cases n'entre en contact avec un terrain similaire";
        }else{
            return null;
        }
    }

    private int[] calculTranslation(IDomino d, int xCase, int yCase, int indexCase, Orientation sens){
        int[] deplacement = {0, 0};
        if(xCase <= 0) {
            if(sens.equals(Orientation.OUEST)){
                deplacement[0] = 1 - xCase;
            }else {
                deplacement[0] = 1;
            }
        }
        if(xCase >= NB_COL_LIG - 1){
            if(sens.equals(Orientation.EST)){
                deplacement[0] = -1 + (NB_COL_LIG - xCase - 1);
            }else {
                deplacement[0] = -1;
            }
        }
        if(yCase <= 0) {
            if(sens.equals(Orientation.NORD)){
                deplacement[1] = 1 - yCase;
            }else {
                deplacement[1] = 1;
            }
        }
        if(yCase >= NB_COL_LIG - 1){
            if(sens.equals(Orientation.SUD)){
                deplacement[1] = -1 + (NB_COL_LIG - xCase - 1);
            }else {
                deplacement[1] = -1;
            }
        }

        return deplacement;
    }

    private void translationPlateau(int[] deplacement) throws DominoException {
        Case[][] newPlateau = new Case[NB_COL_LIG][NB_COL_LIG];
        for(int i = 0; i < NB_COL_LIG; i++){
            for(int j = 0; j < NB_COL_LIG; j++){

            }
        }
    }

    public int CalculPoint()
    {
        int sommePoints = 0;
        List<Case> pileCasesVisitees = new ArrayList<Case>();
        for(int i = 0; i < NB_COL_LIG; i++)
        {
            for(int j = 0 ; j < NB_COL_LIG; j++)
            {
                if(this.tableau[i][j] != null && !pileCasesVisitees.contains(this.tableau[i][j]) && this.tableau[i][j].getTerrain() != Terrain.Chateau)
                {
                    Case caseTemoin = tableau[i][j];
                    List<Case> casesSimilaires = new ArrayList<Case>();
                    rechercheCaseSimilaire(i,j, pileCasesVisitees, caseTemoin, casesSimilaires);

                    int couronne = 0;
                    int compteurCase = 0;
                    for(Case c : casesSimilaires)
                    {
                        compteurCase++;
                        couronne = couronne + c.getNbCouronne();
                    }
                    sommePoints = sommePoints + compteurCase * couronne;
                    //System.out.println(sommePoints);
                }
                else
                {

                }
            }
        }
        return sommePoints;
    }

    private void rechercheCaseSimilaire(int x, int y, List<Case> pileCasesVisitees, Case caseTemoin, List<Case> casesAdjacentes)
    {
        casesAdjacentes.add(caseTemoin);
        pileCasesVisitees.add(caseTemoin);
        List<Case> casesTrouvées = new ArrayList<Case>();

        if(x - 1 >= 0 && tableau[x-1][y] != null && tableau[x-1][y].getTerrain() == caseTemoin.getTerrain() && !pileCasesVisitees.contains(tableau[x-1][y]))
        {
            casesTrouvées.add(tableau[x - 1][y]);
            pileCasesVisitees.add(tableau[x - 1][y]);
            rechercheCaseSimilaire(x-1,y,pileCasesVisitees,tableau[x-1][y],casesAdjacentes);
        }
        if(y - 1 >= 0 && tableau[x][y-1] != null && tableau[x][y - 1].getTerrain() == caseTemoin.getTerrain() && !pileCasesVisitees.contains(tableau[x][y - 1]))
        {
            casesTrouvées.add(tableau[x][y - 1]);
            pileCasesVisitees.add(tableau[x][y - 1]);
            rechercheCaseSimilaire(x,y-1,pileCasesVisitees,tableau[x][y-1],casesAdjacentes);
        }
        if(x + 1 < NB_COL_LIG && tableau[x+1][y] != null && tableau[x+1][y].getTerrain() == caseTemoin.getTerrain() && !pileCasesVisitees.contains(tableau[x+1][y]))
        {
            casesTrouvées.add(tableau[x+1][y]);
            pileCasesVisitees.add(tableau[x+1][y]);
            rechercheCaseSimilaire(x+1,y,pileCasesVisitees,tableau[x+1][y],casesAdjacentes);
        }
        if(y + 1 < NB_COL_LIG && tableau[x][y+1] != null && tableau[x][y + 1].getTerrain() == caseTemoin.getTerrain() && !pileCasesVisitees.contains(tableau[x][y + 1]))
        {
            casesTrouvées.add(tableau[x][y + 1]);
            pileCasesVisitees.add(tableau[x][y + 1]);
            rechercheCaseSimilaire(x,y + 1,pileCasesVisitees,tableau[x][y + 1],casesAdjacentes);
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
