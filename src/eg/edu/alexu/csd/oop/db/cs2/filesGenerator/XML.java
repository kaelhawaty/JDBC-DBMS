package eg.edu.alexu.csd.oop.db.cs2.filesGenerator;

import eg.edu.alexu.csd.oop.db.cs2.Parser;
import eg.edu.alexu.csd.oop.db.cs2.structures.Column;
import eg.edu.alexu.csd.oop.db.cs2.structures.Factory;
import eg.edu.alexu.csd.oop.db.cs2.structures.Record;
import eg.edu.alexu.csd.oop.db.cs2.structures.Table;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public class XML implements Parser {
    @Override
    public  void saveTable(Table table, String dataBaseName, FilesHandler filesHandler) {
        DocumentBuilderFactory DOM = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder;
        try {
            dBuilder = DOM.newDocumentBuilder();
            Document doc = dBuilder.newDocument();
            //add elements to Document
            Element rootElement =
                    doc.createElement("Table");
            rootElement.setAttribute("name", table.getName());
            //append root element to document
            doc.appendChild(rootElement);

            //append child elements to root element
            for (int i = 0; i < table.getSize(); i++) {
                rootElement.appendChild(getColoumns(doc, table.getColumns().get(i)));

            }

            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            //for pretty print
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            DOMSource source = new DOMSource(doc);

            //write to file
            StreamResult file = new StreamResult(new File(filesHandler.getPathOfTable(table.getName(), dataBaseName) + ".xml"));
            transformer.setOutputProperty(OutputKeys.DOCTYPE_SYSTEM, table.getName() + ".dtd");
            transformer.transform(source, file);

            BufferedWriter writer = new BufferedWriter(new FileWriter(filesHandler.getPathOfTable(table.getName(), dataBaseName)+".dtd"));
            StringBuilder sb = new StringBuilder();
            List<Column> list = table.getColumns();
            for(int i = 0; i < list.size(); i++){
               sb.append(list.get(i).getName());
               if(i != list.size()-1)
                   sb.append(',');
            }
            if(sb.toString().equals("")){
                writer.write("<!ELEMENT Table EMPTY>");
            }else {
                writer.write("<!ELEMENT Table (" + sb.toString() + ")>");
            }
            writer.newLine();
            writer.write("<!ATTLIST Table");
            writer.newLine();
            writer.write("  xmlns CDATA #FIXED ''");
            writer.newLine();
            writer.write("  name NMTOKEN #REQUIRED>");
            writer.newLine();
            writer.newLine();
            sb.setLength(0);
            for (int i= 0 ; i <table.getColumns().get(0).getSize(); i++){
                sb.append("Record");
                if(table.getColumns().get(0).getSize()-1 != i){
                    sb.append(",");
                }
            }
            for(int i = 0; i < list.size(); i++) {
                if(sb.toString().equals("")) {
                    writer.write("<!ELEMENT " + list.get(i).getName() + " EMPTY>");
                }else{
                    writer.write("<!ELEMENT " + list.get(i).getName() + " (" + sb.toString() + ")>");
                }
                writer.newLine();
                writer.write("<!ATTLIST "+ list.get(i).getName());
                writer.newLine();
                writer.write("  xmlns CDATA #FIXED ''");
                writer.newLine();
                writer.write("  type NMTOKEN #REQUIRED>");
                writer.newLine();
                writer.newLine();
            }
            if(table.getColumns().get(0).getSize() != 0) {
                writer.write("<!ELEMENT Record (#PCDATA)>");
                writer.newLine();
                writer.write("<!ATTLIST Record");
                writer.newLine();
                writer.write("  xmlns CDATA #FIXED ''>");
                writer.newLine();
                writer.newLine();
            }
            writer.close();

        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    private Node getColoumns(Document doc, Column column) {
        Element colomn = doc.createElement(column.getName());

        colomn.setAttribute("type", column.getType());
        for (int j = 0; j < column.getSize(); j++) {
            Element node = doc.createElement("Record");
            node.appendChild(doc.createTextNode(String.valueOf(column.getRecordAtIndex(j).getValue())));
            colomn.appendChild(node);
        }
        return colomn;
    }
    @Override
    public Table loadTable(String TableName, String dataBaseName, FilesHandler filesHandler) {
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        Table table = null;
        try {
            File xmlFile = XMLValidator.validate(filesHandler.getPathOfTable(TableName, dataBaseName) + ".xml");
            table = new Table();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(xmlFile);
            doc.getDocumentElement().normalize();
            Element tableElement = doc.getDocumentElement();
            table.setName(TableName);
            NodeList tableNodeList = tableElement.getChildNodes();
            int cnt = 0;
            for (int i = 0 ; i < tableNodeList.getLength() ; i++) {
                if (tableNodeList.item(i).getNodeType() == Node.ELEMENT_NODE) {
                    Element element = (Element) tableNodeList.item(i);
                    String colType = element.getAttribute("type");
                    NodeList list = element.getChildNodes();
                    table.addColumn(element.getTagName(),  colType);
                    for (int j = 0 ; j < list.getLength() ; j++){
                        if (list.item(j).getNodeType() == Node.ELEMENT_NODE) {
                            Element elem = (Element) list.item(j);
                            table.getColumns().get(cnt).addRecord(new Record(Factory.getInstance().getObject(elem.getTextContent()), colType));
                        }
                    }
                    cnt++;
                }
            }
        }catch (ParserConfigurationException | IOException | SAXException | SQLException e) {
            e.printStackTrace();
        }
        return table;
    }
}
