package com.example.crimetracker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.Settings;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Menu extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    Button planBtn;
    Button markBtn;
//    Button uploadBtn;

    DrawerLayout drawerLayout;
    NavigationView navigationView;
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        navigationView.bringToFront();
        navigationView.bringToFront();
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open_nav_bar, R.string.close_nav_bar);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.setCheckedItem(R.id.nav_home);

        LocationManager lm = (LocationManager) Menu.this.getSystemService(Context.LOCATION_SERVICE);
        boolean gps_enabled = false;
        boolean network_enabled = false;

        try {
            gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
        } catch (Exception ex) {
        }

        try {
            network_enabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        } catch (Exception ex) {
        }

        if (!gps_enabled && !network_enabled) {
            // notify user
            new AlertDialog.Builder(Menu.this)
                    .setMessage("GPS not allowed")
                    .setPositiveButton("Open Location Setting", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                            Menu.this.startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                        }
                    })
                    .setNegativeButton("Cancel", null)
                    .show();
        }

        planBtn = findViewById(R.id.planRouteButton);
        markBtn = findViewById(R.id.markMuggedActivityButton);
//        uploadBtn = findViewById(R.id.GenerateSOS);

        planBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switchActivities();
            }
        });

        markBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) { switchActivities2(); }
        });

//        uploadBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) { switchActivities3(); }
//        });
    }

    private void switchActivities() {
        Intent switchActivityIntent = new Intent(this, PlanRoute.class);
        startActivity(switchActivityIntent);
    }

    private void switchActivities2() {
        Intent switchActivityIntent = new Intent(this, MarkMuggedActivity.class);
        startActivity(switchActivityIntent);
    }

    private void switchActivities3() {
        Intent switchActivityIntent = new Intent(this, GenerateSOS.class);
        startActivity(switchActivityIntent);
    }

//    private void switchActivities4() {
//        Intent switchActivityIntent = new Intent(this, UserProfile.class);
//        startActivity(switchActivityIntent);
//    }

    private void switchActivities5() {
        Intent switchActivityIntent = new Intent(this, Login.class);
        startActivity(switchActivityIntent);
    }

    @Override
    public void onBackPressed() {
        if(drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        }
        else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        Intent intent, intent2;
        String name, email, contactNo, password;

        switch (item.getItemId()) {
            case R.id.nav_home:
                break;
            case R.id.nav_plan_route:
                switchActivities();
                break;
            case R.id.nav_mark_mugged_activity:
                switchActivities2();
                break;
            case R.id.nav_id_sos:

                intent = getIntent();

                name = intent.getStringExtra("name");
                email = intent.getStringExtra("email");
                contactNo = intent.getStringExtra("contactNumber");
                password = intent.getStringExtra("password");

                intent2 = new Intent(getApplicationContext(), GenerateSOS.class);

                intent2.putExtra("name", name);
                intent2.putExtra("email", email);
                intent2.putExtra("contactNumber", contactNo);
                intent2.putExtra("password", password);
                startActivity(intent2);
                break;

//                switchActivities3();
//                break;
            case R.id.nav_profile:
                intent = getIntent();

                name = intent.getStringExtra("name");
                email = intent.getStringExtra("email");
                contactNo = intent.getStringExtra("contactNumber");
                password = intent.getStringExtra("password");

                intent2 = new Intent(getApplicationContext(), UserProfile.class);

                intent2.putExtra("name", name);
                intent2.putExtra("email", email);
                intent2.putExtra("contactNumber", contactNo);
                intent2.putExtra("password", password);
                startActivity(intent2);
                break;

            case R.id.nav_logout:
                Preferences.clearData(this);
                switchActivities5();
                break;
        }
        return true;
    }

    @Override
    protected void onStart() {
        super.onStart();

        if (Preferences.getDataLogin(this)) {
            if (Preferences.getDataAs(this).equals("user")) {
            } else {
                startActivity(new Intent(this, Login.class));
                finish();
            }
        } else {
            startActivity(new Intent(this, Login.class));
            finish();
        }
    }
}