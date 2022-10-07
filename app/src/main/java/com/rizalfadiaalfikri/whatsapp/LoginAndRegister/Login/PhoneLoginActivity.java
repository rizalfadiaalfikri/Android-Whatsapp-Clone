package com.rizalfadiaalfikri.whatsapp.LoginAndRegister.Login;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.blogspot.atifsoftwares.animatoolib.Animatoo;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.rizalfadiaalfikri.whatsapp.MainActivity;
import com.rizalfadiaalfikri.whatsapp.R;

import java.util.concurrent.TimeUnit;

public class PhoneLoginActivity extends AppCompatActivity {

    private Button btn_send_verification, btn_verify;
    private EditText ed_phone_number, ed_phone_number_verification;

    private PhoneAuthProvider.OnVerificationStateChangedCallbacks callbacks;
    private PhoneAuthProvider.ForceResendingToken mResendingToken;
    private FirebaseAuth mAuth;

    private String mVerificationId;

    ProgressDialog loadingBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_login);

        loadingBar = new ProgressDialog(this);

        mAuth = FirebaseAuth.getInstance();

        init();
    }

    private void init() {
        btn_send_verification = findViewById(R.id.btn_send_verification);
        btn_verify = findViewById(R.id.btn_verify);
        ed_phone_number = findViewById(R.id.ed_phone_number);
        ed_phone_number_verification = findViewById(R.id.ed_phone_number_verification);


        callbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                signInWithPhoneAuthCredential(phoneAuthCredential);
            }

            @Override
            public void onVerificationFailed(@NonNull FirebaseException e) {
                loadingBar.dismiss();
                btn_send_verification.setVisibility(View.VISIBLE);
                ed_phone_number.setVisibility(View.VISIBLE);

                btn_verify.setVisibility(View.INVISIBLE);
                ed_phone_number_verification.setVisibility(View.INVISIBLE);

                Toast.makeText(PhoneLoginActivity.this, "Invalid Phone Number, Please enter correct number with your country code...", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                mVerificationId = s;
                mResendingToken = forceResendingToken;

                loadingBar.dismiss();
                Toast.makeText(PhoneLoginActivity.this, "Code has been sent, please check and verification", Toast.LENGTH_SHORT).show();

                btn_send_verification.setVisibility(View.INVISIBLE);
                ed_phone_number.setVisibility(View.INVISIBLE);

                btn_verify.setVisibility(View.VISIBLE);
                ed_phone_number_verification.setVisibility(View.VISIBLE);

            }
        };

        btn_send_verification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String phoneNumber = ed_phone_number.getText().toString().trim();

                if (TextUtils.isEmpty(phoneNumber)) {
                    Toast.makeText(PhoneLoginActivity.this, "Phone number is required", Toast.LENGTH_SHORT).show();
                } else {
                    loadingBar.setTitle("Phone Verification");
                    loadingBar.setMessage("Please wait....");
                    loadingBar.setCanceledOnTouchOutside(false);
                    loadingBar.show();

                    PhoneAuthProvider.getInstance().verifyPhoneNumber(
                            "+62" + phoneNumber,
                            60,
                            TimeUnit.SECONDS,
                            PhoneLoginActivity.this,
                            callbacks
                    );
                }
            }
        });

        btn_verify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btn_send_verification.setVisibility(View.INVISIBLE);
                ed_phone_number.setVisibility(View.INVISIBLE);

                String phoneNumber = ed_phone_number.getText().toString();

                if (TextUtils.isEmpty(phoneNumber)) {
                    Toast.makeText(PhoneLoginActivity.this, "Please enter your 6 digit otp", Toast.LENGTH_SHORT).show();
                } else {
                    loadingBar.setTitle("Verification Code");
                    loadingBar.setMessage("Please wait....");
                    loadingBar.setCanceledOnTouchOutside(false);
                    loadingBar.show();

                    PhoneAuthCredential phoneAuthCredential = PhoneAuthProvider.getCredential(mVerificationId, phoneNumber);
                    signInWithPhoneAuthCredential(phoneAuthCredential);
                }
            }
        });
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential phoneAuthCredential) {
        mAuth.signInWithCredential(phoneAuthCredential)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            loadingBar.dismiss();
                            Toast.makeText(PhoneLoginActivity.this, "Your Logged", Toast.LENGTH_SHORT).show();

                            Intent intent = new Intent(PhoneLoginActivity.this, MainActivity.class);
                            Animatoo.animateSlideUp(PhoneLoginActivity.this);
                            startActivity(intent);
                            finish();

                        } else {
                            Toast.makeText(PhoneLoginActivity.this, "Something went wrong, Please try again", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}