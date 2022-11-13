package com.example.individualproject;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import org.w3c.dom.Text;

import java.util.UUID;

public class Customer extends AppCompatActivity {

    private static final int REQ_CODE = 123;
    Spinner Food;
    EditText Description;
    RadioGroup radioGroup;
    TextView Adresa;
    double Lat, Log;
    private String Address = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.customer);
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        Description = findViewById(R.id.description);
        radioGroup = findViewById(R.id.Radiogroup);
        Adresa = findViewById(R.id.textLocation);
        Food = findViewById(R.id.Food);
        
    }
        @Override
      public boolean onCreateOptionsMenu (Menu menu){
            MenuInflater inflater = getMenuInflater();
            inflater.inflate(R.menu.main_menu, menu);
            return super.onCreateOptionsMenu(menu);
        }

        @Override
        public boolean onOptionsItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.nextPage:
                    Intent intent = new Intent(this, CustomerOrders.class);
                    startActivity(intent);
                    return true;
                case R.id.signOut:
                    AlertDialog.Builder dialog = new AlertDialog.Builder(this);
                    dialog.setTitle("Sign out");
                    dialog.setMessage("Do you want to sign out?");

                    dialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {

                        public void onClick(DialogInterface builder, int which) {
                            FirebaseAuth.getInstance().signOut();
                            startActivity(new Intent(Customer.this, MainActivity.class));
                            builder.dismiss();
                        }
                    });
                    dialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface builder, int which) {
                            builder.dismiss();
                        }
                    });
                    AlertDialog alert = dialog.create();
                    alert.show();
                default:
                    return super.onOptionsItemSelected(item);
            }
        }

    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (data != null) {
            if (requestCode == REQ_CODE) {
                Lat = data.getDoubleExtra("latitude", 0);
                Log = data.getDoubleExtra("longitude", 0);
                Address = data.getStringExtra("address");
                Adresa.setText(Address);
            }
        }
    }


    public void saveActivity(View view) {
        String description = Description.getText().toString().trim();
       String food = (String) Food.getSelectedItem();
        int Id = radioGroup.getCheckedRadioButtonId();
        String address = Address.toString().trim();

        if (TextUtils.isEmpty(description)) {
            Description.setError("Set your preferences");
            return;
        }

      /*  if (Address.equals("")) {
            Adresa.setError("Enter your address");
            return;
        }

       */
        String UserID = FirebaseAuth.getInstance().getCurrentUser().getUid();

        Order order = new Order(UserID,"",food,description,address,"Active",0,0,"","",0,0,"","");
        FirebaseDatabase.getInstance().getReference("Orders").child(UUID.randomUUID().toString())
                .setValue(order).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(Customer.this, "Your order has been placed.", Toast.LENGTH_SHORT).show();
                    removeActivity();
                } else {
                    Toast.makeText(Customer.this, "Something went wrong. Please try again", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void removeActivity() {
        Description.setText("");
        radioGroup.check(R.id.Delivertodoor);
    }

    public void selectLocation(View view) {
        Intent intent = new Intent(getApplicationContext(), LocationMap.class);
        startActivityForResult(intent, REQ_CODE);

    }
}