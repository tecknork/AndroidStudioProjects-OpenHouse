package it.neokree.example.gps;


import android.app.Activity;
import android.app.ListActivity;
import android.app.ListFragment;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.ListAdapter;

import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.Collections;

import it.neokree.example.R;

public class MapsActivity extends FragmentActivity  {


    ListView list;
    //CustomAdapter adapter;
    public MapsActivity  CustomListView = null;
    public  ArrayList<ListMode> CustomListViewValuesArr = new ArrayList<ListMode>();


    private GoogleMap mMap; // Might be null if Google Play services APK is not available.
    double lat,lng;
    double dist=0;
    private Marker mMarkerA;
    private Marker mMarkerB;
    CalculateDistance c1;
    LatLng myLatLng, newLatLng;
    private LocationManager locMan;
    location l1;


    Integer image_id[] = {R.drawable.mm, R.drawable.img, R.drawable.mcc, R.drawable.dw, R.drawable.dw, R.drawable.img
    };

    double[] locLatitude= {33.639991,33.6848,33.6900558,33.683345,33.708864,33.66902};
    double [] locLangitude= {73.0254,72.986474,73.0308276,72.989001,73.055579,73.07563};
    ArrayList <Double> distance = new ArrayList<>();

    String [] loc_name = {"Metro I 11","Sauda Saulf","Madina C&C","D-Watson F 11","D-Watson Blue Area", "Best Buy I8"};
    ArrayList my_list = new ArrayList<location>();

    //for (int i =0 ; i <4 ; i ++)

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

         setContentView(R.layout.activity_maps);
         setUpMapIfNeeded();

          for (int i =0 ; i < loc_name.length; i++)
         {
             location a = new location(locLatitude[i],locLangitude[i],loc_name[i],distance.get(i));
             my_list.add(a);

         }

         list = (ListView) findViewById(R.id.listView);
         CustomListAdapter  adapter=new CustomListAdapter(this,loc_name,image_id,distance,my_list );
         list.setAdapter(adapter);


      /*  setContentView(R.layout.activity_maps);
        adapter = new CustomListView(this,image_id, loc_name);
        ListView lv = (ListView) findViewById(R.id.listView);
        lv.setAdapter(adapter);
        */



       // Collections.sort(distance);



    }




    @Override
    protected void onResume() {
        super.onResume();
        setUpMapIfNeeded();
    }

    /**
     * Sets up the map if it is possible to do so (i.e., the Google Play services APK is correctly
     * installed) and the map has not already been instantiated.. This will ensure that we only ever
     * call {@link #setUpMap()} once when {@link #mMap} is not null.
     * <p/>
     * If it isn't installed {@link SupportMapFragment} (and
     * {@link com.google.android.gms.maps.MapView MapView}) will show a prompt for the user to
     * install/update the Google Play services APK on their device.
     * <p/>
     * A user can return to this FragmentActivity after following the prompt and correctly
     * installing/updating/enabling the Google Play services. Since the FragmentActivity may not
     * have been completely destroyed during this process (it is likely that it would only be
     * stopped or paused), {@link #onCreate(Bundle)} may not be called again so we should call this
     * method in {@link #onResume()} to guarantee that it will be called.
     */
    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
                    .getMap();
            // Check if we were successful in obtaining the map.
            if (mMap != null) {
               // setUpMap();
                updatePlaces();
            }
        }
    }

    /**
     * This is where we can add markers or lines, add listeners or move the camera. In this case, we
     * just add a marker near Africa.
     * <p/>
     * This should only be called once and when we are sure that {@link #mMap} is not null.
     */
    private void setUpMap() {
        mMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
        LatLng sydney = new LatLng(-33.867, 151.206);
        mMap.setMyLocationEnabled(true);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Your location"));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney, 13));
        mMap.animateCamera(CameraUpdateFactory.newLatLng(sydney), 3000, null);
    }


    private void updatePlaces(){

         locMan = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
         Location lastLoc = locMan.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
         lat = lastLoc.getLatitude();
         lng = lastLoc.getLongitude();
         //lat=33.688975;
       // lng=72.986499;
         myLatLng = new LatLng(lat,lng);
         mMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
         mMap.setMyLocationEnabled(true);
         mMap.addMarker(new MarkerOptions().position(myLatLng).title("Your location ").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
      //  mMarkerA = getMap().addMarker(new MarkerOptions().position(new LatLng(-33.9046, 151.155)).draggable(true));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(myLatLng, 11));
        mMap.animateCamera(CameraUpdateFactory.newLatLng(myLatLng), 3000, null);

        for (int j =0; j < loc_name.length ; j++)
        {



            newLatLng = new LatLng(locLatitude[j],locLangitude[j]);
            dist = c1.calculateDistance(lat, lng , locLatitude[j], locLangitude[j]);
            dist = (double) Math.round(dist * 100) / 100;
            //double dist =getMyDistance(myLatLng,newLatLng);
            distance.add(dist);
          //  Collections.sort(distance);
        }

        for (int i=0 ; i < loc_name.length; i++)
        {
           addmark(locLatitude[i],locLangitude[i],loc_name[i], distance.get(i));
          // addpolyline(lat,lng, locLangitude[i], locLangitude[i]);

        }




    }


     private void addmark(double lat, double lan, String name, double dist )
     {


         LatLng newpositon = new LatLng(lat, lan);
         mMap.setMyLocationEnabled(true);

         mMap.addMarker(new MarkerOptions().position(newpositon).title(name).snippet("your distance is :" + dist + " km. " ).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE)));
        /* var marker = new google.maps.Marker({
                 map: map,
             position: new google.maps.LatLng(latitude, longitude),
             icon: pinSymbol("#FFF"),
         }); */
     }


   /* public double getMyDistance(LatLng LatLng1, LatLng LatLng2) {
        double distance = 0;
        Location locationA = new Location("");
        locationA.setLatitude(LatLng1.latitude);
        locationA.setLongitude(LatLng1.longitude);
        Location locationB = new Location("");
        locationB.setLatitude(LatLng2.latitude);
        locationB.setLongitude(LatLng2.longitude);
       // distance = locationA.distanceTo(locationB);
       // distance = SphericalUtil.computeDistanceBetween();
      /*  public static final int earthRadius = 6371;
        public static float calculateDistance(float lat1, float lon1, float lat2, float lon2)
        {
            float dLat = (float) Math.toRadians(lat2 - lat1);
            float dLon = (float) Math.toRadians(lon2 - lon1);
            float a =
                    (float) (Math.sin(dLat / 2) * Math.sin(dLat / 2) + Math.cos(Math.toRadians(lat1))
                            * Math.cos(Math.toRadians(lat2)) * Math.sin(dLon / 2) * Math.sin(dLon / 2));
            float c = (float) (2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a)));
            float d = earthRadius * c;
            return d;
        }
        return distance;

    }*/

    public void addpolyline(double mypostLat, double mypostlan, double newlat, double newlan)
    {

        Polyline line = mMap.addPolyline(new PolylineOptions()
                .add(new LatLng(mypostLat, mypostlan ), new LatLng(newlat, newlan))
                .width(5)
                .color(Color.RED));

    }





}
