package will.command;

import will.Storage;
import will.TaskList;
import will.Ui;
import will.task.Task;

/**
 * The "find &lt;keyword&gt;" command: lists every task whose description
 * contains the given keyword (case-insensitive).
 */
public class FindCommand extends Command {
    private final String keyword;

    /** @param keyword The text to search for in each task's description. */
    public FindCommand(String keyword) {
        this.keyword = keyword;
    }

    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) {
        ui.showMessage("Here are the matching tasks in your list:");
        int matchNumber = 0;
        for (Task task : tasks) {
            if (task.getDescription().toLowerCase().contains(keyword.toLowerCase())) {
                matchNumber++;
                ui.showMessage(matchNumber + "." + task.toString());
            }
        }
    }
}
