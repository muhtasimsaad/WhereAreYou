package com.example.muhta.whereareyou;

/**
 * Created by muhta on 8/1/2017.
 */

import android.app.Activity;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.concurrent.ExecutionException;


public class CustomList extends ArrayAdapter<String>{

    private  Activity context;
    private  String[] uid;
    private  String[] names;
    private  Long[] time;
    private  int[] imageId;
    private  int[] flag;
    public CustomList(Activity context,
                      String[] web,Long[] web2, int[] imageId,String[] uid,int[] flag) {
        super(context, R.layout.list_single, web);
        this.context = context;
        this.names = web;
        this.uid=uid;
        this.time=web2;
        this.imageId = imageId;
        this.flag=flag;
    }
    public CustomList(Activity context,
                      String[] web,Long[] web2, int[] imageId) {
        super(context, R.layout.list_single, web);
        this.context = context;
        this.names = web;
        this.time=web2;
        this.imageId = imageId;


    }
    @Override
    public View getView(final int position, View view, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View rowView;
        if(!names[position].equals("REQUESTS")){
            rowView= inflater.inflate(R.layout.list_single, null, true);}
            else {
            rowView=inflater.inflate(R.layout.requests, null, true);
            return  rowView;
        }
        TextView txtTitle = (TextView) rowView.findViewById(R.id.textViewName);
        TextView textTime=(TextView)rowView.findViewById(R.id.textViewTime);
        ImageView imageView = (ImageView) rowView.findViewById(R.id.imageView);
        ImageView imageView2 = (ImageView) rowView.findViewById(R.id.imageView2);
        ImageView imageView3 = (ImageView) rowView.findViewById(R.id.imageView3);
        imageView3.setVisibility(View.INVISIBLE);
        imageView2.setVisibility(View.INVISIBLE);

        if(imageId[position]==0){imageView.setImageResource(R.drawable.cancelinvert);}
        if(imageId[position]==1){imageView.setImageResource(R.drawable.ironamn);}
        if(imageId[position]==2){imageView.setImageResource(R.drawable.logout);}
        if(imageId[position]==3){imageView.setImageResource(R.drawable.accept);}
        if(imageId[position]==4){imageView.setImageResource(R.drawable.request);}
        if(imageId[position]==5){imageView.setImageResource(R.drawable.acceptinvert);}

        try{
            if (flag[position]==0){
                imageView2.setVisibility(View.VISIBLE);
                imageView2.setImageResource(R.drawable.request);
                imageView3.setVisibility(View.INVISIBLE);
                imageView2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Log.v("ClickDebugging","Requested ->"+position);
                try {
                    CallAPI l=new CallAPI(context,context,"");
                    Object result = l.execute(
                            "https://us-central1-where-are-you-aa10d.cloudfunctions.net/sendRequst",
                            FirebaseAuth.getInstance().getCurrentUser().getUid()+"_"+uid[position]
                            , "search").get();



                            Toast.makeText(context,result.toString(),Toast.LENGTH_LONG).show();


                }   catch (Exception e) {
                   Log.v("debugging",e.getMessage()+"");
                }


            }
        });



        }
        if(flag[position]==1) {
            //have to introduce delete button
            imageView2.setVisibility(View.INVISIBLE);
            imageView3.setVisibility(View.INVISIBLE);



            }


        if(flag[position]==2){
            //Friends
            //imageView2.setImageResource(R.drawable.ironamn);
            imageView2.setVisibility(View.VISIBLE);
            imageView3.setVisibility(View.INVISIBLE);
            imageView2.setImageResource(R.drawable.remove);
            imageView2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.v("ClickDebugging","Deleted ->"+position);






                }
            });

            imageView3.setVisibility(View.INVISIBLE);
             }
        if(flag[position]==3) {
            //Requests
            imageView2.setVisibility(View.VISIBLE);
            imageView3.setVisibility(View.VISIBLE);
            //imageView2.setImageResource(R.drawable.ironamn);
            imageView2.setImageResource(R.drawable.accept);
            imageView3.setImageResource(R.drawable.cancel);



            imageView2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.v("ClickDebugging","Accepted ->"+position);

                    final DatabaseReference mData=FirebaseDatabase.getInstance().getReference()
                            .child("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                            .child("requests");
                    mData.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                            String s=(dataSnapshot.getValue().toString().replace(uid[position],""));
                            s=s.replace("__","_");
                            mData.setValue(s);
                            Toast.makeText(context,"Request Removed",Toast.LENGTH_LONG).show();
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });

                    final DatabaseReference mData2=FirebaseDatabase.getInstance().getReference()
                            .child("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                            .child("friendList");
                    mData2.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                            mData2.setValue(dataSnapshot.getValue().toString()+uid[position]+"_");

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });

                    final DatabaseReference mData3=FirebaseDatabase.getInstance().getReference()
                            .child("users").child(uid[position])
                            .child("friendList");
                    mData3.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                            mData3.setValue(dataSnapshot.getValue().toString()+
                                    FirebaseAuth.getInstance().getCurrentUser().getUid()+"_");
                            Toast.makeText(context,"Request Approved",Toast.LENGTH_LONG).show();
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });



                }
            });

            imageView3.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.v("ClickDebugging","Rejected ->"+position);

                    final DatabaseReference mData=FirebaseDatabase.getInstance().getReference()
                            .child("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                            .child("requests");
                    mData.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                            mData.setValue(dataSnapshot.getValue().toString().replace(uid[position],""));
                            Toast.makeText(context,"Request Removed",Toast.LENGTH_LONG).show();
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });



                }
            });
        }
        }
        catch (Exception r) {
            //imageView2.setVisibility(View.INVISIBLE);
        Log.v("debugging",r.getMessage());}
        txtTitle.setText(names[position]);
        String s="";
        Long temp=(System.currentTimeMillis()/60000)-time[position];
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



        textTime.setText(s);
        //imageView.setImageBitmap(imageId[position]);




        return rowView;
    }
}