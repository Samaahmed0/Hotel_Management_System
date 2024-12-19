package org.example;

import java.sql.*;
import java.util.ArrayList;
import java.util.Date;

public class BookingHandler {
    private static final String DB_URL = "jdbc:mysql://localhost:3306/hotel"; // Replace with your database URL
    private static final String DB_USER = "root"; // Replace with your MySQL username
    private static final String DB_PASSWORD = "samanour"; // Replace with your MySQL password

    // Read bookings from the database
    public static ArrayList<Booking> readBookingsFromDatabase() {
        ArrayList<Booking> bookings = new ArrayList<>();
        String query = "SELECT * FROM bookings";

        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {

            while (resultSet.next()) {
                int id = resultSet.getInt("booking_id");
                int customerId = resultSet.getInt("customer_id");
                int roomId = resultSet.getInt("room_id");
                String roomType = resultSet.getString("room_type");
                String boardType = resultSet.getString("board_type");
                Date checkIn = resultSet.getDate("check_in");
                Date checkOut = resultSet.getDate("check_out");
                boolean spa = resultSet.getBoolean("spa");
                boolean wifi = resultSet.getBoolean("wifi");
                boolean parking = resultSet.getBoolean("parking");

                Booking booking = new BasicBooking(customerId, roomId, roomType, boardType, checkIn, checkOut, id);

                if (spa) booking = new SpaDecorator(booking);
                if (wifi) booking = new WiFiDecorator(booking);
                if (parking) booking = new ParkingDecorator(booking);

                bookings.add(booking);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return bookings;
    }

    // Write bookings to the database
    public static void writeBookingsToDatabase(ArrayList<Booking> bookings) {
        String query = "REPLACE INTO bookings (booking_id, customer_id, room_id, room_type, board_type, check_in, check_out, spa, wifi, parking) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            for (Booking booking : bookings) {
                BasicBooking basicBooking = (BasicBooking) getBasicBooking(booking);

                preparedStatement.setInt(1, basicBooking.bookingId);
                preparedStatement.setInt(2, basicBooking.customerId);
                preparedStatement.setInt(3, basicBooking.roomId);
                preparedStatement.setString(4, basicBooking.roomType);
                preparedStatement.setString(5, basicBooking.boardType);
                preparedStatement.setDate(6, new java.sql.Date(basicBooking.checkIn.getTime()));
                preparedStatement.setDate(7, new java.sql.Date(basicBooking.checkOut.getTime()));
                preparedStatement.setBoolean(8, hasDecorator(booking, SpaDecorator.class));
                preparedStatement.setBoolean(9, hasDecorator(booking, WiFiDecorator.class));
                preparedStatement.setBoolean(10, hasDecorator(booking, ParkingDecorator.class));

                preparedStatement.executeUpdate();
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Extract the BasicBooking object from a Booking with decorators
    private static BasicBooking getBasicBooking(Booking booking) {
        while (booking instanceof BookingDecorator) {
            booking = ((BookingDecorator) booking).booking;
        }
        return (BasicBooking) booking;
    }

    // Check if a Booking has a specific decorator
    private static boolean hasDecorator(Booking booking, Class<? extends BookingDecorator> decoratorClass) {
        while (booking instanceof BookingDecorator) {
            if (decoratorClass.isInstance(booking)) {
                return true;
            }
            booking = ((BookingDecorator) booking).booking;
        }
        return false;
    }
}
