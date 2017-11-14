import javax.swing.*;

import javax.swing.filechooser.*;

import java.io.*;

//distance between dots on grid
final int spacing = 32;

//Line drawing mode active?
boolean lining = false;

//Used to confirm drawing clearing using "k" key
boolean confirming = false;

//Used to stop drawing while waiting for event
boolean pause = false;
String waitingFor = "";

//previously highlighted node
Node prevNode = null;

//Main grid object
Grid grid;
Executer executer;

void setup() {
  size(1800, 900);

  //Create background img
  background(0);
  fill(0, 153, 204);
  stroke(0, 153, 204);

  //send as bg image for grid
  grid = new Grid(height, width, spacing, this);
  executer = new Executer();

  //set ellipsmode for draw
  ellipseMode(CENTER);  // Set ellipseMode to CENTER
}

void draw() {
  background(grid.bg);
  grid.updateDrawing();
}

void mousePressed() {
  if (!pause) {
    int row = constrain(mouseY, 0, (grid.spacing*grid.nodeHeight)-1)/grid.spacing;
    int col = constrain(mouseX, 0, (grid.spacing*grid.nodeWidth)-1)/grid.spacing;
    Node n = grid.getNodes()[row][col];
    
    //Drawing lines
    if (lining) {
      if (prevNode != null) {
        //connect grid.getNodes()
        Command c = new ConnectCommand(prevNode, n, grid);
        executer.run(c);
      }
      prevNode = n;
    }
    //Clearing node
    else if (mouseButton == RIGHT) {
      Command c = new WipeNodeCommand(n, grid);
      executer.run(c);
    }
    //Highlighting
    else {
      if (grid.nodes[row][col].highlighted) {
        Command c = new HighlightCommand(row, col, false, grid);
        executer.run(c);
      } else {
        Command c = new HighlightCommand(row, col, true, grid);
        executer.run(c);
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
    if (confirming || lining || pause) {
      println("Going back to default mode");    
      confirming = false;
      pause = false;
      waitingFor = "";
      lining = false;
    }
  } 
  //clear all lines and highlights
  else if (key == 'k') {
    if (confirming) {
      Command c = new WipeCommand(grid);
      executer.run(c);
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
    Command c = new WipeHighlightsCommand(grid);
    executer.run(c);
  } 
  //Reduce spacing
  else if (key == '-') {
    Command c = new ShrinkCommand(grid);
    executer.run(c);
  } 
  //Augment spacing
  else if (key == '=') {
    Command c = new GrowCommand(grid);
    executer.run(c);
  }
  //Undo 
  else if (key == 'u') {
    executer.undo();
    delay(20);
  } 
  //Redo
  else if (key == 'r') {
    executer.redo();
  } 
  //Save grid to serialized file
  else if (key == 's') {
    grid.saveToFile();
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
          grid = (Grid) ois.readObject();
          grid.attach(this);
          ois.close();
          fis.close();
        }
      }
      executer.reset();
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