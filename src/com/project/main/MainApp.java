package com.project.main;

import com.project.dao.*;
import com.project.db.DBConnection;
import com.project.model.Animal;
import com.project.model.Bird;
import com.project.model.Mammal;
import com.project.model.Reptile;
import java.sql.Date;
import java.time.LocalDate;
import java.time.Period;
import java.util.List;
import java.util.Scanner;

public class MainApp {

    static final Scanner sc = new Scanner(System.in);
    static final AnimalDAO animalDAO = new AnimalDAO();
    static final SanctuaryDAO sanctDAO = new SanctuaryDAO();
    static final StaffDAO staffDAO = new StaffDAO();
    static final MedicalDAO medDAO = new MedicalDAO();
    static final ReportDAO reportDAO = new ReportDAO();

    public static void main(String[] args) {
        if (DBConnection.getConnection() == null) {
            System.out.println("  Could not connect. Check password in DBConnection.java.");
            return;
        }
        System.out.println("  Connected to database successfully.\n");

        boolean running = true;
        while (running) {
            mainMenu();
            int choice = readInt();
            switch (choice) {
                case 1:
                    animalMenu();
                    break;
                case 2:
                    sanctuaryMenu();
                    break;
                case 3:
                    staffMenu();
                    break;
                case 4:
                    medicalMenu();
                    break;
                case 5:
                    reportsMenu();
                    break;
                case 0:
                    running = false;
                    break;
                default:
                    System.out.println("Invalid choice.");
            }
        }
        sc.close();
        DBConnection.close();
        System.out.println("\n  Goodbye!\n");
    }

    // MENUS
    static void mainMenu() {
        System.out.println("MAIN MENU");
        System.out.println("1. Animals");
        System.out.println("2. Sanctuaries");
        System.out.println("3. Staff");
        System.out.println("4. Medical Records");
        System.out.println("5. Reports & Analytics");
        System.out.println("0. Exit");
        System.out.print("  Choose: ");
    }

    // ANIMAL MENU
    static void animalMenu() {
        boolean back = false;
        while (!back) {
            System.out.println("ANIMAL MENU");
            System.out.println("  │  1. View All Animals            │");
            System.out.println("  │  2. Search Animal by Name       │");
            System.out.println("  │  3. View Full Details (by ID)   │");
            System.out.println("  │  4. Add New Animal              │");
            System.out.println("  │  5. Update Animal               │");
            System.out.println("  │  6. Delete Animal               │");
            System.out.println("  │  0. Back                        │");
            System.out.print("  Choose: ");
            int choice = readInt();

            switch (choice) {
                case 1:
                    viewAllAnimals();
                    break;
                case 2:
                    searchAnimal();
                    break;
                case 3:
                    viewAnimalDetails();
                    break;
                case 4:
                    addAnimal();
                    break;
                case 5:
                    updateAnimal();
                    break;
                case 6:
                    deleteAnimal();
                    break;
                case 0:
                    back = true;
                    break;
                default:
                    System.out.println("Invalid choice.");
            }
        }
    }

    // ─── SANCTUARY MENU ──────────────────────────────────────────────────────
    static void sanctuaryMenu() {
        boolean back = false;
        while (!back) {
            System.out.println("  │       SANCTUARY MENU            │");
            System.out.println("  │  1. View All Sanctuaries        │");
            System.out.println("  │  2. View Sanctuary Details      │");
            System.out.println("  │  0. Back                        │");
            System.out.print("  Choose: ");
            int choice = readInt();

            switch (choice) {
                case 1:
                    sanctDAO.viewAll();
                    break;

                case 2:
                    System.out.print("Enter Sanctuary ID: ");
                    sanctDAO.showDetails(readInt());
                    break;

                case 0:
                    back = true;
                    break;

                default:
                    System.out.println("Invalid choice.");
            }
        }
    }

    // ─── STAFF MENU ──────────────────────────────────────────────────────────
    static void staffMenu() {
        boolean back = false;
        while (!back) {
            System.out.println("  │          STAFF MENU             │");
            System.out.println("  │  1. View All Staff              │");
            System.out.println("  │  2. View All Vets               │");
            System.out.println("  │  3. View All Rangers            │");
            System.out.println("  │  0. Back                        │");
            System.out.print("  Choose: ");
            int choice = readInt();

            switch (choice) {
                case 1:
                    staffDAO.viewAll();
                    break;
                case 2:
                    staffDAO.showVets();
                    break;
                case 3:
                    staffDAO.showRangers();
                    break;
                case 0:
                    back = true;
                    break;
                default:
                    System.out.println("Invalid choice.");
            }
        }
    }

    // MEDICAL MENU
    static void medicalMenu() {
        boolean back = false;
        while (!back) {
            System.out.println("MEDICAL RECORDS");
            System.out.println("1. View All Records");
            System.out.println("2. View Records for Animal");
            System.out.println("3. Add New Medical Record");
            System.out.println("0. Back");
            System.out.print("Choose: ");
            int choice = readInt();

            switch (choice) {
                case 1:
                    medDAO.viewAll();
                    break;

                case 2:
                    System.out.print("Enter Animal ID: ");
                    medDAO.viewForAnimal(readInt());
                    break;

                case 3:
                    addMedicalRecord();
                    break;

                case 0:
                    back = true;
                    break;

                default:
                    System.out.println("Invalid choice.");
            }
        }
    }

    // ─── REPORTS MENU ────────────────────────────────────────────────────────
    static void reportsMenu() {
        boolean back = false;
        while (!back) {
            System.out.println("  │           REPORTS & ANALYTICS           │");
            System.out.println("  │  1. Endangered Animals & Their Threats  │");
            System.out.println("  │  2. Animal Count by Sanctuary           │");
            System.out.println("  │  3. Rescue Events                       │");
            System.out.println("  │  4. Conservation Programs               │");
            System.out.println("  │  0. Back                                │");
            System.out.print("  Choose: ");
            switch (readInt()) {
                case 1:
                    reportDAO.endangeredAnimals();
                    break;

                case 2:
                    reportDAO.animalsBySanctuary();
                    break;

                case 3:
                    reportDAO.recentRescues();
                    break;

                case 4:
                    reportDAO.conservationPrograms();
                    break;

                case 0:
                    back = true;
                    break;

                default:
                    System.out.println("  Invalid choice.");
                    break;
            }
        }
    }
    // ANIMAL ACTIONS

    static void viewAllAnimals() {
        List<Animal> list = animalDAO.getAll();
        if (list.isEmpty()) {
            System.out.println("  No animals found.");
            return;
        }
        System.out.println("ALL ANIMALS");
        System.out.printf("  %-5s  %-15s  %-5s  %-18s  %-12s%n", "ID", "Name", "Age", "Species", "DOB");
        System.out.println("  " + "─".repeat(62));
        for (Animal a : list)
            System.out.printf("  %-5d  %-15s  %-5d  %-18s  %-12s%n",
                    a.getId(), a.getName(), a.getAge(), a.getSpeciesName(), a.getDob());
        System.out.println("  " + "─".repeat(62));
        System.out.println("  Total: " + list.size() + " animals");
    }

    static void searchAnimal() {
        System.out.print("\n  Enter name to search: ");
        String kw = readLine();
        List<Animal> list = animalDAO.searchByName(kw);
        if (list.isEmpty()) {
            System.out.println("  No animals found matching '" + kw + "'.");
            return;
        }
        System.out.printf("\n  %-5s  %-15s  %-5s  %-18s  %-12s%n", "ID", "Name", "Age", "Species", "DOB");
        System.out.println("  " + "─".repeat(62));
        for (Animal a : list)
            System.out.printf("  %-5d  %-15s  %-5d  %-18s  %-12s%n",
                    a.getId(), a.getName(), a.getAge(), a.getSpeciesName(), a.getDob());
    }

    static void viewAnimalDetails() {
        System.out.print("\n  Enter Animal ID: ");
        animalDAO.showDetails(readInt());
    }

    static void addAnimal() {
        System.out.println("\n  ── Add New Animal ──────────────────────");

        System.out.print("  Category (Mammal / Bird / Reptile): ");
        String cat = readLine();
        Animal a;
        if ("Mammal".equalsIgnoreCase(cat)) {
            a = new Mammal();
        } else if ("Bird".equalsIgnoreCase(cat)) {
            a = new Bird();
        } else if ("Reptile".equalsIgnoreCase(cat)) {
            a = new Reptile();
        } else {
            System.out.println("  Invalid category. Aborted.");
            return;
        }

        System.out.print("  Animal ID              : ");
        a.setId(readInt());
        System.out.print("  Name                   : ");
        a.setName(readLine());

        // DOB with validation loop
        Date dob = null;
        while (dob == null) {
            System.out.print("  Date of Birth (YYYY-MM-DD): ");
            try {
                dob = Date.valueOf(readLine());
            } catch (IllegalArgumentException e) {
                System.out.println("  Invalid format. Use YYYY-MM-DD.");
            }
        }
        a.setDob(dob);
        a.setAge(Period.between(dob.toLocalDate(), LocalDate.now()).getYears());

        // Species
        animalDAO.listSpecies();
        System.out.print("  Enter Species Name     : ");
        String sName = readLine();
        int sId = animalDAO.getSpeciesIdByName(sName);
        if (sId == -1) {
            System.out.println("  Species '" + sName + "' not found. Creating a new one...");
            System.out.print("  Enter Conservation Status (e.g. Safe, Endangered): ");
            String status = readLine();
            System.out.print("  Is it poisonous? (yes/no): ");
            boolean isPoisonous = "yes".equalsIgnoreCase(readLine());
            
            sId = animalDAO.addSpecies(sName, status, isPoisonous);
            if (sId == -1) {
                System.out.println("  Failed to create species. Aborted.");
                return;
            }
            System.out.println("  ✔ Species '" + sName + "' dynamically added with ID: " + sId);
        }
        a.setSpeciesId(sId);

        if (a instanceof Mammal) {
            Mammal m = (Mammal) a;
            System.out.print("  Fur Type               : ");
            m.setFurType(readLine());
        } else if (a instanceof Bird) {
            Bird b = (Bird) a;
            System.out.print("  Wing Span (metres)     : ");
            b.setWingSpan(readDouble());
        }

        sanctDAO.viewAll();
        System.out.print("  Enter target Sanctuary ID : ");
        int sanctId = readInt();

        if (animalDAO.add(a)) {
            System.out.println("\n  ✔  " + a.getName() + " added. (Age: " + a.getAge() + " years)");
            if (animalDAO.assignToSanctuary(a.getId(), sanctId)) {
                System.out.println("  ✔  Animal successfully assigned to Sanctuary ID " + sanctId);
            }
        }
    }

    static void updateAnimal() {
        System.out.print("\n  Enter Animal ID to update: ");
        int id = readInt();
        Animal ex = animalDAO.getById(id);
        if (ex == null) {
            System.out.println("  Animal not found.");
            return;
        }
        System.out.println("  Current → Name: " + ex.getName() + "  DOB: " + ex.getDob()
                + "  Species: " + ex.getSpeciesName());
        System.out.println("\n  What to update?  1.Name   2.Date of Birth   3.Species");
        System.out.print("  Choice: ");
        String field = String.valueOf(readInt());
        System.out.print("  New value: ");
        String value = readLine();

        if ("3".equals(field)) {
            int sId = animalDAO.getSpeciesIdByName(value);
            if (sId == -1) {
                System.out.println("  Species not found.");
                return;
            }
            value = String.valueOf(sId);
        }
        if (animalDAO.update(id, field, value))
            System.out.println("  ✔  Updated successfully.");
    }

    static void deleteAnimal() {
        System.out.print("\n  Enter Animal ID to delete: ");
        int id = readInt();
        Animal ex = animalDAO.getById(id);
        if (ex == null)
            return;
        System.out.print("  Confirm delete '" + ex.getName() + "'? (yes/no): ");
        if (!"yes".equalsIgnoreCase(readLine())) {
            System.out.println("  Cancelled.");
            return;
        }
        if (animalDAO.delete(id))
            System.out.println("  ✔  " + ex.getName() + " and all related records deleted.");
    }

    // MEDICAL RECORD ADD
    static void addMedicalRecord() {
        System.out.println("\n  ── Add Medical Record ──────────────────");
        System.out.print("  Record ID        : ");
        int recId = readInt();
        System.out.print("  Animal ID        : ");
        int anId = readInt();

        if (animalDAO.getById(anId) == null) {
            System.out.println("  Animal not found.");
            return;
        }

        String date = null;
        while (date == null) {
            System.out.print("  Date (YYYY-MM-DD): ");
            String d = readLine();
            try {
                Date.valueOf(d);
                date = d;
            } catch (IllegalArgumentException e) {
                System.out.println("  Invalid date. Use YYYY-MM-DD.");
            }
        }
        System.out.print("  Diagnosis        : ");
        String diag = readLine();
        System.out.print("  Treatment        : ");
        String treat = readLine();

        if (medDAO.addRecord(recId, anId, date, diag, treat))
            System.out.println("  ✔  Medical record added.");
    }
    // HELPERS

    static void banner() {
        System.out.println("  ║      🌿  WILDLIFE SANCTUARY SYSTEM  🌿          ║");
        System.out.println("  ║          Animal Management & Reports             ║");
        System.out.println("  ║                                                  ║");
    }

    static int readInt() {
        while (true) {
            try {
                return Integer.parseInt(sc.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.print("  Enter a valid number: ");
            }
        }
    }

    static double readDouble() {
        while (true) {
            try {
                return Double.parseDouble(sc.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.print("  Enter a valid number: ");
            }
        }
    }

    static String readLine() {
        return sc.nextLine().trim();
    }
}