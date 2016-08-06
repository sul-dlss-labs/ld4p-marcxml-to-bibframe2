import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Properties;

public class PropGet
{
    public static Properties getProps(String location)
    {
        Properties props = new Properties();
        try 
        {
            File inFile = new File(new File(Thread.currentThread().getContextClassLoader().getResource("").toURI()), location);
            FileInputStream in = new FileInputStream(inFile);
            props.load(in);
            in.close();
        }
        catch (FileNotFoundException e)
        {
            System.err.println(e.getMessage());
        }
        catch (IOException e)
        {
            System.err.println(e.getMessage());
        }
        catch (URISyntaxException e)
        {
            System.err.println(e.getMessage());
        }
        
        return props;
    }
};
