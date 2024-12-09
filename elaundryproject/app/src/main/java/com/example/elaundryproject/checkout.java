package com.example.elaundryproject;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.UUID;

public class checkout extends AppCompatActivity {

    private EditText etName, etNumber, etAddress;
    private RadioButton rbDeliver, rbPickup, rbQris, rbCod;
    private Button btnConfirm;

    private DatabaseReference orderDetailsRef;
    private long price; // Declare price here

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout);

        // Initialize UI components
        etName = findViewById(R.id.et_name);
        etNumber = findViewById(R.id.et_number);
        etAddress = findViewById(R.id.et_address);
        rbDeliver = findViewById(R.id.rb_deliver);
        rbPickup = findViewById(R.id.rb_pickup);
        rbQris = findViewById(R.id.rb_qris);
        rbCod = findViewById(R.id.rb_cod);
        btnConfirm = findViewById(R.id.btn_confirm);

        // Initialize Firebase database reference
        orderDetailsRef = FirebaseDatabase.getInstance().getReference("orderdetails");

        // Get Intent data
        String ordermasterid = getIntent().getStringExtra("ordermasterid");
        price = getIntent().getLongExtra("price", 0); // Receive the price from the Intent

        // Log the received price
        Log.d("Checkout", "Received price: " + price);
        Toast.makeText(this, "Price: " + price, Toast.LENGTH_SHORT).show();

        // Set up the confirm order button click listener
        btnConfirm.setOnClickListener(v -> {
            String name = etName.getText().toString().trim();
            String number = etNumber.getText().toString().trim();
            String address = etAddress.getText().toString().trim();

            // Determine delivery method
            String deliveryMethod = rbDeliver.isChecked() ? "Deliver laundry" : rbPickup.isChecked() ? "Pick up laundry" : "Not specified";

            // Determine payment method and status
            String paymentMethod = rbQris.isChecked() ? "QRIS" : rbCod.isChecked() ? "Cash on delivery" : "Not specified";
            String paymentStatus = paymentMethod.equals("QRIS") ? "Pending" : "COD - Pending";

            if (name.isEmpty() || number.isEmpty() || address.isEmpty() || paymentMethod.equals("Not specified")) {
                Toast.makeText(checkout.this, "Please fill in all fields and select payment method", Toast.LENGTH_SHORT).show();
                return;
            }

            // Create and save order details
            String orderDetailsId = UUID.randomUUID().toString();  // Generate unique ID for order details
            OrderDetails orderDetails = new OrderDetails(name, number, address, deliveryMethod, paymentMethod, price);

            // Save order details to Firebase
            orderDetailsRef.child(ordermasterid).child(orderDetailsId).setValue(orderDetails)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            // Save payment data if "Cash on Delivery" is selected
                            if (paymentMethod.equals("Cash on delivery")) {
                                String paymentId = UUID.randomUUID().toString();  // Generate payment ID
                                savePaymentData(ordermasterid, paymentMethod, paymentStatus, paymentId);  // Pass paymentId
                                Toast.makeText(checkout.this, "Order confirmed! Payment saved successfully.", Toast.LENGTH_SHORT).show();
                            } else {
                                // If QRIS is selected, navigate to QR Code activity
                                String paymentId = UUID.randomUUID().toString();  // Generate payment ID for QRIS
                                Intent intent = new Intent(checkout.this, qrcode.class);
                                intent.putExtra("ordermasterid", ordermasterid);
                                intent.putExtra("price", price);
                                intent.putExtra("paymentMethod", paymentMethod);
                                intent.putExtra("paymentStatus", paymentStatus);
                                intent.putExtra("paymentId", paymentId);  // Add paymentId to the intent
                                startActivity(intent);
                            }
                            finish(); // Close the activity after saving
                        } else {
                            Log.e("Checkout", "Failed to save order details", task.getException());
                            Toast.makeText(checkout.this, "Failed to save order details: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        });
    }

    private void savePaymentData(String ordermasterid, String paymentMethod, String paymentStatus, String paymentId) {
        // Get current timestamp
        String paymentDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date());

        // Create Payment object
        Payment payment = new Payment(paymentId, ordermasterid, paymentMethod, paymentStatus, paymentDate);

        // Save to Firebase
        DatabaseReference paymentRef = FirebaseDatabase.getInstance().getReference("payment");
        paymentRef.child(paymentId).setValue(payment)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(checkout.this, "Payment saved successfully.", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(checkout.this, "Failed to save payment: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    // OrderDetails model class
    public static class OrderDetails {
        public String name;
        public String number;
        public String address;
        public String deliveryMethod;
        public String paymentMethod;
        public long price;

        public OrderDetails(String name, String number, String address, String deliveryMethod, String paymentMethod, long price) {
            this.name = name;
            this.number = number;
            this.address = address;
            this.deliveryMethod = deliveryMethod;
            this.paymentMethod = paymentMethod;
            this.price = price;
        }

        // Empty constructor required for Firebase
        public OrderDetails() {
        }
    }

    // Payment model class
    public static class Payment {
        public String paymentId;
        public String orderMasterId;
        public String paymentMethod;
        public String paymentStatus;
        public String paymentDate;

        public Payment(String paymentId, String orderMasterId, String paymentMethod, String paymentStatus, String paymentDate) {
            this.paymentId = paymentId;
            this.orderMasterId = orderMasterId;
            this.paymentMethod = paymentMethod;
            this.paymentStatus = paymentStatus;
            this.paymentDate = paymentDate;
        }

        // Empty constructor required for Firebase
        public Payment() {
        }
    }
}
