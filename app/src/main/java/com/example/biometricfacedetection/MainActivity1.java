package com.example.biometricfacedetection;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;

public class MainActivity1 extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity1);


    }

    public void testOnClick(View view) {

            startActivity(new Intent().setClass(MainActivity1.this,RecognizerActivity.class));

        }




        @Override
        public void onBackPressed(){
          //  finish();

       //Intent intent=new Intent(this,MainActivity.class);



      // startActivity(intent);

            Intent a = new Intent(Intent.ACTION_MAIN);
            a.addCategory(Intent.CATEGORY_HOME);
            a.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(a);
        }

}
