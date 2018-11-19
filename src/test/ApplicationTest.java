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
        assertEquals(casePlateau.getTerrain().name(),Terrain.Champs.name());
    }
}