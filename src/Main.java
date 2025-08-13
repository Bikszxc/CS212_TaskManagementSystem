import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Locale;
import java.util.Scanner;

public class Main {
    static TaskManager tm = new TaskManager();
    private static final DateTimeFormatter DMY = DateTimeFormatter.ofPattern("dd/MM/yyyy", Locale.ENGLISH);

    public static void viewTasks(int sorting) {

        int titleLength = tm.getTasks().stream()
                .mapToInt(t -> t.getTitle().length())
                .max()
                .orElse(0);
        int titleColWidth = Math.max(titleLength, 20);

        int descLength = tm.getTasks().stream()
                .mapToInt(t -> t.getDescription().length())
                .max()
                .orElse(0);
        int descColWidth = Math.max(descLength, 35);

        String format = String.format("%%-5s %%-%ds %%-%ds %%-%ds %%-%ds %%-%ds%n",
                titleColWidth,
                descColWidth,
                12,
                10,
                10);

        System.out.println("-".repeat(45 + titleColWidth + descColWidth));
        System.out.printf(format, "ID", "Title", "Description", "Due Date", "Priority", "Completed");
        System.out.println("-".repeat(45 + titleColWidth + descColWidth));

        String rowFormat = String.format("%%-5d %%-%ds %%-%ds %%-%ds %%-%ds %%-%ds%n",
                titleColWidth,
                descColWidth,
                12,
                10,
                10);

        switch (sorting) {
            case 1: // Default Sorting
                for (Tasks t : tm.getTasks()) {
                    System.out.printf(rowFormat,
                            t.getId(),
                            t.getTitle(),
                            t.getDescription(),
                            t.getDueDate().format(DMY),
                            t.getPriority().status,
                            t.isCompleted() ? "Yes" : "No");
                }
                break;
            case 2: // Priority Sorting
                for (Tasks t : tm.sortByPriority(tm.getTasks())) {
                    System.out.printf(rowFormat,
                            t.getId(),
                            t.getTitle(),
                            t.getDescription(),
                            t.getDueDate().format(DMY),
                            t.getPriority().status,
                            t.isCompleted() ? "Yes" : "No");
                }
                break;
            case 3: // View Archived Tasks
                for (Tasks t : tm.getArchivedTasks()) {
                    System.out.printf(rowFormat,
                            t.getId(),
                            t.getTitle(),
                            t.getDescription(),
                            t.getDueDate().format(DMY),
                            t.getPriority().status,
                            t.isCompleted() ? "Yes" : "No");
                }
                break;
            case 4: // View Completed Tasks
                for (Tasks t : tm.getCompletedTasks()) {
                    System.out.printf(rowFormat,
                            t.getId(),
                            t.getTitle(),
                            t.getDescription(),
                            t.getDueDate().format(DMY),
                            t.getPriority().status,
                            t.isCompleted() ? "Yes" : "No");
                }
                break;
            default:
                System.out.println("Enter valid sorting method.");
                break;
        }

        System.out.println("-".repeat(45 + titleColWidth + descColWidth));
    }

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        int choice;

        do {
            System.out.println("\n----- Task Management System ------");
            System.out.println("1. Add Task");
            System.out.println("2. Remove Task");
            System.out.println("3. View Tasks");
            System.out.println("4. Edit a Task");
            System.out.println("5. Undo");
            System.out.println("6. Redo");
            System.out.println("7. Exit");
            System.out.print("Enter your choice: ");
            choice = readInt(sc);

            switch (choice) {
                case 1 -> addTaskFlow(sc);
                case 2 -> deleteTaskFlow(sc);
                case 3 -> viewTasksFlow(sc);
                case 4 -> editTaskFlow(sc);
                case 5 -> tm.undo();
                case 6 -> tm.redo();
                case 7 -> System.out.println("Exiting... Bye!");
                default -> System.out.println("Invalid choice. Please try again.");
            }
        } while (choice != 7);

        sc.close();
    }

    // ---- Flows ----

    private static void addTaskFlow(Scanner sc) {
        System.out.println("\n----- Add New Task -----");

        System.out.print("Enter task name: ");
        String taskName = sc.nextLine().trim();

        System.out.print("Enter task description: ");
        String taskDescription = sc.nextLine().trim();

        String taskDueDate;
        while (true) {
            System.out.print("Enter task due date (DD/MM/YYYY): ");
            taskDueDate = sc.nextLine().trim();
            if (isValidDate(taskDueDate)) break;
            System.out.println("Invalid date. Example: 13/08/2025");
        }

        Tasks.Priority priority = readPriority(sc, true);
        tm.addTask(taskName, taskDescription, taskDueDate, priority);
    }

    private static void deleteTaskFlow(Scanner sc) {
        System.out.print("\nEnter task ID to remove: ");
        int id = readInt(sc);
        boolean ok = tm.deleteTask(id);
        System.out.println(ok ? "Task deleted." : "Task not found.");
    }

    private static void viewTasksFlow(Scanner sc) {
        System.out.println("""
            \nView Options:
            1. Default (no sorting)
            2. Sort by Priority
            3. View Archived
            4. View Completed
            """);
        System.out.print("Choose: ");
        int opt = readInt(sc);
        viewTasks(opt);
    }

    private static void editTaskFlow(Scanner sc) {
        System.out.print("\nEnter task ID to edit: ");
        int id = readInt(sc);

        System.out.println("Leave a field blank to keep current value.");

        System.out.print("New title: ");
        String newTitle = sc.nextLine().trim();
        if (newTitle.isEmpty()) newTitle = null;

        System.out.print("New description: ");
        String newDesc = sc.nextLine().trim();
        if (newDesc.isEmpty()) newDesc = null;

        LocalDate newDate = null;
        while (true) {
            System.out.print("New due date (DD/MM/YYYY): ");
            String dateStr = sc.nextLine().trim();
            if (dateStr.isEmpty()) break;
            if (isValidDate(dateStr)) {
                newDate = LocalDate.parse(dateStr, DMY);
                break;
            }
            System.out.println("Invalid date. Try again or leave blank.");
        }

        Tasks.Priority newPriority = readPriority(sc, false); // allow blank

        TaskManager.TaskUpdater updater = tm.updateTask(id);
        if (newTitle != null) updater.title(newTitle);
        if (newDesc != null) updater.description(newDesc);
        if (newDate != null) updater.dueDate(newDate);
        if (newPriority != null) updater.priority(newPriority);

        boolean ok = updater.apply();
        System.out.println(ok ? "Task updated." : "Task not found.");
    }

    // ---- Helpers ----

    private static boolean isValidDate(String s) {
        try {
            LocalDate.parse(s, DMY);
            return true;
        } catch (DateTimeParseException e) {
            return false;
        }
    }

    private static int readInt(Scanner sc) {
        while (true) {
            String line = sc.nextLine().trim();
            try {
                return Integer.parseInt(line);
            } catch (NumberFormatException e) {
                System.out.print("Please enter a number: ");
            }
        }
    }

    private static Tasks.Priority readPriority(Scanner sc, boolean required) {
        while (true) {
            System.out.print(required
                    ? "Enter task priority (Low/Medium/High): "
                    : "New priority (Low/Medium/High) or leave blank: ");
            String in = sc.nextLine().trim();
            if (!required && in.isEmpty()) return null;

            switch (in.toLowerCase(Locale.ROOT)) {
                case "low": return Tasks.Priority.LOW;
                case "medium": return Tasks.Priority.MEDIUM;
                case "high": return Tasks.Priority.HIGH;
                default:
                    System.out.println("Invalid priority.");
            }
        }
    }
}
