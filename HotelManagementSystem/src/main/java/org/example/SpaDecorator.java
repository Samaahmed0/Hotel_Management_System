package org.example;

public class SpaDecorator extends BookingDecorator {

    public SpaDecorator(Booking booking) {
        super(booking);
    }

    @Override
    public double getCost() {
        return super.getCost() + 70.0;
    }

    @Override
    public String getDescription() {
        return super.getDescription() + ", with Spa";
    }


}


