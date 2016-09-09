package com.mission.schedule.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request.Method;
import com.android.volley.Response;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
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
import com.mission.schedule.applcation.App;
import com.mission.schedule.bean.NewMyFoundShouChangBeen;
import com.mission.schedule.bean.NewMyFoundShouChangListBeen;
import com.mission.schedule.bean.SuccessOrFailBean;
import com.mission.schedule.circleview.CircularImage;
import com.mission.schedule.constants.Const;
import com.mission.schedule.constants.URLConstants;
import com.mission.schedule.cutimage.Crop;
import com.mission.schedule.utils.NetUtil;
import com.mission.schedule.utils.NetUtil.NetWorkState;
import com.mission.schedule.utils.PathFromUriUtils;
import com.mission.schedule.utils.PhotoActionHelper;
import com.mission.schedule.utils.ProgressUtil;
import com.mission.schedule.utils.StringUtils;
import com.mission.schedule.utils.Utils;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import java.io.File;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class SettingNewFocusShareActivity extends BaseActivity implements
		OnClickListener {

	@ViewResId(id = R.id.top_ll_back)
	LinearLayout top_ll_back;
	@ViewResId(id = R.id.middle_tv)
	private TextView middle_tv;
	@ViewResId(id = R.id.top_ll_right)
	private RelativeLayout top_ll_right;
	@ViewResId(id = R.id.title_rl)
	private RelativeLayout title_rl;
	@ViewResId(id = R.id.title_tv)
	private TextView title_tv;
	@ViewResId(id = R.id.headphoto_rl)
	private RelativeLayout headphoto_rl;
	@ViewResId(id = R.id.headphoto_iv)
	private CircularImage headphoto_iv;
	@ViewResId(id = R.id.background_rl)
	private RelativeLayout background_rl;
	@ViewResId(id = R.id.background_iv)
	private ImageView background_iv;
	@ViewResId(id = R.id.updatebackground_bt)
	private Button updatebackground_bt;
	@ViewResId(id = R.id.sharetitle_tv)
	private TextView sharetitle_tv;
	@ViewResId(id = R.id.showrichengmodle_rl)
	private RelativeLayout showrichengmodle_rl;
	@ViewResId(id = R.id.showrichengmodle_tv)
	private TextView showrichengmodle_tv;
	@ViewResId(id = R.id.showrichengcontent_rl)
	private RelativeLayout showrichengcontent_rl;
	@ViewResId(id = R.id.showrichengcontent_tv)
	private TextView showrichengcontent_tv;

	Context context;
	NewMyFoundShouChangListBeen bean = null;
	private DisplayImageOptions options; // DisplayImageOptions是用于设置图片显示的类
	private ImageLoadingListener animateFirstListener = new AnimateFirstDisplayListener();
	private ImageLoader imageLoader;

	public static final int TITLESELECT = 1001;
	public static final int PHOTOSELECT = 1002;
	public static final int BACKGROUNDSELECT = 1003;
	public static final int SHARETITLEELECT = 1004;
	public static final int MODLESELECT = 1005;
	public static final int SHOWCONTENTSELECT = 1006;

	String modlestr = "0";
	String contentstr = "1";
	String photoPath = "";
	private static final int REQUEST_CODE_CAPTURE_CAMEIA = 1458;
	int selecttype = 0;
	Bitmap bitmap = null;

	private String mOutputPath;
	private String mDemoPath;
	boolean permissionFlag = false;

	@Override
	protected void setListener() {
		top_ll_back.setOnClickListener(this);
		title_rl.setOnClickListener(this);
		title_tv.setOnClickListener(this);
		headphoto_rl.setOnClickListener(this);
		headphoto_iv.setOnClickListener(this);
		background_rl.setOnClickListener(this);
		background_iv.setOnClickListener(this);
		updatebackground_bt.setOnClickListener(this);
		sharetitle_tv.setOnClickListener(this);
		showrichengmodle_rl.setOnClickListener(this);
		showrichengmodle_tv.setOnClickListener(this);
		showrichengcontent_rl.setOnClickListener(this);
		showrichengcontent_tv.setOnClickListener(this);
	}

	@Override
	protected void setContentView() {
		setContentView(R.layout.activity_settingnewfocusshare);
	}

	@Override
	protected void init(Bundle savedInstanceState) {
		context = this;
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
		bean = (NewMyFoundShouChangListBeen) getIntent().getSerializableExtra(
				"bean");
		top_ll_right.setVisibility(View.GONE);
		middle_tv.setText("设置");
		loadData();
		title_tv.setText(bean.name);
		if ("".equals(StringUtils.getIsStringEqulesNull(bean.remark5))) {
			sharetitle_tv.setText("");
		} else {
			sharetitle_tv.setText(bean.remark5);
		}
		if ("1".equals(bean.styleView)) {
			modlestr = "1";
			showrichengmodle_tv.setText("图片日程模式");
		} else if ("2".equals(bean.styleView)) {
			showrichengmodle_tv.setText("图片倒数日模式");
			modlestr = "2";
		} else {
			showrichengmodle_tv.setText("纯文本模式");
			modlestr = "0";
		}
		if ("0".equals(bean.remark6)) {
			showrichengcontent_tv.setText("全部↑");
			contentstr = "0";
		} else if ("2".equals(bean.remark6)) {
			showrichengcontent_tv.setText("全部↓");
			contentstr = "2";
		} else {
			showrichengcontent_tv.setText("日程");
			contentstr = "1";
		}
		setheadview();
	}

	private void setheadview() {
		String imagetype = bean.startStateImg;
		String state1 = imagetype.split(",")[0];
		String state2 = imagetype.split(",")[1];
		String imageurl = "";
		if ("0".equals(state1)) {
			imageurl = URLConstants.图片
			+ bean.titleImg.replace("\\/", "")
			+ "&imageType=2&imageSizeType=3";
		}else {
			imageurl = URLConstants.图片
					+ bean.titleImg.replace("\\/", "")
					+ "&imageType=11&imageSizeType=1";

		}
		imageLoader.displayImage(imageurl, headphoto_iv, options,
				animateFirstListener);
		String backgroundimageurl = "";
		if ("0".equals(state2)) {
			backgroundimageurl = URLConstants.背景图片 + "="+ bean.backgroundImg;
		} else {
			backgroundimageurl = URLConstants.图片 + bean.backgroundImg
					+ "&imageType=12&imageSizeType=1";
		}
		imageLoader.displayImage(backgroundimageurl, background_iv,
				options, animateFirstListener);
		LinearLayout.LayoutParams layoutParams = new LayoutParams(
				LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		layoutParams.width = mScreenWidth;
		layoutParams.height = mScreenWidth;
		background_rl.setLayoutParams(layoutParams);
		RelativeLayout.LayoutParams params = (android.widget.RelativeLayout.LayoutParams) background_iv
				.getLayoutParams();
		params.width = mScreenWidth - Utils.dipTopx(context, 20);
		params.height = mScreenWidth - Utils.dipTopx(context, 20);
		background_iv.setLayoutParams(params);
	}

	private void loadData() {
		String path = URLConstants.新版发现我的分享 + "?userId=" + bean.id + "&type=1";
		if (NetUtil.getConnectState(context) != NetWorkState.NONE) {
			LoadDataAsync(path);
		} else {
			Toast.makeText(context, "请检查您的网络..", Toast.LENGTH_SHORT).show();
		}
	}
	@Override
	protected void setAdapter() {

	}

	@Override
	public void onClick(View v) {
		Intent intent = null;
		switch (v.getId()) {
			case R.id.top_ll_back:
				intent = new Intent();
				intent.putExtra("bean", bean);
				setResult(Activity.RESULT_OK, intent);
				this.finish();
				break;
			case R.id.title_rl:
				intent = new Intent(context,
						EditSettingNewFocusShareTitleActivity.class);
				intent.putExtra("title", title_tv.getText().toString());
				intent.putExtra("uid", bean.id);
				intent.putExtra("photopath", bean.titleImg.replace("\\/", ""));
				intent.putExtra("backgroundpath",
						bean.backgroundImg.replace("\\/", ""));
				intent.putExtra("sharetitle", sharetitle_tv.getText().toString());
				intent.putExtra("modlestr", modlestr);
				intent.putExtra("contentstr", contentstr);
				startActivityForResult(intent, TITLESELECT);
				break;
			case R.id.sharetitle_tv:
				intent = new Intent(context,
						EditSettingNewFocusShareSmallTitleActivity.class);
				intent.putExtra("title", title_tv.getText().toString());
				intent.putExtra("uid", bean.id);
				intent.putExtra("photopath", bean.titleImg.replace("\\/", ""));
				intent.putExtra("backgroundpath",
						bean.backgroundImg.replace("\\/", ""));
				intent.putExtra("sharetitle", sharetitle_tv.getText().toString());
				intent.putExtra("modlestr", modlestr);
				intent.putExtra("contentstr", contentstr);
				startActivityForResult(intent, SHARETITLEELECT);
				break;
			case R.id.showrichengmodle_rl:
				intent = new Intent(context, EditNewFocusShareModleActivity.class);
				intent.putExtra("title", title_tv.getText().toString());
				intent.putExtra("uid", bean.id);
				intent.putExtra("photopath", bean.titleImg.replace("\\/", ""));
				intent.putExtra("backgroundpath",
						bean.backgroundImg.replace("\\/", ""));
				intent.putExtra("sharetitle", sharetitle_tv.getText().toString());
				intent.putExtra("modlestr", modlestr);
				intent.putExtra("contentstr", contentstr);
				startActivityForResult(intent, MODLESELECT);
				break;
			case R.id.showrichengmodle_tv:
					intent = new Intent(SettingNewFocusShareActivity.this, EditNewFocusShareModleActivity.class);
					intent.putExtra("title", title_tv.getText().toString());
					intent.putExtra("uid", bean.id);
					intent.putExtra("photopath", bean.titleImg.replace("\\/", ""));
					intent.putExtra("backgroundpath",
							bean.backgroundImg.replace("\\/", ""));
					intent.putExtra("sharetitle", sharetitle_tv.getText().toString());
					intent.putExtra("modlestr", modlestr);
					intent.putExtra("contentstr", contentstr);
					startActivityForResult(intent, MODLESELECT);
				break;
			case R.id.showrichengcontent_rl:
				intent = new Intent(context,
						EditNewFocusShareShowContentActivity.class);
				intent.putExtra("title", title_tv.getText().toString());
				intent.putExtra("uid", bean.id);
				intent.putExtra("photopath", bean.titleImg.replace("\\/", ""));
				intent.putExtra("backgroundpath",
						bean.backgroundImg.replace("\\/", ""));
				intent.putExtra("sharetitle", sharetitle_tv.getText().toString());
				intent.putExtra("modlestr", modlestr);
				intent.putExtra("contentstr", contentstr);
				startActivityForResult(intent, SHOWCONTENTSELECT);
				break;
			case R.id.showrichengcontent_tv:
				intent = new Intent(context,
						EditNewFocusShareShowContentActivity.class);
				intent.putExtra("title", title_tv.getText().toString());
				intent.putExtra("uid", bean.id);
				intent.putExtra("photopath", bean.titleImg.replace("\\/", ""));
				intent.putExtra("backgroundpath",
						bean.backgroundImg.replace("\\/", ""));
				intent.putExtra("sharetitle", sharetitle_tv.getText().toString());
				intent.putExtra("modlestr", modlestr);
				intent.putExtra("contentstr", contentstr);
				startActivityForResult(intent, SHOWCONTENTSELECT);
				break;
			case R.id.headphoto_rl:
				checkPermission();
				if (permissionFlag) {
					selecttype = 0;
					Crop.pickImage(SettingNewFocusShareActivity.this);
				} else {
					Toast.makeText(context, "权限被禁止了!", Toast.LENGTH_LONG).show();
				}
				break;
			case R.id.headphoto_iv:
				checkPermission();
				if (permissionFlag){
					selecttype = 0;
					Crop.pickImage(SettingNewFocusShareActivity.this);
				}else{
					Toast.makeText(context,"权限被禁止了!",Toast.LENGTH_LONG).show();
				}
			break;
		case R.id.updatebackground_bt:
			checkPermission();
			if(permissionFlag) {
				selecttype = 1;
				Crop.pickImage(SettingNewFocusShareActivity.this);
			}else {
				Toast.makeText(context,"权限被禁止了!",Toast.LENGTH_LONG).show();
			}
			break;
		case R.id.background_rl:
			checkPermission();
			if(permissionFlag){
				selecttype = 1;
				Crop.pickImage(SettingNewFocusShareActivity.this);
			}else {
				Toast.makeText(context,"权限被禁止了!",Toast.LENGTH_LONG).show();
			}
			break;
		case R.id.background_iv:
			checkPermission();
			if(permissionFlag) {
				selecttype = 1;
				Crop.pickImage(SettingNewFocusShareActivity.this);
			}else{
				Toast.makeText(context,"权限被禁止了!",Toast.LENGTH_LONG).show();
			}
			break;
		default:
			break;
		}
	}
	@SuppressLint("NewApi") 
	 private void handleCrop(int resultCode, Intent result) {
	        if (resultCode == Activity.RESULT_OK) {
				if (selecttype == 0) {
					bitmap = getBitmapFromUri(Crop.getOutput(result));
					headphoto_iv.setBackground(null);
					headphoto_iv.setImageBitmap(null);
//					headphoto_iv.setImageURI(Crop.getOutput(result));
					headphoto_iv.setImageBitmap(bitmap);
				} else {
					bitmap = getBitmapFromUri(Crop.getOutput(result));
					background_iv.setBackground(null);
					background_iv.setImageBitmap(null);
//					background_iv.setImageURI(Crop.getOutput(result));
					background_iv.setImageBitmap(bitmap);
				}
				if (NetUtil.getConnectState(context) != NetWorkState.NONE) {
					if (selecttype == 0) {
						uploadPhotoData(0);
					} else {
						uploadPhotoData(1);
					}
				} else {
					alertFailDialog(0);
				}
	        } else if (resultCode == Crop.RESULT_ERROR) {
//	            Toast.makeText(getActivity(), Crop.getError(result).getMessage(),
//	                    Toast.LENGTH_SHORT).show();
	        }
	    }
	private Bitmap getBitmapFromUri(Uri uri)
	 {
	  try
	  {
	   // 读取uri所在的图片
	   Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);
	   return bitmap;
	  }
	  catch (Exception e)
	  {
	   return null;
	  }
	 }
	private void beginCrop(Uri source) {
//       boolean isCircleCrop = mCheckBox.isChecked();
	  File temp = new File(Environment.getExternalStorageDirectory()
				.getPath() + "/yourAppCacheFolder/");// 自已缓存文件夹
		if (!temp.exists()) {
			temp.mkdir();
		}
       String fileName = "Temp_" + String.valueOf( System.currentTimeMillis())+".png";
       File cropFile = new File( temp, fileName);
       Uri outputUri = Uri.fromFile( cropFile);
       photoPath = cropFile.getAbsolutePath();
       new Crop( source).output( outputUri).setCropType(false).withAspect(1, 1).start(this);
   }
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == Activity.RESULT_OK) {
			Uri photouri = null;
			if (requestCode != Crop.REQUEST_PICK
					&& requestCode != Crop.REQUEST_CROP
					&& requestCode != REQUEST_CODE_CAPTURE_CAMEIA
					&& requestCode != Const.REQUEST_CLIP_IMAGE
					&& requestCode != Const.REQUEST_TAKE_PHOTO) {
				bean = (NewMyFoundShouChangListBeen) data
						.getSerializableExtra("bean");
			}
			 if(requestCode == Crop.REQUEST_PICK) {
//	             beginCrop( data.getData());
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
				 PhotoActionHelper.clipImage(this, PersonPhotoClipActivity.class).input(mDemoPath).output(mOutputPath)
						 .requestCode(Const.REQUEST_CLIP_IMAGE).start();
	         }
			if (data != null
					&& (requestCode == Const.REQUEST_CLIP_IMAGE || requestCode == Const.REQUEST_TAKE_PHOTO)) {
				String path = PhotoActionHelper.getOutputPath(data);
				if (path != null) {
					photoPath = path;
					bitmap = BitmapFactory.decodeFile(path);
					if (selecttype == 0) {
						headphoto_iv.setBackground(null);
						headphoto_iv.setImageBitmap(null);
//					headphoto_iv.setImageURI(Crop.getOutput(result));
						headphoto_iv.setImageBitmap(bitmap);
					} else {
						background_iv.setBackground(null);
						background_iv.setImageBitmap(null);
//					background_iv.setImageURI(Crop.getOutput(result));
						background_iv.setImageBitmap(bitmap);
					}
					if (NetUtil.getConnectState(context) != NetWorkState.NONE) {
						if (selecttype == 0) {
							uploadPhotoData(0);
						} else {
							uploadPhotoData(1);
						}
					} else {
						alertFailDialog(0);
					}
				}
			}
//	         else if(requestCode == Crop.REQUEST_CROP) {
//	             handleCrop( resultCode, data);
//	         }
	         else if(requestCode == REQUEST_CODE_CAPTURE_CAMEIA) {
//	             System.out.println( " REQUEST_CODE_CAPTURE_CAMEIA " + mCurrentPhotoPath);
//	             if(mCurrentPhotoPath != null) {
//	                 beginCrop( Uri.fromFile( new File( mCurrentPhotoPath)));
//	             }
	         }
			if (requestCode == TITLESELECT) {
				title_tv.setText(bean.name);
			} else if (requestCode == SHARETITLEELECT) {
				sharetitle_tv.setText(bean.remark5);
			} else if (requestCode == MODLESELECT) {
				if ("1".equals(bean.styleView)) {
					modlestr = "1";
					showrichengmodle_tv.setText("图片日程模式");
				} else if ("2".equals(bean.styleView)) {
					showrichengmodle_tv.setText("图片倒数日模式");
					modlestr = "2";
				} else {
					showrichengmodle_tv.setText("纯文本模式");
					modlestr = "0";
				}
			} else if (requestCode == SHOWCONTENTSELECT) {
				if ("0".equals(bean.remark6)) {
					showrichengcontent_tv.setText("全部↑");
					contentstr = "0";
				} else if ("2".equals(bean.remark6)) {
					showrichengcontent_tv.setText("全部↓");
					contentstr = "2";
				} else {
					showrichengcontent_tv.setText("日程");
					contentstr = "1";
				}
			}
//			if (requestCode == AppConstant.KITKAT_LESS) {
//				photouri = data.getData();
//				Log.d("tag", "uri=" + photouri);
//				// 调用裁剪方法
//				ImageCutTools.getInstance().cropPicture(this, photouri);
//			} else if (requestCode == AppConstant.KITKAT_ABOVE) {
//				photouri = data.getData();
//				Log.d("tag", "uri=" + photouri);
//				// 先将这个uri转换为path，然后再转换为uri
//				String thePath = ImageCutTools.getInstance().getPath(this,
//						photouri);
//				ImageCutTools.getInstance().cropPicture(this,
//						Uri.fromFile(new File(thePath)));
//			} else if (requestCode == AppConstant.INTENT_CROP) {
//				photoBitmap = data.getParcelableExtra("data");
//				File temp = new File(Environment.getExternalStorageDirectory()
//						.getPath() + "/yourAppCacheFolder/");// 自已缓存文件夹
//				if (!temp.exists()) {
//					temp.mkdir();
//				}
//				File tempFile = new File(temp.getAbsolutePath() + "/"
//						+ Calendar.getInstance().getTimeInMillis() + ".jpg"); // 以时间秒为文件名
//				// 图像保存到文件中
//				FileOutputStream foutput = null;
//				try {
//					foutput = new FileOutputStream(tempFile);
//					if (photoBitmap.compress(Bitmap.CompressFormat.JPEG, 100,
//							foutput)) {
//						photoPath = tempFile.getAbsolutePath();
//						// Toast.makeText(
//						// context,
//						// "已生成缓存文件，等待上传！文件位置："
//						// + tempFile.getAbsolutePath(),
//						// Toast.LENGTH_LONG).show();
//					}
//					if (selecttype == 0) {
//						headphoto_iv.setImageBitmap(photoBitmap);
//					} else {
//						background_iv.setImageBitmap(photoBitmap);
//					}
//					if (NetUtil.getConnectState(context) != NetWorkState.NONE) {
//						if (selecttype == 0) {
//							uploadPhotoData(0);
//						} else {
//							uploadPhotoData(1);
//						}
//					} else {
//						alertFailDialog(0);
//					}
//				} catch (FileNotFoundException e) {
//					e.printStackTrace();
//				}
//			}
		}
	}

	private void uploadPhotoData(final int type) {// 0 头像 1背景图片
		final ProgressUtil progressUtil = new ProgressUtil();
		progressUtil.ShowProgress(context, true, true, "正在上传...");
		HttpUtils httpUtils = new HttpUtils(10000);
		RequestParams params = new RequestParams();
		String upPath = URLConstants.新版发现上传分享背景图或头像;
		params.addBodyParameter("id", String.valueOf(bean.id));
		if (type == 0) {
			params.addBodyParameter("uploadImage", new File(photoPath));
			params.addBodyParameter("uploadImageFileName", photoPath.substring(
					photoPath.lastIndexOf("/") + 1, photoPath.length()));
		} else {
			params.addBodyParameter("uploadImage", new File(photoPath));
			params.addBodyParameter("uploadImageFileName", photoPath.substring(
					photoPath.lastIndexOf("/") + 1, photoPath.length()));
		}
		params.addBodyParameter("type", String.valueOf(type));
		httpUtils.send(HttpMethod.POST, upPath, params,
				new RequestCallBack<String>() {

					@Override
					public void onFailure(HttpException e, String msg) {
						progressUtil.dismiss();
						Toast.makeText(context, "上传失败！", Toast.LENGTH_SHORT)
								.show();
					}

					@Override
					public void onSuccess(ResponseInfo<String> responseInfo) {
						progressUtil.dismiss();
						if (!TextUtils.isEmpty(responseInfo.result)) {
							Gson gson = new Gson();
							SuccessOrFailBean bean;
							try {
								bean = gson.fromJson(responseInfo.result,
										SuccessOrFailBean.class);
								if (bean.status == 0) {
									if (type == 0) {
										headphoto_iv
												.setImageBitmap(bitmap);
									} else {
										background_iv
												.setImageBitmap(bitmap);
									}
								} else {
									alertFailDialog(1);
								}
							} catch (JsonSyntaxException e) {
								e.printStackTrace();
							}
						} else {
						}
					}
				});

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
			delete_tv.setText("头像上传失败！");
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

	private void LoadDataAsync(String path) {
		final ProgressUtil progressUtil = new ProgressUtil();
		progressUtil.ShowProgress(context, true, true, "正在更新...");
		StringRequest request = new StringRequest(Method.GET, path,
				new Listener<String>() {
					@Override
					public void onResponse(String result) {
						progressUtil.dismiss();
						if (!TextUtils.isEmpty(result)) {
							try {
								Gson gson = new Gson();
								NewMyFoundShouChangBeen backBean = gson
										.fromJson(result,
												NewMyFoundShouChangBeen.class);
								if (backBean.status == 0) {
									List<NewMyFoundShouChangListBeen> fengxingList = backBean.list;
									if (fengxingList != null
											&& fengxingList.size() > 0) {
										bean = fengxingList.get(0);
									}
								}

							} catch (JsonSyntaxException e) {
								e.printStackTrace();
								return;
							}
						} else {
							return;
						}
					}
				}, new Response.ErrorListener() {
					@Override
					public void onErrorResponse(VolleyError volleyError) {
						progressUtil.dismiss();
					}
				});
		request.setTag("down");
		request.setRetryPolicy(new DefaultRetryPolicy(10000, 1, 1.0f));
		App.getHttpQueues().add(request);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		App.getHttpQueues().cancelAll("down");
	}
	private void checkPermission(){
		if (Build.VERSION.SDK_INT >= 23) {
			int checkCallPhonePermission = ContextCompat.checkSelfPermission(mContext, Manifest.permission_group.STORAGE);
			if(checkCallPhonePermission != PackageManager.PERMISSION_GRANTED){
				ActivityCompat.requestPermissions(SettingNewFocusShareActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 10001);
			}else{
				permissionFlag = true;
			}
		}else{
			permissionFlag = true;
		}
	}
	@TargetApi(Build.VERSION_CODES.M)
	@Override
	public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
		super.onRequestPermissionsResult(requestCode, permissions, grantResults);
		switch (requestCode) {
			case 10001:
				if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
					// Permission Granted
					permissionFlag = true;
				} else {
					permissionFlag = false;
				}
				break;
			default:
				super.onRequestPermissionsResult(requestCode, permissions, grantResults);
		}
	}
}
