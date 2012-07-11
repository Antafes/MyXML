package myXML;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
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

	public XMLParser(String schema)
	{
		this.validator = new XMLValidator(new File(schema));
		this.factory = DocumentBuilderFactory.newInstance();

		try
		{
			this.builder = this.factory.newDocumentBuilder();
		}
		catch(ParserConfigurationException e)
		{
			System.out.println("Problem occured while creating the document builder.");
		}
	}

	public boolean parse(String path)
	{
		return this.parse(new File(path));
	}

	public boolean parse(File path)
	{
		if (path != null && this.validator.validate(path))
		{
			try
			{
				this.document = this.builder.parse(path);
			}
			catch(SAXException | IOException e) {}

			this.document.getDocumentElement().normalize();

			return true;
		}
		else
		{
			return false;
		}
	}

	public XMLValidator getValidator()
	{
		return this.validator;
	}

	public Document getDocument()
	{
		return this.document;
	}

	public static String getTagValue(String tag, Element element)
	{
		NodeList list = element.getElementsByTagName(tag).item(0).getChildNodes();
		Node valueNode = (Node) list.item(0);
		return valueNode.getNodeValue();
	}

	public static int getTagValueInt(String tag, Element element)
	{
		return Integer.parseInt(XMLParser.getTagValue(tag, element));
	}

	public static double getTagValueDouble(String tag, Element element)
	{
		return Double.parseDouble(XMLParser.getTagValue(tag, element));
	}

	public static ArrayList<String> getAllTagValues(String tag, Element element)
	{
		ArrayList<String> values = new ArrayList<>();

		NodeList list = element.getElementsByTagName(tag).item(0).getChildNodes();
		for (int i = 0; i < list.getLength(); i++)
		{
			Node valueNode = (Node) list.item(i);
			values.add(valueNode.getNodeValue());
		}

		return values;
	}

	public static ArrayList<Integer> getAllTagValuesInt(String tag, Element element)
	{
		ArrayList<String> valuesString = XMLParser.getAllTagValues(tag, element);
		ArrayList<Integer> values = new ArrayList<>();

		for (int i = 0; i < valuesString.size(); i++)
			values.add(Integer.parseInt(valuesString.get(i)));

		return values;
	}

	public static ArrayList<Double> getAllTagValuesDouble(String tag, Element element)
	{
		ArrayList<String> valuesString = XMLParser.getAllTagValues(tag, element);
		ArrayList<Double> values = new ArrayList<>();

		for (int i = 0; i < valuesString.size(); i++)
			values.add(Double.parseDouble(valuesString.get(i)));

		return values;
	}

	public static boolean tagExists(String tag, Element element)
	{
		return element.getElementsByTagName(tag).item(0) != null;
	}
}