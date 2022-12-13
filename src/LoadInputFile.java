import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

public class LoadInputFile {
    BayesianNetwork bn;
    ArrayList<Queries> queries;

    public LoadInputFile(String path) {
        try {
            File file = new File((path));
            Scanner scanner = new Scanner(file);
            String XmlPath = "";
            if (scanner.hasNextLine()) {
                XmlPath = scanner.nextLine();
                this.bn.loadBnFromXml("src/"+XmlPath);
            }
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                Queries q = new Queries(line, bn);
                this.queries.add(q);
            }
            scanner.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

}


