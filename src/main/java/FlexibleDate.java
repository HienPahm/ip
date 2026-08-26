import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

/**
 * A deadline/event date-or-time value that understands the yyyy-MM-dd
 * format (e.g. "2019-10-15") as an actual java.time.LocalDate, while
 * still accepting free text (e.g. "Sunday", "no idea :-p") that doesn't
 * match that format, storing it as-is instead of rejecting it.
 *
 * A value recognized as a date is displayed as "MMM dd yyyy" (e.g.
 * "Oct 15 2019") instead of being echoed back verbatim.
 */
public class FlexibleDate {
    private static final DateTimeFormatter INPUT_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter DISPLAY_FORMAT = DateTimeFormatter.ofPattern("MMM dd yyyy");

    // Exactly one of these is non-null.
    private final LocalDate date;
    private final String text;

    public FlexibleDate(String value) {
        LocalDate parsed;
        try {
            parsed = LocalDate.parse(value, INPUT_FORMAT);
        } catch (DateTimeParseException e) {
            parsed = null;
        }
        this.date = parsed;
        this.text = (parsed == null) ? value : null;
    }

    /** How this value should be shown to the user, e.g. in toString(). */
    public String toDisplayString() {
        return (date != null) ? date.format(DISPLAY_FORMAT) : text;
    }

    /**
     * How this value should be written to the save file. A recognized
     * date is normalized back to yyyy-MM-dd so it round-trips through
     * save/load as the same LocalDate rather than as display text.
     */
    public String toSaveString() {
        return (date != null) ? date.format(INPUT_FORMAT) : text;
    }
}
