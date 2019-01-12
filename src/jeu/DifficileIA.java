package jeu;

import exceptions.DominoException;
import exceptions.TuileException;
import plateau.IDomino;
import plateau.PlacementDomino;
import plateau.Plateau;

import java.util.List;

public class DifficileIA extends AbstractIA {

    private PlacementDomino positionDomino;

    public DifficileIA(String nom, Roi couleur, NbJoueur nbJoueur, ModeJeu modeJeu, int score) throws DominoException, TuileException {
        super(nom, couleur, nbJoueur, modeJeu, score);
    }

    @Override
    public int pickInPioche(List<IDomino> cartesSurBoard, List<Joueur> joueursAdverses) throws DominoException, TuileException {
/*        for (Joueur j : joueursAdverses) {
            Plateau p = j.getPlateau();
            if(j.getNbRois() < j.getPioche().size()){
                for (IDomino d : cartesSurBoard) {
                    List<PlacementDomino> possibilites = j.getPlateau().possibilite(d);
                    PlacementDomino best = possibilites.get(0);
                    int score = j.getScore();
                    for (PlacementDomino pos : possibilites) {
                        p.addDomino(pos.getDomino(),pos.getRowCase2(),pos.getColCase2(),pos.getCaseId(),pos.getSens());
                        int scorePos = p.calculPoint();
                        if(score < scorePos)
                            best = pos;
                    }
                }
            }
        }*/
        Plateau p = this.getPlateau();
        List<PlacementDomino> possibilites;
        positionDomino = null;
        for (IDomino d : cartesSurBoard) {
            possibilites = this.getPlateau().possibilite(d);
            int score = this.getScore();
            for (PlacementDomino pos : possibilites) {
                p.addDomino(pos.getDomino(),pos.getRowCase2(),pos.getColCase2(),pos.getCaseId(),pos.getSens());
                int scorePos = p.calculPoint();
                if(score < scorePos)
                    positionDomino = pos;
            }
        }
        if(positionDomino != null) {
            for (int i = 0; i < cartesSurBoard.size(); ++i) {
                if (cartesSurBoard.get(i).getIdentifiant() == positionDomino.getDomino().getIdentifiant())
                    return i;
            }
            return 0;
        } else return 0;
    }

    @Override
    public PlacementDomino pickPossibilite(IDomino domino) throws Exception {
        if(positionDomino == null)
        {
            throw new Exception("Impossible pour l'IA de placer le domino");
        }
        return positionDomino;
    }
}
