package com.shoppin.splash;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.login.helper.SQLiteHandler;
import com.login.helper.SessionManager;

import it.neokree.example.light.MockedAccount;
import it.neokree.example.mockedActivity.Asilemap;
import it.neokree.example.mockedActivity.CheckIn;
import it.neokree.example.mockedFragments.Userprofile;

import it.neokree.example.MainActivity;
import it.neokree.example.R;

/**
 * Created by TecKNork on 3/21/2015.
 */
public class Splash extends Activity {
    private SessionManager session;
    private SQLiteHandler db;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash);
        Button button1 = (Button) findViewById(R.id.button1);
        session = new SessionManager(getApplicationContext());
        db=new SQLiteHandler(getApplicationContext());

        session.setLogin(false);
        db.deleteUsers();
        // Check if user is already logged in or not
        if (session.isLoggedIn()) {
            // User is already logged in. Take him to main activity
            Intent intent = new Intent(Splash.this, MainActivity.class);
            startActivity(intent);
            finish();
        }
        button1.setOnClickListener(new View.OnClickListener(){
            @Override
            //On click function
            public void onClick(View view) {
                //Create the intent to start another activity
                Intent intent = new Intent(view.getContext(), Register.class);
                startActivity(intent);
            }
        });

        Button button2 = (Button) findViewById(R.id.button2);

        button2.setOnClickListener(new View.OnClickListener(){
            @Override
            //On click function
            public void onClick(View view) {
                //Create the intent to start another activity
                Intent intent = new Intent(view.getContext(), Login.class);
                startActivity(intent);
            }
        });

        Button button3 = (Button) findViewById(R.id.button3);

        button3.setOnClickListener(new View.OnClickListener(){
            @Override
            //On click function
            public void onClick(View view) {
                //Create the intent to start another activity
                Intent intent = new Intent(view.getContext(), CheckIn.class);
                startActivity(intent);
                finish();
            }
        });

    }
}
