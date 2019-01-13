/**
 * Classe permettant de décrire une partie de jeu
 * @author Mathieu Valentin, Sébastien Viguier, Laurent Yu
 * @version 1.0
 */
package model.joueur;

import model.exceptions.DominoException;
import model.exceptions.TuileException;
import model.jeu.ModeJeu;
import model.jeu.NbJoueur;
import model.jeu.Roi;
import model.plateau.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class NormalIA extends AbstractIA{

    private List<PlacementDomino> positionDomino = new ArrayList<>();

    /**
     * Constructeur d'un IA normal
     * @param nom Pseudo de l'IA
     * @param couleur Roi attribué à l'IA
     * @param nbJoueur Paramètre du jeu
     * @param modeJeu Mode du jeu
     * @param score Score de départ de l'IA
     * @throws DominoException
     * @throws TuileException
     * @see AbstractIA
     */
    public NormalIA(String nom, Roi couleur, NbJoueur nbJoueur, ModeJeu modeJeu, int score) throws DominoException, TuileException {
        super(nom, couleur, nbJoueur, modeJeu, score);
    }

    /**
     * Methode permettant de choisir un domino dans la pioche
     * @param cartesSurBoard Pioche contenant les dominos restants
     * @param joueursAdverses Liste des joueurs en jeu
     * @return Le numéro du domino à piocher
     * @throws DominoException
     * @throws TuileException
     * @see Plateau#calculPoint
     * @see #getPlacement
     * @see Domino#getIdentifiant
     */
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

    /**
     * Methode permettant de retourner le placement qui donnerai le gros score à l'IA
     * @param score Score de départ
     * @param possibilites La liste des possibilités pour les dominos de la pioche
     * @param placement Meilleur placement possible
     * @return Le placement le plus avantageux
     * @throws DominoException
     * @throws TuileException
     * @see #clonePlateau
     * @see PlacementDomino#getDomino
     * @see PlacementDomino#getRow
     * @see PlacementDomino#getColumn
     * @see PlacementDomino#getCaseId
     * @see PlacementDomino#getSens
     * @see Plateau#calculPoint
     */
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

    /**
     * Methode permettant de créer une nouvelle instance du plateau du plateau de l'IA
     * @param plateau Instance du plateau de l'IA
     * @return Le plateau cloné
     * @see Plateau#getSize
     * @see Plateau#setCaseAt
     * @see Plateau#getCaseAt
     * @see Plateau#setTuileAjoutee
     * @see Plateau#setXBound
     * @see Plateau#getXBounds
     * @see Plateau#setYBound
     * @see Plateau#getYBounds
     */
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

    /**
     * Methode permettant de choisir le placement d'un domino
     * @param domino Domino à placer
     * @return La position du domino à placer
     * @throws Exception
     * @see Plateau#calculPoint
     * @see Plateau#possibilite
     * @see #getPlacement
     */
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
