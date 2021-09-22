package com.example.pharmago.Model;

import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;

public class MyOrderModel {
    private String status;
    private String driver_status;
    private String driver_id;
    private String user_id;
    private String myOrder_id;
    private String payment_method;
    private String pharmacy_id;
    private @ServerTimestamp Date dateOrdered;

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

    public String getMyOrder_id() {
        return myOrder_id;
    }

    public void setMyOrder_id(String myOrder_id) {
        this.myOrder_id = myOrder_id;
    }

    public String getPayment_method() {
        return payment_method;
    }

    public void setPayment_method(String payment_method) {
        this.payment_method = payment_method;
    }

    public String getPharmacy_id() {
        return pharmacy_id;
    }

    public void setPharmacy_id(String pharmacy_id) {
        this.pharmacy_id = pharmacy_id;
    }

    public Date getDateOrdered() {
        return dateOrdered;
    }

    public void setDateOrdered(Date dateOrdered) {
        this.dateOrdered = dateOrdered;
    }
}
