package com.example.muhta.whereareyou;


import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Environment;
import android.provider.Settings;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.content.ContextCompat;
import android.database.sqlite.SQLiteDatabase;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;

import org.w3c.dom.Text;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;


public class MapsActivity extends FragmentActivity implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {
    // public static String[] broadcastReceivers;

  static   Queue <String> que;

    Long lastUpdated=((System.currentTimeMillis()/1000)-1000);

    boolean addMark=false;
    static boolean markerShowable=true;
    GPSTracker gps;
    Context context;
    ListView lv;
    boolean changeCamera=true;
    SQLiteDatabase db;
    boolean clickable=false;
    Button search;
    Button mark;
    Boolean debug=false;
    Button accounts;
    Button logout;
    Button settings;
    LinearLayout layout;
    FirebaseAuth auth;
    static boolean dataReady=false;
    private GoogleMap mMap;
    GoogleApiClient mGoogleApiClient;
    Location mLastLocation;
    static skeleton main;
    Marker mCurrLocationMarker;
    static boolean markerReady=false;
    static ArrayList<String> arrayList=new ArrayList<String>();

    LocationRequest mLocationRequest;
    static private DatabaseReference mDatabase;
    int markerSelector=-1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        if(!isInternetConnected()){
            showInternetDialog();


            Intent i=new Intent(MapsActivity.this,noInternet.class);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(i);

            return;


        }


        main=new skeleton();

        Log.v("ajob","ashse");
        mDatabase = FirebaseDatabase.getInstance().getReference();
        context=MapsActivity.this;


        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            checkLocationPermission();

        }

       initializeQue();





        final FirebaseAuth mAuth= FirebaseAuth.getInstance();
        final FirebaseUser currentUser = mAuth.getCurrentUser();
        try {
            currentUser.getUid();

            try{
            mDatabase.child("users").child(currentUser.getUid()).child("username").
                    addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {


                           try {

                               // Log.v("testing33",dataSnapshot.getValue().toString());
                               if (dataSnapshot.getValue().toString().length() < 4
                                       || dataSnapshot.getValue().toString().equals("null")) {

                                   if(tutorial.i!=3){
                                       Intent ii=new Intent(MapsActivity.this,tutorial.class);
                                       ii.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                       startActivity(ii);

                                   }

                                   final Dialog dialog = new Dialog(context);
                                   dialog.setContentView(R.layout.dialoglayout);
                                   dialog.setTitle("Change Password");
                                   dialog.setCancelable(false);
                                   Button save = (Button) dialog.findViewById(R.id.send);
                                   final EditText ed1 = (EditText) dialog.findViewById(R.id.ed1);
                                   final TextView tv2 = (TextView) dialog.findViewById(R.id.tv1);

                                   save.setOnClickListener(new View.OnClickListener() {
                                       @Override
                                       public void onClick(View v) {

                                           if (ed1.getText().toString().length() < 4) {

                                               tv2.setText("Username must be at least 4 characters");
                                           } else {

                                               if (ed1.getText().toString().equals("null")) {
                                                   return;
                                               }

                                               mDatabase.child("users").child(currentUser.getUid()).child("username").
                                                       setValue(ed1.getText() + "");
                                           }
                                           dialog.dismiss();
                                       }
                                   });

                                   dialog.show();
                               } else {
                                   main.username = dataSnapshot.getValue() + "";
                               }


                           }
                           catch (Exception r){

                               final Dialog dialog = new Dialog(context);
                               dialog.setContentView(R.layout.dialoglayout);
                               dialog.setTitle("Change Password");
                               dialog.setCancelable(false);
                               Button save = (Button) dialog.findViewById(R.id.send);
                               final EditText ed1 = (EditText) dialog.findViewById(R.id.ed1);
                               final TextView tv2 = (TextView) dialog.findViewById(R.id.tv1);

                               save.setOnClickListener(new View.OnClickListener() {
                                   @Override
                                   public void onClick(View v) {

                                       if (ed1.getText().toString().length() < 4) {

                                           tv2.setText("Username must be at least 4 characters");
                                       } else {

                                           if (ed1.getText().toString().equals("null")) {
                                               return;
                                           }

                                           mDatabase.child("users").child(currentUser.getUid()).child("username").
                                                   setValue(ed1.getText() + "");
                                       }
                                       dialog.dismiss();
                                   }
                               });

                               dialog.show();

                           }



                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });

        }
        catch (Exception r){}
        }
        catch (NullPointerException rr){
            Intent i=new Intent(MapsActivity.this,LoginActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(i);}



        try{auth = FirebaseAuth.getInstance();
            FirebaseUser user = auth.getCurrentUser();
            if (user == null) {

                Intent i = new Intent(MapsActivity.this, LoginActivity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(i);

            }
        }
        catch (Exception rr){

            Intent i = new Intent(MapsActivity.this, LoginActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(i);
        }




        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
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
        mMap = googleMap;
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

        Log.v("backTesting","ashse");


//        CallAPI l = new CallAPI(context, context, "--");
//        l.execute("updateLocation",  "test", "test2");
        try {
            openNotificationPortal();
            downloadSettings();
            downloadFriendList();
            downloadMarkers();}
            catch (Exception r )
            {

                Intent intent=new Intent(MapsActivity.this,MapsActivity.class);
                startActivity(intent);


            }


            downloadMarkers();


            mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                @Override
                public boolean onMarkerClick(final Marker marker) {





                    final Dialog dialog = new Dialog(context);
                    dialog.setContentView(R.layout.sure);

                    Button yes=(Button)dialog.findViewById(R.id.Yes);
                    Button No=(Button)dialog.findViewById(R.id.No);

                    No.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                        }
                    });

                    yes.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            marker.remove();

                            for(int i=0;i<main.markers.size();i++) {

                             Log.v("markerTesting00",main.markers.get(i).name);

                            }

                            for(int i=0;i<main.markers.size();i++) {

                                Log.v("markerTesting00","comparing : "+
                                        marker.getTitle()+"  VS  "+main.markers.get(i).name);

                            if(main.markers.get(i).name.equals(marker.getTitle()))
                            {
                                main.markers.remove(i);

                                Log.v("markerTesting00","removed "+i);
                            }

                            }
//
//
//
//
//

                            mDatabase.getDatabase().getReference().child("users").
                                    child(FirebaseAuth.getInstance().
                                            getCurrentUser().getUid()).child("markers").
                                    child(marker.getTitle()+"").removeValue();
//


                            dialog.dismiss();


                        }
                    });




                    dialog.show();


                   return true;
                }
            });
            checkLocationPermission();
            initializeGPS();
            initializeButtonFunctions();
            turnListOff();






    }
    private void showMarkers(){

    Log.v("markerTesting00","inside Show");
        Log.v("markerTesting00","Size : "+main.markers.size());

        markerShowable=false;

       try{
           for(int i=0;i<main.markers.size();i++) {


            mMap.addMarker(new MarkerOptions().position(main.markers.get(i).latlon)
                   .title(main.markers.get(i).name)
                   .icon(bitmapDescriptorFromVector(this,
                           getMarkerImage(main.markers.get(i).image))));


       }



        }catch (NullPointerException r){}



    }
    private void updateNotification(){


        if(!markerReady){ return;}

        mDatabase.child("users").child(auth.getCurrentUser().getUid()).child("notifications").
               setValue(checkForNotifications());
    }
    private int getMarkerImage(int i) {
        Log.v("asdasd",""+i);
        if(i==0){return R.drawable.home;}
        if(i==1){return (R.drawable.work);}
        if(i==2){return (R.drawable.sport);}

        return R.drawable.cancel;

    }
    private void downloadMarkers() {
        CallAPI l = new CallAPI(context);
        l.execute("https://us-central1-where-are-you-aa10d.cloudfunctions.net/downloadMarkers",  auth.getCurrentUser().getUid(), "downloadMarkers");
    }
    private void downloadFriendList() {



        mDatabase.child("users").child(auth.getCurrentUser().getUid()).
                child("friendList").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.v("testingFriends","string found : "+dataSnapshot.toString());
        try {
            String[] tt = dataSnapshot.getValue().toString().split("_");


            MapsActivity.main.frndList.clear();

            for (int i = 0; i < tt.length; i++) {
                Log.v("testingFriends", "string found Inside: " + tt[i]);
                if (tt[i].length() < 12) {
                    continue;
                }
                Friend f = new Friend(tt[i]);
                MapsActivity.main.frndList.add(f);
                Log.v("testingFriends", "Friend created in API" + tt[i]);
            }

            MapsActivity.createFriendsLocationListener();
        }catch (Exception r){

            Intent intent=new Intent(MapsActivity.this,MapsActivity.class);
            startActivity(intent);

        }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });





    }
    static void createFriendsLocationListener() {


        Log.v("phase3", "ashse");


        for(int i=0;i<main.frndList.size();i++) {
            Log.v("phase3", "size ->"+main.frndList.size());


            // download other details
            mDatabase.child("users").child(main.frndList.get(i).UID).
                    addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                   // Log.v("testing","temp ->"+dataSnapshot.getKey()+"");
                    try {
                        for (int i = 0; i < main.frndList.size(); i++) {
                            if (main.frndList.get(i).UID.equals(dataSnapshot.getKey() + "")) {
                                Log.v("phase3", "username -> "+dataSnapshot.child("username").getValue()+"");
                                main.frndList.get(i).name = dataSnapshot.child("username").getValue()+"";
                                main.frndList.get(i).image= Integer.parseInt(dataSnapshot.child("image").getValue()+"");
                                Log.v("check22",main.frndList.get(i).image+"");
                            }
                        }
                    }
                    catch (Exception r){Log.v("testing","Error -> "+r.getMessage());}
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });




            //download and open location portal
            Log.v("phase3", "UID -->"+ main.frndList.get(i).UID);
            mDatabase.child("users").child(main.frndList.get(i).UID).child("location").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    Log.v("phase3", "Data -> " + dataSnapshot.toString());
                    Log.v("phase3", "Data -> " + main.frndList.size());
                     for (int i=0;i<main.frndList.size();i++) {
                        if(main.frndList.get(i).UID.equals(dataSnapshot.child("uid").getValue()+"")) {
                            Log.v("testing000","Lat  -->"+dataSnapshot.child("lat").getValue()+"");double lat = Double.parseDouble(dataSnapshot.child("lat").getValue() + "");
                            double lon = Double.parseDouble(dataSnapshot.child("lon").getValue() + "");
                            long time = Long.parseLong(dataSnapshot.child("time").getValue() + "");
                            main.frndList.get(i).latlon = new LatLng(lat, lon);
                            main.frndList.get(i).time=time;
                            Log.v("phase3","lat: "+lat+"---"+"Lon: "+lon);
                            Log.v("phase3","success");
                            if(i==(main.frndList.size()-1)){dataReady=true;}
                        }
                    }



                }
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
    }
    private void downloadSettings() {

        CallAPI l = new CallAPI(context);
        l.execute("https://us-central1-where-are-you-aa10d.cloudfunctions.net/downloadSettings",
                auth.getUid(), "downloadSettings");



    }
    private void initializeButtonFunctions(){




        //q=new LinkedList<>();
        //n=new LinkedList<>();
         lv = (ListView) findViewById(R.id.listView);
        search = (Button) findViewById(R.id.search_buttom);
        mark = (Button) findViewById(R.id.mark);
        accounts = (Button) findViewById(R.id.accounts);
        logout = (Button) findViewById(R.id.logout);
        settings=(Button) findViewById(R.id.settings);
        layout=(LinearLayout)findViewById(R.id.containerLayout);

        layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                turnOpacityOn();
            }
        });

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(!clickable){turnOpacityOn();return;}
                auth.signOut();
                Intent i = new Intent(MapsActivity.this, LoginActivity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(i);

            }
        });

        settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                  if(!clickable){turnOpacityOn();return;}
                Intent i=new Intent(MapsActivity.this, settingsN.class);
                //i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(i);
            }
        });

        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!clickable){turnOpacityOn();return;}
                turnListOn();


            }
        });

        mark.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!clickable){turnOpacityOn();return;}


                Toast.makeText(getApplicationContext(),
                        "Click on the map to add a marker",Toast.LENGTH_LONG).show();

                turnListOff();
                clickable=false;

                addMark=true;

            }
        });

        accounts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                  if(!clickable){turnOpacityOn();return;}
                Intent i=new Intent(MapsActivity.this,Friends.class);
                startActivity(i);

            }
        });


        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {

                clickable=false;
                turnListOff();
               if(addMark){
                   addMarker(latLng.latitude,latLng.longitude);
                   addMark=false;
               }
            }
        });

      }
    private void initializeGPS(){

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                buildGoogleApiClient();
                mMap.setMyLocationEnabled(true);

                gps=new GPSTracker(MapsActivity.this,mMap);

                if(gps.isGPSEnabled){
                    //
                    LatLng latLng = new LatLng(gps.getLatitude(),gps.getLongitude());
                    CameraUpdate location2 = CameraUpdateFactory.newLatLngZoom(
                            latLng, 15);
                    mMap.animateCamera(location2);
                    Log.d("----","----"+gps.getLatitude()+"");
                }
                else{
                    gps.showSettingsAlert();
                    Log.d("maps","ashse3");
                }

            }
        }
        else {


            buildGoogleApiClient();
            mMap.setMyLocationEnabled(true);
            gps=new GPSTracker(MapsActivity.this,mMap);
            if(gps.isGPSEnabled){
                //
                LatLng latLng = new LatLng(gps.getLatitude(),gps.getLongitude());
                CameraUpdate location2 = CameraUpdateFactory.newLatLngZoom(
                        latLng, 15);
                mMap.animateCamera(location2);
            }
            else{
                gps.showSettingsAlert();
            }

        }

    }
    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnected(Bundle bundle) {

        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(1000);
        mLocationRequest.setFastestInterval(1000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        }

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onLocationChanged(Location location) {

        mLastLocation = location;
        if (mCurrLocationMarker != null) {
            mCurrLocationMarker.remove();

        }
        if(changeCamera) {
            CameraUpdate location2 = CameraUpdateFactory.newLatLngZoom(
                    new LatLng(location.getLatitude(), location.getLongitude()), 15);
            mMap.animateCamera(location2);
        changeCamera=false;
        }






        Log.v("testingTiming","LocationSharing :"+main.shareLocations);
            if(main.shareLocations==1) {
                Long l=(System.currentTimeMillis()/1000)-lastUpdated;
                Log.v("testingTiming","Difference :"+l);
            if (l>=main.uploadFrequency) {
                    lastUpdated = System.currentTimeMillis() / 1000;
                Log.v("testingTiming","updated :"+lastUpdated);
                    mDatabase.child("users").child(auth.getCurrentUser().getUid()).child("location").
                            child("lat").setValue("" + location.getLatitude());
                    mDatabase.child("users").child(auth.getCurrentUser().getUid()).child("location").
                            child("lon").setValue("" + location.getLongitude());
                    mDatabase.child("users").child(auth.getCurrentUser().getUid()).child("location").
                            child("time").setValue("" + (System.currentTimeMillis() / 60000));
                }

            }



        if(dataReady)refreshMap();

        if(markerReady && markerShowable){
            showMarkers();
            updateNotification();
        }

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;

    @Override
    public void onRequestPermissionsResult(int requestCode,String permissions[], int[] grantResults) {

        switch (requestCode) {

            case MY_PERMISSIONS_REQUEST_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0) {

                    // permission was granted. Do the
                    // contacts-related task you need to do.
                    if (ContextCompat.checkSelfPermission(this,
                            Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {

                        if (mGoogleApiClient == null) {
                            buildGoogleApiClient();
                        }
                        gps=new GPSTracker(MapsActivity.this,mMap);
                        if(gps.canGetLocation){
                            //
                            LatLng latLng = new LatLng(gps.getLatitude(),gps.getLongitude());
                            CameraUpdate location2 = CameraUpdateFactory.newLatLngZoom(
                                    latLng, 15);
                            mMap.animateCamera(location2);
                            if(debug)Toast.makeText(getApplicationContext(),"Can get location --"+gps.getLongitude(),Toast.LENGTH_LONG).show();
                        }else{
                            if(debug)Toast.makeText(getApplicationContext(),"Cant get location",Toast.LENGTH_LONG).show();
                        }
                        mMap.setMyLocationEnabled(true);
                    }
                    if (ContextCompat.checkSelfPermission(this,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE)
                            == PackageManager.PERMISSION_GRANTED){

                    }

                } else {

                    // Permission denied, Disable the functionality that depends on this permission.
                    if(debug)Toast.makeText(this, "permission denied", Toast.LENGTH_LONG).show();
                }
                return;
            }

            // other 'case' lines to check for other permissions this app might request.
            // You can add here other case statements according to your requirement.
        }
    }
    private void turnOpacityOn(){

        search.getBackground().setAlpha(255);
        mark.getBackground().setAlpha(255);
        accounts.getBackground().setAlpha(255);
        settings.getBackground().setAlpha(255);
        logout.getBackground().setAlpha(255);
        layout.getBackground().setAlpha(1);

        clickable=true;

    }
    private void downloadImage(String s) throws IOException {

        final File localFile = File.createTempFile("images", "jpg");

        if(debug)Toast.makeText(getApplicationContext(),"Downloading for :"+s,Toast.LENGTH_LONG).show();

        Log.d("===","Download called");

        FirebaseStorage.getInstance().getReference().child(
                s).getFile(localFile)
                .addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                        //Log.d("===", "Looking for : "+uid);
                        Bitmap bitmap = BitmapFactory.decodeFile(localFile.getAbsolutePath());
                        //Toast.makeText(getApplicationContext(),taskSnapshot.getStorage().getName()+"",Toast.LENGTH_LONG).show();
                        Log.d("====",taskSnapshot.getStorage().getName()+"");
                        saveImage(bitmap,taskSnapshot.getStorage().getName());


                    }
                }).addOnFailureListener(new OnFailureListener() {

            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle failed download
                // ...
                //Log.d("===", "UID : "+uid+"  ERROR :"+exception.getMessage());
            }
        });
    }
    private void saveImage(Bitmap finalBitmap,String uid) {


        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED  ){

            if(debug)Toast.makeText(getApplicationContext(),"Download Queued",Toast.LENGTH_LONG).show();

            return;
        }
        else{


            if(debug)Toast.makeText(getApplicationContext(),"trying to save , -->"+uid,Toast.LENGTH_LONG).show();
            String root = Environment.getExternalStorageDirectory().toString();
            File myDir = new File(root + "/WhereAreYou");
            myDir.mkdirs();
            String fname = "Image-"+ uid +".jpg";

            File file = new File (myDir, fname);
            if (file.exists ()) file.delete ();
            try {

                FileOutputStream out = new FileOutputStream(file);
                finalBitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
                out.flush();
                out.close();

                db.execSQL("UPDATE datas set data='"+System.currentTimeMillis()+"'" +
                        " where id='"+FirebaseAuth.getInstance().getCurrentUser().getUid()+"' AND variable='picUploadTime';");
                Log.d("===","UploadTime updated"+ System.currentTimeMillis());

                if(debug)Toast.makeText(getApplicationContext(),"Image saved",Toast.LENGTH_LONG).show();

            } catch (Exception e) {

                Log.d("saad",e.getMessage());
                Toast.makeText(getApplicationContext(),"saving error :"+e.getMessage(),Toast.LENGTH_LONG).show();
            }
        }
    }
    private void turnListOff() {
        logout.setVisibility(View.VISIBLE);
        search.setVisibility(View.VISIBLE);
        mark.setVisibility(View.VISIBLE);
        accounts.setVisibility(View.VISIBLE);
        settings.setVisibility(View.VISIBLE);
        layout.setVisibility(View.VISIBLE);


        logout.getBackground().setAlpha(45);
        search.getBackground().setAlpha(45);
        mark.getBackground().setAlpha(45);
        accounts.getBackground().setAlpha(45);
        settings.getBackground().setAlpha(45);
        layout.getBackground().setAlpha(1);
        clickable=false;
        lv.setVisibility(View.GONE);

    }
    private void turnListOn() {
        logout.setVisibility(View.GONE);
        search.setVisibility(View.GONE);
        mark.setVisibility(View.GONE);
        accounts.setVisibility(View.GONE);
        settings.setVisibility(View.GONE);
        layout.setVisibility(View.GONE);

       try { if(main.frndList.size()==0){return;}}
       catch (NullPointerException r){return;}

        lv.setVisibility(View.VISIBLE);




         if(dataReady) {
             Log.v("testingFriends","ashse");

             String[] names = new String[main.frndList.size()];
            Long[] times = new Long[main.frndList.size()];
            int[] images = new int[main.frndList.size()];
            final Double[] lat = new Double[main.frndList.size()];
            final Double[] lon = new Double[main.frndList.size()];
//
//            int counter=0;
            for (int counter = 0; counter < main.frndList.size(); counter++) {

                names[counter] = main.frndList.get(counter).name;

                times[counter] = main.frndList.get(counter).time;
                lat[counter] = main.frndList.get(counter).latlon.latitude;
                lon[counter] = main.frndList.get(counter).latlon.longitude;
                images[counter] = main.frndList.get(counter).image;

            }


            CustomList adapter = new CustomList(MapsActivity.this, names, times, images);

            lv.setAdapter(adapter);

            lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> parent, View view,
                                        int position, long id) {


                    if (lat[position] != 0 && lon[position] != 0) {
                        LatLng latLng = new LatLng(lat[position], lon[position]);
                        CameraUpdate location2 = CameraUpdateFactory.newLatLngZoom(latLng, 15);
                        mMap.animateCamera(location2);
                        turnListOff();

                    } else {
                        Toast.makeText(getApplicationContext(), "No location found", Toast.LENGTH_LONG).show();
                        turnListOff();
                    }



                }
            });
        }

    }
    private Bitmap getImage(String uid){
        Bitmap result;
//        try{
//
//            String root = Environment.getExternalStorageDirectory().toString();
//            File myDir = new File(root + "/WhereAreYou");
//            myDir.mkdirs();
//            String fname = "Image-"+ uid +".jpg";
//            File f = new File (myDir, fname);
//
//            Bitmap bmp = BitmapFactory.decodeFile(f.getAbsolutePath());
//            String s=bmp.toString();
//            bmp=getResizedBitmap(bmp,80);
//            result=bmp;
//
//        }
//        catch (Exception rr){
//
//            Log.d("===","(Default image selected) Error : "+rr.getMessage());
//            Bitmap icon = BitmapFactory.decodeResource(this.getResources(),R.drawable.profile);
//            icon=getResizedBitmap(icon,80);
//            result=icon;
//
//
//
//        }
        return  getResizedBitmap(BitmapFactory.decodeResource(this.getResources(),
                R.drawable.ironamn),150);
    }
    private void refreshMap() {


            if (debug) Toast.makeText(getApplicationContext(),
                    "Reading database to create markers", Toast.LENGTH_LONG).show();

        mMap.clear();

        LatLng[] lats = new LatLng[main.frndList.size()];
        String[] names = new String[main.frndList.size()];
        Long[] times = new Long[main.frndList.size()];
        String[] uid = new String[main.frndList.size()];


        for (int counter = 0; counter < main.frndList.size(); counter++) {

            try {


                names[counter] = main.frndList.get(counter).name;
                lats[counter] = main.frndList.get(counter).latlon;
                uid[counter] = main.frndList.get(counter).UID;
                times[counter] = main.frndList.get(counter).time;


            } catch (Exception rr) {
                if (debug)
                    Toast.makeText(getApplicationContext(), "-->" +
                            rr.getMessage(), Toast.LENGTH_LONG).show();
            }

        }








        try {
            for (int i = 0; i < lats.length; i++) {




                if (lats[i].latitude != 0 && lats[i].longitude != 0) {
                    mMap.addMarker(new MarkerOptions().position(lats[i])
                            .title(names[i])
                            .icon(bitmapDescriptorFromVector(this,
                                    getImage(main.frndList.get(i).image)))
                    .snippet(getTimeString(times[i]) + ""));
                }
            }
        }
        catch (Exception r){Log.v("testing",r.getMessage());}


    }
    private BitmapDescriptor bitmapDescriptorFromVector(Context context, @DrawableRes int vectorDrawableResourceId) {
        Drawable background = ContextCompat.getDrawable(context, R.drawable.markercover);
        background.setBounds(0, 0, background.getIntrinsicWidth(), background.getIntrinsicHeight());
        Drawable vectorDrawable = ContextCompat.getDrawable(context, vectorDrawableResourceId);
        vectorDrawable.setBounds(10, 10, vectorDrawable.getIntrinsicWidth() + 40,
                vectorDrawable.getIntrinsicHeight() + 20);
        Bitmap bitmap = Bitmap.createBitmap(background.getIntrinsicWidth(),
                background.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        background.draw(canvas);
        vectorDrawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }
    private boolean isInternetConnected() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
    private void showInternetDialog(){
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Please connect to internet")
                .setCancelable(false)
                .setPositiveButton("Connect to Wifi", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
                    }
                })
                .setNegativeButton("Connect to Data", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        startActivity(new Intent(Settings.ACTION_DATA_ROAMING_SETTINGS));
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }
    public Bitmap getResizedBitmap(Bitmap image, int maxSize) {
        int width = image.getWidth();
        int height = image.getHeight();

        float bitmapRatio = (float)width / (float) height;
        if (bitmapRatio > 1) {
            width = maxSize;
            height = (int) (width / bitmapRatio);
        } else {
            height = maxSize;
            width = (int) (height * bitmapRatio);
        }
        return Bitmap.createScaledBitmap(image, width, height, true);
    }
    public boolean checkLocationPermission(){
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED  ) {


            // No explanation needed, we can request the permission.
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.WRITE_EXTERNAL_STORAGE
                    },
                    MY_PERMISSIONS_REQUEST_LOCATION);

            return false;
        } else {
            return true;
        }
    }
    public int getImage(int i){

        if(i==0){return R.drawable.ironman;}
        if(i==1){return (R.drawable.spiderman);}
        if(i==2){return (R.drawable.captainamerica);}
        if(i==3){return (R.drawable.batman);}
        if(i==4){return (R.drawable.superman);}
        if(i==5){return (R.drawable.flash);}

        return R.drawable.cancelinvert;
    }
    private double getDistance(LatLng x,LatLng y) {

        double lat1=x.latitude;
        double lat2=y.latitude;
        double lon1=x.longitude;
        double lon2=y.longitude;

        double R = 6371; // Radius of the earth in km
        double dLat = deg2rad(lat2-lat1);  // deg2rad below
        double dLon = deg2rad(lon2-lon1);
        double a =
                Math.sin(dLat/2) * Math.sin(dLat/2) +
                        Math.cos(deg2rad(lat1)) * Math.cos(deg2rad(lat2)) *
                                Math.sin(dLon/2) * Math.sin(dLon/2)
                ;
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
        double d = R * c; // Distance in km
        return d*1000;
    }
    private double deg2rad(double deg) {
        return deg * (Math.PI/180);
    }
    private String checkForNotifications(){
      //  mDatabase.child("users").child(auth.getCurrentUser().getUid()).child("notifications").
        if(!markerReady){return "";}
        String result="";
        LatLng temp=new LatLng(mLastLocation.getLatitude(),mLastLocation.getLongitude());
        try {
            for (int i = 0; i < main.markers.size(); i++) {
                if (getDistance(temp, main.markers.get(i).latlon) < 70) {
                    result = main.username + " is at " + main.markers.get(i).name;
                    break;
                }
            }
        }catch (NullPointerException r){}



        return result;
    }
    private void getUsername(){
        final  EditText name=(EditText)findViewById(R.id.currentName);
        mDatabase.child("users").child(auth.getCurrentUser().getUid()).child("username").
                addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        main.username=dataSnapshot.getValue()+"";
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

    }
    private void openNotificationPortal(){


//        CallAPI l=new CallAPI(MapsActivity.this,MapsActivity.this,"");
//        l.execute("https://us-central1-where-are-you-aa10d.cloudfunctions.net/notificationPortal",
//                auth.getCurrentUser().getUid(),"notificationPortal");

for(int i=0;i<main.frndList.size();i++){

    mDatabase.child("users").child(main.frndList.get(i).UID).child("notifications").addValueEventListener(new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            String s= dataSnapshot.getValue().toString().split(" ")[0];
            if(arrayList.contains(s)){MapsActivity.que.add(dataSnapshot.getValue()+"");}
            arrayList.add(s);
        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {

        }
    });

}





    }
    public void showNotification(String s) {

        que.poll();

        String CHANNEL_ID = "my_channel_01";
        CharSequence name = "my_channel";
        String Description = "This is my channel";

        int NOTIFICATION_ID = 234;

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);


        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {

            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel mChannel = new NotificationChannel(CHANNEL_ID, name, importance);
            mChannel.setDescription(Description);
            mChannel.enableLights(true);
            mChannel.setLightColor(Color.RED);
            mChannel.setShowBadge(true);

            if (notificationManager != null) {

                notificationManager.createNotificationChannel(mChannel);
            }

        }


        Intent resultIntent = new Intent(this, MapsActivity.class);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addParentStack(MapsActivity.class);
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);



        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle(s)

                .setSmallIcon(R.mipmap.ic_launcher)
                .setPriority(NotificationCompat.PRIORITY_LOW)
                .setContentIntent(resultPendingIntent)
                .setAutoCancel(true)
                .setColor(getResources().getColor(android.R.color.holo_red_dark));


        if (notificationManager != null) {

            notificationManager.notify(NOTIFICATION_ID, builder.build());
        }



    }
    private void addMarker(final double lat,final double lon){

        final Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.markercustom);
        dialog.setTitle("Custom List");
        Button save=(Button)dialog.findViewById(R.id.markerSave);
        Button cancel=(Button)dialog.findViewById(R.id.markerCancel);
        final EditText ed=(EditText)dialog.findViewById(R.id.markerName);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.cancel();
            }
        });

        final ImageView iv1=(ImageView)dialog.findViewById(R.id.imageView1);
        final ImageView iv2=(ImageView)dialog.findViewById(R.id.imageView2);
        final ImageView iv3=(ImageView)dialog.findViewById(R.id.imageView3);

        markerSelector=0;
        iv1.setImageAlpha(255);
        iv2.setImageAlpha(128);
        iv3.setImageAlpha(128);


        iv1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                iv1.setImageAlpha(255);
                iv2.setImageAlpha(128);
                iv3.setImageAlpha(128);
                markerSelector=0;
            }
        });

        iv2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                iv1.setImageAlpha(128);
                iv2.setImageAlpha(255);
                iv3.setImageAlpha(128);
                markerSelector=1;
            }
        });

        iv3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                iv1.setImageAlpha(128);
                iv2.setImageAlpha(128);
                iv3.setImageAlpha(255);
                markerSelector=2;
            }
        });


        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {




                if(ed.getText().toString().length()<3){
                    Toast.makeText(getApplicationContext(),"Please insert a name"
                            ,Toast.LENGTH_LONG).show();
                    return;
                }
                String s=ed.getText()+"";
                //String s=ed.getText()+"";
                //Log.v("marker",lat+"___"+lon);
                // Log.v("marker",s);


                mDatabase.child("users").child(auth.getCurrentUser().getUid()).
                        child("markers").child(s).child("name").setValue(ed.getText()+"");

                mDatabase.child("users").child(auth.getCurrentUser().getUid()).
                        child("markers").child(s).child("lat").setValue(lat+"");

                mDatabase.child("users").child(auth.getCurrentUser().getUid()).
                        child("markers").child(s).child("lon").setValue(lon+"");

                mDatabase.child("users").child(auth.getCurrentUser().getUid()).
                        child("markers").child(s).child("image").setValue(markerSelector+"");

                mDatabase.child("users").child(auth.getCurrentUser().getUid()).
                        child("markers").child(s).child("key").setValue(s);


                marker m = new marker();
                m.name = ed.getText() + "";
                m.latlon = new LatLng(lat, lon);
                m.image = markerSelector;


                main.markers.add(m);




                Log.v("markerTesting00",main.markers.size()+"");
                mMap.addMarker(new MarkerOptions().position(new LatLng(lat,lon))
                        .title(ed.getText()+"")
                        .icon(bitmapDescriptorFromVector(context,
                                getMarkerImage(markerSelector)
                        )));



                Log.v("markerTesting","ashse");


                dialog.dismiss();


            }
        });




//
////                FirebaseDatabase.getInstance().getReference().
////                        child("Saved Places").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).
////                        child("picUploadTime");
//
        dialog.show();

    }
    private String getTimeString(Long time){
        String s="";
        Long temp=(System.currentTimeMillis()/60000)-time;
        if(temp>60){
            temp=temp/60;
            if(temp>24){
                temp=temp/24;
                if(temp>30){
                    temp=temp/30;
                    if(temp>12){}
                    else{
                        s=temp+" month(s) ";
                    }
                }
                else{
                    s=temp+" day(s) ";
                }
            }
            else {
                s=temp+" hour(s) ";
            }
        }
        else{
            s=temp+" minute(s) ";
        }
        s+="ago";
        if(s.equals("ago")){s="";}

return s;
    }
    private void initializeQue() {

        que=new Queue<String>() {
            @Override
            public boolean add(String s) {
                if(main.notifications==1) showNotification(s);

                return true;
            }

            @Override
            public boolean offer(String s) {
                return false;
            }

            @Override
            public String remove() {
                return null;
            }

            @Override
            public String poll() {
                return null;
            }

            @Override
            public String element() {
                return null;
            }

            @Override
            public String peek() {
                return null;
            }

            @Override
            public int size() {
                return 0;
            }

            @Override
            public boolean isEmpty() {
                return false;
            }

            @Override
            public boolean contains(Object o) {
                return false;
            }

            @NonNull
            @Override
            public Iterator<String> iterator() {
                return null;
            }

            @NonNull
            @Override
            public Object[] toArray() {
                return new Object[0];
            }

            @NonNull
            @Override
            public <T> T[] toArray(@NonNull T[] a) {
                return null;
            }

            @Override
            public boolean remove(Object o) {
                return false;
            }

            @Override
            public boolean containsAll(@NonNull Collection<?> c) {
                return false;
            }

            @Override
            public boolean addAll(@NonNull Collection<? extends String> c) {
                return false;
            }

            @Override
            public boolean removeAll(@NonNull Collection<?> c) {
                return false;
            }

            @Override
            public boolean retainAll(@NonNull Collection<?> c) {
                return false;
            }

            @Override
            public void clear() {

            }
        };


    }


}
