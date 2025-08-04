import java.util.Stack;
import java.util.ArrayList;

public class TaskManager {
    // This is where we keep all our tasks - like a big list!
    private ArrayList<Tasks> tasks;

    // These stacks are for undo/redo - think of them like a pile of papers
    // undoStack = pile of actions we can undo (most recent on top)
    // redoStack = pile of actions we can redo (most recent on top)
    private Stack<Action> undoStack = new Stack<>();
    private Stack<Action> redoStack = new Stack<>();

    // This keeps track of what ID number to give the next task we create
    // Starts at 1, then 2, then 3, etc. so every task has a unique ID
    private int nextId;

    // Constructor - this runs when we create a new TaskManager
    public TaskManager() {
        tasks = new ArrayList<>();  // Start with empty task list
        nextId = 1;                // First task will have ID = 1
    }

    // This method is called every time we do something that can be undone
    // It saves the action to our undo pile and clears the redo pile
    public void performAction(Action action) {
        redoStack.clear();          // Clear redo stack (important!)
        undoStack.push(action);     // Add this action to undo stack
    }

    // UNDO METHOD - reverses the last thing we did
    public void undo() {
        // First check: do we have anything to undo?
        if (undoStack.isEmpty()) {
            System.out.println("No tasks to undo");
            return; // Exit method if nothing to undo
        }

        // Get the most recent action from undo stack
        Action action = undoStack.pop();    // Remove from undo stack
        redoStack.push(action);             // Put it in redo stack

        System.out.println("Undoing action: " + action.getType()); // Debug message

        // Now do the OPPOSITE of what the original action did
        switch (action.getType()) {
            case ADD:
                // Original: added a task → Undo: remove that task
                tasks.remove(action.getTask());
                System.out.println("Undo ADD: Task ID" + action.getTask().getId());
                break;

            case DELETE:
                // Original: deleted a task → Undo: put task back where it was
                int originalIndex = action.getIndex(); // Where it used to be
                if (originalIndex >= 0 && originalIndex <= tasks.size()) {
                    tasks.add(originalIndex, action.getTask()); // Put it back in same spot
                } else {
                    // Safety check: if index is weird, just add to end
                    tasks.add(action.getTask());
                }
                System.out.println("Undo DELETE: Task ID " + action.getTask().getId());
                break;

            case UPDATE:
                // Original: changed a task → Undo: change it back to old version
                Tasks currentTask = action.getTask();  // This is the NEW version
                Tasks oldTask = action.getOldTask();   // This is the OLD version

                // Find the task in our list and replace it with old version
                for (int i = 0; i < tasks.size(); i++) {
                    if (tasks.get(i).getId() == currentTask.getId()) {
                        tasks.set(i, oldTask); // Put back the old version
                        System.out.println("Undo UPDATE: Task ID " + oldTask.getId());
                        break; // Found it, no need to keep looking
                    }
                }
                break;
        }
    }

    // REDO METHOD - does the last thing we undid, again
    public void redo() {
        // First check: do we have anything to redo?
        if (redoStack.isEmpty()) {
            System.out.println("No tasks to redo");
            return; // Exit if nothing to redo
        }

        // Get the most recent undone action
        Action action = redoStack.pop();    // Remove from redo stack
        undoStack.push(action);             // Put back in undo stack

        System.out.println("Redoing action: " + action.getType());

        // Do the SAME thing the original action did
        switch (action.getType()) {
            case ADD:
                // Re-do the add: put the task back in the list
                tasks.add(action.getTask());
                System.out.println("Redo ADD: Task ID " + action.getTask().getId());
                break;

            case DELETE:
                // Re-do the delete: remove the task again
                tasks.remove(action.getTask());
                System.out.println("Redo DELETE: Task ID " + action.getTask().getId());
                break;

            case UPDATE:
                // Re-do the update: change task back to the NEW version
                Tasks oldTask = action.getOldTask();   // This was the OLD version
                Tasks newTask = action.getTask();      // This was the NEW version

                // Find the task and replace it with the NEW version
                for (int i = 0; i < tasks.size(); i++) {
                    if (tasks.get(i).getId() == oldTask.getId()) {
                        tasks.set(i, newTask); // Put back the NEW version
                        System.out.println("Redo UPDATE: Task ID " + newTask.getId());
                        break; // Found it, stop looking
                    }
                }
                break;
        }
    }

    // ADD TASK METHOD - creates a new task and adds it to our list
    public void addTask(String title, String description, int days, Tasks.Priority priority) {
        // Create the new task with current nextId
        Tasks task = new Tasks(nextId, title, description, days, priority);
        System.out.println(task); // Show what we created

        // Add it to our task list
        tasks.add(task);
        nextId++; // Increment for next task (so next one gets ID = nextId + 1)

        // Make this action undoable by recording it
        Action addAction = new Action(Action.ActionType.ADD, task);
        performAction(addAction); // This puts it in the undo stack
    }

    // DELETE TASK METHOD - removes a task with the given ID
    public boolean deleteTask(int id) {
        // Loop through all tasks to find the one with matching ID
        for (int i = 0; i < tasks.size(); i++) {
            Tasks task = tasks.get(i);
            if (task.getId() == id) {
                // Found it! Remove from list
                tasks.remove(i);

                // Make this action undoable by recording it
                // We need to remember the index (i) so we can put it back in same spot
                Action deleteAction = new Action(Action.ActionType.DELETE, task, i);
                performAction(deleteAction);

                return true; // Success!
            }
        }
        return false; // Task with that ID not found
    }

    // TOGGLE STATUS METHOD - flips a task's completion status (done ↔ not done)
    public boolean toggleTaskStatus(int id) {
        // Loop through tasks to find the one with matching ID
        for (Tasks task : tasks) {
            if (task.getId() == id) {
                // IMPORTANT: Save the current state BEFORE we change it
                Tasks oldTask = new Tasks(task); // Make a copy of current state

                // Now flip the completion status
                task.setCompleted(!task.isCompleted()); // ! means "opposite of"

                // Make this action undoable by recording both old and new states
                Action updateAction = new Action(Action.ActionType.UPDATE, oldTask, task);
                performAction(updateAction);

                System.out.println("Toggled completion status for task ID " + id);
                return true; // Success!
            }
        }
        return false; // Task with that ID not found
    }

    // SORTING METHODS - these arrange tasks in different orders

    // Sort by priority (HIGH, MEDIUM, LOW)
    public ArrayList<Tasks> sortByPriority(ArrayList<Tasks> taskList) {
        taskList.sort((t1, t2) -> t1.getPriority().compareTo(t2.getPriority()));
        return taskList;
    }

    // Sort by due date (earliest first)
    public ArrayList<Tasks> sortByDueDate(ArrayList<Tasks> taskList) {
        taskList.sort((t1, t2) -> t1.getDueDate().compareTo(t2.getDueDate()));
        return taskList;
    }

    // Sort by creation time (oldest first)
    public ArrayList<Tasks> sortByCreationTime(ArrayList<Tasks> taskList) {
        taskList.sort((t1, t2) -> t1.getCreationTime().compareTo(t2.getCreationTime()));
        return taskList;
    }

    // GETTER METHOD - returns our task list so other classes can see it
    public ArrayList<Tasks> getTasks() {
        return tasks;
    }
}