//package com.mission.schedule.widget;
//
//
//import com.mission.schedule.activity.MainActivity;
//import com.mission.schedule.R;
//
//import android.app.Service;
//import android.appwidget.AppWidgetManager;
//import android.appwidget.AppWidgetProvider;
//import android.content.Context;
//import android.content.Intent;
//
//public class TsWidgetProvider extends AppWidgetProvider{
//	//进入时间表
//    public static final String broadCastWIGET_CLICKString = "com.mission.schedule.WIGET_CLICK";
//    //进入日程界面
//    public static final String broadCastNOTICE_WIGET_CLICKString = "com.mission.schedule.NOTICE_WIGET_CLICK";
//    //添加日程
//    public static final String broadCastADD_WIGET_CLICKString = "com.mission.schedule.ADD_WIGET_CLICK";
//    @Override
//    public void onDeleted(Context context, int[] appWidgetIds)
//    {
//        super.onDeleted(context, appWidgetIds);
//        dealIds(context, appWidgetIds,-1);
//        Intent intent = new Intent(context, WidgetService.class);
//        intent.setFlags(Service.START_REDELIVER_INTENT);
//        context.stopService(intent);
//    }
//    @Override
//    public void onEnabled(Context context)
//    {
//        super.onEnabled(context);
//    }
//    @Override
//    public void onReceive(Context context, Intent intent)
//    {
//        super.onReceive(context, intent);
//    	if(broadCastWIGET_CLICKString.equals(intent.getAction())){
//            Intent i = new Intent(context, MainActivity.class);
//			i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//			context.startActivity(i);
//        }
//    }
//    @Override
//    public void onUpdate(Context context, AppWidgetManager appWidgetManager,
//            int[] appWidgetIds)  {
//        super.onUpdate(context, appWidgetManager, appWidgetIds);
//        dealIds(context, appWidgetIds,1);
//        // 启动刷新UI的Service
//        Intent intent = new Intent(context, WidgetService.class);
//        context.startService(intent);
//    }
//
//    private void dealIds(Context context,int[] appWidgetIds,int type){
//
//    }
//}
