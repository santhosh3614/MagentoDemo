package com.fourbuy.android.frag;

import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.fourbuy.android.Callback;
import com.fourbuy.android.model.CartInfo;

/**
 * Created by Santosh on 12/20/2015.
 */
public class ShippingMethodsFragment extends BaseFragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActivity().setTitle("Shipping Method");
    }

    public ShippingMethodsFragment(CartInfo cartInfo) {

    }

    @Override
    public void setContinue(Callback callback) {

    }
}
