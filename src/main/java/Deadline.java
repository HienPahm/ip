import java.time.LocalDate;

public class Deadline extends Task {
    protected FlexibleDate by;

    public Deadline(String description, String by) {
        super(description, TaskType.DEADLINE);
        this.by = new FlexibleDate(by);
    }

    @Override
    public String toString() {
        return super.toString() + " (by: " + by.toDisplayString() + ")";
    }

    @Override
    public String toSaveFormat() {
        return super.toSaveFormat() + " | " + by.toSaveString();
    }

    @Override
    public boolean occursOn(LocalDate date) {
        LocalDate byDate = by.getDate();
        return byDate != null && byDate.equals(date);
    }
}
