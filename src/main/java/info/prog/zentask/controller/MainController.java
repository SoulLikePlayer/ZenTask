package info.prog.zentask.controller;

import info.prog.zentask.controller.Service.*;

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

    private TaskService taskService;
    private ProjectService projectService;

    @FXML
    public void initialize() {
        taskService = new TaskService();
        projectService = new ProjectService();

        setupTableView();
        loadTasks();
        loadProjects();

        setupEventHandlers();
    }

    private void setupTableView() {
        titleColumn.setCellValueFactory(new PropertyValueFactory<>("title"));
        descriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));
        priorityColumn.setCellValueFactory(new PropertyValueFactory<>("priority"));
        deadlineColumn.setCellValueFactory(new PropertyValueFactory<>("deadline"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
        projectNameColumn.setCellValueFactory(new PropertyValueFactory<>("project"));

        tasksTable.setItems(tacheList);
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

    private void loadTasks() {
        tacheList.setAll(taskService.getAllTasks());
    }

    private void loadProjects() {
        projectList.setAll(projectService.getAllProjects());
        projectComboBox.setItems(projectList);
    }

    @FXML
    private void handleAjouterTache() {
        String title = titleField.getText();
        String description = descriptionField.getText();
        String priorityText = priorityField.getText().trim();
        String deadline = deadlinePicker.getValue() != null ? deadlinePicker.getValue().toString() : null;
        Status status = Status.EN_COURS;
        Projet selectedProject = projectComboBox.getValue();

        if (title.isEmpty() || description.isEmpty() || priorityText.isEmpty()) {
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
        boolean addReussie = taskService.addTask(newTache);

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
            int newProjectId = projectService.getMaxProjectId() + 1;
            Projet newProjet = new Projet(newProjectId, newProjectName, newProjectDescription, new ArrayList<>());
            if (projectService.addProject(newProjet)) {
                loadProjects();
            }
        }
    }

    @FXML
    private void handleTerminerTache() {
        Tache selectedTache = tasksTable.getSelectionModel().getSelectedItem();
        if (selectedTache != null) {
            selectedTache.setStatus(Status.TERMINÉ);
            if (taskService.updateTaskStatus(selectedTache)) {
                loadTasks();
            }
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
                if (taskService.deleteTask(selectedTache)) {
                    showAlert("Succès", "Tâche supprimée", "La tâche a été supprimée avec succès.");
                    loadTasks();
                } else {
                    showAlert("Erreur", "Échec de la suppression", "Une erreur s'est produite lors de la suppression de la tâche.");
                }
            }
        }
    }

    private void setupEventHandlers() {
        tasksTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            boolean selected = newSelection != null;
            deleteButton.setDisable(!selected);
            completeButton.setDisable(!selected);
        });
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
