package org.example;

import java.sql.*;
import java.util.ArrayList;

public class Room {
    int RoomId;
    String roomType; // e.g., "Single", "Double", "Triple"
    boolean isAvailable; // true if the room is available, false if occupied

    // Constructor
    public Room(int roomNumber, String roomType, boolean isAvailable) {
        this.RoomId = roomNumber;
        this.roomType = roomType;
        this.isAvailable = isAvailable;
    }

    // Getters and Setters
    public int getRoomId() {
        return RoomId;
    }

    public String getRoomType() {
        return roomType;
    }

    public boolean isAvailable() {
        return isAvailable;
    }

    public void setAvailable(boolean isAvailable) {
        this.isAvailable = isAvailable;
    }

    @Override
    public String toString() {
        return "Room Number: " + RoomId +
                ", Type: " + roomType +
                ", Available: " + (isAvailable ? "Yes" : "No");
    }

    // Database credentials
    static final String URL = "jdbc:mysql://localhost:3306/hotel"; // Replace with your database URL
    static final String USER = "root"; // Replace with your MySQL username
    static final String PASSWORD = "samanour"; // Replace with your MySQL password

    // Method to read rooms from the database and return as an ArrayList
    public static ArrayList<Room> readRoomsFromDatabase() {
        ArrayList<Room> roomsList = new ArrayList<>();
        String sql = "SELECT RoomId, roomType, isAvailable FROM rooms"; // Query to select room details

        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(sql)) {

            // Read the results from the database and create Room objects
            while (resultSet.next()) {
                int roomId = resultSet.getInt("RoomId");
                String roomType = resultSet.getString("roomType");
                boolean isAvailable = resultSet.getBoolean("isAvailable");

                // Create Room object and add to the list
                Room room = new Room(roomId, roomType, isAvailable);
                roomsList.add(room);
            }

        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Error reading rooms from the database.");
        }

        return roomsList; // Return the list of rooms
    }

    // Method to write rooms back to the database from the ArrayList
    public static void writeRoomsToDatabase(ArrayList<Room> roomsList) {
        String sql = "INSERT INTO rooms (RoomId, roomType, isAvailable) " +
                "VALUES (?, ?, ?) ON DUPLICATE KEY UPDATE roomType = ?, isAvailable = ?";

        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            for (Room room : roomsList) {
                // Set the parameters for the query
                preparedStatement.setInt(1, room.getRoomId());
                preparedStatement.setString(2, room.getRoomType());
                preparedStatement.setBoolean(3, room.isAvailable());
                preparedStatement.setString(4, room.getRoomType()); // For the update part
                preparedStatement.setBoolean(5, room.isAvailable()); // For the update part

                // Execute the update
                preparedStatement.executeUpdate();
            }

            System.out.println("Rooms successfully written to the database.");

        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Error writing rooms to the database.");
        }

    }
}
