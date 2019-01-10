package jeu;

import exceptions.DominoException;
import exceptions.TuileException;
import plateau.IDomino;
import plateau.PlacementDomino;

import java.util.List;
import java.util.Random;

public abstract class AbstractIA extends Joueur
{
    public AbstractIA(String nom, Roi couleur, NbJoueur nbJoueur, ModeJeu modeJeu, int score) throws DominoException, TuileException
    {
       super(nom, couleur, nbJoueur, modeJeu, score);
    }

    public abstract int pickInPioche(List<IDomino> cartesSurBoard, List<Joueur> joueursAdverses);

    public abstract PlacementDomino pickPossibilite(IDomino domino);

    public boolean isIA()
    {
        return true;
    }
}
