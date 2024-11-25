package com.example.elaundryproject;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import java.util.HashMap;
import java.util.Map;

public class ordermaster {

    private DatabaseReference databaseReference;

    public ordermaster() {
        databaseReference = FirebaseDatabase.getInstance().getReference();
    }

    public void createOrder(String orderId, String customerId, String shopId,
                            String orderDate, int totalPrice, String status,
                            Map<String, Map<String, Object>> orderDetails) {

        Map<String, Object> orderMaster = new HashMap<>();
        orderMaster.put("order_id", orderId);
        orderMaster.put("customer_id", customerId);
        orderMaster.put("shop_id", shopId);
        orderMaster.put("order_date", orderDate);
        orderMaster.put("total_price", totalPrice);
        orderMaster.put("status", status);

        databaseReference.child("orders").child("order_master").child(orderId)
                .setValue(orderMaster)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        System.out.println("OrderMaster added successfully!");
                    } else {
                        System.out.println("Failed to add OrderMaster: " + task.getException());
                    }
                });

        // Add OrderDetails
        databaseReference.child("orders").child("order_details").child(orderId)
                .setValue(orderDetails)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        System.out.println("OrderDetails added successfully!");
                    } else {
                        System.out.println("Failed to add OrderDetails: " + task.getException());
                    }
                });

    }

    
    public void initializeDummyData() {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

        // Dummy data for order_master
        Map<String, Object> orderMasterData = new HashMap<>();
        orderMasterData.put("customer_id", "customer_id_123");
        orderMasterData.put("shop_id", "shop_id_456");
        orderMasterData.put("order_date", "2024-11-24");
        orderMasterData.put("total_price", 50000);
        orderMasterData.put("status", "Pending");


        databaseReference.child("orders").child("order_master").child("order_id_1").setValue(orderMasterData);


        Map<String, Object> orderDetailsData = new HashMap<>();
        orderDetailsData.put("category_id", "category_id_789");
        orderDetailsData.put("quantity", 2);
        orderDetailsData.put("price", 25000);

        databaseReference.child("orders").child("order_details").child("order_id_1").child("item_id_1").setValue(orderDetailsData);
    }
}

