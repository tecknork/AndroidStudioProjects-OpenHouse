package it.neokree.example.mockedActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import model.Product;
import it.neokree.example.R;

/**
 * Created by TecKNork on 3/27/2015.
 */
public class Shopingdialog extends Activity {

EditText name;
EditText name2;
 Button ok;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.float_shopping);
         name=(EditText)  findViewById(R.id.text0);
         name2=(EditText)  findViewById(R.id.text1);
          ok=(Button) findViewById(R.id.buttonok);



       ok.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
               sendResult();
           }

       });




    }


    private void sendResult() {
        Intent resultI = new Intent();
        Product alpha= new Product();
        alpha.setName(name.getText().toString());
        alpha.setPrice(name2.getText().toString());

        resultI.putExtra("newproduct", (Parcelable)alpha);
        setResult(RESULT_OK, resultI);
        finish();
    }

}
