import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

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

    public ArrayList<HashMap<Variable, LinkedHashMap<String, Double>>> SimpleSolve() {
        AtomicInteger addCounter = new AtomicInteger();
        AtomicInteger multiplyCounter= new AtomicInteger();
        //solve the query using the simple enumeration algorithm
        ArrayList<HashMap<Variable, LinkedHashMap<String, Double>>> quryResults = new ArrayList<>();
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
                quryResults.add(new HashMap<Variable, LinkedHashMap<String, Double>>() {{
                    put(bn.BN.get(hiddenVariable), cpt);
                }});
            } else {
                quryResults.add(new HashMap<Variable, LinkedHashMap<String, Double>>() {{
                    put(bn.BN.get(hiddenVariable), bn.BN.get(hiddenVariable).cpt);
                }});
            }
        }
        //find all combinations of hidden variables keys with each other only if in both of them the evidence variables have the same outcomes
        ArrayList<String> combinations = new ArrayList<>();
        for (int i = 0; i < quryResults.size(); i++) {
            for (int j = i + 1; j < quryResults.size(); j++) {
                for (Variable key1 : quryResults.get(i).keySet()) {
                    for (Variable key2 : quryResults.get(j).keySet()) {
                        ArrayList<String> parents = key1.commonParents(key2);
                        if (parents.size() != 0) {
                            for (String parent : parents) {
                                for (String key1Value : quryResults.get(i).get(key1).keySet()) {
                                    for (String key2Value : quryResults.get(j).get(key2).keySet()) {
                                        if (key1Value.contains(key2Value.substring(key2Value.indexOf(parent), key2Value.indexOf(" ", key2Value.indexOf(parent))))) {
                                            combinations.add(key1Value + "," + key2Value);
                                        }
                                    }
                                }
                            }
                            //if the two variables have no common parents but one of them is a parent of the other

                        } else if (key1.parents.contains(key2.name)
                                || key2.parents.contains(key1.name)) {
                            for (String key1Value : quryResults.get(i).get(key1).keySet()) {
                                for (String key2Value : quryResults.get(j).get(key2).keySet()) {
                                    if (key1Value.contains(key2Value.substring(key2Value.indexOf(key2.name), key2Value.indexOf(" ", key2Value.indexOf(key2.name)))) ||
                                            key2Value.contains(key1Value.substring(key1Value.indexOf(key1.name), key1Value.indexOf(" ", key1Value.indexOf(key1.name))))) {
                                        combinations.add(key1Value + "," + key2Value);
                                    }
                                }
                            }

                        }
                    }


                }
            }
        }


//        System.out.println("combinations: " + combinations+ " size: "+combinations.size());
//        //calculate the probability of each combination
//        ArrayList<Double> probabilities = new ArrayList<>();
//        for (String combination : combinations) {
//            double probability = 1;
//            for (int i = 0; i < quryResults.size(); i++) {
//                for (Variable key : quryResults.get(i).keySet()) {
//                    probability *= quryResults.get(i).get(key).get(combination);
//                }
//            }
//            probabilities.add(probability);
//        }
//        System.out.println("probabilities: " + probabilities);
//        //calculate the probability of the query




        return quryResults;
    }
}





