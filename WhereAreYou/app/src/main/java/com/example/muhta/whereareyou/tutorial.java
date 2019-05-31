package com.example.muhta.whereareyou;

import android.content.Intent;
import android.media.Image;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class tutorial extends AppCompatActivity {


    static int i=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tutorial);

        final Button next=(Button)findViewById(R.id.next);
        final Button prev=(Button)findViewById(R.id.previous);
        final ImageView iv=(ImageView)findViewById(R.id.centerView);
        final ImageView tv=(ImageView)findViewById(R.id.textView3);
        Log.v("tutorialTesting",i+" in outside");
        if(i==0){prev.setClickable(false);}

            tv.setVisibility(View.INVISIBLE);

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(i==0){
                    Log.v("tutorialTesting",i+" in 0");
                    prev.setClickable(true);
                    iv.setImageResource(R.drawable.backin);
                    i++;
                    return;
                }
                if(i==1){
                    Log.v("tutorialTesting",i+" in 1");
                    iv.setImageResource(R.drawable.tutorials);
                    i++;
                    return;
                }
                if(i==2){

                    Log.v("tutorialTesting",i+" in 2");
                    iv.setImageAlpha(25);
                    tv.setVisibility(View.VISIBLE);
                    next.setText("Let's Go !!");

                    i++;
                    return;
                }


                if(i==3){
                    Log.v("tutorialTesting",i+" in 3");
                    Intent i=new Intent(tutorial.this,MapsActivity.class);
                    startActivity(i);

                }
            }
        });
        prev.setOnClickListener(new View.OnClickListener() {
            @Override
                public void onClick(View v)
            {
                if(i==0){


                }
                if(i==1){

                    prev.setClickable(false);
                    iv.setImageResource(R.drawable.starter);
                    i--;

                }
                if(i==2){

                    iv.setImageResource(R.drawable.backin);
                    i--;

                }

                if(i==3){

                    iv.setImageAlpha(255);
                    iv.setImageResource(R.drawable.tutorials);
                    tv.setVisibility(View.INVISIBLE);
                    next.setText("Next");

                    i--;



                }



                 }
             }
        );

    }
}
