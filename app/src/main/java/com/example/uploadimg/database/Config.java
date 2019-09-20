package com.example.uploadimg.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

public class Config {

    private static MyHelper helper;

    public Config(Context context) {

        helper = new MyHelper(context, "qiniuyun.db", null, 1);

    }

    public static void update(Key key) {

        SQLiteDatabase db = helper.getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put("accesskey", key.getAccesskey());
        values.put("secretkey", key.getSecretkey());
        values.put("bucket", key.getBucket());
        values.put("domain", key.getDomain());

        db.update("Key", values, null, null);

        db.close();
    }

    public static List<Key> queryAll() {

        SQLiteDatabase db = helper.getReadableDatabase();

        Cursor c = db.query("Key", null, null, null, null, null, null);

        List<Key> list = new ArrayList<>();

        while (c.moveToNext()) {

            String accesskey = c.getString(0);
            String secretkey = c.getString(1);
            String bucket = c.getString(2);
            String domain = c.getString(3);

            list.add(new Key(accesskey, secretkey, bucket, domain));
        }

        c.close();

        db.close();

        return list;
    }
    
}