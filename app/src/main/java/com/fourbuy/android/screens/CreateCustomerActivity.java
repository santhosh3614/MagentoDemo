package com.fourbuy.android.screens;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.fourbuy.android.Callback;
import com.fourbuy.android.FourBuyApp;
import com.fourbuy.android.NetworkUtils;
import com.fourbuy.android.R;
import com.fourbuy.android.db.CustomerInfo;
import com.fourbuy.android.model.Customer;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.util.Vector;

/**
 * Created by Santosh on 12/19/2015.
 */
public class CreateCustomerActivity extends BaseActivity {

    private LinearLayout group;
    private static final String TAG = MainActivity.class.getSimpleName();
    private EditText confirmPwd;
    private EditText pwdEditText;
    private Button submit;
    private EditText mailEditText;
    private EditText firstNameEdittext;
    private EditText middleEditText;
    private EditText lastNameEdittext;
    private CustomerInfo customerInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        customerInfo = new CustomerInfo(this);
        Customer customer = customerInfo.getCustomer();
        if (customer != null) {
            startActivity(new Intent(this, MainActivity.class));
            finish();
            return;
        }
        setContentView(R.layout.login_activity);
        confirmPwd = (EditText) findViewById(R.id.cnfrmPasswordEdittext);
        pwdEditText = (EditText) findViewById(R.id.passwordEdittext);
        mailEditText = (EditText) findViewById(R.id.mailEdittext);
        firstNameEdittext = (EditText) findViewById(R.id.firstNameEdittext);
        middleEditText = (EditText) findViewById(R.id.middleNameEdittext);
        lastNameEdittext = (EditText) findViewById(R.id.lastNameEdittext);
        submit = (Button) findViewById(R.id.submitBtn);
        findViewById(R.id.passwordEdittext);
        if (!NetworkUtils.isNetworkAvailable(this)) {
            Toast.makeText(this, "Poor Internet Connection", Toast.LENGTH_LONG).show();
            return;
        }
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String firstName = firstNameEdittext.getText().toString();
                String lastName = lastNameEdittext.getText().toString();
                String middleName = middleEditText.getText().toString();
                String mailId = mailEditText.getText().toString();
                String pwd = pwdEditText.getText().toString();
                String confirmPassword = confirmPwd.getText().toString();
                if (TextUtils.isEmpty(firstName)) {
                    Toast.makeText(getApplicationContext(), "First name must not be empty.", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(middleName)) {
                    Toast.makeText(getApplicationContext(), "Middle name must not be empty.", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(lastName)) {
                    Toast.makeText(getApplicationContext(), "Last name must not be empty.", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(mailId)) {
                    Toast.makeText(getApplicationContext(), "MailId must not be empty.", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(pwd)) {
                    Toast.makeText(getApplicationContext(), "Password must not be empty.", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(confirmPassword)) {
                    Toast.makeText(getApplicationContext(), "Conform Password must not be empty.", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (!pwd.equals(confirmPassword)) {
                    Toast.makeText(getApplicationContext(), "Password and Confirm Password must match", Toast.LENGTH_SHORT).show();
                    return;
                }
                Customer customer = new Customer(-1, mailId, pwd, firstName, middleName, lastName);
                createCustomer(customer);
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        customerInfo.close();
    }

    private void createCustomer(final Customer customer) {
        final String sessionId = ((FourBuyApp) getApplication()).getSessionId();
        if (TextUtils.isEmpty(sessionId)) {
            getSession(new Callback() {
                @Override
                public void onSuccess(Object... data) {
                    String session = (String) data[0];
                    getCutomeIdFromSession(customer, new Callback() {
                        @Override
                        public void onSuccess(Object... data) {
                            startActivity(new Intent(getApplicationContext(), MainActivity.class));
                        }

                        @Override
                        public void onFailure(Exception exception) {

                        }
                    });
                }

                @Override
                public void onFailure(Exception exception) {
                    Toast.makeText(getApplicationContext(),
                            "Opps Something went wrong.Please try later.", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            getCutomeIdFromSession(customer, new Callback() {
                @Override
                public void onSuccess(Object... data) {
                    startActivity(new Intent(getApplicationContext(), MainActivity.class));
                }

                @Override
                public void onFailure(Exception exception) {

                }
            });
        }
    }

    private void getCutomeIdFromSession(final Customer customer, final Callback callback) {
        new AsyncTask<Void, Void, String>() {
            @Override
            protected void onPreExecute() {
                if (progressDialog == null) {
                    progressDialog = ProgressDialog.show(CreateCustomerActivity.this, null,
                            "Loading..", true);
                } else {
                    progressDialog.show();
                }
            }

            @Override
            protected String doInBackground(Void... params) {
                final String sessionId = ((FourBuyApp) getApplication()).getSessionId();
                try {
                    SoapSerializationEnvelope env = new SoapSerializationEnvelope(
                            SoapEnvelope.VER12);
                    env.dotNet = false;
                    env.xsd = SoapSerializationEnvelope.XSD;
                    env.enc = SoapSerializationEnvelope.ENC;

                    SoapObject request = new SoapObject("", "customerCustomerCreate");
                    request.addProperty("sessionId", sessionId);

                    SoapObject entityArray = new SoapObject("", "customerCustomerEntityToCreate");
                    entityArray.addProperty("email", customer.getEmailId());
                    entityArray.addProperty("firstname", customer.getFirstName());
                    entityArray.addProperty("lastname", customer.getLastName());
                    entityArray.addProperty("password", customer.getPassword());
                    entityArray.addProperty("website_id", 1);
                    entityArray.addProperty("store_id", 1);
                    entityArray.addProperty("group_id", 1);
//                    entityArray.addProperty("firstname",firstName);


//////                    SoapObject soapObject = new SoapObject("", "customerCustomerEntity");
//////
//////                    PropertyInfo pinfo = new PropertyInfo();
//////                    pinfo.setName("email");
//////                    pinfo.setValue(mailId);
//////                    pinfo.setType(String.class);
//////                    soapObject.addProperty(pinfo);
//////
//////                    pinfo = new PropertyInfo();
//////                    pinfo.setName("firstname");
//////                    pinfo.setValue(firstName);
//////                    pinfo.setType(String.class);
//////                    soapObject.addProperty(pinfo);
//////
//////                    pinfo = new PropertyInfo();
//////                    pinfo.setName("lastname");
//////                    pinfo.setValue(lastName);
//////                    pinfo.setType(String.class);
//////                    soapObject.addProperty(pinfo);
//////
//////                    pinfo.setName("password");
//////                    pinfo.setValue(password);
//////                    pinfo.setType(String.class);
//////                    soapObject.addProperty(pinfo);
//////
//////                    pinfo = new PropertyInfo();
//////                    pinfo.setName("website_id");
//////                    pinfo.setValue(1);
//////                    pinfo.setType(Integer.class);
//////                    soapObject.addProperty(pinfo);
//////
//////                    pinfo = new PropertyInfo();
//////                    pinfo.setName("store_id");
//////                    pinfo.setValue(1);
//////                    pinfo.setType(Integer.class);
//////                    soapObject.addProperty(pinfo);
////
////                    pinfo = new PropertyInfo();
////                    pinfo.setName("group_id");
////                    pinfo.setValue(1);
////                    pinfo.setType(Integer.class);
////                    soapObject.addProperty(pinfo);
//
//                    entityArray.addProperty("customerData", soapObject);
                    request.addProperty("customerData", entityArray);

                    env.setOutputSoapObject(request);
                    HttpTransportSE androidHttpTransport = new HttpTransportSE("http://4buy.in/api/v2_soap");
                    androidHttpTransport.call("", env);
                    Object result = env.getResponse();
                    String customerId = ((Vector) result).get(1).toString();
                    Log.d(TAG, "customerId:" + result.toString());
                    return customerId;

                } catch (final Exception e) {
                    Log.d(TAG, "Customer Error:" + e.getMessage());
                    new Handler(Looper.myLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    });
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(String customerId) {
                progressDialog.dismiss();
                if (callback != null) {
                    if (customerId != null) {
                        customer.setCustomerId(Integer.parseInt(customerId));
                        customerInfo.addCustomer(customer);
                        callback.onSuccess(customerId);
                    } else {
                        callback.onFailure(new Exception("Server....failure"));
                    }
                }
            }
        }.execute();
    }


}
