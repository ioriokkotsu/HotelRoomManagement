package models;
import java.util.ArrayList;
public class ReservationManager {
    
    //static list of reservation
    private static ArrayList<Reservation> reservations = new ArrayList<>();

    //add reservation
    public static void addReservation(Reservation r) {
        reservations.add(r);
    }
    //remove reservation based on bookingId
    public static boolean removeReservation(String bookingId) {
        for (int i=0; i < reservations.size(); i++) {
            if (reservations.get(i).getBookingID().getId().equals(bookingId)) {
                reservations.remove(i);
                return true;
            }
        }
        return false;
    }

    //get all reservation
    public static ArrayList<Reservation> getAllReservations() {
        return reservations;
    }

    //Empty the list
    public static void clearAll() {
        reservations.clear();
    }

    //Get list size
    public static int getSize() {
        return reservations.size();
    }

    //Find by ID
    public static Reservation findByID(String bookingID) {
        for (Reservation r : reservations) {
            if (r.getBookingID().getId().equals(bookingID)) {
                return r;
            }
        }
        return null;
    }

    //Generate Report
    public static String generateReport() {
        StringBuilder report = new StringBuilder();
        report.append("╔══════════════════════════════════════╗\n");
        report.append("║     HOTEL RESERVATION REPORT         ║\n");
        report.append("╚══════════════════════════════════════╝\n\n");
        
        int standardCount = 0, suiteCount = 0, conferenceCount = 0;
        double totalRevenue = 0;
        
        for (Reservation r : reservations) {
            if (r instanceof StandardRoom) standardCount++;
            else if (r instanceof SuiteRoom) suiteCount++;
            else if (r instanceof ConferenceHall) conferenceCount++;
            
            totalRevenue += r.calculateTotal();
        }
        
        report.append("Total Reservations: ").append(reservations.size()).append("\n");
        report.append("─────────────────────────────\n");
        report.append("Standard Rooms: ").append(standardCount).append("\n");
        report.append("Suite Bookings: ").append(suiteCount).append("\n");
        report.append("Conference Halls: ").append(conferenceCount).append("\n");
        report.append("─────────────────────────────\n");
        report.append("Total Revenue: $").append(String.format("%.2f", totalRevenue)).append("\n");
        
        return report.toString();
    }

    //status count
    public static int[] getStatusCounts() {
        int available = 0;
        int occupied = 0;
        int maintenance = 0;
        int cleaning = 0;

        for (Reservation r : reservations) {
            switch (r.getRoomStatus()) {
                case "AVAILABLE": {
                    available++; 
                    break;
                }
                case "OCCUPIED": occupied++; break;
                case "MAINTENANCE": maintenance++; break;
                case "CLEANING": cleaning++; break;
            }
        }
        return new int[] {available, occupied, maintenance, cleaning}; 
    }

    //get reservation by status
    public static ArrayList<Reservation> getReservationsByStatus(String status) {
        ArrayList<Reservation> filtered = new ArrayList<>();
        for (Reservation r : reservations) {
            if (r.getRoomStatus().equalsIgnoreCase(status)) {
                filtered.add(r);
            }
        }
        return filtered;
    }
}
