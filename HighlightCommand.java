public class HighlightCommand implements Command {

  private Node n;
  private boolean val;
  private Grid grid;


  public HighlightCommand(Node n, boolean val, Grid g) {
    this.grid = g;
    this.n = n;
    this.val = val;
  }

  public boolean execute() {
    boolean success = this.grid.highlight(n, val);
    return success;
  }

  public boolean undo() {
    boolean success = this.grid.highlight(n, !val);    
    return success;
  }
}