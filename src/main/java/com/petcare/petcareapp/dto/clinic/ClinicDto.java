package com.petcare.petcareapp.dto.clinic;

public class ClinicDto {
    private Long id;
    private String name;
    private String address;
    private String phoneNumber;
    private String operatingHours;
    private String servicesOffered;
    private Double latitude;
    private Double longitude;

    // Constructors
    public ClinicDto() {}

    public ClinicDto(Long id, String name, String address, String phoneNumber, String operatingHours, String servicesOffered, Double latitude, Double longitude) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.phoneNumber = phoneNumber;
        this.operatingHours = operatingHours;
        this.servicesOffered = servicesOffered;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }
    public String getOperatingHours() { return operatingHours; }
    public void setOperatingHours(String operatingHours) { this.operatingHours = operatingHours; }
    public String getServicesOffered() { return servicesOffered; }
    public void setServicesOffered(String servicesOffered) { this.servicesOffered = servicesOffered; }
    public Double getLatitude() { return latitude; }
    public void setLatitude(Double latitude) { this.latitude = latitude; }
    public Double getLongitude() { return longitude; }
    public void setLongitude(Double longitude) { this.longitude = longitude; }
}
