package info.prog.zentask.controller.Service;

import info.prog.zentask.model.Status;
import info.prog.zentask.model.Tache;
import info.prog.zentask.model.Projet;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TaskService {
    private static final String DB_URL = "jdbc:sqlite:src/main/resources/info/prog/zentask/database/gestionnaire.db";

    // Méthode pour obtenir toutes les tâches
    public List<Tache> getAllTasks() {
        List<Tache> tasks = new ArrayList<>();
        String sql = "SELECT t.*, p.name AS projectName FROM tasks t LEFT JOIN projects p ON t.projectId = p.id";

        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                int id = rs.getInt("id");
                String title = rs.getString("title");
                String description = rs.getString("description");
                int priority = rs.getInt("priority");
                String deadline = rs.getString("deadline");
                Status status = Status.valueOf(rs.getString("status"));
                String projectName = rs.getString("projectName");

                Projet project = ProjectService.getProjectByName(projectName); // Assurez-vous que cette méthode existe dans ProjectService

                Tache tache = new Tache(id, title, description, priority, deadline, status, project);
                tasks.add(tache);
            }
        } catch (SQLException e) {
            System.out.println("Erreur lors du chargement des tâches : " + e.getMessage());
        }
        return tasks;
    }

    // Méthode pour ajouter une tâche
    public boolean addTask(Tache tache) {
        String sql = "INSERT INTO tasks(title, description, priority, deadline, status, projectId) VALUES(?, ?, ?, ?, ?, ?)";

        try (Connection conn = DriverManager.getConnection(DB_URL);
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

    // Méthode pour mettre à jour le statut d'une tâche
    public boolean updateTaskStatus(Tache tache) {
        String sql = "UPDATE tasks SET status = ? WHERE id = ?";

        try (Connection conn = DriverManager.getConnection(DB_URL);
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

    // Méthode pour supprimer une tâche
    public boolean deleteTask(Tache tache) {
        String sql = "DELETE FROM tasks WHERE id = ?";

        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, tache.getId());
            pstmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.out.println("Erreur lors de la suppression de la tâche : " + e.getMessage());
            return false;
        }
    }
}
