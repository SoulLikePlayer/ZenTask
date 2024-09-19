import info.prog.zentask.model.Projet;
import info.prog.zentask.model.Status;
import info.prog.zentask.model.Tache;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.not;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;

/**
 * Classe de test pour les classes Tache et Projet.
 */
public class TacheProjetTest {

    private Projet projet;
    private Tache tache1;
    private Tache tache2;

    @BeforeEach
    public void setUp() {
        // Créer un projet pour les tests
        projet = new Projet("Projet Test", "Description du projet test");

        // Créer deux tâches pour les tests
        tache1 = new Tache(1, "Tâche 1", "Description Tâche 1", 1, "2023-09-30", Status.EN_COURS, projet);
        tache2 = new Tache(2, "Tâche 2", "Description Tâche 2", 2, null, Status.TERMINÉ, projet);
    }

    @Test
    public void testCreationProjet() {
        // Tester la création du projet
        assertThat(projet.getName()).isEqualTo("Projet Test");
        assertThat(projet.getDescription()).isEqualTo("Description du projet test");
        assertThat(projet.getTasks()).isEmpty(); // Par défaut, la liste des tâches est vide
    }

    @Test
    public void testAjoutTacheAuProjet() {
        // Ajouter une tâche au projet et vérifier
        projet.addTask(tache1);
        projet.addTask(tache2);

        List<Tache> taches = projet.getTasks();
        assertThat(taches).hasSize(2);
        assertThat(taches).contains(tache1, tache2);
    }

    @Test
    public void testCreationTacheSansDeadline() {
        // Vérifier qu'une tâche peut être créée sans deadline
        assertThat(tache1.getDeadline()).isEqualTo("2023-09-30");
        assertThat(tache2.getDeadline()).isNull();
    }

    @Test
    public void testCreationTacheAvecStatut() {
        // Vérifier que le statut de la tâche est bien défini
        assertThat(tache1.getStatus()).isEqualTo(Status.EN_COURS);
        assertThat(tache2.getStatus()).isEqualTo(Status.TERMINÉ);
    }

    @Test
    public void testCreationTacheAvecPrioritéNull() {
        // Créer une tâche sans priorité
        Tache tacheSansPriorite = new Tache(1, "Tâche 3", "Description Tâche 3", null, null, Status.ANNULÉ, projet);

        assertThat(tacheSansPriorite.getPriority()).isNull();
    }

    @Test
    public void testSettersEtGettersTache() {
        // Modifier les valeurs des propriétés de la tâche et vérifier les changements
        tache1.setTitle("Nouveau titre");
        tache1.setDescription("Nouvelle description");
        tache1.setPriority(3);
        tache1.setDeadline("2023-10-10");
        tache1.setStatus(Status.ERREUR);

        assertThat(tache1.getTitle()).isEqualTo("Nouveau titre");
        assertThat(tache1.getDescription()).isEqualTo("Nouvelle description");
        assertThat(tache1.getPriority()).isEqualTo(3);
        assertThat(tache1.getDeadline()).isEqualTo("2023-10-10");
        assertThat(tache1.getStatus()).isEqualTo(Status.ERREUR);
    }

    @Test
    public void testAjoutTacheAuProjetEtVerificationDesIds() {
        // Ajouter des tâches au projet et vérifier les ids générés
        projet.addTask(tache1);
        projet.addTask(tache2);

        assertThat(tache1.getId()).isNotNull();
        assertThat(tache2.getId()).isNotNull();

        assertThat(tache1.getProject().getId()).isEqualTo(projet.getId());
        assertThat(tache2.getProject().getId()).isEqualTo(projet.getId());
    }

    @Test
    public void testModificationProjetDepuisTache() {
        // Modifier le projet d'une tâche
        Projet nouveauProjet = new Projet("Nouveau Projet", "Description Nouveau Projet");
        tache1.setProject(nouveauProjet);

        assertThat(tache1.getProject().getName()).isEqualTo("Nouveau Projet");
        assertThat(tache1.getProject().getId()).isEqualTo(nouveauProjet.getId());
    }

    @Test
    public void testGenerationIdUniquePourLesTachesEtProjets() {
        // Vérifier que chaque projet et chaque tâche a un id unique
        Projet projet2 = new Projet("Deuxième projet", "Description projet 2");
        Tache tache3 = new Tache(0, "Tâche 3", "Description Tâche 3", null, null, Status.ANNULÉ, projet2);

        assertThat(projet2.getId()).isNotEqualTo(projet.getId());
        assertThat(tache3.getId()).isNotEqualTo(tache1.getId());
    }
}
