package com.fourbuy.android.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.fourbuy.android.Constants;
import com.fourbuy.android.Product;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 3164 on 11-12-2015.
 */
public class ProductsSqliteOpenHelper extends CommdB {

    private static final String DATABASE_TABLE = "CartProducts";
    private static final String CART_ID="cart_id";
    private static final String PRODUCT_ID = "product_id";
    private static final String PRODUCT_NAME = "product_name";
    private static final String SKU = "sku";
    private static final String TYPE = "type";

    public ProductsSqliteOpenHelper(Context context) {
        super(context);
    }

    static void initProduct(SQLiteDatabase db){
        db.execSQL("CREATE TABLE IF NOT EXISTS "
                + DATABASE_TABLE + "("
                + CART_ID + " TEXT NOT NULL,"
                + PRODUCT_ID + " TEXT,"
                + PRODUCT_NAME + " TEXT,"
                + SKU + " TEXT, "
                + TYPE + " TEXT);");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }

    public void addTocart(String cartId,Product product) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(CART_ID,cartId);
        contentValues.put(PRODUCT_ID, product.getProduct_id());
        contentValues.put(PRODUCT_NAME, product.getName());
        contentValues.put(SKU, product.getSku());
        contentValues.put(TYPE, product.getType_id());

        getWritableDatabase().insert(DATABASE_TABLE, null, contentValues);
    }

    public void deleteInCart(String cartId,Product product) {
        getWritableDatabase().delete(DATABASE_TABLE, CART_ID+"? and "+PRODUCT_ID + "=?",
                new String[]{cartId,product.getProduct_id()});
    }

    public void deleteProducts(String cartId) {
        getWritableDatabase().delete(DATABASE_TABLE, CART_ID + "=?",
                new String[]{cartId});
    }

    public List<Product> getProducts(String cartId) {
        List<Product> products = new ArrayList<>();
        SQLiteDatabase readableDatabase = getReadableDatabase();
        Cursor query = readableDatabase.query(DATABASE_TABLE, null,CART_ID+"=?",new String[]{cartId},
                null, null, null);
        for (boolean hasItem = query.moveToFirst(); hasItem; hasItem = query.moveToNext()) {
            // use cursor to work with current item
            Product product = new Product();
            String prodId=query.getString(query.getColumnIndex(PRODUCT_ID));
            product.setProduct_id(prodId);
            product.setName(query.getString(query.getColumnIndex(PRODUCT_NAME)));
            product.setSku(query.getString(query.getColumnIndex(SKU)));
            product.setType_id(query.getString(query.getColumnIndex(TYPE)));
            products.add(product);
        }
        query.close();
        return products;
    }


}
