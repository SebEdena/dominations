package test;

import org.junit.Test;
import plateau.Case;

import static org.junit.Assert.*;

public class ApplicationTest {

    @Test
    public void CaseTest(){
        Case casePlateau = new Case(1,1);
        assertTrue(casePlateau.getX() == 1);
        assertFalse(casePlateau.getY() == 2);
    }

}