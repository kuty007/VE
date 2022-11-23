
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
    public HashMap<String, Variable> BN;

    public void loadBnFromXml(String path) {
        BN = new HashMap<String, Variable>();
        try {
            DocumentBuilderFactory db = DocumentBuilderFactory.newInstance();//create a new instance of DocumentBuilderFactory
            DocumentBuilder dBuilder = db.newDocumentBuilder();//create a new instance of DocumentBuilder
            Document doc = dBuilder.parse(new File(path));//parse the xml file
            doc.getDocumentElement().normalize();//normalize the xml file
            NodeList defNodes = doc.getElementsByTagName("DEFINITION");//get all the definition nodes
            NodeList varList = doc.getElementsByTagName("VARIABLE");//get all the variable nodes
            for (int i = 0; i < varList.getLength(); i++) {//for each variable node
                Node nNode = varList.item(i);//get the node
                if (nNode.getNodeType() == Node.ELEMENT_NODE) {//if the node is an element
                    Element eElement = (Element) nNode;//cast the node to an element
                    String name = eElement.getElementsByTagName("NAME").item(0).getTextContent();//get the name of the variable
                    NodeList outcomes = eElement.getElementsByTagName("OUTCOME");//outcomes of the variable
                    String[] outcomesArray = new String[outcomes.getLength()];//create an array of outcomes
                    for (int j = 0; j < outcomes.getLength(); j++) {
                        outcomesArray[j] = outcomes.item(j).getTextContent();
                    }
                    Variable var = new Variable(name, outcomesArray, BN);//create a new variable
                    BN.put(name, var);//add the variable to the hashmap
                }
            }
            for (int i = 0; i < defNodes.getLength(); i++) {//for each definition node
                Node nNode = defNodes.item(i);//get the node
                if (nNode.getNodeType() == Node.ELEMENT_NODE) {//if the node is an element
                    Element eElement = (Element) nNode;//cast the node to an element
                    String name = eElement.getElementsByTagName("FOR").item(0).getTextContent();//get the name of the variable
                    NodeList parents = eElement.getElementsByTagName("GIVEN");//get the parents of the variable
                    for (int j = 0; j < parents.getLength(); j++) {//for each parent
                        String parent = parents.item(j).getTextContent();//get the name of the parent
                        BN.get(name).parents.add(parent);//add the parent to the parents list of the variable
                        BN.get(parent).sons.add(name);//add the variable to the sons list of the parent
                    }
                    NodeList cpt = eElement.getElementsByTagName("TABLE");
                    String[] cptArray = cpt.item(0).getTextContent().split(" ");//cpt results of the variable
                    BN.get(name).buildCpt(cptArray);//build the cpt of the variable
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }
    public String[] getKeys(){
        String[] keys = new String[BN.size()];
        int i=0;
        for (String key : BN.keySet()) {
            keys[i]=key;
            i++;
        }
        return keys;
    }
}
