import Utilities.Utils;
import org.apache.commons.configuration.ConfigurationException;
import org.testng.annotations.Test;

import java.io.IOException;

/**
 *
 */
public class OneKBMessagesTestCase extends JMXSetup {

    public OneKBMessagesTestCase() throws ConfigurationException, IOException {
        super.init();
        Utils.editPublisherInputFilePath(super.publisher, getClass().getResource("/sampleMessages/sample_1KB_msg" +
                                                                                 ".xml").getPath());
        Utils.editPublisherDestinationName(super.publisher, "QueueOneKB");
        Utils.editSubscriberDestinationName(super.subscriber, "QueueOneKB");
    }

    @Test()
    public void oneSubscriberTestCase() throws Exception {

        Utils.editThreadCount(super.publisher, "1");
        Utils.editThreadCount(super.subscriber, "1");
        Utils.editRampUpTime(super.publisher, "1");
        Utils.editRampUpTime(super.subscriber, "1");

        super.runTest();
    }

    @Test()
    public void twoSubscriberTestCase() throws Exception {

        Utils.editThreadCount(super.publisher, "2");
        Utils.editThreadCount(super.subscriber, "2");
        Utils.editRampUpTime(super.publisher, "2");
        Utils.editRampUpTime(super.subscriber, "2");

        super.runTest();
    }
}
