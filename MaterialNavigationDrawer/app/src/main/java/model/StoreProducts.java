package model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by TecKNork on 4/7/2015.
 */
public class StoreProducts implements Parcelable{


   public String prodname;
   public int sectionid;



    public StoreProducts(){

    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(prodname);
        dest.writeInt(sectionid);
    }


    public static final Parcelable.Creator<StoreProducts> CREATOR = new Parcelable.Creator<StoreProducts>() {
        public StoreProducts createFromParcel(Parcel in) {
            return new StoreProducts(in);
        }

        public StoreProducts[] newArray(int size) {
            return new StoreProducts[size];
        }
    };

    private StoreProducts(Parcel in) {
        prodname=in.readString();
       sectionid=in.readInt();
    }
}
