package models;
import exceptions.InvalidReservationException;
public final class BookingID {
    private final String id;
    private static int counter = 0;

    //constructors
    public BookingID() {
        counter++;
        this.id = String.format("HRM-2025-%03d", counter);
    }
    public BookingID(String existID) throws InvalidReservationException {
        if (! validateID(existID)) {
            throw new InvalidReservationException("Invalid Booking ID format.");
        }
        this.id = existID;
        
    }

    private boolean validateID(String existID) {
        if (existID == null || existID.length() != 12) {
            return false;
        }
        return existID.matches("HRM-2025-\\d{3}");
    }

    //getters
    public String getId() {
        return id;
    }
    public int getCounter() {
        return counter;
    }
    //return booking ID as string
    @Override
    public String toString() {
        return id;
    }
    
}
