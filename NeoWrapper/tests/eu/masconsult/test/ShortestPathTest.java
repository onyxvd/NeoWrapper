package eu.masconsult.test;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.neo4j.graphalgo.GraphAlgoFactory;
import org.neo4j.graphalgo.PathFinder;
import org.neo4j.graphdb.*;
import org.neo4j.graphdb.index.Index;
import org.neo4j.kernel.EmbeddedGraphDatabase;
import org.neo4j.kernel.Traversal;

import java.io.File;
import java.util.*;

import static junit.framework.Assert.*;

/**
 * User: Mas Consult
 * Date: 15-08-2011
 */
public class ShortestPathTest {

    public static final String DB_PATH = "path-test";
    private static final String NODE_KEY = "NODE_NAME";

    private static RelationshipType KNOWS = DynamicRelationshipType.withName("KNOWS");
    private static GraphDatabaseService graphService;
    private static Index<Node> indexService;

    @BeforeClass
    public static void init() {
        graphService = new EmbeddedGraphDatabase(DB_PATH);
        indexService = graphService.index().forNodes("nodes");
        initGraph();
    }

    @AfterClass
    public static void cleanup() {
        graphService.shutdown();
        deleteDatabase();
    }

    private static void deleteDatabase() {
        doDeleteDatabase(new File(DB_PATH));
    }

    private static void doDeleteDatabase(File dbFile) {
        if (dbFile.exists()) {
            if (dbFile.isDirectory()) {
                for (File child : dbFile.listFiles()) {
                    doDeleteDatabase(child);
                }
            }
            dbFile.delete();
        }
    }

    private Node findNode(String name) {
        return indexService.get(NODE_KEY, name).getSingle();
    }

    private static Node createNode(String name) {
        Node newNode = graphService.createNode();
        newNode.setProperty(NODE_KEY, name);
        indexService.add(newNode, NODE_KEY, name);
        return newNode;
    }

    private static Map<String, Node> createNodes(String... names) {
        Map<String, Node> result = new HashMap<String, Node>();
        for (String name : names)
            result.put(name, createNode(name));
        return result;
    }

    private static void connect(Node parent, Node child) {
        parent.createRelationshipTo(child, KNOWS);
    }

    @Test
    public void testShortestPathLength() {
        Path path = findPath("A", "G");
        assertEquals(3, path.length());
    }

    @Test
    public void testShortestPathCorrectness() {
        Path path = findPath("A", "G");
        Set<Node> nodeSet = new HashSet<Node>();
        for(Node node : path.nodes())
            nodeSet.add(node);
        assertTrue(nodeSet.contains(findNode("D")));
    }

    private Path findPath(String parent, String child) {
        PathFinder<Path> finder = GraphAlgoFactory.shortestPath(Traversal.expanderForTypes(KNOWS, Direction.BOTH), 10);
        Node parentNode = findNode(parent);
        Node childNode = findNode(child);
        return finder.findSinglePath(parentNode, childNode);
    }
    

    private static void initGraph() {
        Transaction transaction = graphService.beginTx();
        Map<String, Node> nodeMap = createNodes("A", "B", "C", "D", "E", "F", "G");
        connect(nodeMap.get("A"), nodeMap.get("B"));
        connect(nodeMap.get("B"), nodeMap.get("C"));
        connect(nodeMap.get("B"), nodeMap.get("D"));
        connect(nodeMap.get("C"), nodeMap.get("F"));
        connect(nodeMap.get("D"), nodeMap.get("E"));
        connect(nodeMap.get("D"), nodeMap.get("F"));
        connect(nodeMap.get("D"), nodeMap.get("G"));
        transaction.success();
        transaction.finish();
    }
}