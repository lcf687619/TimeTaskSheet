//package com.mission.schedule.service;
//
//import com.mission.schedule.widget.WidgetService;
//import com.mission.schedule.service.StrongService;
//
//import android.app.Service;
//import android.content.Intent;
//import android.os.IBinder;
//import android.os.RemoteException;
//import android.widget.Toast;
//
//public class Service1 extends Service {
//
//	private String TAG = getClass().getName();
//	// 用于判断进程是否运行
//	private String Process_Name = "com.mission.schedule:service2";
//
//	/**
//	 *启动Service2
//	 */
//	private StrongService startS2 = new StrongService.Stub() {
//		@Override
//		public void stopService() throws RemoteException {
//			Intent i = new Intent(getBaseContext(), Service2.class);
//			getBaseContext().stopService(i);
//		}
//
//		@Override
//		public void startService() throws RemoteException {
//			Intent i = new Intent(getBaseContext(), Service2.class);
//			getBaseContext().startService(i);
//		}
//	};
//
//	@Override
//	public void onTrimMemory(int level){
//		Toast.makeText(getBaseContext(), "Service1 onTrimMemory..."+level, Toast.LENGTH_SHORT)
//		.show();
//
//		keepService2();//保持Service2一直运行
//
//	}
//
//	@Override
//	public void onCreate() {
//		Toast.makeText(Service1.this, "Service1 onCreate...", Toast.LENGTH_SHORT)
//				.show();
//		keepService2();
//	}
//
//	/**
//	 * 判断Service2是否还在运行，如果不是则启动Service2
//	 */
//	private  void keepService2(){
//		boolean isRun = ServiceUtils.isProessRunning(Service1.this, Process_Name);
//		if (isRun == false) {
//			try {
//				Toast.makeText(getBaseContext(), "重新启动 Service2", Toast.LENGTH_SHORT).show();
//				startS2.startService();
//				Intent intent = new Intent(getApplication(), WidgetService.class);
//				intent.setFlags(Service.START_REDELIVER_INTENT);
//				intent.setAction("com.mission.schedule.widget.WidgetService");
//				intent.setPackage("com.mission.schedule");
//				intent.putExtra("autoStart", "0");
//				startService(intent);
//			} catch (RemoteException e) {
//				e.printStackTrace();
//			}
//		}
//	}
//
//	@Override
//	public int onStartCommand(Intent intent, int flags, int startId) {
//		return START_STICKY;
//	}
//
//	@Override
//	public IBinder onBind(Intent intent) {
//		return (IBinder) startS2;
//	}
//}
