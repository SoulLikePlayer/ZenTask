package info.prog.zentask.model;

/**
 * Enumération Status représente les différents statuts qu'une tâche ou un projet peut avoir.
 * Elle permet de spécifier l'état d'avancement ou l'état actuel.
 */
public enum Status {
    EN_COURS, // Tâche ou projet en cours d'exécution
    TERMINÉ, // Tâche ou projet terminé avec succès
    ERREUR, // Tâche ou projet qui a rencontré une erreur
    ANNULÉ // Tâche ou projet annulé avant la fin
}
