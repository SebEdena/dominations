package jeu;

import com.sun.xml.internal.bind.v2.model.core.ID;
import exceptions.DominoException;
import exceptions.TuileException;
import javafx.util.Pair;
import plateau.*;
import sun.nio.cs.ext.ISCII91;
import util.CSVParser;

import java.io.IOException;
import java.util.*;

public class Partie {

    private List<Joueur> joueurs;
    private List<IDomino> dominosRestants;
    private List<IDomino> tirage;
    private NbJoueur nbJoueur;
    private ModeJeu modeJeu;

    public Partie(List<Joueur> joueurs, List<IDomino> deckDominos, NbJoueur nbJoueur, ModeJeu modeJeu) {
        this.nbJoueur = nbJoueur;
        this.modeJeu = modeJeu;
        this.dominosRestants = deckDominos;
        this.joueurs = joueurs;
        retirerDominos();
    }

    public boolean partieFinie(){
        return dominosRestants.size() == 0 && tirage == null; //vérifier que le tirage n'est pas vide aussi. c'est bien is null car à chaque fois on clear le tirage
    }

    public NbJoueur getParamJeu() {
        return nbJoueur;
    }

    public Joueur getJoueur(Roi roi){
        for (Joueur j : joueurs){
            if(j.getCouleurRoi().equals(roi)) return j;
        }
        return null;
    }

    public IDomino getDominoPioche(int i) throws ArrayIndexOutOfBoundsException {
        return tirage.remove(i);
    }

    private void retirerDominos(){
        int nbDominos = nbJoueur.getNbDominosRetires();
        Random rand = new Random();
        for (int i = 0; i < nbDominos; i++) {
            int index = rand.nextInt(dominosRestants.size());
            dominosRestants.remove(index);
        }
    }

    public List<Roi> melangerRois(){
        List<Roi> rois = new ArrayList<>();
        for(Joueur j : joueurs){
            for(int i = 0; i < j.getNbRois(); i++){
                rois.add(j.getCouleurRoi());
            }
        }
        Collections.shuffle(rois);
        return rois;
    }

    public List<IDomino> pioche(){
        for (Joueur j : joueurs){
            j.resetTirage();
        }
        Random mainInnocente = new Random();
        tirage = null;
        if(!dominosRestants.isEmpty()){
            Collections.shuffle(dominosRestants);
            tirage = new ArrayList<IDomino>();
            for (int i = 0; i < nbJoueur.getNbRoiParJoueur()*nbJoueur.getNbJoueurs(); i++) {
                int indexDomino = mainInnocente.nextInt(dominosRestants.size());
                tirage.add(dominosRestants.remove(indexDomino));
            }
            tirage.sort(Comparator.comparingInt(IDomino::getIdentifiant));
        }
        return tirage;
    }

    public List<Pair<IDomino, Joueur>> getTourOrder(){
        if(tirage != null && tirage.size() != 0){
            List<Pair<IDomino, Joueur>> order = new ArrayList<>();
            for(IDomino d : tirage){
                boolean playerFound = false;
                for(int i = 0; i < joueurs.size() && !playerFound; i++){
                    if(joueurs.get(i).piocheContainsDomino(d)){
                        order.add(new Pair<>(d, joueurs.get(i)));
                        playerFound = true;
                    }
                }
                if(!playerFound) throw new IllegalStateException("Pas de joueur pour ce domino !");
            }
            return order;
        }else{
            return null;
        }
    }

    public List<Joueur> calculScores(){
        List<Joueur> resultats = new ArrayList<>();
        for(Joueur j : joueurs){
            j.calculScore();
            resultats.add(j);
        }
        resultats.sort((j1, j2) -> {
            if(j1.getScore() == j2.getScore())
                if(j1.getScoreDomaine() == j2.getScoreDomaine())
                    if(j1.getScoreCouronne() == j2.getScoreCouronne()){
                        j1.setEgalite(true);
                        j2.setEgalite(true);
                        return 0;
                    }
                    else return j2.getScoreCouronne() - j1.getScoreCouronne();
                else return j2.getScoreDomaine() - j1.getScoreDomaine();
            else return j2.getScore() - j1.getScore();
        });
        return resultats;
    }

    public int getNbDominoPioche()
    {
        return tirage.size();
    }

    public List<IDomino> getTirage() {
        return tirage;
    }

    public List<Joueur> getJoueurs(){
        return joueurs;
    }
}
