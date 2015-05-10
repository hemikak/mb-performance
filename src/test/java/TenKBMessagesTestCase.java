import exceptions.MBPerformanceException;
import org.apache.commons.configuration.ConfigurationException;
import org.testng.Assert;
import org.testng.annotations.Test;
import org.xml.sax.SAXException;
import utilities.Constants;
import utilities.Utils;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;

/**
 *
 */
public class TenKBMessagesTestCase extends JMXSetup{

    /**
     * Initializer for the test case.
     *
     * @throws org.apache.commons.configuration.ConfigurationException
     * @throws java.io.IOException
     */
    public TenKBMessagesTestCase() throws ConfigurationException, IOException {
        super.init();
        Utils.editPublisherInputFilePath(super.publisher, getClass().getResource("/sampleMessages/sample_10KB_msg" +
                                                                                 ".xml").getPath());
        Utils.editPublisherDestinationName(super.publisher, "QueueTenKB");
        Utils.editSubscriberDestinationName(super.subscriber, "QueueTenKB");
    }

    /**
     * Runs test case with 1 subscriber and publisher with 1 second ramp up time.
     *
     * @throws ConfigurationException
     * @throws org.xml.sax.SAXException
     * @throws javax.xml.parsers.ParserConfigurationException
     * @throws exceptions.MBPerformanceException
     * @throws IOException
     */
    @Test(groups = {"10kb"}, priority = 1)
    public void oneSubscriberTestCase() throws ConfigurationException, SAXException, ParserConfigurationException,
            MBPerformanceException, IOException {

        Utils.editThreadCount(super.publisher, "1");
        Utils.editThreadCount(super.subscriber, "1");
        Utils.editRampUpTime(super.publisher, "1");
        Utils.editRampUpTime(super.subscriber, "1");

        super.runTest("10KB-1Threads");

        double publisherActualMessageCount = Double.parseDouble(super.publisher.getString(Constants
                .MESSAGE_COUNT_PROPERTY));
        Assert.assertEquals(Utils
                .getMessageCount
                        (super.publisherManager), publisherActualMessageCount, "All messages were not published.");
        Assert.assertEquals(Utils.getMessageCount(super.subscriberManager), publisherActualMessageCount, "All " +
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
    @Test(groups = {"10kb"}, priority = 2)
    public void twoSubscriberTestCase() throws ConfigurationException, SAXException, ParserConfigurationException,
            MBPerformanceException, IOException {

        Utils.editThreadCount(super.publisher, "2");
        Utils.editThreadCount(super.subscriber, "2");
        Utils.editRampUpTime(super.publisher, "1");
        Utils.editRampUpTime(super.subscriber, "1");

        super.runTest("10KB-2Threads");

        double publisherActualMessageCount = Double.parseDouble(super.publisher.getString(Constants
                .MESSAGE_COUNT_PROPERTY)) * 2;
        Assert.assertEquals(Utils
                .getMessageCount
                        (super.publisherManager), publisherActualMessageCount, "All messages were not published.");
        Assert.assertEquals(Utils.getMessageCount(super.subscriberManager), publisherActualMessageCount, "All " +
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
    @Test(groups = {"10kb"}, priority = 3)
    public void fiveSubscriberTestCase() throws ConfigurationException, SAXException, ParserConfigurationException,
            MBPerformanceException, IOException {

        Utils.editThreadCount(super.publisher, "5");
        Utils.editThreadCount(super.subscriber, "5");
        Utils.editRampUpTime(super.publisher, "5");
        Utils.editRampUpTime(super.subscriber, "5");

        super.runTest("10KB-5Threads");

        double publisherActualMessageCount = Double.parseDouble(super.publisher.getString(Constants
                .MESSAGE_COUNT_PROPERTY)) * 5;
        Assert.assertEquals(Utils
                .getMessageCount
                        (super.publisherManager), publisherActualMessageCount, "All messages were not published.");
        Assert.assertEquals(Utils.getMessageCount(super.subscriberManager), publisherActualMessageCount, "All " +
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
    @Test(groups = {"10kb"}, priority = 4)
    public void tenSubscriberTestCase() throws ConfigurationException, SAXException, ParserConfigurationException,
            MBPerformanceException, IOException {

        Utils.editThreadCount(super.publisher, "10");
        Utils.editThreadCount(super.subscriber, "10");
        Utils.editRampUpTime(super.publisher, "5");
        Utils.editRampUpTime(super.subscriber, "5");

        super.runTest("10KB-10Threads");

        double publisherActualMessageCount = Double.parseDouble(super.publisher.getString(Constants
                .MESSAGE_COUNT_PROPERTY)) * 10;
        Assert.assertEquals(Utils
                .getMessageCount
                        (super.publisherManager), publisherActualMessageCount, "All messages were not published.");
        Assert.assertEquals(Utils.getMessageCount(super.subscriberManager), publisherActualMessageCount, "All " +
                                                                                                         "messages " +
                                                                                                         "were not " +
                                                                                                         "received.");
    }
}
