package eg.edu.alexu.csd.oop.db.cs2.filesGenerator;

import eg.edu.alexu.csd.oop.db.cs2.Database;
import eg.edu.alexu.csd.oop.db.cs2.Parser;
import eg.edu.alexu.csd.oop.db.cs2.controller.DatabaseManager;
import eg.edu.alexu.csd.oop.db.cs2.structures.Column;
import eg.edu.alexu.csd.oop.db.cs2.structures.Factory;
import eg.edu.alexu.csd.oop.db.cs2.structures.Record;
import eg.edu.alexu.csd.oop.db.cs2.structures.Table;
import javafx.scene.control.Tab;
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
import java.nio.file.Files;
import java.sql.SQLException;

public class XML implements Parser {
    @Override
    public  void saveTable(Table table, String dataBaseName, FilesHandler filesHandler) throws IOException {
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

            transformer.transform(source, file);

            BufferedWriter writer = new BufferedWriter(new FileWriter(filesHandler.getPathOfTable(table.getName(), dataBaseName)+".dtd"));
            String fileStream = "" ;
            for (int i= 0 ; i <table.getSize(); i++){
                if ( i==table.getSize()-1){
                    fileStream += table.getColumns().get(i).getName();
                }else{
                    fileStream += table.getColumns().get(i).getName() + ",";
                }
            }
            writer.write("<!ELEMENT "+table.getName()+" ("+fileStream+")>");
            writer.newLine();
            for (int j = 0 ; j<table.getSize() ; j++){
                writer.write("<!ELEMENT "+table.getColumns().get(j).getName()+" (#PCDATA)>");
                writer.newLine();
            }
            writer.close();

        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    private Node getColoumns(Document doc, Column column) {
        Element colomn = doc.createElement("Column");

        colomn.setAttribute("name", column.getName());
        colomn.setAttribute("type", column.getType());
        for (int j = 0; j < column.getSize(); j++) {

            colomn.appendChild(getTableElements(doc, "record" + j, column.getRecordAtIndex(j).getValue()));
        }
        return colomn;
    }

    private Node getTableElements(Document doc, String name, Object value) {
        Element node = doc.createElement(name);
        node.appendChild(doc.createTextNode(String.valueOf(value)));
        return node;
    }

    @Override
    public Table loadTable(String TableName, String dataBaseName, FilesHandler filesHandler) {
        File xmlFile = new File(filesHandler.getPathOfTable(TableName, dataBaseName) + ".xml");
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder;
        Table table = new Table();
        try {
            dBuilder = dbFactory.newDocumentBuilder();
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
                    table.addColumn(element.getAttribute("name"),  colType);
                    for (int j = 0 ; j < list.getLength() ; j++){
                        if (list.item(j).getNodeType() == Node.ELEMENT_NODE) {
                            Element elem = (Element) list.item(j);
                            table.getColumns().get(cnt).addRecord(new Record(Factory.getInstance().getObject(elem.getTextContent()), colType));
                        }
                    }
                    cnt++;
                }
            }
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return table;
    }
}
