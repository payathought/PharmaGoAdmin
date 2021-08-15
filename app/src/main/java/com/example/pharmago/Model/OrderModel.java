package com.example.pharmago.Model;

public class OrderModel {
    private String medecine_name;
    private String medecine_price;
    private String medicine_id;
    private String status;
    private String driver_status;
    private String driver_id;
    private String user_id;
    private String order_id;
    private String paymant_method;
    private String pharmacy_id;
    private String quantity;

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public String getPharmacy_id() {
        return pharmacy_id;
    }

    public void setPharmacy_id(String pharmacy_id) {
        this.pharmacy_id = pharmacy_id;
    }

    public String getMedecine_name() {
        return medecine_name;
    }

    public void setMedecine_name(String medecine_name) {
        this.medecine_name = medecine_name;
    }

    public String getMedecine_price() {
        return medecine_price;
    }

    public void setMedecine_price(String medecine_price) {
        this.medecine_price = medecine_price;
    }

    public String getMedicine_id() {
        return medicine_id;
    }

    public void setMedicine_id(String medicine_id) {
        this.medicine_id = medicine_id;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getDriver_status() {
        return driver_status;
    }

    public void setDriver_status(String driver_status) {
        this.driver_status = driver_status;
    }

    public String getDriver_id() {
        return driver_id;
    }

    public void setDriver_id(String driver_id) {
        this.driver_id = driver_id;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getOrder_id() {
        return order_id;
    }

    public void setOrder_id(String order_id) {
        this.order_id = order_id;
    }

    public String getPaymant_method() {
        return paymant_method;
    }

    public void setPaymant_method(String paymant_method) {
        this.paymant_method = paymant_method;
    }
}
