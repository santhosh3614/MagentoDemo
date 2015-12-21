package com.fourbuy.android.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.fourbuy.android.Constants;

/**
 * Created by Santosh on 12/19/2015.
 */
abstract class CommdB extends SQLiteOpenHelper {
    public CommdB(Context context) {
        super(context, Constants.DATABASE_NAME, null, 1);
    }

    @Override
    public final void onCreate(SQLiteDatabase db) {
        ProductsSqliteOpenHelper.initProduct(db);
        CustomerInfo.createTable(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
