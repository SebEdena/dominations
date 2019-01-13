/**
 * Classe permettant de décrire un IA simple
 * @author Mathieu Valentin, Sébastien Viguier, Laurent Yu
 * @version 1.0
 */
package model.joueur;

import model.exceptions.DominoException;
import model.exceptions.TuileException;
import model.jeu.ModeJeu;
import model.jeu.NbJoueur;
import model.jeu.Roi;
import model.plateau.IDomino;
import model.plateau.PlacementDomino;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class SimpleIA extends AbstractIA
{
    /**
     * Constructeur d'un IA simple
     * @param nom Pseudo de l'IA
     * @param couleur Roi attribué à l'IA
     * @param nbJoueur Paramètre du jeu
     * @param modeJeu Mode du jeu
     * @param score Score de départ de l'IA
     * @throws DominoException
     * @throws TuileException
     * @see AbstractIA
     */
    public SimpleIA(String nom, Roi couleur, NbJoueur nbJoueur, ModeJeu modeJeu, int score) throws DominoException, TuileException
    {
        super(nom, couleur, nbJoueur, modeJeu, score);
    }

    /**
     * Methode permettant de choisir un domino dans la pioche
     * @param cartesSurBoard Pioche contenant les dominos restants
     * @param joueursAdverses Liste des joueurs en jeu
     * @return Le numéro du domino à piocher
     */
    @Override
    public int pickInPioche(List<IDomino> cartesSurBoard, List<Joueur> joueursAdverses) {
        Random random = new Random();
        return random.nextInt(cartesSurBoard.size());
    }

    /**
     * Methode permettant de choisir le placement d'un domino
     * @param domino Domino à placer
     * @return La position du domino à placer
     * @throws Exception
     * @see model.plateau.Plateau#possibilite
     */
    @Override
    public PlacementDomino pickPossibilite(IDomino domino) throws Exception{
        Random r = new Random();
        List<PlacementDomino> placementDominoList = new ArrayList<PlacementDomino>();
        placementDominoList = this.getPlateau().possibilite(domino);
        if(placementDominoList.size() == 0)
        {
            throw new Exception("Impossible pour l'IA de placer le domino");
        }
        return placementDominoList.get(r.nextInt(placementDominoList.size()));
    }
}
