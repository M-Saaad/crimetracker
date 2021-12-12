package com.example.crimetracker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class PlanRoute extends FragmentActivity implements OnMapReadyCallback, TaskLoadedCallback  {

    private GoogleMap mMap;
    LatLng sydney;
    FusedLocationProviderClient client;
    private static final int MY_PERMISSION_ACCESS_COURSE_LOCATION = 11;
    SupportMapFragment mapFragment;
    LatLng latLng;
    Marker currentMarker;
    EditText editText;
    int flag;

    private MarkerOptions startLocation, endLocation;


    private Polyline currentPolyline;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plan_route);

        flag = 1;

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);




        client = LocationServices.getFusedLocationProviderClient(this);

        if (ActivityCompat.checkSelfPermission(PlanRoute.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            getCurrentLocation();
        } else {
            ActivityCompat.requestPermissions(PlanRoute.this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 44);
        }

    }

    private void getCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                    MY_PERMISSION_ACCESS_COURSE_LOCATION);
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        Task<Location> task = client.getLastLocation();
        task.addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location != null) {
                    if (ActivityCompat.checkSelfPermission(PlanRoute.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(PlanRoute.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        // TODO: Consider calling
                        //    ActivityCompat#requestPermissions
                        // here to request the missing permissions, and then overriding
                        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                        //                                          int[] grantResults)
                        // to handle the case where the user grants the permission. See the documentation
                        // for ActivityCompat#requestPermissions for more details.
                        return;
                    } else {
                        mMap.setMyLocationEnabled(true);
                        mapFragment.getMapAsync(new OnMapReadyCallback() {
                            @Override
                            public void onMapReady(GoogleMap googleMap) {
                                latLng = new LatLng(location.getLatitude()
                                        , location.getLongitude());

                                googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 16));
                            }
                        });
                    }
                }
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 44) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getCurrentLocation();
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

        //searching

        editText = findViewById(R.id.editTextLocation2);

        Places.initialize(getApplicationContext() , "AIzaSyBPBHcA905_IeKRnwOQglk5gF1guvwNRec");
        editText.setFocusable(false);
        editText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                List<Place.Field> fieldList = Arrays.asList(Place.Field.ADDRESS , Place.Field.LAT_LNG , Place.Field.NAME);

                Intent intent = new Autocomplete.IntentBuilder(AutocompleteActivityMode.FULLSCREEN, fieldList).build(PlanRoute.this);

                startActivityForResult(intent , 100);
            }
        });

        ExtendedFloatingActionButton arrowBtn = findViewById(R.id.arrowButton);

        mMap = googleMap;


        sydney = new LatLng(24.864375, 67.350002);
        MarkerOptions marker = new MarkerOptions().position(sydney).title("Start Location");
        marker.icon(BitmapDescriptorFactory.fromResource(R.drawable.pin2));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney, 15f));
        currentMarker = mMap.addMarker(marker.position(sydney));

        mMap.setOnCameraIdleListener(new GoogleMap.OnCameraIdleListener() {
            @Override
            public void onCameraIdle() {
//                int height = 100;
//                int width = 80;
//                Bitmap b = BitmapFactory.decodeResource(getResources(), R.drawable.pin);
//                Bitmap smallMarker = Bitmap.createScaledBitmap(b, width, height, false);
//                BitmapDescriptor smallMarkerIcon = BitmapDescriptorFactory.fromBitmap(smallMarker);
                if(flag == 1) {

                    currentMarker.remove();
                    LatLng center = mMap.getCameraPosition().target;
                    MarkerOptions m2 = new MarkerOptions();
                    m2.position(center);
//                m2.icon(smallMarkerIcon);
                    m2.icon(BitmapDescriptorFactory.fromResource(R.drawable.pin2));
                    currentMarker = mMap.addMarker(m2);
                    latLng = currentMarker.getPosition();
                    findViewById(R.id.pin).setVisibility(View.INVISIBLE);
                    TextInputEditText startLocationTextEdit = findViewById(R.id.editTextLocation2);


                    startLocationTextEdit.setText(getStringAddress(currentMarker.getPosition().latitude, currentMarker.getPosition().longitude));
                }
            }
        });

        mMap.setOnCameraMoveListener(new GoogleMap.OnCameraMoveListener() {
            @Override
            public void onCameraMove() {
                currentMarker.remove();
                findViewById(R.id.pin).setVisibility(View.VISIBLE);
            }
        });

        arrowBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(startLocation == null) {
                    //changing hint to endlocation

                    TextInputLayout layoutUser;
                    layoutUser = (TextInputLayout) findViewById(R.id.editTextStartLocation);
                    layoutUser.setHint("End Location");
                    startLocation = new MarkerOptions().position(new LatLng(currentMarker.getPosition().latitude, currentMarker.getPosition().longitude));
                    mMap.addMarker(startLocation);



                }else{
                    endLocation = new MarkerOptions().position(new LatLng(currentMarker.getPosition().latitude, currentMarker.getPosition().longitude));
                    mMap.addMarker(endLocation);
                    arrowBtn.setVisibility(View.INVISIBLE);
                    String url = getUrl(startLocation.getPosition() , endLocation.getPosition() , "driving");
                    new FetchURL(PlanRoute.this).execute(url , "driving");
                    flag = 0;
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 100 && resultCode == RESULT_OK){
            Place place = Autocomplete.getPlaceFromIntent(data);
            editText.setText(place.getAddress());

            sydney = place.getLatLng();
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney, 15f));



        }else if(resultCode == AutocompleteActivity.RESULT_ERROR){
            Toast.makeText(PlanRoute.this , "Error occoured", Toast.LENGTH_LONG);
        }
    }

    private String getUrl(LatLng origin, LatLng dest, String directionMode) {
        // Origin of route
        String str_origin = "origin=" + origin.latitude + "," + origin.longitude;
        // Destination of route
        String str_dest = "destination=" + dest.latitude + "," + dest.longitude;
        // Mode
        String mode = "mode=" + directionMode;
        // Building the parameters to the web service
        String parameters = str_origin + "&" + str_dest + "&" + mode;
        // Output format
        String output = "json";
        // Building the url to the web service
        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters + "&key=" + "AIzaSyBPBHcA905_IeKRnwOQglk5gF1guvwNRec";


        return url;
    }

    public void onTaskDone(Object... values) {
        if (currentPolyline != null)
            currentPolyline.remove();
        currentPolyline = mMap.addPolyline((PolylineOptions) values[0]);
    }

    public String getStringAddress(Double lat, Double lng) {
        String address = "";
        Geocoder geocoder;
        List<Address> addresses;
        geocoder = new Geocoder(this, Locale.getDefault());
        try {
            addresses = geocoder.getFromLocation(lat, lng, 1);
            address = addresses.get(0).getAddressLine(0);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return address;
    }
}

