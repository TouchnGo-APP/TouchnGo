package com.example.touchngo;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;


public class DatabaseAccess {
    private SQLiteOpenHelper openHelper;
    public SQLiteDatabase database;
    private static DatabaseAccess instance;
        /**
      * Private constructor to aboid object creation from outside classes.
      *
      * @param context
      */
        private DatabaseAccess(Context context) {
            this.openHelper = new DatabaseOpenHelper(context);
        }

        /**
      * Return a singleton instance of DatabaseAccess.
      *
      * @param context the Context
      * @return the instance of DabaseAccess
      */
        public static DatabaseAccess getInstance(Context context) {
            if (instance == null) {
                instance = new DatabaseAccess(context);
            }
            return instance;
        }

        /**
      * Open the database connection.
      */
        public  SQLiteDatabase open() {
            this.database = openHelper.getReadableDatabase();
            Log.i("open","success");
            return database;
        }

        /**
      * Close the database connection.
      */
        public void close() {
            if (database != null) {
                this.database.close();
            }
        }

        /**
      * Read all points from the database.
      *
      * @return a List of points
      */
        public List<String> getQuotes(String point_category,String point_elbe, String point_fk ) {
            List<String> list = new ArrayList<>();
            Cursor cursor = database.rawQuery("SELECT * FROM points WHERE category = :point_category and elbe= :point_elbe and fk = :point_fk ", null);
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                list.add(cursor.getString(2));
                cursor.moveToNext();
            }
            cursor.close();
            return list;

        }
}