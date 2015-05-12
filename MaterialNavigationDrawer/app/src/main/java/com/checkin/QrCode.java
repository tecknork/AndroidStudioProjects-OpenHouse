package com.checkin;

import it.neokree.example.MainActivity;
import it.neokree.example.R;
import model.Shop;

import net.sourceforge.zbar.Config;
import net.sourceforge.zbar.Image;
import net.sourceforge.zbar.ImageScanner;
import net.sourceforge.zbar.Symbol;
import net.sourceforge.zbar.SymbolSet;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.hardware.Camera;
import android.hardware.Camera.AutoFocusCallback;
import android.hardware.Camera.PreviewCallback;
import android.hardware.Camera.Size;
import android.net.wifi.*;
import android.net.wifi.p2p.nsd.*;
import android.net.wifi.p2p.*;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiConfiguration.KeyMgmt;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.com.web.talk.HttpManager;
import com.com.web.talk.RequestPackage;
import com.login.helper.SQLiteHandler;

import net.sourceforge.zbar.Config;
import net.sourceforge.zbar.ImageScanner;
import net.sourceforge.zbar.SymbolSet;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

/**
 * Created by TecKNork on 3/26/2015.
 */
public class QrCode extends ActionBarActivity {
    private Camera mCamera;
    private CameraPreview mPreview;
    private Handler autoFocusHandler;
    public static String StoreID;
    private SQLiteHandler db;
    TextView scanText;
    Button scanButton;

    ImageScanner scanner;

    private boolean barcodeScanned = false;
    private boolean previewing = true;

    static {
        System.loadLibrary("iconv");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qr);
           db=new SQLiteHandler(getApplicationContext());
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        autoFocusHandler = new Handler();
        mCamera = getCameraInstance();

        /* Instance barcode scanner */
        scanner = new ImageScanner();
        scanner.setConfig(0, Config.X_DENSITY, 3);
        scanner.setConfig(0, Config.Y_DENSITY, 3);
            mPreview= new com.checkin.CameraPreview(this,mCamera,previewCb,autoFocusCB);

        FrameLayout preview = (FrameLayout) findViewById(R.id.cameraPreview);
        preview.addView(mPreview);

        scanText = (TextView) findViewById(R.id.scanText);

        scanButton = (Button) findViewById(R.id.ScanButton);

        scanButton.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                if (barcodeScanned) {
                    barcodeScanned = false;
                    scanText.setText("Scanning...");
                    mCamera.setPreviewCallback(previewCb);
                    mCamera.startPreview();
                    previewing = true;
                    mCamera.autoFocus(autoFocusCB);
                }
            }
        });
    }

    public void onPause() {
        super.onPause();
        releaseCamera();
    }

    /**
     * A safe way to get an instance of the Camera object.
     */
    public static Camera getCameraInstance() {
        Camera c = null;
        try {
            c = Camera.open();
        } catch (Exception e) {
        }
        return c;
    }

    private void releaseCamera() {
        if (mCamera != null) {
            previewing = false;
            mCamera.setPreviewCallback(null);
            mCamera.release();
            mCamera = null;
        }
    }

    private Runnable doAutoFocus = new Runnable() {
        public void run() {
            if (previewing)
                mCamera.autoFocus(autoFocusCB);
        }
    };

    PreviewCallback previewCb = new PreviewCallback() {
        public void onPreviewFrame(byte[] data, Camera camera) {
            Camera.Parameters parameters = camera.getParameters();
            Size size = parameters.getPreviewSize();

            Image barcode = new Image(size.width, size.height, "Y800");
            barcode.setData(data);

            int result = scanner.scanImage(barcode);

            if (result != 0) {
                previewing = false;
                mCamera.setPreviewCallback(null);
                mCamera.stopPreview();

                SymbolSet syms = scanner.getResults();
                for (Symbol sym : syms) {
                    scanText.setText("barcode result " + sym.getData());
                    barcodeScanned = true;
                    String text = sym.getData();
                    parseWifiInfo(text);
                }
            }
        }
    };

    // Mimic continuous auto-focusing
    AutoFocusCallback autoFocusCB = new AutoFocusCallback() {
        public void onAutoFocus(boolean success, Camera camera) {
            autoFocusHandler.postDelayed(doAutoFocus, 1000);
        }
    };


    void parseWifiInfo(String text) {
        Toast t2 = Toast.makeText(getApplicationContext(), text, Toast.LENGTH_LONG);
        t2.show();
        //text="WIFI:S:EVO Wingle 211-F;T:WPA;P:hamza123;;";
        //	if(!text.startsWith("WIFI:") || !text.endsWith(";;"))
        //	{
        //		Toast t = Toast.makeText(getApplicationContext(), "Not a valid Wifi QR code", Toast.LENGTH_LONG);
        //		t.show();
        // 		return;
        //  	}

        String ssid = null;
        String username = null;
        String password = null;
        String EAP = null;
        String phase2 = null;
        String anon = null;

        text = text.substring("WIFI:".length(), text.length() - 2);

        //This split method leads to some weird edge cases, but I want to support semicolons in fields
        //without breaking backwards compatibility
        String[] params = text.split(";(?=S:|T:|U:|P:|E:|PH:|A:|CC:|PK:|CA:)");

        for (String s : params) {
            String[] keyval = s.split(":");
            if (keyval[0].equals("S")) {
                ssid = keyval[1];
            } else if (keyval[0].equals("U")) {
                username = keyval[1];
            } else if (keyval[0].equals("P")) {
                password = keyval[1];
            } else if (keyval[0].equals("E")) {
                EAP = keyval[1];
            } else if (keyval[0].equals("PH")) {
                phase2 = keyval[1];
            } else if (keyval[0].equals("A")) {
                anon = keyval[1];
            }
        }

        Toast t = Toast.makeText(getApplicationContext(), "Adding network '" + ssid + "'...", Toast.LENGTH_SHORT);
        t.show();

        saveEapConfig(ssid, username, password, EAP, phase2, anon);

        finish();
    }


    boolean saveEapConfig(String ssid, String userName, String passString, String EAP, String phase2, String anon) {
        /********************************Configuration Strings****************************************************/
        Toast t2 = Toast.makeText(getApplicationContext(), ssid, Toast.LENGTH_LONG);
        t2.show();

        Toast t4 = Toast.makeText(getApplicationContext(), passString, Toast.LENGTH_LONG);
        t4.show();

        Toast t5 = Toast.makeText(getApplicationContext(), anon, Toast.LENGTH_LONG);
        t5.show();

        final String ENTERPRISE_EAP = EAP;
        final String ENTERPRISE_PHASE2 = phase2;
        final String ENTERPRISE_ANON_IDENT = anon;
        StoreID = anon;
     //   SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
       // prefs.edit().putString("storeID", StoreID).commit();
 //       requestData(getResources().getString(R.string.localhost)+"rest/v1/shopinfo/" + StoreID);
	    /*Create a WifiConfig*/
        WifiConfiguration selectedConfig = new WifiConfiguration();

        selectedConfig.SSID = "\"" + ssid + "\"";
        selectedConfig.status = WifiConfiguration.Status.ENABLED;
        selectedConfig.preSharedKey = "\"" + passString + "\"";


        selectedConfig.allowedProtocols.set(WifiConfiguration.Protocol.RSN);
        selectedConfig.allowedProtocols.set(WifiConfiguration.Protocol.WPA);
        selectedConfig.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
        selectedConfig.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
        selectedConfig.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);
        selectedConfig.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40);
        selectedConfig.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP104);
        selectedConfig.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
        selectedConfig.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);

        selectedConfig.preSharedKey = "\"".concat(passString).concat("\"");

 //WORKING FOR WIFI WPA-2PERSONAL WITH AES KEY // ROYALLODGE1 - Password Peshawar123 //

        //  selectedConfig.allowedKeyManagement.set(KeyMgmt.IEEE8021X);
        //   selectedConfig.allowedKeyManagement.set(KeyMgmt.WPA_EAP);

        //    selectedConfig.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
        //    selectedConfig.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40);

        // Enterprise Settings
	 /*   String networkPass = "hamza123";
	    WifiManager wifi = (WifiManager) getSystemService(Context.WIFI_SERVICE);
	    WifiConfiguration wc = new WifiConfiguration();
	    wc.SSID = "\"EVO Wingle 211-F\""; //IMP! This should be in Quotes!!
	    wc.hiddenSSID = false;
	    wc.status = WifiConfiguration.Status.DISABLED;
	    wc.priority = 40;
	    wc.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
	    wc.allowedProtocols.set(WifiConfiguration.Protocol.RSN);
	    wc.allowedProtocols.set(WifiConfiguration.Protocol.WPA);
	    wc.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN);
	    wc.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.SHARED);
	    wc.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
	    wc.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);
	    wc.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40);
	    wc.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP104);
*/
//        String networkSSID = "Royal Lodge1 ";
//        String password = "peshawar123";
//        //   wc.preSharedKey = "\""+ networkPass +"\"";
//        WifiConfiguration conf = new WifiConfiguration();
//        conf.SSID = "\"" + networkSSID + "\"";
//        conf.preSharedKey = "\"" + password + "\"";
//        conf.status = WifiConfiguration.Status.ENABLED;


        // wc.wepKeys[0] = "\"aaabbb1234\""; //This is the WEP Password
        //   wc.wepTxKeyIndex = 0;


        WifiManager wifiManag = (WifiManager) this.getSystemService(WIFI_SERVICE);
        boolean res1 = wifiManag.setWifiEnabled(true);
        int res = wifiManag.addNetwork(selectedConfig);
        Log.d("WifiPreference", "add Network returned " + res);
        boolean es = wifiManag.saveConfiguration();
        Log.d("WifiPreference", "saveConfiguration returned " + es);
        boolean b = wifiManag.enableNetwork(res, true);
        Log.d("WifiPreference", "enableNetwork returned " + b);


        // end


        if (!res1 || res == -1 || !b || !es) {
            Toast t = Toast.makeText(getApplicationContext(), "WiFi network connection failed", Toast.LENGTH_LONG);
            t.show();
            return false;
        }

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        Toast t = Toast.makeText(getApplicationContext(), "Network added", Toast.LENGTH_SHORT);
        t.show();
        return true;
    }




    private void requestData(String uri) {
     //   HashMap<String, String> user = db.getUserDetails();
       // String apikey=user.get("uid");
        RequestPackage p = new RequestPackage();
    //    p.setApi_key(apikey);
        p.setMethod("GET");
        p.setUri(uri);
   //    p.setParam("email","a@gmail.com" );
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
            try {
                JSONObject jObj = new JSONObject(result);
                boolean error = jObj.getBoolean("error");

                // Check for error node in json
                if (!error) {
                    // user successfully logged in
                    // Create login session
            //       session.setLogin(true);

                    Shop shop1=new Shop();

                    shop1.setShop_id(jObj.getString("shop_id"));
                    shop1.setName(jObj.getString("name"));
                    shop1.setCity( jObj.getString("city"));
                    shop1.setLat(jObj.getString("lat"));
                    shop1.setLon(jObj.getString("lon"));
                    shop1.setImage(jObj.getString("image"));
                    shop1.setWifiname(jObj.getString("wifi_name"));
                    shop1.setWifi_password(jObj.getString("wifi_password"));
                    shop1.setContact(jObj.getString("contact"));
                    SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(QrCode.this);
                    prefs.edit().putString("storeID2",shop1.getShop_id()).commit();
                db.addShop(shop1.getName(), shop1.getWifiname(), shop1.getWifi_password() , shop1.getContact(),shop1.getImage(),shop1.getLat(),shop1.getLon(),shop1.getShop_id(),shop1.getCity() );


                    // Launch main activity
//                    Intent intent = new Intent(Login.this,
//                            MainActivity.class);
//                    startActivity(intent);
//                    finish();
                } else {
                    // Error in login. Get the error message
                    String errorMsg = jObj.getString("error_msg");
                    Toast.makeText(getApplicationContext(),
                            errorMsg, Toast.LENGTH_LONG).show();
                }
            } catch (JSONException e) {
                // JSON error
                e.printStackTrace();
            }

      //      Log.d("hello", "Login Response: " + result.toString());



            // updateDisplay(result);

        }

    }




}
