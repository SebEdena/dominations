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
    public SimpleIA(String nom, Roi couleur, NbJoueur nbJoueur, ModeJeu modeJeu, int score) throws DominoException, TuileException
    {
        super(nom, couleur, nbJoueur, modeJeu, score);
    }

    @Override
    public int pickInPioche(List<IDomino> cartesSurBoard, List<Joueur> joueursAdverses) {
        Random random = new Random();
        return random.nextInt(cartesSurBoard.size());
    }

    @Override
    public PlacementDomino pickPossibilite(IDomino domino) {
        Random r = new Random();
        List<PlacementDomino> placementDominoList = new ArrayList<PlacementDomino>();
        placementDominoList = this.getPlateau().possibilite(domino);
        return placementDominoList.get(r.nextInt(placementDominoList.size()));
    }
}
