package com.example.smartwastedetector;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.smartwastedetector.Services.NotificationService;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.kaopiz.kprogresshud.KProgressHUD;
import com.nightonke.boommenu.BoomButtons.HamButton;
import com.nightonke.boommenu.BoomButtons.OnBMClickListener;
import com.nightonke.boommenu.BoomMenuButton;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

public class GarbageLocations extends FragmentActivity implements OnMapReadyCallback {


    //Database variables
    final FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference ref = database.getReference();


    //Maps Variables
    private GoogleMap mMap;
    public static final int REQUEST_LOCATION = 1;
    private static int AUTOCOMPLETE_REQUEST_CODE = 1;

    AutocompleteSupportFragment autocompleteFragment;

    //Location variables
    LocationManager locationManager;
    protected LocationListener locationListener;
    protected double latitude;
    protected double longitude;

    //local variables
    ArrayList<Double> latitudes = new ArrayList<>();
    ArrayList<Double> longitudes = new ArrayList<>();
    ArrayList<String> pinCodes = new ArrayList<>();
    ArrayList<String> ragPickersNames = new ArrayList<>();
    ArrayList<String> ragPickersID = new ArrayList<>();
    ArrayList<Long> ragPickersNumbers = new ArrayList<>();
    ArrayList<String> finalPincodes = new ArrayList<>();
    ArrayList<String> garbagePicsURL = new ArrayList<>();

    ArrayList<String> garbageLocationIds = new ArrayList<>();
    ArrayList<Double> garbageIndices = new ArrayList<>();

    ArrayList<Integer> pincodesFrequencies = new ArrayList<>();


    ArrayList<String> timeStamps = new ArrayList<>();
    ArrayList<Boolean> cleaned = new ArrayList<>();
    boolean openMenu;
    int newHt = 0,initialHt = 0,parentViewInitHt,mScreenHeight;


    //Views
    RelativeLayout whiteMenuView;
    ImageButton menuBtn,myLocationBtn;
    RelativeLayout parentRl;
    SearchView searchView;
    Button AssignTask,screenDismissBtn;
    BoomMenuButton bmb;
    BitmapDescriptor icon;
    ListView pincodeLV;
    SupportMapFragment mapFragment;
    ImageView garbagePic;
    KProgressHUD hud;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_garbage_locations);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        ///Creating notification service
        Intent notificationServiceIntent = new Intent(GarbageLocations.this, NotificationService.class);
        startService(notificationServiceIntent);



        // Start the autocomplete intent.

        DisplayMetrics displaymetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        mScreenHeight = displaymetrics.heightPixels;

        icon = BitmapDescriptorFactory.fromResource(R.drawable.pin2);

        setLocationManagers();
        connectViews();


        parentViewInitHt = parentRl.getHeight();
        final RelativeLayout.LayoutParams paramsInit = (RelativeLayout.LayoutParams) whiteMenuView.getLayoutParams();
        initialHt = paramsInit.height;
        placeLocation();


    }

    public void setLocationManagers(){
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION);
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            buildAlertMessageNoGps("Please Turn ON your GPS Connection");
        } else if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            getLocation();
        }
        getGarbageLocations();
    }

    public void connectViews(){
        whiteMenuView = findViewById(R.id.whiteMenuView);
        menuBtn = findViewById(R.id.MenuBtn);
        menuBtn.setOnClickListener(BtnListener);
        parentRl = findViewById(R.id.parentRl);
        garbagePic = findViewById(R.id.garbagePic);
//        parentRl.setOnClickListener(BtnListener);
        screenDismissBtn = findViewById(R.id.ScreenDismissButton);
        screenDismissBtn.setOnClickListener(BtnListener);

        AssignTask = findViewById(R.id.AssignTask);
        AssignTask.setOnClickListener(BtnListener);
        myLocationBtn = findViewById(R.id.MyLocation);
        myLocationBtn.setOnClickListener(BtnListener);

        pincodeLV = findViewById(R.id.PincodesListView);

        setUpPincodesListView();

        bmb = (BoomMenuButton) findViewById(R.id.bmb);
        setBMB();

    }

    public void setUpPincodesListView(){

        PincodeAdapter pincodeAdapter = new PincodeAdapter();
        pincodeLV.setAdapter(pincodeAdapter);
    }
    public void setBMB() {

        ArrayList<String> menuNames = new ArrayList<String>(
                Arrays.asList("Garbage Locations",
                        "Search for more location",
                        "Help",
                        "Settings"));

        ArrayList<String> menuNamesSubText = new ArrayList<String>(
                Arrays.asList("Click here to assign a rag picker",
                        "",
                        "Click here if you need any assistance",
                        ""));

        for (int i = 0; i < bmb.getPiecePlaceEnum().pieceNumber(); i++) {

            HamButton.Builder builder = new HamButton.Builder()
                    .normalImageRes(R.drawable.pin)
                    .normalText(menuNames.get(i))
                    .subNormalText(menuNamesSubText.get(i))
                    .listener(new OnBMClickListener() {
                        @Override
                        public void onBoomButtonClick(int index) {
                            // When the boom-button corresponding this builder is clicked.
                            if(index == 1){
                                Toast.makeText(GarbageLocations.this, "Clicked " + index, Toast.LENGTH_SHORT).show();
                                // Start the autocomplete intent.
                                List<Place.Field> fields = Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG);
                                if (!Places.isInitialized()) {
                                    Places.initialize(getApplicationContext(), getString(R.string.google_maps_key), Locale.US);
                                }
                                Intent intent = new Autocomplete.IntentBuilder(AutocompleteActivityMode.OVERLAY, fields).build(GarbageLocations.this);
                                startActivityForResult(intent, AUTOCOMPLETE_REQUEST_CODE);
                            }
                            else if(index == 0){
                                if(finalPincodes.size()>0){
                                    screenDismissBtn.setVisibility(View.VISIBLE);
                                    expandView(pincodeLV,0,mScreenHeight - 600);
                                }else {
                                    Toast.makeText(GarbageLocations.this, "Please wait for a few seconds while we gather all data", Toast.LENGTH_SHORT).show();
                                    collapseView(pincodeLV,pincodeLV.getLayoutParams().height,0);
                                }

                            }
                        }
                    });
            bmb.addBuilder(builder);
        }

    }


    public void getLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission
                (this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION);
            Log.d("INNN HEREEEE","YESS");

        } else {
            Location location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            Log.d("INNN HEREEEE","YESS");

            Location location1 = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            Location location2 = locationManager.getLastKnownLocation(LocationManager. PASSIVE_PROVIDER);
//            Log.d("LAT IS ", String.valueOf(location.getLatitude()));

            if (location != null) {

                latitude = location.getLatitude();
                longitude = location.getLongitude();

                Log.d("LAT IS ", String.valueOf(latitude));
                Log.d("Long is ", String.valueOf(longitude));

            } else  if (location1 != null) {
                latitude = location1.getLatitude();
                longitude = location1.getLongitude();


                Log.d("LAT1 IS ", String.valueOf(latitude));
                Log.d("Long1 is ", String.valueOf(longitude));



            } else  if (location2 != null) {
                latitude = location2.getLatitude();
                longitude = location2.getLongitude();

                Log.d("LAT2 IS ", String.valueOf(latitude));
                Log.d("Long2 is ", String.valueOf(longitude));


            }else{

                Toast.makeText(this,"Unable to Trace your location",Toast.LENGTH_SHORT).show();
                getLocationUsingCoarse();
            }
        }
    }

    public void getLocationUsingCoarse(){
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission
                (this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, REQUEST_LOCATION);
            Log.d("INNN HEREEEE","YESS");

        } else {
            Location location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            Log.d("INNN HEREEEE","YESS");

            Location location1 = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            Location location2 = locationManager.getLastKnownLocation(LocationManager. PASSIVE_PROVIDER);
//            Log.d("LAT IS ", String.valueOf(location.getLatitude()));

            if (location != null) {

                latitude = location.getLatitude();
                longitude = location.getLongitude();

                Log.d("LAT IS ", String.valueOf(latitude));
                Log.d("Long is ", String.valueOf(longitude));

            } else  if (location1 != null) {
                latitude = location1.getLatitude();
                longitude = location1.getLongitude();


                Log.d("LAT1 IS ", String.valueOf(latitude));
                Log.d("Long1 is ", String.valueOf(longitude));



            } else  if (location2 != null) {
                latitude = location2.getLatitude();
                longitude = location2.getLongitude();

                Log.d("LAT2 IS ", String.valueOf(latitude));
                Log.d("Long2 is ", String.valueOf(longitude));


            }else{

                Toast.makeText(this,"Unable to Trace your location",Toast.LENGTH_SHORT).show();
//                getLocationUsingCoarse();
            }
        }
    }
    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMinZoomPreference(6.0f);
        mMap.setMaxZoomPreference(16.0f);


        // Add a marker in Sydney and move the camera
//        LatLng myLocation = new LatLng(latitude, longitude);

        //// For Emulator
        LatLng myLocation = new LatLng(18.573361, 73.875861);

        mMap.addMarker(new MarkerOptions()
            .position(myLocation)
            .icon(icon)
            .title("My Location"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(myLocation));

        // Create a LatLngBounds that includes Australia.
        LatLngBounds INDIA = new LatLngBounds(
                new LatLng(18.573361, 73.875861), new LatLng(18.573361, 73.875861));
        // Set the camera to the greatest possible zoom level that includes the bounds
//        mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(INDIA, 15));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(INDIA.getCenter(), 50));

//        for(int i =0;i<longitudes.size();i++){
//            Log.d("GARBAGELOCATIONS",String.valueOf(latitudes.get(i)));
//            mMap.addMarker(new MarkerOptions().position(new LatLng(latitudes.get(i), longitudes.get(i))).title(String.valueOf(i)));
//        }

        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                garbagePic.setVisibility(View.GONE);
                collapseView(pincodeLV,pincodeLV.getLayoutParams().height,0);

            }
        });

        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {

              Log.d("MARKER_CLICKED", String.valueOf(marker.getTitle()));

                garbagePic.setVisibility(View.GONE);
                new DownloadImageTask(garbagePic,garbagePicsURL.get(Integer.parseInt(marker.getTitle()))).execute();


                return false;
            }
        });
    }



    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;
        String url;
        public DownloadImageTask(ImageView bmImage,String url) {
            this.bmImage = bmImage;
            this.url = url;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            hud = KProgressHUD.create(GarbageLocations.this)
                    .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                    .setLabel("Loading")
                    .setMaxProgress(100).show();

        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = url;
            Bitmap bmp = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                bmp = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                hud.dismiss();
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return bmp;
        }
        protected void onPostExecute(Bitmap result) {
            bmImage.setVisibility(View.VISIBLE);
            hud.dismiss();
            bmImage.setImageBitmap(result);
        }
    }
    public void placeLocation(){

        List<Place.Field> fields = Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG);
        if (!Places.isInitialized()) {
            Places.initialize(getApplicationContext(), getString(R.string.google_maps_key), Locale.US);
        }
        // Start the autocomplete intent.
//        Intent intent = new Autocomplete.IntentBuilder(AutocompleteActivityMode.OVERLAY, fields).build(this);
//        startActivityForResult(intent, AUTOCOMPLETE_REQUEST_CODE);
        autocompleteFragment = (AutocompleteSupportFragment) getSupportFragmentManager().findFragmentById(R.id.autocomplete_fragment);

        // Specify the types of place data to return.
        autocompleteFragment.setPlaceFields(fields);
        // Set up a PlaceSelectionListener to handle the response.
        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(@NonNull Place place) {

                Log.i("TAG", "Place: " + place.getLatLng() + ", " + place.getId());

                mMap.addMarker(new MarkerOptions().position(place.getLatLng()).title(place.getName().toString()));
                mMap.animateCamera(CameraUpdateFactory.newLatLng(place.getLatLng()));

                collapseView(whiteMenuView,initialHt,0);


            }

            @Override
            public void onError(@NonNull Status status) {

            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                assert data != null;
                Place place = Autocomplete.getPlaceFromIntent(data);
                Log.i("TAG", "Place: " + place.getLatLng() + ", " + place.getId());

                mMap.addMarker(new MarkerOptions().position(Objects.requireNonNull(place.getLatLng())).title(place.getName()));
                mMap.moveCamera(CameraUpdateFactory.newLatLng(place.getLatLng()));

            } else if (resultCode == AutocompleteActivity.RESULT_ERROR) {
                // TODO: Handle the error.
                Status status = Autocomplete.getStatusFromIntent(data);
                Log.i("ERROR ", status.getStatusMessage());
            } else if (resultCode == RESULT_CANCELED) {

                // The user canceled the operation.

            }
            return;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    public void getGarbageLocations(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                ref.child("Detected").addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                        final Map<String, Object> map = (Map<String, Object>) dataSnapshot.getValue();

                        garbageLocationIds.add(String.valueOf(dataSnapshot.getKey().toString()));
                        latitudes.add(Double.parseDouble(String.valueOf(map.get("latitude"))));
                        longitudes.add(Double.parseDouble(String.valueOf(map.get("longitude"))));
                        garbagePicsURL.add(String.valueOf(map.get("image")));
                        pinCodes.add(String.valueOf(map.get("Pincode")));


                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                for(String element : pinCodes){

                                    if(Collections.frequency(finalPincodes, element) == 0) {
                                        Log.d("ELEMENT ",element);
                                        finalPincodes.add(element);
                                        pincodesFrequencies.add(Collections.frequency(pinCodes,element));

                                        ref.child("Indexes").addValueEventListener(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                Map<String, Object> map = (Map<String, Object>) dataSnapshot.getValue();
                                                for(String pincode: finalPincodes){
                                                    garbageIndices.add(Double.parseDouble(String.valueOf(map.get(pincode))));
                                                    Log.d("INDEX ",String.valueOf(map.get(pincode)));

                                                }
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError databaseError) {

                                            }
                                        });
                                    }

                                }
                                setLocationsOfGarbageOnMap();
                            }
                        },1000);
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

                ref.child("Users").addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                        Map<String, Object> map = (Map<String, Object>) dataSnapshot.getValue();

                        ragPickersID.add((String.valueOf(dataSnapshot.getKey())));
                        ragPickersNames.add((String.valueOf(map.get("Name"))));
//                        ragPickersNumbers.add(Long.parseLong(String.valueOf("Number")));



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
        }).start();

    }


    public void setLocationsOfGarbageOnMap(){
        // Add a marker in Sydney and move the camera
// Set a preference for minimum and maximum zoom.
        BitmapDescriptor icon = BitmapDescriptorFactory.fromResource(R.drawable.garbage);

        for(int i =0;i<longitudes.size();i++){
            Log.d("GARBAGELOCATIONS",String.valueOf(latitudes.get(i)));
            mMap.addMarker(new MarkerOptions()
                 .position(new LatLng(latitudes.get(i), longitudes.get(i)))
                 .icon(icon)
                 .title(String.valueOf(i)));
        }
    }




    public class PincodeAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return finalPincodes.size();
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
            convertView =  getLayoutInflater().inflate(R.layout.pincode_row,null);

            TextView pincodeTV,garbageLocations;
            Button assignBtn;

            if(convertView != null){

                garbageLocations = convertView.findViewById(R.id.GarbageLocationPerPincode);
                garbageLocations.setText("Garbage Locations "+String.valueOf(pincodesFrequencies.get(position)));

                pincodeTV = convertView.findViewById(R.id.PickerNameTV);
                pincodeTV.setText(finalPincodes.get(position));


                assignBtn = convertView.findViewById(R.id.ChoosePicker);

                if(position < garbageIndices.size()) {
                    Log.d(" INDICE ", String.valueOf(garbageIndices.get(position)));

                    if(garbageIndices.get(position) > 20000.0) {
                        assignBtn.setBackgroundResource(R.drawable.extreme_index);
                    }else if(garbageIndices.get(position) > 8000.0){
                        assignBtn.setBackgroundResource(R.drawable.medium_index);
                    }else if(garbageIndices.get(position) < 8000.0){
                        assignBtn.setBackgroundResource(R.drawable.low_index);
                    }
                }
                assignBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(GarbageLocations.this,AvailablePickers.class);
                        Bundle bundle = new Bundle();
//                        bundle.putStringArrayList("GarbageLocationsID",garbageLocationIds);
                        bundle.putString("Pincode",finalPincodes.get(position));
                        intent.putExtras(bundle);
                        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                        startActivity(intent);
                    }
                });

            }

            return convertView;
        }
    }




        //Create an anonymous implementation of OnClickListener
    private View.OnClickListener BtnListener = new View.OnClickListener() {
        public void onClick(View v) {
            // do something when the button is clicked
            if(v == findViewById(R.id.MenuBtn)) {
                if (!openMenu) {
                    openMenu = true;

                    newHt = parentRl.getHeight() / 2;
                    Log.d("NEWHT", String.valueOf(newHt));

//                ObjectAnimator.ofFloat(v, "rotation", 0f, 90f).start();
                    expandView(findViewById(R.id.whiteMenuView), initialHt, newHt);

                } else {
                    openMenu = false;
                    ObjectAnimator.ofFloat(v, "rotation", 90f, 0f).start();
                    collapseView(findViewById(R.id.whiteMenuView), newHt, 0);

                }
            }
            else if(v == findViewById(R.id.MyLocation)){

                LatLng myLocation = new LatLng(latitude,longitude);
                mMap.addMarker(new MarkerOptions().position(myLocation).title("My Location").icon(icon));
                mMap.animateCamera(CameraUpdateFactory.newLatLng(myLocation));
                LatLngBounds INDIA = new LatLngBounds(
                        new LatLng(latitude, longitude), new LatLng(latitude, longitude));
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(INDIA.getCenter(), 50));
            }
            else if (v == findViewById(R.id.ScreenDismissButton)){
                collapseView(pincodeLV,pincodeLV.getLayoutParams().height,0);
                screenDismissBtn.setVisibility(View.GONE);
            }

        }
    };





    boolean isTouch;
    @Override
    public boolean onTouchEvent(MotionEvent event) {

        int X = (int) event.getX();
        int Y = (int) event.getY();
        int eventaction = event.getAction();

        switch (eventaction) {
            case MotionEvent.ACTION_DOWN:
                Toast.makeText(this, "ACTION_DOWN AT COORDS "+"X: "+X+" Y: "+Y, Toast.LENGTH_SHORT).show();
                isTouch = true;
                break;

            case MotionEvent.ACTION_MOVE:
                Toast.makeText(this, "MOVE "+"X: "+X+" Y: "+Y, Toast.LENGTH_SHORT).show();
                break;

            case MotionEvent.ACTION_UP:
                Toast.makeText(this, "ACTION_UP "+"X: "+X+" Y: "+Y, Toast.LENGTH_SHORT).show();
                break;
        }
        return true;
    }




    public void expandView(final View v,int initialHt,int finalHt){

        ObjectAnimator.ofFloat(menuBtn, "rotation", 0f, 90f).start();

        ValueAnimator slideAnimator = ValueAnimator.ofInt(initialHt,finalHt).setDuration(400);
        slideAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                Integer value = (Integer) animation.getAnimatedValue();
                v.getLayoutParams().height = value.intValue();
                v.requestLayout();

            }
        });

        AnimatorSet set = new AnimatorSet();
        set.play(slideAnimator);
        set.setInterpolator(new AccelerateDecelerateInterpolator());
        set.start();

    }

    public void collapseView(final View v,int initialHt,int finalHt){
        ObjectAnimator.ofFloat(menuBtn, "rotation", 90f, 0f).start();


        ValueAnimator slideAnimator = ValueAnimator.ofInt(initialHt,finalHt).setDuration(500);
        slideAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                // get the value the interpolator is at
                Integer value = (Integer) animation.getAnimatedValue();
                // I'm going to set the layout's height 1:1 to the tick
                v.getLayoutParams().height = value.intValue();
                // force all layouts to see which ones are affected by
                // this layouts height change
                v.requestLayout();

            }
        });

        AnimatorSet set = new AnimatorSet();
        set.play(slideAnimator);
        set.setInterpolator(new AccelerateDecelerateInterpolator());
        set.start();

    }
    protected void buildAlertMessageNoGps(String message) {

        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(message)
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                        startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    }
                });

        final AlertDialog alert = builder.create();
        alert.show();
    }

}
