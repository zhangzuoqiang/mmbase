package org.mmbase.streams;

import org.junit.*;
import org.junit.runner.*;
import org.junit.runners.*;
import static org.junit.Assert.*;
import static org.junit.Assume.*;
import java.util.*;
import java.io.*;
import org.mmbase.bridge.*;
import org.mmbase.datatypes.DataType;
import static org.mmbase.datatypes.Constants.*;
import org.mmbase.bridge.mock.*;
import org.mmbase.streams.transcoders.*;
import static org.mmbase.streams.transcoders.AnalyzerUtils.*;
import org.mmbase.util.logging.*;

import org.mmbase.servlet.FileServlet;


/**
 * @author Michiel Meeuwissen
 */

public class CreateCachesTest {

    private static final String REMOTE_URI = "rmi://127.0.0.1:1111/remotecontext";
    private static Cloud remoteCloud;


    private final static MockCloudContext cloudContext = new MockCloudContext();
    private final static String FILE = "basic.mp4";
    private static File  testFile;

    public CreateCachesTest() {
    }

    @BeforeClass
    public static void setUp() throws Exception {
        try {
            CloudContext c =  ContextProvider.getCloudContext(REMOTE_URI);
            remoteCloud = c.getCloud("mmbase", "class", null);
            System.out.println("Found remote cloud " + remoteCloud);
        } catch (Exception e) {
            System.err.println("Cannot get RemoteCloud. (" + e.getMessage() + "). Some tests will be skipped. (but reported as succes: see http://jira.codehaus.org/browse/SUREFIRE-542)");
            System.err.println("You can start up a test-environment for remote tests: trunk/example-webapp$ mvn jetty:run");
            remoteCloud = null;
        }

        //cloudContext.clear();
        /*
        cloudContext.addCore();
        cloudContext.addNodeManagers(DummyBuilderReader.getBuilderLoader().getChildResourceLoader("resources"));
        cloudContext.addNodeManagers(DummyBuilderReader.getBuilderLoader().getChildResourceLoader("media"));
        cloudContext.addNodeManagers(DummyBuilderReader.getBuilderLoader().getChildResourceLoader("streams"));
        */

        /* Mock stuff not yet sufficiently useable
        {
            Map<String, DataType> map = new HashMap<String, DataType>();
            map.put("number",    DATATYPE_INTEGER);
            map.put("url",       DATATYPE_STRING);
            map.put("title",     DATATYPE_STRING);
            map.put("mimetype",  DATATYPE_STRING);
            map.put("id",        DATATYPE_INTEGER);
            map.put("key",       DATATYPE_STRING);
            map.put("format",    DATATYPE_INTEGER);
            map.put("codec",     DATATYPE_INTEGER);
            map.put("state",     DATATYPE_INTEGER);
            map.put("mediafragment",     DATATYPE_NODE);
            map.put("mediaprovider",     DATATYPE_NODE);
            cloudContext.addNodeManager("dummy", map);

            cloudContext.getCloud("mmbase").getNodeManager("dummy").getProperties().put("org.mmbase.streams.cachestype", "dummy");

        }

        {
            Map<String, DataType> map = new HashMap<String, DataType>();
            map.put("number", DATATYPE_INTEGER);
            map.put("title",     DATATYPE_STRING);
            cloudContext.addNodeManager("container", map);
        }

        */
        testFile = new File("samples", FILE);

    }

    protected Cloud getCloud() {
        return remoteCloud;
    }




    @Test
    public void node() {
        Cloud cloud = getCloud();
        assumeNotNull(cloud);
        NodeManager nm = cloud.getNodeManager("streamsources");
        assumeNotNull(nm);
        Node newSource = nm.createNode(); //
        newSource.commit();

    }


    @Test
    public  void test1() throws Exception {
        CreateCachesProcessor proc = new CreateCachesProcessor("dummycreatecaches_1.xml");
        //proc.setCacheManagers("dummy");
        Cloud cloud = getCloud();
        assumeNotNull(cloud);


        Node container = cloud.getNodeManager("mediafragments").createNode();
        container.commit();

        NodeManager nm = cloud.getNodeManager("streamsources");

        System.out.println("DATATYPE " + nm.getField("url").getDataType());

        Node newSource = nm.createNode();
        newSource.setNodeValue("mediafragment", container);
        newSource.setNodeValue("mediaprovider", cloud.getNode("default.provider"));
        newSource.commit();

        assertTrue(testFile.exists());

        System.out.println("DATATYPE " + newSource.getNodeManager().getField("url").getDataType());

        newSource.setStringValue("url", testFile.toURL().toString());



        // FAILS ('getList' not working yet on Dummy))
        CreateCachesProcessor.Job job = proc.createCaches(cloud, newSource.getNumber());
        newSource.commit();

        job.waitUntilReady();

        // 2 nodes should have been created
        //assertEquals("" + cloud.getCloudContext().getNodes(), nodeCount + 2, cloudContext.getNodes().size());




    }
}


