package eu.masconsult.rest;

import org.neo4j.graphdb.*;
import org.neo4j.graphdb.index.Index;
import org.neo4j.kernel.EmbeddedGraphDatabase;

import java.io.File;
import java.util.*;

/**
 * User: Mas Consult
 * Date: 15-08-2011
 */
public class GraphItemService implements ItemService {

    public static final String DB_PATH = "itemdb";
    public static final String ITEM_NODES = "items";
    public static final String NODE_KEY = "name";
    private static final String ITEM_NAME = "item";

    private static RelationshipType SIMILAR = DynamicRelationshipType.withName("SIMILAR");

    private final GraphDatabaseService graphService;
    private final Index<Node> index;

    public GraphItemService() {
        graphService = new EmbeddedGraphDatabase(DB_PATH);
        index = graphService.index().forNodes(ITEM_NODES);
    }

    @Override
    public void add(Item item) {
        Transaction transaction = graphService.beginTx();
        Node node = graphService.createNode();
        node.setProperty(NODE_KEY, item.getName());
        node.setProperty(ITEM_NAME, item.getName());
        index.add(node, NODE_KEY, item.getName());
        transaction.success();
        transaction.finish();
    }

    @Override
    public Item find(String name) {
        Node node = findNode(name);
        if(node == null)
            return null;
        return new Item((String ) node.getProperty(ITEM_NAME));
    }

    private Node findNode(String name) {
        return index.get(NODE_KEY, name).getSingle();
    }


    @Override
    public boolean isAvailable(Item item) {
        return find(item.getName()) != null;
    }

    @Override
    public void remove(Item item) {
        Transaction transaction = graphService.beginTx();
        index.remove(findNode(item.getName()), NODE_KEY, item.getName());
        transaction.success();
        transaction.finish();
    }

    @Override
    public void finish() {
        doDeleteDatabase(new File(DB_PATH));
    }

    @Override
    public Collection<Item> findAdjacent(Item item) {
        List<Item> result = new ArrayList<Item>();
        for (Node node : findAdjacent(findNode(item.getName()))) {
            result.add(find((String) node.getProperty(ITEM_NAME)));
        }
        return result;
    }

    private Set<Node> findAdjacent(Node node) {
        Set<Node> items = new HashSet<Node>();
        for (Relationship relationship : node.getRelationships())
            items.add(relationship.getEndNode());
        return items;
    }

    @Override
    public Item createItem(String name) {
        return new Item(name);
    }

    @Override
    public void connect(Item parent, Item child) {
        Transaction transaction = graphService.beginTx();
        Node parentNode = findNode(parent.getName());
        Node childNode = findNode(child.getName());
        parentNode.createRelationshipTo(childNode, SIMILAR);
        transaction.success();
        transaction.finish();
    }

    @Override
    public Collection<Item> findConnected(Item item) {
        Node curNode = findNode(item.getName());
        Set<Node> connectedNodes = findConnected(curNode, new HashSet<Node>());
        List<Item> result = new ArrayList<Item>();
        for (Node node : connectedNodes) {
            result.add(find((String) node.getProperty(ITEM_NAME)));
        }       
        return result;
    }

    @Override
    public Collection<Item> largestItemCollection() {

        Set<Node> used = new HashSet<Node>();
        List<Node> largestComponent = new ArrayList<Node>();
        for(Node node : graphService.getAllNodes()) {
            if(used.contains(node)) continue;
            Traverser traverser = node.traverse(Traverser.Order.DEPTH_FIRST, StopEvaluator.END_OF_GRAPH, ReturnableEvaluator.ALL, SIMILAR, Direction.BOTH);
            Collection<Node> curComponent = traverser.getAllNodes();
            if(curComponent.size() > largestComponent.size()) {
                largestComponent.clear();
                largestComponent.addAll(curComponent);
            }
            used.addAll(curComponent);
        }

        List<Item> componentList = new ArrayList<Item>();
        for(Node node : largestComponent)
            componentList.add(find((String) node.getProperty(ITEM_NAME)));
        return componentList;

//        Set<Node> used = new HashSet<Node>();
//        List<Set<Node>> components = new ArrayList<Set<Node>>();
//        for (Node node : graphService.getAllNodes())
//            if(!used.contains(node))
//                components.add(findConnected(node, new HashSet<Node>()));
//
//        int maxSize = 0;
//        int maxIndex = 0;
//        for(int i = 0; i < components.size(); i++) {
//            Set<Node> component = components.get(i);
//            if(component.size() > maxSize) {
//                maxSize = component.size();
//                maxIndex = i;
//            }
//        }
//        List<Item> componentList = new ArrayList<Item>();
//        for(Node node : components.get(maxIndex))
//            componentList.add(find((String) node.getProperty(ITEM_NAME)));
//        return componentList;
    }

    private Set<Node> findConnected(Node parent, Set<Node> result) {
        result.add(parent);
        for(Node node : findAdjacent(parent))
            if(!result.contains(node))
               result.addAll(findConnected(node, result));
        return result;
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
}
