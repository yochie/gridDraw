import javax.swing.*;
import javax.swing.filechooser.*;
import java.io.*;
import java.io.*;
import java.util.*;
import processing.core.*;

public class Grid implements Serializable {
  //2D array of nodes
  public Node nodes[][];

  public int pWidth;
  public int pHeight;

  public int nodeWidth;
  public int nodeHeight;

  public int spacing;

  public transient PImage bg;

  private transient PApplet parent;

  public Grid(int pHeight, int pWidth, int spacing, PImage bg, PApplet parent) {

    this.parent = parent;
    this.pHeight = pHeight;
    this.pWidth = pWidth;
    this.spacing = spacing;
    this.bg = bg;
    this.nodeHeight = this.pHeight/this.spacing;
    this.nodeWidth = this.pWidth/this.spacing;
    this.nodes = new Node[this.nodeHeight][this.nodeWidth];

    //create nodes spaced by this.spacing pixels and draw a dot at their location on bg
    int row;
    int col;
    for (int i = 0; i < this.bg.pixels.length; i++) {

      //get pixel coordinates in matrix form
      row = i / this.pWidth;
      col = i % this.pWidth; 

      if ((row - (this.spacing/2)) % this.spacing == 0) {
        if ((col  - (this.spacing/2))% this.spacing == 0) {
          this.bg.pixels[i] = parent.color(0, 153, 204);
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
    this.nodes[row][col].wipe();
    this.nodes[row][col].highlighted = false;
  }

  public int highlight (int row, int col) {
    return nodes[row][col].highlight();
  }

  public void updateDrawing() {
    for (int i = 0; i < this.nodes.length; i++) {
      for (Node n : this.nodes[i]) {
        if (n.highlighted) {
          parent.ellipse(n.x, n.y, 10, 10);
        }
        if (!n.getOut().isEmpty()) {
          for (Node d : n.getOut()) {
            parent.line(n.x, n.y, d.x, d.y);
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

  public void recomputePositions() {
  }

  public void saveToFile() {
    try { 
      File saveDir = new File("saves");

      // if the directory does not exist, create it
      if (!saveDir.exists()) {
        parent.println("creating directory: " + saveDir.getName());
        boolean result = false;
        try {
          saveDir.mkdir();
          result = true;
        } 
        catch(SecurityException se) {
          //handle it
          se.toString();
        }        
        if (result) {    
          parent.println("DIR created");
        }
      }

      JFrame frame = new JFrame("FileChooser");
      JFileChooser chooser = new JFileChooser(saveDir.getName());
      chooser.setSelectedFile(new File("drawing.save"));

      FileNameExtensionFilter filter = new FileNameExtensionFilter("Saves", "save");
      chooser.setFileFilter(filter);

      int returnVal = chooser.showSaveDialog(frame);
      String fname;
      if (returnVal == JFileChooser.APPROVE_OPTION) {
        File f = chooser.getSelectedFile();
        fname = f.getPath();
        parent.println("Saving to this file: " + fname);
        if (fname != null) {
          FileOutputStream fos = new FileOutputStream(fname);
          ObjectOutputStream oos = new ObjectOutputStream(fos);

          oos.writeObject(this);
          oos.close();
          fos.close();
        }
      }
    }
    catch (Exception ex)
    {
      parent.println("Exception thrown during test: " + ex.toString());
    }
  }

  public void attach(PApplet parent, PImage bg) {
    this.parent = parent;

    //create nodes spaced by this.spacing pixels and draw a dot at their location on bg
    int row;
    int col;
    this.bg = bg;

    for (int i = 0; i < this.bg.pixels.length; i++) {

      //get pixel coordinates in matrix form
      row = i / this.pWidth;
      col = i % this.pWidth; 

      if ((row - (this.spacing/2)) % this.spacing == 0) {
        if ((col  - (this.spacing/2))% this.spacing == 0) {
          this.bg.pixels[i] = parent.color(0, 153, 204);
        }
      }
    }
  }
}