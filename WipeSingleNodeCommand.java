import java.util.ArrayList;

//USE WITH CARE
//Wipes all data inside a single node WITHOUT deleting other side of connected edges
//This means the caller is responsible for deleting any other references to this node (found in out and in lists of other nodes) 
public class WipeSingleNodeCommand implements Command {
  private Node node;
  private ArrayList<Node> outgoing;
  private ArrayList<Node> incoming;
  private boolean highlighted;

  public WipeSingleNodeCommand(Node n) {
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
    boolean result;
    //if there is anything to do with node
    if (!this.node.getIn().isEmpty() || !this.node.getOut().isEmpty() || this.node.isHighlighted()) {
      this.node.getOut().clear();
      this.node.getIn().clear();
      this.node.highlight(false);
      result = true;
    } else {
      result = false;
    }
    return result;
  }

  public boolean undo() {
    this.node.highlight(this.highlighted);
    this.node.setOut(this.outgoing);
    this.node.setIn(this.incoming);
    return true;
  }
}