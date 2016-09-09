package com.mission.schedule.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase.CursorFactory;

public class DBHelper extends SQLiteOpenHelper {

	private final static int VERSION = 1;// 版本号

	// 自带的构造方法
	public DBHelper(Context context, String name, CursorFactory factory,
			int version) {
		super(context, name, factory, version);
	}

	// 为了每次构造时不用传入dbName和版本号，自己得新定义一个构造方法
	public DBHelper(Context cxt) {
		this(cxt, DBSourse.dataBaseName, null, VERSION);// 调用上面的构造方法
	}

	// 版本变更时
	public DBHelper(Context cxt, int version) {
		this(cxt, DBSourse.dataBaseName, null, version);
	}

	// 当数据库创建的时候调用
	public void onCreate(SQLiteDatabase db) {

	}

	// 版本更新时调用
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		try {
			
			db.beginTransaction();
			db.setTransactionSuccessful(); // 设置事务处理成功，不设置会自动回滚不提交
			db.endTransaction(); // 处理完成
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
}
