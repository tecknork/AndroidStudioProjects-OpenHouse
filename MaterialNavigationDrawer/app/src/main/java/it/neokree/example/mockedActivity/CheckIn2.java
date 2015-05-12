package it.neokree.example.mockedActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.checkin.Nfc;
import com.checkin.Nfc2;
import com.checkin.QrCode;
import com.checkin.QrCode2;

import it.neokree.example.R;

/**
 * Created by TecKNork on 3/26/2015.
 */
public class CheckIn2 extends Activity {
        private Button button1;
    private  Button button2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.twobuttons);

         button1 = (Button) findViewById(R.id.button);
        button2 = (Button) findViewById(R.id.button2);
        button1.setOnClickListener(new View.OnClickListener(){
            @Override
            //On click function
            public void onClick(View view) {
                //Create the intent to start another activity
                Intent intent = new Intent(view.getContext(), QrCode2.class);
                startActivity(intent);
                finish();
            }
        });



        button2.setOnClickListener(new View.OnClickListener(){
            @Override
            //On click function
            public void onClick(View view) {
                //Create the intent to start another activity
                Intent intent = new Intent(view.getContext(), Nfc2.class);
                startActivity(intent);
                finish();
            }
        });
    }

}
