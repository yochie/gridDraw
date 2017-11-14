import java.util.ArrayList;

public class WipeNodeCommand implements Command {

  Node node;
  Grid grid;
  ArrayList<Node> outgoing;
  ArrayList<Node> incoming;
  boolean highlighted;

  public WipeNodeCommand(Node n, Grid g) {
    this.grid = g;
    this.node = n;
    this.highlighted = n.highlighted;
    this.outgoing = new ArrayList<Node>();
    this.incoming = new ArrayList<Node>();

    for (Node adjacent : n.getOut()) {
      outgoing.add(adjacent);
    }
    for (Node adjacent : n.getIn()) {
      incoming.add(adjacent);
    }
  }

  public boolean execute() {
    //System.out.println(this.node + " " + this.node.getOut() + " " + this.node.getIn());
    boolean success = grid.wipe(this.node);
    if (!success) {
      System.out.println("Node wipe failed : No edges to wipe from node. Normal during mass wipe.");
    }

    return success;
  }


  public boolean undo() {
    boolean success = true;
    //reset highlight
    this.node.highlight(this.highlighted);

    //reset out
    for (Node n : outgoing) {
      success = this.grid.connect(this.node, n);
      if (!success) {
        System.out.println("Warning: Failed to reestablish connection. This is normal when undoing 'k' clearing.");
      }
    }

    //vreset in
    for (Node n : incoming) {
      success = this.grid.connect(n, this.node);
      if (!success) {
        System.out.println("Warning: Failed to reestablish connection. This is normal when undoing 'k' clearing.");
      }
    }

    //Because the wipe() functionnality is difficult to reverse when performed in batch, we expect SOME undos to fail when doing them in batch.
    //This only occurs when attempting to connect already connected nodes and does not represent an actual problem with the data,
    //only a redundancy in the operations being performed. In case other problems occur, log printing messages have been included.
    return true;
  }
}