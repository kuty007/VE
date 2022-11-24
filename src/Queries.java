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

    public ArrayList<HashMap<Variable,LinkedHashMap<String, Double>>> SimpleSolve() {
        //solve the query using the simple enumeration algorithm
        ArrayList<HashMap<Variable,LinkedHashMap<String, Double>>> quryResults = new ArrayList<>();
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
                quryResults.add(new HashMap<Variable,LinkedHashMap<String, Double>>(){{put(bn.BN.get(hiddenVariable),cpt);}});
            } else {
                quryResults.add(new HashMap<Variable,LinkedHashMap<String, Double>>(){{put(bn.BN.get(hiddenVariable),bn.BN.get(hiddenVariable).cpt);}});
            }
        }
        //find all combinations of hidden variables keys with each other only if in both of them the evidence variables have the same outcomes
        ArrayList<String> combinations = new ArrayList<>();
        for (int i = 0; i < quryResults.size(); i++) {
            for (int j = i + 1; j < quryResults.size(); j++) {
                for (Variable key1 : quryResults.get(i).keySet()) {
                    for (Variable key2 : quryResults.get(j).keySet()) {
                        ArrayList<String>parents=key1.commonParents(key2);
                        if(parents.size()!=0){



                        }
                    }
                }
            }
        }
        System.out.println("combinations: " + combinations);


        return quryResults;
    }
}





