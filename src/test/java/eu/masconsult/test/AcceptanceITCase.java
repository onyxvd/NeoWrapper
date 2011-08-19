package eu.masconsult.test;

import junit.framework.Assert;

import org.codehaus.jackson.jaxrs.JacksonJsonProvider;
import org.junit.Before;
import org.junit.Test;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;

import eu.masconsult.rest.Item;

public class AcceptanceITCase {

  private Client      client;
  private WebResource resource;

  @Before
  public void setUp() {
    ClientConfig cc = new DefaultClientConfig();
    cc.getClasses().add(JacksonJsonProvider.class);
    client = Client.create(cc);
    String port = System.getProperty("jetty.port");
    if (port == null) {
      port = "8080";
    }
    resource = client.resource("http://localhost:" + port + "/item");
  }

  @Test
  public void testFind() {
    Assert.assertEquals(new Item("alabala"), resource.path("alabala").get(Item.class));
  }

}
