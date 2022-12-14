import java.util.*;

public class VariableElimination {
    BayesianNetwork bn;
    Queries query;

    public VariableElimination(BayesianNetwork bn, Queries query) {
        this.bn = bn;
        this.query = query;
    }

    /**
     * this function change each variable CPT to a factor by removing irrelevant values form the CPT
     */
    public void getRelevantCpt() {
        for (String Vari : this.bn.BN.keySet()) {
            this.bn.BN.get(Vari).cptCopy = this.bn.BN.get(Vari).cpt;
            StringBuilder relventKey = new StringBuilder();
            for (int i = 0; i < this.query.evidenceVariablesNames.length; i++) {
                if (bn.BN.get(Vari).parents.contains(this.query.evidenceVariablesNames[i]) || bn.BN.get(Vari).name.equals(this.query.evidenceVariablesNames[i])) {
                    relventKey.append(this.query.evidence[i]).append(" ");
                }
            }
            //split the string into an array of strings
            if (relventKey.length() != 0) {
                String[] relventKeyArray = relventKey.toString().split(" ");
                LinkedHashMap<String, Double> cpt = new LinkedHashMap<>();
                for (String key : bn.BN.get(Vari).cpt.keySet()) {
                    //check if the key contains all the relevant keys from keyArray
                    if (containsAll(relventKeyArray, key)) {
                        cpt.put(key, bn.BN.get(Vari).cpt.get(key));
                    }
                }
                {
                    bn.BN.get(Vari).setCpt(cpt);
                    System.out.println("cpt of " + bn.BN.get(Vari).name + " is " + bn.BN.get(Vari).cpt);
                }
            }
        }
    }

    /**
     * this function check if the key contains all the relevant keys from keyArray
     * @param keyArray
     * @param key
     * @return true if the key contains all the relevant keys from keyArray
     */
    public Boolean containsAll(String[] keyArray, String key) {
        for (String keyName : keyArray) {
            if (!key.contains(keyName)) {
                return false;
            }
        }
        return true;
    }

    /**
     * this function join two factors(CPTs)
     * @param cpt1
     * @param cpt2
     * @return the joined factor
     */
    public LinkedHashMap<String, Double> joinCpt(LinkedHashMap<String, Double> cpt1, LinkedHashMap<String, Double> cpt2) {

        LinkedHashMap<String, Double> cpt = new LinkedHashMap<>();
        String[] keys1 = cpt1.keySet().toArray(new String[0]);//get the keys of the first cpt
        String[] keys2 = cpt2.keySet().toArray(new String[0]);//get the keys of the second cpt


        ArrayList<String> commonVariables = new ArrayList<>();//create an array list to store the common variables
        String[] firstKey = keys1[0].split(" ");//split the first key of the first cpt
        String[] secondKey = keys2[0].split(" ");//split the first key of the second cpt
        for (String s1 : firstKey) {
            for (String s2 : secondKey) {
                if (!Objects.equals(s1, "") && !Objects.equals(s2, "") && s1.substring(0, s1.indexOf("=")).equals(s2.substring(0, s2.indexOf("=")))) {
                    commonVariables.add(s1.substring(0, s1.indexOf("=")));//add the common variable to the array list
                }
            }
        }
        //if for each key in keys1 all the common variables with keys2 are the same multiply the probabilities and add them to the cpt
        for (String key1 : keys1) {//for each key in keys1

            for (String key2 : keys2) {//for each key in keys2
                boolean flag = true;
                for (String commonVariable : commonVariables) {//for each common variable
                    if (!key1.substring(key1.indexOf(commonVariable), key1.indexOf(" ", key1.indexOf((commonVariable)))).equals(key2.substring(key2.indexOf(commonVariable), key2.indexOf(" ", key2.indexOf(commonVariable))))) {//if the common variable is not the same in the two keys
                        flag = false;//set the flag to false
                        break;
                    }
                }
                if (!flag) {//if the flag is false
                    continue;//continue to the next key in keys2
                }
                StringBuilder key = new StringBuilder();//create a string builder to store the new key of the cpt
                key.append(key1);//add the first key to the string builder
                for (String s : key2.split(" ")) {//for each variable in the second key
                    if (!Objects.equals(s, "") && !commonVariables.contains(s.substring(0, s.indexOf("=")))) {//if the variable is not a common variable
                        key.append(s).append(" ");//add it to the key
                    }
                }
                //add the new key and the product of the probabilities to the cpt
                cpt.put(key.toString(), cpt1.get(key1) * cpt2.get(key2));
                query.multiplyCounter.getAndIncrement();

            }
        }
//        for (String key : cpt.keySet()) {
//            System.out.println(key + " " + cpt.get(key));
//        }
        return cpt;
    }

    /**
     * this function eliminate a variable from a factor
     * @param cpt
     * @param variable
     * @return
     */
    public LinkedHashMap<String, Double> elimination(LinkedHashMap<String, Double> cpt, String variable) {
        //create a new cpt to store the new cpt
        LinkedHashMap<String, Double> newCpt = new LinkedHashMap<>();
        //remove the variable name from the cpt keys
        for (int i = 0; i < cpt.keySet().size() - 1; i++) {
            int counter = 1;
            if (i < 0) {
                i = 0;
            }
            String key1 = cpt.keySet().toArray(new String[0])[i];
            ArrayList<String> keyToRemove = new ArrayList<>();
            double newProbability = 0;
            keyToRemove.add(key1);
            String subKey1 = key1.substring(0, key1.indexOf(variable)) + key1.substring(key1.indexOf(" ", key1.indexOf(variable)));
            for (int j = i + 1; j < cpt.keySet().size(); j++) {
                String key2 = cpt.keySet().toArray(new String[0])[j];
                String subKey2 = key2.substring(0, key2.indexOf(variable)) + key2.substring(key2.indexOf(" ", key2.indexOf(variable)));
                //if the two keys are the same except for the variable name and the variable value
                if (subKey1.equals(subKey2)) {
                    //add the new key and the sum of the probabilities to the new cpt
                    if (counter == 1) {
                        newProbability = cpt.get(key1) + cpt.get(key2);
                    } else {
                        newProbability += cpt.get(key2);
                    }
                    keyToRemove.add(key2);
                    query.addCounter.getAndIncrement();
                    counter++;
                    i--;
                    if (counter == bn.BN.get(variable).outcomes.size()) {
                        newCpt.put(subKey1, newProbability);
                        for (String key : keyToRemove) {
                            cpt.remove(key);
                        }
                        break;
                    }
                }
            }
        }
//        System.out.println("cpt after elimination is of " + variable + newCpt);
        return newCpt;
    }

    public LinkedHashMap<String, Double> normalize(LinkedHashMap<String, Double> cpt) {
        double sum = 0;
        for (double d : cpt.values()) {
            sum += d;
            query.addCounter.getAndIncrement();
        }
        for (String key : cpt.keySet()) {
            cpt.put(key, cpt.get(key) / sum);
        }
        query.addCounter.getAndDecrement();
        return cpt;
    }

    public String answer() {
        this.query.restCounters();
        String ans = simpleAns();
        if (ans != null) {
            return ans;
        }
        this.getRelevantCpt();

        //crate ArrayList that store the all variables cpt's
        ArrayList<LinkedHashMap<String, Double>> cpts = new ArrayList<>();
        ArrayList<String> relevantVariables = new ArrayList<>();
        for (String vari : this.bn.getKeys()) {
            if (this.bn.BN.get(vari).cpt.size() > 1) {
                if (Arrays.asList(query.hiddenVariables).contains(vari)) {
                    if (bn.isAncestor(vari, query.queryAndEvidenceVariables)) {
                        relevantVariables.add(vari);
                        cpts.add(this.bn.BN.get(vari).cpt);
                    }
                } else {
                    cpts.add(this.bn.BN.get(vari).cpt);
                }

            }
        }
        //create list of the relevant variables and the query and evidence variables

        //sort the relevantVariables according to ABC
        if (Objects.equals(query.queryType, "2")) {////sort the relevantVariables according to ABC
            Collections.sort(relevantVariables);
        }
        //if the query type is 3 sort the relevantVariables according to the number of relvantsons
        else if (Objects.equals(query.queryType, "3")) {
            ArrayList<String> variables = new ArrayList<>(); //create list of the relevant variables and the query and evidence variables
            variables.addAll(relevantVariables);
            variables.addAll(Arrays.asList(query.queryAndEvidenceVariables));
            for (String vari : relevantVariables) {
                bn.BN.get(vari).setRelevantSonsAndParents(variables);


            }
            //sort the relevantVariables according to the number of relvantsons
            relevantVariables.sort(new Comparator<String>() {
                @Override
                public int compare(String o1, String o2) {
                    if (bn.BN.get(o1).RelevantSons > bn.BN.get(o2).RelevantSons) {
                        return 1;
                    } else if (bn.BN.get(o1).RelevantSons < bn.BN.get(o2).RelevantSons) {
                        return -1;
                    }
                    if (bn.BN.get(o1).cpt.size() > bn.BN.get(o2).cpt.size()) {
                        return 1;
                    } else if (bn.BN.get(o1).cpt.size() < bn.BN.get(o2).cpt.size()) {
                        return -1;
                    }
                    return 0;


                }
            });
        }
        System.out.println("relevant variables are " + relevantVariables);
        for (String vari : relevantVariables) {
            ArrayList<LinkedHashMap<String, Double>> Hidencpts = new ArrayList<>();
            for (int i = 0; i < cpts.size(); i++) {
                if (cpts.get(i).keySet().toArray(new String[0])[0].contains(vari)) {
                    Hidencpts.add(cpts.get(i));
                    cpts.remove(i);
                    i--;
                }
            }
            //sort Hidencpt by the size of the cpt from small to big and if they have the same size then by the Ascii value of the variables names from small to big
            Hidencpts.sort(new Comparator<LinkedHashMap<String, Double>>() {
                @Override
                public int compare(LinkedHashMap<String, Double> o1, LinkedHashMap<String, Double> o2) {
                    return comp(o1, o2);
                }
            });
            while (Hidencpts.size() > 1) {
                Hidencpts.add(joinCpt(Hidencpts.get(0), Hidencpts.get(1)));
                //sort Hidencpt by the size of the cpt from small to big and if they have the same size then by the Ascii value of the variables names from small to big

                Hidencpts.remove(0);
                Hidencpts.remove(0);
                Hidencpts.sort(this::comp);
            }
//
//            }
            cpts.add(elimination(Hidencpts.get(Hidencpts.size() - 1), vari));
        }
        while (cpts.size() > 1) {
            cpts.add(joinCpt(cpts.get(0), cpts.get(1)));
            cpts.remove(0);
            cpts.remove(0);
        }
        //find the key that contains the query variable and it outcome
        String key = "";
        for (String k : cpts.get(0).keySet()) {
            if (k.contains(query.queryNode)) {
                key = k;
                break;
            }
        }
        normalize(cpts.get(0));
        String quryresult = String.format("%.5f", cpts.get(0).get(key));
        String result = quryresult + "," + query.addCounter + "," + query.multiplyCounter;
        System.out.println(result);


        bn.restNet();
        return result;
    }

    private int comp(LinkedHashMap<String, Double> o1, LinkedHashMap<String, Double> o2) {
        if (o1.size() > o2.size()) {
            return 1;
        } else if (o1.size() < o2.size()) {
            return -1;
        }
        if (o1.keySet().toArray(new String[0])[0].compareTo(o2.keySet().toArray(new String[0])[0]) > 0) {
            return 1;
        } else if (o1.keySet().toArray(new String[0])[0].compareTo(o2.keySet().toArray(new String[0])[0]) < 0) {
            return -1;
        }
        return 0;
    }


    public String simpleAns() {
        int counter = 0;
        if (bn.BN.get(query.queryNodeName).parents.size() == 0) {
            return null;
        }

        for (String evi : query.evidenceVariablesNames) {
            if (bn.BN.get(query.queryNodeName).parents.contains(evi)) {
                counter++;

            }
        }
        if (counter < bn.BN.get(query.queryNodeName).parents.size()) {
            return null;
        }
        StringBuilder key = new StringBuilder();
        if (counter == bn.BN.get(query.queryNodeName).parents.size()) {
            //find the key that contains the query variable and it outcome and the evidence variables and their outcomes
            key = new StringBuilder();
            for (String evi : bn.BN.get(query.queryNodeName).parents) {
                key.append(query.query, query.query.indexOf(evi), query.query.indexOf(",", query.query.indexOf(evi))).append(" ");
                //if key contains ") " then remove it
                if (key.toString().contains(")")) {
                    key = new StringBuilder(key.substring(0, key.length() -2)).append(" ");
                }
            }
            key.append(query.queryNode).append(" ");

        }
        String ans = String.format("%.5f", bn.BN.get(query.queryNodeName).cpt.get(key.toString())) + "," + query.addCounter + "," + query.multiplyCounter;
        return ans;
    }
}














