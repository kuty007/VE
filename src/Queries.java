import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class Queries {
    String queryType;
    String query;
    String queryNode;
    String queryNodeName;
    String[] evidence;
    String[] evidenceVariablesNames;
    String[] hiddenVariables;
    String[] queryAndEvidenceVariables;
    BayesianNetwork bn;
    AtomicInteger multiplyCounter = new AtomicInteger(0);
    AtomicInteger addCounter = new AtomicInteger(0);
    double queryProbability = 0;

    public Queries(String query, BayesianNetwork bn) {
        this.queryType =  query.substring(query.length()-1);
        this.query = query;
        this.bn = bn;
        String s = query.substring(query.indexOf("(") + 1, query.indexOf(")"));
        String[] arr = s.split("\\|");
        queryNode = arr[0];
        queryNodeName = queryNode.substring(0, queryNode.indexOf("="));
        evidence = arr[1].split(",");
        //crate an array of evidence variables names
        String[] evidenceVariablesNames = new String[evidence.length];
        for (int i = 0; i < evidence.length; i++) {
            evidenceVariablesNames[i] = evidence[i].substring(0, evidence[i].indexOf("="));
        }
        this.evidenceVariablesNames = evidenceVariablesNames;


        //create an array of hidden variables names
        hiddenVariables = new String[bn.BN.size() - 1 - evidenceVariablesNames.length];
        System.out.println(hiddenVariables.length);
        System.out.println(bn.BN.size());
        int i = 0;
        for (String key : bn.BN.keySet()) {
            if (!Arrays.asList(evidenceVariablesNames).contains(key) && (!queryNodeName.equals(key))) {
                hiddenVariables[i] = key;
                i++;


            }


        }
        //create an array of query and evidence variables names
        queryAndEvidenceVariables = new String[evidenceVariablesNames.length + 1];
        queryAndEvidenceVariables[0] = queryNodeName;
        System.arraycopy(evidenceVariablesNames, 0, queryAndEvidenceVariables, 1, queryAndEvidenceVariables.length - 1);
    }

    public ArrayList<HashMap<Variable, LinkedHashMap<String, Double>>> SimpleSolve() {
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


//        System.out.println("combinations: " + combinations + " size: " + combinations.size());
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
        restCounters();
        double sum = 0;

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
        System.out.println("stepSize: " + stepSize);
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
        int counter = 0;
        for (int i = 0; i < bn.BN.get(queryNodeName).outcomes.size(); i++) {
            counter++;
            //add the query node with outcome i to the combinations and calculate the probability
            ArrayList<String> newCombinations = new ArrayList<>();
            for (String combination : combinations) {
                newCombinations.add(combination + "," + queryNodeName + "=" + bn.BN.get(queryNodeName).outcomes.get(i));
            }
            System.out.println(newCombinations);
            int counter2 = 0;
            for (String combination : newCombinations) {
                counter2++;

                double value = combinationValue(combination);
                if ((queryNodeName + "=" + bn.BN.get(queryNodeName).outcomes.get(i)).equals(queryNode)) {
                    queryProbability += value;
                }
                if (counter == 1 && counter2 == 1) {
                    sum = value;
                } else {
                    sum += value;
                    addCounter.getAndIncrement();
                }

                System.out.println("P(" + combination + ") = " + value);
                System.out.println("multiplyCounter: " + multiplyCounter.get() + " addCounter: " + addCounter.get());
            }
        }
        System.out.println(query + "=" + String.format("%.5f", (queryProbability / (sum))) + " multiplyCounter: " + multiplyCounter.get() + " addCounter: " + addCounter.get());


    }


    public double combinationValue(String combination) {
        String[] arr = combination.split(",");
        double multiply = 0;
        int counter = 0;
        for (String s : arr) {
            counter++;
            if (this.bn.BN.get(s.substring(0, s.indexOf("="))).parents.size() == 0) {
                if (counter == 1) {
                    multiply = this.bn.BN.get(s.substring(0, s.indexOf("="))).cpt.get(s + " ");
                } else {
                    multiply *= this.bn.BN.get(s.substring(0, s.indexOf("="))).cpt.get(s + " ");
                    multiplyCounter.getAndIncrement();
                }
            } else {

                StringBuilder parentValues = new StringBuilder();
                for (String parent : (this.bn.BN.get(s.substring(0, s.indexOf("="))).parents)) {
                    for (String s1 : arr) {
                        if (s1.contains(parent)) {
                            parentValues.append(s1).append(" ");
                        }
                    }
                }
                if (counter == 1) {
                    multiply = this.bn.BN.get(s.substring(0, s.indexOf("="))).cpt.get(parentValues + s + " ");
                } else {

                    multiply *= this.bn.BN.get(s.substring(0, s.indexOf("="))).cpt.get(parentValues + s + " ");
                    this.multiplyCounter.getAndIncrement();
                }

            }

        }
        return multiply;
    }

    public void getRelevantCpt() {
        for (String Vari : bn.BN.keySet()) {
            bn.BN.get(Vari).cptCopy = bn.BN.get(Vari).cpt;
            StringBuilder relventKey = new StringBuilder();
            for (int i = 0; i < evidenceVariablesNames.length; i++) {
                if (bn.BN.get(Vari).parents.contains(evidenceVariablesNames[i]) || bn.BN.get(Vari).name.equals(evidenceVariablesNames[i])) {
                    relventKey.append(evidence[i]).append(" ");
                }
            }

            if (relventKey.length() != 0) {
                LinkedHashMap<String, Double> cpt = new LinkedHashMap<>();
                for (String key : bn.BN.get(Vari).cpt.keySet()) {
                    if (key.contains(relventKey.toString())) {
                        cpt.put(key, bn.BN.get(Vari).cpt.get(key));
                    }
                }
                {
                    bn.BN.get(Vari).cpt = cpt;
                }

            }
        }
    }
    public void restCounters(){
        multiplyCounter.set(0);
        addCounter.set(0);
    }

}





