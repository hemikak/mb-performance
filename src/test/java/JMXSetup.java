import exceptions.MBPerformanceException;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.XMLConfiguration;
import org.apache.commons.configuration.tree.xpath.XPathExpressionEngine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wso2.automation.tools.jmeter.JMeterTest;
import org.xml.sax.SAXException;
import utilities.MBJMeterTestManager;
import utilities.Utils;

import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.util.Properties;

/**
 *
 */
public class JMXSetup {
    private static final Logger log = LoggerFactory.getLogger(JMXSetup.class);

    protected XMLConfiguration publisher;
    protected XMLConfiguration subscriber;
    protected MBJMeterTestManager publisherManager;
    protected MBJMeterTestManager subscriberManager;
    private String publisherPath = getClass().getResource("/jmx/JMSPublisher.jmx").getPath();
    private String subscriberPath = getClass().getResource("/jmx/JMSSubscriber.jmx").getPath();
    private static final String PUBLISHER_PROPERTY_PREFIX = "publisher.";
    private static final String SUBSCRIBER_PROPERTY_PREFIX = "subscriber.";

    public void init() throws ConfigurationException, IOException {
        publisher = new XMLConfiguration(publisherPath);
        publisher.setExpressionEngine(new XPathExpressionEngine());

        subscriber = new XMLConfiguration(subscriberPath);
        subscriber.setExpressionEngine(new XPathExpressionEngine());

        Utils.editPublisherJNDIPath(publisher, getClass().getResource("/jndi.properties").getPath());
        Utils.editSubscriberJNDIPath(subscriber, getClass().getResource("/jndi.properties").getPath());

        Properties props = Utils.loadProperties(getClass().getResource("/performance.properties").getPath());

        for (String propertyKey : props.stringPropertyNames()) {
            if (propertyKey.startsWith(PUBLISHER_PROPERTY_PREFIX)) {
                Utils.editProperty(publisher, propertyKey.replace(PUBLISHER_PROPERTY_PREFIX, ""), props.getProperty
                        (propertyKey));
            }
            if (propertyKey.startsWith(SUBSCRIBER_PROPERTY_PREFIX)) {
                Utils.editProperty(subscriber, propertyKey.replace(SUBSCRIBER_PROPERTY_PREFIX, ""), props.getProperty
                        (propertyKey));
            }
        }
    }

    public void runTest() throws ConfigurationException, IOException, MBPerformanceException,
            ParserConfigurationException, SAXException {
        publisher.save();
        subscriber.save();

        log.info("Subscriber started...");
        subscriberManager = new MBJMeterTestManager();
        JMeterTest subscriberScript = new JMeterTest(new File(this.subscriberPath));
        subscriberManager.runTest(subscriberScript);

        log.info("Publisher started...");
        publisherManager = new MBJMeterTestManager();
        JMeterTest publisherScript = new JMeterTest(new File(this.publisherPath));
        publisherManager.runTest(publisherScript);

        Utils.waitUntilNoChangeInJTLFile(publisherManager);
        Utils.waitUntilNoChangeInJTLFile(subscriberManager);

        Utils.stopAllTests();

        publisherManager.xmlValidator();
        subscriberManager.xmlValidator();

        log.info("Publisher message count : " + Utils.getMessageCount(publisherManager));
        log.info("Subscriber message count : " + Utils.getMessageCount(subscriberManager));
    }
}
