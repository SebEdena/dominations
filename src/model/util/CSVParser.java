package model.util;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CSVParser {

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
