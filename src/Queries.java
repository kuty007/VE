import java.util.Arrays;
import java.util.Collections;
import java.util.Objects;

public class Queries {
    String query;
    String queryNode;
    String[] evidence;
    String[] hiddenVariables;
    BayesianNetwork bn;

    public Queries(String query, BayesianNetwork bn) {
        this.query = query;
        this.bn = bn;
        String s = query.substring(query.indexOf("(") + 1, query.indexOf(")"));
        String[] arr = s.split("\\|");
        queryNode = arr[0];
        evidence = arr[1].split(",");
        //crate an array of evidence variables names
        String[] evidenceVariables = new String[evidence.length];
        for (int i = 0; i < evidence.length; i++) {
            evidenceVariables[i] = evidence[i].substring(0, evidence[i].indexOf("="));
        }


        //create an array of hidden variables names
        hiddenVariables = new String[bn.BN.size() -1- evidenceVariables.length];
        int i = 0;
        System.out.println(queryNode.substring(0, 1));
        for (String key : bn.BN.keySet()) {
            if (!Arrays.asList(evidenceVariables).contains(key)&&(!queryNode.substring(0, 1).equals(key))) {
                hiddenVariables[i] = key;
                i++;
            }


        }
    }
}



