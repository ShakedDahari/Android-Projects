package com.example.app_sem_a;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SignUpActivity extends AppCompatActivity {

    EditText email_signup, password_signup, confirm_signup, name_signup;
    TextView welcome;
    Button btn_signup;
    private FirebaseAuth mAuth;
    FirebaseDatabase database;
    DatabaseReference myRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        welcome = findViewById(R.id.welcome);
        email_signup = findViewById(R.id.email_signup);
        password_signup = findViewById(R.id.password_signup);
        name_signup = findViewById(R.id.name_signup);
        confirm_signup = findViewById(R.id.confirm_signup);
        btn_signup = findViewById(R.id.btn_signup);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                welcome.setText("W");
            }
        }, 400);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                welcome.append("e");
            }
        }, 550);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                welcome.append("l");
            }
        }, 700);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                welcome.append("c");
            }
        }, 850);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                welcome.append("o");
            }
        }, 1000);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                welcome.append("m");
            }
        }, 1150);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                welcome.append("e");
            }
        }, 1300);

        initParameters();

        btn_signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!email_signup.getText().toString().isEmpty() && !password_signup.getText().toString().isEmpty()) {
                    if (password_signup.getText().toString().equals(confirm_signup.getText().toString())) {
                        signUpAction();
                    }
                } else {
                    Toast.makeText(SignUpActivity.this, "Incorrect input", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void initParameters() {
        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("Users");
    }

    private void signUpAction() {
        mAuth.createUserWithEmailAndPassword(email_signup.getText().toString(),
                password_signup.getText().toString()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    User user = new User(name_signup.getText().toString());
                    myRef.child(mAuth.getUid()).setValue(user);
                    startActivity(new Intent(SignUpActivity.this, LoginActivity.class));
                } else {
                    Toast.makeText(SignUpActivity.this, task.getException().toString(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}