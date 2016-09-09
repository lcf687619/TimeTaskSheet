package com.mission.schedule.utils;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import android.widget.ImageView;

public class ImageManager {
    public static ImageLoader getInstance(){  
        return ImageLoader.getInstance();  
    }
	
    public static void Load(String imgUrl,ImageView imageView){  
        ImageLoader.getInstance().displayImage(imgUrl, imageView);  
    }
    
    public static void Load(String imgUrl,ImageView imageView,DisplayImageOptions o){  
        ImageLoader.getInstance().displayImage(imgUrl, imageView,o);  
    }
    
    public static void Load(String imgUrl,ImageView imageView,DisplayImageOptions options,ImageLoadingListener listener){  
        ImageLoader.getInstance().displayImage(imgUrl, imageView,options,listener);  
    } 
}
