import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Will {
    // Relative, OS-independent path (Paths.get joins with the right
    // separator for whatever OS this runs on): ./data/will.txt
    private static final Path DATA_FILE = Paths.get("data", "will.txt");

    public static void main(String[] args) {
        Ui ui = new Ui();
        ui.showWelcome();

        ArrayList<Task> tasks = loadTasks(ui);
        ui.showLine();

        Scanner scanner = new Scanner(System.in);
        while (true) {
            // Trim the whole line first: without this, a line with leading
            // whitespace (e.g. " todo book") would split into an empty
            // first token and every command would look unrecognized.
            String input = scanner.nextLine().trim();
            if (input.isEmpty()) {
                // A blank line (or one that's only whitespace) isn't a
                // typo worth an "OOPS!!!" — just wait for the next line.
                continue;
            }
            String command = input.split(" ", 2)[0].toLowerCase();
            String rest = input.length() > command.length() ? input.substring(command.length()).trim() : "";

            try {
                if (command.equals("bye")) {
                    ui.showGoodbye();
                    ui.showLine();
                    break;
                } else if (command.equals("list")) {
                    ui.showMessage("Here are the tasks in your list:");
                    for (int i = 0; i < tasks.size(); i++) {
                        ui.showMessage((i + 1) + "." + tasks.get(i).toString());
                    }
                    ui.showLine();
                } else if (command.equals("on")) {
                    if (rest.isEmpty()) {
                        throw new WillException("Tell me which date! Try: on <yyyy-MM-dd>, e.g. on 2019-10-15");
                    }
                    LocalDate queryDate;
                    try {
                        queryDate = FlexibleDate.parseExact(rest);
                    } catch (DateTimeParseException e) {
                        throw new WillException("\"" + rest + "\" isn't a date in yyyy-MM-dd format. "
                                + "Try: on <yyyy-MM-dd>, e.g. on 2019-10-15");
                    }
                    ui.showMessage("Here are the tasks occurring on " + FlexibleDate.formatForDisplay(queryDate) + ":");
                    int matchNumber = 0;
                    for (Task task : tasks) {
                        if (task.occursOn(queryDate)) {
                            matchNumber++;
                            ui.showMessage(matchNumber + "." + task.toString());
                        }
                    }
                    ui.showLine();
                } else if (command.equals("mark")) {
                    int index = parseTaskIndex(command, rest, tasks.size());
                    tasks.get(index).markAsDone();
                    saveTasks(tasks);
                    ui.showMessage("Amazing Gangie! I've marked this task as done:");
                    ui.showMessage("  " + tasks.get(index).toString());
                    ui.showLine();
                } else if (command.equals("unmark")) {
                    int index = parseTaskIndex(command, rest, tasks.size());
                    tasks.get(index).markAsNotDone();
                    saveTasks(tasks);
                    ui.showMessage("OK, I've marked this task as not done yet:");
                    ui.showMessage("  " + tasks.get(index).toString());
                    ui.showLine();
                } else if (command.equals("delete")) {
                    int index = parseTaskIndex(command, rest, tasks.size());
                    Task removed = tasks.remove(index);
                    saveTasks(tasks);
                    ui.showMessage("Noted. I've removed this task:");
                    ui.showMessage("  " + removed.toString());
                    ui.showMessage("Now you have " + tasks.size() + " tasks in the list.");
                    ui.showLine();
                } else if (command.equals("todo")) {
                    if (rest.isEmpty()) {
                        throw new WillException("A todo needs a description! Try: todo <what you need to do>");
                    }
                    requireNoPipe(rest, "description");
                    tasks.add(new Todo(rest));
                    saveTasks(tasks);
                    ui.showMessage("Got it. I've added this task:");
                    ui.showMessage("  " + tasks.get(tasks.size() - 1).toString());
                    ui.showMessage("Now you have " + tasks.size() + " tasks in the list.");
                    ui.showLine();
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
                    requireNoPipe(description, "description");
                    requireNoPipe(by, "/by time");
                    tasks.add(new Deadline(description, by));
                    saveTasks(tasks);
                    ui.showMessage("Got it. I've added this task:");
                    ui.showMessage("  " + tasks.get(tasks.size() - 1).toString());
                    ui.showMessage("Now you have " + tasks.size() + " tasks in the list.");
                    ui.showLine();
                } else if (command.equals("event")) {
                    if (rest.isEmpty() || !rest.contains("/from") || !rest.contains("/to")) {
                        throw new WillException("An event needs a description, a /from time and a /to time! "
                                + "Try: event <what's happening> /from <start> /to <end>");
                    }
                    int fromIndex = rest.indexOf("/from");
                    int toIndex = rest.indexOf("/to");
                    if (fromIndex > toIndex) {
                        throw new WillException("Your /from time needs to come before /to! "
                                + "Try: event <what's happening> /from <start> /to <end>");
                    }
                    String description = rest.substring(0, fromIndex).trim();
                    String from = rest.substring(fromIndex + 5, toIndex).trim();
                    String to = rest.substring(toIndex + 3).trim();
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
                    requireNoPipe(description, "description");
                    requireNoPipe(from, "/from time");
                    requireNoPipe(to, "/to time");
                    tasks.add(new Event(description, from, to));
                    saveTasks(tasks);
                    ui.showMessage("Got it. I've added this task:");
                    ui.showMessage("  " + tasks.get(tasks.size() - 1).toString());
                    ui.showMessage("Now you have " + tasks.size() + " tasks in the list.");
                    ui.showLine();
                } else {
                    throw new WillException("I don't recognize that command. "
                            + "Try: todo, deadline, event, list, on, mark, unmark, delete, or bye.");
                }
            } catch (WillException e) {
                ui.showMessage("OOPS!!! " + e.getMessage());
                ui.showLine();
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
     * The save file format is pipe-delimited, so a "|" typed into a
     * description/by/from/to field would silently corrupt it (it'd be
     * misread as an extra field on the next load). Reject it up front
     * with a clear message instead of accepting input we can't save
     * correctly.
     */
    private static void requireNoPipe(String field, String label) throws WillException {
        if (field.contains("|")) {
            throw new WillException("Sorry, the " + label + " can't contain a \"|\" character — "
                    + "try rephrasing without it.");
        }
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
    private static ArrayList<Task> loadTasks(Ui ui) {
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
                    ui.showMessage("OOPS!!! Skipping a corrupted line in the data file: " + e.getMessage());
                }
            }
        } catch (IOException e) {
            ui.showMessage("OOPS!!! I couldn't load your saved tasks: " + e.getMessage());
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
        if (!parts[1].equals("0") && !parts[1].equals("1")) {
            throw new WillException("\"" + line + "\" has an invalid done flag (expected 0 or 1).");
        }
        boolean isDone = parts[1].equals("1");
        String description = parts[2];
        if (description.isBlank()) {
            throw new WillException("\"" + line + "\" has an empty description.");
        }

        Task task;
        if (typeSymbol.equals(TaskType.TODO.getSymbol())) {
            task = new Todo(description);
        } else if (typeSymbol.equals(TaskType.DEADLINE.getSymbol())) {
            if (parts.length < 4 || parts[3].isBlank()) {
                throw new WillException("\"" + line + "\" is missing its /by field.");
            }
            task = new Deadline(description, parts[3]);
        } else if (typeSymbol.equals(TaskType.EVENT.getSymbol())) {
            if (parts.length < 5 || parts[3].isBlank() || parts[4].isBlank()) {
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

}
