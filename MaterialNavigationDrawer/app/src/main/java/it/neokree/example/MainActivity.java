package it.neokree.example;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import it.neokree.example.light.MockedAccount;


/**
 * Created by neokree on 30/12/14.
 */

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent;
        intent = new Intent(this,MockedAccount.class);
        startActivity(intent);
        finish();
    }


}
