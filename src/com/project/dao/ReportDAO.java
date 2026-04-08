package com.project.dao;

import com.project.db.DBConnection;
import java.sql.*;

public class ReportDAO {

    // Report 1: Endangered animals and what threatens them
    public void endangeredAnimals() {
        String sql = "SELECT a.A_Name, s.Species_Name, s.Conservation_Status, " +
                     "t.Description AS Threat, t.Severity_Level " +
                     "FROM Animal a JOIN Species s ON a.S_ID = s.S_ID " +
                     "LEFT JOIN Faces f ON a.A_ID = f.A_ID " +
                     "LEFT JOIN Threat t ON f.Threat_ID = t.Threat_ID " +
                     "WHERE s.Conservation_Status = 'Endangered' ORDER BY a.A_Name";
        try (Statement st = DBConnection.getConnection().createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            System.out.println("\n  ┌──────────────────────────────────────────────────────────┐");
            System.out.println("  │        ENDANGERED ANIMALS & THEIR THREATS               │");
            System.out.println("  └──────────────────────────────────────────────────────────┘");
            System.out.printf("  %-12s %-12s %-15s %-22s %-10s%n","Animal","Species","Status","Threat","Severity");
            System.out.println("  " + "─".repeat(74));
            boolean any = false;
            while (rs.next()) {
                any = true;
                System.out.printf("  %-12s %-12s %-15s %-22s %-10s%n",
                        rs.getString("A_Name"), rs.getString("Species_Name"),
                        rs.getString("Conservation_Status"),
                        nvl(rs.getString("Threat")), nvl(rs.getString("Severity_Level")));
            }
            if (!any) System.out.println("  No endangered animals found.");
        } catch (SQLException e) { System.out.println("  Error: " + e.getMessage()); }
    }


    // Report 3: Animals per sanctuary summary
    public void animalsBySanctuary() {
        String sql = "SELECT sa.S_Name AS Sanctuary, sa.State, COUNT(DISTINCT a.A_ID) AS Total_Animals, " +
                     "COUNT(DISTINCT a.S_ID) AS Species_Count " +
                     "FROM Sanctuary sa " +
                     "LEFT JOIN Habitat h ON sa.Sanct_ID = h.Sanct_ID " +
                     "LEFT JOIN Lives_In li ON h.H_ID = li.H_ID " +
                     "LEFT JOIN Animal a ON li.A_ID = a.A_ID " +
                     "GROUP BY sa.Sanct_ID ORDER BY Total_Animals DESC";
        try (Statement st = DBConnection.getConnection().createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            System.out.println("\n  ┌──────────────────────────────────────────────────┐");
            System.out.println("  │          ANIMALS COUNT BY SANCTUARY             │");
            System.out.println("  └──────────────────────────────────────────────────┘");
            System.out.printf("  %-20s %-15s %-14s %-12s%n","Sanctuary","State","Total Animals","Species Count");
            System.out.println("  " + "─".repeat(64));
            while (rs.next())
                System.out.printf("  %-20s %-15s %-14d %-12d%n",
                        rs.getString("Sanctuary"), rs.getString("State"),
                        rs.getInt("Total_Animals"), rs.getInt("Species_Count"));
        } catch (SQLException e) { System.out.println("  Error: " + e.getMessage()); }
    }

    // Report 4: Recent rescue events
    public void recentRescues() {
        String sql = "SELECT re.Rescue_ID, re.Re_Date, re.R_Location, re.Reason, a.A_Name " +
                     "FROM Rescue_Event re " +
                     "JOIN Involved_In ii ON re.Rescue_ID = ii.Rescue_ID " +
                     "JOIN Animal a ON ii.A_ID = a.A_ID ORDER BY re.Re_Date DESC";
        try (Statement st = DBConnection.getConnection().createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            System.out.println("\n  ┌──────────────────────────────────────────────────────┐");
            System.out.println("  │               RESCUE EVENTS                         │");
            System.out.println("  └──────────────────────────────────────────────────────┘");
            System.out.printf("  %-5s %-12s %-12s %-18s %-15s%n","ID","Date","Location","Reason","Animal");
            System.out.println("  " + "─".repeat(65));
            boolean any = false;
            while (rs.next()) {
                any = true;
                System.out.printf("  %-5d %-12s %-12s %-18s %-15s%n",
                        rs.getInt("Rescue_ID"), rs.getDate("Re_Date"),
                        rs.getString("R_Location"), rs.getString("Reason"),
                        rs.getString("A_Name"));
            }
            if (!any) System.out.println("  No rescue events.");
        } catch (SQLException e) { System.out.println("  Error: " + e.getMessage()); }
    }

    // Report 5: Conservation programs and what species they protect
    public void conservationPrograms() {
        String sql = "SELECT cp.Prog_Name, cp.Start_Date, cp.End_Date, cp.Budget, " +
                     "sp.Species_Name, sp.Conservation_Status, sa.S_Name AS Sanctuary " +
                     "FROM Conservation_Program cp " +
                     "JOIN Protects pt ON cp.Program_ID = pt.Program_ID " +
                     "JOIN Species sp ON pt.S_ID = sp.S_ID " +
                     "JOIN Sanctuary sa ON cp.Sanct_ID = sa.Sanct_ID " +
                     "ORDER BY cp.Budget DESC";
        try (Statement st = DBConnection.getConnection().createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            System.out.println("\n  ┌────────────────────────────────────────────────────────────────┐");
            System.out.println("  │             CONSERVATION PROGRAMS & PROTECTED SPECIES         │");
            System.out.println("  └────────────────────────────────────────────────────────────────┘");
            System.out.printf("  %-18s %-12s %-12s %-10s %-12s %-15s%n",
                    "Program","Start","End","Budget","Species","Sanctuary");
            System.out.println("  " + "─".repeat(82));
            boolean any = false;
            while (rs.next()) {
                any = true;
                System.out.printf("  %-18s %-12s %-12s %-10.0f %-12s %-15s%n",
                        rs.getString("Prog_Name"), rs.getDate("Start_Date"),
                        rs.getDate("End_Date"), rs.getFloat("Budget"),
                        rs.getString("Species_Name"), rs.getString("Sanctuary"));
            }
            if (!any) System.out.println("  No programs.");
        } catch (SQLException e) { System.out.println("  Error: " + e.getMessage()); }
    }

    private String nvl(String s) { return s != null ? s : "N/A"; }
}
