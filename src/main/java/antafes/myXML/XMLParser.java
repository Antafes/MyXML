/*
 * This file is part of MyXML_v8.
 *
 * MyXML_v8 is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * MyXML_v8 is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with MyXML_v8. If not, see <http://www.gnu.org/licenses/>.
 *
 * @package MyXML_v8
 * @author Marian Pollzien <map@wafriv.de>
 * @copyright (c) 2019, Marian Pollzien
 * @license https://www.gnu.org/licenses/lgpl.html LGPLv3
 */

package antafes.myXML;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 *
 * @author Neithan
 */
public class XMLParser
{
    private XMLValidator validator;
    private DocumentBuilderFactory factory;
    private DocumentBuilder builder;
    private Document document;
    private ArrayList<Exception> exceptionList;

    /**
     * Create a new XML parser.
     */
    public XMLParser()
    {
        this.prepare();
    }

    /**
     * Create a new XML parser with the given schema.
     *
     * @param schema An XML schema to use in the XML file.
     *
     * @throws FileNotFoundException
     */
    public XMLParser(String schema) throws FileNotFoundException
    {
        this(new File(schema));
    }

    /**
     * Create a new XML parser with the given schema.
     *
     * @param schema An XML schema file to use in the XML file.
     *
     * @throws FileNotFoundException
     */
    public XMLParser(File schema) throws FileNotFoundException
    {
        this(new FileInputStream(schema));
    }

    /**
     * Create a new XML parser with the given schema.
     *
     * @param schema An XML schema input stream to use in the XML file.
     */
    public XMLParser(InputStream schema)
    {
        this.validator = new XMLValidator(schema);
        this.prepare();
    }

    /**
     * Prepare the XML parser for its work. This includes initializing some
     * class variables.
     */
    private void prepare()
    {
        this.exceptionList = new ArrayList<>();
        this.factory = DocumentBuilderFactory.newInstance();

        try
        {
            this.builder = this.factory.newDocumentBuilder();
        }
        catch(ParserConfigurationException e)
        {
            this.exceptionList.add(e);
            System.out.println("Problem occured while creating the document builder.");
        }
    }

    /**
     * Get the list of exceptions thrown during the parse process.
     *
     * @return An ArrayList of Exception objects.
     */
    public ArrayList<Exception> getExceptionList()
    {
        return this.exceptionList;
    }

    /**
     * Get the last thrown exception.
     *
     * @return The last exception as an object.
     */
    public Exception getLastException()
    {
        return this.exceptionList.get(this.exceptionList.size() - 1);
    }

    /**
     * Print every thrown exception including the stack trace on system.out
     */
    public void printExceptions()
    {
        if (this.getExceptionList().size() > 0)
        {
            System.out.println("Exception in XMLParser thrown.");
            for (int i = 0; i < this.getExceptionList().size(); i++)
            {
                Exception ex = this.getExceptionList().get(i);
                System.out.println(ex.getClass());

                for (StackTraceElement stackTrace : ex.getStackTrace())
                {
                    System.out.println(stackTrace);
                }
                System.out.println(ex.getMessage());
                System.out.println("");
            }
        }
    }

    /**
     * Parse the file at the given path.
     *
     * @param path The path where an XML file resides.
     *
     * @return Returns true if successful, otherwise false.
     */
    public boolean parse(String path)
    {
        return this.parse(new File(path));
    }

    /**
     * Parse the given file.
     *
     * @param file The file to parse.
     *
     * @return Returns true if successful, otherwise false.
     */
    public boolean parse(File file)
    {
        try
        {
            return this.parse(new FileInputStream(file));
        }
        catch (FileNotFoundException e)
        {
            this.exceptionList.add(e);
            return false;
        }
    }

    /**
     * Parse the given InputStream.
     *
     * @param stream The stream to parse.
     *
     * @return Returns true if successful, otherwise false.
     */
    public boolean parse(InputStream stream)
    {
        if (stream != null)
        {
            try
            {
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                byte[] buf = new byte[1024];
                int n;
                while ((n = stream.read(buf)) >= 0)
                    baos.write(buf, 0, n);
                byte[] content = baos.toByteArray();

                InputStream is1 = new ByteArrayInputStream(content);

                if (this.validator != null && !this.validator.validate(is1))
                {
                    return false;
                }

                try
                {
                    InputStream is2 = new ByteArrayInputStream(content);
                    this.document = this.builder.parse(is2);
                    this.document.getDocumentElement().normalize();

                    return true;
                }
                catch(SAXException | IOException e)
                {
                    this.exceptionList.add(e);
                    return false;
                }
            }
            catch(IOException ex)
            {
                Logger.getLogger(XMLParser.class.getName()).log(Level.SEVERE, null,ex);
            }
        }

        return false;
    }

    /**
     * Get the XML validator. This will only return an object, if a schema has
     * been set.
     *
     * @return The XML validator used to check an XML file.
     */
    public XMLValidator getValidator()
    {
        return this.validator;
    }

    /**
     * Get the contents of the XML file.
     *
     * @return Object representation of the XML file.
     */
    public Document getDocument()
    {
        return this.document;
    }

    /**
     * Get the root element of the parsed XML file.
     *
     * @return The XML Element representing the root node.
     */
    public Element getRootElement()
    {
        document = this.getDocument();
        Node rootNode = document.getChildNodes().item(0);
        return (Element) rootNode;
    }

    /**
     * Get an element node by tag from a given parent.
     *
     * @param tag     The tag to look for.
     * @param element The parent element.
     *
     * @return Returns a Node object on success.
     */
    public static Node getTagNode(String tag, Element element)
    {
        NodeList list = element.getElementsByTagName(tag).item(0).getChildNodes();
        return (Node) list.item(0);
    }

    /**
     * Get an element by tag from a given parent.
     *
     * @param tag     The tag to look for.
     * @param element The parent element.
     *
     * @return Returns an Element object on success, otherwise null.
     */
    public static Element getTagElement(String tag, Element element)
    {
        return (Element) element.getElementsByTagName(tag).item(0);
    }

    /**
     * Get the value of an element which is defined by a tag and searched below
     * its parent element.
     *
     * @param tag     The tag to search the element for.
     * @param element The parent element.
     *
     * @return The elements value as a string.
     */
    public static String getTagValue(String tag, Element element)
    {
        Node valueNode = XMLParser.getTagNode(tag, element);

        if (valueNode != null)
        {
            return valueNode.getNodeValue();
        }

        return "";
    }

    /**
     * Get the value as described by getTagValue and parse it into an integer.
     *
     * @param tag     he tag to search the element for.
     * @param element The parent element.
     *
     * @return The elements value as an integer.
     */
    public static int getTagValueInt(String tag, Element element)
    {
        return Integer.parseInt(XMLParser.getTagValue(tag, element));
    }

    /**
     * Get the value as described by getTagValue and parse it into a double.
     *
     * @param tag     he tag to search the element for.
     * @param element The parent element.
     *
     * @return The elements value as a double.
     */
    public static double getTagValueDouble(String tag, Element element)
    {
        return Double.parseDouble(XMLParser.getTagValue(tag, element));
    }

    /**
     * Get the value as described by getTagValue and parse it into a boolean.
     *
     * @param tag     he tag to search the element for.
     * @param element The parent element.
     *
     * @return The elements value as aboolean.
     */
    public static boolean getTagValueBoolean(String tag, Element element)
    {
        return Boolean.parseBoolean(XMLParser.getTagValue(tag, element));
    }

    /**
     * Get an elements direct value.
     *
     * @param element
     *
     * @return
     */
    public static String getElementValue(Element element) {
        return element.getFirstChild().getNodeValue();
    }

    /**
     * Get an elements direct value and parse it into an integer.
     *
     * @param element
     *
     * @return
     */
    public static int getElementValueInt(Element element) {
        return Integer.parseInt(XMLParser.getElementValue(element));
    }

    /**
     * Get an elements direct value and parse it into an integer.
     *
     * @param element
     *
     * @return
     */
    public static double getElementValueDouble(Element element) {
        return Double.parseDouble(XMLParser.getElementValue(element));
    }

    /**
     * Get an elements direct value and parse it into an integer.
     *
     * @param element
     *
     * @return
     */
    public static boolean getElementValueBoolean(Element element) {
        return Boolean.parseBoolean(XMLParser.getElementValue(element));
    }

    /**
     * Get all tags inside of the defined element with the given tag name.
     *
     * @param tag     The tag to search for.
     * @param element The parent element.
     *
     * @return A list of elements.
     */
    public static ArrayList<Element> getAllTags(String tag, Element element)
    {
        ArrayList<Element> elements = new ArrayList<>();

        if (element.hasChildNodes() && element.getElementsByTagName(tag).getLength() > 0) {
            NodeList list = element.getElementsByTagName(tag).item(0).getChildNodes();
            for (int i = 0; i < list.getLength(); i++)
            {
                Node node = (Node) list.item(i);

                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    elements.add((Element) node);
                }
            }
        }

        return elements;
    }

    /**
     * Get the value of each element which is defined by a tag and searched
     * below its parent element.
     *
     * @param tag     The tag to search the elements for.
     * @param element The parent element.
     *
     * @return A list of values as string.
     */
    public static ArrayList<String> getAllTagValues(String tag, Element element)
    {
        ArrayList<String> values = new ArrayList<>();

        for (Element listElement : XMLParser.getAllTags(tag, element)) {
            values.add(listElement.getNodeValue());
        }

        return values;
    }

    /**
     * Get the values as described by getAllTagValues and parse them into an
     * integer.
     *
     * @param tag     The tag to search the elements for.
     * @param element The parent element.
     *
     * @return A list of values as integer.
     */
    public static ArrayList<Integer> getAllTagValuesInt(String tag, Element element)
    {
        ArrayList<Integer> values = new ArrayList<>();

        for (String value : XMLParser.getAllTagValues(tag, element)) {
            values.add(Integer.parseInt(value));
        }

        return values;
    }

    /**
     * Get the values as described by getAllTagValues and parse them into a
     * double.
     *
     * @param tag     The tag to search the elements for.
     * @param element The parent element.
     *
     * @return A list of values as double.
     */
    public static ArrayList<Double> getAllTagValuesDouble(String tag, Element element)
    {
        ArrayList<Double> values = new ArrayList<>();

        for (String value : XMLParser.getAllTagValues(tag, element)) {
            values.add(Double.parseDouble(value));
        }

        return values;
    }

    /**
     * Get a list of all child elements.
     *
     * @param element
     *
     * @return
     */
    public static ArrayList<Element> getAllChildren(Element element)
    {
        ArrayList<Element> elements = new ArrayList<>();

        NodeList list = element.getChildNodes();
        for (int i = 0; i < list.getLength(); i++)
        {
            Node valueNode = (Node) list.item(i);

            if (valueNode.getNodeType() == Node.ELEMENT_NODE) {
                elements.add((Element) valueNode);
            }
        }

        return elements;
    }

    /**
     * Get the first children of the given element.
     *
     * @param element
     *
     * @return
     */
    public static Element getFirstChildren(Element element)
    {
        return XMLParser.getAllChildren(element).get(0);
    }

    /**
     * Check if the given tag exists on an element below the given parent.
     *
     * @param tag     The tag to look for.
     * @param element The parent element.
     *
     * @return Returns true if found, otherwise false
     */
    public static boolean tagExists(String tag, Element element)
    {
        return element.getElementsByTagName(tag).item(0) != null;
    }

    /**
     * Parse a prepared XML string to convert the escaped characters back into
     * the original.
     *
     * @param xml The prepared XML as a string.
     *
     * @return The parsed XML.
     */
    public static String parsePrepareXML(String xml)
    {
        String preparedXML;

        preparedXML = xml.replace("&#x26;", "&");
        preparedXML = preparedXML.replace("&#x3C;", "<");
        preparedXML = preparedXML.replace("&#x3E;", ">");
        preparedXML = preparedXML.replace("&#x22;", "\"");
        preparedXML = preparedXML.replace("&#x27;", "'");

        return preparedXML;
    }
}