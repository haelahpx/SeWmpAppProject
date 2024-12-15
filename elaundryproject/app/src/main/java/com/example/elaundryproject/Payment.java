package com.example.elaundryproject;

public class Payment {
    private String paymentId;
    private String orderMasterId;
    private String paymentMethod;
    private String paymentStatus;
    private String paymentDate;

    public Payment() {}

    public Payment(String paymentId, String orderMasterId, String paymentMethod, String paymentStatus, String paymentDate) {
        this.paymentId = paymentId;
        this.orderMasterId = orderMasterId;
        this.paymentMethod = paymentMethod;
        this.paymentStatus = paymentStatus;
        this.paymentDate = paymentDate;
    }

    public String getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(String paymentId) {
        this.paymentId = paymentId;
    }

    public String getOrderMasterId() {
        return orderMasterId;
    }

    public void setOrderMasterId(String orderMasterId) {
        this.orderMasterId = orderMasterId;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public String getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(String paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    public String getPaymentDate() {
        return paymentDate;
    }

    public void setPaymentDate(String paymentDate) {
        this.paymentDate = paymentDate;
    }
}
