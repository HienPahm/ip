/** The "delete &lt;task number&gt;" command: removes a task from the list. */
public class DeleteCommand extends Command {
    private final int index;

    public DeleteCommand(int index) {
        this.index = index;
    }

    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) throws WillException {
        requireValidIndex(tasks, index);
        Task removed = tasks.remove(index);
        storage.save(tasks.getTasks());
        ui.showMessage("Noted. I've removed this task:");
        ui.showMessage("  " + removed.toString());
        ui.showMessage("Now you have " + tasks.size() + " tasks in the list.");
    }
}
