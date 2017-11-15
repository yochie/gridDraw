import java.io.*;
import java.util.*;

public class Node implements Serializable {
  private ArrayList<Node> out;
  private ArrayList<Node> in;
  public boolean visited;
  public int x;
  public int y;
  public boolean highlighted;

  //constructor
  public Node(int i, int w) {
    this.in = new ArrayList<Node>();
    this.out = new ArrayList<Node>();
    this.visited = false;
    this.highlighted = false;

    this.y = i / w; //row is y...
    this.x = i % w; //column is x...
  }

  //constructor
  public Node() {
    this.in = new ArrayList<Node>();
    this.out = new ArrayList<Node>();
    this.visited = false;
    this.highlighted = false;
  }

  public boolean highlight(boolean val) {
    if (!highlighted && val) {
      highlighted = true;
      return true;
    } else if (highlighted && !val) {
      highlighted = false;
      return true;
    } else {
      return false;
    }
  }

  public void reposition(int x, int y) {
    this.x = x;
    this.y = y;
  }

  public ArrayList<Node> getOut() {
    return this.out;
  }

  public ArrayList<Node> getIn() {
    return this.in;
  }
  
  public void setIn(ArrayList<Node> in) {
    this.in = in;
  }
  
  public void setOut(ArrayList<Node> out) {
    this.out = out;
  }
  
  public boolean wipe() {
    boolean success;
    if(this.out.isEmpty() && this.in.isEmpty()){
      success = false;
      return success;
    }
    for (Node n : this.out) {
      n.getIn().remove(this);
    }
    this.out.clear();
    for (Node n : this.in) {
      n.getOut().remove(this);
    }
    this.in.clear();
    
    //System.out.println("cleared node " + this.x + " " + this.y);
    success = true;
    return success;
  }
}