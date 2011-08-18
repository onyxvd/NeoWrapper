package eu.masconsult.rest;

import java.util.Collection;

/**
 * User: Mas Consult
 * Date: 15-08-2011
 */
public interface ItemService {
    void add(Item item);
    Item find(String name);
    boolean isAvailable(Item item);
    void remove(Item item);
    void finish();
    Collection<Item> findAdjacent(Item item);
    Item createItem(String name);
    void connect(Item parent, Item child);
    Collection<Item> findConnected(Item item);
    Collection<Item> largestItemCollection();
}
