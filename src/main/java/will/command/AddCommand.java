package will.command;

import will.Storage;
import will.TaskList;
import will.Ui;
import will.WillException;
import will.task.Task;

/**
 * The "todo"/"deadline"/"event" commands: adds an already-constructed
 * Task to the list. One AddCommand class serves all three task types,
 * since the only thing that differs between them is which Task
 * subclass Parser builds before wrapping it in an AddCommand.
 */
public class AddCommand extends Command {
    private final Task task;

    /** @param task The already-constructed task to add. */
    public AddCommand(Task task) {
        this.task = task;
    }

    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) throws WillException {
        tasks.add(task);
        storage.save(tasks.getTasks());
        ui.showMessage("Got it. I've added this task:");
        ui.showMessage("  " + tasks.get(tasks.size() - 1).toString());
        ui.showMessage("Now you have " + tasks.size() + " tasks in the list.");
    }
}
