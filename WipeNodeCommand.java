import java.util.ArrayList;

public class WipeNodeCommand implements Command {

  Node node;
  Grid grid;
  ArrayList<Node> outgoing;
  ArrayList<Node> incoming;

  public WipeNodeCommand(Node n, Grid g) {
    this.grid = g;
    this.node = n;
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
    boolean success = grid.wipe(this.node);
    return success;
  }

  public boolean undo() {
    boolean success = false;
    for (Node n: outgoing){
       success = this.grid.connect(node, n);
       if (!success){return success;}
    }
    for (Node n: incoming){
      success = this.grid.connect(n, node);
      if (!success){return success;}
    }
      
    return success;
  }
}