public class Action {
    enum ActionType { ADD, DELETE, UPDATE }

    private ActionType type;
    private Tasks task;
    private Tasks oldTask;
    private int index;

    public Action(ActionType type, Tasks task) {
        this.type = type;
        this.task = task;
        this.index = -1;
    }

    public Action(ActionType type, Tasks task, int index) {
        this.type = type;
        this.task = task;
        this.index = index;
    }

    public Action(ActionType type, Tasks oldTask, Tasks newTask) {
        this.type = type;
        this.oldTask = oldTask;
        this.task = newTask;
        this.index = -1;
    }

    public int getIndex() {return index;}
    public ActionType getType() {return type;}
    public Tasks getTask() {return task;}
    public Tasks getOldTask() {return oldTask;}
}
