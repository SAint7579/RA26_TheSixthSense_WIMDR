package com.example.smartwastedetector;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.w3c.dom.Text;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class AvailablePickers extends AppCompatActivity {
    public class task{
        public double latitude,longitude;
        public boolean Completed;

        public task(double latitude, double longitude, boolean completed) {
            this.latitude = latitude;
            this.longitude = longitude;
            Completed = completed;
        }


    }



    //Database variables
    final FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference ref = database.getReference();


    //Class variables
    ArrayList<String> pickersIds = new ArrayList<>();
    ArrayList<String> pickerNames = new ArrayList<>();
    ArrayList<Long> pickerNumbers = new ArrayList<>();
//    ArrayList<String> garbageLocationIds = new ArrayList<>();
    ArrayList<String> garbageLocationIds = new ArrayList<>();

    String pincode;
    double latitude,longitude;

    // Views
    ListView pickerLV;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_available_pickers);

        getSomeData();
        connectViews();
        getRagPickersInfo();
    }

    public void getSomeData(){
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
//        garbageLocationIds = bundle.getStringArrayList("GarbageLocationsID");
        pincode = bundle.getString("Pincode");

        Log.d("PINCODE_SELE",pincode);

    }

    public void connectViews() {

        pickerLV = findViewById(R.id.AvailPickerLV);

    }

    public void getRagPickersInfo(){

        ref.child("Detected").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                final Map<String, Object> map = (Map<String, Object>) dataSnapshot.getValue();
                if(map != null){
                    if(String.valueOf(map.get("Pincode")).equals(pincode)){
                        garbageLocationIds.add(dataSnapshot.getKey());
                        latitude = Double.parseDouble(String.valueOf(map.get("latitude")));
                        longitude = Double.parseDouble(String.valueOf(map.get("longitude")));

                    }
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        ref.child("users").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Log.d(" USERID ",dataSnapshot.getKey());
                pickersIds.add(dataSnapshot.getKey().toString());
                final Map<String, Object> map = (Map<String, Object>) dataSnapshot.getValue();

                if(map != null){
                    pickerNames.add(String.valueOf(map.get("Name")));
                    pickerNumbers.add(Long.parseLong(String.valueOf(map.get("Number"))));


                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            pickerLV.setAdapter(new AvailablePickerAdapter());
                        }
                    },500);
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public class AvailablePickerAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return pickerNames.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {

            convertView =  getLayoutInflater().inflate(R.layout.avail_picker_lv_row,null);

            Button choosePicker;
            TextView pickerTV;

            if(convertView != null && position < pickerNames.size()){

                choosePicker = convertView.findViewById(R.id.ChoosePicker);
                pickerTV = convertView.findViewById(R.id.PickerNameTV);
                pickerTV.setText(pickerNames.get(position));

                choosePicker.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        for(String id: garbageLocationIds){

                            Log.d("GAR_ID",pickersIds.get(position));
                            final HashMap<String, Object> updatePickerID = new HashMap<String, Object>();
                            updatePickerID.put("collectorid",pickersIds.get(position));
                            ref.child("Detected").child(id).updateChildren(updatePickerID);

                            task newTask = new task(latitude,longitude,false);
                            final String uniqueID = UUID.randomUUID().toString();
                            String taskID = (uniqueID.substring(uniqueID.length()-8));

                            ref.child("users").child(pickersIds.get(position)).child("Tasks").child(taskID).setValue(newTask);
                            Toast.makeText(AvailablePickers.this, "This Picker has been assigned to the pickup task", Toast.LENGTH_SHORT).show();
                            finish();

                        }




                    }
                });
            }

            return convertView;
        }
    }

}