package info.prog.zentask.controller.Service;

import info.prog.zentask.model.Projet;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProjectService {
    private static final String DB_URL = "jdbc:sqlite:src/main/resources/info/prog/zentask/database/gestionnaire.db";

    // Méthode pour obtenir tous les projets
    public static List<Projet> getAllProjects() {
        List<Projet> projects = new ArrayList<>();
        String sql = "SELECT * FROM projects";

        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                int id = rs.getInt("id");
                String name = rs.getString("name");
                String description = rs.getString("description");
                Projet projet = new Projet(id, name, description, new ArrayList<>());
                projects.add(projet);
            }
        } catch (SQLException e) {
            System.out.println("Erreur lors du chargement des projets : " + e.getMessage());
        }
        return projects;
    }

    // Méthode pour obtenir un projet par son nom
    public static Projet getProjectByName(String name) {
        String sql = "SELECT * FROM projects WHERE name = ?";
        Projet projet = null;

        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, name);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    int id = rs.getInt("id");
                    String description = rs.getString("description");
                    projet = new Projet(id, name, description, new ArrayList<>());
                }
            }
        } catch (SQLException e) {
            System.out.println("Erreur lors de la récupération du projet : " + e.getMessage());
        }
        return projet;
    }

    // Méthode pour ajouter un projet
    public static boolean addProject(Projet projet) {
        String sql = "INSERT INTO projects(id, name, description) VALUES(?, ?, ?)";

        try (Connection conn = DriverManager.getConnection(DB_URL);
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

    // Méthode pour obtenir le dernier ID de projet
    public static int getMaxProjectId() {
        String sql = "SELECT MAX(id) AS maxId FROM projects";
        int maxId = 0;

        try (Connection conn = DriverManager.getConnection(DB_URL);
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
}
