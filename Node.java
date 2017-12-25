import java.io.*;
import java.util.*;

public class Node implements Serializable {
  private ArrayList<Node> out;
  private ArrayList<Node> in;
  
  //pixel coordinates in screen
  public int x;
  public int y;
  
  //node coordinates in grid
  private int row;
  private int col;
  
  private boolean highlighted;

  //constructor
  public Node(int row, int col) {
    this.in = new ArrayList<Node>();
    this.out = new ArrayList<Node>();
    this.highlighted = false;
    this.row = row;
    this.col = col;
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
  
  public int getRow(){
    return this.row;
  }  
  
  public int getCol(){
    return this.col;
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
    
    success = true;
    return success;
  }
  
  public boolean isHighlighted(){
    return this.highlighted;
  }
}