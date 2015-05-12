package com.login.helper;

import android.app.IntentService;
import android.content.Intent;

import com.login.helper.SQLiteHandler;
import com.login.helper.SessionManager;

import java.util.HashMap;

import it.neokree.example.light.MockedAccount;

/**
 * Created by TecKNork on 3/23/2015.
 */
public class IntentUI extends IntentService {
    public static final String REQUEST_STRING = "myRequest";
    public static final String RESPONSE_STRING = "myResponse";
    public static final String RESPONSE_MESSAGE = "myResponseMessage";

    private SQLiteHandler db;
    private SessionManager session;
    public IntentUI() {
        super("IntentUI");
    }


    @Override
    protected void onHandleIntent(Intent intent) {
      db=new SQLiteHandler(getApplicationContext());
      session=new SessionManager(getApplicationContext());

        HashMap<String, String> user = db.getUserDetails();
        String name = user.get("name");
        String email = user.get("email");

        Intent broadcastIntent = new Intent();
        broadcastIntent.setAction(MockedAccount.MyWebRequestReceiver.PROCESS_RESPONSE);
        broadcastIntent.addCategory(Intent.CATEGORY_DEFAULT);
        broadcastIntent.putExtra("name",name);
        broadcastIntent.putExtra("email", email);
        sendBroadcast(broadcastIntent);

    }
}
