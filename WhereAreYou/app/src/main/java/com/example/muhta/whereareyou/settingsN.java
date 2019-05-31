package com.example.muhta.whereareyou;

import android.app.ActionBar;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Map;

public class settingsN extends AppCompatActivity {
    static private DatabaseReference mDatabase;
    static final private FirebaseAuth auth=FirebaseAuth.getInstance();
    Context context;
    int selected=-1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings_n);

        if(!isInternetConnected()){



            Intent i=new Intent(settingsN.this,noInternet.class);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(i);

            return;


        }


        final ImageView pic=(ImageView)findViewById(R.id.profPic);
        mDatabase= FirebaseDatabase.getInstance().getReference();
        context=this;
        final EditText name=(EditText)findViewById(R.id.currentName);

        final SeekBar sb=(SeekBar)findViewById(R.id.seekBar);
        final TextView tv=(TextView)findViewById(R.id.seekbarLabel);

        final Button emailChanger=(Button)findViewById(R.id.changeEmail);
        final Button passChanger=(Button)findViewById(R.id.changePassword);


        emailChanger.setOnClickListener(new View.OnClickListener() {




            @Override
            public void onClick(View view) {



              Toast.makeText(getApplicationContext(),"Note: This is a security " +
                      "sensitive operation that requires the user to have " +
                      "recently signed in.",Toast.LENGTH_LONG).show();

                final Dialog dialog = new Dialog(context);
                dialog.setContentView(R.layout.emailchanger);
                dialog.setTitle("Change Email");
                Button save=(Button)dialog.findViewById(R.id.save);

                final TextView tv=(TextView)dialog.findViewById(R.id.Title);
                final EditText current=(EditText)dialog.findViewById(R.id.current);
                final EditText New=(EditText)dialog.findViewById(R.id.newW);
                final EditText confirmNew=(EditText)dialog.findViewById(R.id.confirmNew);

                tv.setText("Change Email");
                save.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {



                        if(!(current.getText()+"").equals(FirebaseAuth.getInstance().getCurrentUser().getEmail())){
                            Toast.makeText(getApplicationContext(),"Current email is incorrect", Toast.LENGTH_LONG).show();
                            return;
                        }
                        else {

                            if(!(New.getText()+"").equals(confirmNew.getText()+"")){
                                Toast.makeText(getApplicationContext(),"New emails do not match",Toast.LENGTH_LONG).show();
                                return;
                            }

                            else{

                                FirebaseAuth.getInstance().getCurrentUser().
                                        updateEmail(New.getText()+"").addOnCompleteListener(
                                        new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if(task.isSuccessful()){
                                            Toast.makeText(getApplicationContext(),"Email updated",Toast.LENGTH_LONG).show();

                                            mDatabase.child("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).
                                                    child("email").setValue(New.getText()+"");




                                            dialog.dismiss();
                                        }
                                        else{
                                            Toast.makeText(getApplicationContext(),"Please Retry",Toast.LENGTH_LONG).show();
                                        }
                                    }
                                });

                            }

                        }





                    }
                });



                dialog.show();
            }
        });


        Log.v("testingDownload",""+ MapsActivity.main.notifications);

        final Switch notifications=(Switch)findViewById(R.id.switchN);
        if(MapsActivity.main.notifications==0){notifications.setChecked(false);}
        else {notifications.setChecked(true);}


        notifications.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if(isChecked){

                    mDatabase.child("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                            .child("settings").child("notifications").setValue(1+"");

                }
                else{
                    mDatabase.child("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                            .child("settings").child("notifications").setValue(0+"");
                }

            }
        });




        passChanger.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                Toast.makeText(getApplicationContext(),"Note: This is a security " +
                        "sensitive operation that requires the user to have " +
                        "recently signed in.",Toast.LENGTH_LONG).show();


                final Dialog dialog = new Dialog(context);
                dialog.setContentView(R.layout.emailchanger);
                dialog.setTitle("Change Password");


                Button save=(Button)dialog.findViewById(R.id.save);
                final EditText current=(EditText)dialog.findViewById(R.id.current);
                final EditText New=(EditText)dialog.findViewById(R.id.newW);
                final EditText confirmNew=(EditText)dialog.findViewById(R.id.confirmNew);







                save.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        AuthCredential credential = EmailAuthProvider.getCredential(FirebaseAuth
                                .getInstance().getCurrentUser().getEmail()+"",
                                current.getText()+"");

                        FirebaseAuth.getInstance().getCurrentUser()
                                .reauthenticate(credential).
                                addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {

                                if(!task.isSuccessful()){
                                    Toast.makeText(getApplicationContext(),"Current password does not match",Toast.LENGTH_LONG).show();
                                    return;
                                }

                                else {

                                    if (!(New.getText() + "").equals(confirmNew.getText() + "")) {
                                        Toast.makeText(getApplicationContext(), "Passwords do not match", Toast.LENGTH_LONG).show();
                                        return;
                                    }


                                    else{
                                        FirebaseAuth.getInstance().getCurrentUser()
                                                .updatePassword(New.getText() + "").addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    Toast.makeText(getApplicationContext(), "Password updated"
                                                            , Toast.LENGTH_LONG).show();
                                                    dialog.dismiss();
                                                } else {
                                                    Toast.makeText(getApplicationContext(), "Please retry", Toast.LENGTH_LONG).show();
                                                }
                                            }
                                        });

                                    }
                                }

                            }
                        });







                    }
                });

                dialog.show();
            }
        });




        final Switch sw=(Switch)findViewById(R.id.switch1);

        if(MapsActivity.main.shareLocations==1){sw.setChecked(true);}
        if(MapsActivity.main.shareLocations==0){sw.setChecked(false);}

        sw.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    mDatabase.child("users").child(auth.getCurrentUser().getUid()).
                            child("settings").child("LocationSharing").setValue(1+"");
                }
                else{
                    mDatabase.child("users").child(auth.getCurrentUser().getUid()).
                            child("settings").child("LocationSharing").setValue(0+"");
                }
            }
        });




        sb.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean fromUser) {
                if(fromUser){

                    int result=-1;

                    if(i<33){sb.setProgress(0);tv.setText("1 sec");result=1;}
                    if(i>=33 && i<=66){sb.setProgress(50);tv.setText("1 min");result=60;}
                    if(i>66){sb.setProgress(100);tv.setText("5 min");result=300;}

                    MapsActivity.main.uploadFrequency=result;



                    mDatabase.child("users").child(auth.getCurrentUser().getUid()).child("settings").
                            child("updateFrequency").setValue(result+"");


                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });


        mDatabase.child("users").child(auth.getCurrentUser().getUid()).child("username").
                addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {



                         name.setText(dataSnapshot.getValue().toString());

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });


        name.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                mDatabase.child("users").child(auth.getCurrentUser().getUid()).child("username").
                        setValue(name.getText().toString());
            }
        });


        mDatabase.child("users").child(auth.getCurrentUser().getUid()).child("image").
               addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                int i=Integer.parseInt(dataSnapshot.getValue()+"");

                pic.setImageResource(getImage(i));



            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });



        mDatabase.child("users").child(auth.getCurrentUser().getUid()).child("settings").
                child("updateFrequency").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int i=Integer.parseInt(dataSnapshot.getValue()+"");
                if(i<33){sb.setProgress(0);tv.setText("1 sec");}
                if(i>=33 && i<=66){sb.setProgress(50);tv.setText("1 min");}
                if(i>66){sb.setProgress(100);tv.setText("5 min");}
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

                int i=sb.getProgress();
                Log.v("seekbar",i+"");
                sb.setProgress(90);

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
                         pic.setImageResource(getImage(selected));
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

private int getImage(int i){
    if(i==0){return (R.drawable.ironman);}
    if(i==1){return (R.drawable.spiderman);}
    if(i==2){return (R.drawable.captainamerica);}
    if(i==3){return (R.drawable.batman);}
    if(i==4){return (R.drawable.superman);}
    if(i==5){return (R.drawable.flash);}

    return R.drawable.ironman;
}

    private boolean isInternetConnected() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

}
