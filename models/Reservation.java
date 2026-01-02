package models;
import exceptions.InvalidReservationException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;


public abstract class Reservation implements Payable {
    private BookingID bookingID;
    private ClientDetails clientDetails;
    private LocalDate checkInDate;
    private int nights;
    private double roomRate;
    private boolean isPaid;
    private ArrayList<String> housekeepingNotes;
    private String roomStatus;
    private ArrayList<String> guestServices;

    //constructor
    public Reservation(String clientName,String clientContact,LocalDate checkInDate,int nights,double roomRate) throws InvalidReservationException{

        validateReservation(checkInDate,nights,roomRate);
        this.bookingID = new BookingID();
        this.clientDetails = new ClientDetails(clientName,clientContact);
        this.checkInDate = checkInDate;
        this.nights = nights;
        this.roomRate = roomRate;
        this.isPaid = false;
        
        this.housekeepingNotes = new ArrayList<>(Arrays.asList("None"));
        this.roomStatus = "AVAILABLE";  
        this.guestServices = new ArrayList<>(Arrays.asList("None"));
    }

    //validation
    private void validateReservation(LocalDate date,int nights,double roomRate) throws InvalidReservationException {
        if (date.isBefore(LocalDate.now()) || date == null ) {
            throw new InvalidReservationException("Check in date is required");
        }
        if (nights <= 0) {
            throw new InvalidReservationException("Number of nights must be greater than zero");
        }
        if (roomRate <= 0) {
            throw new InvalidReservationException("Room rate must be greater than zero");
        }
    }
    //abstarct method for subclass
    public abstract String getConfirmationDetails();
    public abstract double calculateTotal();
    //calculate total cost
    // public double calculateTotal() {
    //     return nights * roomRate;
    // }

    //interface method
    @Override
    public void processPayment() {
        if (!isPaid) {
            this.isPaid = true;
            System.out.println("Payment processed " + calculateTotal() + " for booking ID: " + bookingID);
        } else {
            System.out.println("Payment has already been processed for booking ID: " + bookingID);
        }
    }

    @Override
    public void applyDiscount() {
        if (nights > 5) {
            this.roomRate *= 0.9;
            System.out.println("10% discount applied for booking ID: " + bookingID + "\n New price is: " + roomRate);
        } else {
            System.out.println("No discount applicable for booking ID: " + bookingID);
        }
    }

    //methods for housekeeping notes
    public void addHousekeepingNote(String note) {
        if (housekeepingNotes.size() == 1 && housekeepingNotes.get(0).equals("None")) {
            housekeepingNotes.clear();
        }
        if (!housekeepingNotes.contains(note)) {
            housekeepingNotes.add(note);
        }
    }
    public String getHousekeepingNotesAsString() {
        StringBuilder notes = new StringBuilder();
        for (String note : housekeepingNotes) {
            notes.append(note).append("\n");
        }
        return notes.toString();
    }
    // String housekeepingAsString = getGuestServicesAsString().replace("\n", ",");
    //String housekeepingAsString = String.join(",", housekeepingNotes);
    //methods for guest services
    public void addGuestService(String service) {
        if (guestServices.size() == 1 && guestServices.get(0).equals("None")) {
            guestServices.clear();
        }
        if (!guestServices.contains(service)) {
            guestServices.add(service);
        }
    }
    public String getGuestServicesAsString() {
        StringBuilder services = new StringBuilder();
        for (String service : guestServices) {
            services.append(service).append("\n");
        }
        return services.toString();
    }

    //calculate total with services
    public double calculateServices() {
        double price = 0.0;
        if (guestServices.contains("None")) {
            return 0.0;
        } 
        if (guestServices.contains("Room Service")) {
            price += 25.0;
        } 
        if (guestServices.contains("Laundry")) {
            price += 15.0;
        } 
        if (guestServices.contains("Spa")) {
            price += 100.0;
        } 
        if (guestServices.contains("Transportation")) {
            price += 50.0;
        }
        
        return price;
    }

    //getters
    public BookingID getBookingID() {
        return bookingID;
    }
    public ClientDetails getClientDetails() {
        return clientDetails;
    }
    public LocalDate getcheckInDate() {
        return checkInDate;
    }
    public int getNights() {
        return nights;
    }
    public double getRoomRate() {
        return roomRate;
    }
    public boolean getIsPaid() {
        return isPaid;
    }
    public ArrayList<String> getHousekeepingNotes() {
        return housekeepingNotes;
    }
    public String getRoomStatus() {
        return roomStatus;
    }
    public ArrayList<String> getGuestServices() {
        return guestServices;
    }
    //setters
    public void setBookingID(BookingID bookingID) {
        this.bookingID = bookingID;
    }
    public void setNights(int nights) {
        this.nights = nights;
    }
    public void setIsPaid(boolean isPaid) {
        this.isPaid = isPaid;
    }
    public void setRoomStatus(String roomStatus) {
        this.roomStatus = roomStatus;
    }
    public void setHousekeepingNotes(ArrayList<String> notes) {
        this. housekeepingNotes = notes;
    }
    public void setGuestServices(ArrayList<String> services) {
        this.guestServices = services;
    }


    //STATIC NESTED - ClientDetails
    public static class ClientDetails {
        private String name;
        private String contact;

        public ClientDetails(String name, String contact) {
            this.name = name;
            this.contact = contact ;
        }
        public String getName() {
            return name;
        }

        public String getContact() {
            return contact;
        }
        @Override
        public String toString() {
            return name + " (" + contact + ")";
        }
    } 

    //Inner Class Reminder
    public class Reminder {

        public String checkReminder() {
            try {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                LocalDate checkIn = LocalDate.parse(checkInDate.toString(),formatter);
                LocalDate today = LocalDate.now();
                
                long daysUntil = ChronoUnit.DAYS.between(today, checkIn);
                
                if (daysUntil < 0 ) {
                    return ("Check In was " + Math.abs(daysUntil) + " days ago");
                } else if (daysUntil == 0) {
                    return ("Check In is today!");
                } else {
                    return ("Check In is in " + daysUntil + " days.");
                }
            } catch (Exception e) {
                return("Error sending reminder: ");
            }
        }
    }
}

