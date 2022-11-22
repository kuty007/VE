import java.util.*;

public class Variable {
    String name;
    ArrayList<String> outcomes;
    ArrayList<String> parents;
    ArrayList<String> sons;
    LinkedHashMap<String, Double> cpt;
    HashMap<String, Variable> bn;

    public Variable(String name, String[] outcomes, HashMap<String, Variable> bn) {
        this.outcomes = new ArrayList<>();
        this.parents = new ArrayList<>();
        this.sons = new ArrayList<>();
        this.cpt = new LinkedHashMap<>();
        this.name = name;
        this.outcomes.addAll(Arrays.asList(outcomes));
        this.bn = bn;


    }

    public void buildCpt(String[] cptArray) {
        String[] keysNames = new String[cptArray.length];
        Arrays.fill(keysNames, "");
        int total_rows = cptArray.length;
        if (total_rows > this.outcomes.size()) {
            int step_size = cptArray.length;
            for (String parent : parents) {
                step_size /= bn.get(parent).outcomes.size();
                int i = 0, j = 0, counter = 0;
                while (i < keysNames.length) {
                    if (counter < step_size) {
                        keysNames[i] += bn.get(parent).name + bn.get(parent).outcomes.get(j);
                        counter++;
                        i++;
                    } else {
                        counter = 0;
                        j++;
                        j = j % bn.get(parent).outcomes.size();
                    }
                }
            }
            for (int i = 0; i <total_rows; i++) {
                keysNames[i] += this.name + this.outcomes.get(i % this.outcomes.size());
                this.cpt.put(keysNames[i], Double.parseDouble(cptArray[i]));
            }
        }
    }
}


//






