package com.login.helper;

import android.app.DownloadManager;
import android.app.IntentService;
import android.content.Intent;

import com.com.web.talk.HttpManager;
import com.com.web.talk.RequestPackage;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import it.neokree.example.R;
import it.neokree.example.light.MockedAccount;
import it.neokree.example.mockedActivity.Profile;
import it.neokree.example.mockedFragments.Userprofile;

/**
 * Created by TecKNork on 3/23/2015.
 */
public class ProfileUi extends IntentService {
    public static final String REQUEST_STRING = "myRequest";
    public static final String RESPONSE_STRING = "myResponse";
    public static final String RESPONSE_MESSAGE = "myResponseMessage";

    private SQLiteHandler db;
    private SessionManager session;
    public ProfileUi() {
        super("IntentUI");
    }


    @Override
    protected void onHandleIntent(Intent intent) {
      db=new SQLiteHandler(getApplicationContext());
      session=new SessionManager(getApplicationContext());

        HashMap<String, String> user = db.getUserDetails();
        boolean error=true;
        String contact=new String();
        String address=new String();
        String gender=new String();

        String apikey=user.get("uid");

        RequestPackage p=new RequestPackage();
        p.setMethod("GET");
        p.setUri(getResources().getString(R.string.localhost) + "rest3/v1/profile");
                //HashMap<String, String> user = db.getUserDetails();

                p.setApi_key(apikey);
        String content = HttpManager.getData(p);
        JSONObject jObj = null;
        try {
            jObj = new JSONObject(content);
             error = jObj.getBoolean("error");
             contact = jObj.getString("contacts");
             address = jObj.getString("address");
             gender = jObj.getString("gender");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        //String name = user.get("name");
        //String email = user.get("email");

        Intent broadcastIntent = new Intent();
        broadcastIntent.setAction(Userprofile.MyWebRequestReceiver.PROCESS_RESPONSE);
        broadcastIntent.addCategory(Intent.CATEGORY_DEFAULT);
        //broadcastIntent.putExtra("name",name);
       // broadcastIntent.putExtra("email", email);
      //  broadcastIntent.putExtra("error", error);
        broadcastIntent.putExtra("contacts",contact);
        broadcastIntent.putExtra("address",address);
        broadcastIntent.putExtra("gender",gender);

        sendBroadcast(broadcastIntent);

    }
}
