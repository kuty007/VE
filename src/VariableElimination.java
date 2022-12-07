import java.util.ArrayList;
import java.util.LinkedHashMap;

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
                    bn.BN.get(Vari).cpt = cpt;
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
        for (String s1 : firstKey) {
            for (String s2 : secondKey) {
                if (s1.substring(0, s1.indexOf("=")).equals(s2.substring(0, s2.indexOf("=")))) {
                    commonVariables.add(s1.substring(0, s1.indexOf("=")));//add the common variable to the array list
                }
            }
            //if for each key in keys1 all the common variables with keys2 are the same multiply the probabilities and add them to the cpt
            for (String key1 : keys1) {//for each key in keys1
                for (String key2 : keys2) {//for each key in keys2
                    for (String commonVariable : commonVariables) {//for each common variable
                        if (!key1.substring(key1.indexOf(commonVariable), key1.indexOf(" ",key1.indexOf((commonVariable)))).equals(key2.substring(key2.indexOf(commonVariable), key2.indexOf(" ", key2.indexOf(commonVariable))))) {//if the common variable is not the same in the two keys
                            break;
                        }
                    }
                    StringBuilder key = new StringBuilder();//create a string builder to store the new key of the cpt
                    key.append(key1);//add the first key to the string builder
                    for (String s : key2.split(" ")) {//for each variable in the second key
                        if (!commonVariables.contains(s.substring(0,s.indexOf("=")))) {//if the variable is not a common variable
                            key.append(s).append(" ");//add it to the key
                        }
                    }
                    //add the new key and the product of the probabilities to the cpt
                    cpt.put(key.toString(), cpt1.get(key1) * cpt2.get(key2));

                }
            }
        }
        return cpt;
    }

}










