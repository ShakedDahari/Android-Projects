package com.example.projectsemb;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {

    TextInputEditText emailInput, passwordInput;
    TextInputLayout emailLayout, passwordLayout;
    MaterialButton buttonLogin, buttonToSignUp;
    private FirebaseAuth mAuth;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        initViews();

        buttonLogin.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    String emailInputText = emailInput.getText().toString();
                    String passwordInputText = passwordInput.getText().toString();

                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("email", emailInputText);
                    editor.putString("password",passwordInputText);
                    editor.commit();

                    if (!emailInputText.isEmpty() && !passwordInputText.isEmpty()) {
                        mAuth.signInWithEmailAndPassword(emailInputText, passwordInputText)
                                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {
                                        if (task.isSuccessful()) {
                                            Toast.makeText(MainActivity.this, "Login successfully", Toast.LENGTH_SHORT).show();
                                            startActivity(new Intent(MainActivity.this, HomeActivity.class));
                                        } else {
                                            Toast.makeText(MainActivity.this, task.getException().toString(), Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                    } else {
                        //Toast.makeText(MainActivity.this, "Incorrect input", Toast.LENGTH_SHORT).show();
                        if (emailInputText.isEmpty())
                            emailLayout.setError("Invalid input");
                        if (passwordInputText.isEmpty())
                            passwordLayout.setError("Invalid input");
                    }
                }
        });

            buttonToSignUp.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent i = new Intent(MainActivity.this, SignUpActivity.class);
                    startActivity(i);
                }
            });
        }


    private void initViews() {
        mAuth = FirebaseAuth.getInstance();
        emailLayout = findViewById(R.id.emailLayout);
        passwordLayout = findViewById(R.id.passwordLayout);
        emailInput = findViewById(R.id.emailInput);
        passwordInput = findViewById(R.id.passwordInput);
        buttonLogin = findViewById(R.id.buttonLogin);
        buttonToSignUp = findViewById(R.id.buttonToSignUp);
    }
}