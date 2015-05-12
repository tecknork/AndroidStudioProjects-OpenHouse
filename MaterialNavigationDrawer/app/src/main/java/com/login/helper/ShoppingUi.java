package com.login.helper;

import android.app.IntentService;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;

import com.com.web.talk.HttpManager;
import com.com.web.talk.RequestPackage;
import com.google.gson.Gson;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import it.neokree.example.mockedFragments.ProductListActivity;
import it.neokree.example.mockedFragments.ShoppingList;
import model.Product;

/**
 * Created by TecKNork on 4/3/2015.
 */
public class ShoppingUi extends IntentService {
    public static final String REQUEST_STRING = "myRequest";
    public static final String RESPONSE_STRING = "myResponse";
    public static final String RESPONSE_MESSAGE = "myResponseMessage";
    private String filename = "MySampleFile6.json";
    private String filepath = "MyFileStorage";
    private SQLiteHandler db;
    private SessionManager session;
    public ShoppingUi() {
        super("IntentUI");
    }


    @Override
    protected void onHandleIntent(Intent intent) {
        db=new SQLiteHandler(getApplicationContext());
        session=new SessionManager(getApplicationContext());


        ArrayList<Product> list=new ArrayList<Product>();
        List<Product> list2=new ArrayList<Product>();
        File myExternalFile = new File(getExternalFilesDir(filepath), filename);
        Type listOfTestObject = new TypeToken<List<Product>>(){}.getType();
//        Product one=new Product("PakistaniFucker","alpha","beta","dd","ss");
//        list2.add(one);
//        list2.add(one);
//
//
//            Gson gson =new Gson();
//
//
//        String json=gson.toJson(list2,listOfTestObject);
//
//
//
//        try{
//            FileWriter writer = new FileWriter(myExternalFile);
//            writer.write(json);
//            writer.close();
//        }catch (IOException e)
//        {
//            e.printStackTrace();
//        }

        Gson gson2 = new Gson();
        List<Product> list3=new ArrayList<Product>();
        try {

            BufferedReader br = new BufferedReader(
                    new FileReader(myExternalFile));

            //convert the json string back to object
            list3 = gson2.fromJson(br, listOfTestObject);
//            Product obj = gson2.fromJson(br, Product.class);
//
//            list.add(obj);

//            System.out.println(obj);

        } catch (IOException e) {
            e.printStackTrace();
        }


//        Product a=new Product();
//        a.setName("PAKISTANI");
//        a.setPrice("2123");
//        Product b=new Product();
//        b.setName("JAPNI");
//        b.setPrice("2123");
//        list.add(a);
//        list.add(b);
//
//        try {
//            FileOutputStream fos = new FileOutputStream(myExternalFile);
//            ObjectOutputStream oos = new ObjectOutputStream(fos);
//            oos.writeObject(list);
//            fos.close();
//        }
//        catch (Exception e)
//        {
//
//        }
//        list=new ArrayList<Product>();
//        List<Product> clubs=new ArrayList<Product>();
//        try {
//            FileInputStream fis = new FileInputStream(myExternalFile);
//            ObjectInputStream is = new ObjectInputStream(fis);
//
//    //        clubs= (List<Product>) is.readObject();
//         list=(ArrayList) is.readObject();
//            is.close();
//            fis.close();
//
//        }
//        catch (Exception e)
//        {
//          e.printStackTrace();
//        }
//
//        list.add(new Product("sadsad0","alpha","beta","dd","ss"));
      //  list=new ArrayList(clubs);
        list=(ArrayList) list3;
        Intent broadcastIntent = new Intent();
        broadcastIntent.setAction(ShoppingList.MyWebRequestReceiver.PROCESS_RESPONSE);
        broadcastIntent.addCategory(Intent.CATEGORY_DEFAULT);
        //broadcastIntent.putExtra("name",name);
        // broadcastIntent.putExtra("email", email);
        //    broadcastIntent.putExtra("error", error);
        broadcastIntent.putParcelableArrayListExtra("product3", list);
        sendBroadcast(broadcastIntent);

    }
}