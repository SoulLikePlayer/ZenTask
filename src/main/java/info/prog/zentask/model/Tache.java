package info.prog.zentask.model;

public class Tache {
    private Integer id; // Id unique de la tâche
    private String title; // Titre de la tâche (obligatoire)
    private String description; // Description de la tâche (obligatoire)
    private Integer priority; // Priorité de la tâche (peut être null)
    private String deadline; // Deadline de la tâche (peut être null)
    private Status status; // Statut de la tâche (obligatoire)
    private Projet project; // Projet associé à la tâche (obligatoire)

    /**
     * Constructeur pour créer une nouvelle tâche sans spécifier l'id.
     *
     * @param title Le titre de la tâche (obligatoire).
     * @param description La description de la tâche (obligatoire).
     * @param priority La priorité de la tâche (peut être null).
     * @param deadline La date limite de la tâche (peut être null).
     * @param status Le statut de la tâche (obligatoire).
     * @param project Le projet auquel cette tâche est associée (obligatoire).
     */
    public Tache(String title, String description, Integer priority, String deadline, Status status, Projet project) {
        this(null, title, description, priority, deadline, status, project);
    }

    /**
     * Constructeur pour créer une nouvelle tâche en spécifiant l'id.
     *
     * @param id L'identifiant de la tâche.
     * @param title Le titre de la tâche (obligatoire).
     * @param description La description de la tâche (obligatoire).
     * @param priority La priorité de la tâche (peut être null).
     * @param deadline La date limite de la tâche (peut être null).
     * @param status Le statut de la tâche (obligatoire).
     * @param project Le projet auquel cette tâche est associée (obligatoire).
     */
    public Tache(Integer id, String title, String description, Integer priority, String deadline, Status status, Projet project) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.priority = priority;
        this.deadline = deadline;
        this.status = status;
        this.project = project;
    }

    // Getters et Setters pour accéder et modifier les attributs de la tâche

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getPriority() {
        return priority;
    }

    public void setPriority(Integer priority) {
        this.priority = priority;
    }

    public String getDeadline() {
        return deadline;
    }

    public void setDeadline(String deadline) {
        this.deadline = deadline;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public Projet getProject() {
        return project;
    }

    public void setProject(Projet project) {
        this.project = project;
    }
}
