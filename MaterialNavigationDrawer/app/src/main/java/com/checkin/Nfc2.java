package com.checkin;

/**
 * Created by TecKNork on 3/26/2015.
 */

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentFilter.MalformedMimeTypeException;
import android.content.SharedPreferences;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiConfiguration.KeyMgmt;
import android.net.wifi.WifiManager;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.IsoDep;
import android.nfc.tech.MifareClassic;
import android.nfc.tech.MifareUltralight;
import android.nfc.tech.Ndef;
import android.nfc.tech.NfcA;
import android.nfc.tech.NfcB;
import android.nfc.tech.NfcF;
import android.nfc.tech.NfcV;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.com.web.talk.HttpManager;
import com.com.web.talk.RequestPackage;
import com.login.helper.SQLiteHandler;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.HashMap;

import it.neokree.example.R;
import model.Shop;

/**
 * Activity for reading data from an NDEF Tag.
 *
 * @author Ralf Wondratschek
 *
 */
public class Nfc2 extends Activity {

    public static final String MIME_TEXT_PLAIN = "text/plain";
    public static final String TAG = "NfcDemo";
    public static String StoreID;
    private final String[][] techList = new String[][] {
            new String[] {
                    NfcA.class.getName(),
                    NfcB.class.getName(),
                    NfcF.class.getName(),
                    NfcV.class.getName(),
                    IsoDep.class.getName(),
                    MifareClassic.class.getName(),
                    MifareUltralight.class.getName(), Ndef.class.getName()
            }
    };
    public TextView mTextView;
    public NfcAdapter mNfcAdapter;
    private SQLiteHandler db;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nfc);
        db=new SQLiteHandler(getApplicationContext());
        mTextView = (TextView) findViewById(R.id.textView_explanation);

        mNfcAdapter = NfcAdapter.getDefaultAdapter(this);

        if (mNfcAdapter == null) {
            // Stop here, we definitely need NFC
            Toast.makeText(this, "This device doesn't support NFC.", Toast.LENGTH_LONG).show();
            finish();
            return;

        }

        if (!mNfcAdapter.isEnabled()) {
            mTextView.setText("NFC is disabled.");
        } else {
            mTextView.setText(R.string.explanation);
        }

        //handleIntent(getIntent());
    }


    public void setadapter()
    {
        mNfcAdapter = NfcAdapter.getDefaultAdapter(this);
    }
    @Override
    public void onResume() {
        super.onResume();
        // creating pending intent:
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
        // creating intent receiver for NFC events:
        IntentFilter filter = new IntentFilter();
        filter.addAction(NfcAdapter.ACTION_TAG_DISCOVERED);
        filter.addAction(NfcAdapter.ACTION_NDEF_DISCOVERED);
        filter.addAction(NfcAdapter.ACTION_TECH_DISCOVERED);
        // enabling foreground dispatch for getting intent from NFC event:
        NfcAdapter nfcAdapter = NfcAdapter.getDefaultAdapter(this);
        nfcAdapter.enableForegroundDispatch(this, pendingIntent, new IntentFilter[]{filter}, this.techList);
    }

    @Override
    protected void onPause() {
        super.onPause();
        // disabling foreground dispatch:
        NfcAdapter nfcAdapter = NfcAdapter.getDefaultAdapter(this);
        nfcAdapter.disableForegroundDispatch(this);
    }
    @Override
    protected void onNewIntent(Intent intent) {
		/*
		 * This method gets called, when a new Intent gets associated with the current activity instance.
		 * Instead of creating a new activity, onNewIntent will be called. For more information have a look
		 * at the documentation.
		 *
		 * In our case this method gets called, when the user attaches a Tag to the device.
		 */
        //handleIntent(intent);

        if (intent.getAction().equals(NfcAdapter.ACTION_TAG_DISCOVERED)) {
            try {
                ((TextView)findViewById(R.id.textView_explanation)).setText(
                        "NFC Tag\n" + NfcAdapter.EXTRA_ID +
                                ByteArrayToHexString(intent.getByteArrayExtra(NfcAdapter.EXTRA_ID)));
                Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
                new NdefReaderTask().execute(tag);

                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
                prefs.edit().putString("storeID", StoreID).commit();
            } catch (UnsupportedEncodingException e) {
                // TODO Auto-generated catch block1
                e.printStackTrace();
            }
        }
    }

    private void handleIntent(Intent intent) {
        String action = intent.getAction();
        if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(action)) {

            String type = intent.getType();
            if (MIME_TEXT_PLAIN.equals(type)) {

                Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
                new NdefReaderTask().execute(tag);


            } else {
                Log.d(TAG, "Wrong mime type: " + type);
            }
        } else if (NfcAdapter.ACTION_TECH_DISCOVERED.equals(action)) {

            // In case we would still use the Tech Discovered Intent
            Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
            String[] techList = tag.getTechList();
            String searchedTech = Ndef.class.getName();

            for (String tech : techList) {
                if (searchedTech.equals(tech)) {
                    new NdefReaderTask().execute(tag);
                    break;
                }
            }
        }
    }

    /**
     * @param activity The corresponding {@link android.app.Activity} requesting the foreground dispatch.
     * @param adapter The {@link android.nfc.NfcAdapter} used for the foreground dispatch.
     */
    public static void setupForegroundDispatch(final Activity activity, NfcAdapter adapter) {
        final Intent intent = new Intent(activity.getApplicationContext(), activity.getClass());
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);

        final PendingIntent pendingIntent = PendingIntent.getActivity(activity.getApplicationContext(), 0, intent, 0);

        IntentFilter[] filters = new IntentFilter[1];
        String[][] techList = new String[][]{};

        // Notice that this is the same filter as in our manifest.
        filters[0] = new IntentFilter();
        filters[0].addAction(NfcAdapter.ACTION_NDEF_DISCOVERED);
        filters[0].addCategory(Intent.CATEGORY_DEFAULT);
        try {
            filters[0].addDataType(MIME_TEXT_PLAIN);
        } catch (MalformedMimeTypeException e) {
            throw new RuntimeException("Check your mime type.");
        }

        adapter.enableForegroundDispatch(activity, pendingIntent, filters, techList);
    }

//    /**
//     * @param activity The corresponding {@link BaseActivity} requesting to stop the foreground dispatch.
//     * @param adapter The {@link NfcAdapter} used for the foreground dispatch.
//     */
    public static void stopForegroundDispatch(final Activity activity, NfcAdapter adapter) {
        adapter.disableForegroundDispatch(activity);
    }

    /**
     * Background task for reading the data. Do not block the UI thread while reading.
     *
     * @author Ralf Wondratschek
     *
     */
    private class NdefReaderTask extends AsyncTask<Tag, Void, String> {

        @Override
        protected String doInBackground(Tag... params) {
            Tag tag = params[0];

            Ndef ndef = Ndef.get(tag);
            if (ndef == null) {
                System.out.println("SADASDASDASS");
                return null;
            }

            NdefMessage ndefMessage = ndef.getCachedNdefMessage();

            NdefRecord[] records = ndefMessage.getRecords();
            for (NdefRecord ndefRecord : records) {
                if (ndefRecord.getTnf() == NdefRecord.TNF_WELL_KNOWN && Arrays.equals(ndefRecord.getType(), NdefRecord.RTD_TEXT)) {
                    try {
                        return readText(ndefRecord);
                    } catch (UnsupportedEncodingException e) {
                        Log.e(TAG, "Unsupported Encoding", e);
                    }
                }
            }

            return null;
        }

        private String readText(NdefRecord record) throws UnsupportedEncodingException {
			/*
			 * See NFC forum specification for "Text Record Type Definition" at 3.2.1
			 *
			 * http://www.nfc-forum.org/specs/
			 *
			 * bit_7 defines encoding
			 * bit_6 reserved for future use, must be 0
			 * bit_5..0 length of IANA language code
			 */

            byte[] payload = record.getPayload();

            // Get the Text Encoding
            String textEncoding = ((payload[0] & 128) == 0) ? "UTF-8" : "UTF-16";

            // Get the Language Code
            int languageCodeLength = payload[0] & 0063;

            // String languageCode = new String(payload, 1, languageCodeLength, "US-ASCII");
            // e.g. "en"

            // Get the Text
            return new String(payload, languageCodeLength + 1, payload.length - languageCodeLength - 1, textEncoding);
        }

        @Override
        protected void onPostExecute(String result) {
            System.out.println("Works");
            if (result != null) {
                mTextView.setText("Read content: " + result);
                parseWifiInfo(result);
                //System.out.println("hello" +result);

            }
        }
    }

    private String ByteArrayToHexString(byte [] inarray) throws UnsupportedEncodingException {
        int i, j, in;
        String [] hex = {"0","1","2","3","4","5","6","7","8","9","A","B","C","D","E","F"};
        String out= "";

        for(j = 0 ; j < inarray.length ; ++j)
        {
            in = (int) inarray[j] & 0xff;
            i = (in >> 4) & 0x0f;
            out += hex[i];
            i = in & 0x0f;
            out += hex[i];
        }


        String file_string = "";

        for(int x = 0; x < inarray.length; x++)
        {
            file_string += (char)inarray[x];
        }


        String str = new String(inarray, "UTF-8");
        return out;
    }


    void parseWifiInfo(String text)
    {
        //text="WIFI:S:EVO Wingle 211-F;T:WPA;P:hamza123;;";
        if(!text.startsWith("WIFI:") || !text.endsWith(";;"))
        {
            Toast t = Toast.makeText(getApplicationContext(), "Not a valid Wifi QR code", Toast.LENGTH_LONG);
            t.show();
            return;
        }

        String ssid = null;
        String username = null;
        String password = null;
        String EAP = null;
        String phase2 = null;
        String anon = null;

        text = text.substring("WIFI:".length(), text.length()-2);

        //This split method leads to some weird edge cases, but I want to support semicolons in fields
        //without breaking backwards compatibility
        String[] params = text.split(";(?=S:|T:|U:|P:|E:|PH:|A:|CC:|PK:|CA:)");

        for(String s: params)
        {
            String[] keyval = s.split(":");
            if(keyval[0].equals("S"))
            {
                ssid = keyval[1];
            }
            else if(keyval[0].equals("U"))
            {
                username = keyval[1];
            }
            else if(keyval[0].equals("P"))
            {
                password = keyval[1];
            }
            else if(keyval[0].equals("E"))
            {
                EAP = keyval[1];
            }
            else if(keyval[0].equals("PH"))
            {
                phase2 = keyval[1];
            }
            else if(keyval[0].equals("A"))
            {
                anon = keyval[1];
            }
        }

        Toast t = Toast.makeText(getApplicationContext(), "Adding network '" + ssid + "'...", Toast.LENGTH_SHORT);
        t.show();

        saveEapConfig(ssid, username, password, EAP, phase2, anon);

        finish();
    }



    boolean saveEapConfig(String ssid, String userName, String passString, String EAP, String phase2, String anon)
    {
        /********************************Configuration Strings****************************************************/
        Toast t2 = Toast.makeText(getApplicationContext(), ssid, Toast.LENGTH_LONG);
        t2.show();

        Toast t4 = Toast.makeText(getApplicationContext(), passString, Toast.LENGTH_LONG);
        t4.show();

        final String ENTERPRISE_EAP = EAP;
        final String ENTERPRISE_PHASE2 = phase2;
        final String ENTERPRISE_ANON_IDENT = anon;
        StoreID=anon;
       SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        prefs.edit().putString("storeID", StoreID).commit();
        requestData(getResources().getString(R.string.localhost)+"rest3/v1/shopinfo2/" + StoreID);

	    /*Create a WifiConfig*/
        WifiConfiguration selectedConfig = new WifiConfiguration();

        selectedConfig.SSID = "\""+ssid+"\"";
        selectedConfig.status = WifiConfiguration.Status.ENABLED;
        selectedConfig.preSharedKey = "\"" + passString + "\"";
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
//        String networkSSID="NUCES Campus";
//        String password="12345678";

        selectedConfig.allowedProtocols.set(WifiConfiguration.Protocol.RSN);
        selectedConfig.allowedProtocols.set(WifiConfiguration.Protocol.WPA);
        selectedConfig.allowedKeyManagement.set(KeyMgmt.WPA_PSK);
        selectedConfig.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
        selectedConfig.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);
        selectedConfig.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40);
        selectedConfig.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP104);
        selectedConfig.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
        selectedConfig.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);

        selectedConfig.preSharedKey = "\"".concat(passString).concat("\"");

        //   wc.preSharedKey = "\""+ networkPass +"\"";
//        WifiConfiguration conf = new WifiConfiguration();
//        conf.SSID = "\"" + networkSSID + "\"";
//        conf.preSharedKey = "\"" + password + "\"";
//        conf.status = WifiConfiguration.Status.ENABLED;



        // wc.wepKeys[0] = "\"aaabbb1234\""; //This is the WEP Password
        //   wc.wepTxKeyIndex = 0;



        WifiManager  wifiManag = (WifiManager) this.getSystemService(WIFI_SERVICE);
        boolean res1 = wifiManag.setWifiEnabled(true);
        int res = wifiManag.addNetwork(selectedConfig);
        Log.d("WifiPreference", "add Network returned " + res );
        boolean es = wifiManag.saveConfiguration();
        Log.d("WifiPreference", "saveConfiguration returned " + es );
        boolean b = wifiManag.enableNetwork(res, true);
        Log.d("WifiPreference", "enableNetwork returned " + b );




        // end


        if(!res1 || res == -1 || !b || !es)
        {
            Toast t = Toast.makeText(getApplicationContext(), "WiFi network connection failed", Toast.LENGTH_LONG);
            t.show();
            return false;
        }

        try { Thread.sleep(1000); } catch (InterruptedException e) { e.printStackTrace(); }

        Toast t = Toast.makeText(getApplicationContext(), "Network added", Toast.LENGTH_SHORT);
        t.show();
       // requestData(getResources().getString(R.string.localhost)+"rest/v1/shopinfo/" + StoreID);
        return true;
    }



    private void requestData(String uri) {
          HashMap<String, String> user = db.getUserDetails();
         String apikey=user.get("uid");
        RequestPackage p = new RequestPackage();
            p.setApi_key(apikey);
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
                    SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(Nfc2.this);
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
