package utilities;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.XMLConfiguration;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.jmeter.util.ShutdownClient;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.Properties;
import java.util.concurrent.TimeUnit;


/**
 * Utility class for MB performance testing.
 */
public class Utils {
    private static final Log log = LogFactory.getLog(Utils.class);

    /**
     * Prints the JMX configuration.
     *
     * @param config The xml configuration
     * @throws ConfigurationException
     */
    public static void printXMLFile(XMLConfiguration config) throws ConfigurationException {
        StringWriter stringWriter = new StringWriter();
        config.save(stringWriter);
        System.out.println(stringWriter.toString());
    }

    /**
     * Update the thread count of the JMX file.
     *
     * @param config
     * @param value
     * @throws ConfigurationException
     */
    public static void editThreadCount(XMLConfiguration config, String value) throws ConfigurationException {
        String messageCountPath = "/hashTree/hashTree/ThreadGroup/stringProp[@name='ThreadGroup.num_threads']";
        editProperty(config, messageCountPath, value);
    }

    /**
     * Updates the ramp up time of the JMX file.
     *
     * @param config
     * @param value
     * @throws ConfigurationException
     */
    public static void editRampUpTime(XMLConfiguration config, String value) throws ConfigurationException {
        String messageCountPath = "/hashTree/hashTree/ThreadGroup/stringProp[@name='ThreadGroup.ramp_time']";
        editProperty(config, messageCountPath, value);
    }

    /**
     * @param config
     * @param value
     * @throws ConfigurationException
     */
    public static void editPublisherJNDIPath(XMLConfiguration config, String value) throws ConfigurationException {
        String messageCountPath = "/hashTree/hashTree/hashTree/PublisherSampler/stringProp[@name='jms.provider_url']";
        editProperty(config, messageCountPath, value);
    }

    public static void editSubscriberJNDIPath(XMLConfiguration config, String value) throws ConfigurationException {
        String messageCountPath = "/hashTree/hashTree/hashTree/SubscriberSampler/stringProp[@name='jms.provider_url']";
        editProperty(config, messageCountPath, value);
    }

    public static void editPublisherDestinationName(XMLConfiguration config, String value) throws
            ConfigurationException {
        String messageCountPath = "/hashTree/hashTree/hashTree/PublisherSampler/stringProp[@name='jms.topic']";
        editProperty(config, messageCountPath, value);
    }

    public static void editSubscriberDestinationName(XMLConfiguration config, String value) throws
            ConfigurationException {
        String messageCountPath = "/hashTree/hashTree/hashTree/SubscriberSampler/stringProp[@name='jms.topic']";
        editProperty(config, messageCountPath, value);
    }

    public static void editPublisherInputFilePath(XMLConfiguration config, String value) throws ConfigurationException {
        String messageCountPath = "/hashTree/hashTree/hashTree/PublisherSampler/stringProp[@name='jms.input_file']";
        editProperty(config, messageCountPath, value);
    }

    public static void editProperty(XMLConfiguration config, String propertyXPath, String value) throws
            ConfigurationException {
        String currentValue = config.getString(propertyXPath);
        config.setProperty(propertyXPath, value);
        String newValue = config.getString(propertyXPath);
        log.info("Updated " + propertyXPath + "'. Old : " + currentValue + " New : " + newValue);
    }

    public static void waitUntilNoChangeInJTLFile(MBJMeterTestManager manager) {
        double oldSize = -1;
        double currentSize = 0;

        while (currentSize != oldSize) {
            try {
                // Waits till the consumer client received more messages.
                log.info("Waiting for no changes in publisher/subscriber...");
                TimeUnit.SECONDS.sleep(15);
            } catch (InterruptedException e) {
                log.error("Error waiting for receiving messages.", e);
            }
            // Updating message counters
            oldSize = currentSize;
            currentSize = new File(manager.getReportFileFullPath()).length();//client.getReceivedMessageCount();
        }

        log.info("Waiting finished.");
    }

    public static double getMessageCount(MBJMeterTestManager manager) throws ParserConfigurationException,
            IOException, SAXException {
        if (Utils.loadProperties(manager.getClass().getResource("/jmeter.properties").getPath()).get("jmeter.save.saveservice.output_format").equals("xml")) {
            // Defines a factory API that enables applications to obtain a parser that produces DOM object trees from XML
            // documents.
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();

            // The Document interface represents the entire HTML or XML document. Conceptually, it is the root of the
            // document tree, and provides the primary access to the document's data.
            Document doc = factory.newDocumentBuilder().parse(manager.getReportFileFullPath());

            // Returns a NodeList of all the Elements in document order with a given tag name and are contained in the
            // document.
            NodeList nodes = doc.getElementsByTagName("sample");
            return nodes.getLength();
        }else{
            CSVParser parser = new CSVParser(new FileReader(new File(manager.getReportFileFullPath())), CSVFormat.EXCEL);
            if (log.isDebugEnabled()) {
                for (CSVRecord strings : parser.getRecords()) {
                    log.info("CSV : " + strings.toString());
                }
            }
            return (double)parser.getRecords().size() -1;
        }
    }

    public static void stopAllTests() throws IOException {
        ShutdownClient.main(new String[]{"StopTestNow"});
    }

    public static Properties loadProperties(String propertyFilePath) throws IOException {
        InputStream input = new FileInputStream(propertyFilePath);
        Properties prop = new Properties();
        prop.load(input);

        input.close();
        return prop;
    }
}
