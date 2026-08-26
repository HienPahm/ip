package will.task;

/** A task with just a description and no associated date, e.g. "read book". */
public class Todo extends Task {
    public Todo(String description) {
        super(description, TaskType.TODO);
    }
}