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

        priceTextView = findViewById(R.id.priceTextView);
        qrcodeImageView = findViewById(R.id.qrcodeImageView);


        String ordermasterid = getIntent().getStringExtra("ordermasterid");
        long price = getIntent().getLongExtra("price", 0);
        String paymentMethod = getIntent().getStringExtra("paymentMethod");
        String paymentStatus = getIntent().getStringExtra("paymentStatus");

        if (price != 0) {
            priceTextView.setText("Price: Rp" + price);

            String qrCodeUrl = "https://api.qrserver.com/v1/create-qr-code/?size=150x150&data=" + price;

            Glide.with(this)
                    .load(qrCodeUrl)
                    .into(qrcodeImageView);

            createPaymentRecord(ordermasterid, paymentMethod, paymentStatus);
        } else {
            Toast.makeText(this, "Failed to get price", Toast.LENGTH_SHORT).show();
        }
    }

    private void createPaymentRecord(String ordermasterid, String paymentMethod, String paymentStatus) {
        String paymentId = UUID.randomUUID().toString();

        String paymentDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date());

        Payment payment = new Payment(paymentId, ordermasterid, paymentMethod, paymentStatus, paymentDate);

        DatabaseReference paymentRef = FirebaseDatabase.getInstance().getReference("payment");
        paymentRef.child(paymentId).setValue(payment)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(qrcode.this, "Payment record created!", Toast.LENGTH_SHORT).show();

                    updatePaymentStatusToDone(paymentId);
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(qrcode.this, "Failed to create payment record: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void updatePaymentStatusToDone(String paymentId) {
        DatabaseReference paymentRef = FirebaseDatabase.getInstance().getReference("payment").child(paymentId);
        paymentRef.child("paymentStatus").setValue("Done")
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(qrcode.this, "Payment status updated to 'Done'", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(qrcode.this, "Failed to update payment status: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

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

        public Payment() {
        }
    }
}
