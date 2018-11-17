import util.CSVParser;

import java.util.Arrays;
import java.util.List;

public class Main {

    public static void main(String[] args) throws Exception {
        List<String[]> dominos = CSVParser.parse("./dominos.csv", ",", true);

        for(String[] strs : dominos){
            System.out.println(Arrays.toString(strs));
        }
    }
}
