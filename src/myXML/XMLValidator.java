package myXML;

import java.io.File;
import java.io.IOException;
import java.net.URL;
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
	private Exception lastException;

	public XMLValidator(URL schemaURL)
	{
		this.factory = SchemaFactory.newInstance(SCHEMA_NAMESPACE_URI);

		try
		{
			if (this.validator == null)
			{
				this.schema = this.factory.newSchema(schemaURL);
				this.validator = this.schema.newValidator();
			}
		}
		catch (SAXException e)
		{
			this.lastException = e;
		}
	}

	public XMLValidator(File schemaFile)
	{
		this.factory = SchemaFactory.newInstance(SCHEMA_NAMESPACE_URI);

		try
		{
			if (this.validator == null)
			{
				this.schema = this.factory.newSchema(schemaFile);
				this.validator = this.schema.newValidator();
			}
		}
		catch (SAXException e)
		{
			this.lastException = e;
		}
	}

	public boolean validate(String xmlPath)
	{
		return this.validate(new File(xmlPath));
	}

	public boolean validate(File xmlFile)
	{
		System.out.println(xmlFile);
		if (xmlFile != null)
		{
			try
			{
				Source source = new StreamSource(xmlFile);

				this.validator.validate(source);
				return true;
			}
			catch(SAXException | IOException e)
			{
				this.lastException = e;
				return false;
			}
		}
		else
		{
			return false;
		}
	}

	public Exception getLastException()
	{
		return this.lastException;
	}

	//@TODO method for showing the last exception in a separate window
}