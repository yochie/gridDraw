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
    boolean success = true;
    for (Command c : this.commands) {
      results.add(c.execute());
    }
    
    //System.out.println(results);

    //Return success if at least one of the commands was successful
    for (boolean result : results) {
      if (result) {
        return true;
      }
    }
    return false;
  }

    public boolean undo() {
      boolean success = true;
      for (int i = this.commands.size() - 1; i >= 0; i--) {
        if (results.get(i)) {
          success = commands.get(i).undo();
        }
        if (!success) {
          return success;
        }
      }
      return success;
    }
  }