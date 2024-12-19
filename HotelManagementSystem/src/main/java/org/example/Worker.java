package org.example;

import java.sql.*;
import java.util.ArrayList;

public class Worker {
    int workerId;
    String name;
    String contactInfo;
    double salary;
    String jobTitle;

    // Constructor for new workers (without workerId)
    public Worker(String name, String contactInfo, double salary, String jobTitle) {
        this.name = name;
        this.contactInfo = contactInfo;
        this.salary = salary;
        this.jobTitle = jobTitle;
    }

    // Constructor for workers fetched from the database (with workerId)
    public Worker(int workerId, String name, String contactInfo, double salary, String jobTitle) {
        this.workerId = workerId;
        this.name = name;
        this.contactInfo = contactInfo;
        this.salary = salary;
        this.jobTitle = jobTitle;
    }

    private void setWorkerId(int newWorkerId) {
        this.workerId=newWorkerId;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getName() {
        return name;
    }

    public String getContactInfo() {
        return contactInfo;
    }

    public void setContactInfo(String contactInfo) {
        this.contactInfo = contactInfo;
    }

    public double getSalary() {
        return salary;
    }

    public void setSalary(double salary) {
        this.salary = salary;
    }

    public String getJobTitle() {
        return jobTitle;
    }

    public void setJobTitle(String jobTitle) {
        this.jobTitle = jobTitle;
    }
    private int getWorkerId() {
        return this.workerId;
    }
    public static ArrayList<Worker> loadWorkersFromDatabase() throws SQLException {
        ArrayList<Worker> workersList = new ArrayList<>();
        Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/hotel", "root", "samanour");
        String query = "SELECT id, name, contact_info, salary, job_title FROM workers";
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery(query);

        while (resultSet.next()) {
            int id = resultSet.getInt("id");
            String name = resultSet.getString("name");
            String contactInfo = resultSet.getString("contact_info");
            double salary = resultSet.getDouble("salary");
            String jobTitle = resultSet.getString("job_title");

            Worker worker = new Worker(id, name, contactInfo, salary, jobTitle);
            workersList.add(worker);
        }

        resultSet.close();
        statement.close();
        connection.close();

        return workersList;
    }

    public static void saveWorkersToDatabase(ArrayList<Worker> workersList) throws SQLException {
        Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/hotel", "root", "samanour");

        // Step 1: Retrieve all worker IDs from the database
        String fetchIdsQuery = "SELECT id FROM workers";
        PreparedStatement fetchIdsStatement = connection.prepareStatement(fetchIdsQuery);
        ResultSet resultSet = fetchIdsStatement.executeQuery();

        ArrayList<Integer> databaseIds = new ArrayList<>();
        while (resultSet.next()) {
            databaseIds.add(resultSet.getInt("id"));
        }
        resultSet.close();
        fetchIdsStatement.close();

        // Step 2: Get IDs of workers currently in the workersList
        ArrayList<Integer> currentWorkerIds = new ArrayList<>();
        for (Worker worker : workersList) {
            currentWorkerIds.add(worker.getWorkerId());
        }

        // Step 3: Find IDs to delete (in database but not in workersList)
        databaseIds.removeAll(currentWorkerIds);

        // Step 4: Delete workers with these IDs
        if (!databaseIds.isEmpty()) {
            String deleteQuery = "DELETE FROM workers WHERE id = ?";
            PreparedStatement deleteStatement = connection.prepareStatement(deleteQuery);

            for (int id : databaseIds) {
                deleteStatement.setInt(1, id);
                deleteStatement.addBatch();
            }

            deleteStatement.executeBatch();
            deleteStatement.close();
        }

        // Step 5: Insert or update workers
        String upsertQuery = "INSERT INTO workers (id, name, contact_info, salary, job_title) " +
                "VALUES (?, ?, ?, ?, ?) ON DUPLICATE KEY UPDATE " +
                "name = VALUES(name), contact_info = VALUES(contact_info), salary = VALUES(salary), job_title = VALUES(job_title)";
        PreparedStatement upsertStatement = connection.prepareStatement(upsertQuery);

        for (Worker worker : workersList) {
            upsertStatement.setInt(1, worker.getWorkerId());
            upsertStatement.setString(2, worker.getName());
            upsertStatement.setString(3, worker.getContactInfo());
            upsertStatement.setDouble(4, worker.getSalary());
            upsertStatement.setString(5, worker.getJobTitle());
            upsertStatement.addBatch();
        }

        upsertStatement.executeBatch();
        upsertStatement.close();

        connection.close();
    }



}


