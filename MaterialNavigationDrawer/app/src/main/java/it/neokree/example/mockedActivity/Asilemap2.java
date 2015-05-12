package it.neokree.example.mockedActivity;

/**
 * Created by TecKNork on 4/1/2015.
 */

import android.app.Activity;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.aisle411.mapsdk.map.CustomRouteOverlay;
import com.aisle411.mapsdk.map.DirectionOverlay;
import com.aisle411.mapsdk.map.ItemizedOverlay;
import com.aisle411.mapsdk.map.MapBundle;
import com.aisle411.mapsdk.map.MapBundleParser;
import com.aisle411.mapsdk.map.MapController;
import com.aisle411.mapsdk.map.MapUtils;
import com.aisle411.mapsdk.map.MapView;
import com.aisle411.mapsdk.map.PathOverlay;
import com.aisle411.mapsdk.map.Router;
import com.aisle411.mapsdk.shopping.Product;
import com.aisle411.mapsdk.shopping.ProductType;
import com.aisle411.mapsdk.shopping.Section;
import com.aisle411.mapsdk.shopping.ShoppingList;
import com.aisle411.mapsdk.shopping.ShoppingListOverlay;
import com.aisle411.mapsdk.shopping.SponsoredPin;
import com.aisle411.mapsdk.shopping.SponsoredPinsOverlay;
import com.com.web.talk.HttpManager;
import com.com.web.talk.RequestPackage;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

import it.neokree.example.R;
import it.neokree.example.mockedFragments.SearchProducts;
import model.Restroom;
import model.StoreProducts;

public class Asilemap2 extends  Activity{
    private ShoppingList shoppingList;
    private StoreProducts alpha;
    private MapBundle bundle;
    private MapView mapView;
    private Router router;
    List<SponsoredPin> pins2;
    private ToggleButton routeButton;
    private String message;
    private PathOverlay routeOverlay;
    private ShoppingListOverlay shoppingListOverlay;
    private CustomRouteOverlay customRouteOverlay;
    private SponsoredPinsOverlay sponsoredPinsOverlay;
    private ItemizedOverlay<Restroom> restroomsOverlay;
    String StoreID2;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
          pins2= new LinkedList<SponsoredPin>();
        mapView = new MapView(this);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        String StoreID = prefs.getString("storeID2",null);
        message = StoreID;
        alpha=new StoreProducts();
        alpha=(StoreProducts) getIntent().getParcelableExtra("product");
//        routeButton = new ToggleButton(this);
//        routeButton.setTextOff("Route");
//        routeButton.setTextOn("Route");
//        routeButton.setText("Route");
//        routeButton.setOnCheckedChangeListener(new OnCheckedChangeListener() {
//
//
//            public void onCheckedChanged(CompoundButton buttonView,
//                                         boolean isChecked) {
//                customRouteOverlay.setEnabled(isChecked);
//                mapView.invalidate();
//            }
//        });

        LinearLayout container = new LinearLayout(this);
        container.setOrientation(LinearLayout.VERTICAL);
        container.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT,
                LayoutParams.FILL_PARENT));
//        container.addView(routeButton);
        container.addView(mapView);
        container.setBackgroundColor(Color.WHITE);

        setContentView(container);
        startLoader();
        SharedPreferences prefs2 = PreferenceManager.getDefaultSharedPreferences(this);
        StoreID2=prefs2.getString("storeID2","failed");

    }

    private void onMapBundleLoaded() {
		/* Loading the map to display */
        mapView.setBundle(bundle);

        final MapController mapContoller = mapView.getMapContoller();
        mapContoller.setCompassEnabled(false);

		/*
		 * Adding routeOverlay as a first overlay, to make sure that over
		 * overlays are displayed on top of the routeOverlay. Calculation of
		 * the route and its display will take place inside
		 * startRouteCalculation().
		 */
        routeOverlay = new PathOverlay();
        mapView.addOverlay(routeOverlay);

		/* Adding an overlay to display a route through
		 * 2 arbitrary user-selected points */
        customRouteOverlay = new CustomRouteOverlay();
        customRouteOverlay.setEnabled(false);
        mapView.addOverlay(customRouteOverlay);

		/* Adding a DirectionOverlay. */
        DirectionOverlay directionOverlay = new DirectionOverlay();
        directionOverlay.setDrawable(getResources()
                .getDrawable(R.drawable.icon));
        directionOverlay.setPoint(bundle.findById(1));
        mapView.addOverlay(directionOverlay);

		/* Adding a restrooms overlay */
//		restroomsOverlay = new ItemizedOverlay<Restroom>();
//		restroomsOverlay.setClickable(false);
//		restroomsOverlay.setDrawable(getResources().getDrawable(
//		        R.drawable.wc_pin));
//		mapView.addOverlay(restroomsOverlay);
////		invalidateRestrooms();

		/* Adding a sponsored pin drops overlay. */
		sponsoredPinsOverlay = new SponsoredPinsOverlay(mapView) {

            @Override
			protected void onCalloutTap(final int index) {
				/* Handling the taps on the callout */
				String message = "Here you can start sponsored pin details activity.";
				Toast.makeText(Asilemap2.this, message,
				        Toast.LENGTH_LONG).show();
			}
		};
		/* Setting the graphical resources for the call-out */
		sponsoredPinsOverlay.setBackgroundDrawable(getResources().getDrawable(R.drawable.callout_back));
		sponsoredPinsOverlay.setChevronDrawable(getResources().getDrawable(R.drawable.callout_chevron));
		/* Setting the graphical resources for pins */
		sponsoredPinsOverlay.setDrawable(getResources().getDrawable(R.drawable.straight_pin), SponsoredPinsOverlay.STRAIGHT_PIN);
		sponsoredPinsOverlay.setDrawable(getResources().getDrawable(R.drawable.left_pin), SponsoredPinsOverlay.LEFT_PIN);
		sponsoredPinsOverlay.setDrawable(getResources().getDrawable(R.drawable.right_pin), SponsoredPinsOverlay.RIGHT_PIN);
		sponsoredPinsOverlay.setDefaultPinImage(getResources().getDrawable(R.drawable.default_pin_image));
		mapView.addOverlay(sponsoredPinsOverlay);

		mapView.invalidate();

		/*
		 * Adding information about the products from the shopping list.
		 * These products will be displayed on the map, after the shopping
		 * list route s constructed in the
		 * startRouteCalculation() function.
		 */
        shoppingListOverlay = new ShoppingListOverlay() {

            @Override
            protected void onOptionChanged(int index, int option,
                                           boolean checked) {
                super.onOptionChanged(index, option, checked);
                //find the selected product object
                Product selectedProduct = getProducts(index).get(option);
                //make the compound id of the product containing the product name, product id and sub-location id
                String id = selectedProduct.getName() + "-" + selectedProduct.getId() + "-" + getSection(index, selectedProduct).getSublocation();
				/* Adding a message about the selected products. */
                String message = "Product " + id
                        + (checked ? " checked" : " unchecked");
                Toast.makeText(Asilemap2.this, message,
                        Toast.LENGTH_LONG).show();
            }

            @Override
            protected void onCalloutTap(int index) {
				/* Handling the taps on the callout */
                String message = "Here you can start details activity.";
                Toast.makeText(Asilemap2.this, message,
                        Toast.LENGTH_LONG).show();

            }
        };

		/* Setting the graphical resources for the call-out */
        shoppingListOverlay.setDrawable(getResources().getDrawable(
                R.drawable.product_pin));
        shoppingListOverlay.setCompletedSectionDrawable(getResources().getDrawable(R.drawable.bought_product_pin));
        shoppingListOverlay.setBackgroundDrawable(getResources().getDrawable(
                R.drawable.callout_back));
        shoppingListOverlay.setChevronDrawable(getResources().getDrawable(
                R.drawable.callout_chevron));
        shoppingListOverlay.setCheckBoxDrawable(R.drawable.shopping_list_item);

        mapView.addOverlay(shoppingListOverlay);

		/* Initialization of the router object responsible for calculation of
		 * the shopping list route */
 //       router = mapView.getRouter();
		/* Calculation of the route*/
        //startRouteCalculation();
        try {
            requestData(getResources().getString(R.string.localhost)+"rest3/v1/sponsoredproducts/" +StoreID2);
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (TimeoutException e) {
            e.printStackTrace();
        }
		/*
		 * Passing null in place of the Route object enables the mode
		 * in which the route is not displayed and pins for all product
		 * locations are displayed on the map.
		 */
        shoppingListOverlay.setShoppingList(shoppingList, null);
    }

    private void startRouteCalculation() {
        new AsyncTask<Void, Void, Void>() {

            @Override
            protected Void doInBackground(Void... params) {

//                    MapPoint start = bundle.getAllEntrances().get(0);
//                    MapPoint finish = bundle.getAllExits().get(0);
//
//                    List<MapPoint> route = router.calculate(start, finish,
//                            shoppingList.getCurrentRouterHelper());
//                    routeOverlay.setRoute(route);



		//		shoppingListOverlay.setShoppingList(shoppingList, route);

			sponsoredPinsOverlay.setPins(pins2);

                return null;
            }

            @Override
            protected void onPostExecute(Void result) {
                mapView.invalidate();
            }

        }.execute();
    }

    private void startLoader() {
        new AsyncTask<Void, Void, Exception>() {

            @Override
            protected Exception doInBackground(Void... params) {
                try {

                    File source = new File("/sdcard/com.aisle411/files/" + message + ".zip");
                    File target = new File("/sdcard/com.aisle411/files/cache/");
//					deleteDir(target);
//					target.mkdirs();
                    MapBundleParser parser = new MapBundleParser(source, target);
                    parser.decompress();
                    bundle = parser.parse();
                    shoppingList = getShoppingList();
                    return null;
                } catch (Exception e) {
                    e.printStackTrace();
                    return e;
                }
            }

            @Override
            protected void onPostExecute(Exception exception) {
                if (exception == null) {
                    onMapBundleLoaded();
                } else {
                    Toast.makeText(Asilemap2.this,
                            "Problem: " + exception.getMessage(),
                            Toast.LENGTH_LONG).show();
                }
            }

        }.execute();
    }

//	private void invalidateRestrooms() {
//		restroomsOverlay.clear();
//
//		Set<Restroom> restrooms = getRestrooms();
//		for (Restroom restroom : restrooms) {
//			restroomsOverlay.add(restroom);
//		}
//
//		mapView.invalidate();
//	}

//	private Set<Restroom> getRestrooms() {
//		Set<Restroom> restrooms = new HashSet<Restroom>();
//		restrooms.add(new Restroom(MapUtils.getPointFromBundle(bundle, 10115,
//		        null, null)));
//		restrooms.add(new Restroom(MapUtils.getPointFromBundle(bundle, 10120,
//		        null, null)));
//		return restrooms;
//	}

	private List<SponsoredPin> getSponsoredPins() {
		List<SponsoredPin> pins = new LinkedList<SponsoredPin>();

		SponsoredPin pin1 = new SponsoredPin();
		pin1.setCalloutImage(getResources().getDrawable(R.drawable.icon));
		pin1.setPinDrawable(getResources().getDrawable(R.drawable.green_pin_image));
		pin1.setCalloutTitle("Sponsored Item 1");
		pin1.addProduct(getProduct(1, "Test Product 1", new Section[] {}));
		pin1.addProduct(getProduct(2, "Test Product 2", new Section[] {}));
		pin1.setPoint(MapUtils.getPointFromBundle(bundle, 21686151, null, null));
		pins.add(pin1);

		SponsoredPin pin2 = new SponsoredPin();
		pin2.setCalloutTitle("Sponsored Item 2");
		pin2.addProduct(getProduct(3, "Test Product 3", new Section[] {}));
		pin2.addProduct(getProduct(4, "Test Product 4", new Section[] {}));
		pin2.addProduct(getProduct(5, "Test Product 5", new Section[] {}));
		pin2.setPoint(MapUtils.getPointFromBundle(bundle, 21686152, null, null));
		pins.add(pin2);

		return pins;
	}

    private ShoppingList getShoppingList() {
        ShoppingList sl = new ShoppingList();

		Section commonSection = getSection(alpha.sectionid, 102976, 622867, alpha.prodname);
		sl.add(getProduct(1,alpha.prodname, new Section[] {
		        commonSection
		}));


//		sl.add(getProduct(3, "Test Product 3", new Section[] {
//		        getSection(10143, 102978, 622895, "Frozen Ethnic"),
//		        getSection(10156, 102979, 622911, "Sub location 1967"),
//		        getSection(10157, 102980, 622920, "Organic")
//		}));

        return sl;
    }

    private Product getProduct(int productId, String productName, Section[] sections) {
        Product product = new Product();
        product.setId(productId);
        product.setName(productName);
        product.setType(ProductType.GENERIC);
        for (Section section : sections) {
            product.addSection(section);
        }

        return product;
    }

    private Section getSection(int mapId, int locationId, int sublocationId, String sectionTitle) {
        Section section = new Section();
        section.setSublocation(sublocationId);
        section.setLocation(locationId);
        section.setTitle(sectionTitle);
        section.setMapId(mapId);
        section.setPoints(MapUtils.getPointsFromBundle(bundle, mapId, locationId, sublocationId));
        return section;
    }

    private static boolean deleteDir(File path) {
        if (path.exists()) {
            if (path.isDirectory()) {
                File[] files = path.listFiles();
                for (int i = 0; i < files.length; i++) {
                    if (files[i].isDirectory()) {
                        deleteDir(files[i]);
                    } else {
                        files[i].delete();
                    }
                }
            }
            return path.delete();
        }
        return false;
    }

    private void requestData(String uri) throws ExecutionException, InterruptedException, TimeoutException {

        RequestPackage p = new RequestPackage();
        p.setMethod("GET");
        p.setUri(uri);
//        p.setParam("email",email );
//        p.setParam("password",password);

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
          //  pDialog.setMessage("Logging in ...");
            //showDialog();

        }

        @Override
        protected String doInBackground(RequestPackage... params) {
            String content = HttpManager.getData(params[0]);
            return content;
        }

        @Override
        protected void onPostExecute(String result) {
    //        hideDialog();

            try {
                JSONObject jObj = new JSONObject(result);
             //   boolean error = jObj.getBoolean("error");


                JSONArray jArray = jObj.getJSONArray("products");
                // Check for error node in json
                for (int i=0; i < jArray.length(); i++)
                {
                    try {
                        JSONObject oneObject = jArray.getJSONObject(i);
                        // Pulling items from the array

                        SponsoredPin pin2 = new SponsoredPin();
                        pin2.setCalloutTitle(oneObject.getString("prod_des"));
                        pin2.addProduct(getProduct(1,oneObject.getString("prod_name"), new Section[] {}));
                        pin2.setPoint(MapUtils.getPointFromBundle(bundle,oneObject.getInt("sectionid"), null, null));
                        pins2.add(pin2);
//                        product.setName(oneObject.getString("productname"));
//                        product.setImage(oneObject.getString("imagelink"));
//                        product.setPrice(oneObject.getString("price"));
//                        list.add(product);
                    } catch (JSONException e) {
                        // Oops
                    }
                }


            } catch (JSONException e) {
                // JSON error
                e.printStackTrace();
            }




//           sponsoredPinsOverlay.setPins(pins2);
//            SponsoredPin pin1 = new SponsoredPin();
//            pin1.setCalloutImage(getResources().getDrawable(R.drawable.icon));
//            pin1.setPinDrawable(getResources().getDrawable(R.drawable.green_pin_image));
//            pin1.setCalloutTitle("Sponsored Item 1");
//            pin1.addProduct(getProduct(1, "Test Product 1", new Section[] {}));
//            pin1.addProduct(getProduct(2, "Test Product 2", new Section[] {}));
//            pin1.setPoint(MapUtils.getPointFromBundle(bundle, 21686151, null, null));
//            pins2.add(pin1);
           startRouteCalculation();
            Log.d("heelo", "Login Response: " + result.toString());



            // updateDisplay(result);

        }

    }



}
