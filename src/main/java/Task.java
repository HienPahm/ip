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
}
