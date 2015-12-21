package com.fourbuy.android;

/**
 * Created by 3164 on 09-12-2015.
 */
public interface Callback {

    void onSuccess(Object... data);

    void onFailure(Exception exception);
}
