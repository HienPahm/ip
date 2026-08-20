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
        int taskCount = 0;

        Scanner scanner = new Scanner(System.in);
        while (true) {
            String input = scanner.nextLine();
            if (input.equals("bye")) {
                printMessage("Seee yaaaa! Meet again soon!");
                printLine();
                break;
            } else if (input.equals("list")) {
                for (int i = 0; i < taskCount; i++) {
                    printMessage((i + 1) + ". " + tasks[i]);
                }
                printLine();
            } else {
                tasks[taskCount] = input;
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
