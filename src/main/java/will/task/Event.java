package will.task;

import java.time.LocalDate;

/** A task that happens over a time span, e.g. "project meeting from Mon 2pm to 4pm". */
public class Event extends Task {
    protected FlexibleDate from;
    protected FlexibleDate to;

    /**
     * @param description What's happening.
     * @param from When it starts. Recognized as a date if it matches
     *             yyyy-MM-dd (see {@link FlexibleDate}), otherwise kept
     *             as free text.
     * @param to When it ends, parsed the same way as {@code from}.
     */
    public Event(String description, String from, String to) {
        super(description, TaskType.EVENT);
        this.from = new FlexibleDate(from);
        this.to = new FlexibleDate(to);
    }

    @Override
    public String toString() {
        return super.toString() + " (from: " + from.toDisplayString() + " to: " + to.toDisplayString() + ")";
    }

    @Override
    public String toSaveFormat() {
        return super.toSaveFormat() + " | " + from.toSaveString() + " | " + to.toSaveString();
    }

    /**
     * Matches if the queried date falls within [from, to] when both are
     * recognized dates, or equals whichever single end is a recognized
     * date if only one is (e.g. an event whose /to is free text like
     * "4pm" on the same day as /from).
     */
    @Override
    public boolean occursOn(LocalDate date) {
        LocalDate fromDate = from.getDate();
        LocalDate toDate = to.getDate();
        if (fromDate != null && toDate != null) {
            return !date.isBefore(fromDate) && !date.isAfter(toDate);
        } else if (fromDate != null) {
            return fromDate.equals(date);
        } else if (toDate != null) {
            return toDate.equals(date);
        }
        return false;
    }
}