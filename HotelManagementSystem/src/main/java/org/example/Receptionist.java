package org.example;

import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Scanner;
import java.text.ParseException;
public class Receptionist {

    String receptionistUserName;
    String receptionistPassword;
    public static ArrayList<Resident> residentsList = new ArrayList<>();
    public static ArrayList<Booking> bookingList = new ArrayList<>();
    public static ArrayList<Room> roomsList = new ArrayList<>();
    Scanner scanner = new Scanner(System.in);
    SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");

    public static void registerReceptionist(String username, String password) throws SQLException {
        String sql = "INSERT INTO receptionist (UserName, Password) VALUES (?, ?)";

        try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/hotel", "root", "samanour");
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setString(1, username);
            preparedStatement.setString(2, password);
            preparedStatement.executeUpdate();
            System.out.println("Receptionist registered successfully.");

        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Error registering receptionist.");
        }
    }

    public static boolean loginReceptionist(String username, String password) {
        String sql = "SELECT * FROM receptionist WHERE UserName = ? AND Password = ?";

        try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/hotel", "root", "samanour");
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setString(1, username);
            preparedStatement.setString(2, password);

            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                System.out.println("Login successful!");
                return true;  // User found, login successful
            } else {
                System.out.println("Invalid username or password.");
                return false;  // No user found
            }

        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Error during login.");
            return false;
        }
    }

    void residentManagement(Resident resident, ArrayList<Resident> residentsList, ArrayList<Booking> bookingList, ArrayList<Room> roomsList) throws ParseException {
        addResident(residentsList, roomsList, bookingList);
        // editResident(resident,residentsList,bookingList);

    }

    void addResident(ArrayList<Resident> residentsList, ArrayList<Room> roomsList, ArrayList<Booking> bookingList) throws ParseException {
        System.out.print("Enter resident name: ");
        String name = scanner.nextLine();
        System.out.print("Enter resident phone number: ");
        String phone_number = scanner.nextLine();
        Resident r = new Resident(name, phone_number);
        residentsList.add(r);
        Resident.writeResidentsToDatabase(residentsList);
        System.out.println(r.residentId);

        System.out.println("Enter Booking details: ");
        System.out.print("Enter room type: ");
        String type = scanner.nextLine();
        int roomId = roomAssigment(roomsList, type);
        if (roomId != -1) {

            System.out.println("Enter board type:");
            String boardtype = scanner.nextLine();

            System.out.print("Enter Check-in date (dd/MM/yyyy): ");
            Date checkInDate = dateFormat.parse(scanner.nextLine());

            System.out.print("Enter Check-out date (dd/MM/yyyy): ");
            Date checkOutDate = dateFormat.parse(scanner.nextLine());
            System.out.println(r.residentId);

            Booking baseBooking = new BookingBuilder()
                    .setCustomerId(r.residentId)
                    .setRoomId(roomId)
                    .setRoomType(type)
                    .setBoardType(boardtype)
                    .setCheckIn(checkInDate)
                    .setCheckOut(checkOutDate)
                    .setbaseCost()
                    .build();

            System.out.println("Do you want to add Spa? y/n");
            String ans = scanner.nextLine();
            if (ans.equals("y")) {
                baseBooking = new SpaDecorator(baseBooking);
            }

            System.out.println("Do you want to add Wifi? y/n");
            String ans2 = scanner.nextLine();
            if (ans2.equals("y")) {
                baseBooking = new WiFiDecorator(baseBooking);
            }

            System.out.println("Do you want to add Parking? y/n");
            String ans3 = scanner.nextLine();
            if (ans3.equals("y")) {
                baseBooking = new ParkingDecorator(baseBooking);
            }

            System.out.println(baseBooking.getDescription());
            System.out.println("Total Cost: $" + baseBooking.getCost());

            bookingList.add(baseBooking);

        }

    }

    void deleteResident(ArrayList<Resident> residentsList) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter resident ID to delete:");
        int id = scanner.nextInt();
        scanner.nextLine(); // Consume the newline character

        boolean found = false;

        // Step 1: Delete from the database
        String sql = "DELETE FROM residents WHERE residentId = ?";
        try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/hotel", "root", "samanour");
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setInt(1, id);
            int rowsAffected = preparedStatement.executeUpdate();

            if (rowsAffected > 0) {
                // Step 2: Remove from ArrayList
                for (int i = 0; i < residentsList.size(); i++) {
                    if (residentsList.get(i).residentId == id) {
                        residentsList.remove(i);
                        found = true;
                        break;
                    }
                }
                System.out.println("Resident deleted successfully.");
            } else {
                System.out.println("Resident with ID " + id + " not found in the database.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        if (!found) {
            System.out.println("Resident with ID " + id + " not found in the list.");
        }
    }

    void calcResidentCost(ArrayList<Booking> bookingList, ArrayList<Resident> residentsList) {
        System.out.println("Enter the resident id: ");
        int id = scanner.nextInt();
        scanner.nextLine();
        boolean userfound = false;
        for (int i = 0; i < residentsList.size(); i++) {
            if (residentsList.get(i).residentId == id) {
                userfound = true;
                break;
            }
        }
        if (userfound) {
            boolean bookingfound = false;
            for (int i = 0; i < bookingList.size(); i++) {
                if (bookingList.get(i).getCustomerId() == id) {
                    bookingfound = true;
                    System.out.println(bookingList.get(i).getDescription());
                    System.out.println("Total Cost: $" + bookingList.get(i).getCost());

                }
            }
            if (!bookingfound) {
                System.out.println("This Resdient has no bookings yet!");
            }
        }
        if (!userfound) {
            System.out.println("Resident with ID " + id + " not found.");
        }


    }

    public int roomAssigment(ArrayList<Room> roomsList, String type) {
        ArrayList<Integer> availableRooms = new ArrayList<>();
        int roomId = 1;
        for (Room room : roomsList) {
            if (room.isAvailable && room.roomType.equals(type)) {
                System.out.println("Room:" + room.RoomId);
                availableRooms.add(room.RoomId);
            }
        }
        if (availableRooms.isEmpty()) {
            System.out.println("No rooms are available");
        } else {
            boolean validRoom = false;

            do {
                System.out.println("Choose the room id: ");
                roomId = scanner.nextInt();
                scanner.nextLine();

                if (availableRooms.contains(roomId)) {
                    validRoom = true;
                } else {
                    System.out.println("Invalid room ID. Please choose a valid room ID from the list: " + availableRooms);
                }
            } while (!validRoom);

            for (int i = 0; i < roomsList.size(); i++) {
                if (roomsList.get(i).RoomId == roomId) {
                    roomsList.get(i).isAvailable = false;
                }
            }
        }
        return roomId;
    }


    public void editResidentInfo(ArrayList<Resident> residentsList, ArrayList<Booking> bookingsList, ArrayList<Room> roomsList) {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter Resident ID to edit: ");
        int residentId = scanner.nextInt();
        scanner.nextLine(); // Consume the newline character

        Resident resident = findResidentById(residentsList, residentId);
        if (resident == null) {
            System.out.println("Resident with ID " + residentId + " not found.");
            return;
        }

        System.out.println("What do you want to change?");
        System.out.println("1. Personal Info");
        System.out.println("2. Booking Details");
        System.out.print("Enter choice: ");
        int choice = scanner.nextInt();
        scanner.nextLine(); // Consume the newline character

        switch (choice) {
            case 1:
                editPersonalInfo(resident, scanner);
                break;
            case 2:
                editBookingDetails(resident, bookingsList, roomsList, scanner);
                break;
            default:
                System.out.println("Invalid choice.");
                break;
        }
    }

    private Resident findResidentById(ArrayList<Resident> residentsList, int residentId) {
        for (Resident resident : residentsList) {
            if (resident.getResidentId() == residentId) {
                return resident;
            }
        }
        return null;
    }

    private void editPersonalInfo(Resident resident, Scanner scanner) {
        System.out.println("Editing Personal Information (leave blank to skip):");
        System.out.print("Enter new name: ");
        String newName = scanner.nextLine();
        System.out.print("Enter new phone number: ");
        String newPhoneNumber = scanner.nextLine();

        // Update ArrayList fields
        if (!newName.isBlank()) {
            resident.name = newName;
        }
        if (!newPhoneNumber.isBlank()) {
            resident.phone_number = newPhoneNumber;
        }

        // Update the database
        try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/hotel", "root", "samanour")) {
            String updateQuery = "UPDATE residents SET " +
                    "name = COALESCE(NULLIF(?, ''), name), " +
                    "phone_number = COALESCE(NULLIF(?, ''), phone_number) " +
                    "WHERE residentId = ?";
            try (PreparedStatement pstmt = connection.prepareStatement(updateQuery)) {
                pstmt.setString(1, newName);
                pstmt.setString(2, newPhoneNumber);
                pstmt.setInt(3, resident.getResidentId());
                int rowsAffected = pstmt.executeUpdate();
                if (rowsAffected > 0) {
                    System.out.println("Resident details updated successfully.");
                } else {
                    System.out.println("Failed to update resident in the database.");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void editBookingDetails(Resident resident, ArrayList<Booking> bookingsList, ArrayList<Room> roomsList, Scanner scanner) {
        BasicBooking basicBooking = null;
        int newRoomId=0;

        for (Booking booking : bookingsList) {
            if (booking.getCustomerId() == resident.residentId) {
                basicBooking = BasicBooking.getBasicBooking(booking);
            }
        }

        if (basicBooking != null) {
            System.out.println("Editing Booking Information (leave blank to skip):");
            System.out.print("Enter new room type: ");
            String newRoomType = scanner.nextLine();
            boolean roomChanged = false;
            if (!newRoomType.isBlank()) {
                int oldRoomId = basicBooking.getRoomId();
                newRoomId = roomAssigment(roomsList, newRoomType);
                basicBooking.setroomId(newRoomId);
                roomChanged = true;

                for (Room room : roomsList) {
                    if (room.RoomId == oldRoomId) {
                        room.isAvailable = true;
                        break;
                    }
                }
                basicBooking.setRoomType(newRoomType);
            }

            System.out.print("Enter new board type: ");
            String newBoardType = scanner.nextLine();
            System.out.print("Enter new check-in date (yyyy-MM-dd): ");
            String newCheckInStr = scanner.nextLine();
            System.out.print("Enter new check-out date (yyyy-MM-dd): ");
            String newCheckOutStr = scanner.nextLine();

            if (!newBoardType.isBlank()) {
                basicBooking.setBoardType(newBoardType);
            }
            if (!newCheckInStr.isBlank()) {
                java.sql.Date newCheckInSqlDate = java.sql.Date.valueOf(newCheckInStr);
                basicBooking.setCheckIn(newCheckInSqlDate);
            }
            if (!newCheckOutStr.isBlank()) {
                java.sql.Date newCheckOutSqlDate = java.sql.Date.valueOf(newCheckOutStr);
                basicBooking.setCheckOut(newCheckOutSqlDate);
            }

            basicBooking.calcCost();

            // Update the booking in the database
            try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/hotel", "root", "samanour")) {
                String updateBookingQuery = "UPDATE bookings SET " +
                        (roomChanged ? "room_id = ?, " : "")  +
                        "room_type = COALESCE(NULLIF(?, ''), room_type), " +
                        "board_type = COALESCE(NULLIF(?, ''), board_type), " +
                        "check_in = COALESCE(NULLIF(?, ''), check_in), " +
                        "check_out = COALESCE(NULLIF(?, ''), check_out) " +
                        "WHERE booking_id = ?";
                try (PreparedStatement pstmt = connection.prepareStatement(updateBookingQuery)) {
                    pstmt.setInt(1, newRoomId);
                    pstmt.setString(2, newRoomType);
                    pstmt.setString(3, newBoardType);
                    pstmt.setDate(4, newCheckInStr.isBlank() ? null : java.sql.Date.valueOf(newCheckInStr));
                    pstmt.setDate(5, newCheckOutStr.isBlank() ? null : java.sql.Date.valueOf(newCheckOutStr));
                    pstmt.setInt(6, basicBooking.bookingId);

                    int rowsAffected = pstmt.executeUpdate();
                    if (rowsAffected > 0) {
                        System.out.println("Booking details updated successfully.");
                    } else {
                        System.out.println("Failed to update booking in the database.");
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}










