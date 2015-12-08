package com.megamin.twitterfmf.magentodemo;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ListView;
import android.widget.Toast;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Vector;

/**
 * Created by 3140 on 03-12-2015.
 */
public class ProductDetailsActivity extends AppCompatActivity {

    private static final String NAMESPACE = "urn:Magento";
    private static final String URL = "http://4buy.in/api/soap";
    private SoapObject response_global;
    String value;
    ListView listView;
    ProductAdapter productAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle("Product Info");
        Bundle extras = getIntent().getExtras();
        listView = (ListView) findViewById(R.id.list_produts);
        if (extras != null) {
            value = extras.getString("key");
        }
        new AsynPost().execute();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    ProgressDialog pd;

    void showProgressDialog() {
        pd = new ProgressDialog(ProductDetailsActivity.this);
        pd.setMessage("loading..");
        pd.show();
    }


    class AsynPost extends AsyncTask<Void, Void, Void> {


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showProgressDialog();
        }

        @Override
        protected Void doInBackground(Void... params) {


            SoapSerializationEnvelope env = new SoapSerializationEnvelope(
                    SoapEnvelope.VER11);
            env.dotNet = false;
            env.xsd = SoapSerializationEnvelope.XSD;
            env.enc = SoapSerializationEnvelope.ENC;

            SoapObject request = new SoapObject(NAMESPACE, "call");
            request.addProperty("sessionId", MainActivity.sessionId);


            request.addProperty("resourcePath", "catalog_product.info");
            request.addProperty("args", value);
            // request.addProperty("resourcePath", "catalog_product.list");
            env.setOutputSoapObject(request);

            HttpTransportSE androidHttpTransport = new HttpTransportSE(URL);
            try {
                try {
                    androidHttpTransport.call("", env);

                    //for product details
                    response_global = (SoapObject) env.getResponse();

                    //for prodcuts
                    // vector = (Vector<Object>) env.getResponse();


                } catch (IOException e) {
                    e.printStackTrace();
                }


            } catch (XmlPullParserException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if (pd.isShowing()) {


                pd.dismiss();
            }

            if (!isOnline()) {
                if (pd.isShowing()) {

                    pd.dismiss();
                }
                Toast.makeText(getApplicationContext(), "Please check yout network connection.", Toast.LENGTH_LONG).show();
                return;
            }
            ArrayList<ArrayList<ProductModel>> productModelArrayList_Array = new ArrayList<>();
            ArrayList<ProductModel> productModelArrayList = new ArrayList<>();
            // for product details

            if (response_global == null) {
                Toast.makeText(getApplicationContext(), "Please check yout network connection.", Toast.LENGTH_LONG).show();
                return;
            }
            int totalCount = response_global.getPropertyCount();
            for (int detailCount = 0; detailCount < totalCount; detailCount++) {
                ProductModel productModel = new ProductModel();
                Object objectResponse = (Object) response_global.getProperty(detailCount);
                SoapObject r = (SoapObject) objectResponse;
                String key1 = (String) r.getPropertySafelyAsString("key").toString();
                String value = (String) r.getPropertySafelyAsString("value").toString();
                productModel.setKey(key1);
                productModel.setValue(value);
                // text = text + "\n" + "---  " + key1 + "   --->  " + value;
                //textView.setText(text);
                productModelArrayList.add(productModel);
            }
            productModelArrayList_Array.add(productModelArrayList);
            productAdapter = new ProductAdapter(getApplicationContext(), productModelArrayList_Array);
            listView.setAdapter(productAdapter);
        }
    }


    public boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }
}
