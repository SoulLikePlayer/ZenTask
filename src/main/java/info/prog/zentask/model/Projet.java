package info.prog.zentask.model;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Classe Projet représente un projet avec un id unique, un nom, une description et une liste de tâches.
 */
public class Projet {
    private static final AtomicInteger idCounter = new AtomicInteger(1); // Compteur pour générer les ids automatiquement
    private Integer id;
    private String name; // Obligatoire
    private String description; // Obligatoire
    private List<Tache> tasks; // Liste des tâches (facultatif)

    /**
     * Constructeur pour créer un projet avec un nom et une description.
     * @param name Le nom du projet (obligatoire).
     * @param description La description du projet (obligatoire).
     */
    public Projet(String name, String description) {
        this.id = idCounter.getAndIncrement();
        this.name = name;
        this.description = description;
        this.tasks = new ArrayList<>();
    }

    // Méthode pour ajouter une tâche à la liste de tâches du projet
    public void addTask(Tache task) {
        this.tasks.add(task);
    }

    // Getters
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