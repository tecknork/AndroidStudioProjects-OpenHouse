package model;

import android.media.Image;
import android.os.Parcel;
import android.os.Parcelable;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by TecKNork on 3/24/2015.
 */

public class Product implements Parcelable,Serializable {

    @SerializedName("image")
    String image;
    @SerializedName("name")
    String name;
    @SerializedName("price")
    String price;
    @SerializedName("producturl")
    String producturl;
    @SerializedName("website")
    String website;
//    String type;


    public Product() {
    }

    public Product(String image, String name, String price, String producturl, String website) {
        this.image = image;
        this.name = name;
        this.price = price;
        this.producturl = producturl;
        this.website = website;
    }

    public Product(String alpha, String beta, String dd, String ss) {


    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getProducturl() {
        return producturl;
    }

    public void setProducturl(String producturl) {
        this.producturl = producturl;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

        dest.writeString(name);
        dest.writeString(image);
        dest.writeString(producturl);
        dest.writeString(price);
        dest.writeString(website);
    }

    public static final Parcelable.Creator<Product> CREATOR = new Parcelable.Creator<Product>() {
        public Product createFromParcel(Parcel in) {
            return new Product(in);
        }

        public Product[] newArray(int size) {
            return new Product[size];
        }
    };


    private Product(Parcel in) {
        name=in.readString();
        image=in.readString();
        website=in.readString();
        price=in.readString();
       producturl=in.readString();

    }

    private void writeObject(java.io.ObjectOutputStream stream)
            throws java.io.IOException
    {
        // "Encrypt"/obscure the sensitive data
        stream.defaultWriteObject();
    }

    private void readObject(java.io.ObjectInputStream stream)
            throws java.io.IOException, ClassNotFoundException
    {
        stream.defaultReadObject();

        // "Decrypt"/de-obscure the sensitive data
       }

    @Override
    public String toString() {
        return "Product [image=" + image + ", name=" + name + ", price="
                + price + ", producturl=" + producturl  + ", website=" + website + "]";
    }


}

