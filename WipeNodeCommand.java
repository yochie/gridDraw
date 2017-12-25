import java.util.ArrayList;

public class WipeNodeCommand implements Command {

  private Node node;
  private Grid grid;
  private ArrayList<Node> outgoing;
  private ArrayList<Node> incoming;
  private boolean highlighted;

  public WipeNodeCommand(Node n, Grid g) {
    this.grid = g;
    this.node = n;
    this.highlighted = n.isHighlighted();
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
      System.out.println("Error : Nothing to wipe from node.");
    }
    return success;
  }


  public boolean undo() {
    boolean success = true;
    //reset highlight
    this.node.highlight(this.highlighted);

    //reset out
    for (Node n : outgoing) {
      success = this.grid.connect(this.node.getRow(),this.node.getCol(), n.getRow(), n.getCol());
      if (!success) {
        System.out.println("Error: Failed to reestablish connection.");
        return success;
      }
    }

    //vreset in
    for (Node n : incoming) {
      success = this.grid.connect(n.getRow(), n.getCol(), this.node.getRow(),this.node.getCol());
      if (!success) {
        System.out.println("Error: Failed to reestablish connection.");
        return success;
      }
    }

    return success;
  }
}