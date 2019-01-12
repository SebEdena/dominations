/**
 * Classe permettant de décrire un IA simple
 * @author Mathieu Valentin, Sébastien Viguier, Laurent Yu
 * @version 1.0
 */
package jeu;

import exceptions.DominoException;
import exceptions.TuileException;
import plateau.IDomino;
import plateau.PlacementDomino;

import java.util.ArrayList;
import java.util.Arrays;
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
     * @throws DominoException
     * @throws TuileException
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
     * @see plateau.Plateau#possibilite
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
