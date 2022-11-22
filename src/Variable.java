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
        String[] keysNames = new String[cptArray.length];//create an array of keys names
        Arrays.fill(keysNames, "");//fill the array with empty strings
        int total_rows = cptArray.length;//total number of rows
        if (total_rows > this.outcomes.size()) {//if the number of rows is greater than the number of outcomes
            int step_size = cptArray.length;
            for (String parent : parents) {//for each parent
                step_size /= bn.get(parent).outcomes.size();//divide the step size by the number of outcomes of the parent
                int i = 0, j = 0, counter = 0;//initialize counters
                while (i < keysNames.length) {//while the counter is less than the length of the array
                    if (counter < step_size) {//if the counter is less than the step size
                        keysNames[i] += bn.get(parent).name +":"+ bn.get(parent).outcomes.get(j) + " ";//add the name of the parent and the outcome to the key name
                        counter++;
                        i++;
                    } else {//if the counter is greater than the step size
                        counter = 0;//reset the counter
                        j++;//increment the outcome counter
                        j = j % bn.get(parent).outcomes.size();//if the outcome counter is greater than the number of outcomes of the parent, reset it
                    }
                }
            }
            for (int i = 0; i <total_rows; i++) {//for each row
                keysNames[i] += this.name +":"+ this.outcomes.get(i % this.outcomes.size());//add the name of the variable and the outcome to the key name
                this.cpt.put(keysNames[i], Double.parseDouble(cptArray[i]));//add the key name and the probability to the cpt
            }
        }
    }
}


//






