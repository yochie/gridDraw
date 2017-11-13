import javax.swing.*;

import javax.swing.filechooser.*;

import java.io.*;


//bg
PImage img;

//distance between dots on grid
final int spacing = 32;

//press shift to draw lines
boolean lining = false;

//variable used to confirm drawing clearing using "k" key
boolean confirming = false;

//Used to stop drawing while waiting for event
boolean pause = false;
String waitingFor = "";

//previously highlighted node
Node prevNode = null;
Grid grid;

void setup() {
  size(1800, 900);
  //Create background img
  background(0);
  fill(0, 153, 204);
  stroke(0, 153, 204);
  img = createImage(this.pixelWidth, this.pixelHeight, RGB);

  //fill img with black
  for (int i = 0; i < img.pixels.length; i++) {  
    img.pixels[i] = color(0, 0, 0, 255);
  }

  //send as bg image for grid
  grid = new Grid(height, width, spacing, img);

  //set ellipsmode for draw
  ellipseMode(CENTER);  // Set ellipseMode to CENTER
}

void draw() {

  grid.updateDrawing();
}

void mousePressed() {
  if (!pause) {
    int row = constrain(mouseY, 0, (grid.spacing*grid.nodeHeight)-1)/spacing;
    int col = constrain(mouseX, 0, (grid.spacing*grid.nodeWidth)-1)/spacing;
    Node n = grid.getNodes()[row][col];
    if (lining) {
      if (prevNode != null) {
        //connect grid.getNodes()
        grid.connect(prevNode, n);
      }
      prevNode = n;
    } else if (mouseButton == RIGHT) {
      grid.wipe(row, col);
    } else {
      //highlight them
      int result = grid.highlight(row, col);
      if (result == 1) {
        println("+row:" + row + " col:" + col);
      } else {
        println("-row:" + row + " col:" + col);
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
    }
  } 
  //reset all global mode variables to their defaults
  else if (key == ESC) {
    key = 0;
    if (confirming || lining) {
      println("Going back to default mode");    
      confirming = false;
      pause = false;
      waitingFor = "";
      lining = false;
    }
  } 
  //clear all lines and highlighted grid.getNodes()
  else if (key == 'k') {
    if (confirming) {
      grid.wipe();
      //Unpause and reset status trackers
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
  }
  //clear all highlights
  else if (key == 'm') {
    grid.wipeHighlights();
  }    
  //Save grid.getNodes() to serialized file
  else if (key == 's') {
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

          oos.writeObject(grid.getNodes());
          oos.close();
          fos.close();
        }
      }
    }
    catch (Exception ex)
    {
      print("Exception thrown during test: " + ex.toString());
    }
  } 
  //open saved file
  else if (key == 'o') {
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
          grid.setNodes((Node[][]) ois.readObject());
          ois.close();
          fis.close();
        }
      }
    }
    catch (Exception ex)
    {
      print("Error reading file: " + ex.toString());
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