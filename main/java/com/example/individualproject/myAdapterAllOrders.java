package com.example.individualproject;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class myAdapterAllOrders extends RecyclerView.Adapter<myAdapterAllOrders.ViewHolder> {

    private List<Order> myList;
    private int rowLayout;
    public Context mContext;

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView txtFood, txtDescription, txtAdresa, txtImePrezime,
                txtSend, txtDone, txtInfo;
        public ImageView Pic;

        public ViewHolder(View itemView) {
            super(itemView);
            txtFood = (TextView) itemView.findViewById(R.id.rw1TipAktivnost);
            txtDescription = (TextView) itemView.findViewById(R.id.rw1OpisAktivnost);
            txtAdresa = (TextView) itemView.findViewById(R.id.rw1Lokacija);
            txtImePrezime = (TextView) itemView.findViewById(R.id.rw1ImePrezime);
            txtSend = (TextView) itemView.findViewById(R.id.rw1Send);
            txtDone = (TextView) itemView.findViewById(R.id.rw1Done);

            txtInfo = (TextView) itemView.findViewById(R.id.rw1Info);

            txtDone.setVisibility(View.INVISIBLE);
            txtInfo.setVisibility(View.INVISIBLE);
            txtSend.setVisibility(View.INVISIBLE);
        }
    }

    public myAdapterAllOrders(List<Order> myList, int rowLayout, Context context) {
        this.myList = myList;
        this.rowLayout = rowLayout;
        this.mContext = context;
    }

    @NonNull
    @Override
    public myAdapterAllOrders.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(rowLayout, viewGroup, false);
        return new myAdapterAllOrders.ViewHolder(v);
    }


    @Override
    public void onBindViewHolder(@NonNull myAdapterAllOrders.ViewHolder viewHolder, int i) {
        Order order = myList.get(i);
        viewHolder.txtFood.setText(order.getDescriptionId());
        viewHolder.txtDescription.setText("Description: "+order.getDescription());
        viewHolder.txtAdresa.setText("Address: " + order.getAdresa() + "\nDistance: " + order.getDistance() + "km");
        viewHolder.txtAdresa.setPaintFlags(viewHolder.txtAdresa.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);

        if(order.getStatus().equals("Placed")) {
            viewHolder.txtDone.setVisibility(View.VISIBLE);
        } else {
            viewHolder.txtDone.setVisibility(View.INVISIBLE);
        }

        if(order.getStatus().equals("Delivered")) {
            viewHolder.txtInfo.setVisibility(View.VISIBLE);
        } else {
            viewHolder.txtInfo.setVisibility(View.INVISIBLE);
        }

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(order.getUserId());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                DecimalFormat decimalFormat = new DecimalFormat("##.0");
                if(user.VkupnoOceni != 0) {
                    double ocena = (double) user.getZbirOceni() / user.getVkupnoOceni();
                    viewHolder.txtImePrezime.setText(user.getName() + " Grade: " + decimalFormat.format(ocena));
                } else {
                    viewHolder.txtImePrezime.setText(user.getName() + "    Grade: /");
                }

            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(mContext, "There was an error. Please try again!", Toast.LENGTH_SHORT).show();
            }
        });

        viewHolder.txtAdresa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, ShowGoogleMap.class);
                intent.putExtra("lat", order.getLatitude());
                intent.putExtra("lon", order.getLongitude());
                v.getContext().startActivity(intent);
            }
        });

        viewHolder.txtDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alert = new AlertDialog.Builder(mContext);

                alert.setTitle("Delivered");

                LinearLayout layout = new LinearLayout(mContext);
                layout.setOrientation(LinearLayout.VERTICAL);

                final TextView textView = new TextView(mContext);
                textView.setText("Grade :");
                textView.setPadding(0,10,0,0);
                textView.setTextSize(16);

                final EditText izvestaj = new EditText(mContext);
                izvestaj.setHint("Submit report");
                layout.addView(izvestaj);
                layout.addView(textView);

                final RadioGroup radioGroup = new RadioGroup(mContext);
                RadioButton radioButton;
                for(int i = 1; i < 6; i++) {
                    radioButton = new RadioButton(mContext);
                    radioButton.setText(String.valueOf(i));
                    radioGroup.addView(radioButton);
                }

                RadioButton radioButton1 = (RadioButton) radioGroup.getChildAt(4);
                radioButton1.setChecked(true);

                radioGroup.setOrientation(LinearLayout.HORIZONTAL);

                layout.addView(radioGroup);

                alert.setView(layout);

                alert.setPositiveButton("Submit", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        RadioButton radioButton1 = (RadioButton) layout.findViewById(radioGroup.getCheckedRadioButtonId());
                        int Ocena = Integer.parseInt(radioButton1.getText().toString());

                        Map<String, Object> map = new HashMap();

                        DatabaseReference firebaseDatabase = FirebaseDatabase.getInstance().getReference("Orders");
                        map.put(order.getDescriptionId()+"/status", "Delivered");
                        map.put(order.getDescriptionId()+"/reportOrder", izvestaj.getText().toString().trim());
                        map.put(order.getDescriptionId()+"/gradeCustomer", Ocena);

                        firebaseDatabase.updateChildren(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful()) {
                                    Toast.makeText(mContext, "Your report was successfully submitted!", Toast.LENGTH_SHORT).show();
                                        DodadiOcena(order.getUserId(), Ocena);
                                } else {
                                    Toast.makeText(mContext, "There was an error! Please try again.", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                        dialog.dismiss();
                    }
                });

                alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        dialog.dismiss();
                    }
                });
                alert.show();
            }
        });

        viewHolder.txtInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder alert = new AlertDialog.Builder(mContext);

                alert.setTitle("Report:");

                LinearLayout layout = new LinearLayout(mContext);
                layout.setOrientation(LinearLayout.VERTICAL);

                final TextView textView = new TextView(mContext);
                textView.setText("Your report:");
                textView.setPadding(0,10,0,0);
                    textView.setTextSize(20);

                final TextView textView1 = new TextView(mContext);
                textView1.setText(order.getReportOrders());
                textView1.setPadding(15,10,0,0);
                textView1.setTextSize(16);

                final TextView textView2 = new TextView(mContext);
                textView2.setText("Grade: " + order.getGradeCustomer());
                textView2.setPadding(15,10,0,0);
                textView2.setTextSize(16);

                layout.addView(textView);
                layout.addView(textView1);
                layout.addView(textView2);

                if(order.getGradeOrders() != 0) {
                    final TextView textView3 = new TextView(mContext);
                    textView3.setText("Report for the customer:");
                    textView3.setPadding(0,10,0,0);
                        textView3.setTextSize(20);

                    final TextView textView4 = new TextView(mContext);
                    textView4.setText(order.getReportCustomer());
                    textView4.setPadding(15,10,0,0);
                    textView4.setTextSize(16);

                    final TextView textView5 = new TextView(mContext);
                    textView5.setText("Grade: " + order.getGradeOrders());
                    textView5.setPadding(15,10,0,0);
                    textView5.setTextSize(16);

                    layout.addView(textView3);
                    layout.addView(textView4);
                    layout.addView(textView5);
                }

                alert.setView(layout);

                alert.setNegativeButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        dialog.dismiss();
                    }
                });
                alert.show();
            }
        });
    }

    private void DodadiOcena(String userId, int ocena) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(userId);
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                int VkupnoOceni = user.getVkupnoOceni() + 1;
                int ZbirOceni = user.getZbirOceni() + ocena;
                user.setVkupnoOceni(VkupnoOceni);
                user.setZbirOceni(ZbirOceni);
                snapshot.getRef().setValue(user);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(mContext, "There was an error. Please try again!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return myList == null ? 0 : myList.size();
    }
}
