import java.util.*;

public class Queries {
    String query;
    String queryNode;
    String[] evidence;
    String[] evidenceVariablesNames;
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
        String[] evidenceVariablesNames = new String[evidence.length];
        for (int i = 0; i < evidence.length; i++) {
            evidenceVariablesNames[i] = evidence[i].substring(0, evidence[i].indexOf("="));
        }
        this.evidenceVariablesNames = evidenceVariablesNames;


        //create an array of hidden variables names
        hiddenVariables = new String[bn.BN.size() - 1 - evidenceVariablesNames.length];
        int i = 0;
        for (String key : bn.BN.keySet()) {
            if (!Arrays.asList(evidenceVariablesNames).contains(key) && (!queryNode.substring(0, 1).equals(key))) {
                hiddenVariables[i] = key;
                i++;
            }


        }
    }

    public ArrayList<LinkedHashMap<String, Double>> SimpleSolve() {
        //solve the query using the simple enumeration algorithm
        ArrayList<LinkedHashMap<String, Double>> quryResults = new ArrayList<>();
        for (String hiddenVariable : hiddenVariables) {
            StringBuilder relventKey = new StringBuilder();
            for (int i = 0; i < evidenceVariablesNames.length; i++) {
                if (bn.BN.get(hiddenVariable).parents.contains(evidenceVariablesNames[i])) {
                    relventKey.append(evidence[i]).append(" ");
                }
            }
            LinkedHashMap<String, Double> cpt = new LinkedHashMap<>();
            if (relventKey.length() != 0) {
                for (String key : bn.BN.get(hiddenVariable).cpt.keySet()) {
                    if (key.contains(relventKey.toString())) {
                        cpt.put(key, bn.BN.get(hiddenVariable).cpt.get(key));
                    }
                }
                quryResults.add(cpt);
            }
            else {
                quryResults.add(bn.BN.get(hiddenVariable).cpt);
            }





        }
        //find all combinations of hidden variables keys with each other
//        ArrayList<String> combinations = new ArrayList<>();
//
//        for (int i = 0; i < quryResults.size(); i++) {
//            for (int j = i + 1; j < quryResults.size(); j++) {
//                for (String key1 : quryResults.get(i).keySet()) {
//                    for (String key2 : quryResults.get(j).keySet()) {
//                        if (!combinations.contains(key1 + key2)) {
//                            combinations.add(key1 + key2);
//                        }
//                    }
//                }
//            }
//        }
        return quryResults;
    }
}




