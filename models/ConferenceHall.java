package models;
import java.time.LocalDate;
import exceptions.InvalidReservationException;

public class ConferenceHall extends Reservation {
    private String hallName;
    private int capacity;
    private boolean hasProjector;

    public ConferenceHall(String clientName, String clientContact, LocalDate checkInDate, int nights,
                          double roomRate, String hallName, int capacity, boolean hasProjector) throws InvalidReservationException {
        super(clientName, clientContact, checkInDate, nights, roomRate);
        this.hallName = hallName;
        this.capacity = capacity;
        this.hasProjector = hasProjector;
    }

    // Getters and Setters
    public String getHallName() {
        return hallName;
    }

    public void setHallName(String hallName) {
        this.hallName = hallName;
    }

    public boolean getHasProjector() {
        return hasProjector;
    }
    public void setHasProjector(boolean hasProjector) {
        this.hasProjector = hasProjector;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }


    public String toFileString() {
        return "CONFERENCE|" + getClientDetails().getName() + "|" + 
            getClientDetails().getContact() + "|" + getcheckInDate() + "|" +
            getNights() + "|" + getRoomRate() + "|" + hallName + "|" +
            capacity + "|" + hasProjector + "|" + getBookingID() + "|" + getIsPaid() + "|" + 
            getRoomStatus() + "|" + "Not Applicable" + "|" +
            "Not Applicable";
    }

    @Override
    public String getConfirmationDetails() {
        return "=== CONFERENCE HALL CONFIRMATION ===\n" +
            "Booking ID: " + getBookingID() + "\n" +
            "Guest: " + getClientDetails() + "\n" +
            "Hall Name: " + hallName + "\n" +
            "Capacity: " + capacity + "\n" +
            "Has Projector: " + (hasProjector ? "Yes" : "No") + "\n" +
            "Check-in: " + getcheckInDate() + "\n" +
            "Room Status: " + getRoomStatus() + "\n" +
            "Nights:  " + getNights() + "\n" +
            "Total: $" + calculateTotal() + "\n" +
            "Status: " + (getIsPaid() ? "PAID" : "PENDING");
    }

    @Override
    public double calculateTotal() {
        double price = getNights() * getRoomRate();
        price = hasProjector ? price + (getNights() * 100.0) : price;
        return price;
    }
    
}
