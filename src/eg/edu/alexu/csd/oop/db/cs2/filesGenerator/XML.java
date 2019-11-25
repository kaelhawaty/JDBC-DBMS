package eg.edu.alexu.csd.oop.db.cs2.filesGenerator;

import eg.edu.alexu.csd.oop.db.cs2.controller.DatabaseManager;
import eg.edu.alexu.csd.oop.db.cs2.structures.Column;
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

public class XML {
    FilesHandler filesHandler = new FilesHandler();
    DatabaseManager databaseManager = new DatabaseManager();

    public void saveTable(Table table) throws IOException {
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
            for (int i = 1; i < table.getSize(); i++) {

                rootElement.appendChild(getColoumns(doc, table.getColumns().get(i)));
            }

            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            //for pretty print
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            DOMSource source = new DOMSource(doc);

            //write to file
            StreamResult file = new StreamResult(new File(filesHandler.getPathOf(table.getName()) + ".xml"));

            transformer.transform(source, file);

            BufferedWriter writer = new BufferedWriter(new FileWriter(filesHandler.getPathOf(table.getName())+".dtd"));
            String fileStream = "" ;
            for (int i= 1 ; i <table.getSize(); i++){
                if ( i==table.getSize()-1){
                    fileStream += table.getColumns().get(i).getName();
                }else{
                    fileStream += table.getColumns().get(i).getName() + ",";
                }
            }
            writer.write("<!ELEMENT "+table.getName()+" ("+fileStream+")>");
            writer.newLine();
            for (int j = 1 ; j<table.getSize() ; j++){
                writer.write("<!ELEMENT "+table.getColumns().get(j).getName()+" (#PCDATA)>");
                writer.newLine();
            }
            writer.close();

        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    private static Node getColoumns(Document doc, Column column) {
        Element colomn = doc.createElement("Coloumn");

        colomn.setAttribute("name", column.getName());
        colomn.appendChild(getTableElements(doc, colomn, "Coloumn_Type", column.getType()));
        for (int j = 0; j < column.getSize(); j++) {

            colomn.appendChild(getTableElements(doc, colomn, "record" + j, column.getRecordAtIndex(j).getValue()));
        }
        return colomn;
    }

    private static Node getTableElements(Document doc, Element element, String name, Object value) {
        Element node = doc.createElement(name);
        node.appendChild(doc.createTextNode(String.valueOf(value)));
        return node;
    }


    public Table loadTable(String TableName) {
        File xmlFile = new File(filesHandler.getPathOf(TableName + ".xml"));
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder;
        Table table = new Table(TableName);
        try {
            dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(xmlFile);
            doc.getDocumentElement().normalize();
            NodeList nodeList = doc.getElementsByTagName(table.getName());
            for (int i = 0 ; i < 100 ; i++) {
                if (nodeList.item(i).getNodeType() == Node.ELEMENT_NODE) {
                    Element element = (Element) nodeList.item(i);
                    table.addColumn(getTagValue("Coloumn name", element), getTagValue("Coloumn_Type", element));
                    if (table.getColumns().get(i).getType().equalsIgnoreCase("int"))
                        table.getColumns().get(i).addRecord(new Record<>(Integer.parseInt((String) getTagValue("Coloumn_Type", element))));
                    else
                        table.getColumns().get(i).addRecord(new Record<>(getTagValue("Coloumn_Type", element)));
                }
            }

        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        }
        return table;
    }

    private static String getTagValue(String tag, Element element) {
        NodeList nodeList = element.getElementsByTagName(tag).item(0).getChildNodes();
        Node node = (Node) nodeList.item(0);
        return node.getNodeValue();
    }
}
