package com.example.individualproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;


import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

public class Register extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private EditText FullName, mEmail, mPassword, PhoneNum;
    private Spinner Type;
    private Button Register;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        FullName = findViewById(R.id.Name);
        mEmail = findViewById(R.id.mailReg);
        mPassword = findViewById(R.id.passReg);
        PhoneNum = findViewById(R.id.editTextPhone);
        Type = findViewById(R.id.spinner);

        mAuth = FirebaseAuth.getInstance();
        Register = findViewById(R.id.Registracija);
    //    Toolbar toolbar = findViewById(R.id.regToolbar);
      //  setSupportActionBar(toolbar);
    }

    public void Registered (View view) {
        String mail = mEmail.getText().toString().trim();
        String password = mPassword.getText().toString().trim();
        String Fname = FullName.getText().toString().trim();
        String phone = PhoneNum.getText().toString().trim();
        String type = (String) Type.getSelectedItem();

        if (TextUtils.isEmpty(Fname)) {
            FullName.setError("Enter First Name and Last Name");
            return;
        }

        if (TextUtils.isEmpty(mail)) {
            mEmail.setError("Enter E-mail address");
            return;
        }

        if (TextUtils.isEmpty(password)) {
            mPassword.setError("Enter password");
            return;
        }

        if (password.length() < 6) {
            mPassword.setError("The password must include more than 6 characters");
            return;
        }

        if(TextUtils.isEmpty(phone)){
            PhoneNum.setError("Enter phone number");
            return;
        }

        mAuth.createUserWithEmailAndPassword(mail, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    User user = new User(Fname, mail, password, phone, type, 0,0);
                    FirebaseDatabase.getInstance().getReference("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                            .setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                Toast.makeText(Register.this, "Registration successful!", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                            }
                            else{
                                Toast.makeText(Register.this, "Unsuccessful registration! Try again", Toast.LENGTH_SHORT).show();
                            }

                        }
                    });
                } else {
                    Toast.makeText(Register.this, "Unsuccessful registration! Try again.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    }