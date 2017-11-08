/**
 * Create Image. 
 * 
 * The createImage() function provides a fresh buffer of pixels to play with.
 * This example creates an image gradient.
 */

//bg
PImage img;

//distance between dots on grid
final int spacing = 64;

//press shift to draw lines
boolean lining = false;

//previously highlighted node
Node prevNode = null;

//2D array of nodes
Node nodes[][];

public class Node {
  private ArrayList<Node> out;
  private ArrayList<Node> in;
  public boolean visited;
  public int centerP;
  public int x;
  public int y;
  public boolean highlighted;

  //constructor
  public Node(int i) {
    this.in = new ArrayList<Node>();
    this.out = new ArrayList<Node>();
    this.visited = false;
    this.centerP = i;
    this.y = centerP / width; //row is y...
    this.x = centerP % width; //column is x...
    this.highlighted = false;
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
    this.y = centerP / width; //row is y...
    this.x = centerP % width; //column is x...
  }

  public void connectTo(Node toAdd) {
    if (this != toAdd) {
      if (!this.getOut().contains(toAdd) && !this.getIn().contains(toAdd)) {
        //sanity check
        if (toAdd.getIn().contains(this) || toAdd.getOut().contains(this)) {
          print("Woops, links aren't properly mirrored. There is a bug in the code.");
        }

        //connect both nodes to eachother (directed)
        this.out.add(toAdd);
        toAdd.addIn(this);
      } else {
        println("redundant edge, ignoring");
      }
    } else {
      println("no self loops plz");
    }
  }
  private void addIn(Node toAdd) {
    if (!this.getIn().contains(toAdd) && !toAdd.getIn().contains(this)) {
      this.in.add(toAdd);
    } else {
      println("redundant edge, ignoring");
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

void setup() {
  size(1024, 768);
  background(0);
  nodes = new Node[height/spacing][width/spacing];

  img = createImage(width, height, RGB);
  for (int i = 0; i < img.pixels.length; i++) {  
    img.pixels[i] = color(0, 0, 0, 255);
  }

  int row;
  int col;

  for (int i = 0; i < img.pixels.length; i++) {
    row = i / width;
    col = i % width; 

    if ((row - (spacing/2)) % spacing == 0) {
      if ((col  - (spacing/2))% spacing == 0) {
        img.pixels[i] = color(0, 153, 204);
        //print((int)row/spacing);
        //print(" ");
        //println((int)col/spacing);
        nodes[(int)row/spacing][(int)col/spacing] = new Node(i);
      }
    }
  }
  //image(img, 0, 0);
  ellipseMode(CENTER);  // Set ellipseMode to CENTER
  fill(0, 153, 204);
  stroke(0, 153, 204);
}

void draw() {
  background(img);
  for (int i = 0; i < nodes.length; i++) {
    for (Node n : nodes[i]) {
      if (n.highlighted) {
        ellipse(n.x, n.y, 10, 10);
      }
      if (!n.getOut().isEmpty()) {
        for (Node d : n.getOut()) {
          line(n.x, n.y, d.x, d.y);
        }
      }
    }
  }
}

void mousePressed() {
  int row = mouseY/spacing;
  int col = mouseX/spacing;
  Node n = nodes[row][col];
  if (lining) {
    if (prevNode != null) {
      //connect nodes
      prevNode.connectTo(n);
    }
    prevNode = n;
  } else if (mouseButton == RIGHT) {
    n.wipe();
  } else {
    //highligh them
    int result = n.highlight();
    if (result == 1) {
      println("row:" + row + " col:" + col);
    } else {
      println("row:" + row + " col:" + col);
    }
  }
}

void keyPressed() {
  
  //switch to line drawing mode
  if (key == CODED && keyCode == SHIFT) {
    if (!lining) {
      lining = true;
    }
  //clear all lines and highlighted nodes
  } else if (key == 'k') {
    for (int i = 0; i < nodes.length; i++) {
      for (Node n : nodes[i]) {
        n.wipe();
        n.highlighted = false;
      }
    }
  }
}
void keyReleased() {
  //Exit line drawing mode
  if (key == CODED && keyCode == SHIFT) {
    if (lining) {
      lining = false;
      prevNode = null;
    }
  }
}