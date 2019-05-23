package com.example.muhta.whereareyou;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Map;

/**
 * Created by SaaD on 02-Sep-16.
 */
public class CallAPI extends AsyncTask<String,String,String> {
    String temp="";
    String flag="";
    private Activity activity;
    private ProgressDialog dialog;
    private Context context;
    AlertDialog alertDialog;
    Context contex;
    String asd="";
    int z=0;


    public CallAPI(Context context, Context context1, String s) {
        contex=context;
    }


    @Override
    protected String doInBackground(String... params) {

        try {


            //conditions here


            String login_url=params[0];
            String user_name = params[1];
            flag= params[2];
            asd=user_name;
            URL url = new URL(login_url);
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setRequestMethod("POST");
            httpURLConnection.setDoOutput(true);

            httpURLConnection.setDoInput(true);


            OutputStream outputStream = httpURLConnection.getOutputStream();
            BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(
                    outputStream, "UTF-8"));





            //conditions here


//                String post_data = URLEncoder.encode("username", "UTF-8") + "=" + URLEncoder.encode(user_name, "UTF-8") + "&"
//                        + URLEncoder.encode("pass", "UTF-8") + "=" + URLEncoder.encode(password, "UTF-8");
            String post_data = URLEncoder.encode("uid", "UTF-8") + "=" + URLEncoder.encode(user_name, "UTF-8") ;

            bufferedWriter.write(post_data);


            bufferedWriter.flush();
            bufferedWriter.close();
            outputStream.close();
            InputStream inputStream = httpURLConnection.getInputStream();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "iso-8859-1"));
            String result = "";
            String line = "";
            while ((line = bufferedReader.readLine()) != null) {
                result += line;
                temp+=line;

            }

            bufferedReader.close();
            inputStream.close();
            httpURLConnection.disconnect();
            return result;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }



    @Override
    protected void onPreExecute() {
        super.onPreExecute();


    }

    @Override
    protected void onPostExecute(String result) {


        //conditions here

        Log.v("asd","---"+result);
        if(flag.equals("downloadSettings")){
            try {
                String[] tt = result.split("_");
                Log.v("testFF", tt[0] + "-" + tt[1] + "-" + tt[2]);


                MapsActivity.main.uploadFrequency = Integer.parseInt(tt[0]);
                MapsActivity.main.downloadFrequency = Integer.parseInt(tt[1]);
                MapsActivity.main.shareLocations = Integer.parseInt(tt[2]);
            }
            catch (Exception r){}

        }

        if(flag.equals("downloadMarkers")){



            if(result.toString().length()<5){return;}

            String[] temp=result.toString().split("_");

            String[] nameTemp=temp[0].split(",");
            String[] latTemp=temp[1].split(",");
            String[] lonTemp=temp[2].split(",");
            String[] imageTemp=temp[3].split(",");



            Marker[] arr=new Marker[nameTemp.length];

            Log.v("marker2",""+nameTemp[0]);

            try{for(int i=0;i<nameTemp.length;i++){
                arr[i]=new Marker();
                arr[i].name=nameTemp[i];
                arr[i].latlon=new LatLng(Double.parseDouble(latTemp[i]),Double.parseDouble(lonTemp[i]));
                arr[i].image=Integer.parseInt(imageTemp[i]);
            }}
            catch (Exception rr){Log.v("marker2",rr.getMessage());}


            MapsActivity.main.markers=arr;
            MapsActivity.markerReady=true;

        }


        if(flag.equals("notificationPortal")){

            final String[] arr=result.split("_");
            DatabaseReference mDatabase=FirebaseDatabase.getInstance().getReference();


          for(int i=0;i<arr.length;i++) {
              Log.v("notpor",arr[i]+"");
              mDatabase.child("users").child(arr[i]).child("notifications").addValueEventListener(new ValueEventListener() {
                  @Override
                  public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                     MapsActivity.que.add(dataSnapshot.getValue()+"");

                  }

                  @Override
                  public void onCancelled(@NonNull DatabaseError databaseError) {

                  }
              });
          }

        }

        if(flag.equals("downloadFriendList")){

            String[] tt=result.split("_");
            MapsActivity.main.frndList.clear();

            for(int i=0;i<tt.length;i++){
                Log.v("testing","string found : "+tt[i]);
                if(tt[i].length()<12){continue;}
                Friend f=new Friend(tt[i]);
                MapsActivity.main.frndList.add(f);
                Log.v("testing","Friend created in API");
            }

            MapsActivity.createFriendsLocationListener();
        }

        if(flag.equals("search")){

//            if(result.length()!=1){
//                try{
//                Log.v("downloadrrr","downloaded -->"+result);
//               String[] temp = result.split("_");
//
//
//               Friends.names = temp[0].split(",");
//               Friends.times=new Long[Friends.names.length];
//               String[] temp2 = temp[1].split(",");
//               Friends.uid=temp[2].split(",");
//                for(int i=0;i<temp2.length;i++){
//                       Friends.times[i]=Long.parseLong(temp2[i]);
//                   }
//
//                      }
//               catch (Exception r){Log.v("downloadrrr -->","in API --> "+ r.getMessage()+"");}
//                Friends.dataReady = true;
//
//             }


        }


        // prasing the value came in


    }
    @Override
    protected void onProgressUpdate(String... values) {
        //super.onProgressUpdate(values);
    }



}