package com.example.individualproject;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
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

public class myAdapter extends RecyclerView.Adapter<myAdapter.ViewHolder> {

    private List<Order> myList;
    private int rowLayout;
    private Context mContext;

    private User volonter;

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView txtAktivnost, txtOpisAktivnost, txtVreme, txtAdresa, txtStatus,
                txtDelete, txtVolonter, txtInfo;
        public ImageView Pic;

        public ViewHolder(View itemView) {
            super(itemView);
            txtAktivnost = (TextView) itemView.findViewById(R.id.Activity);
            txtOpisAktivnost = (TextView) itemView.findViewById(R.id.Description);
            txtVreme = (TextView) itemView.findViewById(R.id.Time);
            txtAdresa = (TextView) itemView.findViewById(R.id.rwAdresa);
            txtStatus = (TextView) itemView.findViewById(R.id.Status);
            txtDelete = (TextView) itemView.findViewById(R.id.Delete);
            txtVolonter = (TextView) itemView.findViewById(R.id.rwVolonter);
            txtInfo = (TextView) itemView.findViewById(R.id.rwInfo);

            txtInfo.setVisibility(View.INVISIBLE);
        }
    }


    public myAdapter(List<Order> myList, int rowLayout, Context context) {
        this.myList = myList;
        this.rowLayout = rowLayout;
        this.mContext = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(rowLayout, viewGroup, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int i) {

        Order order = myList.get(i);
        viewHolder.txtAktivnost.setText(order.getFood());
        viewHolder.txtOpisAktivnost.setText("Description: "+order.getDescription());
        viewHolder.txtAdresa.setText("Address: " + order.getAdresa());
        viewHolder.txtStatus.setText(order.getStatus());

        viewHolder.txtDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder builder = new AlertDialog.Builder(mContext);

                builder.setTitle("Confirm");
                builder.setMessage("Are you sure you want to delete this order?");

                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Baranja");
                        databaseReference.child(order.getDescriptionId()).removeValue();
                        dialog.dismiss();
                    }
                });

                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                AlertDialog alert = builder.create();
                try {
                    alert.show();
                }
                catch (WindowManager.BadTokenException e) {
                    e.printStackTrace();
                }
            }
        });

        viewHolder.txtStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(order.getStatus().equals("Placed")) {
                    DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users")
                            .child(order.getCustomerUId());
                    reference.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            volonter = snapshot.getValue(User.class);
                            AlertDialog.Builder builder = new AlertDialog.Builder(mContext);

                            builder.setTitle("Confirm");
                            DecimalFormat decimalFormat = new DecimalFormat("##.0");
                            if (volonter.getVkupnoOceni() != 0) {
                                double ocena = (double) volonter.getZbirOceni() / volonter.getVkupnoOceni();
                                builder.setMessage(volonter.getName() + "    Grade: " + decimalFormat.format(ocena) + "\n"
                                        + "E-mail: " + volonter.getEmail() + "\n" + "Phone number: " + volonter.getPhone());
                            } else {
                                builder.setMessage(volonter.getName() + "    Grade: / \n"
                                        + "E-mail: " + volonter.getEmail() + "\n" + "Phone number: " + volonter.getPhone());
                            }

                            builder.setPositiveButton("Accept", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {

                                    Map<String, Object> map = new HashMap();

                                    DatabaseReference firebaseDatabase = FirebaseDatabase.getInstance().getReference("Orders");
                                    map.put(order.getDescriptionId() + "/status", "Out for delivery");
                                    map.put(order.getDescriptionId() + "/emailOrders", volonter.getEmail());
                                    map.put(order.getDescriptionId() + "/phoneOrders", volonter.getPhone());

                                    firebaseDatabase.updateChildren(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                Toast.makeText(mContext, "Your order is preparing!", Toast.LENGTH_SHORT).show();
                                            } else {
                                                Toast.makeText(mContext, "There was an error.Please try again!", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });

                                    dialog.dismiss();
                                }
                            });

                            builder.setNegativeButton("Decline", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Map<String, Object> map = new HashMap();

                                    DatabaseReference firebaseDatabase = FirebaseDatabase.getInstance().getReference("Orders");
                                    map.put(order.getDescriptionId() + "/status", "Active");
                                    map.put(order.getDescriptionId() + "/customerUid", "");

                                    firebaseDatabase.updateChildren(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                Toast.makeText(mContext, "The order was placed", Toast.LENGTH_SHORT).show();
                                            } else {
                                                Toast.makeText(mContext, "Настана грешка.Обидете се повторно!", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });

                                    dialog.dismiss();
                                }
                            });

                            AlertDialog alert = builder.create();
                            try {
                                alert.show();
                            } catch (WindowManager.BadTokenException e) {
                                e.printStackTrace();
                            }
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            Toast.makeText(mContext, "Настана грешка.Обидете се повторно!", Toast.LENGTH_SHORT).show();
                        }
                    });

                }else if(order.getStatus().equals("Delivered") && order.getGradeOrders() == 0) {
                    AlertDialog.Builder alert = new AlertDialog.Builder(mContext);

                    alert.setTitle("Delivered order");

                    LinearLayout layout = new LinearLayout(mContext);
                    layout.setOrientation(LinearLayout.VERTICAL);

                    final TextView textView = new TextView(mContext);
                    textView.setText("Grade the service :");
                    textView.setPadding(0,10,0,0);
                    textView.setTextSize(16);

                    final EditText izvestaj = new EditText(mContext);
                    izvestaj.setHint("Write a report");
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
                            map.put(order.getDescriptionId()+"/reportCustomer", izvestaj.getText().toString().trim());
                            map.put(order.getDescriptionId()+"/gradeOrders", Ocena);

                            firebaseDatabase.updateChildren(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()) {
                                        Toast.makeText(mContext, "The report was successfully submitted", Toast.LENGTH_SHORT).show();
                                        DodadiOcena(order.getCustomerUId(), Ocena);
                                    } else {
                                        Toast.makeText(mContext, "There was an error. please try again", Toast.LENGTH_SHORT).show();
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
                    try {
                        alert.show();
                    }
                    catch (WindowManager.BadTokenException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        if(!order.getEmailOrders().equals("")) {
            viewHolder.txtVolonter.setText("Customer service:\n" + order.getEmailOrders() + "       " + order.getPhoneOrders());
        }else{
            viewHolder.txtVolonter.setText("");
        }

        if(order.getGradeOrders() == 0) {
            viewHolder.txtInfo.setVisibility(View.INVISIBLE);
        } else {
            viewHolder.txtInfo.setVisibility(View.VISIBLE);
        }

        if(order.getGradeOrders() == 0 && (order.getStatus().equals("Delivered") || order.getStatus().equals("On hold"))) {
            viewHolder.txtStatus.setTextColor(Color.rgb(55,0,179));
            viewHolder.txtStatus.setPaintFlags(viewHolder.txtStatus.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        } else {
            viewHolder.txtStatus.setTextColor(Color.rgb(0,0,0));
            viewHolder.txtStatus.setPaintFlags(0);
        }

        viewHolder.txtInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder alert = new AlertDialog.Builder(mContext);

                alert.setTitle("Report");

                LinearLayout layout = new LinearLayout(mContext);
                layout.setOrientation(LinearLayout.VERTICAL);

                final TextView textView = new TextView(mContext);
                textView.setText("Your report:");
                textView.setPadding(0,10,0,0);
                textView.setTextSize(20);

                final TextView textView1 = new TextView(mContext);
                textView1.setText(order.getReportCustomer());
                textView1.setPadding(15,10,0,0);
                textView1.setTextSize(16);

                final TextView textView2 = new TextView(mContext);
                textView2.setText("Grade: " + order.getGradeOrders());
                textView2.setPadding(15,10,0,0);
                textView2.setTextSize(16);

                layout.addView(textView);
                layout.addView(textView1);
                layout.addView(textView2);

                if(order.getGradeOrders() != 0) {
                    final TextView textView3 = new TextView(mContext);
                    textView3.setText("Report:");
                    textView3.setPadding(0,10,0,0);
                    textView3.setTextSize(20);

                    final TextView textView4 = new TextView(mContext);
                    textView4.setText(order.getReportOrders());
                    textView4.setPadding(15,10,0,0);
                    textView4.setTextSize(16);

                    final TextView textView5 = new TextView(mContext);
                    textView5.setText("Grade: " + order.getGradeCustomer());
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

                try {
                    alert.show();
                }
                catch (WindowManager.BadTokenException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return myList == null ? 0 : myList.size();
    }

    private void DodadiOcena(String id, int ocena) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(id);
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
                Toast.makeText(mContext, "Настана грешка.Обидете се повторно!", Toast.LENGTH_SHORT).show();
            }
        });
    }
    }
