package com.project.dao;

import com.project.db.DBConnection;
import java.sql.*;

public class MedicalDAO {

    public void viewAll() {
        String sql = "SELECT mr.Record_ID, a.A_Name, mr.M_Date, mr.Diagnosis, mr.Treatment " +
                     "FROM Medical_Record mr JOIN Animal a ON mr.A_ID = a.A_ID ORDER BY mr.M_Date DESC";
        try (Statement st = DBConnection.getConnection().createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            System.out.println("\n  ┌──────────────────────────────────────────────────────────────┐");
            System.out.println("  │                  ALL MEDICAL RECORDS                        │");
            System.out.println("  └──────────────────────────────────────────────────────────────┘");
            System.out.printf("  %-5s %-15s %-12s %-20s %-20s%n",
                    "ID","Animal","Date","Diagnosis","Treatment");
            System.out.println("  " + "─".repeat(75));
            boolean any = false;
            while (rs.next()) {
                any = true;
                System.out.printf("  %-5d %-15s %-12s %-20s %-20s%n",
                        rs.getInt("Record_ID"), rs.getString("A_Name"),
                        rs.getDate("M_Date"), rs.getString("Diagnosis"),
                        rs.getString("Treatment"));
            }
            if (!any) System.out.println("  No records found.");
        } catch (SQLException e) { System.out.println("  Error: " + e.getMessage()); }
    }

    public void viewForAnimal(int animalId) {
        String sql = "SELECT mr.Record_ID, mr.M_Date, mr.Diagnosis, mr.Treatment " +
                     "FROM Medical_Record mr WHERE mr.A_ID = ? ORDER BY mr.M_Date DESC";
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql)) {
            ps.setInt(1, animalId); ResultSet rs = ps.executeQuery();
            System.out.println("\n  Medical Records for Animal ID " + animalId + ":");
            System.out.println("  " + "─".repeat(60));
            System.out.printf("  %-5s %-12s %-20s %-20s%n","ID","Date","Diagnosis","Treatment");
            System.out.println("  " + "─".repeat(60));
            boolean any = false;
            while (rs.next()) {
                any = true;
                System.out.printf("  %-5d %-12s %-20s %-20s%n",
                        rs.getInt("Record_ID"), rs.getDate("M_Date"),
                        rs.getString("Diagnosis"), rs.getString("Treatment"));
            }
            if (!any) System.out.println("  No records for this animal.");
        } catch (SQLException e) { System.out.println("  Error: " + e.getMessage()); }
    }

    public boolean addRecord(int recordId, int animalId, String date, String diagnosis, String treatment) {
        String sql = "INSERT INTO Medical_Record (Record_ID, M_Date, Diagnosis, Treatment, A_ID) VALUES (?,?,?,?,?)";
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql)) {
            ps.setInt(1, recordId);
            ps.setDate(2, Date.valueOf(date));
            ps.setString(3, diagnosis);
            ps.setString(4, treatment);
            ps.setInt(5, animalId);
            ps.executeUpdate();
            return true;
        } catch (SQLException e) { System.out.println("  Failed: " + e.getMessage()); return false; }
    }
}
