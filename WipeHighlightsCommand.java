import java.util.ArrayList;

public class WipeHighlightsCommand implements Command {
  private Grid grid;
  private ArrayList<Node> highlighted;

  public WipeHighlightsCommand(Grid g) {
    this.grid = g;
    this.highlighted = new ArrayList<Node>();
    for (int i = 0; i < grid.getNodes().length; i++) {
      for (Node n : grid.getNodes()[i]) {
        if (n.highlighted) {
          highlighted.add(n);
        }
      }
    }
  }

  public boolean execute() {
    boolean success = this.grid.wipeHighlights();
    return success;
  }

  public boolean undo() {
    boolean success = false;
    for (Node n : highlighted) {
      success = n.highlight(true);
      if (!success) {
        return success;
      }
    }
    return success;
  }
}