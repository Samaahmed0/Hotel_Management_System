package org.example;

public class ParkingDecorator extends BookingDecorator {
    public ParkingDecorator(Booking booking) {
        super(booking);
    }

    @Override
    public double getCost() {
        return super.getCost() + 15.0; // Add parking cost
    }

    @Override
    public String getDescription() {
        return super.getDescription() + ", with Parking";
    }
}

