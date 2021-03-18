package com.zybooks.weighttracker;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class WeightDatabase extends SQLiteOpenHelper {

    private static final int VERSION = 1;
    private static final String DATABASE_NAME = "weight.db";
    private static WeightDatabase mWeightDb;

    // Get database
    public static WeightDatabase getInstance(Context context) {
        if (mWeightDb == null) {
            mWeightDb = new WeightDatabase(context);
        }
        return mWeightDb;
    }

    private WeightDatabase(Context context) {
        super(context, DATABASE_NAME, null, VERSION);
    }

    // Define columns
    private static final class WeightTable {
        private static final String TABLE = "weights";
        private static final String COL_ID = "_id";
        private static final String COL_TIME = "time";
        private static final String COL_WEIGHT = "weight";
    }

    // Create database with fake data
    @Override
    public void onCreate(SQLiteDatabase db) {

        // Create weight table
        db.execSQL("CREATE TABLE " + WeightTable.TABLE + " ("
                + WeightTable.COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + WeightTable.COL_TIME + " INTEGER,  "
                + WeightTable.COL_WEIGHT + " REAL )");

        // Add some test weights
        int testPoints = 50;
        float finalWeight = 190f;
        float startingWeight = 210f;
        float decay = 0.05f;
        int millisPerDay = 24*60*60*1000;
        float delta = startingWeight - finalWeight;

        // Create fake data
        Random rand = new Random();
        for (long j=0; j<testPoints; j++) {
            float fakeWeight = Math.round(finalWeight+delta*Math.exp(-decay*(testPoints-j-1))+rand.nextInt(3));
            long fakeTime = System.currentTimeMillis() - millisPerDay*(j+1);
            Weight weight = new Weight(fakeTime, (int)fakeWeight);
            ContentValues values = new ContentValues();
            values.put(WeightTable.COL_TIME, weight.getTime());
            values.put(WeightTable.COL_WEIGHT, weight.getWeight());
            db.insert(WeightTable.TABLE, null, values);
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("drop table if exists " + WeightTable.TABLE);
        onCreate(db);
    }

    public void clearWeights() {
        SQLiteDatabase db = getWritableDatabase();
        db.delete(WeightTable.TABLE, null, null);
        db.close();
    }

    @Override
    public void onOpen(SQLiteDatabase db) {
        super.onOpen(db);
        if (!db.isReadOnly()) {
            db.setForeignKeyConstraintsEnabled(true);
        }
    }

    // *********************************************************************************************
    // Weight table methods
    // *********************************************************************************************

    // Get list of all weights
    public List<Weight> getWeights() {
        List<Weight> weights = new ArrayList<>();

        String orderBy = WeightTable.COL_TIME + " desc";

        SQLiteDatabase db = this.getReadableDatabase();
        String sql = "select * from " + WeightTable.TABLE + " order by " + orderBy;
        Cursor cursor = db.rawQuery(sql, null);
        if (cursor.moveToFirst()) {
            do {
                Weight weight = new Weight();
                weight.setId(cursor.getLong(0));
                weight.setTime(cursor.getLong(1));
                weight.setWeight(cursor.getFloat(2));
                weights.add(weight);
            } while (cursor.moveToNext());
        }
        cursor.close();

        return weights;
    }

    // Get a single weight based on ID value
//    public Weight getWeight(int id) {
//        Weight weight = null;
//
//        SQLiteDatabase db = this.getReadableDatabase();
//        String sql = "select * from " + WeightTable.TABLE + " where " + WeightTable.COL_ID + " = ?";
//        Cursor cursor = db.rawQuery(sql, new String[] { Float.toString(id) });
//
//        if (cursor.moveToFirst()) {
//            weight = new Weight();
//            weight.setId(cursor.getLong(0));
//            weight.setTime(cursor.getLong(2));
//            weight.setWeight(cursor.getFloat(3));
//        }
//        cursor.close();
//
//        return weight;
//    }

    // Add a weight to the database
    public void addWeight(Weight weight) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(WeightTable.COL_TIME, weight.getTime());
        values.put(WeightTable.COL_WEIGHT, weight.getWeight());
        db.insert(WeightTable.TABLE, null, values);
    }

    // Update a weight based on ID value
    public void updateWeight(Weight weight) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(WeightTable.COL_TIME, weight.getTime());
        values.put(WeightTable.COL_WEIGHT, weight.getWeight());
        db.update(WeightTable.TABLE, values,
                WeightTable.COL_ID + " = ?", new String[] { Long.toString(weight.getId()) });
    }

    // Delete a weight based on ID value
    public void deleteWeight(long id) {
        SQLiteDatabase db = getWritableDatabase();
        db.delete(WeightTable.TABLE,
                WeightTable.COL_ID + " = ?", new String[] { Long.toString(id) });
    }

    // Delete a weight based input weight ID value
//    public void deleteWeight(Weight weight) {
//        SQLiteDatabase db = getWritableDatabase();
//        db.delete(WeightTable.TABLE,
//                WeightTable.COL_ID + " = ?", new String[] { Long.toString(weight.getId()) });
//    }
}