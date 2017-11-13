public class ShrinkCommand implements Command {
  private Grid grid;

  public ShrinkCommand(Grid g) {
    this.grid = g;
  }

  public boolean execute() {
    boolean success = this.grid.shrink();
    return success;
  }

  public boolean undo() {
    boolean success = this.grid.grow();
    return success;
  }
}