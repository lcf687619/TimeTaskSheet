package com.mission.schedule.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;
import com.mission.schedule.R;
import com.mission.schedule.annotation.ViewResId;
import com.mission.schedule.bean.NewFocusShareUpImageBackBean;
import com.mission.schedule.bean.NewFocusShareUpImageBean;
import com.mission.schedule.constants.Const;
import com.mission.schedule.constants.URLConstants;
import com.mission.schedule.cutimage.Crop;
import com.mission.schedule.utils.ActivityManager1;
import com.mission.schedule.utils.NetUtil;
import com.mission.schedule.utils.NetUtil.NetWorkState;
import com.mission.schedule.utils.PathFromUriUtils;
import com.mission.schedule.utils.PhotoActionHelper;
import com.mission.schedule.utils.ProgressUtil;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import java.io.File;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class AddSchWebUrlAndImageActivity extends BaseActivity implements
		OnClickListener {

	@ViewResId(id = R.id.top_ll_back)
	private LinearLayout top_ll_back;
	@ViewResId(id = R.id.middle_tv)
	private TextView middle_tv;
	@ViewResId(id = R.id.top_ll_right)
	private RelativeLayout top_ll_right;
	@ViewResId(id = R.id.image_rl)
	private RelativeLayout image_rl;
	@ViewResId(id = R.id.image_iv)
	private ImageView image_iv;
	@ViewResId(id = R.id.editimage_bt)
	private Button editimage_bt;
	@ViewResId(id = R.id.url_tv)
	private EditText url_tv;

	Context context;
	String weburl = "";
	String imagepath = "";
	String newImagePath = "";

	int isedit = 0;// 1新建 2编辑
	int deleteImage = 0;// 1删除
	String deleteImagePath = "";
	private DisplayImageOptions options; // DisplayImageOptions是用于设置图片显示的类
	private ImageLoadingListener animateFirstListener = new AnimateFirstDisplayListener();
	private ImageLoader imageLoader;
	String selectimagepath = "";
	ProgressUtil progressUtil = null;
	boolean isEditFag = false;
	private static final int REQUEST_CODE_CAPTURE_CAMEIA = 1458;
	private String mOutputPath;
	private String mDemoPath;
	ActivityManager1 activityManager = null;

	@Override
	protected void setListener() {
		top_ll_right.setOnClickListener(this);
		top_ll_back.setOnClickListener(this);
		editimage_bt.setOnClickListener(this);
		image_rl.setOnClickListener(this);
	}

	@Override
	protected void setContentView() {
		setContentView(R.layout.activity_addweburlandimage);
	}

	@Override
	protected void init(Bundle savedInstanceState) {
		context = this;
		activityManager = ActivityManager1.getInstance();
		activityManager.addActivities(this);
		// 使用DisplayImageOptions.Builder()创建DisplayImageOptions
		options = new DisplayImageOptions.Builder()
				.showStubImage(R.mipmap.img_null_smal) // 设置图片下载期间显示的图片
				.showImageForEmptyUri(R.mipmap.img_null_smal) // 设置图片Uri为空或是错误的时候显示的图片
				.showImageOnFail(R.mipmap.img_null_smal) // 设置图片加载或解码过程中发生错误显示的图片
				.cacheInMemory(true) // 设置下载的图片是否缓存在内存中
				.cacheOnDisc(true) // 设置下载的图片是否缓存在SD卡中
				// .displayer(new RoundedBitmapDisplayer(20)) // 设置成圆角图片
				.build(); // 创建配置过得DisplayImageOption对象
		imageLoader = ImageLoader.getInstance();
		progressUtil = new ProgressUtil();
		middle_tv.setText("附加信息");
		setImageBackgroundHeight();
		loadData();
	}

	private void setImageBackgroundHeight() {
		LinearLayout.LayoutParams layoutParams = new LayoutParams(
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		layoutParams.width = mScreenWidth;
		layoutParams.height = (int) (mScreenWidth * 0.6);
		image_rl.setLayoutParams(layoutParams);
		RelativeLayout.LayoutParams params = (android.widget.RelativeLayout.LayoutParams) image_iv
				.getLayoutParams();
		params.width = mScreenWidth;
		params.height = (int) (mScreenWidth * 0.6);
		image_iv.setLayoutParams(params);
	}

	@SuppressLint("NewApi")
	private void loadData() {
		imagepath = getIntent().getStringExtra("imagepath");
		weburl = getIntent().getStringExtra("weburl");
		isedit = getIntent().getIntExtra("isedit", 0);
		if (isedit == 1) {
			if(!"".equals(imagepath)){
				String imageurl = URLConstants.图片 + imagepath
						+ "&imageType=14&imageSizeType=1";
				imageLoader.displayImage(imageurl, image_iv, options,
						animateFirstListener);
			}else{
				image_iv.setBackground(null);
			}
			url_tv.setText(weburl);
		} else if (isedit == 2) {
			if (!"".equals(imagepath)) {
				String imageurl = URLConstants.图片 + imagepath
						+ "&imageType=14&imageSizeType=1";
				imageLoader.displayImage(imageurl, image_iv, options,
						animateFirstListener);
			} else {
				image_iv.setBackground(null);
			}
			url_tv.setText(weburl);
		} else {
			image_iv.setBackground(null);
			url_tv.setText("");
		}
		url_tv.setSelection(weburl.length());
	}

	@Override
	protected void setAdapter() {

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.top_ll_back:
			this.finish();
			break;
		case R.id.editimage_bt:
			dialogOnClick();
			break;
		case R.id.image_rl:
			dialogOnClick();
			break;
		case R.id.top_ll_right:
			if (isEditFag) {
				if (NetUtil.getConnectState(context) != NetWorkState.NONE) {
					if (isedit == 1) {
						if (!"".equals(imagepath)) {
							uploadJvBaoContent(2);
						} else {
							uploadJvBaoContent(1);
						}
					} else if (isedit == 2) {
						if (!"".equals(imagepath)) {
							uploadJvBaoContent(2);
						} else {
							uploadJvBaoContent(1);
						}
					} else {
						if (deleteImage == 1) {
							uploadJvBaoContent(3);
						} else {
							uploadJvBaoContent(1);
						}
					}
				} else {
					alertFailDialog(0);
				}
			} else {
				isEditFag = false;
				Intent intent = new Intent();
				intent.putExtra("url", url_tv.getText().toString());
				intent.putExtra("imagepath", imagepath);
				setResult(Activity.RESULT_OK, intent);
				this.finish();
			}
			break;
		default:
			break;
		}
	}

	private void uploadJvBaoContent(int type) {// 1新建 2编辑 3删除
		if(type==1){
			if ("".equals(selectimagepath)) {
				Intent intent = new Intent();
				intent.putExtra("url", url_tv.getText().toString());
				intent.putExtra("imagepath", "");
				setResult(Activity.RESULT_OK, intent);
				AddSchWebUrlAndImageActivity.this.finish();
			}
		}
		HttpUtils httpUtils = new HttpUtils(20000);
		RequestParams params = new RequestParams();
		if (type == 1) {// 新建
			params.addBodyParameter("uploadImage", new File(selectimagepath));
			params.addBodyParameter("fileName", selectimagepath.substring(
					selectimagepath.lastIndexOf("/") + 1,
					selectimagepath.length()));
			params.addBodyParameter("fileType", "1");
		} else if (type == 2) {
			params.addBodyParameter("uploadImage", new File(selectimagepath));
			params.addBodyParameter("fileType", "2");
			params.addBodyParameter("fileName", imagepath);
		} else if (type == 3) {
			if("".equals(selectimagepath)){
				params.addBodyParameter("uploadImage", "");
				params.addBodyParameter("fileType", "3");
				params.addBodyParameter("fileName", deleteImagePath);
			}else{
				params.addBodyParameter("uploadImage", new File(selectimagepath));
				params.addBodyParameter("fileType", "2");
				params.addBodyParameter("fileName", deleteImagePath);
			}
		} else {
			params.addBodyParameter("fileName", selectimagepath.substring(
					selectimagepath.lastIndexOf("/") + 1,
					selectimagepath.length()));
			params.addBodyParameter("fileType", "1");
		}
		progressUtil.ShowProgress(context, true, true, "正在上传图片...");
		httpUtils.send(HttpMethod.POST, URLConstants.新版发现日程图片上传, params,
				new RequestCallBack<String>() {

					@Override
					public void onFailure(HttpException e, String msg) {
						progressUtil.dismiss();
						newImagePath = "";
						Toast.makeText(context, "上传失败！", Toast.LENGTH_SHORT)
								.show();
					}

					@Override
					public void onSuccess(ResponseInfo<String> responseInfo) {
						progressUtil.dismiss();
						if (!TextUtils.isEmpty(responseInfo.result)) {
							try {
								Gson gson = new Gson();
								NewFocusShareUpImageBackBean backBean = gson
										.fromJson(
												responseInfo.result,
												NewFocusShareUpImageBackBean.class);
								if (backBean.status == 0) {
									isEditFag = false;
									List<NewFocusShareUpImageBean> list = backBean.list;
									if (list != null && list.size() > 0) {
										imagepath = list.get(0).newPath
												.replace("\\/", "");
										deleteImagePath = list.get(0).oldPath
												.replace("\\/", "");
										newImagePath = list.get(0).newPath
												.replace("\\/", "");
									} else {
										newImagePath = "";
									}
									Intent intent = new Intent();
									intent.putExtra("url", url_tv.getText().toString());
									intent.putExtra("imagepath", imagepath);
									setResult(Activity.RESULT_OK, intent);
									AddSchWebUrlAndImageActivity.this.finish();
								} else {
									newImagePath = "";
								}
							} catch (JsonSyntaxException e) {
								e.printStackTrace();
							}
						} else {
							newImagePath = "";
						}
					}
				});
		
	}

	/**
	 * 从相册选择对话框
	 */
	private void dialogOnClick() {
		Dialog dialog = new Dialog(context, R.style.dialog_translucent);
		Window window = dialog.getWindow();
		android.view.WindowManager.LayoutParams params = window.getAttributes();// 获取LayoutParams
		params.alpha = 0.92f;
		window.setGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL);
		window.setAttributes(params);// 设置生效

		LayoutInflater fac = LayoutInflater.from(context);
		View more_pop_menu = fac.inflate(R.layout.dialog_addschweburlandimage,
				null);
		dialog.setCanceledOnTouchOutside(true);
		dialog.setContentView(more_pop_menu);
		params.height = android.view.ViewGroup.LayoutParams.WRAP_CONTENT;
		params.width = getWindowManager().getDefaultDisplay().getWidth();
		dialog.show();

		new OnClick(dialog, more_pop_menu);
	}

	class OnClick implements View.OnClickListener {

		private Dialog dialog;
		private TextView selectphoto_tv;
		private TextView clearphoto_tv;
		private TextView cancle_tv;

		@SuppressLint("NewApi")
		public OnClick(Dialog dialog, View view) {
			this.dialog = dialog;
			selectphoto_tv = (TextView) view.findViewById(R.id.selectphoto_tv);
			selectphoto_tv.setOnClickListener(this);
			clearphoto_tv = (TextView) view.findViewById(R.id.clearphoto_tv);
			clearphoto_tv.setOnClickListener(this);
			cancle_tv = (TextView) view.findViewById(R.id.cancle_tv);
			cancle_tv.setOnClickListener(this);
		}

		@SuppressLint("NewApi")
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.cancle_tv:
				dialog.dismiss();
				break;
			case R.id.clearphoto_tv:
				isEditFag = true;
				if (isedit == 2) {
					if ("".equals(imagepath)) {
						deleteImage = 0;
					} else {
						deleteImagePath = imagepath;
						deleteImage = 1;
					}
				} else {
					if(!"".equals(imagepath)){
						deleteImagePath = imagepath;
						deleteImage = 1;
					}else{
						deleteImage = 0;
					}
				}
				isedit = 3;
				image_iv.setImageBitmap(null);
				image_iv.setBackground(null);
				dialog.dismiss();
				break;
			case R.id.selectphoto_tv:
//				ImageCutTools.getInstance().selectPicture(
//						AddSchWebUrlAndImageActivity.this);
				Crop.pickImage(AddSchWebUrlAndImageActivity.this);
				dialog.dismiss();
				break;
			default:
				break;
			}
		}
	}

	@SuppressLint("NewApi")
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		Uri uri = null;
		int imagewidth = 0;
		int imageheigh = 0;
		 if(requestCode == Crop.REQUEST_PICK) {
//             beginCrop( data.getData());
			 File temp = new File(Environment.getExternalStorageDirectory()
					 .getPath() + "/yourAppCacheFolder/");// 自已缓存文件夹
			 if (!temp.exists()) {
				 temp.mkdir();
			 }
			 String fileName = "Temp_" + String.valueOf(System.currentTimeMillis()) + ".png";
			 File cropFile = new File(temp, fileName);
			 Uri outputUri = Uri.fromFile(cropFile);
			 mOutputPath = outputUri.getPath();
			 mDemoPath = PathFromUriUtils.getPath(context, data.getData());
			 PhotoActionHelper.clipImage(this, NewFocusShareImageClipActivity.class).input(mDemoPath).output(mOutputPath)
					 .requestCode(Const.REQUEST_CLIP_IMAGE).start();
         }
		if (data != null
				&& (requestCode == Const.REQUEST_CLIP_IMAGE || requestCode == Const.REQUEST_TAKE_PHOTO)) {
			String path = PhotoActionHelper.getOutputPath(data);
			if (path != null) {
				selectimagepath = path;
				isEditFag = true;
				image_iv.setBackground(null);
				image_iv.setImageBitmap(null);
				image_iv.setImageBitmap(BitmapFactory.decodeFile(path));
			}
		}
//         else if(requestCode == Crop.REQUEST_CROP) {
//             handleCrop( resultCode, data);
//         }
         else if(requestCode == REQUEST_CODE_CAPTURE_CAMEIA) {
//             System.out.println( " REQUEST_CODE_CAPTURE_CAMEIA " + mCurrentPhotoPath);
//             if(mCurrentPhotoPath != null) {
//                 beginCrop( Uri.fromFile( new File( mCurrentPhotoPath)));
//             }
         }
//		if (requestCode == AppConstant.KITKAT_LESS) {
//			if (resultCode == Activity.RESULT_OK) {
//				uri = data.getData();
//				Bitmap bitmap = ImageCutTools.getInstance().getBitmapFromUri(context, uri);
//				if(bitmap!=null){
//					imagewidth = bitmap.getWidth();
//					imageheigh = bitmap.getHeight();
//				}else{
//					imagewidth = 480;
//					imageheigh = 480;
//				}
//				// 调用裁剪方法
//				ImageCutTools.getInstance().cropPictureWithScreen(AddSchWebUrlAndImageActivity.this, uri,imagewidth,imageheigh);
//			}
//		} else if (requestCode == AppConstant.KITKAT_ABOVE) {
//			if (resultCode == Activity.RESULT_OK) {
//				uri = data.getData();
//				Bitmap bitmap = ImageCutTools.getInstance().getBitmapFromUri(context, uri);
//				if(bitmap!=null){
//					imagewidth = bitmap.getWidth();
//					imageheigh = bitmap.getHeight();
//				}else{
//					imagewidth = 480;
//					imageheigh = 480;
//				}
//				// 先将这个uri转换为path，然后再转换为uri
//				String thePath = ImageCutTools.getInstance().getPath(AddSchWebUrlAndImageActivity.this, uri);
//				ImageCutTools.getInstance().cropPictureWithScreen(AddSchWebUrlAndImageActivity.this,
//						Uri.fromFile(new File(thePath)),imagewidth,imageheigh);
//			}
//		} else if (requestCode == AppConstant.INTENT_CROP_MY) {
//			if (resultCode == Activity.RESULT_OK) {
//				isEditFag = true;
//				Bitmap bitmap = data.getParcelableExtra("data");
//				image_iv.setBackground(null);
//				image_iv.setImageBitmap(null);
//				image_iv.setImageBitmap(bitmap);
////				File temp = new File(Environment.getExternalStorageDirectory()
////						.getPath() + "/yourAppCacheFolder/");// 自已缓存文件夹
////				if (!temp.exists()) {
////					temp.mkdir();
////				}
////				File tempFile = new File(temp.getAbsolutePath() + "/"
////						+ Calendar.getInstance().getTimeInMillis() + ".jpg"); // 以时间秒为文件名
////				// 图像保存到文件中
////				FileOutputStream foutput = null;
////				try {
////					foutput = new FileOutputStream(tempFile);
////					if (bitmap.compress(Bitmap.CompressFormat.JPEG, 100,
////							foutput)) {
////						selectimagepath = tempFile.getAbsolutePath();
////						// Toast.makeText(
////						// context,
////						// "已生成缓存文件，等待上传！文件位置："
////						// + tempFile.getAbsolutePath(),
////						// Toast.LENGTH_LONG).show();
////					}
////				} catch (FileNotFoundException e) {
////					e.printStackTrace();
////				}
//			}
//		}
	}
	 @SuppressLint("NewApi") 
	 private void handleCrop(int resultCode, Intent result) {
	        if (resultCode == Activity.RESULT_OK) {
	        	isEditFag = true;
				image_iv.setBackground(null);
				image_iv.setImageBitmap(null);
				image_iv.setImageURI(Crop.getOutput(result));
	        } else if (resultCode == Crop.RESULT_ERROR) {
//	            Toast.makeText(getActivity(), Crop.getError(result).getMessage(),
//	                    Toast.LENGTH_SHORT).show();
	        }
	    }
	private void beginCrop(Uri source) {
//        boolean isCircleCrop = mCheckBox.isChecked();
	  File temp = new File(Environment.getExternalStorageDirectory()
				.getPath() + "/yourAppCacheFolder/");// 自已缓存文件夹
		if (!temp.exists()) {
			temp.mkdir();
		}
        String fileName = "Temp_" + String.valueOf( System.currentTimeMillis())+".png";
        File cropFile = new File( temp, fileName);
        Uri outputUri = Uri.fromFile( cropFile);
        selectimagepath = cropFile.getAbsolutePath();
        new Crop( source).output( outputUri).setCropType(false).withAspect(5, 3).start(this);
    }
	private void alertFailDialog(int type) {
		final AlertDialog builder = new AlertDialog.Builder(context).create();
		builder.show();
		Window window = builder.getWindow();
		android.view.WindowManager.LayoutParams params = window.getAttributes();// 获取LayoutParams
		params.alpha = 0.92f;
		params.gravity = Gravity.CENTER;
		window.setAttributes(params);// 设置生效
		window.setGravity(Gravity.CENTER);
		window.setContentView(R.layout.dialog_alert_ok);
		TextView delete_ok = (TextView) window.findViewById(R.id.delete_ok);
		TextView delete_tv = (TextView) window.findViewById(R.id.delete_tv);
		if (type == 0) {
			delete_tv.setText("请检查您的网络！");
		} else {
			delete_tv.setText("图片上传失败！");
		}
		delete_ok.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				builder.cancel();
			}
		});

	}

	/**
	 * 图片加载第一次显示监听器
	 * 
	 * @author Administrator
	 * 
	 */
	private static class AnimateFirstDisplayListener extends
			SimpleImageLoadingListener {

		static final List<String> displayedImages = Collections
				.synchronizedList(new LinkedList<String>());

		@Override
		public void onLoadingComplete(String imageUri, View view,
				Bitmap loadedImage) {
			if (loadedImage != null) {
				ImageView imageView = (ImageView) view;
				// 是否第一次显示
				boolean firstDisplay = !displayedImages.contains(imageUri);
				if (firstDisplay) {
					// 图片淡入效果
					FadeInBitmapDisplayer.animate(imageView, 500);
					displayedImages.add(imageUri);
				}
			}
		}
	}
}
