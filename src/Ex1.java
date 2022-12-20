import java.io.PrintWriter;

public class Ex1 {

    public static void main(String[] args) {
//        try {
////           //if we want to load the input file from the command line
//            writeToFile(args[0]);
////            }
//        } catch (Exception e) {
////load the default input file only if the user didn't enter a file name
//
//            writeToFile("input.txt");
//        }
        BayesianNetwork bn = new BayesianNetwork();
        bn.loadBnFromXml("C:\\Users\\asaf7\\IdeaProjects\\algo\\src\\alarm_net.xml");
        Queries q = new Queries("P(M=T|J=T,A=T),1", bn);
        bayesBall bb = new bayesBall(q, bn);
        System.out.println(bb.bayesBallAlgo(bn.BN.get("M"), bn.BN.get("B")));


    }

    /**
     * this function loads the input file and writes the output to the output file
     *
     * @param path
     */
    public static void writeToFile(String path) {
        try {
            LoadInputFile inputFile = new LoadInputFile(path);
            PrintWriter writer = new PrintWriter("output.txt", "UTF-8");
            for (Queries q : inputFile.queries) {
                try {
                    if (q.queryType.equals("1")) {
                        writer.println(q.solve());
                    } else {
                        VariableElimination ve = new VariableElimination(q.bn, q);
                        writer.println(ve.answer());
                    }
                } catch (Exception e) {
                    writer.println("error");
                }
            }
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}






