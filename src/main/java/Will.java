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
            if (input.equals("bye")) {
                printMessage("Seee yaaaa! Meet again soon!");
                printLine();
                break;
            } else if (input.equals("list")) {
                printMessage("Here are the tasks in your list:");
                for (int i = 0; i < taskCount; i++) {
                    printMessage((i + 1) + "." + tasks[i].toString());
                }
                printLine();
            } else if (input.startsWith("mark ")) {
                int index = Integer.parseInt(input.substring(5).trim()) - 1;
                tasks[index].markAsDone();
                printMessage("Amazing Gangie! I've marked this task as done:");
                printMessage("  " + tasks[index].toString());
                printLine();
            } else if (input.startsWith("unmark ")) {
                int index = Integer.parseInt(input.substring(7).trim()) - 1;
                tasks[index].markAsNotDone();
                printMessage("OK, I've marked this task as not done yet:");
                printMessage("  " + tasks[index].toString());
                printLine();
            } else if (input.startsWith("todo ")) {
                String description = input.substring(5).trim();
                tasks[taskCount] = new Todo(description);
                taskCount++;
                printMessage("Got it. I've added this task:");
                printMessage("  " + tasks[taskCount - 1].toString());
                printMessage("Now you have " + taskCount + " tasks in the list.");
                printLine();
            } else if (input.startsWith("deadline ")) {
                String details = input.substring(9).trim();
                String description = details.substring(0, details.indexOf("/by")).trim();
                String by = details.substring(details.indexOf("/by") + 3).trim();
                tasks[taskCount] = new Deadline(description, by);
                taskCount++;
                printMessage("Got it. I've added this task:");
                printMessage("  " + tasks[taskCount - 1].toString());
                printMessage("Now you have " + taskCount + " tasks in the list.");
                printLine();
            } else {
                tasks[taskCount] = new Task(input);
                taskCount++;
                printMessage("added: " + input);
                printLine();
            }
        }

        scanner.close();
    }

    private static void printLine() {
        System.out.println("    ____________________________________________________________");
    }

    private static void printMessage(String message) {
        System.out.println("     " + message);
    }
}
