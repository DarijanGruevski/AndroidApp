package com.example.individualproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    EditText Password, Email;
    Button Login,Register;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();
        Password = findViewById(R.id.password);
        Email = findViewById(R.id.Email);
        Login = findViewById(R.id.Najava);
        Register = findViewById(R.id.Regbutton);

        Register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(getApplicationContext(),Register.class);
                startActivity(intent);
            }
        });
        Login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String mail = Email.getText().toString().trim();
                String password = Password.getText().toString().trim();

                if (TextUtils.isEmpty(mail)) {
                    Email.setError("Enter your e-mail address");
                    return;
                }

                if (TextUtils.isEmpty(password)) {
                    Password.setError("Enter your password");
                    return;
                }
                mAuth.signInWithEmailAndPassword(mail, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()) {
                            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                            DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
                            String UserId = user.getUid();

                            reference.child(UserId).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    User korisnik = snapshot.getValue(User.class);
                                    if (korisnik != null) {
                                        String tip = korisnik.getType();
                                        if (tip.equals("Customer")) {
                                            Intent intent = new Intent(getApplicationContext(), Customer.class);
                                            startActivity(intent);
                                        } else {
                                            Intent intent = new Intent(getApplicationContext(), Orders.class);
                                            startActivity(intent);
                                        }
                                    }
                                }
                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {
                                    Toast.makeText(getApplicationContext(), "Error!", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }else {
                            Toast.makeText(MainActivity.this, "Unsuccessful login.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
    }
}
