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
        AtomicInteger multiplyCounter = new AtomicInteger();
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
        //find all combinations of hidden variables keys with each other only if in both of them the evidence variables
        // have the same outcomes and add team and their values to a new hashmap
        ArrayList<HashMap<String, Double>> combinations = new ArrayList<>();
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
                                            // add the keys and their values to a new hashmap
                                            HashMap<String, Double> newCpt = new HashMap<>();
                                            newCpt.put(key1Value, quryResults.get(i).get(key1).get(key1Value));
                                            newCpt.put(key2Value, quryResults.get(j).get(key2).get(key2Value));

                                            combinations.add(newCpt);
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
                                        HashMap<String, Double> newCpt = new HashMap<>();
                                        newCpt.put(key1Value, quryResults.get(i).get(key1).get(key1Value));
                                        newCpt.put(key2Value, quryResults.get(j).get(key2).get(key2Value));
                                        combinations.add(newCpt);
                                    }
                                }
                            }

                        }
                    }


                }
            }
        }


        System.out.println("combinations: " + combinations + " size: " + combinations.size());
//        for (int i = 0; i <bn.BN.get(queryNode.substring(0,1)).outcomes.size() ; i++) {
//            double sum=0;
//            for (String combination : combinations) {
//                String[] arr = combination.split(",");
//                double multiply = 1;
//                for (String s : arr) {
//                    multiply *= quryResults.get(0).get(bn.BN.get(s.substring(0, s.indexOf("=")))).get(s);
//                    multiplyCounter.getAndIncrement();
//                }
//                multiply *= bn.BN.get(queryNode).cpt.get(queryNode + "=" + bn.BN.get(queryNode).outcomes.get(i) + " " + combination);
//                multiplyCounter.getAndIncrement();
//                sum += multiply;
//                addCounter.getAndIncrement();
//            }
//            System.out.println("P("+queryNode+"="+bn.BN.get(queryNode).outcomes.get(i)+"|"+query.substring(query.indexOf("(")+1,query.indexOf(")"))+") = "+sum);


        return quryResults;
    }

    public void solve() {
        //get the combinations of the hidden variables outcomes and the evidence variables outcomes
        StringBuilder base = new StringBuilder();
        for (int i = 0; i < evidenceVariablesNames.length; i++) {
            base.append(evidence[i]).append(",");
        }
        int numOfCombinations = 1;
        for (String hiddenVariable : hiddenVariables) {
            numOfCombinations *= bn.BN.get(hiddenVariable).outcomes.size();
        }
        ArrayList<String> combinations = new ArrayList<>();
        for (int i = 0; i < numOfCombinations; i++) {
            combinations.add(base.toString());
        }

        int stepSize = combinations.size();
        for (String hiddenVariable : hiddenVariables) {
            stepSize /= bn.BN.get(hiddenVariable).outcomes.size();
            int counter = 0;
            int i = 0;
            int j = 0;
            while (i < combinations.size()) {
                if (counter < stepSize) {
                    combinations.set(i, combinations.get(i) + hiddenVariable + "=" + bn.BN.get(hiddenVariable).outcomes.get(j) + ",");
                    counter++;
                    i++;

                } else {
                    counter = 0;
                    j++;
                    j %= bn.BN.get(hiddenVariable).outcomes.size();
                }
            }
        }
        //remove the last comma from each combination
        combinations.replaceAll(s -> s.substring(0, s.length() - 1));

        for (int i = 0; i < bn.BN.get(queryNode.substring(0, 1)).outcomes.size(); i++) {
            //add the query node with outcome i to the combinations and calculate the probability
            ArrayList<String> newCombinations = new ArrayList<>();
            for (String combination : combinations) {
                newCombinations.add(combination + "," + queryNode.substring(0, 1) + "=" + bn.BN.get(queryNode.substring(0, 1)).outcomes.get(i));
            }
            System.out.println(newCombinations);
            for (String combination : newCombinations) {
                double value = combinationValue(combination);
                System.out.println("P(" + combination +") = " + value);


            }


        }


    }


    public double combinationValue(String combination) {
        String[] arr = combination.split(",");
        double multiply = 1;
        for (String s : arr) {
            AtomicInteger multiplyCounter = new AtomicInteger();
            if (this.bn.BN.get(s.substring(0, s.indexOf("="))).parents.size() == 0) {
                multiply *= this.bn.BN.get(s.substring(0, s.indexOf("="))).cpt.get(s + " ");
                multiplyCounter.getAndIncrement();
            } else {
                ;
                StringBuilder parentValues = new StringBuilder();
                for (String parent : (this.bn.BN.get(s.substring(0, s.indexOf("="))).parents)) {
                    for (String s1 : arr) {
                        if (s1.contains(parent)) {
                            parentValues.append(s1).append(" ");
                        }
                    }
                }
                multiply *= this.bn.BN.get(s.substring(0, s.indexOf("="))).cpt.get(parentValues + s + " ");
                multiplyCounter.getAndIncrement();
            }

        }
        return multiply;

    }
}





