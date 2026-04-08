package com.project.dao;

import com.project.db.DBConnection;
import java.sql.*;

public class SanctuaryDAO {

    public void viewAll() {
        String sql = "SELECT * FROM Sanctuary ORDER BY Sanct_ID";
        try (Statement st = DBConnection.getConnection().createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            System.out.println("\n  ┌──────────────────────────────────────────────────────────────┐");
            System.out.println("  │                    ALL SANCTUARIES                          │");
            System.out.println("  └──────────────────────────────────────────────────────────────┘");
            System.out.printf("  %-4s  %-18s  %-14s  %-12s  %-18s  %-8s  %-6s%n",
                    "ID", "Name", "State", "City", "Location", "Area km²", "Est.");
            System.out.println("  " + "─".repeat(86));
            while (rs.next())
                System.out.printf("  %-4d  %-18s  %-14s  %-12s  %-18s  %-8.0f  %-6d%n",
                        rs.getInt("Sanct_ID"), rs.getString("S_Name"),
                        rs.getString("State"), rs.getString("City"),
                        rs.getString("S_Location"), rs.getFloat("S_Area"),
                        rs.getInt("Established_Year"));
        } catch (SQLException e) { System.out.println("  Error: " + e.getMessage()); }
    }

    public void showDetails(int id) {
        // Sanctuary info
        String sql = "SELECT * FROM Sanctuary WHERE Sanct_ID = ?";
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql)) {
            ps.setInt(1, id); ResultSet rs = ps.executeQuery();
            if (!rs.next()) { System.out.println("  No sanctuary with ID " + id); return; }
            System.out.println("\n  ╔══════════════════════════════════════╗");
            System.out.println("  ║       SANCTUARY DETAILS             ║");
            System.out.println("  ╠══════════════════════════════════════╣");
            System.out.printf("  ║  %-15s: %-18s║%n", "Name",     rs.getString("S_Name"));
            System.out.printf("  ║  %-15s: %-18s║%n", "State",    rs.getString("State"));
            System.out.printf("  ║  %-15s: %-18s║%n", "City",     rs.getString("City"));
            System.out.printf("  ║  %-15s: %-18s║%n", "Location", rs.getString("S_Location"));
            System.out.printf("  ║  %-15s: %-18.0f║%n","Area km²", rs.getFloat("S_Area"));
            System.out.printf("  ║  %-15s: %-18d║%n", "Est. Year",rs.getInt("Established_Year"));
            System.out.println("  ╚══════════════════════════════════════╝");
        } catch (SQLException e) { System.out.println("  Error: " + e.getMessage()); }

        // Animals in this sanctuary (via habitat)
        String aSql = "SELECT a.A_ID, a.A_Name, s.Species_Name, " +
                      "TIMESTAMPDIFF(YEAR,a.DOB,CURDATE()) AS Age " +
                      "FROM Animal a JOIN Species s ON a.S_ID = s.S_ID " +
                      "JOIN Lives_In li ON a.A_ID = li.A_ID " +
                      "JOIN Habitat h ON li.H_ID = h.H_ID WHERE h.Sanct_ID = ?";
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(aSql)) {
            ps.setInt(1, id); ResultSet rs = ps.executeQuery();
            System.out.println("  ┌──────────────────────────────────────┐");
            System.out.println("  │  ANIMALS IN THIS SANCTUARY          │");
            System.out.println("  └──────────────────────────────────────┘");
            System.out.printf("  %-5s %-15s %-15s %-5s%n", "ID","Name","Species","Age");
            System.out.println("  " + "─".repeat(44));
            boolean any = false;
            while (rs.next()) {
                any = true;
                System.out.printf("  %-5d %-15s %-15s %-5d%n",
                        rs.getInt("A_ID"), rs.getString("A_Name"),
                        rs.getString("Species_Name"), rs.getInt("Age"));
            }
            if (!any) System.out.println("  No animals linked.");
        } catch (SQLException e) { System.out.println("  Error: " + e.getMessage()); }

        // Staff
        String stSql = "SELECT Staff_ID, S_Name, S_Phone, Salary FROM Staff WHERE Sanct_ID = ?";
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(stSql)) {
            ps.setInt(1, id); ResultSet rs = ps.executeQuery();
            System.out.println("  ┌──────────────────────────────────────┐");
            System.out.println("  │  STAFF                              │");
            System.out.println("  └──────────────────────────────────────┘");
            System.out.printf("  %-5s %-18s %-14s %-8s%n","ID","Name","Phone","Salary");
            System.out.println("  " + "─".repeat(48));
            boolean any = false;
            while (rs.next()) {
                any = true;
                System.out.printf("  %-5d %-18s %-14s %-8.0f%n",
                        rs.getInt("Staff_ID"), rs.getString("S_Name"),
                        rs.getString("S_Phone"), rs.getFloat("Salary"));
            }
            if (!any) System.out.println("  No staff linked.");
        } catch (SQLException e) { System.out.println("  Error: " + e.getMessage()); }

        // Conservation programs
        String cpSql = "SELECT Program_ID, Prog_Name, Start_Date, End_Date, Budget " +
                       "FROM Conservation_Program WHERE Sanct_ID = ?";
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(cpSql)) {
            ps.setInt(1, id); ResultSet rs = ps.executeQuery();
            System.out.println("  ┌──────────────────────────────────────┐");
            System.out.println("  │  CONSERVATION PROGRAMS              │");
            System.out.println("  └──────────────────────────────────────┘");
            System.out.printf("  %-5s %-20s %-12s %-12s %-10s%n","ID","Program","Start","End","Budget");
            System.out.println("  " + "─".repeat(62));
            boolean any = false;
            while (rs.next()) {
                any = true;
                System.out.printf("  %-5d %-20s %-12s %-12s %-10.0f%n",
                        rs.getInt("Program_ID"), rs.getString("Prog_Name"),
                        rs.getDate("Start_Date"), rs.getDate("End_Date"),
                        rs.getFloat("Budget"));
            }
            if (!any) System.out.println("  No programs.");
        } catch (SQLException e) { System.out.println("  Error: " + e.getMessage()); }
    }
}
