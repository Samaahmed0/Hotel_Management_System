package org.example;


import java.util.Date;

public abstract class Booking {
    public abstract int getCustomerId();

    public abstract int getRoomId();

    public abstract String getRoomType();

    public abstract String getBoardType();

    public abstract Date getCheckIn();

    public abstract Date getCheckOut();

    public abstract int getNumOfDays();

    public abstract double getCost(); // Base cost

    public abstract String getDescription();


}
