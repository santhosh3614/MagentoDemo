package com.fourbuy.android.screens;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.fourbuy.android.Callback;
import com.fourbuy.android.Category;
import com.fourbuy.android.FourBuyApp;
import com.fourbuy.android.NetworkUtils;
import com.fourbuy.android.R;
import com.fourbuy.android.StoreInfo;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.SoapFault12;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import static com.fourbuy.android.Constants.DEFAULT_NAMESPACE;
import static com.fourbuy.android.Constants.URL;

/**
 * Created by 3164 on 09-12-2015.
 */
public class MainActivity extends BaseActivity {

    private LinearLayout group;
    private static final String TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initActionBar();
        group = (LinearLayout) findViewById(R.id.group);
        if (!NetworkUtils.isNetworkAvailable(this)) {
            Toast.makeText(this, "Poor Internet Connection", Toast.LENGTH_LONG).show();
            return;
        }
        final String sessionId = ((FourBuyApp) getApplication()).getSessionId();
        if (TextUtils.isEmpty(sessionId)) {
            getSession(new Callback() {
                @Override
                public void onSuccess(Object... data) {
                    String session = (String) data[0];
                    loadCategories(session);
                }
                @Override
                public void onFailure(Exception exception) {
                    Toast.makeText(getApplicationContext(),
                            "Opps Something went wrong.Please try later.", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            loadCategories(sessionId);
        }
    }


    private void loadCategories(final String sessionId) {
        loadStoreObj(sessionId, new Callback() {
            @Override
            public void onSuccess(Object... data) {
                List<StoreInfo> storeInfos = (List<StoreInfo>) data[0];
                if (!storeInfos.isEmpty()) {
                    loadCategories(storeInfos.get(0), new Callback() {
                        @Override
                        public void onSuccess(Object... data) {
                            inflateCategories((Category) data[0]);
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
    }

    private void inflateCategories(Category category) {
        group.removeAllViews();
        Category rootCategory = category;
        Category defaultCategory = rootCategory.getCategoryList().get(0);
        List<Category> categoryList = defaultCategory.getCategoryList();
        inflateInnerCategories(categoryList);
    }


    private void inflateInnerCategories(List<Category> categoryList) {
        for (int i = 0; i < categoryList.size(); i++) {
            Category level2category = categoryList.get(i);
            TextView textView = new TextView(this);
            textView.setText(level2category.getName());
            group.addView(textView);

            List<Category> categoryList1 = level2category.getCategoryList();
            for (int j = 0; j < categoryList1.size(); j++) {
                final Category category = categoryList1.get(j);
                View inflate = LayoutInflater.from(this).inflate(R.layout.categories_list_item, group, false);
                TextView title = (TextView) inflate.findViewById(R.id.title);
                title.setText(category.getName());
                group.addView(inflate);
                inflate.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(MainActivity.this,
                                ProductListActivity.class);
                        intent.putExtra(ProductListActivity.EXTRA_CATEGORY,category);
                        startActivity(intent);
                    }
                });
            }
        }
    }

    private void loadCategories(final StoreInfo storeInfo, final Callback callback) {
        final String sessionId = ((FourBuyApp) getApplication()).getSessionId();
        new AsyncTask<Void, Void, Category>() {

            @Override
            protected void onPreExecute() {
                progressDialog.show();
            }

            @Override
            protected Category doInBackground(Void... params) {
                try {
                    SoapSerializationEnvelope env = new SoapSerializationEnvelope(
                            SoapEnvelope.VER12);
                    env.dotNet = false;
                    env.xsd = SoapSerializationEnvelope.XSD;
                    env.enc = SoapSerializationEnvelope.ENC;

                    SoapObject request = new SoapObject(DEFAULT_NAMESPACE, "call");
                    request.addProperty("sessionId", sessionId);
                    request.addProperty("resourcePath", "catalog_category.tree");
                    request.addProperty("args", storeInfo.getStoreId());
                    env.setOutputSoapObject(request);

                    HttpTransportSE androidHttpTransport = new HttpTransportSE(URL);
                    androidHttpTransport.call("", env);
                    Object result = env.getResponse();
                    Log.d(TAG, "loadCategories List:" + result);
                    return getCategories(result);
                } catch (SoapFault12 soapFault) {
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(Category category) {
                if (callback != null) {
                    if (category != null) {
                        callback.onSuccess(category);
                    } else {
                        callback.onFailure(new Exception());
                    }
                }
                progressDialog.dismiss();
            }
        }.execute();
    }

    private Category getCategories(Object result) {
        Category category = null;
        if (result instanceof Vector) {
            SoapPrimitive soapPrimitive = (SoapPrimitive) ((Vector) result).get(0);
            if (soapPrimitive.getValue().equals("callReturn")) {
                SoapObject soapResult = (SoapObject) ((Vector) result).get(1);
                category = getCatageory(soapResult);
            }
        }
        return category;
    }


    Category getCatageory(SoapObject soapObject) {
        Category category = null;
        int propertyCount = soapObject.getPropertyCount();
        if (propertyCount > 0) {
            category = new Category();
            for (int i = 0; i < propertyCount; i++) {
                Object property = soapObject.getProperty(i);
                if (property instanceof SoapObject) {
                    SoapObject soapProperty = (SoapObject) property;
                    String key = soapProperty.getPropertySafelyAsString("key");
                    if (key.equals("category_id")) {
                        String value = soapProperty.getPropertySafelyAsString("value");
                        category.setCategoryId(Integer.parseInt(value));//String value = ;
                    } else if (key.equals("parent_id")) {
                        category.setParentId(Integer.parseInt(soapProperty.getPropertySafelyAsString("value")));
                    } else if (key.equals("name")) {
                        category.setName(soapProperty.getPropertySafelyAsString("value"));
                    } else if (key.equals("is_active")) {
                        category.setActive(soapProperty.getPropertySafelyAsString("value").equals("1"));
                    } else if (key.equals("children")) {
                        Object value = soapProperty.getProperty("value");
                        if (value instanceof Vector) {
                            Vector<SoapObject> val = (Vector) value;
                            if (!val.isEmpty()) {
                                for (int j = 0; j < val.size(); j++) {
                                    category.addCategory(getCatageory(val.get(j)));
                                }
                            }
                        }
                    }
                }
            }
        }
        return category;
    }

    private void loadStoreObj(final String sessionId, final Callback callback) {
        new AsyncTask<Void, Void, List<StoreInfo>>() {
            @Override
            protected void onPreExecute() {
                if(progressDialog==null){
                    progressDialog = ProgressDialog.show(MainActivity.this, null, "Loading..", true);
                }else {
                    progressDialog.show();
                }
            }

            @Override
            protected List<StoreInfo> doInBackground(Void... params) {
                List<StoreInfo> storeInfo = new ArrayList<>();
                try {
                    SoapSerializationEnvelope env = new SoapSerializationEnvelope(
                            SoapEnvelope.VER12);
                    env.dotNet = false;
                    env.xsd = SoapSerializationEnvelope.XSD;
                    env.enc = SoapSerializationEnvelope.ENC;

                    SoapObject request = new SoapObject(DEFAULT_NAMESPACE, "call");
                    request.addProperty("sessionId", sessionId);
                    request.addProperty("resourcePath", "core_store.list");
                    env.setOutputSoapObject(request);

                    HttpTransportSE androidHttpTransport = new HttpTransportSE(URL);
                    androidHttpTransport.call("", env);
                    Object result = env.getResponse();


                    Log.d(TAG, "Request Dump:" + androidHttpTransport.requestDump);

                    if (result instanceof Vector) {
                        SoapPrimitive soapPrimitive = (SoapPrimitive) ((Vector) result).get(0);
                        if (soapPrimitive.getValue().equals("callReturn")) {
                            Vector soapResult = (Vector) ((Vector) result).get(1);
                            for (int i = 0; i < soapResult.size(); i++) {
                                StoreInfo storeObjInfo = getStoreObject((SoapObject) soapResult.get(0));
                                storeInfo.add(storeObjInfo);
                            }
                        }
                    }
                    Log.d(TAG, "Store List:" + result);
                } catch (SoapFault12 soapFault) {

                } catch (Exception e) {
                    e.printStackTrace();
                }
                return storeInfo;
            }

            @Override
            protected void onPostExecute(List<StoreInfo> storeInfos) {
                if (callback != null) {
                    if (storeInfos != null) {
                        progressDialog.dismiss();
                        callback.onSuccess(storeInfos);
                    } else {
                        callback.onFailure(new Exception());
                    }
                }
            }
        }.execute();
    }

    private StoreInfo getStoreObject(SoapObject soapObject) {
        StoreInfo storeInfo = null;
        int propertyCount = soapObject.getPropertyCount();
        if (propertyCount > 0) {
            storeInfo = new StoreInfo();
            for (int i = 0; i < propertyCount; i++) {
                Object property = soapObject.getProperty(i);
                if (property instanceof SoapObject) {
                    SoapObject soapObj = (SoapObject) property;
                    String key = soapObj.getPropertySafelyAsString("key");
                    String value = soapObj.getPropertySafelyAsString("value");
                    if (key.equals("store_id")) {
                        storeInfo.setStoreId(Integer.parseInt(value));
                    } else if (key.equals("code")) {
                        storeInfo.setCode(value);
                    } else if (key.equals("website_id")) {
                        storeInfo.setWebsite(value);
                    } else if (key.equals("name")) {
                        storeInfo.setName(value);
                    } else if (key.equals("sort_order")) {
                        storeInfo.setSortOrder(Integer.parseInt(value));
                    } else if (key.equals("is_active")) {
                        storeInfo.setActive(value.equals("1"));
                    }
                }
            }
        }
        return storeInfo;
    }

}
