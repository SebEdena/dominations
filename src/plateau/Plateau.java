package plateau;

import exceptions.DominoException;
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

    public void addDomino(IDomino d, int xCase, int yCase, int indexCase, Orientation sens) throws TuileException, DominoException {
        String erreur;
        if(d instanceof Tuile){
            if(!tuileAjoutee){
                tableau[xCase][yCase] = d.getCases()[indexCase];
                updateSizePlateau(xCase, yCase, sens);
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
                    if(translation[0] != 0 || translation[1] != 0){
                        xCase += translation[0];
                        yCase += translation[1];
                        translationPlateau(translation);
                    }

                    Case[] tmpCases = d.getCases();
                    tableau[xCase][yCase] = tmpCases[indexCase];
                    tableau[xCase + sens.getOffsetX()][yCase + sens.getOffsetY()] = tmpCases[indexAutreCase];
                    updateSizePlateau(xCase, yCase, sens);
                    dominos.add(d);
                }
            }else{
                throw new TuileException("La tuile du chateau n'a pas encore été placée !");
            }
        }
    }

    private String placementValide(IDomino d, int xCase, int yCase, int indexCase, Orientation sens){
        int xCase2 = xCase + sens.getOffsetX(), yCase2 = yCase + sens.getOffsetY();

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
        if((xCase >= 0 && xCase < NB_COL_LIG && yCase >= 0 && yCase < NB_COL_LIG &&
                tableau[xCase][yCase] != null) ||
                (xCase2 >= 0 && xCase2 < NB_COL_LIG && yCase2 >= 0 && yCase2 < NB_COL_LIG &&
                        tableau[xCase2][yCase2] != null)){
            return "Au moins une des deux cases où placer le domino est déjà remplie";
        }

        boolean[] isValid = {false, false};
        int indexAutreCase = Math.abs(indexCase - 1);
        for(int i = 0; i < d.getNbCases(); i++){
            for(Orientation o : Orientation.values()){
                if(indexCase == i &&
                        xCase+o.getOffsetX() >= 0 && xCase+o.getOffsetX() < NB_COL_LIG &&
                        yCase+o.getOffsetY() >= 0 && yCase+o.getOffsetY() < NB_COL_LIG &&
                        tableau[xCase+o.getOffsetX()][yCase+o.getOffsetY()] != null &&
                        !o.equals(sens) &&
                        (tableau[xCase+o.getOffsetX()][yCase+o.getOffsetY()].getTerrain()
                                .equals(d.getCases()[indexCase].getTerrain()) ||
                                tableau[xCase+o.getOffsetX()][yCase+o.getOffsetY()].getTerrain()
                                        .equals(Terrain.CHATEAU))){
                        isValid[indexCase] = true;
                } else if(!o.equals(sens.getOppose())&&
                        xCase2+o.getOffsetX() >= 0 && xCase2+o.getOffsetX() < NB_COL_LIG &&
                        yCase2+o.getOffsetY() >= 0 && yCase2+o.getOffsetY() < NB_COL_LIG &&
                        tableau[xCase2+o.getOffsetX()][yCase2+o.getOffsetY()] != null &&
                            (tableau[xCase2+o.getOffsetX()][yCase2+o.getOffsetY()].getTerrain()
                                    .equals(d.getCases()[indexAutreCase].getTerrain())||
                            tableau[xCase2+o.getOffsetX()][yCase2+o.getOffsetY()].getTerrain()
                                    .equals(Terrain.CHATEAU))){
                        isValid[indexAutreCase] = true;
                }
                if(isValid[i]){
                    break;
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
        if(yCase <= 0) {
            if(sens.equals(Orientation.OUEST)){
                deplacement[1] = 1 - yCase;
            }else if(sens.equals(Orientation.EST))
            {
                deplacement[1] += - yCase;
            }
        }
        if(yCase >= NB_COL_LIG - 1){
            if(sens.equals(Orientation.EST)){
                deplacement[1] = -1 + (NB_COL_LIG - yCase - 1);
            }else if(sens.equals(Orientation.OUEST))
            {
                deplacement[1] += NB_COL_LIG - yCase - 1;
            }
        }
        if(xCase <= 0) {
            if(sens.equals(Orientation.NORD)){
                deplacement[0] = 1 - xCase;
            }else if(sens.equals(Orientation.SUD))
            {
                deplacement[0] += - xCase;
            }
        }
        if(xCase >= NB_COL_LIG - 1){
            if(sens.equals(Orientation.SUD)){
                deplacement[0] = -1 + (NB_COL_LIG - xCase - 1);
            }else if(sens.equals(Orientation.NORD))
            {
                deplacement[0] += NB_COL_LIG - xCase - 1;
            }
        }
        return deplacement;
    }

    private void translationPlateau(int[] deplacement) {
        Case[][] newPlateau = new Case[NB_COL_LIG][NB_COL_LIG];
        for(int i = 0; i < NB_COL_LIG; i++){
            for(int j = 0; j < NB_COL_LIG; j++){
                if(i+deplacement[0] >= 0 && i + deplacement[0] < NB_COL_LIG &&
                        j+deplacement[1] >= 0 && j+deplacement[1] < NB_COL_LIG){
                    newPlateau[i+deplacement[0]][j+deplacement[1]] = tableau[i][j];
                }
            }
        }
        tableau = newPlateau;
        minX += deplacement[0];
        maxX += deplacement[0];
        minY += deplacement[1];
        maxY += deplacement[1];
    }

    private void updateSizePlateau(int xCase, int yCase, Orientation sens){
        if(!tuileAjoutee){
            minX = xCase;
            maxX = xCase;
            minY = yCase;
            maxY = yCase;
        }else{
            if(xCase + sens.getOffsetX() >= 0 && xCase + sens.getOffsetX() < NB_COL_LIG){
                if(xCase < minX) minX = xCase + sens.getOffsetX();
                if(xCase > maxX) maxX = xCase + sens.getOffsetX();
            }
            if(yCase + sens.getOffsetY() >= 0 && yCase + sens.getOffsetY() < NB_COL_LIG){
                if(yCase < minY) minY = yCase + sens.getOffsetY();
                if(yCase > maxY) maxY = yCase + sens.getOffsetY();
            }
        }
        System.out.println("x : ["+minX+ ","+maxX+"]");
        System.out.println("y : ["+minY+ ","+maxY+"]");
        System.out.println();
    }

    public int calculPoint()
    {
        int sommePoints = 0;
        List<Case> pileCasesVisitees = new ArrayList<Case>();
        for(int i = 0; i < NB_COL_LIG; i++)
        {
            for(int j = 0 ; j < NB_COL_LIG; j++)
            {
                if(this.tableau[i][j] != null && !pileCasesVisitees.contains(this.tableau[i][j]) &&
                        !this.tableau[i][j].getTerrain().equals(Terrain.CHATEAU))
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
            }
        }
        return sommePoints;
    }

    private void rechercheCaseSimilaire(int x, int y, List<Case> pileCasesVisitees, Case caseTemoin, List<Case> casesAdjacentes)
    {
        casesAdjacentes.add(caseTemoin);
        pileCasesVisitees.add(caseTemoin);
        List<Case> casesTrouvées = new ArrayList<Case>();

        for(Orientation o : Orientation.values())
        {
            if(x + o.getOffsetX() >= 0 && x + o.getOffsetX() < NB_COL_LIG &&
                    y + o.getOffsetY() >= 0 && y + o.getOffsetY() < NB_COL_LIG &&
                    tableau[x + o.getOffsetX()][y + o.getOffsetY()] != null &&
                    tableau[x + o.getOffsetX()][y + o.getOffsetY()].getTerrain().equals(caseTemoin.getTerrain()) &&
                    !pileCasesVisitees.contains(tableau[x + o.getOffsetX()][y + o.getOffsetY()]))
            {
                casesTrouvées.add(tableau[x + o.getOffsetX()][y + o.getOffsetY()]);
                pileCasesVisitees.add(tableau[x + o.getOffsetX()][y + o.getOffsetY()]);
                rechercheCaseSimilaire(x + o.getOffsetX(), y + o.getOffsetY(),
                        pileCasesVisitees,tableau[x + o.getOffsetX()][y + o.getOffsetY()],casesAdjacentes);
            }
        }
    }

    public String affichePlateau(boolean modeAjout){
        int[] borneX = {0, NB_COL_LIG};
        int[] borneY = {0, NB_COL_LIG};
        List<String[]> cases = new ArrayList<String[]>();
        StringBuilder sb = new StringBuilder();
        String separateurCase = Case.getSeparateurPlateau();

        if(modeAjout){
            if (maxX - minX + 1 < NB_COL_LIG) {
                borneX[0]--;
                borneX[1]++;
            }
            if (maxY - minY + 1 < NB_COL_LIG){
                borneY[0]--;
                borneY[1]++;
            }
            sb.append("   ");
            for(int j = borneY[0]; j < borneY[1]; j++){
                sb.append(" " + String.format("%2d", j) + " \t");
            }
            sb.append("\n");
        }
        for(int i = borneX[0]; i < borneX[1]; i++){
            for(int j = borneY[0]; j < borneY[1]; j++){
                if(i < 0 || i >= NB_COL_LIG || j < 0 || j >= NB_COL_LIG || tableau[i][j] == null){
                    cases.add(("    " + separateurCase + "    ").split(separateurCase));
                }else{
                    cases.add(tableau[i][j].affichagePlateau().split(separateurCase));
                }
            }
            if(modeAjout) sb.append(String.format("%2d", i)+ " ");
            for(int j = 0; j < 2*(borneY[1] - borneY[0]); j++){
                String casePlateau = cases.get(j % (borneY[1] - borneY[0]))[j/(borneY[1] - borneY[0])];
                sb.append(casePlateau);
                if(j % (borneY[1] - borneY[0]) != (borneY[1] - borneY[0]) - 1){
                    sb.append("\t");
                }else{
                    sb.append("\n");
                    if(modeAjout) sb.append("   ");
                }
            }
            sb.append("\n");
            cases.clear();
        }
        return sb.toString();
    }
}
