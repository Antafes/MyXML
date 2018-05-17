/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package myXML;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 *
 * @author Neithan
 */
public class XMLWriter
{
    private DocumentBuilderFactory factory;
    private DocumentBuilder builder;
    private Document document;
    private Element root;
    private ArrayList<Exception> exceptionList;

    /**
     * Create a new XMLWriter
     *
     * @param rootElement
     */
    public XMLWriter(String rootElement)
    {
        this.prepare(rootElement);
    }

    /**
     * Prepare the DocumentBuilderFactory, the internal exceptionList and create
     * the root element.
     *
     * @param rootElement
     */
    private void prepare(String rootElement)
    {
        this.exceptionList = new ArrayList<>();
        this.factory = DocumentBuilderFactory.newInstance();
        try
        {
            this.builder = this.factory.newDocumentBuilder();
            this.document = this.builder.newDocument();
            this.root = this.document.createElement(rootElement);
            this.document.appendChild(this.root);
        }
        catch(ParserConfigurationException e)
        {
            this.exceptionList.add(e);
            System.out.println("Problem occured while creating the document builder.");
        }
    }

    /**
     * Get the exception list
     *
     * @return ArrayList
     */
    public ArrayList<Exception> getExceptionList()
    {
        return this.exceptionList;
    }

    /**
     * Get the last thrown exception
     *
     * @return Exception
     */
    public Exception getLastException()
    {
        return this.exceptionList.get(this.exceptionList.size() - 1);
    }

    /**
     * Print all thrown exceptions into the console
     */
    public void printExceptions()
    {
        if (this.getExceptionList().size() > 0)
        {
            System.out.println("Exception in XMLWriter thrown.");
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
     * @param attributes
     */
    public void addRootNodeAttributes(HashMap<String, String> attributes)
    {
        for (Map.Entry<String, String> entry : attributes.entrySet())
        {
            this.root.setAttribute(entry.getKey(), entry.getValue());
        }
    }

    /**
     * Add a child element with a name, a value and attributes
     *
     * @param parent
     * @param name
     * @param value
     * @param attributes
     *
     * @return Element
     */
    public Element addChild(Element parent, String name, String value, HashMap<String, String> attributes)
    {
        Element e = this.document.createElement(name);

        if (value != null)
        {
            e.setTextContent(value);
        }

        if (attributes != null)
        {
            for (Map.Entry<String, String> entry : attributes.entrySet())
            {
                e.setAttribute(entry.getKey(), entry.getValue());
            }
        }

        parent.appendChild(e);

        return e;
    }

    /**
     * Add a child element with a name and attributes
     *
     * @param parent
     * @param name
     * @param attributes
     *
     * @return Element
     */
    public Element addChild(Element parent, String name, HashMap<String, String> attributes)
    {
        return this.addChild(parent, name, null, attributes);
    }

    /**
     * Add a child element with a name and a value
     *
     * @param parent
     * @param name
     * @param value
     *
     * @return Element
     */
    public Element addChild(Element parent, String name, String value)
    {
        return this.addChild(parent, name, value, null);
    }

    /**
     * Add a child element with a name
     *
     * @param parent
     * @param name
     *
     * @return Element
     */
    public Element addChild(Element parent, String name)
    {
        return this.addChild(parent, name, null, null);
    }

    /**
     * Add a child element to the root element with a name and attributes
     *
     * @param name
     * @param attributes
     *
     * @return Element
     */
    public Element addChild(String name, HashMap<String, String> attributes)
    {
        return this.addChild(this.root, name, attributes);
    }

    /**
     * Add a child element to the root element with a name and a value
     *
     * @param name
     * @param value
     *
     * @return Element
     */
    public Element addChild(String name, String value)
    {
        return this.addChild(this.root, name, value, null);
    }

    /**
     * Add a child element to the root element with a name
     *
     * @param name
     *
     * @return Element
     */
    public Element addChild(String name)
    {
        return this.addChild(this.root, name, null, null);
    }

    /**
     * Generate the xml and write it to the given path
     *
     * @param path
     */
    public void write(String path)
    {
        this.write(new File(path));
    }

    /**
     * Generate the xml and write it to the given file
     *
     * @param file
     */
    public void write(File file)
    {
        try
        {
            if (!file.exists())
            {
                try
                {
                    File path = new File(file.getParent());
                    path.mkdirs();
                    file.createNewFile();
                }
                catch (IOException ex)
                {
                    this.exceptionList.add(ex);
                }
            }

            FileOutputStream fos = new FileOutputStream(file);
            OutputStreamWriter osw = new OutputStreamWriter(fos, "UTF-8");
            this.write(osw);
        }
        catch (FileNotFoundException | UnsupportedEncodingException e)
        {
            this.exceptionList.add(e);
        }
    }

    /**
     * Write the created XML to a ByteArrayOutputStream.
     *
     * @return
     */
    public ByteArrayOutputStream writeToOutputStream()
    {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        try
        {
            TransformerFactory transFactory = TransformerFactory.newInstance();
            Transformer transformer = transFactory.newTransformer();
            DOMSource source = new DOMSource(this.document);
            StreamResult result = new StreamResult(baos);
            transformer.transform(source, result);
        }
        catch (TransformerConfigurationException ex)
        {
            this.exceptionList.add(ex);
        }
        catch (TransformerException ex)
        {
            this.exceptionList.add(ex);
        }

        return baos;
    }

    /**
     * Generate the xml and write it to the given OutputStream
     *
     * @param writer
     */
    public void write(OutputStreamWriter writer)
    {
        try
        {
            TransformerFactory transFactory = TransformerFactory.newInstance();
            Transformer transformer = transFactory.newTransformer();
            DOMSource source = new DOMSource(this.document);
            StreamResult result = new StreamResult(writer);
            transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");
            transformer.setOutputProperty(OutputKeys.METHOD, "xml");
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
            transformer.transform(source, result);
        }
        catch (TransformerConfigurationException ex)
        {
            this.exceptionList.add(ex);
        }
        catch (TransformerException ex)
        {
            this.exceptionList.add(ex);
        }
    }

    /**
     * Generate the xml and write it to an url.
     *
     * @param urlString
     *
     * @return
     */
    public InputStream writeToURL(String urlString)
    {
        return this.writeToURL(urlString, "UTF-8", null);
    }

    /**
     * Generate the xml and write it to an url using the given charset.
     *
     * @param urlString
     * @param charset
     *
     * @return
     */
    public InputStream writeToURL(String urlString, String charset)
    {
        return this.writeToURL(urlString, charset, null);
    }

    /**
     * Generate the xml and write it to an url with the given additional
     * parameters.
     *
     * @param urlString
     * @param additionalParams
     * @return
     */
    public InputStream writeToURL(String urlString, HashMap<String, Object> additionalParams)
    {
        return this.writeToURL(urlString, "UTF-8", additionalParams);
    }

    /**
     * Generate the xml and write it to an url with the given additional
     * parameters using the given charset.
     *
     * @param urlString
     * @param charset
     * @param additionalParams
     *
     * @return
     */
    public InputStream writeToURL(String urlString, String charset, HashMap<String, Object> additionalParams)
    {
        try
        {
            URL url = new URL(urlString);
            URLConnection connection = url.openConnection();
            connection.setUseCaches(false);
            connection.setDoOutput(true);
            connection.setRequestProperty("accept-charset", charset);
            connection.setRequestProperty("content-type", "application/x-www-form-urlencoded");
            String query = "";

            if (additionalParams != null)
            {
                for (Map.Entry<String, Object> entry : additionalParams.entrySet()) {
                    String key = entry.getKey();
                    Object value = entry.getValue();
                    query += String.format("%s=%s&", URLEncoder.encode(key, charset), URLEncoder.encode(value.toString(), charset));
                }
            }

            TransformerFactory transFactory = TransformerFactory.newInstance();
            Transformer transformer = transFactory.newTransformer();
            DOMSource source = new DOMSource(this.document);
            StringWriter sw = new StringWriter();
            StreamResult result = new StreamResult(sw);
            transformer.transform(source, result);

            query += String.format("%s=%s", URLEncoder.encode("message", charset), URLEncoder.encode(sw.toString(), charset));

            OutputStreamWriter writer = null;
            InputStream response;
            try
            {
                writer = new OutputStreamWriter(connection.getOutputStream(), charset);
                writer.write(query); // Write POST query string (if any needed).
            }
            finally
            {
                if (writer != null)
                {
                    try
                    {
                        writer.close();
                    }
                    catch (IOException ex)
                    {
                        this.exceptionList.add(ex);
                    }
                }
            }

            response = connection.getInputStream();

            return response;
        }
        catch (TransformerConfigurationException ex)
        {
            this.exceptionList.add(ex);
        }
        catch (TransformerException ex)
        {
            this.exceptionList.add(ex);
        }
        catch (MalformedURLException ex)
        {
            this.exceptionList.add(ex);
        }
        catch (IOException ex)
        {
            this.exceptionList.add(ex);
        }
        catch (Exception ex)
        {
            this.exceptionList.add(ex);
        }

        return null;
    }

    /**
     * Prepare the xml so it can be inserted into other xml
     *
     * @param xml
     *
     * @return
     */
    public static String prepareXML(String xml)
    {
        String preparedXML;

        preparedXML = xml.replace("&", "&#x26;");
        preparedXML = preparedXML.replace("<", "&#x3C;");
        preparedXML = preparedXML.replace(">", "&#x3E;");
        preparedXML = preparedXML.replace("\"", "&#x22;");
        preparedXML = preparedXML.replace("'", "&#x27;");

        return preparedXML;
    }
}