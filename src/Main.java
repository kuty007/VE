import java.util.Arrays;
import java.util.LinkedHashMap;

public class Main {
    public static void main(String[] args) {
        BayesianNetwork bn = new BayesianNetwork();
  bn.loadBnFromXml("C:\\Users\\asaf7\\IdeaProjects\\algo\\src\\big_net (1).xml");
//        System.out.println(bn.BN.get("J").parents.indexOf("B"));
//     Queries q = new Queries("P(B0=v3|C3=T,B2=F,C2=v3),1", bn);
//     bn.loadBnFromXml("C:\\Users\\asaf7\\IdeaProjects\\algo\\src\\alarm_net.xml");
//      System.out.println(bn.BN.get("C3").cpt.keySet());
//        print all the keys of the hashmap of bn
//        System.out.println(Arrays.toString(bn.getKeys()));

//        System.out.println(bn.BN.get("C3").cpt.get("B1=T B0=v1 C3=T "));
//        Queries q = new Queries("P(D1=F|C1=T,C2=v1,C3=T,A1=T),1", bn);
        Queries q = new Queries("P(B0=v3|C3=T,B2=F,C2=v3),1", bn);

//        bn.BN.get("C3").cpt.forEach((k, v) -> System.out.println(k + " " + v));
//        System.out.println(bn.isAncestor("M", q.evidenceVariablesNames));
//    q.solve();
     VariableElimination ve = new VariableElimination(bn, q);
//       ve.getRelevantCpt();
       ve.answer();
//     bn.BN.get("C3").cpt.forEach((k, v) -> System.out.println(k + " " + v));
//       ve.answer();
        //LinkedHashMap<String,Double> sa = ve.joinCpt(bn.BN.get("J").cpt, bn.BN.get("A").cpt);
        //LinkedHashMap<String,Double> sa2 = ve.elimination(sa, "A");
        //sa2.forEach((k, v) -> System.out.println(k + " " + v));
//       bn.BN.get("A").cpt.forEach((k, v) -> System.out.println(k + " " + v) );;
//        System.out.println("*************");
//       bn.BN.get("A").cptCopy.forEach((k, v) -> System.out.println(k + " " + v));;
//        System.out.println(q.queryNode);
//        System.out.println(Arrays.toString(q.evidence));
//        System.out.println(Arrays.toString(q.hiddenVariables));
//        System.out.println(Arrays.toString(q.evidenceVariablesNames));
//          bn.BN.get("B").cpt.forEach((k, v) -> System.out.println(k + " " + v));


    }


}
