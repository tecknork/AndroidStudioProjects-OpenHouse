package it.neokree.example.mockedFragments;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Parcel;
import android.os.Parcelable;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.com.web.talk.HttpManager;
import com.com.web.talk.RequestPackage;
import com.login.helper.IntentUI;
import com.login.helper.ProfileUi;
import com.login.helper.SQLiteHandler;
import com.login.helper.SessionManager;
import com.shoppin.splash.Login;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import it.neokree.example.MainActivity;
import it.neokree.example.R;
import it.neokree.example.mockedActivity.ChangeAddress;
import it.neokree.example.mockedActivity.ChangeGender;
import it.neokree.example.mockedActivity.ChangePhone;
import it.neokree.example.mockedActivity.ProductActivity;
import it.neokree.example.mockedActivity.Shopingdialog;
import lib.RoundedImageView;
import model.Product;

/**
 * Created by TecKNork on 3/22/2015.
 */

public class Userprofile  extends Fragment {
    private static final int SELECT_PICTURE = 1;
    private static final int REQUEST_LIST_ITEM1 = 235;
    private static final int REQUEST_LIST_ITEM2 = 234;
    private static final int REQUEST_LIST_ITEM3 = 233;
    private MyWebRequestReceiver receiver;
    private String selectedImagePath;
    private SQLiteHandler db;
    private SessionManager session;
    private ListView listView;
    private RoundedImageView img;
    private CustomListAdapter adapter;
    private ArrayList<Prof> movieList = new ArrayList<Prof>();
    private TextView text;
    private Button logout;
    public  Userprofile CustomListView = null;
    public Userprofile(){

    }


    @Override
    public void onStart() {
        super.onStart();



    }

    @Override
    public void onCreate (Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);


        if (savedInstanceState != null) {
            ArrayList<Prof> values = savedInstanceState.getParcelableArrayList("myKey");
            if (values != null) {
                Resources res=getResources();
               // adapter = new CustomListAdapter(getActivity(), values,res);
            }

        }

    }





    @Override
    public View onCreateView(LayoutInflater inflater,@Nullable ViewGroup container,@Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.userprofile2, null, false);
        IntentFilter filter = new IntentFilter(MyWebRequestReceiver.PROCESS_RESPONSE);
        filter.addCategory(Intent.CATEGORY_DEFAULT);
        receiver = new MyWebRequestReceiver();
        getActivity().registerReceiver(receiver, filter);
        Intent msgIntent = new Intent(getActivity(), ProfileUi.class);
        getActivity().startService(msgIntent);
        db = new SQLiteHandler(getActivity().getApplicationContext());
        CustomListView=this;
        // session manager
        session = new SessionManager(getActivity().getApplicationContext());

        listView=null;
        logout=(Button) rootView.findViewById(R.id.button);
        listView = (ListView) rootView.findViewById(R.id.listView);
        text=(TextView) rootView.findViewById(R.id.myImageViewText);
        img=(RoundedImageView) rootView.findViewById(R.id.imageView);
        Resources res=getResources();
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String name2 = prefs.getString("ProfileName"," ");
        text.setText(name2);


        logout.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                logoutUser();
                //Do stuff here
            }
        });


        img.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {

                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN: {
                        ImageView view = (ImageView) v;
                        //overlay is black with transparency of 0x77 (119)
                        view.getDrawable().setColorFilter(0x77000000, PorterDuff.Mode.SRC_ATOP);
                        view.invalidate();
                        break;
                    }
                    case MotionEvent.ACTION_UP:
                    {
                        Intent intent = new Intent();
                        intent.setType("image/*");
                        intent.setAction(Intent.ACTION_GET_CONTENT);
                        startActivityForResult(Intent.createChooser(intent,"Select Picture"), SELECT_PICTURE);
                    }
                    case MotionEvent.ACTION_CANCEL: {
                        ImageView view = (ImageView) v;
                        //clear the overlay
                        view.getDrawable().clearColorFilter();
                        view.invalidate();
                        break;
                    }
                }

                return true;
            }
        });


//        if (!session.isLoggedIn()) {
//            logoutUser();
//        }

        return rootView;
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        getActivity().unregisterReceiver(receiver);
    }

    public void onSaveInstanceState(Bundle savedState) {

     super.onSaveInstanceState(savedState);

        // Note: getValues() is a method in your ArrayAdaptor subclass
       ArrayList values = adapter.getData();
        ///savedState.putStringArray("myKey", values);
     savedState.putParcelableArrayList("myKey", values);
    // savedState.

    }




    private void logoutUser() {
        session.setLogin(false);

        db.deleteUsers();
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getActivity());
        settings.edit().clear().commit();
        // Launching the login activity
        Intent intent = new Intent(getActivity(), Login.class);
        startActivity(intent);
        getActivity().finish();
    }

    class Prof implements Parcelable{
      public  String value;
        public String type;


        public Prof(){

        }
        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(value);
            dest.writeString(type);

        }


        public  final Parcelable.Creator<Prof> CREATOR = new Parcelable.Creator<Prof>() {
            public Prof createFromParcel(Parcel in) {
                return new Prof(in);
            }

            public Prof[] newArray(int size) {
                return new Prof[size];
            }
        };

        // example constructor that takes a Parcel and gives you an object populated with it's values
        private Prof(Parcel in) {
            value=in.readString();
            type=in.readString();

        }


    }
    class CustomListAdapter extends BaseAdapter implements View.OnClickListener {

        private Activity activity;
        private ArrayList<Prof> data;
        private  LayoutInflater inflater=null;
        public Resources res;
        Prof tempValues=null;
        int i=0;

//        public CustomListAdapter(Activity activity, List<Movie> movieItems) {
//           activity = activity;
//            this.movieItems = movieItems;
//        }


        public ArrayList<Prof> getData() {
            return data;
        }

        public void setData(ArrayList<Prof> data) {
            this.data = data;
        }

        public CustomListAdapter(Activity a, ArrayList d,Resources resLocal) {

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

        }


        @Override
        public int getCount() {
            if(data.size()<=0)
                return 1;
            return data.size();
        }


        @Override
        public Object getItem(int position) {
            return null;
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
                vi = inflater.inflate(R.layout.list_row, null);

                /****** View Holder Object to contain tabitem.xml file elements ******/

                holder = new ViewHolder();
                holder.text = (TextView) vi.findViewById(R.id.textView4);
                holder.text1 = (TextView) vi.findViewById(R.id.textView5);
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
                    tempValues = ( Prof ) data.get( position );

                    /************  Set Model values in Holder elements ***********/
                    holder.text.setText( tempValues.value );
                    holder.text1.setText( tempValues.type );


                    /******** Set Item Click Listner for LayoutInflater for each row *******/

                    vi.setOnClickListener(new OnItemClickListener( position ));
                }
            return vi;
        }



        @Override
        public void onClick(View v) {


        }

        private class OnItemClickListener  implements View.OnClickListener {
            private int mPosition;

            OnItemClickListener(int position){
                mPosition = position;
            }

            @Override
            public void onClick(View arg0) {

                if(mPosition==0) {
                    Intent intent = new Intent(getActivity(), ChangePhone.class);
                    startActivityForResult(intent, REQUEST_LIST_ITEM1);
                }

                if(mPosition==1) {
                    Intent intent = new Intent(getActivity(), ChangeAddress.class);
                    startActivityForResult(intent, REQUEST_LIST_ITEM2);
                }

                if(mPosition==2) {
                    Intent intent = new Intent(getActivity(), ChangeGender.class);
                    startActivityForResult(intent, REQUEST_LIST_ITEM3);
                }
                /****  Call  onItemClick Method inside CustomListViewAndroidExample Class ( See Below )****/


            }
        }
    }


    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == getActivity().RESULT_OK) {
            if (requestCode == SELECT_PICTURE) {
                Uri selectedImageUri = data.getData();
                selectedImagePath = getPath(selectedImageUri);
                System.out.println("Image Path : " + selectedImagePath);
                //File f = new File(selectedImageUri);
                img.setImageURI(selectedImageUri);

            }
        }

        if(requestCode == REQUEST_LIST_ITEM1 && resultCode == getActivity().RESULT_OK) {
            String address=data.getStringExtra("phone");
            movieList.get(0).value=address;
            Resources res =getActivity().getResources();
            adapter = new CustomListAdapter(CustomListView.getActivity(), movieList,res);
            listView.setAdapter(adapter);
        }

        if(requestCode == REQUEST_LIST_ITEM2 && resultCode == getActivity().RESULT_OK) {
            String address=data.getStringExtra("address");
            movieList.get(1).value=address;
            Resources res =getActivity().getResources();
            adapter = new CustomListAdapter(CustomListView.getActivity(), movieList,res);
            listView.setAdapter(adapter);
        }
        if(requestCode == REQUEST_LIST_ITEM3 && resultCode == getActivity().RESULT_OK) {
            String address=data.getStringExtra("gender");
            movieList.get(2).value=address;
            Resources res =getActivity().getResources();
            adapter = new CustomListAdapter(CustomListView.getActivity(), movieList,res);
            listView.setAdapter(adapter);
        }
    }

    public String getPath(Uri uri) {
        String[] projection = { MediaStore.Images.Media.DATA };
        Cursor cursor = getActivity().managedQuery(uri, projection, null, null, null);
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }


//    public String getRealPathFromURI(Uri contentUri) {
//        String res = null;
//        String[] proj = { MediaStore.Images.Media.DATA };
//        Cursor cursor = getActivity().getContentResolver().query(contentUri, proj, null, null, null);
//        if(cursor.moveToFirst()){;
//            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
//            res = cursor.getString(column_index);
//        }
//        cursor.close();
//        return res;
//    }

    private void requestData(String uri,String apikey) {

        RequestPackage p = new RequestPackage();
        p.setMethod("GET");
        p.setUri(uri);
        //HashMap<String, String> user = db.getUserDetails();

        p.setApi_key(apikey);
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
//            pDialog.setMessage("Logging in ...");
//            showDialog();

        }

        @Override
        protected String doInBackground(RequestPackage... params) {
            String content = HttpManager.getData(params[0]);
            return content;
        }

        @Override
        protected void onPostExecute(String result) {
            hideDialog();
            try {
                JSONObject jObj = new JSONObject(result);
                boolean error = jObj.getBoolean("error");

                // Check for error node in json
                if (!error) {
                    // user successfully logged in
                    // Create login session
                    session.setLogin(true);

                    String contact = jObj.getString("contacts");
                    String address = jObj.getString("address");
                    String gender = jObj.getString("gender");

                    Prof a=new Prof();
                    a.type="Phone";
                    a.value=contact;
                    movieList.add(0,a);
                    Prof b=new Prof();
                    b.type="Address";
                    b.value=address;
                    movieList.add(1,b);
                    Prof c=new Prof();
                    c.type="Gender";
                    c.value=gender;
                    movieList.add(2,c);
                    // Launch main activity

                } else {
                    // Error in login. Get the error message
                    String errorMsg = jObj.getString("error_msg");
                    Toast.makeText(getActivity().getApplicationContext(),
                            errorMsg, Toast.LENGTH_LONG).show();
                }
            } catch (JSONException e) {
                // JSON error
                e.printStackTrace();
            }

            Log.d("TAG", "Login Response: " + result.toString());



            // updateDisplay(result);

        }

    }





    public class MyWebRequestReceiver extends BroadcastReceiver {

        public static final String PROCESS_RESPONSE = "it.neokree.example.mockedFragments.intent.action.PROCESS_RESPONSE";
        public String alpha;
        @Override
        public void onReceive(Context context, Intent intent) {
           // boolean error = intent.getBooleanExtra("error",true);
            String  contact= intent.getStringExtra("contacts");
            String  address= intent.getStringExtra("address");
            String  gender= intent.getStringExtra("gender");
            movieList=new ArrayList<Prof>();
            Prof a=new Prof();
            a.type="Phone";
            a.value=contact;
            movieList.add(0,a);
            Prof b=new Prof();
            b.type="Address";
            b.value=address;
            movieList.add(1,b);
            Prof c=new Prof();
            c.type="Gender";
            c.value=gender;
            movieList.add(2,c);

            Resources res =getActivity().getResources();
            adapter = new CustomListAdapter(CustomListView.getActivity(), movieList,res);
            listView.setAdapter(adapter);

           // requestData("http://192.168.1.50:7777/fucker/v1/profile",responseString);

            //setUsername(responseString);
         //   setUserEmail(reponseMessage);

        }


    }



}


