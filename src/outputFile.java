import java.util.ArrayList;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
public class outputFile {
    ArrayList<Queries> queries;

    public outputFile(ArrayList<Queries> queries) {
        this.queries = queries;
    }

    public void writeToFile() {
        try {
            PrintWriter writer = new PrintWriter("outputFile.txt", "UTF-8");
            for (Queries q : queries) {
               if (q.queryType.equals("1")) {
                    writer.println(q.solve());
                } else {
                   VariableElimination ve = new VariableElimination(q.bn,q);
                     writer.println(ve.answer());
                    ;
                }
            }
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
//Create a new file: src\outputFile.txt and write the results to it
// Path: src\outputFile.txt
// Compare this snippet from src\outputFile.java:
// import java.util.ArrayList;
//
// public class outputFile {
//     ArrayList<Queries> queries;
//
//     public outputFile(ArrayList<Queries> queries) {
//         this.queries = queries;
//     }
// }
// Create a new file: src\outputFile.txt and write the results to it
// Path: src\outputFile.txt






