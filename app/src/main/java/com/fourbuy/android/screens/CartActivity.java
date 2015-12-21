package com.fourbuy.android.screens;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.fourbuy.android.Callback;
import com.fourbuy.android.Constants;
import com.fourbuy.android.FourBuyApp;
import com.fourbuy.android.Product;
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
 * Created by 3164 on 11-12-2015.
 */
public class CartActivity extends BaseActivity {

    private static final String TAG = CartActivity.class.getSimpleName();
    private ProductsSqliteOpenHelper cartSqliteOpenHelper;
    private LinearLayout group;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initActionBar();
        group = (LinearLayout) findViewById(R.id.group);
        cartSqliteOpenHelper = new ProductsSqliteOpenHelper(this);
        String cartId = getSharedPreferences(Constants.PREFERENCE_NAME, MODE_PRIVATE).getString(Constants.CART_ID, "");
        if (TextUtils.isEmpty(cartId)) {
            final List<Product> products = cartSqliteOpenHelper.getProducts(cartId);
            getCardId(new Callback() {
                @Override
                public void onSuccess(Object... data) {
                    final String cardId = ((Vector) data[0]).get(1).toString();
                    if (products.size() > 0) {
                        addProductsToCart(cardId, products, new Callback() {
                            @Override
                            public void onSuccess(Object... data) {
                                loadProducts(cardId);
                            }
                            @Override
                            public void onFailure(Exception exception) {

                            }
                        });
                    }
                }
                @Override
                public void onFailure(Exception exception) {
                }
            });
        } else {
            loadProducts(cartId);
        }
    }

    private void getProductFromCart(final String quoteId, final Callback callback) {
        new AsyncTask() {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                if (progressDialog == null) {
                    progressDialog = ProgressDialog.show(CartActivity.this, null, "Loading..", true);
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

                    SoapObject request = new SoapObject("", "shoppingCartProductList");
                    request.addProperty("sessionId", sessionId);
                    request.addProperty("quoteId", quoteId);

                    env.setOutputSoapObject(request);
                    HttpTransportSE androidHttpTransport = new HttpTransportSE("http://4buy.in/api/v2_soap");
                    androidHttpTransport.call("", env);
                    Object result = env.getResponse();

                    Log.d(TAG, "getProductFromCart List:" + result);
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

    private void loadProducts(String cardId) {
        getProductFromCart(cardId, new Callback() {
            @Override
            public void onSuccess(Object... data) {
                List<Product> productModels = new ArrayList<Product>();
                Vector res = (Vector) data[0];
                SoapObject sopaSoapObject = (SoapObject) res.get(1);
                Log.d(TAG, "sopaSoapObject Count:" + sopaSoapObject.getPropertyCount());
                for (int i = 0; i < sopaSoapObject.getPropertyCount(); i++) {
                    SoapObject property = (SoapObject) sopaSoapObject.getProperty(i);
                    Log.d(TAG, "Prp:" + property);
                    Log.d(TAG, "Prp count:" + property.getPropertyCount());
                    Product productModel = new Product();
                    if (property.hasProperty("product_id")) {
                        productModel.setProduct_id(property.getProperty("product_id").toString());
                    }
                    productModel.setSku(property.getProperty("sku").toString());
                    productModel.setName(property.getProperty("name").toString());
                    productModel.setType_id(property.getProperty("type").toString());
                    if (productModel.getProduct_id() != null) {
                        productModels.add(productModel);
                    }
                }
                inflateProducts(productModels);
                Log.d(TAG, "products Loaded:" + productModels.size());
            }

            @Override
            public void onFailure(Exception exception) {

            }
        });
    }

    private void inflateProducts(List<Product> products) {
        LayoutInflater layoutInflater = LayoutInflater.from(this);

        for (int i = 0; i < products.size(); i++) {
            Product product = products.get(i);
            View view = layoutInflater.inflate(R.layout.row_products_item, null, false);
            TextView title = (TextView) view.findViewById(R.id.title);
            TextView type = (TextView) view.findViewById(R.id.type);
            view.findViewById(R.id.add_cart_checkBox).setVisibility(View.GONE);
            title.setText(product.getName());
            type.setText(product.getType_id());
            group.addView(view);
        }
        if (products.size() > 0) {
            Button checkOut = new Button(this);
            checkOut.setText("Check out");
            group.addView(checkOut, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            checkOut.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(getApplicationContext(),CheckOutActivity.class));
                }
            });
        } else {
            TextView order = new TextView(this);
            order.setText("No Item Available");
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            layoutParams.gravity = Gravity.CENTER;
            group.addView(order, layoutParams);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        cartSqliteOpenHelper.close();
    }

}
