package com.example.elaundryproject;

public class Order {
    private String categoryName;
    private String orderDate;
    private String orderId;
    private String orderStatus;
    private String userId;
    private Long price;
    private String shopId;

    public Order() { }

    public Order(String orderDate, String userId, String orderStatus,
                 String categoryName, Long price, String shopId) {
        this.orderDate = orderDate;
        this.userId = userId;
        this.orderStatus = orderStatus;
        this.categoryName = categoryName;
        this.price = price;
        this.shopId = shopId;
    }

    public String getCategoryName() { return categoryName; }
    public void setCategoryName(String categoryName) { this.categoryName = categoryName; }

    public String getOrderDate() { return orderDate; }
    public void setOrderDate(String orderDate) { this.orderDate = orderDate; }

    public String getOrderStatus() { return orderStatus; }
    public void setOrderStatus(String orderStatus) { this.orderStatus = orderStatus; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public Long getPrice() { return price; }
    public void setPrice(Long price) { this.price = price; }

    public String getShopId() { return shopId; }
    public void setShopId(String shopId) { this.shopId = shopId; }
}
