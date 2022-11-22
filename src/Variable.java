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

    public String[] buildCpt(String[] cptArray) {
        String[] keysNames = new String[cptArray.length];
        Arrays.fill(keysNames, "");

//        int numOfOutcomes = outcomes.size();
//        int numOfParents = parents.size();
//        int total_rows = numOfOutcomes;
//        for (String parent : parents) {
//            total_rows *= bn.get(parent).outcomes.size();
//        }
//        int numOfCols = numOfOutcomes;
//        int numOfCells = total_rows * numOfCols;
//        System.out.println(total_rows);
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
        }
        return keysNames;
    }

}


//






