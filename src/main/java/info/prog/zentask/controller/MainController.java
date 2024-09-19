package info.prog.zentask.controller;

import info.prog.zentask.model.Projet;
import info.prog.zentask.model.Status;
import info.prog.zentask.model.Tache;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.Optional;

/**
 * Le controller MainController permet l'utilisation d'une base de données locale pour afficher les différentes tâches et projets.
 */
public class MainController {
    @FXML
    private TableView<Tache> tasksTable;
    @FXML
    private TableColumn<Tache, String> titleColumn;
    @FXML
    private TableColumn<Tache, String> descriptionColumn;
    @FXML
    private TableColumn<Tache, Integer> priorityColumn;
    @FXML
    private TableColumn<Tache, String> deadlineColumn;
    @FXML
    private TableColumn<Tache, Status> statusColumn;
    @FXML
    private TableColumn<Tache, Projet> projectNameColumn;

    @FXML
    private TextField titleField;
    @FXML
    private TextField descriptionField;
    @FXML
    private TextField priorityField;
    @FXML
    private DatePicker deadlinePicker;
    @FXML
    private ComboBox<Projet> projectComboBox;

    @FXML
    private Button addButton;
    @FXML
    private Button completeButton;
    @FXML
    private Button deleteButton;

    private ObservableList<Tache> tacheList = FXCollections.observableArrayList();
    private ObservableList<Projet> projectList = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        // Initialisation des colonnes du TableView
        titleColumn.setCellValueFactory(new PropertyValueFactory<>("title"));
        descriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));
        priorityColumn.setCellValueFactory(new PropertyValueFactory<>("priority"));
        deadlineColumn.setCellValueFactory(new PropertyValueFactory<>("deadline"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
        projectNameColumn.setCellValueFactory(new PropertyValueFactory<>("project"));

        loadTasks(); // Charger les tâches depuis la base de données
        loadProjects(); // Charger les projets depuis la base de données

        // Désactiver les boutons "Terminer" et "Supprimer" par défaut
        completeButton.setDisable(true);
        deleteButton.setDisable(true);

        // Désactiver les boutons si aucune tâche n'est sélectionnée
        tasksTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            boolean selected = newSelection != null;
            deleteButton.setDisable(!selected);
            completeButton.setDisable(!selected);
        });

        // Appliquer un style différent aux lignes selon le statut de la tâche
        tasksTable.setRowFactory(tv -> new TableRow<Tache>() {
            @Override
            protected void updateItem(Tache tache, boolean empty) {
                super.updateItem(tache, empty);
                if (tache == null || empty) {
                    setStyle("");
                } else {
                    if (Status.TERMINÉ.equals(tache.getStatus())) {
                        setStyle("-fx-background-color: #c8e6c9;"); // Fond vert clair
                    } else {
                        setStyle("");
                    }
                }
            }
        });
    }

    private void loadProjects() {
        projectList.clear();
        String url = "jdbc:sqlite:src/main/resources/info/prog/zentask/database/gestionnaire.db";
        String sql = "SELECT * FROM projects";

        try (Connection conn = DriverManager.getConnection(url);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                int id = rs.getInt("id");
                String name = rs.getString("name");
                String description = rs.getString("description");
                Projet projet = new Projet(id, name, description, new ArrayList<>());
                projectList.add(projet);
            }
        } catch (SQLException e) {
            System.out.println("Erreur lors du chargement des projets : " + e.getMessage());
        }

        projectComboBox.setItems(projectList);
    }

    @FXML
    private void handleAjouterTache() {
        String title = titleField.getText();
        String description = descriptionField.getText();
        String priorityText = priorityField.getText().trim();
        String deadline = null;
        try {
            deadline = deadlinePicker.getValue().toString();
        } catch (Exception e) {
            deadline = null;
        }
        Status status = Status.EN_COURS;
        Projet selectedProject = projectComboBox.getValue();

        if (title.isEmpty() || priorityText.isEmpty() || description.isEmpty()) {
            showAlert("Erreur", "Champs vides", "Veuillez remplir tous les champs.");
            return;
        }

        int priority;
        try {
            priority = Integer.parseInt(priorityText);
        } catch (NumberFormatException e) {
            showAlert("Erreur", "Format de priorité incorrect", "La priorité doit être un nombre entier.");
            return;
        }

        Tache newTache = new Tache(title, description, priority, deadline, status, selectedProject);
        boolean addReussie = addTacheToDatabase(newTache);

        if (addReussie) {
            showAlert("Succès", "Tâche ajoutée", "La tâche a été ajoutée avec succès.");
            clearFields();
            loadTasks();
        } else {
            showAlert("Erreur", "Échec de l'ajout", "Une erreur s'est produite lors de l'ajout de la tâche.");
        }
    }

    @FXML
    private void handleAjouterProjet() {
        String newProjectName = showInputDialog("Nouveau Projet", "Nom du nouveau projet :");
        if (newProjectName != null && !newProjectName.isEmpty()) {
            String newProjectDescription = showInputDialog("Description", "Description du projet :");
            if (newProjectDescription == null) {
                newProjectDescription = "";
            }
            int newProjectId = getMaxProjectId() + 1;
            Projet newProjet = new Projet(newProjectId, newProjectName, newProjectDescription, new ArrayList<>());
            if (addProjectToDatabase(newProjet)) {
                loadProjects();
            }
        }
    }

    @FXML
    private void handleTerminerTache() {
        Tache selectedTache = tasksTable.getSelectionModel().getSelectedItem();
        if (selectedTache != null) {
            selectedTache.setStatus(Status.TERMINÉ);
            updateTacheStatus(selectedTache);
            loadTasks();
        }
    }

    @FXML
    private void handleSupprimerTache() {
        Tache selectedTache = tasksTable.getSelectionModel().getSelectedItem();
        if (selectedTache != null) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Confirmation de suppression");
            alert.setHeaderText("Êtes-vous sûr de vouloir supprimer cette tâche ?");
            alert.setContentText("La tâche sera définitivement supprimée.");

            Optional<ButtonType> result = alert.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                if (deleteTacheFromDatabase(selectedTache)) {
                    showAlert("Succès", "Tâche supprimée", "La tâche a été supprimée avec succès.");
                    loadTasks(); // Recharger la liste des tâches après suppression
                } else {
                    showAlert("Erreur", "Échec de la suppression", "Une erreur s'est produite lors de la suppression de la tâche.");
                }
            }
        }
    }

    private void loadTasks() {
        tacheList.clear();
        String url = "jdbc:sqlite:src/main/resources/info/prog/zentask/database/gestionnaire.db";
        String sql = "SELECT t.*, p.name AS projectName FROM tasks t LEFT JOIN projects p ON t.projectId = p.id";

        try (Connection conn = DriverManager.getConnection(url);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                int id = rs.getInt("id");
                String title = rs.getString("title");
                String description = rs.getString("description");
                int priority = rs.getInt("priority");
                String deadline = rs.getString("deadline");
                String statusStr = rs.getString("status");
                Status status;
                try {
                    status = Status.valueOf(statusStr);
                } catch (IllegalArgumentException e) {
                    status = Status.ERREUR; // ou une autre valeur par défaut
                }

                String projectName = rs.getString("projectName");

                // Rechercher le projet associé pour obtenir l'objet Projet
                Projet project = projectList.stream()
                        .filter(proj -> proj.getName().equals(projectName))
                        .findFirst()
                        .orElse(null);

                Tache tache = new Tache(id, title, description, priority, deadline, status, project); // Assurez-vous d'avoir un constructeur approprié
                tacheList.add(tache);
            }
        } catch (SQLException e) {
            System.out.println("Erreur lors du chargement des tâches : " + e.getMessage());
        }

        tasksTable.setItems(tacheList);
    }


    private boolean addTacheToDatabase(Tache tache) {
        String url = "jdbc:sqlite:src/main/resources/info/prog/zentask/database/gestionnaire.db";
        String sql = "INSERT INTO tasks(title, description, priority, deadline, status, projectId) " +
                "VALUES(?, ?, ?, ?, ?, ?)";

        try (Connection conn = DriverManager.getConnection(url);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, tache.getTitle());
            pstmt.setString(2, tache.getDescription());
            pstmt.setInt(3, tache.getPriority());
            pstmt.setString(4, tache.getDeadline());
            pstmt.setString(5, tache.getStatus().name());
            pstmt.setObject(6, tache.getProject() != null ? tache.getProject().getId() : null);

            pstmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.out.println("Erreur lors de l'ajout de la tâche : " + e.getMessage());
            return false;
        }
    }

    private boolean addProjectToDatabase(Projet projet) {
        String url = "jdbc:sqlite:src/main/resources/info/prog/zentask/database/gestionnaire.db";
        String sql = "INSERT INTO projects(id, name, description) VALUES(?, ?, ?)";

        try (Connection conn = DriverManager.getConnection(url);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, projet.getId());
            pstmt.setString(2, projet.getName());
            pstmt.setString(3, projet.getDescription());

            pstmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.out.println("Erreur lors de l'ajout du projet : " + e.getMessage());
            return false;
        }
    }

    private boolean updateTacheStatus(Tache tache) {
        String url = "jdbc:sqlite:src/main/resources/info/prog/zentask/database/gestionnaire.db";
        String sql = "UPDATE tasks SET status = ? WHERE id = ?";

        try (Connection conn = DriverManager.getConnection(url);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, tache.getStatus().name());
            pstmt.setInt(2, tache.getId());

            pstmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.out.println("Erreur lors de la mise à jour de la tâche : " + e.getMessage());
            return false;
        }
    }

    private boolean deleteTacheFromDatabase(Tache tache) {
        String url = "jdbc:sqlite:src/main/resources/info/prog/zentask/database/gestionnaire.db";
        String sql = "DELETE FROM tasks WHERE id = ?";

        try (Connection conn = DriverManager.getConnection(url);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, tache.getId());
            pstmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.out.println("Erreur lors de la suppression de la tâche : " + e.getMessage());
            return false;
        }
    }

    private int getMaxProjectId() {
        String url = "jdbc:sqlite:src/main/resources/info/prog/zentask/database/gestionnaire.db";
        String sql = "SELECT MAX(id) AS maxId FROM projects";
        int maxId = 0;

        try (Connection conn = DriverManager.getConnection(url);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            if (rs.next()) {
                maxId = rs.getInt("maxId");
            }
        } catch (SQLException e) {
            System.out.println("Erreur lors de la récupération de l'id maximum des projets : " + e.getMessage());
        }

        return maxId;
    }

    private void clearFields() {
        titleField.clear();
        descriptionField.clear();
        priorityField.clear();
        deadlinePicker.setValue(null);
        projectComboBox.setValue(null);
    }

    private void showAlert(String title, String headerText, String contentText) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(headerText);
        alert.setContentText(contentText);
        alert.showAndWait();
    }

    private String showInputDialog(String title, String headerText) {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle(title);
        dialog.setHeaderText(headerText);
        Optional<String> result = dialog.showAndWait();
        return result.orElse(null);
    }
}
