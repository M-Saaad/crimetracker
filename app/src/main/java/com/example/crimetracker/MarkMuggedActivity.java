package com.example.crimetracker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MarkMuggedActivity extends FragmentActivity implements OnMapReadyCallback {

    SupportMapFragment mapFragment;
    private GoogleMap mMap;
    FusedLocationProviderClient client;
    LatLng latLng;
    LatLng sydney;
    Marker currentMarker;
    private static final int MY_PERMISSION_ACCESS_COURSE_LOCATION = 11;
    FirebaseAuth fAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LocationManager lm = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        boolean gps_enabled = false;
        boolean network_enabled = false;

        setContentView(R.layout.activity_mark_mugged);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        client = LocationServices.getFusedLocationProviderClient(this);

        if (ActivityCompat.checkSelfPermission(MarkMuggedActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            getCurrentLocation();
        } else {
            ActivityCompat.requestPermissions(MarkMuggedActivity.this,
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
                    if (ActivityCompat.checkSelfPermission(MarkMuggedActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(MarkMuggedActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
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

        TextInputEditText locationEditText = (TextInputEditText) findViewById(R.id.editTextLocation2);
        TextInputEditText nameEditText = (TextInputEditText) findViewById(R.id.editTextName2);
        TextInputEditText itemEditText = (TextInputEditText) findViewById(R.id.editTextItems2);
        TextInputEditText detailEditText = (TextInputEditText) findViewById(R.id.editTextDetail2);
        ExtendedFloatingActionButton arrowBtn = findViewById(R.id.arrowButton);
        ExtendedFloatingActionButton submitBtn = findViewById(R.id.submitButton);

        mMap = googleMap;

//        int height = 100;
//        int width = 80;
//        Bitmap b = BitmapFactory.decodeResource(getResources(), R.drawable.pin);
//        Bitmap smallMarker = Bitmap.createScaledBitmap(b, width, height, false);
//        BitmapDescriptor smallMarkerIcon = BitmapDescriptorFactory.fromBitmap(smallMarker);

        // Add a marker in Sydney and move the camera
        sydney = new LatLng(24.864375, 67.350002);
        MarkerOptions marker = new MarkerOptions().position(sydney).title("Crime");
//        marker.icon(smallMarkerIcon);
        marker.icon(BitmapDescriptorFactory.fromResource(R.drawable.pin));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney, 15f));
        currentMarker = mMap.addMarker(marker.position(sydney));
//        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
//            @Override
//            public void onMapClick(LatLng point) {
//                mMap.clear();
//                mMap.addMarker(marker.position(point));
//                et1.setVisibility(View.VISIBLE);
//                et2.setVisibility(View.VISIBLE);
//                et3.setVisibility(View.VISIBLE);
//                submitBtn.setVisibility(View.VISIBLE);
//            }
//        });

        mMap.setOnCameraIdleListener(new GoogleMap.OnCameraIdleListener() {
            @Override
            public void onCameraIdle() {
//                int height = 100;
//                int width = 80;
//                Bitmap b = BitmapFactory.decodeResource(getResources(), R.drawable.pin);
//                Bitmap smallMarker = Bitmap.createScaledBitmap(b, width, height, false);
//                BitmapDescriptor smallMarkerIcon = BitmapDescriptorFactory.fromBitmap(smallMarker);

                currentMarker.remove();
                LatLng center = mMap.getCameraPosition().target;
                MarkerOptions m2 = new MarkerOptions();
                m2.position(center);
//                m2.icon(smallMarkerIcon);
                m2.icon(BitmapDescriptorFactory.fromResource(R.drawable.pin));
                currentMarker = mMap.addMarker(m2);
                latLng = currentMarker.getPosition();
                findViewById(R.id.pin).setVisibility(View.INVISIBLE);
                TextInputEditText locationTextEdit = findViewById(R.id.editTextLocation2);
                locationTextEdit.setText(getStringAddress(currentMarker.getPosition().latitude, currentMarker.getPosition().longitude));
//                Toast.makeText(getApplicationContext(),getStringAddress(currentMarker.getPosition().latitude, currentMarker.getPosition().longitude),Toast.LENGTH_SHORT).show();
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
                findViewById(R.id.editTextItems).setVisibility(View.VISIBLE);
                findViewById(R.id.editTextName).setVisibility(View.VISIBLE);
                findViewById(R.id.editTextDetail).setVisibility(View.VISIBLE);
                submitBtn.setVisibility(View.VISIBLE);
                arrowBtn.setVisibility(View.INVISIBLE);
            }
        });

        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = nameEditText.getText().toString().trim();
                String item = itemEditText.getText().toString().trim();
                String detail = detailEditText.getText().toString().trim();
                String locationName = locationEditText.getText().toString().trim();
                String locationLatLng = (currentMarker.getPosition().latitude + ", " + currentMarker.getPosition().longitude);

                SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss", Locale.getDefault());
                String dateTime = sdf.format(new Date());

                if (name.isEmpty()) {
                    nameEditText.setError("Name is Required");
                    nameEditText.requestFocus();
                    return;
                }

                if (item.isEmpty()) {
                    itemEditText.setError("Email is Required!");
                    itemEditText.requestFocus();
                    return;
                }

                if (locationName.isEmpty()) {
                    locationEditText.setError("Password is Required!");
                    locationEditText.requestFocus();
                    return;
                }

                if (detail.isEmpty()) {
                    detailEditText.setError("Password is Required!");
                    detailEditText.requestFocus();
                    return;
                }

                ItemDetail itemDetail = new ItemDetail(name, item, detail, locationName, locationLatLng, dateTime);

                FirebaseDatabase.getInstance().getReference("Dashboard")
                        .child(dateTime)
                        .setValue(itemDetail).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(MarkMuggedActivity.this, "Successfully marked mugged activity", Toast.LENGTH_LONG).show();
//                            findViewById(R.id.editTextItems).setVisibility(View.INVISIBLE);
//                            findViewById(R.id.editTextName).setVisibility(View.INVISIBLE);
//                            findViewById(R.id.editTextDetail).setVisibility(View.INVISIBLE);
//                            submitBtn.setVisibility(View.INVISIBLE);
//                            arrowBtn.setVisibility(View.VISIBLE);
                        } else {
                            Toast.makeText(MarkMuggedActivity.this, "Unsuccesfull, please try again!", Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }
        });
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