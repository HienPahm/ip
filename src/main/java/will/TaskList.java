package will;

import java.util.ArrayList;
import java.util.Iterator;

import will.task.Task;

/**
 * Contains the task list, with operations to add, remove, and access
 * tasks in it. Wraps an ArrayList&lt;Task&gt; so callers (Will, and later
 * the Command classes) work in terms of tasks rather than the
 * underlying list implementation.
 *
 * Implements Iterable so existing for-each loops over the task list
 * (e.g. "list" and "on &lt;date&gt;") keep working unchanged.
 */
public class TaskList implements Iterable<Task> {
    private final ArrayList<Task> tasks;

    /** Starts an empty task list, e.g. when loading from disk failed. */
    public TaskList() {
        this.tasks = new ArrayList<>();
    }

    /** Wraps an already-populated list, e.g. one just read from Storage. */
    public TaskList(ArrayList<Task> tasks) {
        this.tasks = tasks;
    }

    /** Adds a task to the end of the list. */
    public void add(Task task) {
        tasks.add(task);
    }

    /** Removes and returns the task at the given index. */
    public Task remove(int index) {
        return tasks.remove(index);
    }

    /** @return The task at the given index. */
    public Task get(int index) {
        return tasks.get(index);
    }

    /** @return How many tasks are in the list. */
    public int size() {
        return tasks.size();
    }

    /**
     * The underlying ArrayList, for callers that still need it directly
     * (currently Storage#save, which serializes the whole list to disk).
     */
    public ArrayList<Task> getTasks() {
        return tasks;
    }

    /** @return An iterator over the tasks, in list order. */
    @Override
    public Iterator<Task> iterator() {
        return tasks.iterator();
    }
}
