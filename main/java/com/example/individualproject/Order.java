package com.example.individualproject;

import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

public class Order {
    private String UserId;
    private String CustomerUId;
    private String DescriptionId;
    private String Food;
    private String Description;
    private String Adresa;
    private String Status;
    private double distance;
    private double Longitude;
    private double Latitude;
    private String EmailOrders;
    private String PhoneOrders;
    private int GradeOrders;
    private int GradeCustomer;
    private String ReportOrders;
    private String ReportCustomer;
    private int StatusId;

    public Order(){

    }


    public Order(String userID, String customerUId, String food, String description, String address, String status, double longitude, double latitude, String emailOrders, String phoneOrders, int gradeOrders, int gradeCustomer, String reportOrders, String reportCustomer){
       this.UserId = userID;
        this.CustomerUId = customerUId;
        this.Food = food;
        this.Description = description;
        this.Adresa = address;
        this.Status = status;
        this.Longitude = longitude;
        this.Latitude = latitude;
        this.EmailOrders = emailOrders;
        this.PhoneOrders = phoneOrders;
        this.GradeOrders = gradeOrders;
        this.GradeCustomer = gradeCustomer;
        this.ReportOrders = reportOrders;
        this.ReportCustomer = reportCustomer;
    }

    public String getFood() {
        return Food;
    }

    public void setFood(String food) {
        Food = food;
    }

    public String getUserId() {
        return UserId;
    }

    public void setUserId(String userId) {
        UserId = userId;
    }

    public String getCustomerUId() {
        return CustomerUId;
    }

    public void setCustomerUId(String customerUId) {
        CustomerUId = customerUId;
    }

    public String getDescriptionId() {
        return DescriptionId;
    }

    public void setDescriptionId(String descriptionId) {
        DescriptionId = descriptionId;
    }

    public String getDescription() {
        return Description;
    }

    public void setDescription(String description) {
        Description = description;
    }

    public String getAdresa() {
        return Adresa;
    }

    public void setAdresa(String adresa) {
        Adresa = adresa;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public double getLongitude() {
        return Longitude;
    }

    public void setLongitude(double longitude) {
        Longitude = longitude;
    }

    public double getLatitude() {
        return Latitude;
    }

    public void setLatitude(double latitude) {
        Latitude = latitude;
    }

    public String getEmailOrders() {
        return EmailOrders;
    }

    public void setEmailOrders(String emailOrders) {
        EmailOrders = emailOrders;
    }

    public String getPhoneOrders() {
        return PhoneOrders;
    }

    public void setPhoneOrders(String phoneOrders) {
        PhoneOrders = phoneOrders;
    }

    public int getGradeOrders() {
        return GradeOrders;
    }

    public void setGradeOrders(int gradeOrders) {
        GradeOrders = gradeOrders;
    }

    public int getGradeCustomer() {
        return GradeCustomer;
    }

    public void setGradeCustomer(int gradeCustomer) {
        GradeCustomer = gradeCustomer;
    }

    public String getReportOrders() {
        return ReportOrders;
    }

    public void setReportOrders(String reportOrders) {
        ReportOrders = reportOrders;
    }

    public String getReportCustomer() {
        return ReportCustomer;
    }

    public void setReportCustomer(String reportCustomer) {
        ReportCustomer = reportCustomer;
    }

    public int getStatusId() {
        return StatusId;
    }

    public void setStatusId(int statusId) {
        StatusId = statusId;
    }

    public String getStatus() {
        return Status;
    }

    public void setStatus(String status) {
        Status = status;
    }
}
