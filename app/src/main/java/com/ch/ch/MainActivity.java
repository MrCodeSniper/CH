package com.ch.ch;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

    }


    public void process(View view){
        UserManager.USER_ID=2;
        startActivity(new Intent(this,OtherProcessActivity.class));
    }





}
