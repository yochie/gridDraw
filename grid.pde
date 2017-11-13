import javax.swing.*;

import javax.swing.filechooser.*;

import java.io.*;


//bg
PImage img;

//distance between dots on grid
final int spacing = 16;

//press shift to draw lines
boolean lining = false;

//variable used to confirm drawing clearing using "k" key
boolean confirming = false;

//Used to stop drawing while waiting for event
boolean pause = false;
String waitingFor = "";

//previously highlighted node
Node prevNode = null;

//2D array of nodes
Node nodes[][];

void setup() {
  size(1800, 900);
  background(0);
  ellipseMode(CENTER);  // Set ellipseMode to CENTER
  fill(0, 153, 204);
  stroke(0, 153, 204);
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
        nodes[(int)row/spacing][(int)col/spacing] = new Node(i, width);
      }
    }
  }
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
  if (!pause) {
    int row = constrain(mouseY, 0, spacing*(nodes.length)-1)/spacing;
    int col = constrain(mouseX, 0, spacing*(nodes[0].length)-1)/spacing;
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
  } else {
    println("Paused : " + waitingFor);
  }
}

void keyPressed() {

  //switch to line drawing mode
  if (key == CODED) {
    if (keyCode == SHIFT) {
      if (!lining) {
        lining = true;
      }
      //reset all global mode variables to their defaults
    }
  } else if (key == ESC) {
    key = 0;
    if (confirming || lining) {
      println("Going back to default mode");    
      confirming = false;
      pause = false;
      waitingFor = "";
      lining = false;
    }

    //clear all lines and highlighted nodes
  } else if (key == 'k') {
    if (confirming) {
      for (int i = 0; i < nodes.length; i++) {
        for (Node n : nodes[i]) {
          n.wipe();
          n.highlighted = false;
        }
      }
      confirming = false;
      pause = false;
      waitingFor = "";
      println("Deleted.");
    } else { 
      println("Delete everything? Press k again for yes, or press ESC for no.");
      pause = true;
      waitingFor = "Deletion confirmation.";
      confirming = true;
    }
    //clear all highlights
  } else if (key == 'm') {
    for (int i = 0; i < nodes.length; i++) {
      for (Node n : nodes[i]) {
        n.highlighted = false;
      }
    }

    //Save nodes to serialized file
  } else if (key == 's') {
    try { 
      File saveDir = new File("saves");

      // if the directory does not exist, create it
      if (!saveDir.exists()) {
        System.out.println("creating directory: " + saveDir.getName());
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
          System.out.println("DIR created");
        }
      }


      JFrame frame = new JFrame("FileChooser");

      JFileChooser chooser = new JFileChooser(saveDir.getName());

      chooser.setSelectedFile(new File("drawing.save"));
      FileNameExtensionFilter filter = new FileNameExtensionFilter(
        "Saves", "save");
      chooser.setFileFilter(filter);
      int returnVal = chooser.showSaveDialog(frame);
      String fname;
      if (returnVal == JFileChooser.APPROVE_OPTION) {
        File f = chooser.getSelectedFile();
        fname = f.getPath();
        println("Saving to this file: " + fname);
        if (fname != null) {
          FileOutputStream fos = new FileOutputStream(fname);
          ObjectOutputStream oos = new ObjectOutputStream(fos);

          oos.writeObject(nodes);
          oos.close();
          fos.close();
        }
      }
    }
    catch (Exception ex)
    {
      print("Exception thrown during test: " + ex.toString());
    }
  } else if (key == 'o') {
    try {
      File saveDir = new File("saves");

      JFrame frame = new JFrame("FileChooser");
      JFileChooser chooser = new JFileChooser(saveDir.getName());
      FileNameExtensionFilter filter = new FileNameExtensionFilter(
        "Saves", "save");
      chooser.setFileFilter(filter);
      int returnVal = chooser.showOpenDialog(frame);
      String fname;
      if (returnVal == JFileChooser.APPROVE_OPTION) {
        File f = chooser.getSelectedFile();
        fname = f.getPath();
        println("Opening file : " + fname);
        if (fname != null) {
          FileInputStream fis = new FileInputStream(fname);
          ObjectInputStream ois = new ObjectInputStream(fis);
          nodes = (Node[][]) ois.readObject();
          ois.close();
          fis.close();
        }
      }
    }
    catch (Exception ex)
    {
      print("Exception thrown during test: " + ex.toString());
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