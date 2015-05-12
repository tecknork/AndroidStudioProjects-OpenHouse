package it.neokree.example.gps;


/**
 * Created by MUS on 4/5/15.
 */
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.TextureView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import it.neokree.example.R;

public class InfoPage extends Activity {
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.info_page);

        TextView txtName = (TextView) findViewById(R.id.shopname);
        TextView txtAddress = (TextView) findViewById(R.id.shopaddress);
        TextView txtDistance= (TextView) findViewById(R.id.distance);

        Button btnToShop = (Button) findViewById(R.id.ButtonGoToShop);

        Intent i = getIntent();
        // Receiving the Data
        String name = i.getStringExtra("name");
     //   String address = i.getStringExtra("address");
     //   String distacne = i.getStringExtra("distance");
     //   Log.e("Second Screen", name + "." + address );

        // Displaying Received data
        txtName.setText("ShopName : " + name);
      //  txtAddress.setText("ShopAddress : "+ address);
      //  txtDistance.setText("Distance : " + distacne);

        // Binding Click event to Button
        btnToShop.setOnClickListener(new View.OnClickListener() {

            public void onClick(View arg0) {
                //Closing SecondScreen Activity
                finish();
            }
        });

    }
}