/*
* Concreate class StandardRoom that extends Reservation
*/
package models;
import exceptions.InvalidReservationException;
import java.time.LocalDate;

public class StandardRoom extends Reservation {

    //field specific StandardRoom
    private int roomNumber;
    private String bedType;
    private boolean hasWifi;

    public StandardRoom(String clientName, String clientContact, 
                        LocalDate checkInDate, int nights, 
                        double roomRate, int roomNumber,String bedType,
                        boolean hasWifi)
            throws InvalidReservationException {
        super(clientName, clientContact, checkInDate, nights, roomRate);
        this.roomNumber = roomNumber;
        this.bedType = bedType;
        this.hasWifi = hasWifi;
    }

    @Override
    public String getConfirmationDetails() {
         return "=== STANDARD ROOM CONFIRMATION ===\n" +
            "Booking ID: " + getBookingID() + "\n" +
            "Guest: " + getClientDetails() + "\n" +
            "Room Number: " + roomNumber + "\n" +
            "Bed Type: " + bedType + "\n" +
            "WiFi: " + (hasWifi ? "Yes" : "No") + "\n" +
            "Check-in: " + getcheckInDate() + "\n" +
            "Room Status: " + getRoomStatus() + "\n" +
            "Nights:  " + getNights() + "\n" +
            "Total: $" + calculateTotal() + "\n" +
            "Status: " + (getIsPaid() ? "PAID" : "PENDING");
    }

    @Override
    public double calculateTotal() {
        double price = getNights() * getRoomRate();
        if (hasWifi) {
            price += 10.0 * getNights();
        }
        switch (bedType.toUpperCase()) {
            case "SINGLE":
                price += 0.0;
                break;
            case "DOUBLE":
                price += 20.0 * getNights();
                break;
            case "QUEEN":
                price += 30.0 * getNights();
                break;
            case "KING":
                price += 40.0 * getNights();
                break;
            default:
                break;
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
    public int getRoomNumber() {
        return roomNumber;
    }
    public String getBedType() {
        return bedType;
    }
    public boolean getHasWifi() {
        return hasWifi;
    }
    public String toFileString() {
        return "STANDARD|" + getClientDetails().getName() + "|" + 
            getClientDetails().getContact() + "|" + getcheckInDate() + "|" +
            getNights() + "|" + getRoomRate() + "|" + roomNumber + "|" +
            bedType + "|" + hasWifi + "|" + getBookingID() + "|" + getIsPaid() + "|" + 
            getRoomStatus() + "|" + arrayStringFile(getHousekeepingNotesAsString()) + "|" +
            arrayStringFile(getGuestServicesAsString());
    }
    public String arrayStringFile(String getters) {
        return getters.replaceAll("\n(?!$)", ",").replaceAll("\n$", "");
    }
}
