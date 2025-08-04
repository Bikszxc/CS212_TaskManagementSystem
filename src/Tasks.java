import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class Tasks {
    private int id;
    private String title, description;
    private LocalDate dueDate, creationTime;
    private boolean completed, archived;
    private Priority priority;

    public enum Priority {
        HIGH("High", "#FF0000"),
        MEDIUM("Medium", "#FF00FF"),
        LOW("Low", "#0000FF");

        public final String status, color;
        private Priority priority;

        Priority(String status, String color) {
            this.status = status;
            this.color = color;
        }

        public Priority getPriority() {
            return priority;
        }
    }

    public Tasks(int id, String title, String description, int days, Priority priority) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.dueDate = LocalDate.now().plusDays(days);
        this.priority = priority;
        this.creationTime = LocalDate.now();
        this.completed = false;
        this.archived = false;
    }

    public int getId() {return id;}
    public void setId(int id) {this.id = id;}

    public String getTitle() {return title;}
    public void setTitle(String title) {this.title = title;}

    public String getDescription() {return description;}
    public void setDescription(String description) {this.description = description;}

    public LocalDate getDueDate() {return dueDate;}
    public void setDueDate(LocalDate dueDate) {this.dueDate = dueDate;}

    public Priority getPriority() {return priority;}
    public void setPriority(Priority priority) {this.priority = priority;}

    public LocalDate getCreationTime() {return creationTime;}

    public boolean isCompleted() {return completed;}
    public void setCompleted(boolean completed) {this.completed = completed;}

    public boolean isArchived() {return archived;}
    public void setArchived(boolean archived) {this.archived = archived;}

    public String getFormattedDate() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        return dueDate.format(formatter);
    }

    @Override
    public String toString() {
        return "Tasks{id=" + id +
                ", title=" + title +
                ", desc=" + description +
                ", dueDate=" + getFormattedDate() +
                ", priority=" + priority +
                "}";
    }
}
