package models;
import console.FileHandler;
import java.time.LocalDate;

import exceptions.InvalidReservationException;

public class Main {
    public static void main(String[] args) throws InvalidReservationException {
        //Test suite room creation
        StandardRoom std = new StandardRoom("Alice Smith", "alice@example.com", LocalDate.of(2026, 6, 15), 3, 250.0, 101, "Executive", true);
        //System.out.println(standardRoom.getConfirmationDetails());
        // System.out.println(standardRoom.getRoomStatus());
        // System.out.println(ReservationManager.getStatusCounts()[0]);
        // System.out.println(ReservationManager.getAllReservations());
        // ReservationManager.addReservation(suite);
        // std.addGuestService("Airport Pickup");
        // std.addGuestService("Hotel Pickup");
        std.addHousekeepingNote("Extra Towels");
        System.out.println(std.toFileString());
        System.out.println(std.getGuestServicesAsString());
        ReservationManager.addReservation(std);
        FileHandler.saveToFile();
        ReservationManager.clearAll();
        ReservationManager.getAllReservations();
        FileHandler.loadFromFile();
        System.out.println(ReservationManager.getAllReservations());

    }
}
//1
// -1 subclass
