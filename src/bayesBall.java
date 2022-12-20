import java.util.LinkedList;
import java.util.Queue;

public class bayesBall {
    BayesianNetwork bn;
    Queries q;

    public bayesBall(Queries q, BayesianNetwork bn) {
        this.bn = bn;
        this.q = q;
    }

    public void setEvidence() {
        for (String var : q.evidenceVariablesNames) {
            bn.BN.get(var).setEvidence(true);
        }
    }

    public boolean bayesBallAlgo(Variable varToReach, Variable StartVar) {
        setEvidence();
        StartVar.setVisitState(Variable.FROM_CHILD);
        Queue<Variable> queue = new LinkedList<Variable>();
        queue.add(StartVar);
        while (!queue.isEmpty()) {
            Variable var = queue.poll();
            if (var.equals(varToReach)) {
                resetVisitState();
                resetEvidence();
                return true;
            }
            if (!var.isEvidence() && var.getVisitState() == Variable.FROM_CHILD) {
                for (String parent : var.parents) {
                    if (bn.BN.get(parent).getVisitState()==Variable.UNVISITED) {
                        bn.BN.get(parent).setVisitState(Variable.FROM_CHILD);
                        queue.add(bn.BN.get(parent));
                    }
                }
                for (String son : var.sons) {
                    if (bn.BN.get(son).getVisitState()==Variable.UNVISITED) {
                        bn.BN.get(son).setVisitState(Variable.FROM_PARENT);
                        queue.add(bn.BN.get(son));
                    }
                }

            } else if (!var.isEvidence() && var.getVisitState() == Variable.FROM_PARENT) {
                for (String son: var.sons) {
                    if (bn.BN.get(son).getVisitState()!=Variable.FROM_PARENT) {
                        bn.BN.get(son).setVisitState(Variable.FROM_PARENT);
                        queue.add(bn.BN.get(son));
                    }

                }
            } else if (var.isEvidence() && var.getVisitState() == Variable.FROM_PARENT) {
                for (String parent : var.parents) {
                    if (bn.BN.get(parent).getVisitState()!=Variable.FROM_CHILD) {
                        bn.BN.get(parent).setVisitState(Variable.FROM_CHILD);
                        queue.add(bn.BN.get(parent));
                    }
                }
            }

        }
        resetEvidence();
        resetVisitState();
        return false;


    }
    public void resetVisitState() {
        for (String var : bn.BN.keySet()) {
            bn.BN.get(var).restVisitState();
        }
    }
    public void resetEvidence() {
        for (String var : bn.BN.keySet()) {
            bn.BN.get(var).setEvidence(false);
        }
    }
}


