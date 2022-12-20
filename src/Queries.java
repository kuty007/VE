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

    /**
     * this function gets the query as a string and the bayesian network and parses the query
     * to the relevant variables for solving the query
     *
     * @param query
     * @param bn
     */
    public Queries(String query, BayesianNetwork bn) {
        this.queryType = query.substring(query.length() - 1);//get the query type
        this.query = query;
        this.bn = bn;
        String s = query.substring(query.indexOf("(") + 1, query.indexOf(")"));//get the query without the brackets
        String[] arr = s.split("\\|");//split the query to the query node and the evidence
        queryNode = arr[0];//get the query node
        queryNodeName = queryNode.substring(0, queryNode.indexOf("="));//get the query node name
        evidence = arr[1].split(",");//get the evidence
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

    /**
     * this function solves the query type 1
     * Basic inference
     */
    public String solve() {
        if (directAns()!=null){
            return directAns();
        }
        restCounters();//reset the counters
        double sum = 0;//initialize the sum

        //get the combinations of the hidden variables outcomes and the evidence variables outcomes
        StringBuilder base = new StringBuilder();//create a string builder for the base
        for (int i = 0; i < evidenceVariablesNames.length; i++) {
            base.append(evidence[i]).append(",");//add the evidence variables outcomes to the base
        }
        //find all the combinations of the hidden variables outcomes
        int numOfCombinations = 1;
        for (String hiddenVariable : hiddenVariables) {
            numOfCombinations *= bn.BN.get(hiddenVariable).outcomes.size();
        }
        ArrayList<String> combinations = new ArrayList<>();
        for (int i = 0; i < numOfCombinations; i++) {
            combinations.add(base.toString());
        }
        //add the hidden variables outcomes to the combinations array
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
//            System.out.println(newCombinations);
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

//                System.out.println("P(" + combination + ") = " + value);
//                System.out.println("multiplyCounter: " + multiplyCounter.get() + " addCounter: " + addCounter.get());
            }
            System.out.println("sum="+sum +bn.BN.get(queryNodeName).outcomes.get(i)) ;
        }
        System.out.println(" מווו"+queryProbability);
        System.out.println(sum-queryProbability);
        System.out.println(sum);
        String ans = String.format("%.5f", (queryProbability / (sum))) + "," + addCounter.get() + "," + multiplyCounter.get();
//        System.out.println(ans);
        return ans;


    }

    /**
     * this function gets a combination of variables and returns the probability of the combination
     *
     * @param combination
     * @return
     */
    public double combinationValue(String combination) {
        String[] arr = combination.split(",");//split the combination to the variables
        double multiply = 0;
        int counter = 0;
        for (String s : arr) {
            counter++;
            if (this.bn.BN.get(s.substring(0, s.indexOf("="))).parents.size() == 0) {//if the variable has no parents
                if (counter == 1) {
                    multiply = this.bn.BN.get(s.substring(0, s.indexOf("="))).cpt.get(s + " ");//get the probability of the variable
                } else {
                    multiply *= this.bn.BN.get(s.substring(0, s.indexOf("="))).cpt.get(s + " ");
                    multiplyCounter.getAndIncrement();
                }
            } else {//if the variable has parents

                StringBuilder parentValues = new StringBuilder();//create a string builder for the parents values
                for (String parent : (this.bn.BN.get(s.substring(0, s.indexOf("="))).parents)) {
                    for (String s1 : arr) {
                        if (s1.substring(0,s1.indexOf("=")).equals((parent))){
                            parentValues.append(s1).append(" ");
                        }
                    }
                }
                if (counter == 1) {
                    multiply = this.bn.BN.get(s.substring(0, s.indexOf("="))).cpt.get(parentValues + s + " ");//get the probability of the variable
                } else {

                    multiply *= this.bn.BN.get(s.substring(0, s.indexOf("="))).cpt.get(parentValues + s + " ");
                    this.multiplyCounter.getAndIncrement();
                }

            }

        }
        return multiply;//return the probability of the combination
    }

    /**
     * this function resets the counters
     */
    public void restCounters() {
        multiplyCounter.set(0);
        addCounter.set(0);
    }
    /**
     * this function check if we can solve the query immediately
     * by check if all the parents of the query variable are evidence variables
     * and if they are get the value of the query  from the query variable cpt
     * @return the result of the query or null if we can't solve the query immediately
     */
    public String directAns() {
        int counter = 0;
        if (bn.BN.get(this.queryNodeName).parents.size() == 0) {
            return null;
        }
        for (String evi : this.evidenceVariablesNames) {
            if (bn.BN.get(this.queryNodeName).parents.contains(evi)) {
                counter++;
            }
        }
        if (counter < bn.BN.get(this.queryNodeName).parents.size()) {
            return null;
        }
        StringBuilder key = new StringBuilder();
        if (counter == bn.BN.get(this.queryNodeName).parents.size()) {
            //find the key that contains the query variable and it outcome and the evidence variables and their outcomes
            key = new StringBuilder();
            for (String evi : bn.BN.get(this.queryNodeName).parents) {
                key.append(this.query, this.query.indexOf(evi), this.query.indexOf(",", this.query.indexOf(evi))).append(" ");
                //if key contains ") " then remove it
                if (key.toString().contains(")")) {
                    key = new StringBuilder(key.substring(0, key.length() -2)).append(" ");
                }
            }
            key.append(this.queryNode).append(" ");

        }
        String ans = String.format("%.5f", bn.BN.get(this.queryNodeName).cpt.get(key.toString())) + "," + this.addCounter + "," + this.multiplyCounter;
        return ans;
    }
}







