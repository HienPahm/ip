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

    public static void main(String[] args) {
        new Will("data/will.txt").run();
    }
}
