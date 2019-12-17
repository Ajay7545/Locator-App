package com.digipodium.withyou;

import android.Manifest;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import pub.devrel.easypermissions.EasyPermissions;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {
    String[] permission = new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION};
    private FusedLocationProviderClient mFusedLocationClient;
    double lat, lng;

    private GoogleMap mMap;

    private MarkerOptions map;
    final ArrayList<FamilyGroup> members = new ArrayList<>();
    private boolean[] isMarkerSet;
    ArrayList<String> key = new ArrayList<>();
    private LatLng location1;
    private String memberKey;
    private String group;


 //   private DrawerLayout mDrawerLayout;
   // private ActionBarDrawerToggle mtoggle;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);



        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

   /* private void getSupportActionBar() {

        mDrawerLayout = findViewById(R.id.drawer);
        mtoggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.open, R.string.close);
        mDrawerLayout.addDrawerListener(mtoggle);
        mtoggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }    */


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults);
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


    int count = 0;
    String checkKey = null;

    @Override
    public void onMapReady(GoogleMap googleMap) {

        mMap = googleMap;
        DatabaseReference myRef;
        SharedPreferences pref = getSharedPreferences("pref", MODE_PRIVATE);
        memberKey = pref.getString("key", "");
        group = pref.getString("group", "abcxyz");

        if (!group.isEmpty() && !memberKey.isEmpty()) {
            myRef = FirebaseDatabase.getInstance().getReference(group);
            myRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    int i1 = 0;
                    Log.d("datasnapshotttttt", dataSnapshot.getKey());
                    if (dataSnapshot.hasChildren()) {
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            FamilyGroup member = snapshot.getValue(FamilyGroup.class);
                            members.add(member);
                        }

                        displayMemberOnMap(members);

                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

        }


        boolean hasPermissions = EasyPermissions.hasPermissions(this, permission);
        if (hasPermissions) {
            if ((ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) && (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)) {
                return;
            }


            mFusedLocationClient = new FusedLocationProviderClient(this);
            mFusedLocationClient.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    if (location != null) {
                        lat = location.getLatitude();
                        lng = location.getLongitude();
                    }
                }
            });
            Toast.makeText(this, "permission provided", Toast.LENGTH_SHORT).show();

        } else {
            EasyPermissions.requestPermissions(this, "app permission", 28, permission);
        }


        CameraPosition googlePlex = CameraPosition.builder()
                .target(new LatLng(lat, lng))
                .zoom(16)
                .bearing(0)
                .tilt(45)
                .build();


        mMap.setMyLocationEnabled(true);

    }

    private void displayMemberOnMap(ArrayList<FamilyGroup> members) {
        for (FamilyGroup member : members) {
            double lat = member.lat;
            double lng = member.lng;
            String username = member.username;
            updateMarker(new LatLng(lat, lng), username);
        }
    }

    private void updateMarker(LatLng address, String username) {
        Marker marker = mMap.addMarker(new MarkerOptions()
                .position(address)
                .title(username)
                .icon(BitmapDescriptorFactory
                        .defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
        marker.showInfoWindow();
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(address, 17));
    }

    private void updateMyLocation() {
        final DatabaseReference gref = FirebaseDatabase.getInstance().getReference(group);
        gref.child(memberKey).child("lng").setValue(lng);
        gref.child(memberKey).child("lat").setValue(lat);
    }

    @Override
    protected void onStop() {
        super.onStop();
        updateMyLocation();
    }
}

