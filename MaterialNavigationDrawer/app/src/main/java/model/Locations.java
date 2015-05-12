package model;

/**
 * Created by TecKNork on 3/31/2015.
 */
public class Locations {
    private double latitude;

    private double langitude;
    private String name;
    private double distance;

    public Locations(double lat, double lan, String name, double dist) {
        this.langitude = lat;
        this.langitude = lan;
        this.name = name;
        this.distance = dist;


    }

    public double getLatitude() {
        return langitude;
    }

    public double getLangitude() {

        return langitude;
    }

    public String getName() {

        return name;

    }

    public double getDistance() {
        return distance;
    }

}