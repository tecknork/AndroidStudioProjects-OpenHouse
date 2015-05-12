package it.neokree.example.mockedFragments;

/**
 * Created by TecKNork on 3/24/2015.
 */
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;

import it.neokree.example.R;
import model.Card;
import model.CardArrayAdapter;

public class CardListActivity extends Activity {

    private static final String TAG = "CardListActivity";
    private CardArrayAdapter cardArrayAdapter;
    private ListView listView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.listview);
        listView = (ListView) findViewById(R.id.card_listView);

        listView.addHeaderView(new View(this));
        listView.addFooterView(new View(this));

        cardArrayAdapter = new CardArrayAdapter(getApplicationContext(), R.layout.list_item_card);

        for (int i = 0; i < 10; i++) {
            Card card = new Card("Card " + (i+1) + " Line 1", 2.3);
            cardArrayAdapter.add(card);
        }
        listView.setAdapter(cardArrayAdapter);
    }
}