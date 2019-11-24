package eg.edu.alexu.csd.oop.db.cs2.filesGenerator;

import eg.edu.alexu.csd.oop.db.cs2.structures.Column;
import eg.edu.alexu.csd.oop.db.cs2.structures.Record;
import eg.edu.alexu.csd.oop.db.cs2.structures.Table;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;

public class XML {
    FilesHandler filesHandler = new FilesHandler();
    public void saveTable(Table table){
        DocumentBuilderFactory DOM = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder;
        try {
            dBuilder = DOM.newDocumentBuilder();
            Document doc = dBuilder.newDocument();
            //add elements to Document
            Element rootElement =
                    doc.createElement("Table");
            rootElement.setAttribute("name",table.getName());
            //append root element to document
            doc.appendChild(rootElement);

            //append first child element to root element
            for (int i = 1 ; i< table.getSize() ; i++){
                rootElement.appendChild(getColoumns(doc,table.getColumns().get(i)));
            }

            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            //for pretty print
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            DOMSource source = new DOMSource(doc);

            //write to console or file
            //StreamResult console = new StreamResult(System.out);
            StreamResult file = new StreamResult(new File(filesHandler.getPathOf(table.getName())));

           // transformer.transform(source, console);
            transformer.transform(source, file);
            //System.out.println("DONE");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private static Node getColoumns(Document doc , Column column) {
        Element colomn = doc.createElement("Coloumn");

            colomn.setAttribute("name", column.getName());
            for (int j = 0 ; j<column.getSize() ; j++) {
                colomn.appendChild(getTableElements(doc, colomn, "record"+j,column.getRecordAtIndex(j).getValue()));
            }
        return colomn;
    }
    private static Node getTableElements(Document doc, Element element, String name, Object value) {
        Element node = doc.createElement(name);
        node.appendChild(doc.createTextNode(String.valueOf(value)));
        return node;
    }

    public Table loadTable(String tableName){
return null ;
    }
}
