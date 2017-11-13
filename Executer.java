import java.util.*;

public class Executer {

  Stack<Command> executed;
  Stack<Command> undone;

  public Executer() {
    executed = new Stack<Command>();
    undone = new Stack<Command>();
  }

  public void run(Command c) {

    if (c.execute()) {
      executed.push(c);
      undone.clear();
    } else {
      System.out.println("Useless or failing command.");
    }
  }

  public void undo() {
    if (!executed.empty()) {
      Command toUndo = executed.pop();

      boolean success = toUndo.undo();

      if (success) {
        undone.push(toUndo);
      } else {
        System.out.println("Could not undo command. The executer must've missed something (:S). You can try to revert your actions manually to get it back on track... Please report bug...");
      }
    } else {
      System.out.println("Nothing to undo.");
    }
  }

  public void redo() {
    if (!undone.empty()) {
      Command toRedo = undone.pop();
      boolean success = toRedo.execute();
      if (success) {
        executed.push(toRedo);
      } else {
        System.out.println("Could not redo command. The executer must've missed something (:S). You can try to revert your actions manually to get it back on track... Please report bug...");
      }
    } else {
      System.out.println("Nothing to redo.");
    }
  }

  public void reset() {
    executed.clear();
    undone.clear();
  }
}