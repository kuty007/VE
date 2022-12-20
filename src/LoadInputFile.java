import java.io.File;
import java.io.FileNotFoundException;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Scanner;

public class LoadInputFile {
    BayesianNetwork bn;
    ArrayList<Queries> queries;

    public LoadInputFile(String path) {
        this.bn = new BayesianNetwork();
        this.queries = new ArrayList<>();
        try {
            File file = new File((path));
            Scanner scanner = new Scanner(file);
            String XmlPath = "";
            if (scanner.hasNextLine()) {
                XmlPath = scanner.nextLine();
                String absolutePath = FileSystems.getDefault().getPath(XmlPath).toAbsolutePath().toString();
                System.out.println(absolutePath);
                this.bn.loadBnFromXml(absolutePath);
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


