import exceptions.MBPerformanceException;
import org.testng.Assert;
import utilities.Constants;
import utilities.Utils;
import org.apache.commons.configuration.ConfigurationException;
import org.testng.annotations.Test;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;

/**
 * Test class for 5KB messages.
 */
public class FiveKBMessagesTestCase extends JMXSetup {

    /**
     * Initializer for the test case.
     *
     * @throws ConfigurationException
     * @throws IOException
     */
    public FiveKBMessagesTestCase() throws ConfigurationException, IOException {
        super.init();
        Utils.editPublisherInputFilePath(super.publisher, getClass().getResource("/sampleMessages/sample_5KB_msg" +
                                                                                 ".xml").getPath());
        Utils.editPublisherDestinationName(super.publisher, "QueueFiveKB");
        Utils.editSubscriberDestinationName(super.subscriber, "QueueFiveKB");
    }

    /**
     * Runs test case with 1 subscriber and publisher with 1 second ramp up time.
     *
     * @throws ConfigurationException
     * @throws SAXException
     * @throws ParserConfigurationException
     * @throws MBPerformanceException
     * @throws IOException
     */
    @Test(groups = {"5kb"})
    public void oneSubscriberTestCase() throws ConfigurationException, SAXException, ParserConfigurationException,
            MBPerformanceException, IOException {

        Utils.editThreadCount(super.publisher, "1");
        Utils.editThreadCount(super.subscriber, "1");
        Utils.editRampUpTime(super.publisher, "1");
        Utils.editRampUpTime(super.subscriber, "1");

        super.runTest();

        double publisherActualMessageCount = Double.parseDouble(super.publisher.getString(Constants
                .MESSAGE_COUNT_PROPERTY));
        Assert.assertEquals(publisherActualMessageCount, Utils
                .getMessageCount
                        (super.publisherManager), "All messages were not published.");
        Assert.assertEquals(publisherActualMessageCount, Utils.getMessageCount(super.subscriberManager), "All " +
                                                                                                         "messages " +
                                                                                                         "were not " +
                                                                                                         "received.");
    }

    /**
     * Runs test case with 2 subscribers and publishers with 1 second ramp up time.
     *
     * @throws ConfigurationException
     * @throws SAXException
     * @throws ParserConfigurationException
     * @throws MBPerformanceException
     * @throws IOException
     */
    @Test(groups = {"5kb"})
    public void twoSubscriberTestCase() throws ConfigurationException, SAXException, ParserConfigurationException,
            MBPerformanceException, IOException {

        Utils.editThreadCount(super.publisher, "2");
        Utils.editThreadCount(super.subscriber, "2");
        Utils.editRampUpTime(super.publisher, "1");
        Utils.editRampUpTime(super.subscriber, "1");

        super.runTest();

        double publisherActualMessageCount = Double.parseDouble(super.publisher.getString(Constants
                .MESSAGE_COUNT_PROPERTY)) * 2;
        Assert.assertEquals(publisherActualMessageCount, Utils
                .getMessageCount
                        (super.publisherManager), "All messages were not published.");
        Assert.assertEquals(publisherActualMessageCount, Utils.getMessageCount(super.subscriberManager), "All " +
                                                                                                         "messages " +
                                                                                                         "were not " +
                                                                                                         "received.");
    }

    /**
     * Runs test case with 5 subscriber and publisher with 5 second ramp up time.
     *
     * @throws ConfigurationException
     * @throws SAXException
     * @throws ParserConfigurationException
     * @throws MBPerformanceException
     * @throws IOException
     */
    @Test(groups = {"5kb"})
    public void fiveSubscriberTestCase() throws ConfigurationException, SAXException, ParserConfigurationException,
            MBPerformanceException, IOException {

        Utils.editThreadCount(super.publisher, "5");
        Utils.editThreadCount(super.subscriber, "5");
        Utils.editRampUpTime(super.publisher, "5");
        Utils.editRampUpTime(super.subscriber, "5");

        super.runTest();

        double publisherActualMessageCount = Double.parseDouble(super.publisher.getString(Constants
                .MESSAGE_COUNT_PROPERTY)) * 5;
        Assert.assertEquals(publisherActualMessageCount, Utils
                .getMessageCount
                        (super.publisherManager), "All messages were not published.");
        Assert.assertEquals(publisherActualMessageCount, Utils.getMessageCount(super.subscriberManager), "All " +
                                                                                                         "messages " +
                                                                                                         "were not " +
                                                                                                         "received.");
    }

    /**
     * Runs test case with 10 subscriber and publisher with 5 second ramp up time.
     *
     * @throws ConfigurationException
     * @throws SAXException
     * @throws ParserConfigurationException
     * @throws MBPerformanceException
     * @throws IOException
     */
    @Test(groups = {"5kb"})
    public void tenSubscriberTestCase() throws ConfigurationException, SAXException, ParserConfigurationException,
            MBPerformanceException, IOException {

        Utils.editThreadCount(super.publisher, "10");
        Utils.editThreadCount(super.subscriber, "10");
        Utils.editRampUpTime(super.publisher, "5");
        Utils.editRampUpTime(super.subscriber, "5");

        super.runTest();

        double publisherActualMessageCount = Double.parseDouble(super.publisher.getString(Constants
                .MESSAGE_COUNT_PROPERTY)) * 10;
        Assert.assertEquals(publisherActualMessageCount, Utils
                .getMessageCount
                        (super.publisherManager), "All messages were not published.");
        Assert.assertEquals(publisherActualMessageCount, Utils.getMessageCount(super.subscriberManager), "All " +
                                                                                                         "messages " +
                                                                                                         "were not " +
                                                                                                         "received.");
    }
}
