/**
 * Classe abstraite permettant de décrire un IA
 * @author Mathieu Valentin, Sébastien Viguier, Laurent Yu
 * @version 1.0
 */
package jeu;

import exceptions.DominoException;
import exceptions.TuileException;
import plateau.IDomino;
import plateau.PlacementDomino;

import java.util.List;
import java.util.Random;

public abstract class AbstractIA extends Joueur
{
    /**
     * Constructeur de la classe abtraite AbstractIA
     * @param nom Pseudo de l'IA
     * @param couleur Roi attribué à l'IA
     * @param nbJoueur Paramètre du jeu
     * @param modeJeu Mode du jeu
     * @param score Score de départ de l'IA
     * @throws DominoException
     * @throws TuileException
     */
    public AbstractIA(String nom, Roi couleur, NbJoueur nbJoueur, ModeJeu modeJeu, int score) throws DominoException, TuileException
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
    public abstract int pickInPioche(List<IDomino> cartesSurBoard, List<Joueur> joueursAdverses) throws DominoException, TuileException;

    /**
     * Methode permettant de choisir le placement d'un domino
     * @param domino Domino à placer
     * @return La position du domino à placer
     * @throws Exception
     */
    @Override
    public abstract PlacementDomino pickPossibilite(IDomino domino) throws Exception;

    /**
     * Methode permettant d'identifier un IA d'un joueur
     * @return Si le joueur est un IA (true) ou un joueur normal (false)
     */
    @Override
    public boolean isIA()
    {
        return true;
    }
}
