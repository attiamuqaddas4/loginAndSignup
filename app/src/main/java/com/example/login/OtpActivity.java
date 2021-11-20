package com.example.login;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.hbb20.CountryCodePicker;

import java.util.concurrent.TimeUnit;

public class OtpActivity extends AppCompatActivity {
    EditText edtNumber;
    Button btnLogin;
    String phoneNumber;
    CountryCodePicker codePicker;
    private static final String TAG = "EmailPassword";
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otp);

        edtNumber=findViewById(R.id.edtNumber);
        btnLogin=findViewById(R.id.btnLogin);
        codePicker=findViewById(R.id.codePicker);

        ProgressBar pbSendingOtp=findViewById(R.id.pbSendingOtp);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                phoneNumber=edtNumber.getText().toString().trim();

                if(!phoneNumber.isEmpty()){
                    if((phoneNumber).length()==10){

                        PhoneAuthOptions options =
                                PhoneAuthOptions.newBuilder(mAuth)
                                        .setPhoneNumber("+" + codePicker.getSelectedCountryCode() + phoneNumber)       // Phone number to verify
                                        .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
                                        .setActivity(OtpActivity.this)                 // Activity (for callback binding)
                                        .setCallbacks(new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

                                            @Override
                                            public void onVerificationCompleted(PhoneAuthCredential credential) {
                                                // This callback will be invoked in two situations:
                                                // 1 - Instant verification. In some cases the phone number can be instantly
                                                //     verified without needing to send or enter a verification code.
                                                // 2 - Auto-retrieval. On some devices Google Play services can automatically
                                                //     detect the incoming verification SMS and perform verification without
                                                //     user action.
                                                Log.d(TAG, "onVerificationCompleted:" + credential);
                                                pbSendingOtp.setVisibility(View.VISIBLE);
                                                edtNumber.setVisibility(View.INVISIBLE );
                                                signInWithPhoneAuthCredential(credential);
                                            }

                                            @Override
                                            public void onVerificationFailed(FirebaseException e) {
                                                // This callback is invoked in an invalid request for verification is made,
                                                // for instance if the the phone number format is not valid.
                                                Log.w(TAG, "onVerificationFailed", e);

                                                if (e instanceof FirebaseAuthInvalidCredentialsException) {
                                                    // Invalid request
                                                } else if (e instanceof FirebaseTooManyRequestsException) {
                                                    // The SMS quota for the project has been exceeded
                                                }

                                                // Show a message and update the UI
                                                Toast.makeText(OtpActivity.this,e.getMessage(),Toast.LENGTH_SHORT).show();
                                                pbSendingOtp.setVisibility(View.VISIBLE);
                                                edtNumber.setVisibility(View.INVISIBLE );
                                            }

                                            @Override
                                            public void onCodeSent(@NonNull String verificationId,
                                                                   @NonNull PhoneAuthProvider.ForceResendingToken token) {

                                                pbSendingOtp.setVisibility(View.VISIBLE);
                                                edtNumber.setVisibility(View.INVISIBLE );

                                                // Save verification ID and token so we can use them later
                                                Intent intent=new Intent(getApplicationContext(),OtpCodeActivity.class);
                                                intent.putExtra("mobile", "+" + codePicker.getSelectedCountryCode() + edtNumber.getText().toString());
                                                intent.putExtra("backendotp",token);
                                                startActivity(intent);
                                                // The SMS verification code has been sent to the provided phone number, we
                                                // now need to ask the user to enter the code and then construct a credential
                                                // by combining the code with a verification ID.
                                                Log.d(TAG, "onCodeSent:" + verificationId);
                                            }
                                        })          // OnVerificationStateChangedCallbacks
                                        .build();
                        PhoneAuthProvider.verifyPhoneNumber(options);

                    }
                    else {
                        Toast.makeText(OtpActivity.this,"Please enter correct phone number",Toast.LENGTH_SHORT).show();

                    }
                }
                else{
                    Toast.makeText(OtpActivity.this,"Enter mobile number",Toast.LENGTH_SHORT).show();
                }
            }

        });
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null){
            reload();
        }
    }


    private void reload() { }

    private void updateUI(FirebaseUser user) {

    }
    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");

                            FirebaseUser user = task.getResult().getUser();
                            // Update UI
                        } else {
                            // Sign in failed, display a message and update the UI
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                // The verification code entered was invalid
                            }
                        }
                    }
                });
    }
}