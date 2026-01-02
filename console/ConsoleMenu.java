package console;
import models.*;
import exceptions.InvalidReservationException;

import java.time.LocalDate;
import java.util.Scanner;
import models.SuiteRoom;

public class ConsoleMenu {
    private static Scanner sc = new Scanner(System.in);

    public static void main(String[] args) {

        FileHandler.loadFromFile();

        boolean running = true;

        while (running) {
            printMenu();
            int choice = getIntInput("Enter your choice: ");

            switch (choice) {
                case 1:
                    addStandardRoom();
                    break;
                case 2:
                    addSuiteRoom();
                    break;
                case 3:
                    addConferenceHall();
                    break;
                case 4:
                    viewAllReservations();
                    break;
                case 5:
                    viewReport();
                    break;
                case 6:
                    FileHandler.saveToFile();
                    break;
                case 7:
                    FileHandler.loadFromFile();
                    break;
                case 8:
                    processPayment();
                    break;
                case 0:
                    FileHandler.saveToFile();
                    running = false;
                    System.out.println("Exiting... Goodbye!");
                    break;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
        sc.close();
    }

    //Menu Console
    private static void printMenu() {
        System.out.println("\n╔═══════════════════════════════════════╗");
        System.out.println("║   SMART HOSPITALITY SYSTEM (SHS)      ║");
        System.out.println("╠═══════════════════════════════════════╣");
        System.out.println("║  1. Add Standard Room Booking         ║");
        System.out.println("║  2. Add Suite Booking                 ║");
        System.out.println("║  3. Add Conference Hall Booking       ║");
        System.out.println("║  4. View All Reservations             ║");
        System.out.println("║  5. View Report                       ║");
        System.out.println("║  6. Save to File                      ║");
        System.out.println("║  7. Load from File                    ║");
        System.out.println("║  8. Process Payment                   ║");
        System.out.println("║  0. Exit                              ║");
        System.out.println("╚═══════════════════════════════════════╝");
    }

    //Menu for add standard room
    private static void addStandardRoom() {
        try {
            System.out.println("---Add Standard Room---");
            String name = getStringInput("Guest Name: ");
            String contact = getStringInput("Contact Info: ");
            LocalDate date = getDateInput("Reservation Date");
            int nights = getIntInput("Number of Nights: ");
            double rate = getDoubleInput("Room Rate per Night: ");
            int roomNumber = getIntInput("Room Number: ");
            String bedType = getStringInput("Bed Type (Single/Double/Queen/King): ");
            boolean hasWifi = getStringInput("Has Wifi (yes/no): ").equalsIgnoreCase("yes");

            StandardRoom room = new StandardRoom(name, contact, date, nights, rate, roomNumber, bedType, hasWifi);
            ReservationManager.addReservation(room);
            System.out.println("Standard Room booking added successfully!");

        } catch (InvalidReservationException e) {
            System.out.println("Error adding reservation: " + e.getMessage());
        }
    }

    private static void addSuiteRoom() {
        try {
            System.out.println("---Add Suite Room---");
            String name = getStringInput("Guest Name: ");
            String contact = getStringInput("Contact Info: ");
            LocalDate date = getDateInput("Reservation Date");
            int nights = getIntInput("Number of Nights: ");
            double rate = getDoubleInput("Room Rate per Night: ");
            int level = getIntInput("Level: ");
            String suiteType = getStringInput("Suite Type (Junior/Executive/Presidential): ");
            boolean hasJacuzzi = getStringInput("Has Jacuzzi? (yes/no): ").equalsIgnoreCase("yes");

            SuiteRoom room = new SuiteRoom(name, contact, date, nights, rate, level, suiteType, hasJacuzzi);
            ReservationManager.addReservation(room);
            System.out.println("Suite Room booking added successfully!");

        } catch (InvalidReservationException e) {
            System.out.println("Error adding reservation: " + e.getMessage());
        }
    }
    
    private static void addConferenceHall() {
        try {
            System.out.println("---Add Conference Hall---");
            String name = getStringInput("Client Name: ");
            String contact = getStringInput("Contact Info: ");
            LocalDate date = getDateInput("Reservation Date");
            int nights = getIntInput("Number of Nights: ");
            double rate = getDoubleInput("Hall Rate per Night: ");
            String hallName = getStringInput("Hall Name: ");
            int capacity = getIntInput("Capacity: ");
            boolean hasProjector = getStringInput("Has Projector? (yes/no): ").equalsIgnoreCase("yes");

            ConferenceHall hall = new ConferenceHall(name, contact, date, nights, rate, hallName, capacity, hasProjector);
            ReservationManager.addReservation(hall);
            System.out.println("Conference Hall booking added successfully!");

        } catch (InvalidReservationException e) {
            System.out.println("Error adding reservation: " + e.getMessage());
        }
    }

    //Menu for view all reservations
    private static void viewAllReservations() {
        System.out.println("---All Reservations---");
        if (ReservationManager.getSize() == 0) {
            System.out.println("No reservations found.");
            return;
        }
        for (Reservation r : ReservationManager.getAllReservations()) {
            System.out.println("\n" + r.getConfirmationDetails());

            Reservation.Reminder reminder = r.new Reminder();
            System.out.println(reminder.checkReminder());
            System.out.println("--------------------------");
        }
    }

    //Menu for view report
    private static void viewReport() {
        System.out.println("---Reservation Report---");
        String report = ReservationManager.generateReport();
        System.out.println(report);
    }

    //Menu for process payment
    private static void processPayment() {
        String bookingID = getStringInput("Enter Booking ID for payment: ");
        Reservation r = ReservationManager.findByID(bookingID);

        if (r == null) {
            System.out.println("Reservation not found.");
            return;
        }
        if (r.getIsPaid()) {
            System.out.println("Booking alread paid.");
            return;
        }
        r.applyDiscount();
        r.processPayment();
    }


    //Helper for Input
    private static String getStringInput(String prompt) {
        System.out.print(prompt);
        return sc.nextLine().trim();
    }
    private static int getIntInput(String prompt) {
        while (true) {
            try {
                System.out.print(prompt);
                return Integer.parseInt(sc.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a valid integer.");
            }
        }
    }
    private static double getDoubleInput(String prompt) {
        while (true) {
            try {
                System.out.print(prompt);
                return Double.parseDouble(sc.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a valid number.");
            }
        }
    }
    private static LocalDate getDateInput(String prompt) {
        while (true) {
            try {
                System.out.print(prompt + " (YYYY-MM-DD): ");
                String input = sc.nextLine().trim();
                return LocalDate.parse(input);
            } catch (Exception e) {
                System.out.println("Invalid date format. Please enter date in YYYY-MM-DD format.");
            }
        }
    }

}
