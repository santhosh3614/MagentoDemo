package com.fourbuy.android;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.fourbuy.android.db.ProductsSqliteOpenHelper;
import com.fourbuy.android.screens.BaseActivity;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.SoapFault12;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

/**
 * Created by 3140 on 03-12-2015.
 */
public class ProductAdapter extends BaseAdapter {

    private static final String TAG = ProductAdapter.class.getSimpleName();
    private final ProductsSqliteOpenHelper cartSqliteOpenHelper;
    private List<Product> cartProducts;
    Context context;
    List<Product> arrayListArrayList;
    LayoutInflater layoutInflater;
    private ProgressDialog progressDialog;

    public ProductAdapter(Context context, ProductsSqliteOpenHelper cartSqliteOpenHelper, List<Product> arrayListArrayList) {
        this.context = context;
        this.cartSqliteOpenHelper = cartSqliteOpenHelper;
        String cartId = context.getSharedPreferences(Constants.PREFERENCE_NAME,
                Context.MODE_PRIVATE).getString(Constants.CART_ID, "");
        if(!TextUtils.isEmpty(cartId)){
            cartProducts = this.cartSqliteOpenHelper.getProducts(cartId);
        }else{
            cartProducts=new ArrayList();
        }
        this.arrayListArrayList = arrayListArrayList;
        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return arrayListArrayList.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    ViewHolder viewHolder;

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = layoutInflater.inflate(R.layout.row_products_item, parent, false);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.initView(arrayListArrayList.get(position));

        return convertView;
    }

    class ViewHolder {
        CheckBox addCartCheckbox;
        ImageView imgView;
        TextView title;
        TextView type;

        ViewHolder(View view) {
            imgView = (ImageView) view.findViewById(R.id.img);
            title = (TextView) view.findViewById(R.id.title);
            type = (TextView) view.findViewById(R.id.type);
            addCartCheckbox = (CheckBox) view.findViewById(R.id.add_cart_checkBox);
        }

        public void initView(final Product product) {
            title.setText(product.getName());
            type.setText("Type :" + product.getType_id());
            addCartCheckbox.setOnCheckedChangeListener(null);
            addCartCheckbox.setChecked(cartProducts.contains(product));
            addCartCheckbox.setText(addCartCheckbox.isChecked() ? "Added To Cart" : "Add To Cart");
            addCartCheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, final boolean isChecked) {
                    addCartCheckbox.setText(isChecked ? "Added To Cart" : "Add To Cart");
                    final String cartId = context.getSharedPreferences(Constants.PREFERENCE_NAME, Context.MODE_PRIVATE)
                            .getString(Constants.CART_ID, "");
                    if (TextUtils.isEmpty(cartId)) {
                        ((BaseActivity) context).getCardId(new Callback() {
                            @Override
                            public void onSuccess(Object... data) {
                                final String cardId = ((Vector) data[0]).get(1).toString();
                                List<Product> products = new ArrayList(cartProducts);
                                products.add(product);
                                ((BaseActivity) context).addProductsToCart(cardId, products, new Callback() {
                                    @Override
                                    public void onSuccess(Object... data) {
                                        if (isChecked) {
                                            cartProducts.add(product);
                                            cartSqliteOpenHelper.addTocart(cardId,product);
                                        } else {
                                            cartSqliteOpenHelper.deleteInCart(cardId,product);
                                            Log.d(TAG, "Remove....:" + cartProducts.remove(product));
                                            Log.d(TAG, "Remove....List:" + cartProducts);
                                        }
                                    }

                                    @Override
                                    public void onFailure(Exception exception) {

                                    }
                                });
                            }

                            @Override
                            public void onFailure(Exception exception) {

                            }
                        });
                    } else {
                        if (isChecked) {
                            addProduct(product, cartId, 1);
                            cartProducts.add(product);
                            cartSqliteOpenHelper.addTocart(cartId,product);
                        } else {
                            removeProduct(product, cartId, 1);
                            cartProducts.remove(product);
                            Log.d(TAG, "Remove....:" + cartProducts.remove(product));
                            Log.d(TAG, "Remove....List:" + cartProducts);
                            cartSqliteOpenHelper.deleteInCart(cartId,product);
                        }
                    }
                }
            });
        }
    }

    private void addProduct(final Product product, final String cartId, final int quantity) {
        new AsyncTask() {

            @Override
            protected void onPreExecute() {
                if (progressDialog == null) {
                    progressDialog = ProgressDialog.show(context, null, "Loading..", true);
                } else {
                    progressDialog.show();
                }
            }

            @Override
            protected Object doInBackground(Object[] params) {
                final String sessionId = ((FourBuyApp) ((BaseActivity) context).getApplication()).getSessionId();
                try {
                    SoapSerializationEnvelope env = new SoapSerializationEnvelope(
                            SoapEnvelope.VER12);
                    env.dotNet = false;
                    env.xsd = SoapSerializationEnvelope.XSD;
                    env.enc = SoapSerializationEnvelope.ENC;

                    SoapObject request = new SoapObject("", "shoppingCartProductAdd");
                    request.addProperty("sessionId", sessionId);
                    request.addProperty("quoteId", cartId);

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
                    pinfo.setValue(quantity);
                    pinfo.setType(Double.class);
                    soapObject.addProperty(pinfo);

                    SoapObject entityArray = new SoapObject("", "shoppingCartProductEntityArray");
                    entityArray.addProperty("productsData", soapObject);
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
                Toast.makeText(context, "Product added to cart", Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
            }
        }.execute();
    }

    void removeProduct(final Product product, final String cartId, final int quantity) {
        new AsyncTask() {
            @Override
            protected void onPreExecute() {
                if (progressDialog == null) {
                    progressDialog = ProgressDialog.show(context, null, "Loading..", true);
                } else {
                    progressDialog.show();
                }
            }

            @Override
            protected Object doInBackground(Object[] params) {
                final String sessionId = ((FourBuyApp) ((BaseActivity) context).getApplication()).getSessionId();
                try {
                    SoapSerializationEnvelope env = new SoapSerializationEnvelope(
                            SoapEnvelope.VER12);
                    env.dotNet = false;
                    env.xsd = SoapSerializationEnvelope.XSD;
                    env.enc = SoapSerializationEnvelope.ENC;

                    SoapObject request = new SoapObject("", "shoppingCartProductRemove");
                    request.addProperty("sessionId", sessionId);
                    request.addProperty("quoteId", cartId);

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
                    pinfo.setValue(quantity);
                    pinfo.setType(Double.class);
                    soapObject.addProperty(pinfo);

                    SoapObject entityArray = new SoapObject("", "shoppingCartProductEntityArray");
                    entityArray.addProperty("productsData", soapObject);
                    request.addProperty("products", entityArray);

                    env.setOutputSoapObject(request);
                    HttpTransportSE androidHttpTransport = new HttpTransportSE("http://4buy.in/api/v2_soap");
                    androidHttpTransport.call("", env);
                    Object result = env.getResponse();
                    Log.d(TAG, "Remove Cart List:" + result);

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
                Toast.makeText(context, "Product added to cart", Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
            }
        }.execute();
    }
}