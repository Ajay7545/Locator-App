package com.digipodium.withyou;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

public class RegisterDetailActivity extends AppCompatActivity {

    EditText Name;
    EditText Email;
    EditText Phone;
    EditText Password;
    TextView ErrorMsg;
    Button b5;
    Dialog myDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_detail);
        Name = findViewById(R.id.Name);
        Email = findViewById(R.id.Email);
        Phone = findViewById(R.id.Phone);
        Password = findViewById(R.id.Password);
        ErrorMsg = findViewById(R.id.ErrorMsg);
        b5 = findViewById(R.id.b5);
        myDialog = new Dialog(this);

        b5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            FirebaseAuth fbase = FirebaseAuth.getInstance();
            String email = Email.getText().toString();
            final String password = Password.getText().toString();
            final String name = Name.getText().toString();
            String phone = Phone.getText().toString();

            if (!email.isEmpty() && email.contains("@") && email.contains(".com") && !password.isEmpty() && password.length() > 7) {

                fbase.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.getException() == null) {
                            FirebaseUser user = task.getResult().getUser();
                            UserProfileChangeRequest updateProfile = new UserProfileChangeRequest.Builder().setDisplayName(name).build();
                            user.updateProfile(updateProfile);
                            updateUI(user);
                        } else {
                            String error = task.getException().getMessage();
                            ErrorMsg.setText(error);
                            updateUI(null);
                        }
                    }
                });
            } else {
                ErrorMsg.setText("Email is not valid");
            }










            }
        });
        }




    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        updateUI(user);

    }
    private void updateUI(FirebaseUser user) {
        if (user != null) {
            TextView txtclose;
            TextView t2;
            TextView t3;

            myDialog.setContentView(R.layout.custompopup);
            txtclose =(TextView) myDialog.findViewById(R.id.txtclose);
            txtclose.setText("M");
            t2=(TextView)myDialog.findViewById(R.id.t2);
            t3=(TextView)myDialog.findViewById(R.id.t3);
            t2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent=new Intent(RegisterDetailActivity.this,CreateActivity.class);
                    startActivity(intent);
                }
            });


            t3.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent=new Intent(RegisterDetailActivity.this,JoinActivity.class);
                    startActivity(intent);
                }
            });

            txtclose.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    myDialog.dismiss();
                }
            });
            myDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            myDialog.show();

          //  finish();
        }
    }


}
