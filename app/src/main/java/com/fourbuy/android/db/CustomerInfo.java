package com.fourbuy.android.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.fourbuy.android.Constants;
import com.fourbuy.android.Product;
import com.fourbuy.android.model.Customer;

/**
 * Created by Santosh on 12/19/2015.
 */
public class CustomerInfo extends CommdB {

    private static final String DATABASE_TABLE = "CustomerInfo";
    private static final String EMAIL = "email";
    private static final String FIRST_NAME = "firstName";
    private static final String MIDDLE_NAME = "middleName";
    private static final String LASTNAME = "lastName";
    private static final String PASSWORD = "password";
    private static final String CUSTOMER_ID = "customer_id";

    public CustomerInfo(Context context) {
        super(context);
    }

    static void createTable(SQLiteDatabase db){
        db.execSQL("CREATE TABLE IF NOT EXISTS "
                + DATABASE_TABLE + "("
                + CUSTOMER_ID + " INTEGER NOT NULL UNIQUE,"
                + EMAIL + " TEXT NOT NULL, "
                + PASSWORD + " TEXT NOT NULL,"
                + FIRST_NAME + " TEXT, "
                + MIDDLE_NAME + " TEXT," +
                LASTNAME + " TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }

    public Customer getCustomer() {
        Customer customer = null;
        Cursor query = getReadableDatabase().query(DATABASE_TABLE, null, null, null, null, null, null);
        for (boolean hasItem = query.moveToFirst(); hasItem; hasItem = query.moveToNext()) {
            // use cursor to work with current item
            customer = new Customer();
            customer.setCustomerId(query.getInt(query.getColumnIndex(CUSTOMER_ID)));
            customer.setEmailId(query.getString(query.getColumnIndex(EMAIL)));
            customer.setPassword(query.getString(query.getColumnIndex(PASSWORD)));
            customer.setFirstName(query.getString(query.getColumnIndex(FIRST_NAME)));
            customer.setMiddleName(query.getString(query.getColumnIndex(MIDDLE_NAME)));
            customer.setLastName(query.getString(query.getColumnIndex(LASTNAME)));
        }
        query.close();
        return customer;
    }

    public boolean addCustomer(Customer customer) {
        Customer customer1 = getCustomer();
        if (customer1==null || customer1.getCustomerId() != customer.getCustomerId()) {
            ContentValues values = new ContentValues();
            values.put(CUSTOMER_ID, customer.getCustomerId());
            values.put(EMAIL, customer.getEmailId());
            values.put(PASSWORD, customer.getPassword());
            values.put(FIRST_NAME, customer.getFirstName());
            values.put(MIDDLE_NAME, customer.getMiddleName());
            values.put(LASTNAME, customer.getLastName());
            return getWritableDatabase().insert(DATABASE_TABLE
                    , null, values) > 0;
        }
        return false;
    }
}
