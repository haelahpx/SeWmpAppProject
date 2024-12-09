package com.example.elaundryproject;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.UUID;

public class qrcode extends AppCompatActivity {

    private TextView priceTextView;
    private ImageView qrcodeImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qrcode);

        // Initialize UI components
        priceTextView = findViewById(R.id.priceTextView);
        qrcodeImageView = findViewById(R.id.qrcodeImageView);

        // Get Intent data
        String ordermasterid = getIntent().getStringExtra("ordermasterid");
        long price = getIntent().getLongExtra("price", 0);
        String paymentMethod = getIntent().getStringExtra("paymentMethod");
        String paymentStatus = getIntent().getStringExtra("paymentStatus");

        // Show price
        if (price != 0) {
            priceTextView.setText("Price: Rp" + price);

            // Generate QR code URL
            String qrCodeUrl = "https://api.qrserver.com/v1/create-qr-code/?size=150x150&data=" + price;

            // Load QR code into ImageView using Glide
            Glide.with(this)
                    .load(qrCodeUrl)
                    .into(qrcodeImageView);

            // Create payment record in Firebase
            createPaymentRecord(ordermasterid, paymentMethod, paymentStatus);
        } else {
            Toast.makeText(this, "Failed to get price", Toast.LENGTH_SHORT).show();
        }
    }

    private void createPaymentRecord(String ordermasterid, String paymentMethod, String paymentStatus) {
        // Generate unique payment ID
        String paymentId = UUID.randomUUID().toString();

        // Get the current timestamp
        String paymentDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date());

        // Create a Payment object
        Payment payment = new Payment(paymentId, ordermasterid, paymentMethod, paymentStatus, paymentDate);

        // Save the payment record to Firebase Realtime Database
        DatabaseReference paymentRef = FirebaseDatabase.getInstance().getReference("payment");
        paymentRef.child(paymentId).setValue(payment)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(qrcode.this, "Payment record created!", Toast.LENGTH_SHORT).show();

                    // After creating the payment record, update the payment status to "Done"
                    updatePaymentStatusToDone(paymentId);
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(qrcode.this, "Failed to create payment record: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void updatePaymentStatusToDone(String paymentId) {
        // Update the payment status to "Done" in Firebase
        DatabaseReference paymentRef = FirebaseDatabase.getInstance().getReference("payment").child(paymentId);
        paymentRef.child("paymentStatus").setValue("Done")
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(qrcode.this, "Payment status updated to 'Done'", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(qrcode.this, "Failed to update payment status: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    // Payment model class (remains unchanged)
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
