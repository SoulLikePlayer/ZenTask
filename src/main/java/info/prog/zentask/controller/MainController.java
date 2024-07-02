package info.prog.zentask.controller;

import info.prog.zentask.model.Tache;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class MainController {

    @FXML
    private TextField titleField;

    @FXML
    private TextField descriptionField;

    @FXML
    private TextField priorityField;

    @FXML
    private TextField deadlineField;

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
    private Button addButton;

    @FXML
    private void initialize() {
        initializeTableColumns();
        loadTasks();
    }

    private void initializeTableColumns() {
        titleColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getTitle()));
        descriptionColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getDescription()));
        priorityColumn.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getPriority()).asObject());
        deadlineColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getDeadline()));
        statusColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getStatus()));
    }

    @FXML
    private void handleAjouterTache() {
        String title = titleField.getText().trim();
        String description = descriptionField.getText().trim();
        String priorityText = priorityField.getText().trim();
        String deadline = deadlineField.getText().trim();

        if (title.isEmpty() || priorityText.isEmpty() || deadline.isEmpty()) {
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

        Tache nouvelleTache = new Tache(0, title, description, priority, deadline, "À faire");
        boolean ajoutReussi = ajouterTache(nouvelleTache);

        if (ajoutReussi) {
            showAlert("Succès", "Tâche ajoutée", "La tâche a été ajoutée avec succès.");
            clearFields();
            loadTasks();
        } else {
            showAlert("Erreur", "Échec de l'ajout", "Une erreur s'est produite lors de l'ajout de la tâche.");
        }
    }

    private boolean ajouterTache(Tache tache) {
        String url = "jdbc:sqlite:src/main/resources/info/prog/zentask/database/gestionnaire.db";
        String sql = "INSERT INTO tasks(title, description, priority, deadline, status) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = DriverManager.getConnection(url);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, tache.getTitle());
            pstmt.setString(2, tache.getDescription());
            pstmt.setInt(3, tache.getPriority());
            pstmt.setString(4, tache.getDeadline());
            pstmt.setString(5, tache.getStatus());
            pstmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return false;
        }
    }

    private void loadTasks() {
        tasksTable.getItems().clear();

        String url = "jdbc:sqlite:src/main/resources/info/prog/zentask/database/gestionnaire.db";
        String sql = "SELECT * FROM tasks";

        try (Connection conn = DriverManager.getConnection(url);
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            List<Tache> taches = new ArrayList<>();
            while (rs.next()) {
                int id = rs.getInt("id");
                String title = rs.getString("title");
                String description = rs.getString("description");
                int priority = rs.getInt("priority");
                String deadline = rs.getString("deadline");
                String status = rs.getString("status");
                taches.add(new Tache(id, title, description, priority, deadline, status));
            }
            tasksTable.getItems().addAll(taches);

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    private void showAlert(String title, String headerText, String contentText) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(headerText);
        alert.setContentText(contentText);
        alert.showAndWait();
    }

    private void clearFields() {
        titleField.clear();
        descriptionField.clear();
        priorityField.clear();
        deadlineField.clear();
    }
}
