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

public class LoginActivity extends AppCompatActivity {

    EditText user_password, user_email;
    TextView btn_login_signup, typingTitle;
    Button btn_login;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();
        user_email = findViewById(R.id.user_email);
        user_password = findViewById(R.id.user_password);
        btn_login_signup = findViewById(R.id.btn_login_signup);
        btn_login = findViewById(R.id.btn_login);
        typingTitle = findViewById(R.id.typingTitle);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                typingTitle.setText("G");
            }
        }, 400);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                typingTitle.append("a");
            }
        }, 550);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                typingTitle.append("m");
            }
        }, 700);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                typingTitle.append("e");
            }
        }, 850);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                typingTitle.append(" ");
            }
        }, 1000);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                typingTitle.append("T");
            }
        }, 1150);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                typingTitle.append("r");
            }
        }, 1300);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                typingTitle.append("i");
            }
        }, 1450);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                typingTitle.append("o");
            }
        }, 1600);

        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!user_email.getText().toString().isEmpty() && !user_password.getText().toString().isEmpty()) {
                    mAuth.signInWithEmailAndPassword(user_email.getText().toString(), user_password.getText().toString())
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(LoginActivity.this, "Login successfully", Toast.LENGTH_SHORT).show();
                                        startActivity(new Intent(LoginActivity.this, HomeActivity.class));
                                    } else {
                                        Toast.makeText(LoginActivity.this, task.getException().toString(), Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                } else {
                    Toast.makeText(LoginActivity.this, "Incorrect input", Toast.LENGTH_SHORT).show();
                }
            }
        });

        btn_login_signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(LoginActivity.this, SignUpActivity.class);
                startActivity(i);
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
        FirebaseAuth.getInstance().signOut();
        finish();
    }
}

