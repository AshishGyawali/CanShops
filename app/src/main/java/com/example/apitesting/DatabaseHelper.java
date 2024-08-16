package com.example.apitesting;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "products_orders.db";
    private static final int DATABASE_VERSION = 1;

    private static final String TABLE_PRODUCTS = "Products";
    private static final String TABLE_ORDERS = "Orders";

    private static final String CREATE_PRODUCTS_TABLE = "CREATE TABLE " + TABLE_PRODUCTS + " (" +
            "product_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
            "name TEXT, " +
            "description TEXT, " +
            "price REAL)";

    private static final String CREATE_ORDERS_TABLE = "CREATE TABLE " + TABLE_ORDERS + " (" +
            "order_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
            "product_id INTEGER, " +
            "product_name TEXT, " +
            "quantity INTEGER, " +
            "amount REAL, " +
            "username TEXT, " +
            "FOREIGN KEY(product_id) REFERENCES Products(product_id))";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_PRODUCTS_TABLE);
        db.execSQL(CREATE_ORDERS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PRODUCTS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ORDERS);
        onCreate(db);
    }
}
