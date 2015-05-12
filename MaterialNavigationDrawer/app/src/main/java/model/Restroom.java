package model;

/**
 * Created by TecKNork on 4/1/2015.
*/

import com.aisle411.mapsdk.map.MapPoint;

import android.graphics.drawable.Drawable;

import com.aisle411.mapsdk.map.MapPoint;
import com.aisle411.mapsdk.map.OverlayItem;

        import android.graphics.drawable.Drawable;

        import com.aisle411.mapsdk.map.MapPoint;
        import com.aisle411.mapsdk.map.OverlayItem;

public class Restroom implements OverlayItem {

    private MapPoint point;

    public Restroom(MapPoint point) {
        this.point = point;
    }


    public CharSequence getTitle() {
        return null;
    }


    public MapPoint getPoint() {
        return point;
    }


    public Drawable getDrawable() {
        return null;
    }

}

