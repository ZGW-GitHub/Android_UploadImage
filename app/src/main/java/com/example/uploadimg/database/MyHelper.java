package com.example.uploadimg.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * 数据库操作工具类
 */
public class MyHelper extends SQLiteOpenHelper {

	// 有参构造方法
	MyHelper(Context context, String name, CursorFactory factory, int version) {
		// 调用父类的构造方法，创建数据库
		super(context, name, factory, version);
	}


	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL("create table `key`(accesskey varchar(50), secretkey varchar(50), bucket varchar(50), domain varchar(50))");
		db.execSQL("insert into `key`(accesskey, secretkey, bucket, domain) values('mlY9EeELUucBbhlCvF1OGSuSzoD1irQf4KTD3ur1','7WrlxqvRZsodRmOF6wb43aBzRkdBtz7EKVQ-xmFe','imageblogs','images.notuptoyou.site')");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {}

}
