package com.example.muhta.whereareyou;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.concurrent.ExecutionException;

public class Friends extends AppCompatActivity {
String[] names;
Long[] times;
String[] uid;
int[] images;
static String[] namesS;
static Long[] timesS;
static String[] uidS;
int[] imagesS;
CallAPI l;
ListView lv ;
    Dialog dialog;
    static boolean dataReady=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        lv= (ListView) findViewById(R.id.ListSearch);
        setContentView(R.layout.activity_friends);
        final EditText search=(EditText)findViewById(R.id.editTextSearch);
        Button send=(Button)findViewById(R.id.buttonSearch);
        //dataReady=false;
        dialog = new Dialog(Friends.this);

        dialog.setContentView(R.layout.loading);
        dialog.setTitle("Downloading Data");
        ProgressBar pb=(ProgressBar)dialog.findViewById(R.id.progressBar);
        pb.setProgress(20);
                dialog.show();
        getRequests();
        showFriends();
        dialog.dismiss();





    send.setOnClickListener(new View.OnClickListener() {
     @Override
         public void onClick(View v) {
            String temp=search.getText()+"";
           search(temp);

        }
    });

    search.addTextChangedListener(new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            if(search.getText().length()<1){showFriends();}
        }
    });

    }
    private void showFriends(){
        int[] flags;
        try{


        names=new String[MapsActivity.main.frndList.size()+namesS.length];
        times=new Long[MapsActivity.main.frndList.size()+namesS.length];
        uid=new String[MapsActivity.main.frndList.size()+namesS.length];
        flags=new int[MapsActivity.main.frndList.size()+namesS.length];
        images=new int[names.length];}
        catch (Exception r ){

            names=new String[MapsActivity.main.frndList.size()];
            times=new Long[MapsActivity.main.frndList.size()];
            uid=new String[MapsActivity.main.frndList.size()];
            flags=new int[MapsActivity.main.frndList.size()];
            images=new int[names.length];}




        for(int i=0;i<names.length;i++){
            if(i<MapsActivity.main.frndList.size()){
                names[i] = MapsActivity.main.frndList.get(i).name;
                times[i]=MapsActivity.main.frndList.get(i).time;
                uid[i]=MapsActivity.main.frndList.get(i).UID;
                images[i]=MapsActivity.main.frndList.get(i).image;
                flags[i]=2;
                Log.v("testingDebug2",images[i]+"___"+uid[i]+"___"+flags[i]);
                }
            else{

                   try {
                       names[i] = namesS[i - MapsActivity.main.frndList.size()];
                       //Log.v("phase2",names[i]);
                       times[i] = timesS[i - MapsActivity.main.frndList.size()];
                       uid[i] = uidS[i - MapsActivity.main.frndList.size()];
                       images[i] = imagesS[i - MapsActivity.main.frndList.size()];
                       flags[i] = 3;
                       Log.v("testingDebug3", images[i] + "<-->" + uid[i] + "<-->" + flags[i]);
                   }
                   catch (Exception r ){}
            }
        }

        //int [] flags=new int[names.length];

//        FirebaseAuth mAuth= FirebaseAuth.getInstance();
//        FirebaseUser currentUser = mAuth.getCurrentUser();

//        for (int i = 0; i <names.length; i++) {
//            images[i] = BitmapFactory.decodeResource(Friends.this.getResources(),
//                    R.drawable.ironamn);
//        }

//        Log.v("tester","Names :"+names[0]);
//        Log.v("tester","times :"+times[0]);
//        Log.v("tester","uid :"+uid[0]);




        CustomList adapter = new CustomList(Friends.this, names, times, images,uid,flags);
        lv = (ListView) findViewById(R.id.ListSearch);
        lv.setAdapter(adapter);



    }

private void search(final String temp) {


    names = null;
    times = null;
    uid = null;

    try {
        lv.setAdapter(null);
    } catch (Exception r) {
        Log.v("testfff", r.getMessage());
    }
    l = new CallAPI(Friends.this, Friends.this, "--");
    //names=null;times=null;uid=null;
    Object result = null;
    try {
        result = l.execute(
                "https://us-central1-where-are-you-aa10d.cloudfunctions.net/searchDatabase", temp
                , "search").get();

        if (result.toString().length() < 10) {
            lv.setAdapter(null);
            return;
        }


        Log.v("phase2", "In Search : Response ->" + result.toString());
    } catch (Exception r) {
        Log.v("tester", "Error :" + r.getMessage());
    }

    String[] arr = (result + "").split("_");


    names = arr[0].split(",");
    times = new Long[names.length];
    int[] flags = new int[names.length];
    images = new int[names.length];
    String[] temp2 = arr[1].split(",");
    uid = arr[2].split(",");
    Log.v("phase2", "asd");
    String[] imageTemp = arr[3].split(",");

    Log.v("phase2", namesS.length + "_");
    Log.v("phase2", timesS.length + "_");
    Log.v("phase2", uidS.length + "");

    for (int i = 0; i < names.length; i++) {
        images[i] = Integer.parseInt(imageTemp[i]);
    }


    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    FirebaseUser currentUser = mAuth.getCurrentUser();
    for (int i = 0; i < names.length; i++) {


        if (uid[i].equals(currentUser.getUid() + "")) {
            flags[i] = 1;
        }

        for (int ii = 0; ii < MapsActivity.main.frndList.size(); ii++) {

            if (uid[i].equals(MapsActivity.main.frndList.get(ii).UID)) {
                flags[i] = 1;
            }


            {
            }

        }

    }

    for (int i = 0; i < temp2.length; i++) {
        times[i] = Long.parseLong(temp2[i]);
    }


//        Log.v("tester","Names :"+names[0]);
//        Log.v("tester","times :"+times[0]);
//        Log.v("tester","uid :"+uid[0]);


    try {

        String[] namesF = new String[names.length + namesS.length];

        String[] uidF = new String[uidS.length + uid.length];
        Long[] timesF = new Long[times.length + timesS.length];
        int[] flagF = new int[times.length + timesS.length];
        int[] imageF = new int[times.length + timesS.length];

        for (int i = 0; i < namesF.length; i++) {

            if (i < names.length) {
                namesF[i] = names[i];
                timesF[i] = times[i];
                uidF[i] = uid[i];
                flagF[i] = flags[i];
                imageF[i] = images[i];
            } else {
                namesF[i] = namesS[i - names.length];
                timesF[i] = timesS[i - names.length];
                uidF[i] = uidS[i - names.length];
                flagF[i] = 3;
                imageF[i] = imagesS[i - names.length];

            }


        }


        CustomList adapter = new CustomList(Friends.this, namesF, timesF, imageF, uidF, flagF);
        lv = (ListView) findViewById(R.id.ListSearch);
        lv.setAdapter(adapter);

    }
    catch (Exception r ){

        CustomList adapter = new CustomList(Friends.this, names, times, images, uid, flags);
        lv = (ListView) findViewById(R.id.ListSearch);
        lv.setAdapter(adapter);
    }

}


private String getRequests(){


        namesS=null;uidS=null;timesS=null;



        CallAPI call=new CallAPI(Friends.this,Friends.this,"");
    try {
        Object result=call.execute("https://us-central1-where-are-you-aa10d.cloudfunctions.net/downloaRequests",
                FirebaseAuth.getInstance().getCurrentUser().getUid()+"","").get();
        if(result.toString().length()<13){return "";}
        Log.v("phase2","Requests "+result.toString());
        String[] temp=result.toString().split("_");




        String[] namesTemp=temp[0].split(",");
        namesS=new String[namesTemp.length+1];
        timesS=new Long[namesS.length];
        imagesS=new int[namesS.length];
        uidS=new String[namesS.length];


        String[] uidTemp=temp[1].split(",");
        String[] arr=temp[2].split(",");
        String[] te=temp[3].split(",");

        for(int i=0;i<(namesS.length+1);i++){
            if(i!=0){
                namesS[i]=namesTemp[i-1];
                uidS[i]=uidTemp[i-1];
                timesS[i]=Long.parseLong(arr[i-1]);
                imagesS[i]=Integer.parseInt(te[i-1]);
            }
            else {
                namesS[i]="REQUESTS";
                uidS[i]="REQUESTS";
                timesS[i]=Long.parseLong("01");
                imagesS[i]=9;
            }
        }



        Log.v("phase2","Datase sent :"+namesS[0]+"-"+uidS[0]+"-"+timesS[0]);

    } catch (Exception e) {
        Log.v("phase2","Error ->"+e.getMessage());
    }

    Log.v("phase2","Static array Length after populating :"+namesS.length+"");





    return "";
}

}
