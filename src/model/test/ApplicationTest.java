package model.test;

import model.joueur.Joueur;
import model.jeu.ModeJeu;
import model.jeu.NbJoueur;
import model.jeu.Roi;
import org.junit.Test;
import model.plateau.Case;
import model.plateau.Terrain;

import static org.junit.Assert.*;

public class ApplicationTest {

    @Test
    public void CaseTest(){
        Case casePlateau = new Case(1, Terrain.CHAMPS);
        assertTrue(casePlateau.getNbCouronne() == 1);
        assertEquals(casePlateau.getTerrain().name(),Terrain.CHAMPS.name());
    }

    @Test
    public void RoiTest() throws Exception {
        Joueur joueur = new Joueur("John", Roi.Jaune, NbJoueur.jeuA2, ModeJeu.STANDARD, 0);
        assertEquals(joueur.getCouleurRoi().name(), "Jaune");
        assertFalse(joueur.getScore() == 10);
    }
}