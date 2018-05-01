package com.example.slymn.ilk_sql;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Slymn on 19.04.2018.
 */

public class sql_katmani extends SQLiteOpenHelper {


    public sql_katmani(Context c) {
        super(c, "deger", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
  String sql="create table deger (motor text,panel text,tarih text)";
        db.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int eski, int yeni) {
        db.execSQL("drop table if exists deger");
    }
}
