import java.util.ArrayList;

public class WipeCommand extends CompoundCommand {
  private Grid grid;

  public WipeCommand(Grid g) {
    super();
    this.grid = g;
    this.commands = new ArrayList<Command>();
    for (int i = 0; i < this.grid.getNodes().length; i++) {
      for (Node n : this.grid.getNodes()[i]) {
        //System.out.println(n.x + " " + n.y);
        if ((!n.getOut().isEmpty()) || ( !n.getIn().isEmpty()) || n.highlighted) {
          this.commands.add(new WipeNodeCommand(n, this.grid));
        }
      }
    }
  }
}