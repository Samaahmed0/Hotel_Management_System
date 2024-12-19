package org.example;

import java.util.Date;

public class BookingBuilder {
     int customerId;
     int roomId;
     String roomType;

     String boardType;
    Date checkIn;
    Date checkOut;
    int numOfDays;
     double baseCost=0;


    public BookingBuilder setCustomerId(int customerId) {
        this.customerId = customerId;
        return this;
    }

    public BookingBuilder setRoomId(int roomId) {
        this.roomId = roomId;
        return this;
    }

    public BookingBuilder setBoardType(String boardType) {
        this.boardType = boardType;
        return this;
    }

    public BookingBuilder setbaseCost() {
        this.baseCost = baseCost;
        return this;
    }

    public BookingBuilder setNumOfDays(int numOfDays) {
        this.numOfDays = numOfDays;
        return this;
    }

    public Booking build() {
        return new BasicBooking(customerId, roomId,roomType, boardType,checkIn,checkOut,baseCost);
    }

    public BookingBuilder setRoomType(String roomType) {
        this.roomType=roomType;
        return this;
    }

    public BookingBuilder setCheckIn(Date checkIn) {
        this.checkIn = checkIn;
        return this;
    }

    public BookingBuilder setCheckOut(Date checkOut) {
        this.checkOut = checkOut;
        return this;
    }


}

