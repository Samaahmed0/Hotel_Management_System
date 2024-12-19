package org.example;

import java.sql.*;
import java.util.ArrayList;

public class Resident {
    int residentId; // Now residentId will be assigned by the database
    String name;
    String phone_number;

    // Constructor for creating a new Resident (without residentId)
    public Resident(String name, String phone_number) {
        this.name = name;
        this.phone_number = phone_number;
    }

    // Constructor for reading Resident objects from the database
    public Resident(int residentId, String name, String phone_number) {
        this.residentId = residentId;
        this.name = name;
        this.phone_number = phone_number;
    }

    // Getter methods for the fields
    public int getResidentId() {
        return residentId;
    }

    public String getName() {
        return name;
    }

    public String getPhoneNumber() {
        return phone_number;
    }

    // Method to read residents from the database
    public static ArrayList<Resident> readResidentsFromDatabase() {
        ArrayList<Resident> residentsList = new ArrayList<>();
        String sql = "SELECT residentId, name, phone_number FROM residents";

        try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/hotel", "root", "samanour");
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(sql)) {

            while (resultSet.next()) {
                int residentId = resultSet.getInt("residentId");
                String name = resultSet.getString("name");
                String phone_number = resultSet.getString("phone_number");
                residentsList.add(new Resident(residentId, name, phone_number));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return residentsList;
    }

    // Method to write new residents to the database
    public static void writeResidentsToDatabase(ArrayList<Resident> residentsList) {
        String insertSql = "INSERT INTO residents (name, phone_number) VALUES (?, ?)";

        try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/hotel", "root", "samanour");
             PreparedStatement insertStatement = connection.prepareStatement(insertSql, Statement.RETURN_GENERATED_KEYS)) {

            for (Resident resident : residentsList) {
                // Only add residents without a valid residentId (indicating they are new)
                if (resident.residentId == 0) {
                    insertStatement.setString(1, resident.name);
                    insertStatement.setString(2, resident.phone_number);
                    insertStatement.executeUpdate();

                    // Retrieve the generated residentId
                    ResultSet generatedKeys = insertStatement.getGeneratedKeys();
                    if (generatedKeys.next()) {
                        resident.residentId = generatedKeys.getInt(1); // Assign the auto-generated ID
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void setName(String newName) {
    }
}
