package it.neokree.example.light;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;


import com.login.helper.IntentUI;

import it.neokree.example.R;
import it.neokree.example.gps.MapsActivity;
import it.neokree.example.mockedActivity.Settings;
import it.neokree.example.mockedFragments.History;
import it.neokree.example.mockedFragments.ProductListActivity;
import it.neokree.example.mockedFragments.FragmentButton;
import it.neokree.example.mockedFragments.RewardPoints;
import it.neokree.example.mockedFragments.SearchProducts;
import it.neokree.example.mockedFragments.ShopFragment;
import it.neokree.example.mockedFragments.ShoppingList;
import it.neokree.example.mockedFragments.Userprofile;
import it.neokree.materialnavigationdrawer.MaterialNavigationDrawer;

/**
 * Created by neokree on 18/01/15.
 */
public class MockedAccount extends MaterialNavigationDrawer {
    private static final String TAG = "MockedAcvitiy";
    private MyWebRequestReceiver receiver;


    @Override
    protected void onStop() {
        super.onStop();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(receiver);
    }

    @Override
    public void init(Bundle savedInstanceState) {





        IntentFilter filter = new IntentFilter(MyWebRequestReceiver.PROCESS_RESPONSE);
        filter.addCategory(Intent.CATEGORY_DEFAULT);
        receiver = new MyWebRequestReceiver();
        registerReceiver(receiver, filter);

        // set header data
        setDrawerHeaderImage(R.drawable.mat2);
        setUsername("My App Name");
        setUserEmail("My version build");


        Intent msgIntent = new Intent(MockedAccount.this, IntentUI.class);
        startService(msgIntent);
        //setFirstAccountPhoto(getResources().getDrawable(R.drawable.photo));
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        String StoreID = prefs.getString("storeID",null);
        String StoreID2=prefs.getString("storeID2","failed");
        // create sections
        this.addSection(newSection("ShoppingList",R.drawable.ic_shopping_grey600_24dp, new ShoppingList()).setSectionColor(Color.parseColor("#F2B90F")));

        this.addSection(newSection("Profile", R.drawable.ic_profile,new Userprofile()).setSectionColor(Color.parseColor("#e62f17")));

        this.addSection(newSection("ProductList",R.drawable.ic_crawler,new ProductListActivity()).setSectionColor(Color.parseColor("#F28705")));
       // this.addSection(newSection("SearchProd",new SearchProducts()));
//        this.addSection(newSection("GPS",new LocationFragment()));

        if(StoreID!=null) {
            this.addSection(newSection(StoreID,R.drawable.ic_shop, new ShopFragment()).setSectionColor(Color.parseColor("#730202")));
        }

          //  this.addSection(newSection(StoreID2, R.drawable.ic_mic_white_24dp, new ShopFragment()).setSectionColor(Color.parseColor("#9c27b0")));



        this.addSection(newSection("RewardPoints",R.drawable.ic_reward,new RewardPoints()).setSectionColor(Color.parseColor("#F25C05")));

        this.addSection(newSection("Transaction History",R.drawable.ic_history,new History()).setSectionColor(Color.parseColor("#F25C05")));
        // create bottom section
        this.addBottomSection(newSection("GPS Location",R.drawable.ic_settings_black_24dp,new Intent(this,MapsActivity.class)));
    }




    public class MyWebRequestReceiver extends BroadcastReceiver{

        public static final String PROCESS_RESPONSE = "it.neokree.example.light.intent.action.PROCESS_RESPONSE";

        @Override
        public void onReceive(Context context, Intent intent) {
            String responseString = intent.getStringExtra("name");
            String reponseMessage = intent.getStringExtra("email");

            setUsername(responseString);
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(MockedAccount.this);
            prefs.edit().putString("ProfileName",responseString).commit();
            setUserEmail(reponseMessage);

        }


    }

}
