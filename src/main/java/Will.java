import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Will {
    // Relative, OS-independent path (Paths.get joins with the right
    // separator for whatever OS this runs on): ./data/will.txt
    private static final Path DATA_FILE = Paths.get("data", "will.txt");

    public static void main(String[] args) {
        String logo = " __        _____ _     _     \n"
                + " \\ \\      / /_ _| |   | |    \n"
                + "  \\ \\ /\\ / / | || |   | |    \n"
                + "   \\ V  V /  | || |___| |___ \n"
                + "    \\_/\\_/  |___|_____|_____|\n";

        printLine();
        System.out.println(logo);
        printMessage("What's up!!! I'm Will.");
        printMessage("How may I assist you?");

        ArrayList<Task> tasks = loadTasks();
        printLine();

        Scanner scanner = new Scanner(System.in);
        while (true) {
            String input = scanner.nextLine();
            String command = input.split(" ", 2)[0];
            String rest = input.length() > command.length() ? input.substring(command.length()).trim() : "";

            try {
                if (command.equals("bye")) {
                    printMessage("Seee yaaaa! Meet again soon!");
                    printLine();
                    break;
                } else if (command.equals("list")) {
                    printMessage("Here are the tasks in your list:");
                    for (int i = 0; i < tasks.size(); i++) {
                        printMessage((i + 1) + "." + tasks.get(i).toString());
                    }
                    printLine();
                } else if (command.equals("mark")) {
                    int index = parseTaskIndex(command, rest, tasks.size());
                    tasks.get(index).markAsDone();
                    saveTasks(tasks);
                    printMessage("Amazing Gangie! I've marked this task as done:");
                    printMessage("  " + tasks.get(index).toString());
                    printLine();
                } else if (command.equals("unmark")) {
                    int index = parseTaskIndex(command, rest, tasks.size());
                    tasks.get(index).markAsNotDone();
                    saveTasks(tasks);
                    printMessage("OK, I've marked this task as not done yet:");
                    printMessage("  " + tasks.get(index).toString());
                    printLine();
                } else if (command.equals("delete")) {
                    int index = parseTaskIndex(command, rest, tasks.size());
                    Task removed = tasks.remove(index);
                    saveTasks(tasks);
                    printMessage("Noted. I've removed this task:");
                    printMessage("  " + removed.toString());
                    printMessage("Now you have " + tasks.size() + " tasks in the list.");
                    printLine();
                } else if (command.equals("todo")) {
                    if (rest.isEmpty()) {
                        throw new WillException("A todo needs a description! Try: todo <what you need to do>");
                    }
                    tasks.add(new Todo(rest));
                    saveTasks(tasks);
                    printMessage("Got it. I've added this task:");
                    printMessage("  " + tasks.get(tasks.size() - 1).toString());
                    printMessage("Now you have " + tasks.size() + " tasks in the list.");
                    printLine();
                } else if (command.equals("deadline")) {
                    if (rest.isEmpty() || !rest.contains("/by")) {
                        throw new WillException("A deadline needs a description and a /by time! "
                                + "Try: deadline <what you need to do> /by <when it's due>");
                    }
                    String description = rest.substring(0, rest.indexOf("/by")).trim();
                    String by = rest.substring(rest.indexOf("/by") + 3).trim();
                    if (description.isEmpty()) {
                        throw new WillException("A deadline needs a description before /by! "
                                + "Try: deadline <what you need to do> /by <when it's due>");
                    }
                    if (by.isEmpty()) {
                        throw new WillException("Tell me when this deadline is due! "
                                + "Try: deadline <what you need to do> /by <when it's due>");
                    }
                    tasks.add(new Deadline(description, by));
                    saveTasks(tasks);
                    printMessage("Got it. I've added this task:");
                    printMessage("  " + tasks.get(tasks.size() - 1).toString());
                    printMessage("Now you have " + tasks.size() + " tasks in the list.");
                    printLine();
                } else if (command.equals("event")) {
                    if (rest.isEmpty() || !rest.contains("/from") || !rest.contains("/to")) {
                        throw new WillException("An event needs a description, a /from time and a /to time! "
                                + "Try: event <what's happening> /from <start> /to <end>");
                    }
                    String description = rest.substring(0, rest.indexOf("/from")).trim();
                    String from = rest.substring(rest.indexOf("/from") + 5, rest.indexOf("/to")).trim();
                    String to = rest.substring(rest.indexOf("/to") + 3).trim();
                    if (description.isEmpty()) {
                        throw new WillException("An event needs a description before /from! "
                                + "Try: event <what's happening> /from <start> /to <end>");
                    }
                    if (from.isEmpty()) {
                        throw new WillException("Tell me when this event starts! "
                                + "Try: event <what's happening> /from <start> /to <end>");
                    }
                    if (to.isEmpty()) {
                        throw new WillException("Tell me when this event ends! "
                                + "Try: event <what's happening> /from <start> /to <end>");
                    }
                    tasks.add(new Event(description, from, to));
                    saveTasks(tasks);
                    printMessage("Got it. I've added this task:");
                    printMessage("  " + tasks.get(tasks.size() - 1).toString());
                    printMessage("Now you have " + tasks.size() + " tasks in the list.");
                    printLine();
                } else {
                    throw new WillException("I don't recognize that command. "
                            + "Try: todo, deadline, event, list, mark, unmark, delete, or bye.");
                }
            } catch (WillException e) {
                printMessage("OOPS!!! " + e.getMessage());
                printLine();
            }
        }

        scanner.close();
    }

    /**
     * Parses the task number argument for mark/unmark/delete, validating
     * that it is present, numeric, and refers to an existing task.
     */
    private static int parseTaskIndex(String command, String rest, int taskCount) throws WillException {
        if (rest.isEmpty()) {
            throw new WillException("Tell me which task number! Try: " + command + " <task number>");
        }
        int index;
        try {
            index = Integer.parseInt(rest) - 1;
        } catch (NumberFormatException e) {
            throw new WillException("\"" + rest + "\" isn't a valid task number.");
        }
        if (index < 0 || index >= taskCount) {
            throw new WillException("Task number " + (index + 1) + " doesn't exist. "
                    + "You have " + taskCount + " task(s) in your list.");
        }
        return index;
    }

    /**
     * Writes the current task list to disk, one task per line in
     * Task#toSaveFormat(). Creates the ./data folder first if it doesn't
     * exist yet, so this works on a fresh checkout where the folder has
     * never been created. On failure, reports it as a WillException
     * rather than crashing, so a save error is just another "OOPS!!!"
     * message and the session keeps running.
     */
    private static void saveTasks(ArrayList<Task> tasks) throws WillException {
        try {
            Files.createDirectories(DATA_FILE.getParent());
            StringBuilder content = new StringBuilder();
            for (Task task : tasks) {
                content.append(task.toSaveFormat()).append(System.lineSeparator());
            }
            Files.writeString(DATA_FILE, content.toString());
        } catch (IOException e) {
            throw new WillException("I couldn't save your tasks to disk: " + e.getMessage());
        }
    }

    /**
     * Loads the task list from disk at startup. If the data file doesn't
     * exist yet (e.g. this is the first time the program has ever been
     * run), returns an empty list rather than treating that as an error.
     * A line that fails to parse is skipped with a warning instead of
     * aborting the whole load, so one corrupted line doesn't cost the
     * user every other saved task.
     */
    private static ArrayList<Task> loadTasks() {
        ArrayList<Task> tasks = new ArrayList<>();
        if (!Files.exists(DATA_FILE)) {
            return tasks;
        }
        try {
            List<String> lines = Files.readAllLines(DATA_FILE);
            for (String line : lines) {
                if (line.isBlank()) {
                    continue;
                }
                try {
                    tasks.add(parseSavedTask(line));
                } catch (WillException e) {
                    printMessage("OOPS!!! Skipping a corrupted line in the data file: " + e.getMessage());
                }
            }
        } catch (IOException e) {
            printMessage("OOPS!!! I couldn't load your saved tasks: " + e.getMessage());
        }
        return tasks;
    }

    /**
     * Parses one line of the data file (Task#toSaveFormat()'s format)
     * back into the matching Task subclass.
     */
    private static Task parseSavedTask(String line) throws WillException {
        String[] parts = line.split("\\s*\\|\\s*");
        if (parts.length < 3) {
            throw new WillException("\"" + line + "\" doesn't have enough fields.");
        }
        String typeSymbol = parts[0];
        boolean isDone = parts[1].equals("1");
        String description = parts[2];

        Task task;
        if (typeSymbol.equals(TaskType.TODO.getSymbol())) {
            task = new Todo(description);
        } else if (typeSymbol.equals(TaskType.DEADLINE.getSymbol())) {
            if (parts.length < 4) {
                throw new WillException("\"" + line + "\" is missing its /by field.");
            }
            task = new Deadline(description, parts[3]);
        } else if (typeSymbol.equals(TaskType.EVENT.getSymbol())) {
            if (parts.length < 5) {
                throw new WillException("\"" + line + "\" is missing its /from or /to field.");
            }
            task = new Event(description, parts[3], parts[4]);
        } else {
            throw new WillException("\"" + typeSymbol + "\" isn't a recognized task type.");
        }

        if (isDone) {
            task.markAsDone();
        }
        return task;
    }

    private static void printLine() {
        System.out.println("    ____________________________________________________________");
    }

    private static void printMessage(String message) {
        System.out.println("     " + message);
    }
}
