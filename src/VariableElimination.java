import java.util.*;

public class VariableElimination {
    BayesianNetwork bn;
    Queries query;

    public VariableElimination(BayesianNetwork bn, Queries query) {
        this.bn = bn;
        this.query = query;
    }

    public void getRelevantCpt() {
        for (String Vari : this.bn.BN.keySet()) {
            this.bn.BN.get(Vari).cptCopy = this.bn.BN.get(Vari).cpt;
            StringBuilder relventKey = new StringBuilder();
            for (int i = 0; i < this.query.evidenceVariablesNames.length; i++) {
                if (bn.BN.get(Vari).parents.contains(this.query.evidenceVariablesNames[i]) || bn.BN.get(Vari).name.equals(this.query.evidenceVariablesNames[i])) {
                    relventKey.append(this.query.evidence[i]).append(" ");
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
                    bn.BN.get(Vari).setCpt(cpt);
                    System.out.println("cpt of " + bn.BN.get(Vari).name + " is " + bn.BN.get(Vari).cpt);
                }
            }
        }
    }

    public LinkedHashMap<String, Double> joinCpt(LinkedHashMap<String, Double> cpt1, LinkedHashMap<String, Double> cpt2) {
        LinkedHashMap<String, Double> cpt = new LinkedHashMap<>();
        String[] keys1 = cpt1.keySet().toArray(new String[0]);//get the keys of the first cpt
        String[] keys2 = cpt2.keySet().toArray(new String[0]);//get the keys of the second cpt


        ArrayList<String> commonVariables = new ArrayList<>();//create an array list to store the common variables
        String[] firstKey = keys1[0].split(" ");//split the first key of the first cpt
        String[] secondKey = keys2[0].split(" ");//split the first key of the second cpt
        //remove empty string from first key and second key
//        ArrayList<String> firstKeyList = new ArrayList<>();
//        ArrayList<String> secondKeyList = new ArrayList<>();
//        for (String s : firstKey) {
//            if (!s.equals("")) {
//                firstKeyList.add(s);
//            }
//        }
//        for (String s : secondKey) {
//            if (!s.equals("")) {
//                secondKeyList.add(s);
//            }
//        }
//        firstKey = firstKeyList.toArray(new String[0]);
//        secondKey = secondKeyList.toArray(new String[0]);
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
                for (String commonVariable : commonVariables) {//for each common variable
                    if (!key1.substring(key1.indexOf(commonVariable), key1.indexOf(" ", key1.indexOf((commonVariable)))).equals(key2.substring(key2.indexOf(commonVariable), key2.indexOf(" ", key2.indexOf(commonVariable))))) {//if the common variable is not the same in the two keys
                        break;
                    }
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

        return cpt;
    }


    public LinkedHashMap<String, Double> elimination(LinkedHashMap<String, Double> cpt, String variable) {
        //create a new cpt to store the new cpt
        LinkedHashMap<String, Double> newCpt = new LinkedHashMap<>();
        //remove the variable name from the cpt keys
        for (int i = 0; i < cpt.keySet().size() - 1; i++) {
            String key1 = cpt.keySet().toArray(new String[0])[i];
            String subKey1 = key1.substring(0, key1.indexOf(variable)) + key1.substring(key1.indexOf(" ", key1.indexOf(variable)));
            for (int j = i + 1; j < cpt.keySet().size(); j++) {
                String key2 = cpt.keySet().toArray(new String[0])[j];
                String subKey2 = key2.substring(0, key2.indexOf(variable)) + key2.substring(key2.indexOf(" ", key2.indexOf(variable)));
                //if the two keys are the same except for the variable name and the variable value
                if (subKey1.equals(subKey2)) {
                    //add the new key and the sum of the probabilities to the new cpt
                    newCpt.put(subKey1.trim(), cpt.get(key1) + cpt.get(key2));
                    query.addCounter.getAndIncrement();
                    cpt.remove(key1);
                    cpt.remove(key2);
//                    cpt.put(subKey1, newCpt.get(subKey1));
                    i--;
                    break;
                }
            }
        }
        System.out.println("cpt after elimination is of " + variable + newCpt);
        System.out.println();
        return newCpt;
    }

    public LinkedHashMap<String, Double> normalize(LinkedHashMap<String, Double> cpt) {
        double sum = 0;
        for (double d : cpt.values()) {
            sum += d;
        }
        for (String key : cpt.keySet()) {
            cpt.put(key, cpt.get(key) / sum);
        }
        return cpt;
    }

    public double[] answer() {
        this.getRelevantCpt();
        //crate ArrayList that store the all variables cpt's
        ArrayList<LinkedHashMap<String, Double>> cpts = new ArrayList<>();
        for (String vari : this.bn.getKeys()) {
            cpts.add(this.bn.BN.get(vari).cpt);
        }
        for (String vari : this.query.hiddenVariables) {
            ArrayList<LinkedHashMap<String, Double>> Hidencpts = new ArrayList<>();
            for (int i = 0; i < cpts.size(); i++) {
                if (cpts.get(i).keySet().toArray(new String[0])[0].contains(vari)) {
                    Hidencpts.add(cpts.get(i));
                    cpts.remove(i);
                    i--;
                }
            }

                //sort Hidencpt by the size of the cpt
                Hidencpts.sort(Comparator.comparingInt(HashMap::size));
                while (Hidencpts.size() > 1) {
                    Hidencpts.add(joinCpt(Hidencpts.get(0), Hidencpts.get(1)));
                    Hidencpts.remove(0);
                    Hidencpts.remove(0);
                }
//
//            }
                cpts.add(elimination(Hidencpts.get(Hidencpts.size() - 1), vari));
//            System.out.println("cpt after elimination: " + cpts.get(cpts.size() - 1));
        }
        return new double[0];

    }
}










