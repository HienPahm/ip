/** The "mark &lt;task number&gt;" command: marks a task as done. */
public class MarkCommand extends Command {
    private final int index;

    public MarkCommand(int index) {
        this.index = index;
    }

    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) throws WillException {
        requireValidIndex(tasks, index);
        tasks.get(index).markAsDone();
        storage.save(tasks.getTasks());
        ui.showMessage("Amazing Gangie! I've marked this task as done:");
        ui.showMessage("  " + tasks.get(index).toString());
    }
}
