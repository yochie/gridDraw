public class ConnectCommand implements Command {

  private Node out, in;
  private Grid grid;


  public ConnectCommand(Node out, Node in, Grid g) {
    this.grid = g;
    this.out = out;
    this.in = in;
  }

  public boolean execute() {
    boolean success = this.grid.connect(this.out, this.in);
    return success;
  }

  public boolean undo() {
    boolean success = this.grid.disconnect(this.out, this.in);
    return success;
  }
}