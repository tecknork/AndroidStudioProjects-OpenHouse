package it.neokree.example.mockedActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.com.web.talk.HttpManager;
import com.com.web.talk.RequestPackage;
import com.login.helper.SQLiteHandler;

import java.util.HashMap;

import it.neokree.example.R;

/**
 * Created by TecKNork on 4/10/2015.
 */
public class ChangeGender extends Activity {

    private SQLiteHandler db;
    private EditText inputAddress;
    private Button btnOk;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.changegender);
        db=new SQLiteHandler(getApplicationContext());
        inputAddress = (EditText) findViewById(R.id.edittext1);
        btnOk = (Button) findViewById(R.id.ok);
        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendResult();
            }

        });
    }


    private void sendResult() {
        String alpha=inputAddress.getText().toString();
        requestData(getResources().getString(R.string.localhost)+"rest3/v1/profilegender",alpha);
        Intent resultI = new Intent();
        //Product alpha= new Product();
//        alpha.setName(name.getText().toString());
//        alpha.setPrice(name2.getText().toString());
//
      //  String alpha=inputAddress.getText().toString();
      resultI.putExtra("gender", alpha);
        setResult(RESULT_OK, resultI);
        finish();
    }


    private void requestData(String uri,String addresss) {
        HashMap<String, String> user = db.getUserDetails();
        String apikey=user.get("uid");
        RequestPackage p = new RequestPackage();
        p.setApi_key(apikey);
        p.setMethod("POST");
        p.setUri(uri);
        p.setParam("gender",addresss);
//        p.setParam("password","123");

        MyTask task = new MyTask();
        task.execute(p);
    }


    private class MyTask extends AsyncTask<RequestPackage, String, String> {

        String tag_string_req = "req_login";

        private void showDialog() {
//            if (!pDialog.isShowing())
//                pDialog.show();
        }

        private void hideDialog() {
//            if (pDialog.isShowing())
//                pDialog.dismiss();
        }

        @Override
        protected void onPreExecute() {
            //   pDialog.setMessage("Logging in ...");
            // showDialog();
        }

        @Override
        protected String doInBackground(RequestPackage... params) {
            String content = HttpManager.getData(params[0]);
            return content;
        }

        @Override
        protected void onPostExecute(String result) {
            // hideDialog();
//            try {
//                JSONObject jObj = new JSONObject(result);
//                boolean error = jObj.getBoolean("error");
//
//                // Check for error node in json
//                if (!error) {
//                    // user successfully logged in
//                    // Create login session
//                    //       session.setLogin(true);
//
////                    Shop shop1=new Shop();
////
////                    shop1.setShop_id(jObj.getString("shop_id"));
////                    shop1.setName(jObj.getString("name"));
////                    shop1.setCity( jObj.getString("city"));
////                    shop1.setLat(jObj.getString("lat"));
////                    shop1.setLon(jObj.getString("lon"));
////                    shop1.setImage(jObj.getString("image"));
////                    shop1.setWifiname(jObj.getString("wifi_name"));
////                    shop1.setWifi_password(jObj.getString("wifi_password"));
////                    shop1.setContact(jObj.getString("contact"));
////                    SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(QrCode.this);
////                    prefs.edit().putString("storeID2",shop1.getShop_id()).commit();
////                    db.addShop(shop1.getName(), shop1.getWifiname(), shop1.getWifi_password() , shop1.getContact(),shop1.getImage(),shop1.getLat(),shop1.getLon(),shop1.getShop_id(),shop1.getCity() );
//
//
//                    // Launch main activity
////                    Intent intent = new Intent(Login.this,
////                            MainActivity.class);
////                    startActivity(intent);
////                    finish();
//                } else {
//                    // Error in login. Get the error message
//                    String errorMsg = jObj.getString("error_msg");
//                    Toast.makeText(getApplicationContext(),
//                            errorMsg, Toast.LENGTH_LONG).show();
//                }
//            } catch (JSONException e) {
//                // JSON error
//                e.printStackTrace();
//            }

            //      Log.d("hello", "Login Response: " + result.toString());



            // updateDisplay(result);

        }

    }


}
