package ua.tqs.smartvolt.smartvolt.dto;
import java.time.LocalDateTime;

public class ChargingSlotsResponse {
    private LocalDateTime[] availableTimeSlots;
    private double pricePerKWh;

    public ChargingSlotsResponse() {}

    public ChargingSlotsResponse(LocalDateTime[] availableTimeSlots, double pricePerKWh) {
        this.availableTimeSlots = availableTimeSlots;
        this.pricePerKWh = pricePerKWh;
    }

    public LocalDateTime[] getAvailableTimeSlots() {
        return availableTimeSlots;
    }

    public void setAvailableTimeSlots(LocalDateTime[] availableTimeSlots) {
        this.availableTimeSlots = availableTimeSlots;
    }

    public double getPricePerKWh() {
        return pricePerKWh;
    }

    public void setPricePerKWh(double pricePerKWh) {
        this.pricePerKWh = pricePerKWh;
    }  
}
