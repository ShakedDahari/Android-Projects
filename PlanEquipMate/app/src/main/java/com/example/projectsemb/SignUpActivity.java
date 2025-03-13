package com.example.projectsemb;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SignUpActivity extends AppCompatActivity {

    TextInputEditText emailSignUp, phoneInput, nameInput, passwordSignUp, passwordConfirmSignUp;
    MaterialButton buttonSignUp, buttonToLogin;
    TextInputLayout signUpEmailLayout, signUpNameLayout, signUpPhoneLayout, signUpPasswordLayout, signUpConfirmLayout;
    private FirebaseAuth mAuth;
    FirebaseDatabase database;
    DatabaseReference myRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        initViews();
        initParameters();

        buttonSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String emailInputText = emailSignUp.getText().toString();
                String phoneInputText = phoneInput.getText().toString();
                String nameInputText = nameInput.getText().toString();
                String passwordInputText = passwordSignUp.getText().toString();
                String confirmPassInputText = passwordConfirmSignUp.getText().toString();

                if (!emailInputText.isEmpty() && !phoneInputText.isEmpty() &&
                        !nameInputText.isEmpty() && !passwordInputText.isEmpty() && !confirmPassInputText.isEmpty())  {
                    if (passwordInputText.equals(confirmPassInputText)) {
                        signUpAction(emailInputText, passwordInputText, nameInputText);
                    }
                } else {
                    //Toast.makeText(SignUpActivity.this, "Incorrect input", Toast.LENGTH_SHORT).show();
                    if (emailInputText.isEmpty())
                        signUpEmailLayout.setError("Empty input");
                    if (phoneInputText.isEmpty())
                        signUpPhoneLayout.setError("Empty input");
                    if (nameInputText.isEmpty())
                        signUpNameLayout.setError("Empty input");
                    if (passwordInputText.isEmpty())
                        signUpPasswordLayout.setError("Empty input");
                    if (confirmPassInputText.isEmpty())
                        signUpConfirmLayout.setError("Empty input");
                }
            }
        });

        buttonToLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(SignUpActivity.this, MainActivity.class);
                startActivity(i);
            }
        });
    }

    private void signUpAction(String emailInputText, String passwordInputText, String nameInputText) {

        mAuth.createUserWithEmailAndPassword(emailInputText, passwordInputText).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    User user = new User(nameInputText);
                    myRef.child(mAuth.getUid()).setValue(user);
                    startActivity(new Intent(SignUpActivity.this, MainActivity.class));
                } else {
                    Toast.makeText(SignUpActivity.this, task.getException().toString(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void initParameters() {
        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("Users");
    }

    private void initViews() {
        signUpEmailLayout = findViewById(R.id.signUpEmailLayout);
        signUpNameLayout = findViewById(R.id.signUpNameLayout);
        signUpPhoneLayout = findViewById(R.id.signUpPhoneLayout);
        signUpPasswordLayout = findViewById(R.id.signUpPasswordLayout);
        signUpConfirmLayout = findViewById(R.id.signUpConfirmLayout);
        emailSignUp = findViewById(R.id.emailSignUp);
        phoneInput = findViewById(R.id.phoneInput);
        nameInput = findViewById(R.id.nameInput);
        passwordSignUp = findViewById(R.id.passwordSignUp);
        passwordConfirmSignUp = findViewById(R.id.passwordConfirmSignUp);
        buttonSignUp = findViewById(R.id.buttonSignUp);
        buttonToLogin = findViewById(R.id.buttonToLogin);
    }


}