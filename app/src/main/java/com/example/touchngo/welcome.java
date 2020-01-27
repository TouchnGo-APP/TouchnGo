package com.example.touchngo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

public class welcome extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        new Handler().postDelayed(new Runnable() {

            public void run() {

                Intent intent = new Intent(welcome.this, MainActivity.class);
                welcome.this.startActivity(intent);

                welcome.this.finish();
            }

        }, 3000);// 5 Seconds
    };
}
