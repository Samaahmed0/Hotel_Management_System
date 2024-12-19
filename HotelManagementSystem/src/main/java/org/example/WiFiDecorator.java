package org.example;


public class WiFiDecorator extends BookingDecorator {
    public WiFiDecorator(Booking booking) {
        super(booking);
    }

    @Override
    public double getCost() {
        return super.getCost() + 20.0; // Add Wi-Fi cost
    }

    @Override
    public String getDescription() {
        return super.getDescription() + ", with Wi-Fi";
    }
}
