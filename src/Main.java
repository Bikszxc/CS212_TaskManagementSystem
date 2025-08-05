public class Main {
    static TaskManager tm = new TaskManager();

    public static void viewTasks(int sorting) {

        int titleLength = tm.getTasks().stream()
                .mapToInt(t -> t.getTitle().length())
                .max()
                .orElse(0);
        int titleColWidth = Math.max(titleLength, 20);

        int descLength = tm.getTasks().stream()
                        .mapToInt(t -> t.getDescription().length())
                                .max().orElse(0);
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
            case 1:
                for (Tasks t : tm.getTasks()) {

                    System.out.printf(rowFormat,
                            t.getId(),
                            t.getTitle(),
                            t.getDescription(),
                            t.getDueDate().toString(),
                            t.getPriority().status,
                            t.isCompleted() ? "Yes" : "No");
                }
                break;
            case 2:
                for (Tasks t : tm.sortByPriority(tm.getTasks())) {

                    System.out.printf(rowFormat,
                            t.getId(),
                            t.getTitle(),
                            t.getDescription(),
                            t.getDueDate().toString(),
                            t.getPriority().status,
                            t.isCompleted() ? "Yes" : "No");
                }
                break;

        }

        System.out.println("-".repeat(45 + titleColWidth + descColWidth));
    }

    public static void main(String[] args) {

        tm.addTask("Buy groceries", "Milk, Eggs, Fruits", "2025-08-15", Tasks.Priority.HIGH);
        tm.addTask("Test2", "onlyTest", "2025-09-15", Tasks.Priority.LOW);
        tm.addTask("Ola", "meow", "2025-08-10", Tasks.Priority.HIGH);
        tm.addTask("eck", "mwash", "2025-08-19", Tasks.Priority.MEDIUM);
        tm.addTask("GERGKERKGERKGEGERGERKGWGWEGOWGOWGOWG", "WHATEVER KERGOERGKERHEHKERHOEKFWWNkdnwkjkweigjwiegjwegefnwjfnwe", "2025-08-25", Tasks.Priority.HIGH);

        viewTasks(2);

        tm.toggleTaskStatus(1);
        tm.toggleTaskStatus(5);

        viewTasks(1);

        tm.updateTask(1).title("Sell groceries").apply();
        tm.updateTask(2).title("Buy house and lot").description("My dream house").apply();

        viewTasks(1);

        tm.undo();

        viewTasks(1);

        tm.redo();

        viewTasks(1);

    }


}