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

import it.neokree.example.mockedActivity.ProductActivity;
import model.Product;

import com.login.helper.CrawlerUi;
import com.login.helper.ProfileUi;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import it.neokree.example.R;
import lib.CircleTransform;

/**
 * Created by TecKNork on 3/24/2015.
 */
public class ProductListActivity extends Fragment {
    private static final String TAG = "ProductListActivity";
    private CustomListAdapter cardArrayAdapter;
    private MyWebRequestReceiver receiver;
    private ListView listView;
    public ProductListActivity CustomListView= null;
    private ArrayList<Product> movieList = new ArrayList<Product>();


    @Override
    public View onCreateView(LayoutInflater inflater,@Nullable ViewGroup container,@Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.listview, null, false);
        CustomListView=this;
        Resources res =getResources();
        IntentFilter filter = new IntentFilter(MyWebRequestReceiver.PROCESS_RESPONSE);
        filter.addCategory(Intent.CATEGORY_DEFAULT);
        receiver = new MyWebRequestReceiver();
        getActivity().registerReceiver(receiver, filter);
        Intent msgIntent = new Intent(getActivity(), CrawlerUi.class);
        getActivity().startService(msgIntent);
        listView = (ListView) rootView.findViewById(R.id.card_listView);


//        for (int i = 0; i < 10; i++) {
//            Card card = new Card("Card " + (i+1) + " Line 1",2.2);
//            movieList.add(i, card);
//        }



    return rootView;
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        getActivity().unregisterReceiver(receiver);
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
                vi = inflater.inflate(R.layout.list_item_card, null);

                /****** View Holder Object to contain tabitem.xml file elements ******/

                holder = new ViewHolder();
                holder.text = (TextView) vi.findViewById(R.id.title);
                holder.text1 = (TextView) vi.findViewById(R.id.price);
                holder.image= (ImageView) vi.findViewById(R.id.photo);
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
                tempValues = ( Product ) data.get( position );

                /************  Set Model values in Holder elements ***********/
                if(tempValues!=null) {
                    holder.text.setText(tempValues.getName());
                    holder.text1.setText(tempValues.getPrice());
                    Picasso.with(activity)
                            .load(tempValues.getImage())
                            .fit()
                            .centerInside()
                            .transform(new CircleTransform())
                            .into(holder.image);
                    //    holder.text1.setText( "Beta" );
                }

                /******** Set Item Click Listner for LayoutInflater for each row *******/

                vi.setOnClickListener(new OnItemClickListener( position, this));
            }
            return vi;
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

            ArrayList<Product> list= intent.getParcelableArrayListExtra("product2");
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
