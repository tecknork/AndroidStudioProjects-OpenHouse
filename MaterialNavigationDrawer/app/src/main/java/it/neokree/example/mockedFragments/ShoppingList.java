package it.neokree.example.mockedFragments;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.login.helper.CrawlerUi;
import com.login.helper.ShoppingUi;
import com.melnykov.fab.FloatingActionButton;

import java.io.File;
import java.io.FileWriter;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import it.neokree.example.R;
import it.neokree.example.mockedActivity.ProductActivity;
import it.neokree.example.mockedActivity.Shopingdialog;
import model.Card;
import model.Product;

/**
 * Created by TecKNork on 3/24/2015.
 */


public class ShoppingList extends Fragment {
    private static final int REQUEST_LIST_ITEM = 235;
    private static final String TAG = "CardListActivity";
    private CustomListAdapter cardArrayAdapter;
    private ListView listView;
    public ShoppingList CustomListView= null;
//    private List<Card> movieList = new ArrayList<Card>();
    private FloatingActionButton fab;
    private MyWebRequestReceiver receiver;
    private ArrayList<Product> movieList = new ArrayList<Product>();


    @Override
    public View onCreateView(LayoutInflater inflater,@Nullable ViewGroup container,@Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_listview, null, false);
        CustomListView=this;
        Resources res =getResources();
        IntentFilter filter = new IntentFilter(MyWebRequestReceiver.PROCESS_RESPONSE);
        filter.addCategory(Intent.CATEGORY_DEFAULT);
        receiver = new MyWebRequestReceiver();
        getActivity().registerReceiver(receiver, filter);
        Intent msgIntent = new Intent(getActivity(), ShoppingUi.class);
        getActivity().startService(msgIntent);




        listView = (ListView) rootView.findViewById(R.id.list);
        fab = (FloatingActionButton) rootView.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               Intent intent=new Intent(getActivity(), Shopingdialog.class);
                startActivityForResult(intent, REQUEST_LIST_ITEM);

            }
        });




    return rootView;
    }


    @Override
    public void onActivityResult(int req, int res, Intent data){
        if(req == REQUEST_LIST_ITEM && res == getActivity().RESULT_OK){
            Product alpha=data.getParcelableExtra("newproduct");
            movieList.add(alpha);
        }
    }

       public void SavetoFile()
       {
           Type listOfTestObject = new TypeToken<List<Product>>(){}.getType();

           Gson gson =new Gson();
           String filename = "MySampleFile6.json";
           String filepath = "MyFileStorage";
           String json=gson.toJson(movieList,listOfTestObject);
           File myExternalFile = new File(getActivity().getExternalFilesDir(filepath), filename);
           try{
               FileWriter writer = new FileWriter(myExternalFile);
               writer.write(json);
               writer.close();
           }catch (IOException e)
           {
               e.printStackTrace();
           }
       }

    @Override
    public void onDestroy() {
        super.onDestroy();
        getActivity().unregisterReceiver(receiver);
        SavetoFile();
    }



    class CustomListAdapter extends BaseAdapter implements View.OnClickListener {

        private Activity activity;
        private ArrayList<Product> data;
        private LayoutInflater inflater=null;
        public Resources res;
        Product tempValues=null;
        int i=0;

//        public CustomListAdapter(Activity activity, List<Movie> movieItems) {
//           activity = activity;
//            this.movieItems = movieItems;
//        }

        public CustomListAdapter(Activity a, ArrayList<Product> d,Resources resLocal) {

            /********** Take passed values **********/
            activity=new Activity();
            activity = a;
            data=d;
            res = resLocal;

            /***********  Layout inflator to call external xml layout () ***********/
            inflater = ( LayoutInflater )activity.
                    getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        }


        public class ViewHolder{

            public TextView text;
            public TextView text1;
            public ImageView image;

        }


        @Override
        public int getCount() {
            if(data.size()<=0)
                return 1;
            return data.size();
        }

        @Override
        public Object getItem(int position) {


            return data.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            View vi = convertView;
            ViewHolder holder;

            if(convertView==null) {

                /****** Inflate tabitem.xml file for each row ( Defined below ) *******/
                vi = inflater.inflate(R.layout.list_item, null);

                /****** View Holder Object to contain tabitem.xml file elements ******/

                holder = new ViewHolder();
                holder.text = (TextView) vi.findViewById(R.id.textView2);
                holder.text1 = (TextView) vi.findViewById(R.id.textView7);
                vi.setTag(holder);
            }
            else
                holder=(ViewHolder)vi.getTag();

            if(data.size()<=0)
            {
                holder.text.setText("No Data");


            }
            else
            {
                /***** Get each Model object from Arraylist ********/
                tempValues=null;
                tempValues = (Product) data.get( position );

                /************  Set Model values in Holder elements ***********/

                holder.text.setText(tempValues.getName());
                holder.text1.setText(tempValues.getPrice());

            //    holder.text1.setText( "Beta" );


                /******** Set Item Click Listner for LayoutInflater for each row *******/

                vi.setOnClickListener(new OnItemClickListener(position, this));
            }
            return vi;
        }

        private String getPriceText(Double price) {
            if (price == null) {
                return res.getString(R.string.product_price_empty);
            } else {
                return res.getString(R.string.product_price, String.format("%1$,.2f", price));
            }
        }

        @Override
        public void onClick(View v) {


        }

        private class OnItemClickListener  implements View.OnClickListener {
            private int mPosition;
            private CustomListAdapter a;

            OnItemClickListener(int position,CustomListAdapter adapter){
                mPosition = position;
                a=adapter;
            }

            @Override
            public void onClick(View arg0) {

                Product product = (Product) a.getItem(mPosition);
                startActivity(new Intent(getActivity(), ProductActivity.class).putExtra("product", (Parcelable)product));


                /****  Call  onItemClick Method inside CustomListViewAndroidExample Class ( See Below )****/


            }
        }
    }



    public class MyWebRequestReceiver extends BroadcastReceiver {

        public static final String PROCESS_RESPONSE = "it.neokree.example.mockedFragments.intent.action.PROCESS_ACTION";
        public String alpha;
        @Override
        public void onReceive(Context context, Intent intent) {
//            String error = intent.getStringExtra("error");
//            String  contact= intent.getStringExtra("contacts");
//            String  address= intent.getStringExtra("address");
//            String  gender= intent.getStringExtra("gender");

            ArrayList<Product> list= intent.getParcelableArrayListExtra("product3");
            // movieList=new ArrayList<Product>();

            movieList=list;

            Resources res =getResources();
            cardArrayAdapter = new CustomListAdapter(CustomListView.getActivity(), movieList,res);
            listView.setAdapter(cardArrayAdapter);




            //    adapter = new CustomListAdapter(CustomListView.getActivity(), movieList,res);
            //       listView.setAdapter(adapter);

            // requestData("http://192.168.1.50:7777/fucker/v1/profile",responseString);

            //setUsername(responseString);
            //   setUserEmail(reponseMessage);

        }


    }



}
