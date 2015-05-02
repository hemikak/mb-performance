import Utilities.MBJMXPropertyEditor;
import Utilities.MBJMeterTestManager;
import org.apache.jmeter.engine.StandardJMeterEngine;
import org.apache.jmeter.save.SaveService;
import org.apache.jmeter.util.JMeterUtils;
import org.apache.jorphan.collections.HashTree;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.Test;
import org.wso2.automation.tools.jmeter.JMeterTest;
import org.wso2.automation.tools.jmeter.JMeterTestManager;

import java.io.File;
import java.io.FileInputStream;
import java.net.URISyntaxException;

/**
 *
 */
public class OneKBMessagesTestCase {
    private static final Logger log = LoggerFactory.getLogger(OneKBMessagesTestCase.class);
    @Test()
    public void listServices() throws Exception {

//        // JMeter Engine
//        StandardJMeterEngine jmeter = new StandardJMeterEngine();
//
//        log.info(OneKBMessagesTestCase.class.getClassLoader().getResource("jmeter.properties").getPath());
//
//        // Initialize Properties, logging, locale, etc.
//        JMeterUtils.loadJMeterProperties("/Users/hemikakodikara/mb/workspace/mb-performance/src/test/resources/jmeter" +
//                                         ".properties");
//        JMeterUtils.setJMeterHome("/Users/hemikakodikara/mb/clients/mb-jmeter");
//        JMeterUtils.initLogging();// you can comment this line out to see extra log messages of i.e. DEBUG level
//        JMeterUtils.initLocale();
//
//        // Initialize JMeter SaveService
//        SaveService.loadProperties();
//
//        // Load existing .jmx Test Plan
//        FileInputStream in = new FileInputStream("/Users/hemikakodikara/mb/workspace/mb-performance/src/test/resources/one-kb-test1/JMSPublisher.jmx");
//        log.info("FIS : " +  Boolean.toString(in == null));
//        HashTree testPlanTree = SaveService.loadTree(in);
//        in.close();
//
//        System.out.println(testPlanTree);
//
//        // Run JMeter Test
//        jmeter.configure(testPlanTree);
//        jmeter.run();


        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    JMeterTest subscriberScript = new JMeterTest(new File(getClass().getResource
                            ("/one-kb-test1/JMSSubscriber" +
                             ".jmx").toURI()));
                    MBJMeterTestManager subscriberManager = new MBJMeterTestManager();
                    subscriberManager.runTest(subscriberScript);
                } catch (URISyntaxException e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();

        Thread.sleep(5000);

        MBJMXPropertyEditor.editPublisherMessageCount(getClass().getResource("/one-kb-test1/JMSPublisher.jmx").getPath(), "100000");

        JMeterTest publisherScript = new JMeterTest(new File(getClass().getResource("/one-kb-test1/JMSPublisher.jmx")
                .toURI()));
        MBJMeterTestManager publisherManager = new MBJMeterTestManager();
        publisherManager.runTest(publisherScript);
    }
}
