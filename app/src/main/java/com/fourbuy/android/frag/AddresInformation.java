package com.fourbuy.android.frag;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckedTextView;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Spinner;

import com.fourbuy.android.Callback;
import com.fourbuy.android.Constants;
import com.fourbuy.android.FourBuyApp;
import com.fourbuy.android.R;
import com.fourbuy.android.db.CustomerInfo;
import com.fourbuy.android.model.AddressInfo;
import com.fourbuy.android.model.CartInfo;
import com.fourbuy.android.model.Country;
import com.fourbuy.android.model.Customer;
import com.fourbuy.android.model.Region;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.util.List;
import java.util.Vector;

/**
 * Created by Santosh on 12/20/2015.
 */
public class AddresInformation extends BaseFragment {

    private static final String TAG = AddresInformation.class.getSimpleName();
    private final CartInfo cartInfo;
    private final boolean shipping;
    private CheckedTextView useBillingAddress;
    private RadioGroup shipRadio;
    private EditText faxEdittext;
    private EditText telephoneEditText;
    private EditText countryEditText;
    private EditText postalCode;
    private EditText stateEdittext;
    private EditText firstNameEdittext;
    private EditText cityEditetxt;
    private EditText addressEditText;
    private EditText companyEdittext;
    private EditText lastNameEdittext;
    private EditText middleNameEdittext;
    private CustomerInfo customerInfo;
    private Customer customer;
    private List<Country> countries;
    private List<Region> regions;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActivity().setTitle(shipping ? "Billing Information" : "Shipping Information");
        customerInfo = new CustomerInfo(getContext());
        customer = customerInfo.getCustomer();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        customerInfo.close();
    }

    public AddresInformation(CartInfo cartInfo, boolean shipping) {
        this.cartInfo = cartInfo;
        this.shipping = shipping;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.billing_frag, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        useBillingAddress = (CheckedTextView) view.findViewById(R.id.billing_address);
        shipRadio = (RadioGroup) view.findViewById(R.id.ship_radio);
        useBillingAddress.setVisibility(shipping ? View.VISIBLE : View.GONE);
        shipRadio.setVisibility(shipping ? View.GONE : View.VISIBLE);
        faxEdittext = (EditText) view.findViewById(R.id.faxEdittext);
        telephoneEditText = (EditText) view.findViewById(R.id.telephoneEditText);
        countryEditText = (EditText) view.findViewById(R.id.countryEditText);
        postalCode = (EditText) view.findViewById(R.id.postalCode);
        stateEdittext = (EditText) view.findViewById(R.id.stateEdittext);
        cityEditetxt = (EditText) view.findViewById(R.id.cityEditetxt);
        addressEditText = (EditText) view.findViewById(R.id.addressEditText);
        companyEdittext = (EditText) view.findViewById(R.id.companyEdittext);
        lastNameEdittext = (EditText) view.findViewById(R.id.lastNameEdittext);
        middleNameEdittext = (EditText) view.findViewById(R.id.middleNameEdittext);
        firstNameEdittext = (EditText) view.findViewById(R.id.firstNameEdittext);
        stateEdittext.setText("Alaska");
        countryEditText.setText("USA");

        if (!shipping) {
            if (cartInfo.customer != null) {
                initCustomer(cartInfo.customer);
            } else {
                initCustomer(customer);
            }
        }

    }

    private void getCountries(Callback callback) {

    }

    private void initCustomer(Customer customer) {
        if (customer != null) {
            firstNameEdittext.setText(customer.getFirstName());
            middleNameEdittext.setText(customer.getMiddleName() != null ? customer.getMiddleName() : "");
            lastNameEdittext.setText(customer.getLastName());
            companyEdittext.setText(customer.getCompanyName() != null ? customer.getCompanyName() : "");
            addressEditText.setText(customer.getAddressInfo() != null ? customer.getAddressInfo().getAddress_id() : null);
            cityEditetxt.setText("XXXX");
            stateEdittext.setText("Alaska");
            countryEditText.setText("US");//country code:IN
        }
    }

    @Override
    public void setContinue(final Callback callback) {
        if (!shipping) {
            if (cartInfo.customer == null) {
                createCustomer(customer, new Callback() {
                    @Override
                    public void onSuccess(Object... data) {
                        cartInfo.customer=customer;
                        initCustomerAddress(new Callback() {
                            @Override
                            public void onSuccess(Object... data) {
                                SharedPreferences sharedPreferences = getActivity().
                                        getSharedPreferences(Constants.PREFERENCE_NAME, Context.MODE_PRIVATE);
                                sharedPreferences.edit().putBoolean("customerAdded",true);
                                callback.onSuccess();
                            }
                            @Override
                            public void onFailure(Exception exception) {
                                callback.onFailure(exception);
                            }
                        });
                    }
                    @Override
                    public void onFailure(Exception exception) {
                        callback.onFailure(exception);
                    }
                });
            } else {
                if(cartInfo.customer.getAddressInfo()==null){
                   initCustomerAddress(new Callback() {
                       @Override
                       public void onSuccess(Object... data) {
                           callback.onSuccess();
                       }
                       @Override
                       public void onFailure(Exception exception) {
                           callback.onFailure(exception);
                       }
                   });
                }
                callback.onSuccess(null);
            }
        }
    }

    private void initCustomerAddress(final Callback callback){
        final AddressInfo addressInfo = new AddressInfo();
        createAddress(true, addressInfo, new Callback() {
            @Override
            public void onSuccess(Object... data) {
                cartInfo.customer.setAddressInfo(addressInfo);
                callback.onSuccess(data);
            }
            @Override
            public void onFailure(Exception exception) {
                callback.onFailure(exception);
            }
        });
    }


    private void createAddress(final boolean isCustomer,AddressInfo addressInfo, Callback callback) {
        new AsyncTask(){
            @Override
            protected Object doInBackground(Object[] params) {
                try {
                    final String sessionId = ((FourBuyApp) getActivity().
                            getApplication()).getSessionId();
                    String cartId = getContext().getSharedPreferences(Constants.PREFERENCE_NAME,
                            Context.MODE_PRIVATE).getString(Constants.CART_ID, "");
                    SoapSerializationEnvelope env = new SoapSerializationEnvelope(
                            SoapEnvelope.VER12);
                    env.dotNet = false;
                    env.xsd = SoapSerializationEnvelope.XSD;
                    env.enc = SoapSerializationEnvelope.ENC;

                    SoapObject request = new SoapObject("", "shoppingCartCustomerAddresses");
                    request.addProperty("sessionId", sessionId);
                    request.addProperty("quoteId", cartId);

                    SoapObject entityArray = new SoapObject("", "shoppingCartCustomerAddressEntity");
                    entityArray.addProperty("mode", isCustomer?"billing":"shipping");
                    entityArray.addProperty("address_id", customer.getCustomerId());
                    entityArray.addProperty("firstname", customer.getEmailId());
                    entityArray.addProperty("lastname", customer.getFirstName());
                    entityArray.addProperty("company", customer.getLastName());
                    entityArray.addProperty("street", customer.getPassword());
                    entityArray.addProperty("city", customer.getCustomerId());
                    entityArray.addProperty("region", customer.getEmailId());
                    entityArray.addProperty("region_id","2");
                    entityArray.addProperty("postcode ","533002");
                    entityArray.addProperty("country_id ","US");
                    entityArray.addProperty("telephone","7842363865");
                    entityArray.addProperty("fax ", customer.getLastName());
                    entityArray.addProperty("is_default_billing  ",1);
                    entityArray.addProperty("is_default_shipping ",0);

                    request.addProperty("customerAddressData", entityArray);

                    env.setOutputSoapObject(request);
                    HttpTransportSE androidHttpTransport = new HttpTransportSE("http://4buy.in/api/v2_soap");
                    androidHttpTransport.call("", env);
                    Object result = env.getResponse();
                    Log.d(TAG, "customerId:" + result.toString());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;
            }
        }.execute();
    }

    private void createCustomer(final Customer customer, final Callback callback) {
        new AsyncTask() {
            @Override
            protected Object doInBackground(Object[] params) {
                try {
                    final String sessionId = ((FourBuyApp) getActivity().
                            getApplication()).getSessionId();
                    String cartId = getContext().getSharedPreferences(Constants.PREFERENCE_NAME,
                            Context.MODE_PRIVATE).getString(Constants.CART_ID, "");
                    SoapSerializationEnvelope env = new SoapSerializationEnvelope(
                            SoapEnvelope.VER12);
                    env.dotNet = false;
                    env.xsd = SoapSerializationEnvelope.XSD;
                    env.enc = SoapSerializationEnvelope.ENC;

                    SoapObject request = new SoapObject("", "shoppingCartCustomerSet");
                    request.addProperty("sessionId", sessionId);
                    request.addProperty("quoteId", cartId);

                    SoapObject entityArray = new SoapObject("", "shoppingCartCustomerEntity");
                    entityArray.addProperty("mode", "customer");
                    entityArray.addProperty("customer_id", customer.getCustomerId());
                    entityArray.addProperty("email", customer.getEmailId());
                    entityArray.addProperty("firstname", customer.getFirstName());
                    entityArray.addProperty("lastname", customer.getLastName());
                    entityArray.addProperty("password", customer.getPassword());
                    entityArray.addProperty("website_id", 1);
                    entityArray.addProperty("store_id", 1);
                    entityArray.addProperty("group_id", 1);

                    request.addProperty("customerData", entityArray);

                    env.setOutputSoapObject(request);
                    HttpTransportSE androidHttpTransport = new HttpTransportSE("http://4buy.in/api/v2_soap");
                    androidHttpTransport.call("", env);
                    Object result = env.getResponse();
                    Log.d(TAG, "customerId:" + result.toString());
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
