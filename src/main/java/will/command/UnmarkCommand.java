package will.command;

import will.Storage;
import will.TaskList;
import will.Ui;
import will.WillException;

/** The "unmark &lt;task number&gt;" command: reverses a task's done status. */
public class UnmarkCommand extends Command {
    private final int index;

    /** @param index Zero-based index of the task to mark not done. */
    public UnmarkCommand(int index) {
        this.index = index;
    }

    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) throws WillException {
        requireValidIndex(tasks, index);
        tasks.get(index).markAsNotDone();
        storage.save(tasks.getTasks());
        ui.showMessage("OK, I've marked this task as not done yet:");
        ui.showMessage("  " + tasks.get(index).toString());
    }
}
