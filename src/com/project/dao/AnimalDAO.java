package com.project.dao;

import com.project.db.DBConnection;
import com.project.model.Animal;
import com.project.model.Mammal;
import com.project.model.Bird;
import com.project.model.Reptile;
import java.sql.*;
import java.util.*;

public class AnimalDAO {

    // ── VIEW ALL ─────────────────────────────────────────────────────────────
    public List<Animal> getAll() {
        List<Animal> list = new ArrayList<>();
        String sql = "SELECT a.A_ID, a.A_Name, a.DOB, " +
                "TIMESTAMPDIFF(YEAR, a.DOB, CURDATE()) AS Age, s.Species_Name, " +
                "m.Fur_Type, b.Wing_Span, " +
                "CASE " +
                "  WHEN m.A_ID IS NOT NULL THEN 'Mammal' " +
                "  WHEN b.A_ID IS NOT NULL THEN 'Bird' " +
                "  WHEN r.A_ID IS NOT NULL THEN 'Reptile' " +
                "  ELSE 'Unknown' END AS CatType " +
                "FROM Animal a JOIN Species s ON a.S_ID = s.S_ID " +
                "LEFT JOIN Mammal m ON a.A_ID = m.A_ID " +
                "LEFT JOIN Bird b ON a.A_ID = b.A_ID " +
                "LEFT JOIN Reptile r ON a.A_ID = r.A_ID " +
                "ORDER BY a.A_ID";
        try (Statement st = DBConnection.getConnection().createStatement();
                ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                Animal a = mapRowToAnimal(rs);
                if (a != null)
                    list.add(a);
            }
        } catch (SQLException e) {
            System.out.println("  Error: " + e.getMessage());
        }
        return list;
    }

    // ── FULL DETAILS ─────────────────────────────────────────────────────────
    public void showDetails(int id) {
        // Basic info + habitat + sanctuary + medical
        String sql = "SELECT a.A_Name, a.DOB, TIMESTAMPDIFF(YEAR,a.DOB,CURDATE()) AS Age, " +
                "s.Species_Name, s.Conservation_Status, s.Is_Poisonous, " +
                "h.Type AS Habitat, h.Climate, h.Area AS HabArea, " +
                "sanc.S_Name AS Sanctuary, sanc.State, " +
                "mr.M_Date, mr.Diagnosis, mr.Treatment " +
                "FROM Animal a " +
                "JOIN Species s ON a.S_ID = s.S_ID " +
                "LEFT JOIN Lives_In li    ON a.A_ID = li.A_ID " +
                "LEFT JOIN Habitat h      ON li.H_ID = h.H_ID " +
                "LEFT JOIN Sanctuary sanc ON h.Sanct_ID = sanc.Sanct_ID " +
                "LEFT JOIN Medical_Record mr  ON a.A_ID = mr.A_ID " +
                "WHERE a.A_ID = ? LIMIT 1";
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (!rs.next()) {
                System.out.println("  No animal with ID " + id);
                return;
            }

            System.out.println("\n  ╔══════════════════════════════════════════╗");
            System.out.println("  ║            ANIMAL FULL DETAILS          ║");
            System.out.println("  ╠══════════════════════════════════════════╣");
            System.out.println("  ║  BASIC INFO                             ║");
            System.out.println("  ╠══════════════════════════════════════════╣");
            prow("Name", rs.getString("A_Name"));
            prow("Date of Birth", "" + rs.getDate("DOB"));
            prow("Age", rs.getInt("Age") + " years");
            prow("Species", rs.getString("Species_Name"));
            prow("Conservation Status", rs.getString("Conservation_Status"));
            prow("Poisonous", rs.getBoolean("Is_Poisonous") ? "YES ⚠" : "No");
            System.out.println("  ╠══════════════════════════════════════════╣");
            System.out.println("  ║  HABITAT & SANCTUARY                    ║");
            System.out.println("  ╠══════════════════════════════════════════╣");
            prow("Habitat Type", nvl(rs.getString("Habitat")));
            prow("Climate", nvl(rs.getString("Climate")));
            prow("Habitat Area", nvl(rs.getString("HabArea")) + " km²");
            prow("Sanctuary", nvl(rs.getString("Sanctuary")));
            prow("State", nvl(rs.getString("State")));
            System.out.println("  ╠══════════════════════════════════════════╣");
            System.out.println("  ║  LAST MEDICAL RECORD                    ║");
            System.out.println("  ╠══════════════════════════════════════════╣");
            prow("Date", nvl(rs.getString("M_Date")));
            prow("Diagnosis", nvl(rs.getString("Diagnosis")));
            prow("Treatment", nvl(rs.getString("Treatment")));
            System.out.println("  ╚══════════════════════════════════════════╝");

        } catch (SQLException e) {
            System.out.println("  Error: " + e.getMessage());
        }

        // Threats
        String tSql = "SELECT t.Description, t.Severity_Level FROM Threat t " +
                "JOIN Faces f ON t.Threat_ID = f.Threat_ID WHERE f.A_ID = ?";
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(tSql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            System.out.println("  ┌──────────────────────────────────────────┐");
            System.out.println("  │  THREATS FACED                          │");
            System.out.println("  └──────────────────────────────────────────┘");
            boolean any = false;
            while (rs.next()) {
                any = true;
                System.out.printf("  ▸ %-30s  Severity: %s%n",
                        rs.getString("Description"), rs.getString("Severity_Level"));
            }
            if (!any)
                System.out.println("  No threats recorded.");
        } catch (SQLException e) {
            System.out.println("  Error: " + e.getMessage());
        }

        // Rescue events
        String rSql = "SELECT re.Re_Date, re.R_Location, re.Reason FROM Rescue_Event re " +
                "JOIN Involved_In ii ON re.Rescue_ID = ii.Rescue_ID WHERE ii.A_ID = ?";
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(rSql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            System.out.println("  ┌──────────────────────────────────────────┐");
            System.out.println("  │  RESCUE EVENTS                          │");
            System.out.println("  └──────────────────────────────────────────┘");
            boolean any = false;
            while (rs.next()) {
                any = true;
                System.out.printf("  ▸ Date: %-12s  Location: %-15s  Reason: %s%n",
                        rs.getDate("Re_Date"), rs.getString("R_Location"), rs.getString("Reason"));
            }
            if (!any)
                System.out.println("  No rescue events recorded.");
        } catch (SQLException e) {
            System.out.println("  Error: " + e.getMessage());
        }

        // Migrations
        String mSql = "SELECT m.Season, m.Start_Location, m.End_Location, m.Distance " +
                "FROM Migration m JOIN Participates_In pi ON m.M_ID = pi.M_ID WHERE pi.A_ID = ?";
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(mSql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            System.out.println("  ┌──────────────────────────────────────────┐");
            System.out.println("  │  MIGRATION HISTORY                      │");
            System.out.println("  └──────────────────────────────────────────┘");
            boolean any = false;
            while (rs.next()) {
                any = true;
                System.out.printf("  ▸ Season: %-8s  Route: %s → %s  (%.1f km)%n",
                        rs.getString("Season"), rs.getString("Start_Location"),
                        rs.getString("End_Location"), rs.getDouble("Distance"));
            }
            if (!any)
                System.out.println("  No migrations recorded.");
        } catch (SQLException e) {
            System.out.println("  Error: " + e.getMessage());
        }
    }

    // ── ASSIGN TO SANCTUARY ──────────────────────────────────────────────────
    public boolean assignToSanctuary(int aId, int sanctId) {
        int hId = -1;
        // Find a valid Habitat ID in that Sanctuary
        String findHabitat = "SELECT H_ID FROM Habitat WHERE Sanct_ID = ? LIMIT 1";
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(findHabitat)) {
            ps.setInt(1, sanctId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    hId = rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            System.out.println("  Error finding relative habitat: " + e.getMessage());
            return false;
        }

        if (hId == -1) {
            System.out.println("  Sanctuary strongly requires a habitat to link animals.");
            return false;
        }

        String sql = "INSERT INTO Lives_In (A_ID, H_ID) VALUES (?, ?)";
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql)) {
            ps.setInt(1, aId);
            ps.setInt(2, hId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("  Error linking animal to sanctuary: " + e.getMessage());
        }
        return false;
    }

    // ── ADD SPECIES ──────────────────────────────────────────────────────────
    public int addSpecies(String name, String status, boolean isPoisonous) {
        int newId = 1;
        try (Statement st = DBConnection.getConnection().createStatement();
             ResultSet rs = st.executeQuery("SELECT COALESCE(MAX(S_ID), 500) + 1 FROM Species")) {
            if (rs.next()) {
                newId = rs.getInt(1);
            }
        } catch (SQLException e) {
            System.out.println("  Error getting new S_ID: " + e.getMessage());
            return -1;
        }

        String sql = "INSERT INTO Species (S_ID, Species_Name, Conservation_Status, Is_Poisonous) VALUES (?, ?, ?, ?)";
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql)) {
            ps.setInt(1, newId);
            ps.setString(2, name);
            ps.setString(3, status);
            ps.setBoolean(4, isPoisonous);
            int rows = ps.executeUpdate();
            if (rows > 0) return newId;
        } catch (SQLException e) {
            System.out.println("  Error adding species: " + e.getMessage());
        }
        return -1;
    }

    // ── ADD ──────────────────────────────────────────────────────────────────
    public boolean add(Animal a) {
        String sql = "INSERT INTO Animal (A_ID, A_Name, DOB, Age, S_ID) VALUES (?,?,?,?,?)";
        try (Connection conn = DBConnection.getConnection()) {
            conn.setAutoCommit(false);
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, a.getId());
                ps.setString(2, a.getName());
                ps.setDate(3, a.getDob());
                ps.setInt(4, a.getAge());
                ps.setInt(5, a.getSpeciesId());
                ps.executeUpdate();

                if (a instanceof Mammal) {
                    Mammal m = (Mammal) a;
                    PreparedStatement pm = conn.prepareStatement("INSERT INTO Mammal (A_ID, Fur_Type) VALUES (?,?)");
                    pm.setInt(1, a.getId());
                    pm.setString(2, nvl(m.getFurType()));
                    pm.executeUpdate();
                } else if (a instanceof Bird) {
                    Bird b = (Bird) a;
                    PreparedStatement pb = conn.prepareStatement("INSERT INTO Bird (A_ID, Wing_Span) VALUES (?,?)");
                    pb.setInt(1, a.getId());
                    pb.setDouble(2, b.getWingSpan());
                    pb.executeUpdate();
                } else if (a instanceof Reptile) {
                    PreparedStatement pr = conn.prepareStatement("INSERT INTO Reptile (A_ID) VALUES (?)");
                    pr.setInt(1, a.getId());
                    pr.executeUpdate();
                }
                conn.commit();
                return true;
            } catch (SQLException e) {
                conn.rollback();
                System.out.println("  Insert failed: " + e.getMessage());
                return false;
            }
        } catch (SQLException e) {
            System.out.println("  DB error: " + e.getMessage());
            return false;
        }
    }

    // ── UPDATE ───────────────────────────────────────────────────────────────
    public boolean update(int id, String field, String value) {
        String col = switch (field) {
            case "1" -> "A_Name";
            case "2" -> "DOB";
            case "3" -> "S_ID";
            default -> null;
        };
        if (col == null) {
            System.out.println("  Invalid choice.");
            return false;
        }
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(
                "UPDATE Animal SET " + col + " = ? WHERE A_ID = ?")) {
            ps.setString(1, value);
            ps.setInt(2, id);
            if (ps.executeUpdate() == 0) {
                System.out.println("  Animal not found.");
                return false;
            }
            if ("2".equals(field)) { // recalc age if DOB changed
                PreparedStatement pa = DBConnection.getConnection().prepareStatement(
                        "UPDATE Animal SET Age = TIMESTAMPDIFF(YEAR, DOB, CURDATE()) WHERE A_ID = ?");
                pa.setInt(1, id);
                pa.executeUpdate();
            }
            return true;
        } catch (SQLException e) {
            System.out.println("  Update failed: " + e.getMessage());
            return false;
        }
    }

    // ── DELETE (cascade) ─────────────────────────────────────────────────────
    public boolean delete(int id) {
        if (getById(id) == null) {
            System.out.println("  Animal not found.");
            return false;
        }
        String[] children = { "Mammal", "Bird", "Reptile", "Faces",
                "Involved_In", "Medical_Record", "Lives_In", "Monitors",
                "Treats", "Participates_In" };
        Connection conn = DBConnection.getConnection();
        try {
            conn.setAutoCommit(false);
            for (String t : children) {
                PreparedStatement ps = conn.prepareStatement("DELETE FROM " + t + " WHERE A_ID = ?");
                ps.setInt(1, id);
                ps.executeUpdate();
            }
            PreparedStatement ps = conn.prepareStatement("DELETE FROM Animal WHERE A_ID = ?");
            ps.setInt(1, id);
            ps.executeUpdate();
            conn.commit();
            return true;
        } catch (SQLException e) {
            try {
                conn.rollback();
            } catch (SQLException ignored) {
            }
            System.out.println("  Delete failed: " + e.getMessage());
            return false;
        } finally {
            try {
                conn.setAutoCommit(true);
            } catch (SQLException ignored) {
            }
        }
    }

    // ── HELPERS ──────────────────────────────────────────────────────────────
    private Animal mapRowToAnimal(ResultSet rs) throws SQLException {
        Animal a = null;
        String type = rs.getString("CatType");
        if ("Mammal".equals(type)) {
            Mammal m = new Mammal();
            m.setFurType(rs.getString("Fur_Type"));
            a = m;
        } else if ("Bird".equals(type)) {
            Bird b = new Bird();
            b.setWingSpan(rs.getDouble("Wing_Span"));
            a = b;
        } else if ("Reptile".equals(type)) {
            a = new Reptile();
        } else {
            a = new Mammal();
        }
        a.setId(rs.getInt("A_ID"));
        a.setName(rs.getString("A_Name"));
        a.setDob(rs.getDate("DOB"));
        try {
            a.setAge(rs.getInt("Age"));
        } catch (SQLException ignored) {
        }
        try {
            a.setSpeciesName(rs.getString("Species_Name"));
        } catch (SQLException ignored) {
        }
        return a;
    }

    public Animal getById(int id) {
        String sql = "SELECT a.*, TIMESTAMPDIFF(YEAR, a.DOB, CURDATE()) AS CalcAge, s.Species_Name, " +
                "m.Fur_Type, b.Wing_Span, " +
                "CASE " +
                "  WHEN m.A_ID IS NOT NULL THEN 'Mammal' " +
                "  WHEN b.A_ID IS NOT NULL THEN 'Bird' " +
                "  WHEN r.A_ID IS NOT NULL THEN 'Reptile' " +
                "  ELSE 'Unknown' END AS CatType " +
                "FROM Animal a JOIN Species s ON a.S_ID = s.S_ID " +
                "LEFT JOIN Mammal m ON a.A_ID = m.A_ID " +
                "LEFT JOIN Bird b ON a.A_ID = b.A_ID " +
                "LEFT JOIN Reptile r ON a.A_ID = r.A_ID " +
                "WHERE a.A_ID = ?";
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                Animal a = mapRowToAnimal(rs);
                try {
                    a.setSpeciesId(rs.getInt("S_ID"));
                } catch (Exception ignored) {
                }
                try {
                    a.setAge(rs.getInt("CalcAge"));
                } catch (Exception ignored) {
                }
                return a;
            }
        } catch (SQLException e) {
            System.out.println("  Error: " + e.getMessage());
        }
        return null;
    }

    public int getSpeciesIdByName(String name) {
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(
                "SELECT S_ID FROM Species WHERE Species_Name = ?")) {
            ps.setString(1, name);
            ResultSet rs = ps.executeQuery();
            if (rs.next())
                return rs.getInt("S_ID");
        } catch (SQLException e) {
            System.out.println("  Error: " + e.getMessage());
        }
        return -1;
    }

    public void listSpecies() {
        try (Statement st = DBConnection.getConnection().createStatement();
                ResultSet rs = st
                        .executeQuery("SELECT S_ID, Species_Name, Conservation_Status FROM Species ORDER BY S_ID")) {
            System.out.println("\n  Available Species:");
            System.out.printf("  %-5s %-20s %-15s%n", "ID", "Name", "Status");
            System.out.println("  " + "─".repeat(42));
            while (rs.next())
                System.out.printf("  %-5d %-20s %-15s%n",
                        rs.getInt("S_ID"), rs.getString("Species_Name"), rs.getString("Conservation_Status"));
        } catch (SQLException e) {
            System.out.println("  Error: " + e.getMessage());
        }
    }

    // Search by name
    public List<Animal> searchByName(String keyword) {
        List<Animal> list = new ArrayList<>();
        String sql = "SELECT a.A_ID, a.A_Name, a.DOB, " +
                "TIMESTAMPDIFF(YEAR,a.DOB,CURDATE()) AS Age, s.Species_Name, " +
                "m.Fur_Type, b.Wing_Span, " +
                "CASE " +
                "  WHEN m.A_ID IS NOT NULL THEN 'Mammal' " +
                "  WHEN b.A_ID IS NOT NULL THEN 'Bird' " +
                "  WHEN r.A_ID IS NOT NULL THEN 'Reptile' " +
                "  ELSE 'Unknown' END AS CatType " +
                "FROM Animal a JOIN Species s ON a.S_ID = s.S_ID " +
                "LEFT JOIN Mammal m ON a.A_ID = m.A_ID " +
                "LEFT JOIN Bird b ON a.A_ID = b.A_ID " +
                "LEFT JOIN Reptile r ON a.A_ID = r.A_ID " +
                "WHERE a.A_Name LIKE ? ORDER BY a.A_ID";
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql)) {
            ps.setString(1, "%" + keyword + "%");
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Animal a = mapRowToAnimal(rs);
                if (a != null)
                    list.add(a);
            }
        } catch (SQLException e) {
            System.out.println("  Error: " + e.getMessage());
        }
        return list;
    }

    private void prow(String label, String value) {
        System.out.printf("  ║  %-18s: %-22s║%n", label, value);
    }

    private String nvl(String s) {
        return s != null ? s : "N/A";
    }
}
