public class WipeCommand implements Command {
  private Grid grid;

  public WipeCommand(Grid g) {
    this.grid = g;
  }

  public boolean execute() {
    boolean success = this.grid.wipe();
    return success;
  }

  public boolean undo() {
    return false;
  }
}