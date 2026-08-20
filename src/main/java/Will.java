import java.util.Scanner;

public class Will {
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
        printLine();

        Task[] tasks = new Task[100];
        int taskCount = 0;

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
                    for (int i = 0; i < taskCount; i++) {
                        printMessage((i + 1) + "." + tasks[i].toString());
                    }
                    printLine();
                } else if (command.equals("mark")) {
                    int index = parseTaskIndex(rest, taskCount);
                    tasks[index].markAsDone();
                    printMessage("Amazing Gangie! I've marked this task as done:");
                    printMessage("  " + tasks[index].toString());
                    printLine();
                } else if (command.equals("unmark")) {
                    int index = parseTaskIndex(rest, taskCount);
                    tasks[index].markAsNotDone();
                    printMessage("OK, I've marked this task as not done yet:");
                    printMessage("  " + tasks[index].toString());
                    printLine();
                } else if (command.equals("todo")) {
                    if (rest.isEmpty()) {
                        throw new WillException("A todo needs a description! Try: todo <what you need to do>");
                    }
                    tasks[taskCount] = new Todo(rest);
                    taskCount++;
                    printMessage("Got it. I've added this task:");
                    printMessage("  " + tasks[taskCount - 1].toString());
                    printMessage("Now you have " + taskCount + " tasks in the list.");
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
                    tasks[taskCount] = new Deadline(description, by);
                    taskCount++;
                    printMessage("Got it. I've added this task:");
                    printMessage("  " + tasks[taskCount - 1].toString());
                    printMessage("Now you have " + taskCount + " tasks in the list.");
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
                    tasks[taskCount] = new Event(description, from, to);
                    taskCount++;
                    printMessage("Got it. I've added this task:");
                    printMessage("  " + tasks[taskCount - 1].toString());
                    printMessage("Now you have " + taskCount + " tasks in the list.");
                    printLine();
                } else {
                    throw new WillException("I don't recognize that command. "
                            + "Try: todo, deadline, event, list, mark, unmark, or bye.");
                }
            } catch (WillException e) {
                printMessage("OOPS!!! " + e.getMessage());
                printLine();
            }
        }

        scanner.close();
    }

    /**
     * Parses the task number argument for mark/unmark, validating that it
     * is present, numeric, and refers to an existing task.
     */
    private static int parseTaskIndex(String rest, int taskCount) throws WillException {
        if (rest.isEmpty()) {
            throw new WillException("Tell me which task number! Try: mark <task number>");
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

    private static void printLine() {
        System.out.println("    ____________________________________________________________");
    }

    private static void printMessage(String message) {
        System.out.println("     " + message);
    }
}
