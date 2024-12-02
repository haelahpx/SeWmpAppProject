package com.example.elaundryproject;

import android.os.Bundle;
import android.view.View;
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
    private RadioGroup radioGroup;
    private RadioButton rbDeliver, rbPickup, rbQris, rbCod;
    private Button btnConfirm;

    private DatabaseReference orderDetailsRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout);

        // Initialize UI components
        etName = findViewById(R.id.et_name);
        etNumber = findViewById(R.id.et_number);
        etAddress = findViewById(R.id.et_address);
        radioGroup = findViewById(R.id.radio_group);
        rbDeliver = findViewById(R.id.rb_deliver);
        rbPickup = findViewById(R.id.rb_pickup);
        rbQris = findViewById(R.id.rb_qris);
        rbCod = findViewById(R.id.rb_cod);
        btnConfirm = findViewById(R.id.btn_confirm);

        // Initialize Firebase database reference
        orderDetailsRef = FirebaseDatabase.getInstance().getReference("orderdetails");

        // Get Intent data
        String ordermasterid = getIntent().getStringExtra("ordermasterid");

        // Set up the confirm order button click listener
        btnConfirm.setOnClickListener(v -> confirmOrder(ordermasterid));
    }

    private void confirmOrder(String ordermasterid) {
        String name = etName.getText().toString();
        String number = etNumber.getText().toString();
        String address = etAddress.getText().toString();
        String deliveryMethod = rbDeliver.isChecked() ? "Deliver laundry" : "Pick up laundry";
        String paymentMethod = rbQris.isChecked() ? "QRIS" : "Cash on delivery";

        if (name.isEmpty() || number.isEmpty() || address.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        // Create the orderdetails object
        String orderDetailsId = orderDetailsRef.push().getKey();  // Generate a unique ID for the order details
        OrderDetails orderDetails = new OrderDetails(name, number, address, deliveryMethod, paymentMethod);

        // Insert into the orderdetails table under the corresponding ordermasterid
        if (orderDetailsId != null) {
            orderDetailsRef.child(ordermasterid).child(orderDetailsId).setValue(orderDetails)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(checkout.this, "Order confirmed", Toast.LENGTH_SHORT).show();
                            finish(); // Finish the activity and go back
                        } else {
                            Toast.makeText(checkout.this, "Failed to confirm order", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    // OrderDetails model class
    public static class OrderDetails {
        public String name;
        public String number;
        public String address;
        public String deliveryMethod;
        public String paymentMethod;

        public OrderDetails(String name, String number, String address, String deliveryMethod, String paymentMethod) {
            this.name = name;
            this.number = number;
            this.address = address;
            this.deliveryMethod = deliveryMethod;
            this.paymentMethod = paymentMethod;
        }
    }
}
