package will.command;

import java.time.LocalDate;

import will.Storage;
import will.TaskList;
import will.Ui;
import will.task.FlexibleDate;
import will.task.Task;

/** The "on &lt;date&gt;" command: lists every task occurring on that date. */
public class OnCommand extends Command {
    private final LocalDate queryDate;

    public OnCommand(LocalDate queryDate) {
        this.queryDate = queryDate;
    }

    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) {
        ui.showMessage("Here are the tasks occurring on " + FlexibleDate.formatForDisplay(queryDate) + ":");
        int matchNumber = 0;
        for (Task task : tasks) {
            if (task.occursOn(queryDate)) {
                matchNumber++;
                ui.showMessage(matchNumber + "." + task.toString());
            }
        }
    }
}
