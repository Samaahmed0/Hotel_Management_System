package org.example;

import java.util.Date;

public abstract class BookingDecorator extends Booking {
    protected Booking booking;

    public BookingDecorator(Booking booking) {
        this.booking = booking;
    }

    @Override
    public double getCost() {
        return booking.getCost();
    }

    @Override
    public String getDescription() {
        return booking.getDescription();
    }



    @Override
    public int getCustomerId() {
        return booking.getCustomerId();
    }

    @Override
    public int getRoomId() {
        return booking.getRoomId();
    }

    @Override
    public String getRoomType() {
        return booking.getRoomType();
    }

    @Override
    public String getBoardType() {
        return booking.getBoardType();
    }

    @Override
    public Date getCheckIn() {
        return booking.getCheckIn();
    }

    @Override
    public Date getCheckOut() {
        return booking.getCheckOut();
    }

    @Override
    public int getNumOfDays() {
        return booking.getNumOfDays();
    }

}

