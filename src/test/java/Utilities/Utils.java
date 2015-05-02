package Utilities;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.XMLConfiguration;
import org.apache.commons.configuration.reloading.FileChangedReloadingStrategy;
import org.apache.commons.configuration.tree.xpath.XPathExpressionEngine;
import org.apache.jorphan.collections.HashTree;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.StringWriter;
import java.util.Map;


/**
 *
 */
public class Utils {
    private static final Logger log = LoggerFactory.getLogger(Utils.class);

    public  static void printXMLFile(XMLConfiguration config) throws ConfigurationException {
        StringWriter stringWriter = new StringWriter();
        config.save(stringWriter);
        System.out.println(stringWriter.toString());
    }

    public static void editPublisherMessageCount(XMLConfiguration config, String value) throws ConfigurationException {
        String messageCountPath = "/hashTree/hashTree/ThreadGroup/elementProp/stringProp[@name='LoopController.loops']";
        editProperty(config, messageCountPath, value);
    }

    public static void editThreadCount(XMLConfiguration config, String value) throws ConfigurationException {
        String messageCountPath = "/hashTree/hashTree/ThreadGroup/stringProp[@name='ThreadGroup.num_threads']";
        editProperty(config, messageCountPath, value);
    }

    public static void editRampUpTime(XMLConfiguration config, String value) throws ConfigurationException {
        String messageCountPath = "/hashTree/hashTree/ThreadGroup/stringProp[@name='ThreadGroup.ramp_time']";
        editProperty(config, messageCountPath, value);
    }

    public static void editPublisherJNDIPath(XMLConfiguration config, String value) throws ConfigurationException {
        String messageCountPath = "/hashTree/hashTree/hashTree/PublisherSampler/stringProp[@name='jms.provider_url']";
        editProperty(config, messageCountPath, value);
    }

    public static void editSubscriberJNDIPath(XMLConfiguration config, String value) throws ConfigurationException {
        String messageCountPath = "/hashTree/hashTree/hashTree/SubscriberSampler/stringProp[@name='jms.provider_url']";
        editProperty(config, messageCountPath, value);
    }

    public static void editPublisherDestinationName(XMLConfiguration config, String value) throws ConfigurationException {
        String messageCountPath = "/hashTree/hashTree/hashTree/PublisherSampler/stringProp[@name='jms.topic']";
        editProperty(config, messageCountPath, value);
    }

    public static void editSubscriberDestinationName(XMLConfiguration config, String value) throws ConfigurationException {
        String messageCountPath = "/hashTree/hashTree/hashTree/SubscriberSampler/stringProp[@name='jms.topic']";
        editProperty(config, messageCountPath, value);
    }

    public static void editPublisherInputFilePath(XMLConfiguration config, String value) throws ConfigurationException {
        String messageCountPath = "/hashTree/hashTree/hashTree/PublisherSampler/stringProp[@name='jms.input_file']";
        editProperty(config, messageCountPath, value);
    }

    public static void editProperty(XMLConfiguration config, String propertyName, String value) throws ConfigurationException {
        String currentValue = config.getString(propertyName);
        config.setProperty(propertyName, value);
        log.info("Updated " + propertyName + "'. Old : " + currentValue + " New : " + value);
    }
}
