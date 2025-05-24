package ua.tqs.smartvolt.smartvolt.dto;

import java.util.List;

public class ChargingStationResponse {
    private Long stationId;
    private String name;
    private String address;
    private double latitude;
    private double longitude;
    private List<String> stationSlotChargingSpeeds;

    public ChargingStationResponse() {
    }

    public ChargingStationResponse(Long stationId, String name, String address, double latitude, double longitude, List<String> stationSlotChargingSpeeds) {
        this.stationId = stationId;
        this.name = name;
        this.address = address;
        this.latitude = latitude;
        this.longitude = longitude;
        this.stationSlotChargingSpeeds = stationSlotChargingSpeeds;
    }

    public Long getStationId() {
        return stationId;
    }
    public void setStationId(Long stationId) {
        this.stationId = stationId;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }
    public void setAddress(String address) {
        this.address = address;
    }

    public double getLatitude() {
        return latitude;
    }
    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }
    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public List<String> getStationSlotChargingSpeeds() {
        return stationSlotChargingSpeeds;
    }
    public void setStationSlotChargingSpeeds(List<String> stationSlotChargingSpeeds) {
        this.stationSlotChargingSpeeds = stationSlotChargingSpeeds;
    }


}
