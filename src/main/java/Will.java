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

        String[] tasks = new String[100];
        boolean[] isDone = new boolean[100];
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
                    printMessage((i + 1) + "." + statusIcon(isDone[i]) + " " + tasks[i]);
                }
                printLine();
            } else if (input.startsWith("mark ")) {
                int index = Integer.parseInt(input.substring(5).trim()) - 1;
                isDone[index] = true;
                printMessage("Amazing Gangie! I've marked this task as done:");
                printMessage("  " + statusIcon(isDone[index]) + " " + tasks[index]);
                printLine();
            } else if (input.startsWith("unmark ")) {
                int index = Integer.parseInt(input.substring(7).trim()) - 1;
                isDone[index] = false;
                printMessage("OK, I've marked this task as not done yet:");
                printMessage("  " + statusIcon(isDone[index]) + " " + tasks[index]);
                printLine();
            } else {
                tasks[taskCount] = input;
                isDone[taskCount] = false;
                taskCount++;
                printMessage("added: " + input);
                printLine();
            }
        }

        scanner.close();
    }

    private static String statusIcon(boolean done) {
        return done ? "[X]" : "[ ]";
    }

    private static void printLine() {
        System.out.println("    ____________________________________________________________");
    }

    private static void printMessage(String message) {
        System.out.println("     " + message);
    }
}
