package org.example;

import javax.swing.*;
import java.text.ParseException;
import java.util.ArrayList;

public class Main {
    public static void main(String[] args) throws ParseException {
        ArrayList<Booking> bookings = BookingHandler.readBookingsFromDatabase();

        ArrayList<Resident> residents = Resident.readResidentsFromDatabase();
        ArrayList<Room> rooms = Room.readRoomsFromDatabase();

        Receptionist r = new Receptionist();
       // r.addResident(residents,rooms,bookings);
        r.editResidentInfo(residents,bookings,rooms);
       // Resident.writeResidentsToDatabase(residents);
      //BookingHandler.writeBookingsToDatabase(bookings);
        Room.writeRoomsToDatabase(rooms);

    }
}