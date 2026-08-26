/**
 * Deals with all interactions with the user: the startup banner and
 * greeting, the goodbye message, and the framed message/line formatting
 * used throughout the program. Extracted out of {@link Will} so that
 * anything about "what we print and how" lives in one place.
 */
public class Ui {
    private static final String LOGO = " __        _____ _     _     \n"
            + " \\ \\      / /_ _| |   | |    \n"
            + "  \\ \\ /\\ / / | || |   | |    \n"
            + "   \\ V  V /  | || |___| |___ \n"
            + "    \\_/\\_/  |___|_____|_____|\n";

    /** Prints the startup banner followed by the greeting. */
    public void showWelcome() {
        showLine();
        System.out.println(LOGO);
        showMessage("What's up!!! I'm Will.");
        showMessage("How may I assist you?");
    }

    /** Prints the farewell message shown when the user types "bye". */
    public void showGoodbye() {
        showMessage("Seee yaaaa! Meet again soon!");
    }

    /** Prints the horizontal divider line used to frame each response. */
    public void showLine() {
        System.out.println("    ____________________________________________________________");
    }

    /** Prints a single indented message line inside the response frame. */
    public void showMessage(String message) {
        System.out.println("     " + message);
    }
}
