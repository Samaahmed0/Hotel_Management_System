package org.example;

import java.sql.SQLException;
import java.util.ArrayList;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

public class Manager{
    public static ArrayList<Worker> workersList;

    static {
        try {
            workersList = Worker.loadWorkersFromDatabase();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private String username = "manager";
    private String password = "manager123";
    private String registeredEmail = "admin@example.com";
    private static Manager admin = new Manager();
    private Manager(){};
    public static Manager getmanager() {
        return admin;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public static void saveWorkers() {
        try {
            Worker.saveWorkersToDatabase(workersList);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void manageWorkers() {
        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.println("Choose an option:");
            System.out.println("1. Add Worker");
            System.out.println("2. Edit Worker");
            System.out.println("3. Delete Worker");
            System.out.println("4. Exit");

            int choice = scanner.nextInt();
            scanner.nextLine(); // Consume newline character

            switch (choice) {
                case 1:
                    System.out.print("Enter Name: ");
                    String name = scanner.nextLine();
                    System.out.print("Enter Contact Info: ");
                    String contactInfo = scanner.nextLine();
                    System.out.print("Enter Salary: ");
                    double salary = scanner.nextDouble();
                    scanner.nextLine(); // Consume newline character
                    System.out.print("Enter Job Title: ");
                    String jobTitle = scanner.nextLine();
                    addWorker(name, contactInfo, salary, jobTitle);
                    break;
                case 2:
                    System.out.print("Enter Worker ID to Edit: ");
                    int workerIdToEdit = scanner.nextInt();
                    scanner.nextLine(); // Consume newline character

                    // Get updated information (you can make this more user-friendly)
                    System.out.print("Enter New Name (or press Enter to skip): ");
                    String newName = scanner.nextLine();
                    System.out.print("Enter New Contact Info (or press Enter to skip): ");
                    String newContactInfo = scanner.nextLine();
                    System.out.print("Enter New Salary (or press Enter to skip): ");
                    String salaryStr = scanner.nextLine();
                    Double newSalary = salaryStr.isEmpty() ? null : Double.parseDouble(salaryStr);
                    System.out.print("Enter New Job Title (or press Enter to skip): ");
                    String newJobTitle = scanner.nextLine();
                    editWorker(workerIdToEdit, newName, newContactInfo, newSalary, newJobTitle);
                    break;
                case 3:
                    System.out.print("Enter Worker ID to Delete: ");
                    int workerIdToDelete = scanner.nextInt();
                    scanner.nextLine(); // Consume newline character
                    deleteWorker(workerIdToDelete);
                    break;
                case 4:
                    System.out.println("Exiting...");
                    scanner.close();
                    return;
                default:
                    System.out.println("Invalid choice. Please select a valid option.");
            }
        }
    }
    public void addWorker(String name, String contactinfo, double salary,String jobtitle) {
        Worker newWorker = new Worker(name, contactinfo, salary, jobtitle);
        workersList.add(newWorker);
    }
    public void deleteWorker(int workerId) {
        int removedIndex = -1;
        for (int i = 0; i < workersList.size(); i++) {
            if (workersList.get(i).workerId == workerId) {
                removedIndex = i;
                System.out.println("Worker found at index: " + i);
                break;
            }
        }
        if (removedIndex != -1) {
            workersList.remove(removedIndex);
            System.out.println("Worker deleted!");
        } else {
            System.out.println("Worker with ID " + workerId + " not found.");
        }
    }

    public void editWorker(int workerId, String name, String contactInfo, Double salary, String jobTitle) {
        boolean found = false;
        for (Worker worker : workersList) {
            if (worker.workerId == workerId) {
                found = true;
                if (!name.isEmpty()) { // Only update if a new name is provided
                    worker.setName(name);
                }
                if (!contactInfo.isEmpty()) { // Only update if new contact info is provided
                    worker.setContactInfo(contactInfo);
                }
                if (salary != null) { // Only update if a new salary is provided
                    worker.setSalary(salary);
                }
                if (!jobTitle.isEmpty()) { // Only update if a new job title is provided
                    worker.setJobTitle(jobTitle);
                }
                System.out.println("Worker updated successfully!");
                break;
            }
        }
        if (!found) {
            System.out.println("Worker with ID " + workerId + " not found.");
        }
    }

    public void viewWorkers() {
        System.out.println("** Worker List **");
        for (Worker worker : workersList) {
            System.out.println("ID: " + worker.workerId);
            System.out.println("Name: " + worker.name);
            System.out.println("Contact Info: " + worker.contactInfo);
            System.out.println("Salary: " + worker.salary);
            System.out.println("Job Title: " + worker.jobTitle);
            System.out.println("---------------");
        }
    }
    public  void viewResidents(ArrayList<Resident> residentList,ArrayList<Booking> bookingsList) {
        System.out.println("** Residents List **");
        for (Resident resident : residentList) {
            System.out.println("ID: " + resident.residentId);
            System.out.println("Name: " + resident.name);
            System.out.println("Contact Info: " + resident.phone_number);
            for(Booking booking:bookingsList){
                if(resident.residentId==booking.getCustomerId()){
                    System.out.println(booking.getDescription());
                }
            }
            System.out.println("---------------");
        }
    }

    public static void viewRooms(ArrayList<Room> roomsList) {
        if (roomsList.isEmpty()) {
            System.out.println("No rooms available.");
        } else {
            System.out.println("Room Information:");
            for (Room room : roomsList) {
                System.out.println(room);
            }
        }
    }
    public double getTotalIncomeForWeek(ArrayList<Booking> bookingsList) {
        return calculateTotalIncome(new Date(), 7,bookingsList); // 7 days for a week
    }

    // Calculate total income for the past month
    public double getTotalIncomeForMonth(ArrayList<Booking> bookingsList) {
        return calculateTotalIncome(new Date(), 30,bookingsList); // 30 days for a month
    }

    // Calculate total income for the past year
    public double getTotalIncomeForYear(ArrayList<Booking> bookingsList) {
        return calculateTotalIncome(new Date(), 365,bookingsList); // 365 days for a year
    }

    // Calculate total income based on the reference date and time frame
    private double calculateTotalIncome(Date referenceDate, int daysInTimeFrame,ArrayList<Booking> bookingsList) {
        double totalIncome = 0.0;
        for (Booking booking : bookingsList) {
            if (isBookingInTimeFrame(booking, referenceDate, daysInTimeFrame)) {
                totalIncome += booking.getCost();
            }
        }
        return totalIncome;
    }


    private boolean isBookingInTimeFrame(Booking booking, Date referenceDate, int daysInTimeFrame) {
        Date checkInDate = booking.getCheckIn();

        // Calculate the difference in days between the reference date and check-in date
        long differenceInMillis = referenceDate.getTime() - checkInDate.getTime();
        long differenceInDays = TimeUnit.MILLISECONDS.toDays(differenceInMillis);

        // Debug output to track the difference
        System.out.println("Booking Check-In: " + checkInDate + ", Reference Date: " + referenceDate);
        System.out.println("Difference in Days: " + differenceInDays);

        return differenceInDays >= 0 && differenceInDays <= daysInTimeFrame;}

    public String getRegisteredEmail() {
        return registeredEmail;
    }
    public void roomMonitoring(ArrayList<Room> roomList){
        System.out.println("View rooms by :");
        System.out.println("1.All rooms");
        System.out.println("2.Single rooms ");
        System.out.println("3. Double rooms");
        System.out.println("4. Triple rooms");
        System.out.print("Choose an option: ");
        Scanner scanner = new Scanner(System.in);
        int choice = scanner.nextInt();
        scanner.nextLine();
        if(choice==1){
            for(Room rooms:roomList){
                System.out.println("Room id : "+ rooms.RoomId+" Room type : "+ rooms.roomType+" Availablity : "+ (rooms.isAvailable ? "Yes" : "No"));
            }
        } else if(choice==2){
            for(Room rooms:roomList){
                if(rooms.roomType.equals("Single"))
                System.out.println("Room id : "+ rooms.RoomId+" Availablity : "+ (rooms.isAvailable ? "Yes" : "No"));
            }
        }else if(choice==3){
            for(Room rooms:roomList){
                if(rooms.roomType.equals("Double"))
                    System.out.println("Room id : "+ rooms.RoomId+" Availablity : "+ (rooms.isAvailable ? "Yes" : "No"));
            }
        }else if(choice==4){
            for(Room rooms:roomList){
                if(rooms.roomType.equals("Triple"))
                    System.out.println("Room id : "+ rooms.RoomId+" Availablity : "+ (rooms.isAvailable ? "Yes" : "No"));
            }
        }
    }
}
