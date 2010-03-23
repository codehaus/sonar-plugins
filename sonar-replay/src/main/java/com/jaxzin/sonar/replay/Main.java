/*
 *  Revision Information:
 *  $Id$
 *  $Author$
 *  $DateTime$
 *
 * Copyright ©2010 ESPN.com and Disney Interactive Media Group.  All rights reserved.
 */
package com.jaxzin.sonar.replay;

import com.perforce.p4java.P4JLog;
import com.perforce.p4java.client.P4JClient;
import com.perforce.p4java.client.P4JClientView;
import com.perforce.p4java.core.P4JLabel;
import com.perforce.p4java.core.file.P4JFileSpec;
import com.perforce.p4java.core.file.P4JFileSpecBuilder;
import com.perforce.p4java.exception.*;
import com.perforce.p4java.impl.generic.client.P4JClientSpecImpl;
import com.perforce.p4java.impl.generic.client.P4JClientViewImpl;
import com.perforce.p4java.impl.mapbased.client.P4JClientImpl;
import com.perforce.p4java.server.P4JServer;
import com.perforce.p4java.server.P4JServerFactory;
import com.perforce.p4java.server.callback.P4JCommandCallback;
import com.perforce.p4java.server.callback.P4JLogCallback;
import com.perforce.p4java.server.callback.P4JProgressCallback;
import org.apache.commons.cli.*;
import org.joda.time.DateTime;
import org.joda.time.Interval;
import org.joda.time.ReadableInterval;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.InetAddress;
import java.net.URISyntaxException;
import java.net.UnknownHostException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * TODO: Add documentation for Main
 *
 * @author <a href="mailto:brian@jaxzin.com">Brian R. Jackson</a>
 * @version $Change$
 */
public class Main {

    private static final Logger LOGGER = LoggerFactory.getLogger(Main.class);

    private static final String ALL_USERS = null;
    private static final int MAX_LABEL_COUNT = 200;
    private static final String NO_LABEL_FILTER = null;

    public static void main(String[] args) {
        /*
            Inputs:
            1) Perforce connection info
            2) Sonar db connection info
            3) Perforce Depot
            4) optional: label filter (regex)
            5) optional: time span window (start/end) ? by label name?
            6) optional: version pattern (how to parse # out of label name)

            Logic for version number
            1) If it has a checked in version number in pom.xml use that
            2) Otherwise if version is a ${..} parse

            General logic:
            1) Get all labels (regarding filter)
            2) For each label...
            2.1) Find time of label
            2.2) If time is not in option span, break.
            2.3) p4 sync to that label
            2.4) figure out version number
            2.5) exec 'mvn clean sonar:sonar' passing it
            2.5.1) sonar conn info
            2.5.2) sonar project date
            2.5.3) conditional: label version as whatever property was found ${}
         */

        Properties props = new Properties();

        if (!readPropertiesFile(args, props)) System.exit(1);

        System.getProperties().putAll(props);
        System.getProperties().put("maven.home", System.getenv("M2_HOME"));
        if(!System.getProperties().containsKey("mvn.exe")) {
            System.getProperties().put("mvn.exe", "mvn");
        }

        // Mild-validation
        if(!System.getProperties().containsKey("p4.file.specs")) {
            LOGGER.warn("Not specifying 'p4.file.specs' will default to the entire perforce depot which is probably NOT what you want.");
        }

        try {

            File tempDir = new File(System.getProperty("java.io.tmpdir"));
            File syncDir = new File(new File(tempDir, "sonar-replay"), "workspace");
            syncDir.mkdirs();
            LOGGER.debug("Made temp workspace: " + syncDir.getAbsolutePath());

            P4JLog.setLogCallback(new LogCallback(System.out));


            P4JServer server = P4JServerFactory.getServer(System.getProperty("p4.url"), null);
            LOGGER.debug("Connecting to server...");
            server.connect();
            server.setUserName(System.getProperty("p4.user"));
            server.setAuthTicket(null); // turn off use of tickets.
            server.login(System.getProperty("p4.password"));
            LOGGER.info("Logged into perforce server [" + System.getProperty("p4.user") + " @ " + System.getProperty("p4.url") + "].");

            server.registerCallback(new P4JCommandCallback() {
                public void issuingServerCommand(int i, String s) {
                    LOGGER.debug(i + ": Issuing Server Command : "+ s);
                }

                public void completedServerCommand(int i, long l) {
                    LOGGER.debug(i + ": Completed Server Command : "+ l);
                }

                public void receivedServerInfoLine(int i, String s) {
                    LOGGER.debug("-- "+i+": I* Received Server Info : "+ s);
                }

                public void receivedServerErrorLine(int i, String s) {
                    LOGGER.debug("-- "+i+": E* Received Server Error : "+ s);
                }

                public void receivedServerMessage(int i, int i1, int i2, String s) {
                    LOGGER.debug("-- "+i+": Received Server Message : "+ i1 + " : " + i2 + " : " + s);
                }
            });
            
            server.registerProgressCallback(new DemoProgressCallback());

            List<P4JFileSpec> fileSpecs = parseFileSpecs(System.getProperty("p4.file.specs", "//..."));

//            System.out.println("validSpecs: " + fileSpecs);

            P4JClientSpecImpl spec = new P4JClientSpecImpl();
            spec.setRoot(syncDir.getAbsolutePath());
            final P4JClientViewImpl view = new P4JClientViewImpl();
            final List<P4JClientView.P4JClientViewMapping> mappings = new ArrayList<P4JClientView.P4JClientViewMapping>();
            final String clientName = "sonar-replay-" + InetAddress.getLocalHost().getHostName();
            for (P4JFileSpec fileSpec : fileSpecs) {
                LOGGER.debug("Adding file spec: " + fileSpec.getDisplayPath());
                mappings.add(new P4JClientViewImpl.P4JClientViewMappingImpl(0, fileSpec.getDisplayPath(), "//" + clientName + "/..."));
            }
            view.setMapping(mappings);
            spec.setClientView(view);
            spec.setName(clientName);
            spec.setOwnerName(server.getUserName());
            spec.setHostName("");
            P4JClient clientStub = new P4JClientImpl(server, spec, false);

            LOGGER.debug("Creating client...");
            server.newClient(clientStub);

            P4JClient client = server.getClient(clientStub.getName());
            LOGGER.debug("Client created.");

            server.setCurrentClient(client);


            LOGGER.debug("Clearing workspace...");

            List<P4JFileSpec> cleared = client.sync(
                    P4JFileSpecBuilder.getValidFileSpecs(
                            P4JFileSpecBuilder.makeFileSpecList(
                                    new String[]{"#0"}
                            )
                    )
                    , false, false, false, false);
            LOGGER.debug("Files cleared: " + cleared.size());


            LOGGER.debug("Getting labels.");
            // Get all the labels for the project...
            List<P4JLabel> labels = new ArrayList<P4JLabel>();
            if(System.getProperties().containsKey("p4.label.name.filters")) {
                for (String nameFilter : System.getProperty("p4.label.name.filters").split(",")) {
                    labels.addAll(server.getLabelList(ALL_USERS, MAX_LABEL_COUNT, nameFilter, fileSpecs));
                }
            } else {
                labels.addAll(server.getLabelList(ALL_USERS, MAX_LABEL_COUNT, NO_LABEL_FILTER, fileSpecs));
            }

            Collections.sort(
                    labels,
                    new Comparator<P4JLabel>() {
                        public int compare(P4JLabel o1, P4JLabel o2) {
                            return Long.valueOf(o1.getLastUpdate().getTime()).compareTo(o2.getLastUpdate().getTime());
                        }
                    }
            );

            LOGGER.info("Labels retrieved: " + labels.size());

            DateTimeFormatter fmt = DateTimeFormat.forPattern("yyyy-MM-dd");

            ReadableInterval interval = new Interval(fmt.parseMillis(System.getProperty("p4.interval.start", "1900-01-01")), fmt.parseMillis(System.getProperty("p4.interval.end", "2100-01-01")));

            Pattern labelFilter = Pattern.compile(System.getProperty("p4.label.filter", ".*"));
            for (P4JLabel label : labels) {
                // If the labels last update time is within the filtering time interval...
                final DateTime lastUpdatedOn = new DateTime(label.getLastUpdate().getTime());
                if (interval.contains(lastUpdatedOn)) {
                    final Matcher matcher = labelFilter.matcher(label.getName());
                    // If the label matches the regex
                    if (matcher.matches()) {
                        LOGGER.info("Processing label: " + label.getName());
                        // Parse out the version
                        String version = matcher.group(1);

                        System.getProperties().put("sonar.projectDate", fmt.print(lastUpdatedOn));
                        if (version != null) {
                            System.getProperties().put("build.number", version);
                        }

                        LOGGER.debug("Syncing label.");
                        List<P4JFileSpec> synced = client.sync(
                                P4JFileSpecBuilder.getValidFileSpecs(
                                        P4JFileSpecBuilder.makeFileSpecList(
                                                new String[]{"@" + label.getName()}
                                        )
                                )
                                , false, false, false, false);

                        LOGGER.debug("Files synced: " + synced.size());

                        LOGGER.debug("Starting Maven...");

                        try {
                            ProcessBuilder b =
                                    new ProcessBuilder()
                                            .command(
                                                System.getenv("M2_HOME") + "/bin/" + System.getProperty("mvn.exe"),
//                                                "-X",
                                                "--batch-mode",
                                                "-e",
                                                "-Dbuild.number=" + version,
                                                "-Dsonar.projectDate=" + System.getProperty("sonar.projectDate"),
                                                "-Dsonar.exclusions=" + System.getProperty("sonar.exclusions", ""),
                                                "-s",
                                                System.getProperty("m2.settings", System.getProperty("user.home")+"/.m2/settings.xml"),
                                                "clean",
                                                "install",
                                                "sonar:sonar"
//                                                "org.codehaus.sonar:sonar-maven-plugin:1.12:sonar"
                                            )
                                            .directory(syncDir);
                            Process p = b.start();
//                            Process p = Runtime.getRuntime().exec(
//                                    new String[]{
//                                            System.getenv("M2_HOME") + "/bin/mvn",
//                                            "-Dbuild.number=" + version,
//                                            "-Dsonar.projectDate=" + System.getProperty("sonar.projectDate"),
//                                            "clean",
//                                            "validate"
//                                    }
//                            );

                            new Thread(new StreamRedirector(p.getErrorStream(), true)).start();
                            new Thread(new StreamRedirector(p.getInputStream(), false)).start();
                            p.waitFor();
                        } catch (IOException e) {
                            LOGGER.warn(e.getMessage(), e);
                        } catch (InterruptedException e) {
                            LOGGER.warn(e.getMessage(), e);
                        }

//                        try {
//                        // Now we are synced, fire up maven
//                        Configuration configuration = new DefaultConfiguration()
//                            .setUserSettingsFile( MavenEmbedder.DEFAULT_USER_SETTINGS_FILE )
//                            .setGlobalSettingsFile( MavenEmbedder.DEFAULT_GLOBAL_SETTINGS_FILE )
//                            .setClassLoader( Thread.currentThread().getContextClassLoader() )
//                                .setSystemProperties(System.getProperties())
//                                .setLocalRepository(new File(new File(System.getProperty("user.home"), ".m2"), "repository"))
//                                ;
//
//                        ConfigurationValidationResult validationResult = MavenEmbedder.validateConfiguration( configuration );
//
//                        if ( validationResult.isValid() )
//                        {
//
//                            MavenEmbedder embedder = new MavenEmbedder( configuration );
//
//                            MavenExecutionRequest request = new DefaultMavenExecutionRequest()
//                                .setBaseDirectory( syncDir )
//                                .setGoals( Arrays.asList( new String[]{"help:effective-settings"} ) )
//                                ;
//
//                            MavenExecutionResult result = embedder.execute( request );
//
//                            if ( result.hasExceptions() )
//                            {
//                                for (Exception exception : (List<Exception>) result.getExceptions()) {
//                                    exception.printStackTrace();
//                                }
//                                return;
//                            }
//                        } else {
//                            final Exception settingsException = validationResult.getGlobalSettingsException();
//                            if(settingsException != null) settingsException.printStackTrace();
//                            final Exception exception = validationResult.getUserSettingsException();
//                            if(exception != null) {
//                                exception.printStackTrace();
//                            }
//                            validationResult.display();
//                        }
//                        } catch (MavenEmbedderException e) {
//                            e.printStackTrace();
//                        }
                    }
                }
            }

            LOGGER.debug("Clearing workspace...");

            List<P4JFileSpec> synced = client.sync(
                    P4JFileSpecBuilder.getValidFileSpecs(
                            P4JFileSpecBuilder.makeFileSpecList(
                                    new String[]{"#0"}
                            )
                    )
                    , false, false, false, false);
            LOGGER.debug("Files cleared: " + synced.size());

            server.setCurrentClient(null);

            server.deleteClient(client.getName(), false);

        } catch (URISyntaxException e) {
            LOGGER.warn(e.getMessage(), e);
        } catch (P4JConnectionException e) {
            LOGGER.warn(e.getMessage(), e);
        } catch (P4JNoSuchObjectException e) {
            LOGGER.warn(e.getMessage(), e);
        } catch (P4JConfigException e) {
            LOGGER.warn(e.getMessage(), e);
        } catch (P4JResourceException e) {
            LOGGER.warn(e.getMessage(), e);
        } catch (P4JAccessException e) {
            LOGGER.warn(e.getMessage(), e);
        } catch (P4JRequestException e) {
            LOGGER.warn(e.getMessage(), e);
        } catch (UnknownHostException e) {
            LOGGER.warn(e.getMessage(), e);
        }


//        ReplayConfig config = buildReplayConfig(args);

    }

    private static List<P4JFileSpec> parseFileSpecs(String filesSpecsCSV) {
        LOGGER.debug("Parsing file spec csv: '" + filesSpecsCSV + "'");
        List<P4JFileSpec> rawSpecs =
                P4JFileSpecBuilder.makeFileSpecList(
                        filesSpecsCSV.split(",")
                );
//        System.out.println("rawSpecs: " + rawSpecs);
        return P4JFileSpecBuilder.getValidFileSpecs(rawSpecs);
    }

    private static boolean readPropertiesFile(String[] args, Properties props) {
        Options options = new Options();
        //noinspection AccessStaticViaInstance
        options.addOption(
                OptionBuilder.withArgName("file")
                        .hasArg()
                        .withDescription("The path to the property file.")
                        .create("props")
        );

        if (args.length == 0) {
            // The property file is optional
            return true;
        }

        CommandLineParser parser = new PosixParser();
        try {

            CommandLine cmd = parser.parse(options, args);
            String propFile = cmd.getOptionValue("props");
            props.load(new FileReader(propFile));

        } catch (ParseException e) {
            LOGGER.warn(e.getMessage(), e);
            HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp("sonar-replay", options);
            return false;
        } catch (FileNotFoundException e) {
            LOGGER.warn(e.getMessage(), e);
            HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp("sonar-replay", options);
            return false;
        } catch (IOException e) {
            LOGGER.warn(e.getMessage(), e);
            HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp("sonar-replay", options);
            return false;
        }
        return true;
    }

    private static ReplayConfig buildReplayConfig(String[] args) {
        return null;
    }

    /**
     * A simple demo P4Java progress callback implementation. Real
     * implementations would probably correlate the key arguments
     * and associated output, but this version simply puts whatever
     * it's passed onto standard output with a dash prepended.
     */
    protected static class DemoProgressCallback implements P4JProgressCallback {

        public void start(int key) {
			LOGGER.debug("Starting command " + key);
        }

        public void stop(int key) {
			LOGGER.debug("Stopping command " + key);
        }

        public boolean tick(int key, String tickMarker) {
            if (tickMarker != null) {
                LOGGER.debug(key + " - " + tickMarker);
            }
            return true;
        }
    }

    /**
     * Simple example P4Java ILogCallback implementation.<p>
     * <p/>
     * Note that this example does not attempt to prevent concurrent
     * calls to the various methods here (there's no need in the example
     * app), but real versions might want to implement synchronized access
     * or methods (with suitable attention being paid to deadlock and
     * blocking issues, etc.).
     */

    public static class LogCallback implements P4JLogCallback {

        private P4JLogTraceLevel traceLevel = P4JLogTraceLevel.NONE; // Don't want tracing...
        private PrintStream outStream = null;

        public LogCallback(PrintStream outStream) {
            this.outStream = outStream;
        }

        public void setTraceLevel(P4JLogTraceLevel traceLevel) {
            this.traceLevel = traceLevel;
        }

        public P4JLogTraceLevel getTraceLevel() {
            return this.traceLevel;
        }

        public void internalError(String errorString) {
            if (this.outStream != null) {

            }
        }

        public void internalException(Throwable thr) {
            if (thr != null) {
                printMessage(thr.getLocalizedMessage(), "EXCEPTION");
                thr.printStackTrace(this.outStream);
            }
        }

        public void internalInfo(String infoString) {
            printMessage(infoString, "INFO");
        }

        public void internalStats(String statsString) {
            printMessage(statsString, "STATS");
        }

        public void internalTrace(P4JLogTraceLevel traceLevel, String traceMessage) {
            // Note: tracing does not work for normal P4Java GA releases, so you
            // should not see any calls to this method unless you have a "special"
            // release...

            printMessage(traceMessage, "TRACE");
        }

        public void internalWarn(String warnString) {
            printMessage(warnString, "WARNING");
        }

        private void printMessage(String msg, String pfx) {
            if (msg != null) {
                LOGGER.debug(" (" + pfx + "): " + msg);
                //this.outStream.println(new Date() + " (" + pfx + "): " + msg);
            }
        }
    }

    private static class StreamRedirector implements Runnable {
        private InputStream in;
        private OutputStream out;
        private boolean error;
        private byte[] buffer = new byte[1024];
        public StreamRedirector(InputStream in, boolean error) {
            this.in = in;
            this.error = error;
        }

        public void run() {
            try {
                int read;
                while((read = in.read(buffer)) != -1) {
                    if(error) {
                        LOGGER.error(new String(buffer,0, read));
                    } else {
                        LOGGER.debug(new String(buffer,0, read));
                    }
                    //out.write(buffer, 0 , read);
                }
            } catch (IOException e) {
                LOGGER.warn(e.getMessage(), e);
            }
        }
    }
}
