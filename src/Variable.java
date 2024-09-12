import java.util.*;

public class Variable {
    final static int UNVISITED = 0, FROM_CHILD = 1, FROM_PARENT = 2;
    String name;
    ArrayList<String> outcomes;
    ArrayList<String> parents;
    Boolean colored = false;
    boolean isEvidence = false;
    int relevantNeighbors = 0;

    public int getVisitState() {
        return visitState;
    }

    public void setVisitState(int visitState) {
        this.visitState = visitState;
    }

    int visitState = UNVISITED;

    public boolean isEvidence() {
        return isEvidence;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    public void setEvidence(boolean evidence) {
        isEvidence = evidence;
    }

    

    public LinkedHashMap<String, Double> getCpt() {
        return cpt;
    }

    public void setCpt(LinkedHashMap<String, Double> cpt) {
        this.cpt = cpt;
    }

    ArrayList<String> sons;
    LinkedHashMap<String, Double> cpt;
    LinkedHashMap<String, Double> cptCopy;
    HashMap<String, Variable> bn;

    public void setRelevantSonsAndParents(ArrayList<String> vars) {
        int counter = 0;
        for (String Var : vars) {
            if (this.sons.contains(Var) || this.parents.contains(Var)) {
                counter++;
            }
        }
        relevantNeighbors = counter;
    }

    

    public void setColored(Boolean colored) {
        this.colored = colored;
    }

    public Boolean getColored() {
        return colored;
    }

    

    public Variable(String name, String[] outcomes, HashMap<String, Variable> bn) {
        this.outcomes = new ArrayList<>();
        this.parents = new ArrayList<>();
        this.sons = new ArrayList<>();
        this.cpt = new LinkedHashMap<>();
        this.name = name;
        this.outcomes.addAll(Arrays.asList(outcomes));
        this.bn = bn;
        this.cptCopy = this.deepCopy(this.cpt);

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
                        keysNames[i] += bn.get(parent).name + "=" + bn.get(parent).outcomes.get(j) + " ";//add the name of the parent and the outcome to the key name
                        counter++;
                        i++;
                    } else {//if the counter is greater than the step size
                        counter = 0;//reset the counter
                        j++;//increment the outcome counter
                        j = j % bn.get(parent).outcomes.size();//if the outcome counter is greater than the number of outcomes of the parent, reset it
                    }
                }
            }
        }
        for (int i = 0; i < total_rows; i++) {//for each row
            keysNames[i] += this.name + "=" + this.outcomes.get(i % this.outcomes.size()) + " ";//add the name of the variable and the outcome to the key name
            this.cpt.put(keysNames[i], Double.parseDouble(cptArray[i]));//add the key name and the probability to the cpt
        }

    }

    public ArrayList<String> commonParents(Variable v) {
        ArrayList<String> commonParents = new ArrayList<>();
        for (String parent : this.parents) {
            if (v.parents.contains(parent)) {
                commonParents.add(parent);
            }
        }
        return commonParents;
    }


    public LinkedHashMap<String, Double> deepCopy(LinkedHashMap<String, Double> original) {
        return new LinkedHashMap<>(original);
    }

    public void resetCpt() {
        setCpt(this.cptCopy);
    }
    public void restVisitState(){
        this.visitState = UNVISITED;
    }

}


//






