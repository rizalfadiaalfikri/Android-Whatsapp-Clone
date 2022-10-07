package com.rizalfadiaalfikri.whatsapp.LoginAndRegister.Login;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.blogspot.atifsoftwares.animatoolib.Animatoo;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.rizalfadiaalfikri.whatsapp.LoginAndRegister.Register.RegisterActivity;
import com.rizalfadiaalfikri.whatsapp.MainActivity;
import com.rizalfadiaalfikri.whatsapp.R;
import com.rizalfadiaalfikri.whatsapp.Session.SessionManager;

public class LoginActivity extends AppCompatActivity {

    private FirebaseUser curretUser;
    private FirebaseAuth mAuth;
    private ProgressDialog loadingBar;

    Button btn_login, btn_phone;
    EditText ed_email, ed_password;

    SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();

        initializeFields();
        
    }

    private void initializeFields() {
        btn_login = findViewById(R.id.btn_login);
        btn_phone = findViewById(R.id.btn_phone);
        ed_email = findViewById(R.id.ed_login_email);
        ed_password = findViewById(R.id.ed_login_password);

        loadingBar = new ProgressDialog(this);

        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                allowUserToLogin();
            }
        });

        btn_phone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, PhoneLoginActivity.class);
                Animatoo.animateSlideUp(LoginActivity.this);
                startActivity(intent);
                finish();
            }
        });

    }

    private void allowUserToLogin() {
        String email = ed_email.getText().toString();
        String password = ed_password.getText().toString();

        if (TextUtils.isEmpty(email)) {
            Toast.makeText(this, "Please Enter email", Toast.LENGTH_SHORT).show();
        }
        if (TextUtils.isEmpty(password)) {
            Toast.makeText(this, "Please Enter password", Toast.LENGTH_SHORT).show();
        }
        else {
            loadingBar.setTitle("Sign In");
            loadingBar.setMessage("Please wait ...");
            loadingBar.setCanceledOnTouchOutside(true);
            loadingBar.show();

            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {

                                sendUserToMainActivity();

                                Toast.makeText(LoginActivity.this, "Login successful.....", Toast.LENGTH_SHORT).show();
                                Log.d("LOGIN", mAuth.getUid().toString());
                                loadingBar.dismiss();
                            } else {
                                String message = task.getException().toString();
                                Toast.makeText(LoginActivity.this, message, Toast.LENGTH_SHORT).show();
                                loadingBar.dismiss();
                            }
                        }
                    });
        }
    }


    private void sendUserToMainActivity() {
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        Animatoo.animateSwipeRight(LoginActivity.this);
        startActivity(intent);
        finish();
    }

    public void signup(View view) {
        Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
        Animatoo.animateSwipeLeft(LoginActivity.this);
        startActivity(intent);
    }

    public void forgotPassword(View view) {

    }

}