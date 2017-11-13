import java.io.*;
import java.util.*;

public class Node implements Serializable {
  private ArrayList<Node> out;
  private ArrayList<Node> in;
  public boolean visited;
  public int centerP;
  public int x;
  public int y;
  public boolean highlighted;
  private int gridWidth;

  //constructor
  public Node(int i, int w) {
    this.in = new ArrayList<Node>();
    this.out = new ArrayList<Node>();
    this.visited = false;
    this.highlighted = false;
    
    this.centerP = i;
    this.gridWidth = w;
    this.y = this.centerP / gridWidth; //row is y...
    this.x = this.centerP % gridWidth; //column is x...
    
    System.out.println(i + " " + x + " " + y);
    
  }

  public int highlight() {
    if (highlighted) {
      highlighted = false;
      return 0;
    } else {
      highlighted = true;
      return 1;
    }
  }

  public void reposition(int newCenter) {
    this.centerP = newCenter;
    this.y = centerP / this.gridWidth; //row is y...
    this.x = centerP % this.gridWidth; //column is x...
  }

  public void connectTo(Node toAdd) {
    if (this != toAdd) {
      if (!this.getOut().contains(toAdd) && !this.getIn().contains(toAdd)) {
        //sanity check
        if (toAdd.getIn().contains(this) || toAdd.getOut().contains(this)) {
          System.out.println("Woops, links aren't properly mirrored. There is a bug in the code.");
        }

        //connect both nodes to eachother (directed)
        this.out.add(toAdd);
        toAdd.addIn(this);
      } else {
        System.out.println("redundant edge, ignoring");
      }
    } else {
      System.out.println("no self loops plz");
    }
  }
  private void addIn(Node toAdd) {
    if (!this.getIn().contains(toAdd) && !toAdd.getIn().contains(this)) {
      this.in.add(toAdd);
    } else {
      System.out.println("redundant edge, ignoring");
    }
  }

  public ArrayList<Node> getOut() {
    return this.out;
  }

  public ArrayList<Node> getIn() {
    return this.in;
  }

  public void wipe() {
    for (Node n : this.out) {
      n.getIn().remove(this);
    }
    this.out.clear();
    for (Node n : this.in) {
      n.getOut().remove(this);
    }
    this.in.clear();
  }
}