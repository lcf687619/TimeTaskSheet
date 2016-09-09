package com.mission.schedule.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Build;

/**
 * 保存SharedPreferences工具类
 * @date 2013-11-19 下午3:19:40
 * @author JohnWatson
 * @version 1.0
 */
public class SharedPrefUtil {
	
	private SharedPreferences.Editor mEditor;
	private SharedPreferences mSharedPreferences;
	
	public SharedPrefUtil(Context context,String name){
		mSharedPreferences = context.getSharedPreferences(name, Context.MODE_PRIVATE);
		mEditor = mSharedPreferences.edit();
	}
	
	public Editor putInt(String key,int value){
		return mEditor.putInt(key, value);
	}
	
	public Editor putFloat(String key,float value){
		return mEditor.putFloat(key, value);
	}
	
	public Editor putLong(String key,long value){
		return mEditor.putLong(key, value);
	}
	
	public Editor putBoolean(String key,boolean value){
		return mEditor.putBoolean(key, value);
	}
	
	public Editor putString(String key,String value){
		return mEditor.putString(key, value);
	}
	
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	public Editor putStringSet(String key,Set<String> defaultValue){
		return mEditor.putStringSet(key, defaultValue);
	}
	
	public int getInt(String key,int defaultValue){
		return mSharedPreferences.getInt(key, defaultValue);
	}
	
	public float getFloat(String key,float defaultValue){
		return mSharedPreferences.getFloat(key, defaultValue);
	}
	
	public long getLong(String key,long defaultValue){
		return mSharedPreferences.getLong(key, defaultValue);
	}
	
	public boolean getBoolean(String key,boolean defaultValue){
		return mSharedPreferences.getBoolean(key, defaultValue);
	}
	
	public String getString(String key,String defaultValue){
		return mSharedPreferences.getString(key, defaultValue);
	}
	/**
	 * 存String值
	 * 
	 * @param context
	 * @param fileName
	 * @param key
	 * @param value
	 */
	public  void putString(Context context, String fileName, String key,
			String value) {
		SharedPreferences sharedPreferences = context.getSharedPreferences(
				fileName, context.MODE_PRIVATE);
		Editor editor = sharedPreferences.edit();
		editor.putString(key, value);
		editor.commit();
	}

	/**
	 * 取String值
	 * 
	 * @param context
	 * @param fileName
	 * @param key
	 * @param value
	 *            不存在时返回此值
	 * @return
	 */

	public String getString(Context context, String fileName,
			String key, String value) {
		SharedPreferences sharedPreferences = context.getSharedPreferences(
				fileName, context.MODE_PRIVATE);
		return sharedPreferences.getString(key, value);

	}
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	public Set<String> getStringSet(String key,Set<String> defaultValue){
		return mSharedPreferences.getStringSet(key, defaultValue);
	}
	
	public void commit() {
		mEditor.commit();
    }
}
