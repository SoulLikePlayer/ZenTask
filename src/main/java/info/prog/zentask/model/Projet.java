package info.prog.zentask.model;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * La classe Projet représente un projet auquel plusieurs tâches peuvent être associées.
 * Elle contient un identifiant unique, un nom, une description, et une liste de tâches.
 *
 * Cette classe utilise un compteur atomique pour générer des identifiants uniques automatiquement.
 */
public class Projet {
    private static final AtomicInteger idCounter = new AtomicInteger(1); // Génération automatique des ids
    private Integer id; // Identifiant unique du projet
    private String name; // Nom du projet (obligatoire)
    private String description; // Description du projet (obligatoire)
    private List<Tache> tasks; // Liste des tâches associées au projet

    /**
     * Constructeur pour créer un projet avec un nom et une description.
     *
     * @param name Le nom du projet (obligatoire).
     * @param description La description du projet (obligatoire).
     */
    public Projet(String name, String description) {
        this.id = idCounter.getAndIncrement();
        this.name = name;
        this.description = description;
        this.tasks = new ArrayList<>();
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

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public List<Tache> getTasks() {
        return tasks;
    }

    public void setId(int id) {
        this.id = id;
    }
}
