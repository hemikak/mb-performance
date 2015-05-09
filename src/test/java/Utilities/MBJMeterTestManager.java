/*
 * Copyright (c) 2010, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


package utilities;

import exceptions.MBPerformanceException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.jmeter.JMeter;
import org.wso2.automation.tools.jmeter.JMeterInstallationProvider;
import org.wso2.automation.tools.jmeter.JMeterTest;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.Thread.UncaughtExceptionHandler;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

public class MBJMeterTestManager {

    private static final Log log = LogFactory.getLog(MBJMeterTestManager.class);

    private String jmeterLogLevel = "INFO";

    private File testFile = null;
    private File jmeterHome = null;

    private DateFormat fmt = new SimpleDateFormat("yyMMdd-HH-mm-ss");

    private File jmeterProps = null;

    private String reportFileFullPath;

    public void runTest(JMeterTest jMeterTest)
            throws IOException, MBPerformanceException {

        // Init JMeter
        jmeterHome = JMeterInstallationProvider.getInstance().getJMeterHome();

        testFile = jMeterTest.getTestFile();
        //setting jmeter.properties file parameter
        setJMeterPropertyFile(jMeterTest);

        if (jMeterTest.getLogLevel() != null) {
            jmeterLogLevel = jMeterTest.getLogLevel();
        }

        String resultFile = executeMe();
        log.info("for more info. " + resultFile);
    }

    private String executeMe() throws MBPerformanceException, IOException {
        addLogFile(testFile.getName());
        return executeTest(testFile);
    }

    private void setJMeterPropertyFile(JMeterTest jMeterTest) throws IOException {
        if (jMeterTest.getJMeterPropertyFile() == null) {
            log.info("Loading default jmeter.properties...");
            jmeterProps = JMeterInstallationProvider.getInstance().getJMeterPropertyFile();
            System.setProperty("jmeter_properties",
                    File.separator + "bin" + File.separator + "jmeter.properties");
        } else {
            log.info("Loading custom jmeter.properties from " + jMeterTest.getJMeterPropertyFile().getCanonicalPath());
            jmeterProps = jMeterTest.getJMeterPropertyFile();
            System.setProperty("jmeter_properties", jmeterProps.getCanonicalPath());

        }
    }

    private String executeTest(File test) throws MBPerformanceException {
        String reportFileName;
        JMeter jmeterInstance = new JMeter();
        try {
            log.info("Executing test: " + test.getCanonicalPath());
            reportFileName = test.getName().substring(0,
                    test.getName().lastIndexOf(".")) + "-"
                             + fmt.format(new Date()) + ".jmeterResult" + ".jtl";

            File reportDir = JMeterInstallationProvider.getInstance().getReportDir();
            reportFileFullPath = reportDir.toString() + File.separator + reportFileName;
            List<String> argsTmp = Arrays.asList("-n",
                    "-t", test.getCanonicalPath(),
                    "-l", reportDir.toString() + File.separator + reportFileName,
                    "-p", jmeterProps.toString(),
                    "-d", jmeterHome.getCanonicalPath(),
                    "-L", "jorphan=" + jmeterLogLevel,
                    "-L", "jmeter.util=" + jmeterLogLevel);

            List<String> args = new ArrayList<String>();

            args.addAll(argsTmp);

            SecurityManager oldManager = System.getSecurityManager();

            UncaughtExceptionHandler oldHandler = Thread.getDefaultUncaughtExceptionHandler();
            Thread.setDefaultUncaughtExceptionHandler(new UncaughtExceptionHandler() {

                public void uncaughtException(Thread t, Throwable e) {
                    if (e instanceof ExitException && ((ExitException) e).getCode() == 0) {
                        return;    //Ignore
                    }
                    log.error("Error in thread " + t.getName());
                }
            });

            try {
                logParamsAndProps(args);

                jmeterInstance.start(args.toArray(new String[args.size()]));

            } catch (ExitException e) {
                if (e.getCode() != 0) {
                    throw new MBPerformanceException("Test failed", e);
                }
            } catch (Exception e) {
                log.error(e);

            } finally {
                System.setSecurityManager(oldManager);
                Thread.setDefaultUncaughtExceptionHandler(oldHandler);
            }
        } catch (IOException e) {
            throw new MBPerformanceException("Can't execute test", e);
        }
        return reportFileFullPath;
    }

    private void logParamsAndProps(List<String> args) {
        log.debug("Starting JMeter with the following parameters:");
        for (String arg : args) {
            log.debug(arg);
        }
        Properties props = System.getProperties();
        Set<Object> keysUnsorted = props.keySet();
        SortedSet<Object> keys = new TreeSet<Object>(keysUnsorted);
        log.debug("... and the following properties:");
        for (Object k : keys) {
            String key = (String) k;
            String value = props.getProperty(key);
            log.debug(key + " = " + value);
        }
    }

    public void xmlValidator() throws ParserConfigurationException, IOException, SAXException {
        File file = new File(reportFileFullPath);

        if (file.exists()) {
            try {
                DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(new InputSource(new
                        FileReader(file)));
            } catch (SAXException e) {
                log.warn("Root end tag missing in JTL file. Attempting to fix.");
                PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(file, true)));
                out.print("</testResults>");
                out.flush();
                out.close();

                try {
                    DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(new InputSource(new
                            FileReader(file)));
                } catch (SAXException e1) {
                    if(e1.getMessage().equals("Content is not allowed in trailing section.")){
                        log.warn("Trailing section in JTL file found. Attempting to fix.");

                        BufferedReader br = new BufferedReader(new FileReader(file));
                        StringBuilder sb = new StringBuilder();
                        String line = br.readLine();

                        while (line != null) {
                            sb.append(line);
                            sb.append(System.getProperty("line.separator"));
                            line = br.readLine();
                        }
                        String everything = sb.toString();
                        everything = everything.trim();
                        br.close();

                        PrintWriter writer = new PrintWriter(file);
                        writer.print(everything);
                        writer.close();

                        DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(new InputSource(new
                                FileReader(file)));
                        log.info("Fixed error in JTL report file.");

                    }
                }
                log.info("Fixed error in JTL report file.");
            }
        } else {
            throw new FileNotFoundException("Result File is not Created");
        }
    }

    private static class ExitException extends SecurityException {

        private static final long serialVersionUID = 5544099211927987521L;
        public int _rc;

        public ExitException(int rc) {
            super(Integer.toString(rc));
            _rc = rc;
        }

        public int getCode() {
            return _rc;
        }
    }

    private void addLogFile(String fileName) throws IOException {

        File jmeterLogFile = new File(JMeterInstallationProvider.getInstance().getLogDir().getCanonicalPath()
                                      + File.separator + fileName.substring(0, fileName.lastIndexOf(".")) + "-" + fmt
                .format(new Date()) + ".log");
        if (!jmeterLogFile.createNewFile()) {
            log.error("unable to create log file");
        }
        try {
            System.setProperty("log_file", jmeterLogFile.getCanonicalPath());
        } catch (IOException e) {
            throw new IOException("Can't get canonical path for log file", e);
        }

    }

    public String getReportFileName() {
        return reportFileFullPath;
    }
}
