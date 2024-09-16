package info.prog.zentask.model;

/**
 *La Classe Tache représente une tache avec un id unique, une description, une priorité, une 'deadline', un état
 *@author LOUIS Lazare
 * @version 1.0
 **/
public class Tache {
    private int id;
    private String title;
    private String description;
    private int priority;
    private String deadline;
    private String status;
    private Integer projectId;
    private String projectName;

    /**
     * Constructeur qui crée une nouvelle tâche
     *
     * @param id
     * @param title
     * @param description
     * @param priority
     * @param deadline
     * @param status
     * @param projectId
     * @param projectName
     */
    public Tache(int id, String title, String description, int priority, String deadline, String status, Integer projectId, String projectName) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.priority = priority;
        this.deadline = deadline;
        this.status = status;
        this.projectId = projectId;
        this.projectName = projectName;
    }


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

    public String getProjectName() {
        return projectName;
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

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }
}
