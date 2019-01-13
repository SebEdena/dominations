/**
 * Classe permettant de parser un fichier csv en une liste de domino en string
 * @author Mathieu Valentin, Sébastien Viguier, Laurent Yu
 * @version 1.0
 */
package model.util;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CSVParser {

    /**
     * Methode permetant de parser un fichier csv en une liste de domino en string
     * @param filePath Adresse de la location du fichier csv
     * @param delimiter Caractère délimitant les données
     * @param ignoreFirstLine (true) si la première ligne contient les titres des colonnes, (false) sinon
     * @return Liste de domino en string
     * @throws IOException
     */
    public static List<String[]> parse(String filePath, String delimiter, boolean ignoreFirstLine) throws IOException {
        BufferedReader file = new BufferedReader(new FileReader(filePath));
        String line = null;
        List<String[]> result = new ArrayList<String[]>();

        if(ignoreFirstLine){
            file.readLine();
        }

        while((line = file.readLine()) != null){
            result.add(line.split(delimiter));
        }

        try{ file.close(); } catch (IOException e){}

        return result;
    }
}
