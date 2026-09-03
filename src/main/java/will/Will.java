package will;

import java.nio.file.Paths;

import will.command.Command;

/**
 * The Will chatbot. Wires together Ui, Storage, TaskList, Parser, and
 * the Command hierarchy: run() reads a line, turns it into a Command,
 * and lets that Command carry itself out.
 */
public class Will {
    private final Ui ui;
    private final Storage storage;
    private final TaskList tasks;

    /** Whether the most recent {@link #getResponse(String)} call was "bye". */
    private boolean isLastResponseExit = false;

    /**
     * Sets up Ui/Storage/TaskList, greets the user, then loads any
     * previously saved tasks.
     *
     * @param filePath Where the task list is loaded from and saved to.
     */
    public Will(String filePath) {
        ui = new Ui();
        // Paths.get joins with the right separator for whatever OS this
        // runs on, e.g. "data/will.txt" -> data\will.txt on Windows.
        storage = new Storage(Paths.get(filePath));
        // Order matters here: greet first, then load — a corrupted-line
        // warning from load() must print after the banner, not before it.
        ui.showWelcome();
        tasks = new TaskList(storage.load(ui));
        ui.showLine();
    }

    /** Reads commands and executes them, one at a time, until "bye". */
    public void run() {
        boolean isExit = false;
        while (!isExit) {
            try {
                String fullCommand = ui.readCommand();
                Command c = Parser.parse(fullCommand);
                c.execute(tasks, ui, storage);
                isExit = c.isExit();
            } catch (WillException e) {
                ui.showError(e.getMessage());
            } finally {
                ui.showLine();
            }
        }
    }

    /**
     * Processes one line of input the same way {@link #run()} does for
     * each command, but returns the chatbot's reply as a String instead
     * of printing it to the console. Used by the GUI (see
     * {@code will.gui}), which shows this text in a dialog bubble rather
     * than the console's framed CLI output.
     *
     * <p>This works by temporarily capturing {@code System.out} while
     * the shared {@link Ui}/{@link Command} code runs, then stripping
     * out the CLI-only framing (the divider line and each message's
     * leading indent) from what was printed — reusing the exact same
     * Parser/Command/Ui path the console UI uses, rather than
     * duplicating its logic for the GUI.
     *
     * @param input One line of user input, e.g. "list".
     * @return The chatbot's reply, with CLI framing stripped.
     */
    public String getResponse(String input) {
        java.io.ByteArrayOutputStream buffer = new java.io.ByteArrayOutputStream();
        java.io.PrintStream original = System.out;
        System.setOut(new java.io.PrintStream(buffer));
        isLastResponseExit = false;
        try {
            Command c = Parser.parse(input);
            c.execute(tasks, ui, storage);
            isLastResponseExit = c.isExit();
            if (isLastResponseExit) {
                ui.showGoodbye();
            }
        } catch (WillException e) {
            ui.showError(e.getMessage());
        } finally {
            System.setOut(original);
        }

        StringBuilder cleaned = new StringBuilder();
        for (String line : buffer.toString().split("\n", -1)) {
            if (line.strip().matches("_{10,}")) {
                continue;
            }
            if (line.startsWith("     ")) {
                line = line.substring(5);
            }
            if (!line.isEmpty()) {
                cleaned.append(line).append('\n');
            }
        }
        return cleaned.toString().strip();
    }

    /** Whether the input last passed to {@link #getResponse(String)} was "bye". */
    public boolean isLastResponseExit() {
        return isLastResponseExit;
    }

    /** Starts the chatbot, saving/loading tasks from data/will.txt. */
    public static void main(String[] args) {
        new Will("data/will.txt").run();
    }
}
