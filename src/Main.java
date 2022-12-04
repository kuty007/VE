import java.util.Arrays;

public class Main {
    public static void main(String[] args) {
        BayesianNetwork bn = new BayesianNetwork();
//        bn.loadBnFromXml("C:\\Users\\asaf7\\IdeaProjects\\algo\\src\\big_net (1).xml");
//        System.out.println(bn.BN.get("J").parents.indexOf("B"));
//        Queries q = new Queries("P(B0=v3|C3=T,B2=F,C2=v3),1", bn);
        bn.loadBnFromXml("C:\\Users\\asaf7\\IdeaProjects\\algo\\src\\alarm_net.xml");
//      System.out.println(bn.BN.get("C3").cpt.keySet());
//        System.out.println(bn.BN.get("C3").cpt.get("B1=T B0=v1 C3=T "));
     Queries q = new Queries("P(J=T|B=T),1", bn);
        q.solve();
//        System.out.println(q.queryNode);
//        System.out.println(Arrays.toString(q.evidence));
//        System.out.println(Arrays.toString(q.hiddenVariables));
//        System.out.println(Arrays.toString(q.evidenceVariablesNames));
//      bn.BN.get("B").cpt.forEach((k, v) -> System.out.println(k + " " + v));
//        System.out.println(bn.BN.get("B").cpt.get("B=T"));
//        System.out.println(bn.BN.get("B").cpt.get("B=T"));


//        String s ="0.5 0.5 0.3 0.7 0.6 0.4 0.9 0.1 0.1 0.9 0.4 0.6 0.7 0.3 0.95 0.05 0.2 0.8 0.5 0.5 0.8 0.2 0.05 0.95 0.88 0.12 0.55 0.45 0.22 0.78 0.15 0.85 0.77 0.23 0.44 0.56 0.11 0.89 0.25 0.75 0.66 0.34 0.33 0.67 0.02 0.98 0.35 0.65 0.45 0.55 0.75 0.25 0.1 0.9 0.2 0.8 0.55 0.45 0.85 0.15 0.1 0.9 0.3 0.7 0.65 0.35 0.95 0.05 0.2 0.8 0.3 0.7 0.4 0.6 0.5 0.5 0.7 0.3 0.8 0.2 0.4 0.6 0.6 0.4 0.7 0.3 0.9 0.1 0.5 0.5 0.6 0.4 0.8 0.2 0.9 0.1";
//        String[] cptArray = s.split(" ");
//        System.out.println(cptArray.length);
    }


}
