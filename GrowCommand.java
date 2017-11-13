public class GrowCommand implements Command {
  private Grid grid;

  public GrowCommand(Grid g) {
    this.grid = g;
  }

  public boolean execute() {
    boolean success = this.grid.grow();
    return success;
  }

  public boolean undo() {
    boolean success = this.grid.shrink();
    return success;
  }
}