/**
 * Classe permettant de décrire un plateau de jeu
 * @author Mathieu Valentin, Sébastien Viguier, Laurent Yu
 * @version 1.0
 */
package plateau;

import exceptions.DominoException;
import exceptions.TuileException;

import javax.sound.midi.Soundbank;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Plateau {
    private int NB_COL_LIG;

    private int minX, minY, maxX, maxY;
    private Case[][] tableau;
    private List<IDomino> dominos;
    private boolean tuileAjoutee = false;

    /**
     * Constructeur d'un plateau avec une taille donnée en paramètre
     * @param NB_COL_LIG
     */
    public Plateau(int NB_COL_LIG){
        this.NB_COL_LIG = NB_COL_LIG;
        this.tableau = new Case[NB_COL_LIG][NB_COL_LIG];
        this.dominos = new ArrayList<IDomino>();
    }

    /**
     * Fonction permettant de récupérer les index minimal et maximal en x du plateau
     * @return retourne un tableau de deux entiers [index minimal en x, index maximal en x]
     */
    public int[] getXBounds(){
        if(tuileAjoutee){
            return new int[]{minX, maxX};
        }
        return null;
    }

    /**
     * Fonction permettant de récupérer les index minimal et maximal en y du plateau
     * @return retourne un tableau de deux entiers [index minimal en y, index maximal en y]
     */
    public int[] getYBounds(){
        if(tuileAjoutee){
            return new int[]{minY, maxY};
        }
        return null;
    }

    /**
     * Fonction permettant de récupérer la taille du tableau en ligne et en colonne (car même valeur)
     * @return retourne la taille du tableau
     */
    public int getSize(){
        return NB_COL_LIG;
    }

    /**
     * Fonction permettant de dire si la tuille du château est placée sur le plateau
     * @return retourne un booléen permettant de dire si la tuille du château est placée sur le plateau
     */
    public boolean tuilePresente(){
        return tuileAjoutee;
    }

    /**
     * Fonction permettant de récupérer une case du plateau en fonction de la ligne et sa colonne
     * @param row numéro de ligne demandé
     * @param col numéro de colonne demandé
     * @return renvoie la case présente à la ligne row et à la colonne col
     */
    public Case getCaseAt(int row, int col) {
        try{
            return tableau[row][col];
        }catch (ArrayIndexOutOfBoundsException e){
            return null;
        }
    }

    /**
     * Fonction permettant de dire si aux coordonnées(row,col) on se trouve dans les limites des dominos placés sur le plateau
     * @param row numéro de ligne demandé
     * @param col numéro de colonne demandé
     * @return renvoie vrai si les coordonnées sont dans les limites, faux sinon
     */
    public boolean inBounds(int row, int col){
        //System.out.println(minX + " " + maxX + " " + minY + " " + maxY);
        return row >= minX && row <= maxX && col >= minY && col <= maxY;
    }

    /**
     * Fonction permettant d'ajouter un domino sur le plateau
     * @param d représente le domino
     * @param xCase numéro de ligne souhaitée
     * @param yCase numéro de colonne souhaitée
     * @param indexCase numéro de la case soit 0, soit 1(Un domino est représenté par deux cases)
     * @param sens Orientation donnée pour le domino
     * @throws TuileException Exeption utilisée pour la tuile château
     * @throws DominoException Exception utilisée pour les dominos
     */
    public void addDomino(IDomino d, int xCase, int yCase, int indexCase, Orientation sens) throws TuileException, DominoException {
        String erreur;
        if(d instanceof Tuile){ //si le domino est la tuile de château
            if(!tuileAjoutee){ //si la tuile du château n'a pas été placée
                tableau[xCase][yCase] = d.getCases()[indexCase];
                updateSizePlateau(xCase, yCase, sens);
                dominos.add(d);
                tuileAjoutee = true;
            }else{
                throw new TuileException("La tuile du chateau est déjà placée");
            }
        }else{  //sinon il s'agit d'un domino
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

    /**
     * Fonction permettant de dire si le positionnement du domino est valide ou non
     * @param d représente le domino
     * @param xCase numéro de ligne souhaitée
     * @param yCase numéro de colonne souhaitée
     * @param indexCase numéro de la case soit 0, soit 1(Un domino est représenté par deux cases)
     * @param sens Orientation donnée pour le domino
     * @return une chaine de caractère si le placement n'est pas valide
     */
    public String placementValide(IDomino d, int xCase, int yCase, int indexCase, Orientation sens){
        int xCase2 = xCase + sens.getOffsetX(), yCase2 = yCase + sens.getOffsetY();
        System.out.println(xCase + " " + yCase);
        System.out.println(minX + " " + maxX);

        if(xCase < -1 || xCase > NB_COL_LIG + 1 || yCase < -1 || yCase > NB_COL_LIG + 1 ||
                (xCase==-1 && yCase==-1) ||
                (xCase==-1 && yCase==NB_COL_LIG)||
                (xCase==NB_COL_LIG && yCase==-1) ||
                (xCase==NB_COL_LIG && yCase==NB_COL_LIG)){
            return "Placement invalide du domino pour " +
                    "[x:" + xCase +", y:" + yCase + ", sens:" + sens.getText()+"]";
        }
        if((!inBounds(xCase, yCase) || !inBounds(xCase2, yCase2)) &&
                (((maxX - minX + 1) + sens.getOffsetX() + ((xCase >= minX && xCase <= maxX)?0:1)) > NB_COL_LIG ||
                        ((maxY - minY + 1) + sens.getOffsetY() + ((yCase >= minY && xCase <= maxY)?0:1)) > NB_COL_LIG)) {
            return "Impossible de placer le domino car la longueur ou largeur dépasserait la " +
                    "limite autorisée de " + NB_COL_LIG;
        }
        if((xCase >= 0 && xCase < NB_COL_LIG && yCase >= 0 && yCase < NB_COL_LIG &&
                tableau[xCase][yCase] != null) ||
                (xCase2 >= 0 && xCase2 < NB_COL_LIG && yCase2 >= 0 && yCase2 < NB_COL_LIG &&
                        tableau[xCase2][yCase2] != null)){
            System.out.println(xCase + " " + yCase + " " + xCase2 + " " + yCase2);
            return "Au moins une des deux cases où placer le domino est déjà remplie";
        }

        boolean[] isValid = {false, false};
        int indexAutreCase = Math.abs(indexCase - 1);
        for(int i = 0; i < d.getNbCases(); i++){
            int tmpX, tmpY;
            Orientation aEviter;
            if(indexCase == i){
                tmpX = xCase;
                tmpY = yCase;
                aEviter = sens;
            }else{
                tmpX = xCase2;
                tmpY = yCase2;
                aEviter = sens.getOppose();
            }
            List<Orientation> orientations = orientationsValides(tmpX, tmpY);
            for(Orientation o : orientations){
                if(!o.equals(aEviter) &&
                        tmpX+o.getOffsetX() >= 0 && tmpX+o.getOffsetX() < NB_COL_LIG &&
                        tmpY+o.getOffsetY() >= 0 && tmpY+o.getOffsetY() < NB_COL_LIG &&
                        tableau[tmpX+o.getOffsetX()][tmpY+o.getOffsetY()] != null &&
                        (tableau[tmpX+o.getOffsetX()][tmpY+o.getOffsetY()].getTerrain()
                                .equals(d.getCases()[i].getTerrain()) ||
                                tableau[tmpX+o.getOffsetX()][tmpY+o.getOffsetY()].getTerrain()
                                        .equals(Terrain.CHATEAU))){
                    isValid[i] = true;
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

    /**
     * Fonction permettant de calculer le nombre de décalage à faire pour que les dominos soient dans le tableau
     * @param d représente le domino
     * @param xCase numéro de ligne souhaitée
     * @param yCase numéro de colonne souhaitée
     * @param indexCase numéro de la case soit 0, soit 1(Un domino est représenté par deux cases)
     * @param sens Orientation donnée pour le domino
     * @return un tableau d'entiers contenant [nombre de cases à décaler x, nombre de cases à décaler y]
     */
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

    /**
     * Fonction permettant de faire la translation du plateau en fonction du calcul de translation
     * @param deplacement tableau d'entiers [nombre de cases à décaler x, nombre de cases à décaler y]
     */
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

    /**
     * Fonction permettant de mettre à jour les limites des dominos placées
     * @param xCase numéro de ligne de la case placée
     * @param yCase numéro de colonne de la case placée
     * @param sens Orientation donnée pour le domino
     */
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

    /**
     * Fonction permettant de calculer le score total du plateau
     * @return retour le score du plateau
     */
    public int calculPoint()
    {
        int sommePoints = 0;
        List<Case> pileCasesVisitees = new ArrayList<Case>();
        for(int i = 0; i < NB_COL_LIG; i++)
        {
            for(int j = 0 ; j < NB_COL_LIG; j++)
            {
                if(this.tableau[i][j] != null && !pileCasesVisitees.contains(this.tableau[i][j]) &&
                        !this.tableau[i][j].getTerrain().equals(Terrain.CHATEAU)) //si la case (i;j) n'est pas vide et qu'il n'a jamais été parcouru et qu'il n'est pas une tuile
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

    /**
     * Fonciton récursive permettant de rechercher des cases similaires au case témoin à la position (x;y) donné
     * @param x numéro de ligne de la case témoin
     * @param y numéro de colonne de la case témoin
     * @param pileCasesVisitees liste des cases visitées
     * @param caseTemoin la case qu'on souhaite voir
     * @param casesAdjacentes liste des cases se trouvant aux côtés de la case témoin (s'incrémente avant d'être utilisé pour calculer le score d'une zone)
     */
    private void rechercheCaseSimilaire(int x, int y, List<Case> pileCasesVisitees, Case caseTemoin, List<Case> casesAdjacentes)
    {
        casesAdjacentes.add(caseTemoin);
        pileCasesVisitees.add(caseTemoin);
        List<Case> casesTrouvées = new ArrayList<Case>();

        for(Orientation o : Orientation.values()) // à chaque orientation on vérifie s'il y a une case de même domaine et non comptabilisée
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

    /**
     * Fonction permettant d'afficher en mode console le plateau
     * @param modeAjout
     * @return
     */
    public String affichePlateau(boolean modeAjout){
        int[] borneX = {minX, maxX + 1};
        int[] borneY = {minY, maxY + 1};
        List<String[]> cases = new ArrayList<String[]>();
        StringBuilder sb = new StringBuilder();
        String separateurCase = Case.getSeparateurPlateau();
        String tabReplacer = "    ";

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
                sb.append(" " + String.format("%2d", j) + " " + tabReplacer);
            }
            sb.append("\n");
        }
        for(int i = borneX[0]; i < borneX[1]; i++){
            for(int j = borneY[0]; j < borneY[1]; j++){
                if(i < 0 || i >= NB_COL_LIG || j < 0 || j >= NB_COL_LIG || tableau[i][j] == null){
                    if((i < 0 || i >= NB_COL_LIG) && (j < 0 || j >= NB_COL_LIG)){
                        cases.add(("XXXX" + separateurCase + "XXXX").split(separateurCase));
                    }else{
                        cases.add(("    " + separateurCase + "    ").split(separateurCase));
                    }
                }else{
                    cases.add(tableau[i][j].affichagePlateau().split(separateurCase));
                }
            }
            if(modeAjout) sb.append(String.format("%2d", i)+ " ");
            for(int j = 0; j < 2*(borneY[1] - borneY[0]); j++){
                String casePlateau = cases.get(j % (borneY[1] - borneY[0]))[j/(borneY[1] - borneY[0])];
                sb.append(casePlateau);
                if(j % (borneY[1] - borneY[0]) != (borneY[1] - borneY[0]) - 1){
                    sb.append(tabReplacer);
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

    /**
     * affiche des possibilités de positionnement d'un domino sur le plateau
     * @param domino domino concerné
     */
    public void possibilite(IDomino domino)
    {
        List<Case> casesPossible = new ArrayList<Case>();
        for(int numeroCase = 0; numeroCase < domino.getNbCases(); numeroCase++)
        {
            for(int i = 0; i < NB_COL_LIG; i++)
            {
                for(int j = 0; j < NB_COL_LIG; j++)
                {
                    for(Orientation o : Orientation.values())
                    {
                        if(this.placementValide(domino,i,j,numeroCase,o) == null)
                        {
                            if(!casesPossible.contains(this.tableau[i][j]))
                            {
                                casesPossible.add(this.tableau[i][j]);
                            }
                        }
                    }
                }
            }
        }
        System.out.println("possibilités de placement : ");
        for(Case c : casesPossible)
        {
            System.out.println("bonjour");
        }
    }

    /**
     * Fonction permettant de donner les orientations possibles en fonction d'une coordonnée
     * @param xCase numéro de ligne souhaitée
     * @param yCase numéro de colonne souhaitée
     * @return retourne une liste d'orientation possible
     */
    private List<Orientation> orientationsValides(int xCase, int yCase)
    {
        List<Orientation> listeOrientationsValides= new ArrayList<Orientation>();
        for(Orientation o : Orientation.values())
        {
            if(!(xCase + o.getOffsetX() < 0 || xCase + o.getOffsetX() >= NB_COL_LIG ||
                    yCase + o.getOffsetY() < 0 || yCase + o.getOffsetY() >= NB_COL_LIG))
            {
                listeOrientationsValides.add(o);
            }
        }
        return listeOrientationsValides;
    }
}
