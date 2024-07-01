package info.prog.zentask.controller;

import info.prog.zentask.model.Tache;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.skin.TableColumnHeader;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class MainController {

    @FXML
    private TableView<Tache> tasksTable;

    public void initialize() {
        initializeDatabase();
        loadTasks();
    }

    private void initializeDatabase() {
        // Vous pouvez ajouter ici le code pour initialiser la connexion à la base de données si nécessaire
        // Assurez-vous que votre gestionnaire de base de données est configuré correctement
    }

    private void loadTasks() {
        try (Connection conn = DriverManager.getConnection("jdbc:sqlite:src/main/resources/database/gestionnaire.db");
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM tasks")) {

            while (rs.next()) {
                Tache task = new Tache(
                        rs.getInt("id"),
                        rs.getString("title"),
                        rs.getString("description"),
                        rs.getInt("priority"),
                        rs.getString("deadline"),
                        rs.getString("status")
                );
                tasksTable.getItems().add(task);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}