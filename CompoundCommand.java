import java.util.ArrayList;

public abstract class CompoundCommand implements Command {

  protected ArrayList<Command> commands;
  private ArrayList<Boolean> results;

  public CompoundCommand() {
    this.commands = new ArrayList<Command>();
    this.results = new ArrayList<Boolean>();
  }

  public boolean execute() {
    this.results.clear();
    for (Command c : this.commands) {
      results.add(c.execute());
    }

    //Return true if at least one of the commands was successful
    for (boolean result : results) {
      if (result) {
        return true;
      }
    }
    return false;
  }

  public boolean undo() {
    for (int i = this.commands.size() - 1; i >= 0; i--) {   
      //only undo command if it was successful in the first place
      if (results.get(i)) {
        //make sure undo operation is successful
        if (!commands.get(i).undo()){
          return false;
        }
      }
    }
    return true;
  }
}