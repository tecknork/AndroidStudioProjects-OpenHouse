package it.neokree.example.mockedFragments;


import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.login.helper.SQLiteHandler;
import com.login.helper.SearchProductUi;

import it.neokree.example.R;
import it.neokree.example.mockedActivity.Asilemap;
import it.neokree.example.mockedActivity.CheckIn;

/**
 * Created by TecKNork on 4/5/2015.
 */
public class ShopFragment extends Fragment {
    private Button viewmap;
    private TextView name;
    private Button Searchproduct;
    public View onCreateView(LayoutInflater inflater,@Nullable ViewGroup container,@Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.storeactivity, null, false);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());

       name= (TextView) rootView.findViewById(R.id.shopname);
       name.setText(prefs.getString("storeID","ShoppingStore"));
       viewmap = (Button) rootView.findViewById(R.id.button5);
       Searchproduct=(Button) rootView.findViewById(R.id.button4);
       viewmap.setOnClickListener(new View.OnClickListener(){
            @Override
            //On click function
            public void onClick(View view) {
                //Create the intent to start another activity
                Intent intent = new Intent(view.getContext(), Asilemap.class);
                startActivity(intent);
           }
        });

       Searchproduct.setOnClickListener(new View.OnClickListener() {
            @Override
            //On click function
            public void onClick(View view) {
                //Create the intent to start another activity
                Intent intent = new Intent(view.getContext(), SearchProducts.class);
                startActivity(intent);
           }
        });

        return rootView;
    }


}
