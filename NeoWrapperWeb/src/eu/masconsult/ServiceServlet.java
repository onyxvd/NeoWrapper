package eu.masconsult;

import java.util.Collection;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import eu.masconsult.rest.Item;
import eu.masconsult.rest.ItemService;

@Path("/item")
@Produces(MediaType.APPLICATION_JSON)
public class ServiceServlet implements ItemService {

	public void add(Item item) {
		// TODO Auto-generated method stub
		
	}

	@GET
	@Path("{name}")
	public Item find(@PathParam("name") String name) {
		return new Item(name);
	}

	public boolean isAvailable(Item item) {
		// TODO Auto-generated method stub
		return false;
	}

	public void remove(Item item) {
		// TODO Auto-generated method stub
		
	}

	public Collection<Item> findAdjacent(Item item) {
		// TODO Auto-generated method stub
		return null;
	}

	public Item createItem(String name) {
		// TODO Auto-generated method stub
		return null;
	}

	public void connect(Item parent, Item child) {
		// TODO Auto-generated method stub
		
	}

	public Collection<Item> findConnected(Item item) {
		// TODO Auto-generated method stub
		return null;
	}

	public Collection<Item> largestItemCollection() {
		// TODO Auto-generated method stub
		return null;
	}

	public void finish() {
		// TODO Auto-generated method stub
		
	}

}
