package com.login.helper;

import android.app.IntentService;
import android.content.Intent;
import android.os.Parcelable;

import com.aisle411.mapsdk.shopping.SponsoredPin;
import com.com.web.talk.HttpManager;
import com.com.web.talk.RequestPackage;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import it.neokree.example.R;
import it.neokree.example.mockedActivity.ProductActivity;
import it.neokree.example.mockedFragments.ProductListActivity;
import model.Product;
import it.neokree.example.mockedFragments.Userprofile;
import model.ProductContainer;

/**
 * Created by TecKNork on 3/27/2015.
 */
public class CrawlerUi extends IntentService {
 public static final String REQUEST_STRING = "myRequest";
public static final String RESPONSE_STRING = "myResponse";
public static final String RESPONSE_MESSAGE = "myResponseMessage";

private SQLiteHandler db;
private SessionManager session;
        public CrawlerUi() {
            super("IntentUI");
        }


        @Override
        protected void onHandleIntent(Intent intent) {
            db=new SQLiteHandler(getApplicationContext());
            session=new SessionManager(getApplicationContext());

            HashMap<String, String> user = db.getUserDetails();
            boolean error=true;
            //Product product=new Product();
            String apikey=user.get("uid");

            RequestPackage p=new RequestPackage();
            p.setMethod("GET");
            p.setUri(getResources().getString(R.string.localhost)+"rest3/v1/crawledproducts");
            //HashMap<String, String> user = db.getUserDetails();
            ArrayList<Product> list=new ArrayList<Product>();
            p.setApi_key(apikey);
            String content = HttpManager.getData(p);
            JSONObject jObj = null;
            JsonParser Jparser=new JsonParser();
            JSONArray jsonarray;

//            Gson g = new Gson();
//            Type listType = new TypeToken<List<Product>>(){}.getType();
//            List<Product> posts = (List<Product>) g.fromJson(content, listType);
        try{

            JSONObject jObject = new JSONObject(content);

              JSONArray jArray = jObject.getJSONArray("product");

            for (int i=0; i < jArray.length(); i++)
            {
                try {
                    JSONObject oneObject = jArray.getJSONObject(i);
                    // Pulling items from the array
                    Product product=new Product();
                    product.setName(oneObject.getString("productname"));
                    product.setImage(oneObject.getString("imagelink"));
                    product.setPrice(oneObject.getString("price"));
                    product.setProducturl(oneObject.getString("pagelink"));
                    list.add(product);
                } catch (JSONException e) {
                    // Oops
                }
            }



            } catch (JSONException e) {
                e.printStackTrace();
            }

//


            //String name = user.get("name");
            //String email = user.get("email");

            Intent broadcastIntent = new Intent();
            broadcastIntent.setAction(ProductListActivity.MyWebRequestReceiver.PROCESS_RESPONSE);
            broadcastIntent.addCategory(Intent.CATEGORY_DEFAULT);
            //broadcastIntent.putExtra("name",name);
            // broadcastIntent.putExtra("email", email);
        //    broadcastIntent.putExtra("error", error);
            broadcastIntent.putParcelableArrayListExtra("product2", list);
            sendBroadcast(broadcastIntent);

        }
}