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

  //switch highlight mode of a specific node
  public int highlight (int row, int col) {
    return nodes[row][col].highlight();
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
  public void connect(Node n1, Node n2) {
    n1.connectTo(n2);
  }

  //clear highlights
  public void wipeHighlights() {
    for (int i = 0; i < nodes.length; i++) {
      for (Node n : nodes[i]) {
        n.highlighted = false;
      }
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

  public void attach(PApplet parent, PImage bg) {
    this.parent = parent;
    this.createBg();
  }

  public void grow() {

    int newSpacing;
    //if already at max spacing, skip
    if (this.spacing >= 128) { 
      return;
    }
    //otherwise augment spacing to a max of 128
    else if (this.spacing <= 124)
      newSpacing = this.spacing + 4 ;
    else {
      newSpacing = 128;
    }
    //if current drawing doesn't fit in requested size
    int occupied[] = this.occupiedRange();
    parent.println(occupied);
    if (occupied[0] > this.pHeight/newSpacing || occupied[1] > this.pWidth/newSpacing) {
      parent.println("Can't resize page, missing place for current drawing.");
      return;
    }
    this.spacing = newSpacing;

    this.nodeHeight = this.pHeight/this.spacing;
    this.nodeWidth = this.pWidth/this.spacing;

    this.createBg();
    this.createNodes(this.nodes);
  }

  public void shrink() {    
    //if already at min spacing, skip
    if (this.spacing <= 4) { 
      return;
    }

    //otherwise reduce spacing to a min of 4
    else if (this.spacing >= 8)
      this.spacing = this.spacing - 4;
    else {
      this.spacing = 4;
    }
    this.nodeHeight = this.pHeight/this.spacing;
    this.nodeWidth = this.pWidth/this.spacing;

    this.createBg();

    this.createNodes(this.nodes);
  }

  public int[] occupiedRange() {
    int maxI = 0;
    int maxJ = 0;
  outerloop:
    for (int i = this.nodeHeight-1; i >= 0; i--) {
      for (Node n : nodes[i]) {
        if (!n.getOut().isEmpty() || !n.getIn().isEmpty() || n.highlighted) {
          maxI = i;
          break outerloop;
        }
      }
    }

  outerloop2:

    for (int j = this.nodeWidth-1; j >= 0; j--) {
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

  void createNodes() {
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

  void createNodes(Node[][] old) {

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

  public void createBg() {
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

      if ((row - (this.spacing/2)) % this.spacing == 0) {
        if ((col  - (this.spacing/2))% this.spacing == 0) {
          img.pixels[i] = parent.color(0, 153, 204);
        }
      }
    }
    this.bg = img;
  }
}