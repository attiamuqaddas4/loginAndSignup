package com.example.login;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.chaos.view.PinView;

public class OtpCodeActivity extends AppCompatActivity {
    Button btnConfirm;
    PinView pinView;

    String getOtp;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otp_code);
        btnConfirm=findViewById(R.id.btnConfirm);
        pinView=findViewById(R.id.pinView);

        getOtp=getIntent().getStringExtra("backendotp");

        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!pinView.getText().toString().trim().isEmpty()){
                    if(pinView.getText().toString().trim().equals(getOtp))
                        Toast.makeText(OtpCodeActivity.this, "Otp verified",Toast.LENGTH_SHORT).show();
                    else
                        Toast.makeText(OtpCodeActivity.this, "You entered invalid otp",Toast.LENGTH_SHORT).show();
                }
                else {
                    Toast.makeText(OtpCodeActivity.this,"Please enter OTP",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}