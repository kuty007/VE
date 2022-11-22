
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import jdk.internal.org.objectweb.asm.tree.analysis.Value;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.w3c.dom.Element;
import java.io.File;
import java.util.Arrays;
import java.util.HashMap;




public class BayesianNetwork {
   public HashMap<String,Variable> BN;
    public void loadBnFromXml(String path){
        BN = new HashMap<String,Variable>();
        try{
            DocumentBuilderFactory db = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = db.newDocumentBuilder();
            Document doc = dBuilder.parse(new File(path));
            doc.getDocumentElement().normalize();
            NodeList defNodes = doc.getElementsByTagName("DEFINITION");
            NodeList varList = doc.getElementsByTagName("VARIABLE");
            for(int i = 0; i < varList.getLength(); i++){
                Node nNode = varList.item(i);
                if(nNode.getNodeType() == Node.ELEMENT_NODE){
                    Element eElement = (Element) nNode;
                    String name = eElement.getElementsByTagName("NAME").item(0).getTextContent();
                    System.out.println(name);
                    NodeList outcomes = eElement.getElementsByTagName("OUTCOME");//outcomes of the variable
                    String[] outcomesArray = new String[outcomes.getLength()];
                    for(int j = 0; j < outcomes.getLength(); j++){
                        outcomesArray[j] = outcomes.item(j).getTextContent();
                        System.out.println(outcomesArray[j]);
                    }
                    Variable var = new Variable(name, outcomesArray, BN);
                    BN.put(name, var);
                }
            }
            for (int i = 0; i <defNodes.getLength(); i++) {
                Node nNode = defNodes.item(i);
                if(nNode.getNodeType() == Node.ELEMENT_NODE){
                    Element eElement = (Element) nNode;
                    String name = eElement.getElementsByTagName("FOR").item(0).getTextContent();
                    NodeList parents = eElement.getElementsByTagName("GIVEN");
                    for(int j = 0; j < parents.getLength(); j++){
                        String parent = parents.item(j).getTextContent();
                        BN.get(name).parents.add(parent);
                        BN.get(parent).sons.add(name);
                    }
                    NodeList cpt = eElement.getElementsByTagName("TABLE");
                    String[] cptArray = cpt.item(0).getTextContent().split(" ");
                    BN.get(name).buildCpt(cptArray);

//                    for(int j = 0; j < cptArray.length; j++){
//                        BN.get(name).cpt.put(BN.get(name).outcomes.get(j), Double.parseDouble(cptArray[j]));
                    }
                }

        }
        catch (Exception e) {
            e.printStackTrace();
        }

            }
        }
