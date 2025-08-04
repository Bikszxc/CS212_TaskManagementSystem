public class Main {
    public static void main(String[] args) {
        TaskManager tm = new TaskManager();

        tm.addTask("Buy groceries", "Milk, Eggs, Fruits", 9, Tasks.Priority.HIGH);
        tm.addTask("Suck a nigga dick", "mwah", 5, Tasks.Priority.MEDIUM);
        tm.addTask("Ola", "meow", 15, Tasks.Priority.HIGH);

        System.out.println("Default Sorting --");

        for (Tasks t : tm.getTasks()) {
            System.out.println(t);
        }

        if (tm.deleteTask(2)) {
            System.out.println("Successfully deleted task");
        } else {
            System.out.println("Failed to delete task");
        }

        for (Tasks t : tm.getTasks()) {
            System.out.println(t);
        }

        tm.undo();

        for (Tasks t : tm.getTasks()) {
            System.out.println(t);
        }

        tm.toggleTaskStatus(1);

        for (Tasks t : tm.getTasks()) {
            System.out.println(t);
        }

        tm.undo();

        for (Tasks t : tm.getTasks()) {
            System.out.println(t);
        }

        tm.redo();

        for (Tasks t : tm.getTasks()) {
            System.out.println(t);
        }

    }
}