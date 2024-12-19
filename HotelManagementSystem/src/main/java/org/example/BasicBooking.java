package org.example;


import java.text.SimpleDateFormat;
import java.util.Date;

public class BasicBooking extends Booking {
    private static int idCounter = 1;
    public int bookingId;
    public int customerId;
    public int roomId;
    public String roomType;//ex: single , double , triple
    public String boardType; // Example: "Full Board", "Half Board", "Bed and Breakfast"
    public Date checkIn;
    public Date checkOut;
    public int numOfDays;
    private double baseCost = 0;

    public BasicBooking(int customerId, int roomId, String roomType, String boardType, Date checkIn, Date checkOut, double baseCost) {
        this.bookingId = idCounter++;
        this.customerId = customerId;
        this.roomId = roomId;
        this.roomType = roomType;
        this.boardType = boardType;
        this.checkIn = checkIn;
        this.checkOut = checkOut;
        this.baseCost = baseCost;
        this.numOfDays = calculateNumberOfDays();
        calcCost();

    }

    public void calcCost() {
        //function bthsb el basecost 3la asas el board wel room type
        if (roomType.equalsIgnoreCase("single")) {
            baseCost += 100 * numOfDays;
        } else if (roomType.equalsIgnoreCase("double")) {
            baseCost += 200 * numOfDays;
        } else if (roomType.equalsIgnoreCase("triple")) {
            baseCost += 300 * numOfDays;
        }

        if (boardType.equalsIgnoreCase("half board")) {
            baseCost += 200;
        } else if (boardType.equalsIgnoreCase("full board")) {
            baseCost += 400;
        } else if (boardType.equalsIgnoreCase("bed with breakfast")) {
            baseCost += 500;
        }

    }

    private int calculateNumberOfDays() {
        long differenceInMillis = checkOut.getTime() - checkIn.getTime();
        return (int) (differenceInMillis / (1000 * 60 * 60 * 24)); // Convert milliseconds to days
    }

    @Override
    public double getCost() {
        return baseCost;
    }

    @Override
    public String getDescription() {
        return "Booking with id : " + bookingId + " for customer " + customerId + ", Room " + roomId + ", Board Type: " + boardType + ", Number of Days: " + numOfDays;
    }

    public int getCustomerId() {
        return this.customerId;
    }

    public int getRoomId() {
        return this.roomId;
    }


    public String getRoomType() {
        return this.roomType;
    }


    public String getBoardType() {
        return this.boardType;
    }

    public Date getCheckIn() {
        return this.checkIn;
    }

    public Date getCheckOut() {
        return this.checkOut;
    }

    public int getNumOfDays() {
        return this.numOfDays;
    }

    public void setRoomType(String newRoomType) { this.roomType = newRoomType; calcCost();  }
    public void setBoardType(String newBoardType) { this.boardType = newBoardType; calcCost(); }
    public void setCheckIn(Date newCheckInSqlDate) { this.checkIn = new Date(newCheckInSqlDate.getTime()); this.numOfDays = calculateNumberOfDays(); calcCost(); }
    public void setCheckOut(Date newCheckOutSqlDate) { this.checkOut = new Date(newCheckOutSqlDate.getTime()); this.numOfDays = calculateNumberOfDays(); calcCost(); }

    public static BasicBooking getBasicBooking(Booking booking) { while (booking instanceof BookingDecorator) {
        booking = ((BookingDecorator) booking).booking;
    } if (booking instanceof BasicBooking) {
        return (BasicBooking) booking; }
    else { return null; }
    }

    public void setroomId(int newRoomId) {
        this.roomId=newRoomId;
    }
}



