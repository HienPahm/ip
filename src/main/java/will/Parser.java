package will;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;

import will.command.AddCommand;
import will.command.Command;
import will.command.DeleteCommand;
import will.command.ExitCommand;
import will.command.FindCommand;
import will.command.ListCommand;
import will.command.MarkCommand;
import will.command.OnCommand;
import will.command.UnmarkCommand;
import will.task.Deadline;
import will.task.Event;
import will.task.FlexibleDate;
import will.task.Todo;

/**
 * Deals with making sense of the user command: turns one line of raw
 * input into the Command object that knows how to carry it out.
 *
 * Note: this only validates what can be known from the text alone
 * (numeric format, presence of required parts). A task-number argument
 * (mark/unmark/delete) can't be bounds-checked here, since Parser never
 * sees the TaskList — that check happens in the Command's execute().
 */
public class Parser {
    public static Command parse(String fullCommand) throws WillException {
        String command = fullCommand.split(" ", 2)[0].toLowerCase();
        String rest = fullCommand.length() > command.length()
                ? fullCommand.substring(command.length()).trim()
                : "";

        if (command.equals("bye")) {
            return new ExitCommand();
        } else if (command.equals("list")) {
            return new ListCommand();
        } else if (command.equals("on")) {
            return parseOn(rest);
        } else if (command.equals("mark")) {
            return new MarkCommand(parseTaskIndex(command, rest));
        } else if (command.equals("unmark")) {
            return new UnmarkCommand(parseTaskIndex(command, rest));
        } else if (command.equals("delete")) {
            return new DeleteCommand(parseTaskIndex(command, rest));
        } else if (command.equals("todo")) {
            return parseTodo(rest);
        } else if (command.equals("deadline")) {
            return parseDeadline(rest);
        } else if (command.equals("event")) {
            return parseEvent(rest);
        } else if (command.equals("find")) {
            return parseFind(rest);
        } else {
            throw new WillException("I don't recognize that command. "
                    + "Try: todo, deadline, event, list, on, find, mark, unmark, delete, or bye.");
        }
    }

    private static Command parseOn(String rest) throws WillException {
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
        return new OnCommand(queryDate);
    }

    /** Parses the argument for "find" into a FindCommand. */
    private static Command parseFind(String rest) throws WillException {
        if (rest.isEmpty()) {
            throw new WillException("Tell me what to search for! Try: find <keyword>");
        }
        return new FindCommand(rest);
    }

    /**
     * Parses the task number argument for mark/unmark/delete, validating
     * that it is present and numeric. Whether it refers to an existing
     * task can't be checked here (see class javadoc) — Command#execute
     * does that via Command#requireValidIndex.
     */
    private static int parseTaskIndex(String command, String rest) throws WillException {
        if (rest.isEmpty()) {
            throw new WillException("Tell me which task number! Try: " + command + " <task number>");
        }
        try {
            return Integer.parseInt(rest) - 1;
        } catch (NumberFormatException e) {
            throw new WillException("\"" + rest + "\" isn't a valid task number.");
        }
    }

    private static Command parseTodo(String rest) throws WillException {
        if (rest.isEmpty()) {
            throw new WillException("A todo needs a description! Try: todo <what you need to do>");
        }
        requireNoPipe(rest, "description");
        return new AddCommand(new Todo(rest));
    }

    private static Command parseDeadline(String rest) throws WillException {
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
        return new AddCommand(new Deadline(description, by));
    }

    private static Command parseEvent(String rest) throws WillException {
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
        return new AddCommand(new Event(description, from, to));
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
}
