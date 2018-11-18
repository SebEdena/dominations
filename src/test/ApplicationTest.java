package test;

import org.junit.Test;
import plateau.Case;
import plateau.Terrain;

import static org.junit.Assert.*;

public class ApplicationTest {

    @Test
    public void CaseTest(){
        Case casePlateau = new Case(1, Terrain.Champs);
        assertTrue(casePlateau.getNbCouronne() == 1);
        assertTrue(casePlateau.getTerrain().equals(Terrain.Champs.name()));
    }
}