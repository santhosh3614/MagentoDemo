package com.fourbuy.android.screens;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.fourbuy.android.Callback;
import com.fourbuy.android.Constants;
import com.fourbuy.android.FourBuyApp;
import com.fourbuy.android.R;
import com.fourbuy.android.frag.AddresInformation;
import com.fourbuy.android.frag.BaseFragment;
import com.fourbuy.android.frag.OrderReview;
import com.fourbuy.android.frag.PaymentInfoFragment;
import com.fourbuy.android.frag.ShippingMethodsFragment;
import com.fourbuy.android.model.CartInfo;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.SoapFault12;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Santosh on 12/19/2015.
 */
public class CheckOutActivity extends BaseActivity {

    private static final String TAG = CheckOutActivity.class.getSimpleName();
    private ViewPager viewPager;
    private ProgressBar progressBar;
    private Button prevBtn;
    private Button nextBtn;
    private LinearLayout btnLay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.checkout);
        viewPager = (ViewPager) findViewById(R.id.viewPager);
        progressBar = (ProgressBar) findViewById(R.id.progressbar);
        btnLay = (LinearLayout) findViewById(R.id.btn_lay);
        prevBtn = (Button) findViewById(R.id.prevBtn);
        prevBtn.setVisibility(View.GONE);

        getCartInfo(new Callback() {
            @Override
            public void onSuccess(Object... data) {
                progressBar.setVisibility(View.GONE);
                btnLay.setVisibility(View.VISIBLE);
                initViewPager((CartInfo) data[0]);
            }

            @Override
            public void onFailure(Exception exception) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(getApplicationContext(), "SomeThing went wrong"
                        , Toast.LENGTH_LONG).show();
            }
        });
    }

    private void initViewPager(final CartInfo cartInfo) {
        SharedPreferences sharedPreferences = getSharedPreferences(Constants.PREFERENCE_NAME, MODE_PRIVATE);
        boolean customerAdded = sharedPreferences.getBoolean("customerAdded", false);
        boolean isShippingAdded = sharedPreferences.getBoolean("shippingAdded", false);
        boolean isPaymentAdded = sharedPreferences.getBoolean("paymentAdded", false);

        final List<BaseFragment> fragments = new ArrayList<>();
        if(!customerAdded){
            fragments.add(new AddresInformation(cartInfo,false));
        }
        if(!isShippingAdded){
            fragments.add(new ShippingMethodsFragment(cartInfo));
        }
        if(!isPaymentAdded){
            fragments.add(new PaymentInfoFragment(cartInfo));
        }
        fragments.add(new OrderReview(cartInfo));
        viewPager.setAdapter(new FragmentStatePagerAdapter(getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                return fragments.get(position);
            }

            @Override
            public int getCount() {
                return fragments.size();
            }
        });
        if(viewPager.getCurrentItem()==0){
            prevBtn.setVisibility(View.GONE);
        }
        prevBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewPager.setCurrentItem(viewPager.getCurrentItem()-1);
            }
        });
        nextBtn = (Button) findViewById(R.id.nxtBtn);
        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fragments.get(viewPager.getCurrentItem()).setContinue(new Callback() {
                    @Override
                    public void onSuccess(Object... data) {

                        if (viewPager.getCurrentItem() != fragments.size() - 1) {
                            viewPager.setCurrentItem(viewPager.getCurrentItem() + 1);
                        } else {

                        }
                    }

                    @Override
                    public void onFailure(Exception exception) {

                    }
                });
            }
        });
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                if(position==0){
                    prevBtn.setVisibility(View.GONE);
                }else if(position==fragments.size()-1){
                    nextBtn.setText("Order");
                }else{
                    nextBtn.setText("Continue");
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    void getCartInfo(final Callback callback) {
        new AsyncTask() {
            @Override
            protected Object doInBackground(Object[] params) {
                final String sessionId = ((FourBuyApp) getApplication()).getSessionId();
                String cartId = getSharedPreferences(Constants.PREFERENCE_NAME, Context.MODE_PRIVATE).getString(Constants.CART_ID, "");
                try {
                    SoapSerializationEnvelope env = new SoapSerializationEnvelope(
                            SoapEnvelope.VER12);
                    env.dotNet = false;
                    env.xsd = SoapSerializationEnvelope.XSD;
                    env.enc = SoapSerializationEnvelope.ENC;

                    SoapObject request = new SoapObject("", "shoppingCartInfo");
                    request.addProperty("sessionId", sessionId);
                    request.addProperty("quoteId", "" + cartId);

                    env.setOutputSoapObject(request);
                    HttpTransportSE androidHttpTransport = new HttpTransportSE("http://4buy.in/api/v2_soap");
                    androidHttpTransport.call("", env);
                    Object result = env.getResponse();
                    CartInfo cartInfo = CartInfo.getCartInfo(result);
                    Log.d(TAG, "Cart List:" + result.toString());
                    return cartInfo;
                } catch (SoapFault12 soapFault) {
                    soapFault.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(Object o) {
                if (o != null) {
                    callback.onSuccess(o);
                } else {
                    callback.onFailure(null);
                }
            }
        }.execute();
    }
}
