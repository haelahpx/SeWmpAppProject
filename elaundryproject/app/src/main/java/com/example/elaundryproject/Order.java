package com.example.elaundryproject;

public class Order {
    private String categoryName;
    private String orderDate;
    private String orderId;
    private String orderStatus;
    private String userId;
    private Long price; // Added price field
    private String shopId; // Added shopId field

    public Order() { }

    public String getCategoryName() { return categoryName; }
    public void setCategoryName(String categoryName) { this.categoryName = categoryName; }

    public String getOrderDate() { return orderDate; }
    public void setOrderDate(String orderDate) { this.orderDate = orderDate; }

    public String getOrderId() { return orderId; }
    public void setOrderId(String orderId) { this.orderId = orderId; }

    public String getOrderStatus() { return orderStatus; }
    public void setOrderStatus(String orderStatus) { this.orderStatus = orderStatus; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public Long getPrice() { return price; }
    public void setPrice(Long price) { this.price = price; }

    public String getShopId() { return shopId; }
    public void setShopId(String shopId) { this.shopId = shopId; }
}
