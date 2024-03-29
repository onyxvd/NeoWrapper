package eu.masconsult.rest;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * User: Mas Consult Date: 15-08-2011
 */
@XmlRootElement
public class Item {
  private String name;

  public Item() {
  }

  public Item(String name) {
    this.name = name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getName() {
    return name;
  }

  @Override
  public boolean equals(Object other) {
    if (this == other) {
      return true;
    }
    if ((other == null) || (getClass() != other.getClass())) {
      return false;
    }
    Item item = (Item) other;
    if (name != null ? !name.equals(item.name) : item.name != null) {
      return false;
    }
    return true;
  }

  @Override
  public int hashCode() {
    return name != null ? name.hashCode() : 0;
  }
}
