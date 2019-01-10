package test;

import jeu.Joueur;
import jeu.Roi;
import org.junit.Test;
import plateau.Case;
import plateau.Terrain;

import static org.junit.Assert.*;

public class ApplicationTest {

    @Test
    public void CaseTest(){
        Case casePlateau = new Case(1, Terrain.CHAMPS);
        assertTrue(casePlateau.getNbCouronne() == 1);
        assertEquals(casePlateau.getTerrain().name(),Terrain.CHAMPS.name());
    }

    @Test
    public void RoiTest(){
//        Joueur joueur = new Joueur("John", Roi.Jaune,0,);
//        assertEquals(joueur.getCouleurRoi().name(), "Jaune");
//        assertFalse(joueur.getScore() == 10);
    }
}