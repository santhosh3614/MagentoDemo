package com.fourbuy.android.screens;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.fourbuy.android.Callback;
import com.fourbuy.android.Category;
import com.fourbuy.android.FourBuyApp;
import com.fourbuy.android.Product;
import com.fourbuy.android.ProductAdapter;
import com.fourbuy.android.R;
import com.fourbuy.android.db.ProductsSqliteOpenHelper;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.SoapFault12;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

/**
 * Created by 3164 on 10-12-2015.
 */
public class ProductListActivity extends BaseActivity {

    public static final String EXTRA_CATEGORY = "extra_category";
    private static final String TAG = ProductListActivity.class.getSimpleName();
    private ListView listView;
    private Category category;
    private ProductsSqliteOpenHelper cartSqliteOpenHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        cartSqliteOpenHelper = new ProductsSqliteOpenHelper(this);
        category =  getIntent().getParcelableExtra(EXTRA_CATEGORY);
        setContentView(R.layout.product_main);
        initActionBar();
        listView = (ListView) findViewById(R.id.productList);

        loadProducts(new Callback() {

            @Override
            public void onSuccess(final Object... data) {
                TextView textView = new TextView(ProductListActivity.this);
                textView.setText("Products");
                listView.addHeaderView(textView);
                listView.setAdapter(new ProductAdapter(ProductListActivity.this,
                        cartSqliteOpenHelper,(List<Product>) data[0]));
                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view,
                                            final int position, long id) {

                    }
                });
            }

            @Override
            public void onFailure(Exception exception) {

            }
        });


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        cartSqliteOpenHelper.close();
    }

    private void loadProducts(final Callback callback) {
        final String sessionId = ((FourBuyApp) getApplication()).getSessionId();
        new AsyncTask<Void, Void, List<Product>>() {
            @Override
            protected void onPreExecute() {
                if (progressDialog == null) {
                    progressDialog = ProgressDialog.show(ProductListActivity.this, null, "Loading..", true);
                }else{
                    progressDialog.show();
                }
            }

            @Override
            protected List<Product> doInBackground(Void... params) {
                try {
                    SoapSerializationEnvelope env = new SoapSerializationEnvelope(
                            SoapEnvelope.VER12);
                    env.dotNet = false;
                    env.xsd = SoapSerializationEnvelope.XSD;
                    env.enc = SoapSerializationEnvelope.ENC;

                    SoapObject request = new SoapObject("", "catalogProductList");
                    request.addProperty("sessionId", sessionId);

                    SoapObject soapObject = new SoapObject();

                    SoapObject soapObject1=new SoapObject();

                    SoapObject soapObject2=new SoapObject();
                    soapObject2.addProperty("like","zol%");
                    soapObject1.addProperty("sku", soapObject2);

                    SoapObject soapObject3=new SoapObject();
                    soapObject3.addProperty("like","zol%");
                    soapObject1.addProperty("name",soapObject3);

                    soapObject.addProperty("custom_filter",soapObject1);

                    request.addProperty("filters",soapObject);

                    env.setOutputSoapObject(request);
                    HttpTransportSE androidHttpTransport = new HttpTransportSE("http://4buy.in/api/v2_soap");
                    androidHttpTransport.call("", env);
                    Object result = env.getResponse();

//                    System.out.println(""+request.);

                    Log.d(TAG, "Product List:" + result);
                    return parseSoapResponse((Vector<Object>) result);

                } catch (SoapFault12 soapFault) {
                    soapFault.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(List<Product> storeInfos) {
                progressDialog.dismiss();
                if (callback != null) {
                    if (storeInfos != null) {
                        callback.onSuccess(storeInfos);
                    } else {
                        callback.onFailure(null);
                    }
                }

            }
        }.execute();
    }

    private List<Product> parseSoapResponse(Vector<Object> vector) {
        SoapObject soapObject = (SoapObject) vector.get(1);
        List<Product> productModelArrayList = new ArrayList<>();
        for (int i = 0; i < soapObject.getPropertyCount(); i++) {
            SoapObject property = (SoapObject) soapObject.getProperty(i);;
//            Product productModel = new Product();
//            for (int j = 0; j < object.getPropertyCount(); j++) {
//                Object objectResponse = (Object) object.getProperty(j);
//                SoapObject r = (SoapObject) objectResponse;
//                String key1 = r.getPropertySafelyAsString("key");
//                if (key1.equals("product_id")) {
//                    productModel.setProduct_id(r.getPropertySafelyAsString("value"));
//                } else if (key1.equals("sku")) {
//                    productModel.setSku(r.getPropertySafelyAsString("value"));
//                } else if (key1.equals("name")) {
//                    productModel.setName(r.getPropertySafelyAsString("value"));
//                } else if (key1.equals("type")) {
//                    productModel.setType_id(r.getPropertySafelyAsString("value"));
//                }
                Product productModel = new Product();
                if (property.hasProperty("product_id")) {
                    productModel.setProduct_id(property.getProperty("product_id").toString());
                }
                productModel.setSku(property.getProperty("sku").toString());
                productModel.setName(property.getProperty("name").toString());
                productModel.setType_id(property.getProperty("type").toString());
                if (productModel.getProduct_id() != null) {
                    productModelArrayList.add(productModel);
                }
//            }
        }
        return productModelArrayList;
    }


}
