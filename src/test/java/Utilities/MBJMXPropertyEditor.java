package Utilities;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.XMLConfiguration;
import org.apache.commons.configuration.tree.xpath.XPathExpressionEngine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.StringWriter;

/**
 *
 */
public class MBJMXPropertyEditor {
    private static final Logger log = LoggerFactory.getLogger(Utils.class);

    public static void editPublisherMessageCount(String filePath, String value) throws ConfigurationException {
        String messageCountPath = "/hashTree/hashTree/ThreadGroup/elementProp/stringProp";
        editProperty(filePath, messageCountPath, value);
    }

    private static void editProperty(String filePath, String propertyName, String value) throws ConfigurationException {
        XMLConfiguration config = new XMLConfiguration(filePath);
        config.setExpressionEngine(new XPathExpressionEngine());
        printXMLFile(config);



        String currentValue = config.getString(propertyName);
        config.setProperty(propertyName, value);
        config.save();
        log.info("Updated " + propertyName + "'. Old : " + currentValue + " New : " + value);
    }

    private static void printXMLFile(XMLConfiguration config) throws ConfigurationException {
        StringWriter stringWriter = new StringWriter();
        config.save(stringWriter);
        System.out.println(stringWriter.toString());
    }
}
