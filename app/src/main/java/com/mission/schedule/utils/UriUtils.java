package com.mission.schedule.utils;

import android.content.ContentResolver;
import android.content.Context;
import android.content.res.Resources;
import android.net.Uri;



public class UriUtils {
	/**
	 * 根据资源获取Uri
	 * @param context
	 * @return
	 */
	public static Uri convertRes2Uri(Context context,int resid){
		Resources r = context.getResources();
		return  Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" 
			    + r.getResourcePackageName(resid) + "/"
			    + r.getResourceTypeName(resid) + "/"
			    + r.getResourceEntryName(resid));
	}
	
	/**
	 * 根据网址获取Uri
	 * @param context
	 * @return
	 */
	public static Uri convertUrl2Uri(Context context,String url){
		return  Uri.parse(url);
	}

}
