package com.rizalfadiaalfikri.whatsapp.LoginAndRegister.Register;

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
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.rizalfadiaalfikri.whatsapp.LoginAndRegister.Login.LoginActivity;
import com.rizalfadiaalfikri.whatsapp.MainActivity;
import com.rizalfadiaalfikri.whatsapp.R;

public class RegisterActivity extends AppCompatActivity {

    private Button btn_register;
    private EditText ed_email, ed_password, ed_fullname;

    FirebaseAuth mFirebaseAuth;
    private DatabaseReference rootRef;

    private ProgressDialog loadingBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mFirebaseAuth = FirebaseAuth.getInstance();
        rootRef = FirebaseDatabase.getInstance().getReference();

        initializeFields();

    }

    private void initializeFields() {
        btn_register = findViewById(R.id.btn_register);
        ed_email = findViewById(R.id.ed_register_email);
        ed_password = findViewById(R.id.ed_register_password);
        ed_fullname = findViewById(R.id.ed_register_fullname);

        loadingBar = new ProgressDialog(this);

        btn_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createNewAccount();
            }
        });

    }

    private void createNewAccount() {
        String email = ed_email.getText().toString();
        String password = ed_password.getText().toString();
        String fullname = ed_fullname.getText().toString();

        if (TextUtils.isEmpty(email)) {
            Toast.makeText(this, "Please Enter email", Toast.LENGTH_SHORT).show();
        }
        if (TextUtils.isEmpty(password)) {
            Toast.makeText(this, "Please Enter password", Toast.LENGTH_SHORT).show();
        }
        if (TextUtils.isEmpty(fullname)) {
            Toast.makeText(this, "Please Enter fullname", Toast.LENGTH_SHORT).show();
        } else {
            loadingBar.setTitle("Create New Account");
            loadingBar.setMessage("Please wait, while we are creating new account for you...");
            loadingBar.setCanceledOnTouchOutside(true);
            loadingBar.show();

            mFirebaseAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
//                                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
//                                Animatoo.animateSwipeRight(RegisterActivity.this);
//                                startActivity(intent);

                                String currentUserID = mFirebaseAuth.getCurrentUser().getUid();
                                rootRef.child("Users").child(currentUserID).setValue("");

                                sendUserToMainActivity();

                                Toast.makeText(RegisterActivity.this, "Account Created Successfully", Toast.LENGTH_SHORT).show();
                                loadingBar.dismiss();
                            } else {
                                String message = task.getException().toString();
                                Toast.makeText(RegisterActivity.this, message, Toast.LENGTH_SHORT).show();
                                loadingBar.dismiss();
                            }
                        }
                    });
        }

    }

    private void sendUserToMainActivity() {
        Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        Animatoo.animateSwipeRight(RegisterActivity.this);
        startActivity(intent);
        finish();
    }

    public void signin(View view) {
        Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
        Animatoo.animateSwipeRight(RegisterActivity.this);
        startActivity(intent);
    }
}