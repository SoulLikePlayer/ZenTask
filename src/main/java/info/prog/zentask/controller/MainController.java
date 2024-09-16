package info.prog.zentask.controller;

import info.prog.zentask.model.Tache;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.sql.*;

/**
 * Le controller MainController permet l'utilisation d'une base de données local pour afficher les différente tâche et projet
 *
 * @author LOUS Lazare
 * @version 1.0
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
    private TableColumn<Tache, String> statusColumn;
    @FXML
    private TableColumn<Tache, String> projectNameColumn;

    @FXML
    private TextField titleField;
    @FXML
    private TextField descriptionField;
    @FXML
    private TextField priorityField;
    @FXML
    private DatePicker deadlinePicker;
    @FXML
    private ComboBox<String> projectComboBox;

    @FXML
    private Button addButton;
    @FXML
    private Button completeButton;
    @FXML
    private Button deleteButton;

    private ObservableList<Tache> tacheList = FXCollections.observableArrayList();
    private ObservableList<String> projectList = FXCollections.observableArrayList();

    /**
     * Initialise le tableau de visualisation des tâche
     *
     * @see #initialize()
     */
    @FXML
    public void initialize() {
        // Initialisation des colonnes du TableView
        titleColumn.setCellValueFactory(new PropertyValueFactory<>("title"));
        descriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));
        priorityColumn.setCellValueFactory(new PropertyValueFactory<>("priority"));
        deadlineColumn.setCellValueFactory(new PropertyValueFactory<>("deadline"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
        projectNameColumn.setCellValueFactory(new PropertyValueFactory<>("projectName"));

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
                    if ("Terminé".equals(tache.getStatus())) {
                        setStyle("-fx-background-color: #c8e6c9;"); // Fond vert clair
                    } else {
                        setStyle("");
                    }
                }
            }
        });
    }

    /**
     * Charge la base de donné local 'gestionnaire'
     *
     * @see #loadProjects()
     */
    private void loadProjects() {
        projectList.clear();
        String url = "jdbc:sqlite:src/main/resources/info/prog/zentask/database/gestionnaire.db";
        String sql = "SELECT * FROM projects";

        try (Connection conn = DriverManager.getConnection(url);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                String projectName = rs.getString("name");
                projectList.add(projectName);
            }
        } catch (SQLException e) {
            System.out.println("Erreur lors du chargement des projets : " + e.getMessage());
        }

        projectComboBox.setItems(projectList);
    }

    /**
     * Permet d'ajouter une tâche au tableau
     *
     * @see #handleAjouterTache()
     */

    @FXML
    private void handleAjouterTache() {
        String title = titleField.getText();
        String description = descriptionField.getText();
        String priorityText = priorityField.getText().trim();
        String deadline = deadlinePicker.getValue().toString();
        String status = "En Cours";
        String projectName = projectComboBox.getValue();

        if (title.isEmpty() || priorityText.isEmpty() || deadline.isEmpty() || projectName == null) {
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

        int projectId = getProjectIdByName(projectName);
        Tache newTache = new Tache(0, title, description, priority, deadline, status, projectId, projectName);
        boolean addReussie = addTacheToDatabase(newTache);

        if (addReussie) {
            showAlert("Succès", "Tâche ajoutée", "La tâche a été ajoutée avec succès.");
            clearFields();
            loadTasks();
        } else {
            showAlert("Erreur", "Échec de l'ajout", "Une erreur s'est produite lors de l'ajout de la tâche.");
        }
    }

    /**
     * Permet d'obtenir le numéro du projet par le nom de se projet
     *
     * @param projectName
     * @return le numéro (ID) du projet
     * @see #getProjectIdByName(String)
     */

    private int getProjectIdByName(String projectName) {
        String url = "jdbc:sqlite:src/main/resources/info/prog/zentask/database/gestionnaire.db";
        String sql = "SELECT id FROM projects WHERE name = ?";
        int projectId = 0;

        try (Connection conn = DriverManager.getConnection(url);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, projectName);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                projectId = rs.getInt("id");
            }
        } catch (SQLException e) {
            System.out.println("Erreur lors de la récupération de l'ID du projet : " + e.getMessage());
        }

        return projectId;
    }

    /**
     * Permet d'ajouter un projet a la base de donnée
     *
     * @see #handleAjouterTache()
     */

    @FXML
    private void handleAjouterProjet() {
        String newProjectName = showInputDialog("Nouveau Projet", "Nom du nouveau projet :");
        if (newProjectName != null && !newProjectName.isEmpty()) {
            String newProjectDescription = showInputDialog("Description", "Description du projet :");
            if (newProjectDescription == null) {
                newProjectDescription = "";
            }
            if (addProjectToDatabase(newProjectName, newProjectDescription)) {
                loadProjects();
            }
        }
    }

    /**
     * Permet de terminer une tâche
     *
     * @see #handleTerminerTache()
     */

    @FXML
    private void handleTerminerTache() {
        Tache selectedTache = tasksTable.getSelectionModel().getSelectedItem();
        if (selectedTache != null) {
            selectedTache.setStatus("Terminé");
            updateTacheStatus(selectedTache);
            loadTasks();
        }
    }

    /**
     * Permet de supprimer une tâche
     *
     * @see #handleSupprimerTache()
     */

    @FXML
    private void handleSupprimerTache() {
        Tache selectedTache = tasksTable.getSelectionModel().getSelectedItem();
        if (selectedTache != null) {
            deleteTacheFromDatabase(selectedTache);
            loadTasks();
        }
    }

    /**
     * Permet de charger les tâches présentes dans la base de données et de les convertir en objet de classe Tâche
     *
     */

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
                String status = rs.getString("status");
                String projectName = rs.getString("projectName");

                Tache tache = new Tache(id, title, description, priority, deadline, status, 0, projectName);
                tacheList.add(tache);
            }
        } catch (SQLException e) {
            System.out.println("Erreur lors du chargement des tâches : " + e.getMessage());
        }

        tasksTable.setItems(tacheList);
    }

    /**
     * effectue le 'ADD' d'une tâche
     *
     * @param tache
     * @return True = ajout bien effectué | False = ajout mal effectué
     * @see #addTacheToDatabase(Tache)
     */
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
            pstmt.setString(5, tache.getStatus());
            pstmt.setInt(6, tache.getProjectId());
            pstmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.out.println("Erreur lors de l'ajout de la tâche : " + e.getMessage());
            return false;
        }
    }

    /**
     * Permet d'ajouter un nouveaux projet dans la base de données
     *
     * @param projectName
     * @param projectDescription
     * @return
     */
    private boolean addProjectToDatabase(String projectName, String projectDescription) {
        String url = "jdbc:sqlite:src/main/resources/info/prog/zentask/database/gestionnaire.db";
        String sql = "INSERT INTO projects(name, description) VALUES(?, ?)";

        try (Connection conn = DriverManager.getConnection(url);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, projectName);
            pstmt.setString(2, projectDescription);
            pstmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.out.println("Erreur lors de l'ajout du projet : " + e.getMessage());
            return false;
        }
    }

    /**
     * permet de mettre à jour l'état de la tâche
     *
     * @param tache
     */
    private void updateTacheStatus(Tache tache) {
        String url = "jdbc:sqlite:src/main/resources/info/prog/zentask/database/gestionnaire.db";
        String sql = "UPDATE tasks SET status = ? WHERE id = ?";

        try (Connection conn = DriverManager.getConnection(url);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, tache.getStatus());
            pstmt.setInt(2, tache.getId());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Erreur lors de la mise à jour du statut de la tâche : " + e.getMessage());
        }
    }

    /**
     * Effectue le 'DELETE' d'une tâche dans la base de données
     *
     * @param tache
     */
    private void deleteTacheFromDatabase(Tache tache) {
        String url = "jdbc:sqlite:src/main/resources/info/prog/zentask/database/gestionnaire.db";
        String sql = "DELETE FROM tasks WHERE id = ?";

        try (Connection conn = DriverManager.getConnection(url);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, tache.getId());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Erreur lors de la suppression de la tâche : " + e.getMessage());
        }
    }

    private void showAlert(String title, String headerText, String contentText) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(headerText);
        alert.setContentText(contentText);
        alert.showAndWait();
    }

    private String showInputDialog(String title, String message) {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle(title);
        dialog.setHeaderText(null);
        dialog.setContentText(message);

        return dialog.showAndWait().orElse(null);
    }

    private void clearFields() {
        titleField.clear();
        descriptionField.clear();
        priorityField.clear();
        deadlinePicker.setValue(null);
        projectComboBox.getSelectionModel().clearSelection();
    }
}
