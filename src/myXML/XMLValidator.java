package myXML;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
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

    /**
     * Create a new XMLValidator.
     *
     * @param schemaFile The path to a schema to use for validation.
     *
     * @throws FileNotFoundException
     */
    public XMLValidator(String schemaFile) throws FileNotFoundException
    {
        this(new File(schemaFile));
    }

    /**
     * Create a new XMLValidator.
     *
     * @param schemaFile The schema to use for validation.
     *
     * @throws FileNotFoundException
     */
    public XMLValidator(File schemaFile) throws FileNotFoundException
    {
        this(new FileInputStream(schemaFile));
    }

    /**
     * Create a new XMLValidator.
     *
     * @param schemaFile The schema to use for validation.
     */
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

    /**
     * Get the list of exceptions thrown during validation.
     *
     * @return List of Exception objects.
     */
    public ArrayList<Exception> getExceptionList()
    {
        return this.exceptionList;
    }

    /**
     * Get the last exception thrown during validation.
     *
     * @return An Exception object.
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
            System.out.println("Exception in XMLValidator thrown.");
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
     * Validate the XML file at the given path.
     *
     * @param xmlPath The path of the XML file.
     *
     * @return Returns true on success, otherwise false.
     */
    public boolean validate(String xmlPath)
    {
        return this.validate(new File(xmlPath));
    }

    /**
     * Validate the given XML file.
     *
     * @param xmlFile The XML file.
     *
     * @return Returns true on success, otherwise false.
     */
    public boolean validate(File xmlFile)
    {
        try
        {
            return this.validate(new FileInputStream(xmlFile));
        }
        catch (FileNotFoundException e)
        {
            this.exceptionList.add(e);
            return false;
        }
    }

    /**
     * Validate the given InputStream of an XML file.
     *
     * @param stream InputStream of the XML file.
     *
     * @return Returns true on success, otherwise false.
     */
    public boolean validate(InputStream stream)
    {
        if (stream != null)
        {
            try
            {
                Source source = new StreamSource(stream);

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