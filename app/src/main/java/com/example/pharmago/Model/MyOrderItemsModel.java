package com.example.pharmago.Model;

public class MyOrderItemsModel {
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

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getPharmacy_id() {
        return pharmacy_id;
    }

    public void setPharmacy_id(String pharmacy_id) {
        this.pharmacy_id = pharmacy_id;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public String getCart_id() {
        return cart_id;
    }

    public void setCart_id(String cart_id) {
        this.cart_id = cart_id;
    }

    public String getMyOrder_id() {
        return myOrder_id;
    }

    public void setMyOrder_id(String myOrder_id) {
        this.myOrder_id = myOrder_id;
    }

    private String medecine_name;
    private String medecine_price;
    private String medicine_id;
    private String user_id;
    private String pharmacy_id;
    private String quantity;
    private String cart_id;
    private String myOrder_id;
}
