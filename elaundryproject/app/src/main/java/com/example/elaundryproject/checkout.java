package com.example.elaundryproject;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class checkout extends AppCompatActivity {

    private EditText etName, etNumber, etAddress;
    private RadioGroup radioGroup, radioGroupPayment;
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
        radioGroup = findViewById(R.id.radio_group);
        radioGroupPayment = findViewById(R.id.radio_group_payment);
        btnConfirm = findViewById(R.id.btn_confirm);

        // Initialize Firebase database reference
        orderDetailsRef = FirebaseDatabase.getInstance().getReference("orderdetails");

        // Get Intent data
        String ordermasterid = getIntent().getStringExtra("ordermasterid");
        price = getIntent().getLongExtra("price", 0); // Receive the price from the Intent

        // Log the received price
        Log.d("Checkout", "Received price: " + price);
        Toast.makeText(this, "Price: " + price, Toast.LENGTH_SHORT).show();

        // Log the ordermasterid to ensure it is valid
        Log.d("Checkout", "Order Master ID: " + ordermasterid);

        // Set up the confirm order button click listener
        btnConfirm.setOnClickListener(v -> confirmOrder(ordermasterid));
    }

    private void confirmOrder(String ordermasterid) {
        String name = etName.getText().toString().trim();
        String number = etNumber.getText().toString().trim();
        String address = etAddress.getText().toString().trim();

        // Get selected delivery method
        String deliveryMethod;
        int selectedDeliveryId = radioGroup.getCheckedRadioButtonId();

        if (selectedDeliveryId == R.id.rb_deliver) {
            deliveryMethod = "Deliver laundry";
        } else if (selectedDeliveryId == R.id.rb_pickup) {
            deliveryMethod = "Pick up laundry";
        } else {
            Toast.makeText(this, "Please select a delivery method", Toast.LENGTH_SHORT).show();
            return; // Stop the method if no delivery method is selected
        }

        // Get selected payment method
        String paymentMethod;
        int selectedPaymentId = radioGroupPayment.getCheckedRadioButtonId();

        if (selectedPaymentId == R.id.rb_qris) {
            paymentMethod = "QRIS";
        } else if (selectedPaymentId == R.id.rb_cod) {
            paymentMethod = "Cash on delivery";
        } else {
            Toast.makeText(this, "Please select a payment method", Toast.LENGTH_SHORT).show();
            return; // Stop the method if no payment method is selected
        }

        if (name.isEmpty() || number.isEmpty() || address.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        // Create the orderDetails object
        String orderDetailsId = orderDetailsRef.push().getKey(); // Generate a unique ID for the order details
        OrderDetails orderDetails = new OrderDetails(name, number, address, deliveryMethod, paymentMethod, price);

        // Insert into the orderdetails table under the corresponding ordermasterid
        if (orderDetailsId != null) {
            orderDetailsRef.child(ordermasterid).child(orderDetailsId).setValue(orderDetails)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(checkout.this, "Order confirmed", Toast.LENGTH_SHORT).show();

                            // Navigate to QRCode activity with price and ordermasterid
                            Intent intent = new Intent(checkout.this, qrcode.class);
                            intent.putExtra("ordermasterid", ordermasterid);
                            intent.putExtra("price", price);
                            startActivity(intent);
                            finish(); // Close the current activity
                        } else {
                            Log.e("Checkout", "Failed to confirm order", task.getException());
                            Toast.makeText(checkout.this, "Failed to confirm order: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        } else {
            Toast.makeText(this, "Error generating order details ID", Toast.LENGTH_SHORT).show();
        }
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
    }
}
