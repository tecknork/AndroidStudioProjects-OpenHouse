package it.neokree.example.gps;


/**
 * Created by MUS on 3/31/15.
 */
public class ListMode {
    private String ShopName;
    private double Distance;


    public void setShopName(String SName)
    {

         this.ShopName= SName;

    }

    public void setDistance(double distance)
    {
        this.Distance= distance;
    }


    public String getShopName() {
        return ShopName;
    }

    public double getDistance() {
        return Distance;
    }
}
