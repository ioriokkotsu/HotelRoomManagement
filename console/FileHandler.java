package console;
import models.*;
import exceptions.InvalidReservationException;

import java.io.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;

public class FileHandler {
    private static final String FILE_NAME = "reservations.txt";

    //Save reservations to file
    public static void saveToFile()  {
        PrintWriter writer = null;

        try {
            writer = new PrintWriter(new FileWriter(FILE_NAME));

            for (Reservation r : ReservationManager.getAllReservations()) {
                if (r instanceof StandardRoom) {
                    writer.println(((StandardRoom) r ).toFileString());
                } else if (r instanceof SuiteRoom) {
                    writer.println(((SuiteRoom) r).toFileString());
                } else if (r instanceof ConferenceHall) {
                    writer.println(((ConferenceHall) r).toFileString());
                }
            }
            System.out.println("Reservation saved");
        } catch (IOException e) {
            System.out.println(e.getMessage());
        } finally {
            if (writer != null) {
                writer.close();
            }
        }
    }  

    //Load reservation from file
    public static void loadFromFile() {
        BufferedReader reader = null;

        try {
            File file = new File(FILE_NAME);
            if (!file.exists()) {
                System.out.println("NO file found.Starting new.");
                return;
            }

            reader = new BufferedReader(new FileReader(file));
            ReservationManager.clearAll();

            String line;
            int count = 0;

            while ((line = reader.readLine()) != null) {
                try {
                    Reservation r = parseReservation(line);
                    if (r != null) {
                        ReservationManager.addReservation(r);
                        count++;
                    }
                } catch (InvalidReservationException e) {
                    System.out.println("Skipping invalid record" + e.getMessage());
                }
            }

            System.out.println("Loaded " + count);

        } catch (FileNotFoundException e) {
            System.out.println(e.getMessage());
        } catch (IOException e) {
            System.out.println(e.getMessage());
        } finally {
            try {
                if (reader != null ) {
                    reader.close();
                }
            } catch (IOException e) {
                System.out.println(e.getMessage());
            }
        }
    }

    //Parse a line into a Reservation object
    public static Reservation parseReservation(String line) throws InvalidReservationException {
        String[] parts = line.split("\\|");

        if (parts.length < 10) {
            throw new InvalidReservationException("Invalid data format");
        }

        String type = parts[0];
        String name = parts[1];
        String contact = parts[2];
        LocalDate checkInDate = LocalDate.parse(parts[3]);
        int nights = Integer.parseInt(parts[4]);
        double rate = Double.parseDouble(parts[5]);

        Reservation reservation = null;

        switch (type) {
            case "STANDARD":
                int roomNumber = Integer.parseInt(parts[6]);
                String bedType = parts[7];
                boolean hasWifi = Boolean.parseBoolean(parts[8]);

                reservation = new StandardRoom(name, contact, checkInDate, nights, rate, roomNumber, bedType, hasWifi);

                break;
            case "SUITE":
                int level = Integer.parseInt(parts[6]);
                String suiteType = parts[7];
                boolean hasJacuzzi = Boolean.parseBoolean(parts[8]);

                reservation = new SuiteRoom(name, contact, checkInDate, nights, rate, level, suiteType, hasJacuzzi);
                break;
            case "CONFERENCE":
                String hallName = parts[6];
                int capacity = Integer.parseInt(parts[7]);
                boolean hasProjector = Boolean.parseBoolean(parts[8]);

                reservation = new ConferenceHall(name, contact, checkInDate, nights, rate, hallName, capacity, hasProjector);
                break;

        }
        if (reservation != null && parts.length >= 11) {
            reservation.setBookingID(new BookingID(parts[9]));
            reservation.setIsPaid(Boolean.parseBoolean(parts[10]));

            if (parts.length >= 12) {
                reservation.setRoomStatus(parts[11]);
            }
        }
        if (!parts[12].isEmpty() && parts.length >= 13) {
            ArrayList<String> housekeeping = new ArrayList<>(
            Arrays.asList(parts[12].split(",")));
            reservation.setHousekeepingNotes(housekeeping);
        }
        if (!parts[13].isEmpty() && parts.length >= 14) {
            ArrayList<String> services = new ArrayList<>(
            Arrays.asList(parts[13].split(",")));
            reservation.setGuestServices(services);
        }

        return reservation;
    }

}
