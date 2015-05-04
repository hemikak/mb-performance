import Utilities.MBJMeterTestManager;
import Utilities.Utils;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.XMLConfiguration;
import org.apache.commons.configuration.tree.xpath.XPathExpressionEngine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wso2.automation.tools.jmeter.JMeterTest;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 *
 */
public class JMXSetup {
    private static final Logger log = LoggerFactory.getLogger(JMXSetup.class);

    protected XMLConfiguration publisher;
    protected XMLConfiguration subscriber;
    protected String publisherPath = getClass().getResource("/jmx/JMSPublisher.jmx").getPath();
    protected String subscriberPath = getClass().getResource("/jmx/JMSSubscriber.jmx").getPath();
    private static final String PUBLISHER_PROPERTY_PREFIX = "publisher.";
    private static final String SUBSCRIBER_PROPERTY_PREFIX = "subscriber.";

    public void init() throws ConfigurationException, IOException {
        publisher = new XMLConfiguration(publisherPath);
        publisher.setExpressionEngine(new XPathExpressionEngine());

        subscriber = new XMLConfiguration(subscriberPath);
        subscriber.setExpressionEngine(new XPathExpressionEngine());

        Utils.editPublisherJNDIPath(publisher, getClass().getResource("/jndi.properties").getPath());
        Utils.editSubscriberJNDIPath(subscriber, getClass().getResource("/jndi.properties").getPath());

        InputStream input = new FileInputStream(getClass().getResource("/performance.properties").getPath());
        Properties prop = new Properties();
        prop.load(input);

        for (String propertyKey : prop.stringPropertyNames()) {
            if (propertyKey.startsWith(PUBLISHER_PROPERTY_PREFIX)) {
                Utils.editProperty(publisher, propertyKey.replace(PUBLISHER_PROPERTY_PREFIX, ""), prop.getProperty
                        (propertyKey));
            }
            if (propertyKey.startsWith(SUBSCRIBER_PROPERTY_PREFIX)) {
                Utils.editProperty(subscriber, propertyKey.replace(SUBSCRIBER_PROPERTY_PREFIX, ""), prop.getProperty
                        (propertyKey));
            }
        }
        input.close();
    }

    public void runTest() throws Exception {

        //Utils.printXMLFile(publisher);

        publisher.save();
        subscriber.save();

        final String subscriberPath = this.subscriberPath;
        //        new Thread(new Runnable() {
        //            @Override
        //            public void run() {
        //                try {
        log.info("Subscriber started.");
        MBJMeterTestManager subscriberManager = new MBJMeterTestManager();
        JMeterTest subscriberScript = new JMeterTest(new File(subscriberPath));
        subscriberManager.runTest(subscriberScript);

        //                } catch (Exception e) {
        //                    log.error("Error running test.", e);
        //                }
        //            }
        //        }).start();


        log.info("Publisher started.");
        JMeterTest publisherScript = new JMeterTest(new File(publisherPath));
        MBJMeterTestManager publisherManager = new MBJMeterTestManager();
        publisherManager.runTest(publisherScript);
        log.info("Publisher finished.");

        Utils.waitAndStopIfNoChangeInFile(publisherManager);
        Utils.waitAndStopIfNoChangeInFile(publisherManager);

//        while (!publisherManager.checkForEndOfTest()) {
//            try {
//                Thread.sleep(1000);
//                log.info("Waiting for Publisher to Finish.");
//            } catch (InterruptedException e) {
//                break;
//            }
//        }
//
//
//        // Check it file changes and quit
//        while (!subscriberManager.checkForEndOfTest()) {
//            try {
//                Thread.sleep(1000);
//                log.info("Waiting for Subscriber to Finish.");
//            } catch (InterruptedException e) {
//                break;
//            }
//        }

        MBJMeterTestManager.stopAllTests();
        log.info("Subscriber finished.");

        //        log.info("Waiting started.");
        //        while (subscriberManager.isRunning) {
        //            log.info(Boolean.toString(subscriberManager.isRunning));
        //            Thread.sleep(5000);
        //        }
        //        log.info("Waiting ended.");
    }

    public void stopAllTests() throws IOException {
        MBJMeterTestManager.stopAllTests();
    }
}
