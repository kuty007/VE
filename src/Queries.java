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

    public String solve() {
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
       String ans = query + "=" + String.format("%.5f", (queryProbability / (sum))) + " multiplyCounter: " + multiplyCounter.get() + " addCounter: " + addCounter.get();
        System.out.println(ans);
        return ans;


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





