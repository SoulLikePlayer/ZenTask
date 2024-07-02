package info.prog.zentask.model;

public class Tache {
    private int id;
    private String title;
    private String description;
    private int priority;
    private String deadline;
    private String status;
    private Integer projectId; // Peut être null
    private boolean completed;

    public Tache(int id, String title, String description, int priority, String deadline, String status, Integer projectId) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.priority = priority;
        this.deadline = deadline;
        this.status = status;
        this.projectId = projectId;
        this.completed = false;
    }

    // Getters et setters
    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public int getPriority() {
        return priority;
    }

    public String getDeadline() {
        return deadline;
    }

    public String getStatus() {
        return status;
    }

    public Integer getProjectId() {
        return projectId;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public void setDeadline(String deadline) {
        this.deadline = deadline;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setProjectId(Integer projectId) {
        this.projectId = projectId;
    }

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }
}
