package it.neokree.example.gps;

/**
 * Created by MUS on 4/4/15.
 */
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Parcelable;
import android.support.v7.internal.widget.AdapterViewCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import java.util.AbstractCollection;
import java.util.ArrayList;

import it.neokree.example.R;
import it.neokree.example.mockedActivity.CheckIn;
import it.neokree.example.mockedActivity.CheckIn2;

public class CustomListAdapter extends BaseAdapter implements View.OnClickListener{

    private  Activity context;
    private  String[] shopname;
    private Integer [] img;
    private double [] addressShop;
    private LayoutInflater inflater=null;
    private ArrayList<Double> dist_list;
    public ArrayList<location> myList;
    public ListView listView;



    public CustomListAdapter (Activity a, String [] name, Integer [] image, ArrayList<Double> list, ArrayList<location> my_list )
    {

        context = new Activity();
        context=a;
        this.shopname= name;
        this.img= image;
        dist_list=list;
        myList=my_list;



        inflater = ( LayoutInflater )context.
                getSystemService(Context.LAYOUT_INFLATER_SERVICE);


    }

    public class ViewHolder{

        public TextView text;
        public TextView text1;
        public ImageView image;

    }



    public int getCount() {
        return shopname.length;
    }


    public Object getItem(int location) {
        return dist_list.get(location);
    }


    public long getItemId(int position) {
        return position;
    }


    public class Listview extends Activity{

        public  Listview()
        {}


    }



    public View getView(int position,View view,ViewGroup parent) {
        Listview l1= new Listview();
        View v1= view;
        ViewHolder holder;
        LayoutInflater inflater=context.getLayoutInflater();
        View rowView=inflater.inflate(R.layout.list_view, null,true);

        TextView txtTitle = (TextView) rowView.findViewById(R.id.item);
        ImageView imageView = (ImageView) rowView.findViewById(R.id.icon);
        TextView distance  = (TextView) rowView.findViewById(R.id.distance);

        txtTitle.setText(shopname[position]);
        imageView.setImageResource(img[position]);
        distance.setText("Distance : "+ dist_list.get(position) + " km .");

        rowView.setOnClickListener(new OnItemClickListener(position, this));
        return rowView;





    };




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

            String inputShop = shopname[mPosition];

            Intent intent = new Intent(context, CheckIn2.class);
            //intent.putExtra("Shop Name" , inputShop );
            context.startActivity(intent);
            context.finish();
            /****  Call  onItemClick Method inside CustomListViewAndroidExample Class ( See Below )****/
        }



    }




}