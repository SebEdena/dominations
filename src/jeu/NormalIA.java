package jeu;

import exceptions.DominoException;
import exceptions.TuileException;
import plateau.IDomino;
import plateau.PlacementDomino;
import plateau.Plateau;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class NormalIA extends AbstractIA{

    private List<PlacementDomino> positionDomino = new ArrayList<>();

    public NormalIA(String nom, Roi couleur, NbJoueur nbJoueur, ModeJeu modeJeu, int score) throws DominoException, TuileException {
        super(nom, couleur, nbJoueur, modeJeu, score);
    }

    @Override
    public int pickInPioche(List<IDomino> cartesSurBoard, List<Joueur> joueursAdverses) throws DominoException, TuileException {
        List<PlacementDomino> possibilites = new ArrayList<>();
        PlacementDomino placement = null;
        int score = this.getPlateau().calculPoint();
        for (IDomino d : cartesSurBoard) {
            placement = getPlacement(score,possibilites, placement);
        }
        positionDomino.add(placement);
        Random rand = new Random();
        if(placement != null) {
            for (int i = 0; i < cartesSurBoard.size(); i++) {
                if (cartesSurBoard.get(i).getIdentifiant() == placement.getDomino().getIdentifiant()) {
                    return i;
                }
            }
            return rand.nextInt(cartesSurBoard.size());
        } else return rand.nextInt(cartesSurBoard.size());
    }

    private PlacementDomino getPlacement(int score, List<PlacementDomino> possibilites, PlacementDomino placement) throws DominoException, TuileException {
        for (PlacementDomino pos : possibilites) {
            Plateau p = clonePlateau(this.getPlateau());
            p.addDomino(pos.getDomino(),pos.getRow(),pos.getColumn(),pos.getCaseId(),pos.getSens());
            int scorePos = p.calculPoint();
            if(score <= scorePos) {
                score = scorePos;
                placement = pos;
            }
        }
        return placement;
    }

    private Plateau clonePlateau(Plateau plateau) {
        Plateau p = new Plateau(this.getPlateau().getSize());
        for (int i = 0; i < p.getSize(); i++) {
            for (int j = 0; j < p.getSize(); j++) {
                p.setCaseAt(i,j,plateau.getCaseAt(i,j));
            }
        }
        p.setTuileAjoutee(true);
        p.setXBound(plateau.getXBounds()[0],plateau.getXBounds()[1]);
        p.setYBound(plateau.getYBounds()[0],plateau.getYBounds()[1]);
        return p;
    }

    @Override
    public PlacementDomino pickPossibilite(IDomino domino) throws Exception {
        int score = this.getPlateau().calculPoint();
        List<PlacementDomino> placement = this.getPlateau().possibilite(domino);
        PlacementDomino bestPlacement = getPlacement(score,placement,null);
        if(bestPlacement == null){
            throw new Exception("Impossible pour l'IA de placer le domino");
        }
        return bestPlacement;
    }
}
