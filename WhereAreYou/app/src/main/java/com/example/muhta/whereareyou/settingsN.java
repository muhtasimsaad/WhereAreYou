package com.example.muhta.whereareyou;

import android.app.Dialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class settingsN extends AppCompatActivity {
    static private DatabaseReference mDatabase;
    static final private FirebaseAuth auth=FirebaseAuth.getInstance();
    Context context;
    int selected=-1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings_n);

        mDatabase= FirebaseDatabase.getInstance().getReference();
        context=this;
       final  EditText name=(EditText)findViewById(R.id.currentName);
//        mDatabase.child("users").child(auth.getCurrentUser().getUid()).child("username").
//                addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                name.setText(dataSnapshot.getValue()+"");
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//            }
//        });
        mDatabase.child("users").child(auth.getCurrentUser().getUid()).child("image").
                addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        MapsActivity.main.image=Integer.parseInt(dataSnapshot.getValue()+"");
                        selected= MapsActivity.main.image;
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

        Button changePic=(Button)findViewById(R.id.changePicture);
         changePic.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 final Dialog dialog = new Dialog(context);
                 dialog.setContentView(R.layout.imageselector);
                 dialog.setTitle("Change Password");
                 Button save=(Button)dialog.findViewById(R.id.send);
                 final ImageView iv1=(dialog).findViewById(R.id.im1);
                 final ImageView iv2=(dialog).findViewById(R.id.im2);
                 final ImageView iv3=(dialog).findViewById(R.id.im3);
                 final ImageView iv4=(dialog).findViewById(R.id.im4);
                 final ImageView iv5=(dialog).findViewById(R.id.im5);
                 final ImageView iv6=(dialog).findViewById(R.id.im6);

                 final ImageView[] arr={iv1,iv2,iv3,iv4,iv5,iv6};

                 if(selected!=-1){arr[selected].setImageAlpha(128);}

                 arr[0].setOnClickListener(new View.OnClickListener() {
                     @Override
                     public void onClick(View v) {

                         arr[0].setImageAlpha(128);
                         arr[1].setImageAlpha(255);
                         arr[2].setImageAlpha(255);
                         arr[3].setImageAlpha(255);
                         arr[4].setImageAlpha(255);
                         arr[5].setImageAlpha(255);
                        selected=0;
                     }
                 });

                     arr[1].setOnClickListener(new View.OnClickListener() {
                         @Override
                         public void onClick(View v) {
                             arr[0].setImageAlpha(255);
                             arr[1].setImageAlpha(128);
                             arr[2].setImageAlpha(255);
                             arr[3].setImageAlpha(255);
                             arr[4].setImageAlpha(255);
                             arr[5].setImageAlpha(255);
                             selected=1;
                         }
                     });
                 arr[2].setOnClickListener(new View.OnClickListener() {
                     @Override
                     public void onClick(View v) {
                         arr[0].setImageAlpha(255);
                         arr[1].setImageAlpha(255);
                         arr[2].setImageAlpha(128);
                         arr[3].setImageAlpha(255);
                         arr[4].setImageAlpha(255);
                         arr[5].setImageAlpha(255);
                         selected=2;
                     }
                 });
                 arr[3].setOnClickListener(new View.OnClickListener() {
                     @Override
                     public void onClick(View v) {
                         arr[0].setImageAlpha(255);
                         arr[1].setImageAlpha(255);
                         arr[2].setImageAlpha(255);
                         arr[3].setImageAlpha(128);
                         arr[4].setImageAlpha(255);
                         arr[5].setImageAlpha(255);
                         selected=3;

                     }
                 });
                 arr[4].setOnClickListener(new View.OnClickListener() {
                     @Override
                     public void onClick(View v) {
                         arr[0].setImageAlpha(255);
                         arr[1].setImageAlpha(255);
                         arr[2].setImageAlpha(255);
                         arr[3].setImageAlpha(255);
                         arr[4].setImageAlpha(128);
                         arr[5].setImageAlpha(255);
                         selected=4;
                     }
                 });
                 arr[5].setOnClickListener(new View.OnClickListener() {
                     @Override
                     public void onClick(View v) {
                         arr[0].setImageAlpha(255);
                         arr[1].setImageAlpha(255);
                         arr[2].setImageAlpha(255);
                         arr[3].setImageAlpha(255);
                         arr[4].setImageAlpha(255);
                         arr[5].setImageAlpha(128);
                         selected=5;
                     }
                 });






                 save.setOnClickListener(new View.OnClickListener() {
                     @Override
                     public void onClick(View v) {

                         mDatabase.child("users").child(auth.getCurrentUser().getUid()).child("image")
                                 .setValue(selected+"");
                         dialog.dismiss();
                     }
                 });

                 name.addTextChangedListener(new TextWatcher() {
                     @Override
                     public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                         mDatabase.child("users").child(auth.getCurrentUser().getUid()).child("username")
                                 .setValue(name.getText()+"");
                     }

                     @Override
                     public void onTextChanged(CharSequence s, int start, int before, int count) {

                     }

                     @Override
                     public void afterTextChanged(Editable s) {

                     }
                 });


                 dialog.show();
             }
         });
    }



}
