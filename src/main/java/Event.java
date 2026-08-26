public class Event extends Task {
    protected FlexibleDate from;
    protected FlexibleDate to;

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
}
