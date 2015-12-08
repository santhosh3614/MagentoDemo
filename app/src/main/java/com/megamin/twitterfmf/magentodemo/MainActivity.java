package com.megamin.twitterfmf.magentodemo;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;
import org.w3c.dom.Text;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Vector;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {


    private static final String NAMESPACE = "urn:Magento";
    private static final String URL = "http://4buy.in/api/soap";
    public static String sessionId;
    ListView listView;
    ProductAdapter productAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        listView = (ListView) findViewById(R.id.list_produts);

        new AsyncTask() {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                showProgressDialog();
                ;
            }

            @Override
            protected Object doInBackground(Object[] params) {
                try {

                    SoapSerializationEnvelope env = new SoapSerializationEnvelope(
                            SoapEnvelope.VER11);
                    env.dotNet = false;
                    env.xsd = SoapSerializationEnvelope.XSD;
                    env.enc = SoapSerializationEnvelope.ENC;

                    SoapObject request = new SoapObject(NAMESPACE, "login");
                    request.addProperty("username", "naresh");
                    request.addProperty("apiKey", "naresh@4buy");
                    env.setOutputSoapObject(request);

                    HttpTransportSE androidHttpTransport = new HttpTransportSE(URL);
                    androidHttpTransport.call("", env);
                    Object result = env.getResponse();
                    Log.d("sessionId", result.toString());

                    sessionId = result.toString();


                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;
            }


            @Override
            protected void onPostExecute(Object o) {
                super.onPostExecute(o);
                if (pd.isShowing()) {

                    pd.dismiss();
                }
                new AsynPost().execute();
            }
        }.execute();


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent intent = new Intent(MainActivity.this, ProductDetailsActivity.class);

        intent.putExtra("key", arrayListArrayList.get(position).get(0).getValue());
        startActivity(intent);
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
            request.addProperty("sessionId", sessionId);

            request.addProperty("resourcePath", "catalog_product.list");
            env.setOutputSoapObject(request);

            HttpTransportSE androidHttpTransport = new HttpTransportSE(URL);
            try {
                try {
                    androidHttpTransport.call("", env);
                    vector = (Vector<Object>) env.getResponse();


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

            if (!isOnline()) {
                if (pd.isShowing()) {
                    pd.dismiss();
                }
                Toast.makeText(getApplicationContext(), "Please check yout network connection.", Toast.LENGTH_LONG).show();
                return;
            }
            if (vector == null) {
                if (pd.isShowing()) {
                    pd.dismiss();
                }
                Toast.makeText(getApplicationContext(), "Please check yout network connection.", Toast.LENGTH_LONG).show();
                return;
            }
            setTitle("Product List");
            parseSoapResponse(vector);

        }
    }

    String text = "";
    Vector<Object> vector;

    ArrayList<ArrayList<ProductModel>> arrayListArrayList = new ArrayList<>();

    //for prodcuts
    private void parseSoapResponse(Vector<Object> vector) {
        for (int i = 0; i < vector.size(); i++) {
            SoapObject object = (SoapObject) vector.get(i);
            if (object != null) {
                ArrayList<ProductModel> productModelArrayList = new ArrayList<>();
                for (int j = 0; j < object.getPropertyCount(); j++) {
                    ProductModel productModel = new ProductModel();
                    Object objectResponse = (Object) object.getProperty(j);
                    SoapObject r = (SoapObject) objectResponse;
                    String key1 = (String) r.getPropertySafelyAsString("key").toString();
                    String value = (String) r.getPropertySafelyAsString("value").toString();
                    productModel.setKey(key1);
                    productModel.setValue(value);
                    productModelArrayList.add(productModel);
                }
                arrayListArrayList.add(productModelArrayList);
            }
        }
        productAdapter = new ProductAdapter(getApplicationContext(),false, arrayListArrayList);
        listView.setAdapter(productAdapter);
        listView.setOnItemClickListener(this);

        if (pd.isShowing()) {

            pd.dismiss();
        }
    }

    SoapObject response_global;
    ProgressDialog pd;

    void showProgressDialog() {
        pd = new ProgressDialog(MainActivity.this);
        pd.setMessage("loading..");
        pd.show();
    }

    public boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }
}
