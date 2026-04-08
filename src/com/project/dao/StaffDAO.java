package com.project.dao;

import com.project.db.DBConnection;
import java.sql.*;

public class StaffDAO {

    public void viewAll() {
        String sql =
            "SELECT st.Staff_ID, st.S_Name, st.S_Phone, st.Salary, st.Address, sa.S_Name AS Sanctuary, " +
            "CASE WHEN v.Staff_ID IS NOT NULL AND r.Staff_ID IS NOT NULL THEN 'Vet + Ranger' " +
            "     WHEN v.Staff_ID IS NOT NULL THEN 'Vet' " +
            "     WHEN r.Staff_ID IS NOT NULL THEN 'Ranger' ELSE 'Other' END AS Role " +
            "FROM Staff st " +
            "JOIN Sanctuary sa ON st.Sanct_ID = sa.Sanct_ID " +
            "LEFT JOIN Vet v ON st.Staff_ID = v.Staff_ID " +
            "LEFT JOIN Ranger r ON st.Staff_ID = r.Staff_ID " +
            "ORDER BY st.Staff_ID";
        try (Statement st = DBConnection.getConnection().createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            System.out.println("\n  ┌────────────────────────────────────────────────────────────────────────┐");
            System.out.println("  │                           ALL STAFF                                  │");
            System.out.println("  └────────────────────────────────────────────────────────────────────────┘");
            System.out.printf("  %-5s  %-18s  %-14s  %-9s  %-15s  %-13s  %-12s%n",
                    "ID","Name","Phone","Salary","Sanctuary","Role","Address");
            System.out.println("  " + "─".repeat(92));
            while (rs.next())
                System.out.printf("  %-5d  %-18s  %-14s  %-9.0f  %-15s  %-13s  %-12s%n",
                        rs.getInt("Staff_ID"), rs.getString("S_Name"),
                        rs.getString("S_Phone"), rs.getFloat("Salary"),
                        rs.getString("Sanctuary"), rs.getString("Role"),
                        rs.getString("Address"));
        } catch (SQLException e) { System.out.println("  Error: " + e.getMessage()); }
    }

    public void showVets() {
        String sql = "SELECT st.Staff_ID, st.S_Name, v.Qualification, v.Specialization, v.V_Contact, " +
                     "sa.S_Name AS Sanctuary FROM Vet v " +
                     "JOIN Staff st ON v.Staff_ID = st.Staff_ID " +
                     "JOIN Sanctuary sa ON st.Sanct_ID = sa.Sanct_ID";
        try (Statement st = DBConnection.getConnection().createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            System.out.println("\n  ┌────────────────────────────────────────────────────┐");
            System.out.println("  │                    ALL VETS                       │");
            System.out.println("  └────────────────────────────────────────────────────┘");
            System.out.printf("  %-5s %-18s %-12s %-14s %-14s %-15s%n",
                    "ID","Name","Qualification","Specialization","Contact","Sanctuary");
            System.out.println("  " + "─".repeat(82));
            while (rs.next())
                System.out.printf("  %-5d %-18s %-12s %-14s %-14s %-15s%n",
                        rs.getInt("Staff_ID"), rs.getString("S_Name"),
                        rs.getString("Qualification"), rs.getString("Specialization"),
                        rs.getString("V_Contact"), rs.getString("Sanctuary"));
        } catch (SQLException e) { System.out.println("  Error: " + e.getMessage()); }
    }

    public void showRangers() {
        String sql = "SELECT st.Staff_ID, st.S_Name, r.Assigned_Area, r.R_Phone, r.Experience, " +
                     "sa.S_Name AS Sanctuary FROM Ranger r " +
                     "JOIN Staff st ON r.Staff_ID = st.Staff_ID " +
                     "JOIN Sanctuary sa ON st.Sanct_ID = sa.Sanct_ID";
        try (Statement st = DBConnection.getConnection().createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            System.out.println("\n  ┌──────────────────────────────────────────────────────┐");
            System.out.println("  │                   ALL RANGERS                       │");
            System.out.println("  └──────────────────────────────────────────────────────┘");
            System.out.printf("  %-5s %-18s %-12s %-14s %-10s %-15s%n",
                    "ID","Name","Area","Phone","Experience","Sanctuary");
            System.out.println("  " + "─".repeat(78));
            while (rs.next())
                System.out.printf("  %-5d %-18s %-12s %-14s %-10d %-15s%n",
                        rs.getInt("Staff_ID"), rs.getString("S_Name"),
                        rs.getString("Assigned_Area"), rs.getString("R_Phone"),
                        rs.getInt("Experience"), rs.getString("Sanctuary"));
        } catch (SQLException e) { System.out.println("  Error: " + e.getMessage()); }
    }
}
