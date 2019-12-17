package com.digipodium.withyou;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.security.acl.Group;

import pub.devrel.easypermissions.EasyPermissions;

public class JoinActivity extends AppCompatActivity {
    String[] permission = new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION};
    private FusedLocationProviderClient mFusedLocationClient;
    double lat, lng;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join);
        final EditText e2 = findViewById(R.id.e1);
        Button b3 = findViewById(R.id.b3);
        final TextView ErrorMsg = findViewById(R.id.ErrorMsg);


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


        FirebaseDatabase db = FirebaseDatabase.getInstance();
        final DatabaseReference dRef = db.getReference("groupcode");


        b3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String myGroup = e2.getText().toString();
                if (myGroup.isEmpty()) {
                    ErrorMsg.setError("Field cannot be empty");
                } else {
                    dRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                if (snapshot.getValue(String.class).equalsIgnoreCase(myGroup)) {
                                    addMembertoGroup(myGroup, lat, lng);
                                    break;
                                } else {
                                    Toast.makeText(JoinActivity.this, "invalid code", Toast.LENGTH_SHORT).show();
                                }
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            Toast.makeText(JoinActivity.this, "code search cancelled", Toast.LENGTH_SHORT).show();
                        }
                    });

                }


            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private void addMembertoGroup(final String group, final double lat, final double lng) {
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        final DatabaseReference gref = FirebaseDatabase.getInstance().getReference(group);
        gref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                long childrenCount = dataSnapshot.getChildrenCount();
                String memberKey = "member" + (childrenCount + 1);
                gref.child(memberKey).setValue(new FamilyGroup(user.getDisplayName(), user.getEmail(), lat, lng, group));
                Toast.makeText(JoinActivity.this, "you joined the group " + group, Toast.LENGTH_SHORT).show();
                savePref(memberKey, group);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Intent i = new Intent(JoinActivity.this, MapsActivity.class);
                        startActivity(i);
                    }
                }, 2000);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(JoinActivity.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void savePref(String memberKey, String group) {
        SharedPreferences pref = getSharedPreferences("pref", MODE_PRIVATE);
        SharedPreferences.Editor edit = pref.edit();
        edit.putString("key", memberKey);
        edit.putString("group", group);
        edit.apply();
    }
}
