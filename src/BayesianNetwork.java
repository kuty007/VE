
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import jdk.internal.org.objectweb.asm.tree.analysis.Value;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.w3c.dom.Element;
import java.io.File;
import java.util.HashMap;

public class BayesianNetwork {
   public HashMap<String, Value> BN;


    public void loadBnFromXml(String path){
        BN = new HashMap<>();
        try{
            DocumentBuilderFactory db = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = db.newDocumentBuilder();
            Document doc = dBuilder.parse(new File(path));
            doc.getDocumentElement().normalize();
            NodeList nList = doc.getElementsByTagName("VARIABLE");
            for(int i = 0; i < nList.getLength(); i++){
                Node nNode = nList.item(i);
                if(nNode.getNodeType() == Node.ELEMENT_NODE){
                    Element eElement = (Element) nNode;
                    String name = eElement.getAttribute("NAME");
                    String type = eElement.getAttribute("TYPE");
                    String outcome = eElement.getAttribute("OUTCOME");
                    String[] outcomes = outcome.split(" ");
                    Value value = new Value(type, outcomes);
                    BN.put(name, value);
                }
            }


        }
        catch (Exception e){
            e.printStackTrace();
        }


    }
}

