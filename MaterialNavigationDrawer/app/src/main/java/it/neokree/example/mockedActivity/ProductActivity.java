package it.neokree.example.mockedActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutCompat;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

import it.neokree.example.R;
import lib.ZoomOutPageTransformer;
import model.Product;

/**
 * Created by TecKNork on 3/24/2015.
 */
public class ProductActivity  extends Activity{

    ViewPager imagesPager;
     ViewGroup thumbnails;
     TextView productNameView;
     TextView companyNameView;
    TextView priceView;
     TextView quantityView;
     TextView descriptionView;
    ImageView image;
    Button buy;
    private Product prod;
    private ImagePagerAdapter adapter;


    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product);
        image=(ImageView) findViewById(R.id.image2);
       //  imagesPager=(ViewPager) findViewById(R.id.view_pager);
          thumbnails=(ViewGroup) findViewById(R.id.thumbnails);
          productNameView=(TextView) findViewById(R.id.product_name);
        buy=(Button) findViewById(R.id.buttonbuy);
         companyNameView=(TextView) findViewById(R.id.company_name);
          priceView=(TextView) findViewById(R.id.price);
         quantityView=(TextView) findViewById(R.id.quantity);

          descriptionView=(TextView) findViewById(R.id.description);



        prod = (Product) getIntent().getParcelableExtra("product");

      //  card=new Card();

     //   initAdapter();

        buy.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_VIEW);
                intent.addCategory(Intent.CATEGORY_BROWSABLE);
                intent.setData(Uri.parse(prod.getProducturl()));
                startActivity(intent);
            }
        });

       initImages();
        setProductText();
        setBrandText();
        setPriceText();
        setQuantityText();
        setDescriptionText();
    }





    private void initImages() {
       // imagesPager.setAdapter(adapter);
       // imagesPager.setPageTransformer(true, new ZoomOutPageTransformer());
        //int size = getResources().getDimensionPixelSize(R.dimen.product_thumbnails_image_size);

        String images = prod.getImage();
//        if (images != null) {
//            for (int i = 0; i < images.size(); i++) {
//                generateThumbnail(url, i, size);
//            }
//        }
        String url = images;
//
        Picasso.with(this).load(url).fit().centerInside().into(image);
    }

    private void generateThumbnail(String url, final int position, int size) {
        ImageView imageView = new ImageView(this);
        imageView.setLayoutParams(new LinearLayoutCompat.LayoutParams(size, size));
        imageView.setBackgroundResource(R.drawable.thumbnail_bg);
        Picasso.with(this).load(url).fit().centerInside().into(imageView);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                imagesPager.setCurrentItem(position, true);
            }
        });
        thumbnails.addView(imageView);
    }

    private void setDescriptionText() {
        String description = null;
        if (description == null) {
            descriptionView.setVisibility(View.GONE);
        } else {
            descriptionView.setText(description);
        }
    }

    private void setQuantityText() {
        Integer quantity = null;
        if (quantity != null) {
            quantityView.setText(getString(R.string.product_quantity, quantity));
        }
    }

    private void setProductText() {
        productNameView.setText(prod.getName());
    }

    private void setPriceText() {
        String price = prod.getPrice();
        if (price == null) {
            priceView.setVisibility(View.GONE);
        } else {
            priceView.setText(price);
        }
    }

    private void setBrandText() {
        String companyName = prod.getWebsite();

        if (companyName == null) {
            companyNameView.setVisibility(View.GONE);
        } else {
            String link = prod.getWebsite();
            if (link == null) {
                link = "#";
            }

            companyNameView.setText(Html.fromHtml(
                    getString(R.string.product_company_name, companyName, link)));

            companyNameView.setMovementMethod(LinkMovementMethod.getInstance());
        }
    }


    static class ImagePagerAdapter extends PagerAdapter {
        private List<String> urls;

        @Override public int getCount() {
            if (urls == null) {
                return 0;
            }

            return urls.size();
        }

        @Override public Object instantiateItem(ViewGroup container, int position) {
            Context context = container.getContext();
            ImageView imageView = new ImageView(context);
            imageView.setBackgroundResource(android.R.color.white);
            Picasso.with(context).load(urls.get(position)).fit().centerInside().into(imageView);
            container.addView(imageView);
            return imageView;
        }

        @Override public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }

        @Override public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        public void setUrls(List<String> urls) {
            this.urls = urls;
        }
    }

}
