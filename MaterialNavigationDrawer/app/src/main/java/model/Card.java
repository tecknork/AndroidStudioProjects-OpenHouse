package model;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by TecKNork on 3/24/2015.
 */
public class Card implements Parcelable {
    private String line1;
    private Double line2;
    List<String> images;
    String website;
    Double price;
    String name;
    String company;

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public Card(String line1, Double line2) {
        this.line1 = line1;
        this.line2 = line2;
        this.company="vmart.com";
        this.website="https://www.vmart.com";
        this.name="JapaniThela";
        this.price=12.4;
        this.images=new ArrayList<String>();

//        this.images.add(1,"https://cms-assets.tutsplus.com/uploads/users/21/posts/19431/featured_image/CodeFeature.jpg");
        this.images.add(0,"http://www.vmart.pk/images/thumbnails/280/186/product/4/Mk-690.jpg");

    }

    public Card()  {
        company="vmart.com";
        website="https://www.vmart.com";
        name="JapaniThela";
        price=12.4;

        this.images=new ArrayList<String>();
       // this.images.add(1,"https://cms-assets.tutsplus.com/uploads/users/21/posts/19431/featured_image/CodeFeature.jpg");
        this.images.add(0,"http://www.vmart.pk/images/thumbnails/280/186/product/4/Mk-690.jpg");

    }

    public List<String> getImages() {
        return images;
    }

    public void setImages(List<String> images) {
        this.images = images;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    public String getLine1() {
        return line1;
    }

    public Double getLine2() {
        return line2;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(line1);
        dest.writeString(website);
        dest.writeString(name);
        dest.writeString(company);
        dest.writeDouble(line2);
        dest.writeDouble(price);
        dest.writeStringList(images);

    }

    public static final Parcelable.Creator<Card> CREATOR = new Parcelable.Creator<Card>() {
        public Card createFromParcel(Parcel in) {
            return new Card(in);
        }

        public Card[] newArray(int size) {
            return new Card[size];
        }
    };

    // example constructor that takes a Parcel and gives you an object populated with it's values
    private Card(Parcel in) {
        images=new ArrayList<String>();
        line1=in.readString();
        line2=in.readDouble();
        in.readStringList(images);
        website=in.readString();
        price=in.readDouble();
        name=in.readString();
        company=in.readString();

    }
}