/*
* Concreate class SuiteRoom that extends Reservation
*/
package models;
import java.time.LocalDate;
import exceptions.InvalidReservationException;

public class SuiteRoom extends Reservation {
    private int level;
    private String suiteType;
    private boolean hasJacuzzi;

    public SuiteRoom(String clientName, String clientContact, 
                        LocalDate checkInDate, int nights, 
                        double roomRate, int level,String suiteType,
                        boolean hasJacuzzi)
            throws InvalidReservationException {
        super(clientName, clientContact, checkInDate, nights, roomRate);
        this.level = level;
        this.suiteType = suiteType;
        this.hasJacuzzi = hasJacuzzi;
    }
    @Override
    public String getConfirmationDetails() {
         return "=== SUITE ROOM CONFIRMATION ===\n" +
            "Booking ID: " + getBookingID() + "\n" +
            "Guest: " + getClientDetails() + "\n" +
            "Level: " + level + "\n" +
            "Suite Type: " + suiteType + "\n" +
            "Jacuzzi: " + (hasJacuzzi ? "Yes" : "No") + "\n" +
            "Check-in: " + getcheckInDate() + "\n" +
            "Nights:  " + getNights() + "\n" +
            "Room Status: " + getRoomStatus() + "\n" +
            "Total: RM" + calculateTotal() + "\n" +
            "Status: " + (getIsPaid() ? "PAID" : "PENDING");
    }
    @Override
    public double calculateTotal() {
        double price = getNights() * getRoomRate();
        switch (suiteType.toUpperCase()) {
            case "JUNIOR":
                price += 50.0 * getNights();
                break;
            case "EXECUTIVE":
                price += 100.0 * getNights();
                break;
            case "PRESIDENTIAL":
                price += 200.0 * getNights();
                break;
            default:
                break;
        }
        if (hasJacuzzi) {
            price += 75.0 * getNights();
        }
        double service = 0.0;
        if (getGuestServices().contains("None")) {
            service = 0.0;
        }
        if (getGuestServices().contains("Room Service")) {
            service += 25.0;
        }
        if (getGuestServices().contains("Laundry")) {
            service += 15.0;
        }
        if (getGuestServices().contains("Spa")) {
            service += 100.0;
        }
        if (getGuestServices().contains("Transportation")) {
            service += 50.0;
        }
        price += service;
        return price;
    }
    //getters
    public int getLevel() {
        return level;
    }
    public String getSuiteType() {
        return suiteType;
    }
    public boolean getHasJacuzzi() {
        return hasJacuzzi;
    }

    public String toFileString() {
        return "SUITE|" + getClientDetails().getName() + "|" + 
            getClientDetails().getContact() + "|" + getcheckInDate() + "|" +
            getNights() + "|" + getRoomRate() + "|" + level + "|" +
            suiteType + "|" + hasJacuzzi + "|" + getBookingID() + "|" + getIsPaid() + "|" + getRoomStatus();
    }
}
