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

    @Override
    public abstract int pickInPioche(List<IDomino> cartesSurBoard, List<Joueur> joueursAdverses);

    @Override
    public abstract PlacementDomino pickPossibilite(IDomino domino) throws Exception;

    @Override
    public boolean isIA()
    {
        return true;
    }
}
