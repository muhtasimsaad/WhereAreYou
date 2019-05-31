package com.example.muhta.whereareyou;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
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
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.w3c.dom.Text;

import java.util.Collection;
import java.util.Iterator;
import java.util.Queue;

public class Friends extends AppCompatActivity {
String[] names;
Long[] times;
String[] uid;
int[] images;
static boolean restart=false;
Dialog dialogLoading;
static Queue <String> requestsQue;
static Queue <String> searchQue;
ListView lv ;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(!isInternetConnected()){



            Intent i=new Intent(Friends.this,noInternet.class);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(i);

            return;


        }
        dialogLoading= new Dialog(this);

        lv= (ListView) findViewById(R.id.ListSearch);
        setContentView(R.layout.activity_friends);
        final EditText search=(EditText)findViewById(R.id.editTextSearch);
        Button send=(Button)findViewById(R.id.buttonSearch);

        TextView rq=(TextView)findViewById(R.id.reque);
        rq.setVisibility(View.INVISIBLE);

        getRequests();
        showFriends();


        requestsQue=new Queue<String>() {
            @Override
            public boolean add(String s) {

//                try{
//
//                    if(s.toString().length()<13){return false;}
//                }
//                catch (Exception r){
//                    return false;}


                if(s.length()<15){

                    return  false;}

                TextView rqt=(TextView)findViewById(R.id.reque);
                rqt.setVisibility(View.VISIBLE);

                Log.v("requestDebug","Requests "+s.toString());
                String[] temp=s.toString().split("_");


                requestsQue.clear();

               String[] names=temp[0].split(",");
               String[] uids=temp[1].split(",");

               Long[] times=new Long[names.length];
               int[] images=new int[names.length];

               int[] flags=new int[names.length];

                String[] timesTemp=temp[2].split(",");
                String[] imageTemp=temp[3].split(",");

                for(int i=0;i<(names.length);i++){
                        times[i]=Long.parseLong(timesTemp[i]);
                        images[i]=Integer.parseInt(imageTemp[i]);
                        flags[i]=3;
                }


                TextView tv=(TextView)findViewById(R.id.reque);
                tv.setVisibility(View.VISIBLE);


                CustomList adapter = new CustomList(Friends.this, names, times, images, uids, flags);
                lv = (ListView) findViewById(R.id.ListRequest);
                lv.setAdapter(adapter);

                return false;
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



        searchQue= new Queue<String>() {
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
            public boolean add(String s) {


                endLoading();

                if(s.length()<15){
                    ListView lv = (ListView) findViewById(R.id.ListSearch);
                    lv.setAdapter(null);
                    return false;
                }

                String[] names = null;
                Long[] times = null;
                String[] uid = null;
                int[] images= null;
                int[] flags=null;


                String[] arr = (s + "").split("_");

                searchQue.clear();
                names = arr[0].split(",");
                uid = arr[2].split(",");


                times = new Long[names.length];
                flags = new int[names.length];
                images = new int[names.length];


                //time

                String[] timeTemp = arr[1].split(",");
                for (int i = 0; i < timeTemp.length; i++) {
                    times[i] = Long.parseLong(timeTemp[i]);
                }




                //image

                String[] imageTemp = arr[3].split(",");
                for (int i = 0; i < names.length; i++) {
                    images[i] = Integer.parseInt(imageTemp[i]);
                }


                //flags
                Log.v("phase2", names.length+""+uid.length+""+flags.length);


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





                CustomList adapter = new CustomList(Friends.this, names, times, images, uid, flags);
                ListView lv = (ListView) findViewById(R.id.ListSearch);
                lv.setAdapter(adapter);




                return false;
            }

            @Override
            public boolean offer(String s) {
                return false;
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
        };


    send.setOnClickListener(new View.OnClickListener() {
     @Override
         public void onClick(View v) {
            String temp=search.getText()+"";
           Log.v("searchDebug","searching -->"+temp);
           temp=temp.toLowerCase();
            if(temp.length()>2){search(temp);
            showLoading();}
            else {Toast.makeText(getApplicationContext(),
                    "Minimum Length : 2",Toast.LENGTH_LONG).show();}


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



        names=new String[MapsActivity.main.frndList.size()];
        times=new Long[MapsActivity.main.frndList.size()];
        uid=new String[MapsActivity.main.frndList.size()];
        flags=new int[MapsActivity.main.frndList.size()];
        images=new int[names.length];


        for(int i=0;i<names.length;i++) {

            names[i] = MapsActivity.main.frndList.get(i).name;
            times[i] = MapsActivity.main.frndList.get(i).time;
            uid[i] = MapsActivity.main.frndList.get(i).UID;
            images[i] = MapsActivity.main.frndList.get(i).image;
            flags[i] = 2;
            Log.v("testingDebug2", images[i] + "___" + uid[i] + "___" + flags[i]);


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


            CustomList adapter = new CustomList(Friends.this, names, times, images, uid, flags);
            lv = (ListView) findViewById(R.id.ListSearch);
            lv.setAdapter(adapter);

        }

    }

private void search (final String temp) {




       // lv.setAdapter(null);

    CallAPI  l = new CallAPI(Friends.this);

         Log.v("searchDebug","searching -_->"+temp);
        l.execute("https://us-central1-where-are-you-aa10d.cloudfunctions.net/searchDatabase", temp
                , "search");




}


private String getRequests(){

        Log.v("requestDebug","getting Reuests"+FirebaseAuth.getInstance().getCurrentUser().getUid());
        CallAPI call=new CallAPI(Friends.this);

        call.execute("https://us-central1-where-are-you-aa10d.cloudfunctions.net/downloaRequests",
                FirebaseAuth.getInstance().getCurrentUser().getUid()+"","requests");

    return "";
}
    private boolean isInternetConnected() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
    private void showLoading(){

        dialogLoading.setContentView(R.layout.showloading);
        dialogLoading.setCancelable(false);
        dialogLoading.show();
    }
    private void endLoading(){

        dialogLoading.setContentView(R.layout.showloading);
        dialogLoading.cancel();
    }
}
