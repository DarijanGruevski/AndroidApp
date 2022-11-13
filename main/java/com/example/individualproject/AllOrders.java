package com.example.individualproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Spinner;
import android.widget.Toast;


import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class AllOrders extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private myAdapterAllOrders mAdapter;
    FusedLocationProviderClient fusedLocationProviderClient;
    private static final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COURSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1234;

    private Boolean mLocationPermissionsGranted = false;

    private Location myLocation;

    private List<Order> list = new ArrayList<>();

    private Spinner spinner;

    private List<String> listspinner = Arrays.asList("Placed", "Out for Delivery", "Delivered");

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_orders);

        getLocationPermission();
        getDeviceLocation();

        mRecyclerView = (RecyclerView) findViewById(R.id.list2);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mAdapter = new myAdapterAllOrders(list, R.layout.adapter_allorders, this);
        mRecyclerView.setAdapter(mAdapter);

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        spinner = findViewById(R.id.spinnerStatus);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                CreateList();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                Toast.makeText(AllOrders.this, "Настана грешка.Обидете се повторно!", Toast.LENGTH_SHORT).show();
            }
        });

    }
    private void CreateList() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Orders");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                list.clear();
                double rastojanie = 0;
                double rastojanieKm = 0;
                String status = spinner.getSelectedItem().toString();

                for(DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Order order = dataSnapshot.getValue(Order.class);
                    if(FirebaseAuth.getInstance().getCurrentUser().getUid().equals(order.getUserId())) {
                        if (myLocation != null) {
                            LatLng Start = new LatLng(myLocation.getLatitude(), myLocation.getLongitude());
                            LatLng End = new LatLng(order.getLatitude(), order.getLongitude());
                            rastojanie = PresmetajRastojanie(Start, End);
                        }
                        rastojanieKm = rastojanie / 1000;
                        order.setDistance((double) Math.round(rastojanieKm * 100) / 100);
                        order.setDescriptionId(dataSnapshot.getKey());
                        if (order.getStatus().equals(status)) {
                            list.add(order);
                        }
                    }
                }

                Collections.sort(list, new Comparator<Order>() {
                    @Override
                    public int compare(Order o1, Order o2) {
                        return Double.valueOf(o1.getDistance()).compareTo(o2.getDistance());
                    }
                });

                mAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(AllOrders.this, "Настана грешка.Обидете се повторно!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private double PresmetajRastojanie(LatLng start, LatLng end) {
        float[] results = new float[1];
        Location.distanceBetween(start.latitude, start.longitude,
                end.latitude, end.longitude,
                results);
        return results[0];
    }

    private void getLocationPermission(){
        String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION};

        if(ContextCompat.checkSelfPermission(this.getApplicationContext(),
                FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            if(ContextCompat.checkSelfPermission(this.getApplicationContext(),
                    COURSE_LOCATION) == PackageManager.PERMISSION_GRANTED){
                mLocationPermissionsGranted = true;
            }else{
                ActivityCompat.requestPermissions(this,
                        permissions,
                        LOCATION_PERMISSION_REQUEST_CODE);
            }
        }else{
            ActivityCompat.requestPermissions(this,
                    permissions,
                    LOCATION_PERMISSION_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        mLocationPermissionsGranted = false;

        switch (requestCode) {
            case LOCATION_PERMISSION_REQUEST_CODE: {
                if (grantResults.length > 0) {
                    for (int i = 0; i < grantResults.length; i++) {
                        if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                            mLocationPermissionsGranted = false;
                            return;
                        }
                    }
                    mLocationPermissionsGranted = true;
                }
            }
        }
    }

    private void getDeviceLocation(){
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        try{
            if(mLocationPermissionsGranted){

                final Task location = fusedLocationProviderClient.getLastLocation();
                location.addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        if(task.isSuccessful()){
                            myLocation = (Location) task.getResult();
                            CreateList();
                        }else{
                            Toast.makeText(AllOrders.this, "Unable to get current location", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        }catch (SecurityException e){
            e.printStackTrace();
        }
    }
    }
