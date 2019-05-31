package com.example.muhta.whereareyou;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class register extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);



         final FirebaseAuth mAuth= FirebaseAuth.getInstance();
// ...
// Initialize Firebase Auth






             final Button register = (Button) findViewById(R.id.register_button);


             register.setOnClickListener(new View.OnClickListener() {
                 @Override
                 public void onClick(View v) {


                         EditText email = (EditText) findViewById(R.id.email);
                         EditText pass = (EditText) findViewById(R.id.password);
                         mAuth.createUserWithEmailAndPassword(email.getText().toString(), pass.getText().toString())
                                 .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                     @Override
                                     public void onComplete(@NonNull Task<AuthResult> task) {
                                         if (task.isSuccessful()) {
                                             // Sign in success, update UI with the signed-in user's information
                                             //Log.d(TAG, "createUserWithEmail:success");
                                             //FirebaseUser user = mAuth.getCurrentUser();
                                             Intent i = new Intent(register.this, LoginActivity.class);
                                             i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                             Toast.makeText(register.this, "Authentication Successful.",
                                                     Toast.LENGTH_SHORT).show();
                                             startActivity(i);
                                             //updateUI(user);
                                         } else {
                                             // If sign in fails, display a message to the user.
                                             Log.d("----",task.getException().toString());
                                             Toast.makeText(register.this, "Authentication failed.",
                                                     Toast.LENGTH_SHORT).show();
                                             //updateUI(null);
                                         }

                                         // ...
                                     }
                                 });

                     }

             });
         }



}
