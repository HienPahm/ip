package will.task;

import java.time.LocalDate;

/**
 * A single task tracked by the chatbot: a description, a done/not-done
 * status, and a {@link TaskType}. {@link Todo}, {@link Deadline}, and
 * {@link Event} extend this with their own extra fields (e.g. a due
 * date) and override {@link #toString()}, {@link #toSaveFormat()}, and
 * {@link #occursOn(LocalDate)} as needed.
 */
public class Task {
    protected String description;
    protected boolean isDone;
    protected TaskType type;

    public Task(String description, TaskType type) {
        this.description = description;
        this.type = type;
        this.isDone = false;
    }

    public String getStatusIcon() {
        return (isDone ? "X" : " "); // mark done task with X
    }

    public String getDescription() {
        return description;
    }

    public TaskType getType() {
        return type;
    }

    public void markAsDone() {
        isDone = true;
    }

    public void markAsNotDone() {
        isDone = false;
    }

    @Override
    public String toString() {
        return "[" + type.getSymbol() + "][" + getStatusIcon() + "] " + description;
    }

    /**
     * Serializes this task to a single pipe-delimited line for storage on
     * disk, e.g. "T | 1 | read book". Subclasses append their own extra
     * fields after calling super.toSaveFormat().
     */
    public String toSaveFormat() {
        return type.getSymbol() + " | " + (isDone ? "1" : "0") + " | " + description;
    }

    /**
     * Whether this task should show up in an "on &lt;date&gt;" query for
     * the given date. A plain Task/Todo has no date, so it never
     * matches; Deadline and Event override this with their own logic.
     */
    public boolean occursOn(LocalDate date) {
        return false;
    }
}