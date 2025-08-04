import java.util.Stack;
import java.util.ArrayList;

public class TaskManager {
    private ArrayList<Tasks> tasks;
    private Stack<Action> undoStack = new Stack<>();
    private Stack<Action> redoStack = new Stack<>();
    private int nextId;

    public TaskManager() {
        tasks = new ArrayList<>();
        nextId = 1;
    }

    public void performAction(Action action) {
        redoStack.clear();
        undoStack.push(action);
    }

    public void undo() {
        if (undoStack.isEmpty()) {
            System.out.println("No tasks to undo");
            return;
        }

        Action action = undoStack.pop();
        redoStack.push(action);

        System.out.println("Undoing action: " + action.getType());

        switch (action.getType()) {
            case ADD:
                tasks.remove(action.getTask());
                System.out.println("Undo ADD: Task ID" + action.getTask().getId());
                break;
            case DELETE:
                // Insert the task back at its original position
                int originalIndex = action.getIndex();
                if (originalIndex >= 0 && originalIndex <= tasks.size()) {
                    tasks.add(originalIndex, action.getTask());
                } else {
                    // Fallback: add to end if index is invalid
                    tasks.add(action.getTask());
                }
                System.out.println("Undo DELETE: Task ID " + action.getTask().getId());
                break;
        }
    }

    public void addTask(String title, String description, int days, Tasks.Priority priority) {
        Tasks task = new Tasks(nextId, title, description, days, priority);
        System.out.println(task);
        tasks.add(task);
        nextId++;

        Action addAction = new Action(Action.ActionType.ADD, task);
        performAction(addAction);
    }

    public boolean deleteTask(int id) {
        for (int i = 0; i < tasks.size(); i++) {
            Tasks task = tasks.get(i);
            if (task.getId() == id) {
                tasks.remove(i);

                Action deleteAction = new Action(Action.ActionType.DELETE, task, i);
                performAction(deleteAction);

                return true;
            }
        }
        return false;
    }

    // Sorting Methods

    public ArrayList<Tasks> sortByPriority(ArrayList<Tasks> taskList) {
        taskList.sort((t1, t2) -> t1.getPriority().compareTo(t2.getPriority()));
        return taskList;
    }

    public ArrayList<Tasks> sortByDueDate(ArrayList<Tasks> taskList) {
        taskList.sort((t1, t2) -> t1.getDueDate().compareTo(t2.getDueDate()));
        return taskList;
    }

    public ArrayList<Tasks> sortByCreationTime(ArrayList<Tasks> taskList) {
        taskList.sort((t1, t2) -> t1.getCreationTime().compareTo(t2.getCreationTime()));
        return taskList;
    }

    public ArrayList<Tasks> getTasks() {
        return tasks;
    }
}
