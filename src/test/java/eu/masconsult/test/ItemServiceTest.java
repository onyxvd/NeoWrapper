package eu.masconsult.test;
import eu.masconsult.rest.GraphItemService;
import eu.masconsult.rest.Item;
import eu.masconsult.rest.ItemService;
import org.junit.AfterClass;
import org.junit.Test;

import java.util.Collection;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;
import static org.junit.Assert.assertFalse;

/**
 * User: Mas Consult
 * Date: 15-08-2011
 */
public class ItemServiceTest {

    private static final ItemService itemService = new GraphItemService();

    private static final String TEST_ITEM_NAME = "test Item";

    @Test
    public void testAdd() {
        Item item = new Item(TEST_ITEM_NAME);
        itemService.add(item);
        assertTrue(itemService.isAvailable(item));
    }

    @Test
    public void testRemove() {
        Item item = itemService.find(TEST_ITEM_NAME);
        itemService.remove(item);
        assertFalse(itemService.isAvailable(item));
    }

    @Test
    public void testAdjacent() {
        Item aItem = itemService.createItem("A");
        Item bItem = itemService.createItem("B");
        itemService.add(aItem);
        itemService.add(bItem);
        itemService.connect(aItem, bItem);
        Collection<Item> adjacentList = itemService.findAdjacent(aItem);
        assertTrue(adjacentList.contains(bItem));
        itemService.remove(aItem);
        itemService.remove(bItem);
    }

    public void initGraph() {

        Item aItem = itemService.createItem("A");
        Item bItem = itemService.createItem("B");
        Item cItem = itemService.createItem("C");
        Item dItem = itemService.createItem("D");
        Item eItem = itemService.createItem("E");

        itemService.add(aItem);
        itemService.add(bItem);
        itemService.add(cItem);
        itemService.add(dItem);
        itemService.add(eItem);

        itemService.connect(aItem, bItem);
        itemService.connect(aItem, cItem);
        itemService.connect(dItem, eItem);
    }

    public void tearDownGraph() {
        removeItem("A");
        removeItem("B");
        removeItem("C");
        removeItem("D");
        removeItem("E");
    }

    private void removeItem(String name) {
        itemService.remove(itemService.find(name));
    }

    @Test
    public void testAllConnected() {
        initGraph();
        Item aItem = itemService.find("A");
        Item bItem = itemService.find("B");
        Item dItem = itemService.find("D");

        Collection<Item> connected = itemService.findConnected(aItem);
        assertEquals(3, connected.size());
        assertTrue(connected.contains(bItem));
        assertFalse(connected.contains(dItem));
        tearDownGraph();
    }

    @Test
    public void testLargestItemSet() {
        initGraph();
        assertEquals(3, itemService.largestItemCollection().size());
        tearDownGraph();
    }

    @AfterClass
    public static void removeDb() {
        itemService.finish();
    }
}
