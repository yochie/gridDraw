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

  public Grid(int pHeight, int pWidth, int spacing, PApplet parent) {

    this.parent = parent;
    this.pHeight = pHeight;
    this.pWidth = pWidth;
    this.spacing = spacing;
    this.nodeHeight = this.pHeight/this.spacing;
    this.nodeWidth = this.pWidth/this.spacing;

    this.createBg();

    this.createNodes();
  }

  public Node[][] getNodes() {
    return this.nodes;
  }

  public void setNodes(Node[][] nodes) {
    this.nodes = nodes;
  }

  //wipe all nodes
  public boolean wipe() {
    for (int i = 0; i < this.nodes.length; i++) {
      for (Node n : this.nodes[i]) {
        n.wipe();
        n.highlighted = false;
      }
    }
    return true;
  }

  //wipe specific node
  public boolean wipe(Node n) {
    boolean success = n.wipe();
    boolean success2 = n.highlight(false);
    
    //we consider operation a success if we managed to clear anything
    return (success || success2);
  }

  //switch highlight mode of a specific node
  public boolean highlight (int row, int col, boolean val) {
    boolean success = nodes[row][col].highlight(val);
    System.out.println("row:" + row + " col:" + col + " " + val);

    return success;
  }

  //clear highlights
  public boolean wipeHighlights() {
    boolean success = false;
    for (int i = 0; i < nodes.length; i++) {
      for (Node n : nodes[i]) {
        if (n.highlighted) {
          success = true;
          n.highlight(false);
        }
      }
    }
    return success;
  }


  //draw node connections and highlights
  public void updateDrawing() {
    for (int i = 0; i < this.nodes.length; i++) {
      for (Node n : this.nodes[i]) {
        if (n.highlighted) {
          parent.ellipse(n.x, n.y, this.spacing/2, this.spacing/2);
        }
        if (!n.getOut().isEmpty()) {
          for (Node d : n.getOut()) {
            parent.line(n.x, n.y, d.x, d.y);
          }
        }
      }
    }
  }

  //connect nodes
  public boolean connect(Node from, Node to) {
    boolean success;
    System.out.println("Connecting : " + from + " to " + to);
    if (from != to) {
      if (!from.getOut().contains(to) && !from.getIn().contains(to)) {
        //sanity check
        if (to.getIn().contains(from) || to.getOut().contains(from)) {
          System.out.println("Woops, links aren't properly mirrored. Please report bug...");
          success = false;
          return success;
        }
        //connect both nodes to eachother (directed)
        from.getOut().add(to);
        to.getIn().add(from);
        success = true;
        return success;
      } else {
        System.out.println("Redundant edge, ignoring");
        success = false;
        return success;
      }
    } else {
      System.out.println("Self loop, ignoring.");
      success = false;
      return success;
    }
  }

  //disconnect nodes (only if right edge direction)
  public boolean disconnect(Node from, Node to) {
    boolean success;

    if (from != to) {
      if (from.getOut().contains(to) && to.getIn().contains(from)) {

        from.getOut().remove(to);
        to.getIn().remove(from);
        success = true;
        return success;
      } else {
        //sanity check
        if (from.getOut().contains(to) || to.getIn().contains(from)) {
          System.out.println("Woops, links aren't properly mirrored. There is a bug in the code.");
        } else {
          System.out.println("Edge does not exist.");
        }
        success = false;
        return success;
      }
    } else {
      System.out.println("Cannot disconnect node from itself.");
      success = false;
      return success;
    }
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

  public void attach(PApplet parent) {
    this.parent = parent;
    this.createBg();
  }

  public boolean grow() {

    int newSpacing;
    //if already at max spacing, skip
    if (this.spacing >= 128) {
      parent.println("Maximum size reached.");
      return false;
    }
    //otherwise augment spacing to a max of 128
    else if (this.spacing <= 124)
      newSpacing = this.spacing + 4 ;
    else {
      newSpacing = 128;
    }
    //if current drawing doesn't fit in requested size
    int occupied[] = this.occupiedRange();
    if (occupied[0] > this.pHeight/newSpacing - 1 || occupied[1] > this.pWidth/newSpacing - 1) {
      parent.println("Can't resize page, missing place for current drawing.");
      return false;
    }
    this.spacing = newSpacing;

    this.nodeHeight = this.pHeight/this.spacing;
    this.nodeWidth = this.pWidth/this.spacing;
    parent.println("new grid size: rows: " + this.nodeHeight + " cols: " + this.nodeWidth);

    this.createBg();
    this.createNodes(this.nodes);
    return true;
  }

  public boolean shrink() {    
    //if already at min spacing, skip
    if (this.spacing <= 4) { 
      parent.println("Minimum size reached.");
      return false;
    }

    //otherwise reduce spacing to a min of 4
    else if (this.spacing >= 8)
      this.spacing = this.spacing - 4;
    else {
      this.spacing = 4;
    }
    this.nodeHeight = this.pHeight/this.spacing;
    this.nodeWidth = this.pWidth/this.spacing;
    parent.println("new grid size: rows: " + this.nodeHeight + " cols: " + this.nodeWidth);

    this.createBg();

    this.createNodes(this.nodes);
    return true;
  }

  private int[] occupiedRange() {
    int maxI = 0;
    int maxJ = 0;
  outerloop:
    for (int i = this.nodeHeight-1; i > 0; i--) {
      for (Node n : nodes[i]) {
        if (!n.getOut().isEmpty() || !n.getIn().isEmpty() || n.highlighted) {
          maxI = i;
          break outerloop;
        }
      }
    }

  outerloop2:

    for (int j = this.nodeWidth-1; j > 0; j--) {
      for (int i = 0; i < this.nodeHeight; i++) {
        Node n = nodes[i][j];
        if (!n.getOut().isEmpty() || !n.getIn().isEmpty() || n.highlighted) {
          maxJ = j;
          break outerloop2;
        }
      }
    }

    int[] toreturn = {maxI, maxJ};
    return toreturn;
  }

  private void createNodes() {
    this.nodes = new Node[this.nodeHeight][this.nodeWidth];

    //fill nodes array with fresh nodes
    int row;
    int col;
    for (int i = 0; i < this.bg.pixels.length; i++) {
      //get pixel coordinates in matrix form
      row = i / this.pWidth;
      col = i % this.pWidth; 
      if ((row - (this.spacing/2)) % this.spacing == 0) {
        if ((col  - (this.spacing/2))% this.spacing == 0) {
          this.nodes[row / this.spacing][col / this.spacing] = new Node(i, this.pWidth);
        }
      }
    }
  }

  private void createNodes(Node[][] old) {

    Node[][] newNodes = new Node[this.nodeHeight][this.nodeWidth];

    //fill nodes array with fresh nodes
    for (int i = 0; i < this.nodeHeight; i++) {
      for (int j = 0; j < this.nodeWidth; j++) {
        if (i < old.length && j < old[0].length) {
          newNodes[i][j] = old[i][j];
          newNodes[i][j].reposition(j * spacing + spacing/2, i * spacing + spacing/2);
        } else {
          newNodes[i][j] = new Node();
          newNodes[i][j].reposition(j * spacing + spacing/2, i * spacing + spacing/2);
        }
      }
    }
    this.nodes = newNodes;
  }

  private void createBg() {
    PImage img = parent.createImage(this.pWidth, this.pHeight, parent.RGB);

    //fill img with black
    for (int i = 0; i < img.pixels.length; i++) {  
      img.pixels[i] = parent.color(0, 0, 0);
    }

    //create nodes spaced by this.spacing pixels and draw a dot at their location on bg
    int row;
    int col;

    for (int i = 0; i < img.pixels.length; i++) {
      //get pixel coordinates in matrix form
      row = i / this.pWidth;
      col = i % this.pWidth; 

      if ((row - (this.spacing/2)) % this.spacing == 0 ) {
        if ((col  - (this.spacing/2))% this.spacing == 0) {
          if (row < this.nodeHeight * this.spacing && col < this.nodeWidth * this.spacing)
            img.pixels[i] = parent.color(0, 153, 204);
        }
      }
    }
    this.bg = img;
  }
}