package info.prog.zentask.model;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * La classe Tache représente une tâche avec un id unique, un titre, une description, une priorité, une 'deadline',
 * un statut, et elle est liée à un projet spécifique.
 */
public class Tache {
    private static final AtomicInteger idCounter = new AtomicInteger(1); // Compteur pour générer les ids automatiquement
    private Integer id; // Id généré automatiquement
    private String title; // Obligatoire
    private String description; // Obligatoire
    private Integer priority; // Peut être null
    private String deadline; // Peut être null
    private Status status; // Obligatoire
    private Projet project; // Lien avec un projet (obligatoire)

    /**
     * Constructeur pour créer une tâche.
     * @param title Le titre de la tâche (obligatoire).
     * @param description La description de la tâche (obligatoire).
     * @param priority La priorité de la tâche (peut être null).
     * @param deadline La deadline de la tâche (peut être null).
     * @param status Le statut de la tâche (obligatoire).
     * @param project Le projet auquel la tâche est associée (obligatoire).
     */
    public Tache(int id, String title, String description, Integer priority, String deadline, Status status, Projet project) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.priority = priority;
        this.deadline = deadline;
        this.status = status;
        this.project = project;
    }

    // Getters et setters

    public Integer getId() {
        return id;
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