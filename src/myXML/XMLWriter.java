/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package myXML;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
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
	private XMLValidator validator;
	private DocumentBuilderFactory factory;
	private DocumentBuilder builder;
	private Document document;
	private Element root;
	private ArrayList<Exception> exceptionList;

	public XMLWriter(InputStream schema, String rootElement)
	{
		this.validator = new XMLValidator(schema);
		this.prepare(rootElement);
	}

	public XMLWriter(String rootElement)
	{
		this.prepare(rootElement);
	}

	private void prepare(String rootElement)
	{
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

	public ArrayList<Exception> getExceptionList()
	{
		return this.exceptionList;
	}

	public Exception getLastException()
	{
		return this.exceptionList.get(this.exceptionList.size() - 1);
	}

	public void printExceptions()
	{
		System.out.println("Exception in XMLWriter thrown.");
		for (int i = 0; i < this.getExceptionList().size(); i++)
		{
			Exception ex = this.getExceptionList().get(i);
			System.out.println(ex.getClass());

			for (int x = 0; x < ex.getStackTrace().length; x++)
			{
				System.out.println(ex.getStackTrace()[x]);
			}
			System.out.println(ex.getMessage());
			System.out.println("");
		}
	}

	public Element addChild(Element parent, String name, String value, HashMap<String, String> attributes)
	{
		Element e = this.document.createElement(name);

		if (value != null)
			e.setTextContent(value);

		if (attributes != null)
			for (Map.Entry<String, String> entry : attributes.entrySet())
				e.setAttribute(entry.getKey(), entry.getValue());

		parent.appendChild(e);

		return e;
	}

	public Element addChild(Element parent, String name, HashMap<String, String> attributes)
	{
		return this.addChild(parent, name, null, attributes);
	}

	public Element addChild(Element parent, String name, String value)
	{
		return this.addChild(parent, name, value, null);
	}

	public Element addChild(Element parent, String name)
	{
		return this.addChild(parent, name, null, null);
	}

	public Element addChild(String name)
	{
		return this.addChild(this.root, name, null, null);
	}

	public void writeFile(String pathname)
	{
		try
		{
			File xmlFile = new File(pathname);

			if (!xmlFile.exists())
			{
				try
				{
					File path = new File(xmlFile.getParent());
					path.mkdirs();
					xmlFile.createNewFile();
				}
				catch (IOException ex)
				{
					this.exceptionList.add(ex);
				}
			}

			TransformerFactory transFactory = TransformerFactory.newInstance();
			Transformer transformer = transFactory.newTransformer();
			DOMSource source = new DOMSource(this.document);
			StreamResult result = new StreamResult(xmlFile);
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
}