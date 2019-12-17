package com.digipodium.withyou;

import android.content.Intent;
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

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        final EditText e3=findViewById(R.id.e3);
        Button b4=findViewById(R.id.b4);
        final EditText e4=findViewById(R.id.e4);
        final TextView ErrorMsg=findViewById(R.id.ErrorMsg);
        b4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = e3.getText().toString();
                String password = e4.getText().toString();


                if (!email.isEmpty() && email.contains("@") && email.contains(".com") && !password.isEmpty() && password.length() > 7) {
                    FirebaseAuth fbase = FirebaseAuth.getInstance();
                    fbase.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.getException() == null) {
                                FirebaseUser user = task.getResult().getUser();
                                updateUI(user);
                            } else {
                                String error = task.getException().getMessage();
                                ErrorMsg.setText(error);
                                updateUI(null);
                            }
                        }
                    });
                }
                else
                {
                    ErrorMsg.setText("Enter email & password");
                }

            }
        });
    }

    private void updateUI(FirebaseUser user) {
        if(user!=null){
            startActivity(new Intent(LoginActivity.this,MapsActivity.class));
            finish();
        }

    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser user=FirebaseAuth.getInstance().getCurrentUser();
        updateUI(user);

    }

}
