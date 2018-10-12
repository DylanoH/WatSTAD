package com.example.watstad.watstad;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    FirebaseAuth mAuth;

    EditText editTextEmailLogin;
    EditText editTextPasswordLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();

        editTextEmailLogin = findViewById(R.id.editTextEmailSignUp);
        editTextPasswordLogin = findViewById(R.id.editTextPasswordSignUp);

        findViewById(R.id.textViewSignUpHere).setOnClickListener(this);
        findViewById(R.id.buttonLogin).setOnClickListener(this);
    }

    private void userLogin() {
        String email = editTextEmailLogin.getText().toString().trim();
        String password = editTextPasswordLogin.getText().toString().trim();

        if (email.isEmpty()) {
            editTextEmailLogin.setError("Email is required");
            editTextEmailLogin.requestFocus();
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            editTextEmailLogin.setError("Please enter a valid email");
            editTextEmailLogin.requestFocus();
            return;
        }

        if (password.isEmpty()) {
            editTextPasswordLogin.setError("Password is required");
            editTextPasswordLogin.requestFocus();
            return;
        }

        if (password.length()<6) {
            editTextPasswordLogin.setError("Minimum length of password should be 6");
            editTextPasswordLogin.requestFocus();
            return;
        }

        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()) {
                    finish();
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                }else {
                    Toast.makeText(getApplicationContext(), task.getException().getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        if (mAuth.getCurrentUser() != null) {
            finish();
            startActivity(new Intent(this, MainActivity.class));
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.textViewSignUpHere:
                finish();
                startActivity(new Intent(this, SignUpActivity.class));
                break;
            case R.id.buttonLogin:
                userLogin();
        }
    }
}
