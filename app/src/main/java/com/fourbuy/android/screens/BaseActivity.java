package com.fourbuy.android.screens;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.fourbuy.android.Callback;
import com.fourbuy.android.Constants;
import com.fourbuy.android.FourBuyApp;
import com.fourbuy.android.Product;
import com.fourbuy.android.R;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.SoapFault12;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.util.List;
import java.util.Vector;

import static com.fourbuy.android.Constants.DEFAULT_NAMESPACE;
import static com.fourbuy.android.Constants.URL;

/**
 * Created by 3164 on 09-12-2015.
 */
public class BaseActivity extends AppCompatActivity {

    private static final String TAG = BaseActivity.class.getSimpleName();
    protected ProgressDialog progressDialog;
    private Toolbar toolBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    protected void initActionBar() {
        toolBar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolBar);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.cart:
                Intent cartintent = new Intent(this, CartActivity.class);
                startActivity(cartintent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void addProductsToCart(final String cardId, final List<Product> products, final Callback callback) {
        new AsyncTask() {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                if (progressDialog == null) {
                    progressDialog = ProgressDialog.show(BaseActivity.this, null, "Loading..", true);
                } else {
                    progressDialog.show();
                }
            }

            @Override
            protected Object doInBackground(Object[] params) {
                final String sessionId = ((FourBuyApp) getApplication()).getSessionId();
                try {
                    SoapSerializationEnvelope env = new SoapSerializationEnvelope(
                            SoapEnvelope.VER12);
                    env.dotNet = false;
                    env.xsd = SoapSerializationEnvelope.XSD;
                    env.enc = SoapSerializationEnvelope.ENC;

                    SoapObject request = new SoapObject("", "shoppingCartProductAdd");
                    request.addProperty("sessionId", sessionId);
                    request.addProperty("quoteId", "" + cardId);

                    SoapObject entityArray = new SoapObject("", "shoppingCartProductEntityArray");

                    for (Product product : products) {
                        SoapObject soapObject = new SoapObject("", "shoppingCartProductEntity");
                        PropertyInfo pinfo = new PropertyInfo();
                        pinfo.setName("product_id");
                        pinfo.setValue(product.getProduct_id());
                        pinfo.setType(String.class);
                        soapObject.addProperty(pinfo);
                        pinfo = new PropertyInfo();
                        pinfo.setName("sku");
                        pinfo.setValue(product.getSku());
                        pinfo.setType(String.class);
                        soapObject.addProperty(pinfo);
                        pinfo = new PropertyInfo();
                        pinfo.setName("qty");
                        pinfo.setValue(1);
                        pinfo.setType(Double.class);
                        soapObject.addProperty(pinfo);
                        entityArray.addProperty("productsData", soapObject);
                    }
//                    entityArray.addProperty("", soapObject);
                    request.addProperty("products", entityArray);

                    env.setOutputSoapObject(request);
                    HttpTransportSE androidHttpTransport = new HttpTransportSE("http://4buy.in/api/v2_soap");
                    androidHttpTransport.call("", env);
                    Object result = env.getResponse();

                    Log.d(TAG, "Cart List:" + result);
                    return result;

                } catch (SoapFault12 soapFault) {
                    soapFault.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(Object o) {
                progressDialog.dismiss();
                if (o != null) {
                    callback.onSuccess(o);
                } else {
                    callback.onFailure(null);
                }

            }
        }.execute();
    }
    public void getCardId(final Callback callback) {
        new AsyncTask() {

            @Override
            protected void onPreExecute() {
                if (progressDialog == null) {
                    progressDialog = ProgressDialog.show(BaseActivity.this, null, "Loading..", true);
                } else {
                    progressDialog.show();
                }
            }

            @Override
            protected Object doInBackground(Object[] params) {
                final String sessionId = ((FourBuyApp) getApplication()).getSessionId();
                try {
                    SoapSerializationEnvelope env = new SoapSerializationEnvelope(
                            SoapEnvelope.VER12);
                    env.dotNet = false;
                    env.xsd = SoapSerializationEnvelope.XSD;
                    env.enc = SoapSerializationEnvelope.ENC;

                    SoapObject request = new SoapObject("", "shoppingCartCreate");
                    request.addProperty("sessionId", sessionId);
                    env.setOutputSoapObject(request);
                    HttpTransportSE androidHttpTransport = new HttpTransportSE("http://4buy.in/api/v2_soap");
                    androidHttpTransport.call("urn:Magento/shoppingCartCreate", env);
                    Object result = env.getResponse();
                    Log.d(TAG, "cart create:" + result);
                    return result;
                } catch (SoapFault12 soapFault) {
                    soapFault.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(Object result) {
                progressDialog.dismiss();
                if (callback != null) {
                    if (result != null) {
                        String cartId=((Vector) result).get(1).toString();
                        getSharedPreferences(Constants.PREFERENCE_NAME,MODE_PRIVATE)
                                .edit().putString(Constants.CART_ID,cartId).commit();
                        callback.onSuccess(result);
                    } else {
                        callback.onFailure(null);
                    }
                }
            }
        }.execute();
    }

    void getSession(final Callback callback) {
        new AsyncTask<Void, Void, String>() {
            @Override
            protected void onPreExecute() {
                progressDialog = ProgressDialog.show(BaseActivity.this, null, "Loading..", true);
            }

            @Override
            protected String doInBackground(Void... params) {
                try {
                    SoapSerializationEnvelope env = new SoapSerializationEnvelope(
                            SoapEnvelope.VER11);
                    env.dotNet = false;
                    env.xsd = SoapSerializationEnvelope.XSD;
                    env.enc = SoapSerializationEnvelope.ENC;

                    SoapObject request = new SoapObject(DEFAULT_NAMESPACE, "login");
                    request.addProperty("username", "naresh");
                    request.addProperty("apiKey", "naresh@4buy");
                    env.setOutputSoapObject(request);

                    HttpTransportSE androidHttpTransport = new HttpTransportSE(URL);
                    androidHttpTransport.call("", env);
                    Object result = env.getResponse();
                    Log.d("sessionId", result.toString());
                    return result.toString();

                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(String sessionId) {
                progressDialog.dismiss();
                if (callback != null) {
                    if (sessionId != null) {
                        ((FourBuyApp) getApplication()).setSessionId(sessionId);
                        callback.onSuccess(sessionId);
                    } else {
                        callback.onFailure(new Exception("Server....failure"));
                    }
                }
            }
        }.execute();
    }
}
