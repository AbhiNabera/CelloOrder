package com.example.wonglab.jmorder.Database;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

import java.util.concurrent.atomic.AtomicInteger;


public class DatabaseHelper extends OrmLiteSqliteOpenHelper {


    private static final String DATABASE_NAME = "ordersDb";
    private static final int DATABASE_VERSION = 1;
    private Dao<Order, String> orderDao;
    private Dao<Item, String> itemDao;

    private static final AtomicInteger usageCounter = new AtomicInteger(0);
    private static DatabaseHelper helper = null;

    public DatabaseHelper(Context context) {
        super(context,DATABASE_NAME,null,DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase database, ConnectionSource connectionSource) {
        try {
            TableUtils.createTable(connectionSource,Order.class);
            TableUtils.createTable(connectionSource,Item.class);
        }  catch (java.sql.SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase database, ConnectionSource connectionSource, int oldVersion, int newVersion) {
        try {
            TableUtils.dropTable(connectionSource,Order.class,false);
            TableUtils.dropTable(connectionSource,Item.class,false);
            onCreate(database,connectionSource);
        }  catch (java.sql.SQLException e) {
            e.printStackTrace();
        }
    }


    public Dao<Item, String> getItemDao() throws SQLException, java.sql.SQLException {
        if(itemDao == null)
            itemDao = getDao(Item.class);
        return itemDao;
    }

    public static synchronized DatabaseHelper getHelper(Context context) {
        if (helper == null) {
            helper = new DatabaseHelper(context);
        }
        usageCounter.incrementAndGet();
        return helper;
    }

    public Dao<Order, String> getOrderDao() throws SQLException, java.sql.SQLException {
        if(orderDao == null)
            orderDao = getDao(Order.class);
        return orderDao;
    }

    @Override
    public void close() {
        if (usageCounter.decrementAndGet() == 0) {
            super.close();
            itemDao = null;
            orderDao = null;
            helper = null;
        }
    }

}
