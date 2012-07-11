package myXML;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import org.xml.sax.SAXException;

/**
 *
 * @author Neithan
 */
public class XMLValidator
{
	private static final String SCHEMA_NAMESPACE_URI = "http://www.w3.org/2001/XMLSchema";
	private SchemaFactory factory;
	private Validator validator;
	private Schema schema;
	private ArrayList<Exception> exceptionList;

	public XMLValidator(InputStream schemaFile)
	{
		this.exceptionList = new ArrayList<>();
		this.factory = SchemaFactory.newInstance(SCHEMA_NAMESPACE_URI);

		try
		{
			this.schema = this.factory.newSchema(new StreamSource(schemaFile));
			this.validator = this.schema.newValidator();
		}
		catch (SAXException e)
		{
			this.exceptionList.add(e);
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
		System.out.println("Exception in XMLValidator thrown.");
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

	public boolean validate(String xmlPath)
	{
		return this.validate(new File(xmlPath));
	}

	public boolean validate(File xmlFile)
	{
		// TODO: nen InputStream statt dem File verwenden
		if (xmlFile != null)
		{
			try
			{
				Source source = new StreamSource(xmlFile);

				this.validator.validate(source);
				return true;
			}
			catch(SAXException | IOException | NullPointerException e)
			{
				this.exceptionList.add(e);
				return false;
			}
		}
		else
		{
			return false;
		}
	}

	//@TODO method for showing the last exception in a separate window
}