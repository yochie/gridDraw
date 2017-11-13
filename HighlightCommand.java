public class HighlightCommand implements Command {

  private int i, j;
  private boolean val;
  private Grid grid;


  public HighlightCommand(int i, int j, boolean val, Grid g) {
    this.grid = g;
    this.i = i;
    this.j = j;
    this.val = val;
  }

  public boolean execute() {
    boolean success = this.grid.highlight(i, j, val);
    return success;
  }

  public boolean undo() {
    boolean success = this.grid.highlight(i, j, !val);
    
    return success;
  }
}