package info.prog.zentask.model;

import java.util.ArrayList;
import java.util.List;

/**
 * La classe Projet représente un projet auquel plusieurs tâches peuvent être associées.
 * Elle contient un identifiant unique, un nom, une description, et une liste de tâches.
 */
public class Projet {
    private Integer id; // Identifiant unique du projet
    private String name; // Nom du projet (obligatoire)
    private String description; // Description du projet (obligatoire)
    private List<Tache> tasks; // Liste des tâches associées au projet

    /**
     * Constructeur pour créer un projet sans spécifier l'id.
     *
     * @param name Le nom du projet (obligatoire).
     * @param description La description du projet (obligatoire).
     */
    public Projet(String name, String description) {
        this(null, name, description, new ArrayList<>());
    }

    /**
     * Constructeur pour créer un projet en spécifiant l'id.
     *
     * @param id L'identifiant du projet.
     * @param name Le nom du projet (obligatoire).
     * @param description La description du projet (obligatoire).
     * @param tasks La liste des tâches associées au projet.
     */
    public Projet(Integer id, String name, String description, List<Tache> tasks) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.tasks = tasks;
    }

    /**
     * Ajoute une tâche à la liste des tâches du projet.
     *
     * @param task La tâche à ajouter.
     */
    public void addTask(Tache task) {
        this.tasks.add(task);
    }

    // Getters et Setters pour accéder et modifier les attributs du projet

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<Tache> getTasks() {
        return tasks;
    }

    public void setTasks(List<Tache> tasks) {
        this.tasks = tasks;
    }
}
