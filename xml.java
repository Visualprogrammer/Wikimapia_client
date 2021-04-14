import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.swing.text.Document;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.util.ArrayList;

public class xml {
    DocumentBuilderFactory dbf;
    DocumentBuilder db ;
    Document doc;
    public xml(String a) throws IOException, SAXException, ParserConfigurationException {
        dbf = DocumentBuilderFactory.newInstance();
        db  = dbf.newDocumentBuilder();
        doc = (Document) db.parse(a);
    }
    public ArrayList<mapobject> getobj() {
        ArrayList<mapobject> q = new ArrayList<mapobject>();
         for(int i = 0; i < doc.getDefaultRootElement().getElementCount(); i++) {
             NodeList obj = (NodeList) doc.getDefaultRootElement().getElement(i);
             mapobject f;
             System.out.println(obj.item(1).getPrefix());
         //    q.add(f);
         }
         return q;
    }

}
