package it.neokree.example.gps;


/**
 * Created by MUS on 3/28/15.
 */
public class location {
    private double latitude;
    private double langitude;
    private String name;
    private double distance;

     public location (double lat, double lan, String name, double dist)
     {

         super();
         this.latitude = lat;
         this.langitude = lan;
         this.name= name;
         this.distance= dist;


     }

     public double getLatitude()
    {
        return latitude;
    }

    public double getLangitude ()
    {

        return langitude;
    }

    public String getName()
    {

        return  name;

    }
     public  double getDistance()
     {
         return distance;
     }



//    public double compareTo(location loc_dist) {
//
//        double dist = ((location) loc_dist).getDistance();
//
//        //ascending order
//        return this.distance - dist;
//
//        //descending order
//        //return compareQuantity - this.quantity;
//
//    }

}
