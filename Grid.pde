import java.io.*;
import java.util.*;

public class Grid implements Serializable {
  //2D array of nodes
  public Node nodes[][];

  public int pWidth;
  public int pHeight;

  public int nodeWidth;
  public int nodeHeight;

  public int spacing;

  public PImage bg;

  public Grid(int pHeight, int pWidth, int spacing, PImage bg) {
    this.pHeight = pHeight;
    this.pWidth = pWidth;
    this.spacing = spacing;
    this.bg = bg;
    this.nodeHeight = this.pHeight/this.spacing;
    this.nodeWidth = this.pWidth/this.spacing;
    this.nodes = new Node[nodeHeight][this.nodeWidth];

    //create nodes spaced by this.spacing pixels and draw a dot at their location on bg
    int row;
    int col;
    for (int i = 0; i < this.bg.pixels.length; i++) {

      //get pixel coordinates in matrix form
      row = i / this.pWidth;
      col = i % this.pWidth; 

      if ((row - (this.spacing/2)) % this.spacing == 0) {
        if ((col  - (this.spacing/2))% this.spacing == 0) {
          this.bg.pixels[i] = color(0, 153, 204);
          this.nodes[(int)row/this.spacing][(int)col/this.spacing] = new Node(i, this.pWidth);
        }
      }
    }
  }

  public Node[][] getNodes() {
    return this.nodes;
  }

  public void setNodes(Node[][] nodes) {
    this.nodes = nodes;
  }

  //wipe all nodes
  public void wipe() {
    for (int i = 0; i < this.nodes.length; i++) {
      for (Node n : this.nodes[i]) {
        n.wipe();
        n.highlighted = false;
      }
    }
  }

  //wipe specific node
  public void wipe(int row, int col) {
    nodes[row][col].wipe();
    nodes[row][col].highlighted = false;
  }

  public int highlight (int row, int col) {
    return nodes[row][col].highlight();
  }

  public void updateDrawing() {
    background(this.bg);
    for (int i = 0; i < nodes.length; i++) {
      for (Node n : nodes[i]) {
        if (n.highlighted) {
          ellipse(n.x, n.y, 10, 10);
          //println("found");
          //delay(3000);
        }
        if (!n.getOut().isEmpty()) {
          for (Node d : n.getOut()) {
            line(n.x, n.y, d.x, d.y);
          }
        }
      }
    }
  }

  public void connect(Node n1, Node n2) {
    n1.connectTo(n2);
  }

  public void wipeHighlights() {
    for (int i = 0; i < nodes.length; i++) {
      for (Node n : nodes[i]) {
        n.highlighted = false;
      }
    }
  }
}