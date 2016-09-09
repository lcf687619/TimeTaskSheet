package com.mission.schedule.activity;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.mission.schedule.R;
import com.mission.schedule.annotation.ViewResId;
import com.mission.schedule.constants.Const;
import com.mission.schedule.constants.ShareFile;
import com.mission.schedule.constants.URLConstants;
import com.mission.schedule.cutimage.Crop;
import com.mission.schedule.utils.ActivityManager1;
import com.mission.schedule.utils.PathFromUriUtils;
import com.mission.schedule.utils.PhotoActionHelper;
import com.mission.schedule.utils.SharedPrefUtil;

import java.io.File;

public class SetBackgroundActivity extends BaseActivity implements
		OnClickListener {

	@ViewResId(id = R.id.top_ll_left)
	private LinearLayout top_ll_left;
	@ViewResId(id = R.id.nonebg_rl)
	private RelativeLayout nonebg_rl;
	@ViewResId(id = R.id.item1_bg_rl)
	private RelativeLayout item1_bg_rl;
	@ViewResId(id = R.id.item2_bg_rl)
	private RelativeLayout item2_bg_rl;
	@ViewResId(id = R.id.item3_bg_rl)
	private RelativeLayout item3_bg_rl;
	@ViewResId(id = R.id.item4_bg_rl)
	private RelativeLayout item4_bg_rl;
	@ViewResId(id = R.id.item5_bg_rl)
	private RelativeLayout item5_bg_rl;
	@ViewResId(id = R.id.item6_bg_rl)
	private RelativeLayout item6_bg_rl;
	@ViewResId(id = R.id.item7_bg_rl)
	private RelativeLayout item7_bg_rl;
	@ViewResId(id = R.id.item8_bg_rl)
	private RelativeLayout item8_bg_rl;
	@ViewResId(id = R.id.selectlocal_rl)
	private RelativeLayout selectlocal_rl;

	@ViewResId(id = R.id.select0_cb)
	private CheckBox select0_cb;
	@ViewResId(id = R.id.select1_cb)
	private CheckBox select1_cb;
	@ViewResId(id = R.id.select2_cb)
	private CheckBox select2_cb;
	@ViewResId(id = R.id.select3_cb)
	private CheckBox select3_cb;
	@ViewResId(id = R.id.select4_cb)
	private CheckBox select4_cb;
	@ViewResId(id = R.id.select5_cb)
	private CheckBox select5_cb;
	@ViewResId(id = R.id.select6_cb)
	private CheckBox select6_cb;
	@ViewResId(id = R.id.select7_cb)
	private CheckBox select7_cb;
	@ViewResId(id = R.id.select8_cb)
	private CheckBox select8_cb;

	Context context;
	SharedPrefUtil sharedPrefUtil = null;
	String localpath = "";
	Bitmap bitmap;
	String paths;
	private static final int REQUEST_CODE_CAPTURE_CAMEIA = 1458;
	private String mOutputPath;
    private String mDemoPath;
	ActivityManager1 activityManager = null;
	boolean permission  = false;

	@Override
	protected void setListener() {
		top_ll_left.setOnClickListener(this);
		nonebg_rl.setOnClickListener(this);
		item1_bg_rl.setOnClickListener(this);
		item2_bg_rl.setOnClickListener(this);
		item3_bg_rl.setOnClickListener(this);
		item4_bg_rl.setOnClickListener(this);
		item5_bg_rl.setOnClickListener(this);
		item6_bg_rl.setOnClickListener(this);
		item7_bg_rl.setOnClickListener(this);
		item8_bg_rl.setOnClickListener(this);
		selectlocal_rl.setOnClickListener(this);
	}

	@Override
	protected void setContentView() {
		setContentView(R.layout.activity_setbackground);
	}

	@Override
	protected void init(Bundle savedInstanceState) {
		activityManager = ActivityManager1.getInstance();
		activityManager.addActivities(this);
		context = this;
		sharedPrefUtil = new SharedPrefUtil(context, ShareFile.USERFILE);
		localpath = sharedPrefUtil.getString(context, ShareFile.USERFILE,
				ShareFile.LOCALPATH, "");
		initview();
	}

	private void initview() {
		if (!"".equals(localpath)) {
			if ("0".equals(localpath)) {
				select0_cb.setVisibility(View.VISIBLE);
				select1_cb.setVisibility(View.GONE);
				select2_cb.setVisibility(View.GONE);
				select3_cb.setVisibility(View.GONE);
				select4_cb.setVisibility(View.GONE);
				select5_cb.setVisibility(View.GONE);
				select6_cb.setVisibility(View.GONE);
				select7_cb.setVisibility(View.GONE);
				select8_cb.setVisibility(View.GONE);
				select0_cb.setChecked(true);
			} else if ("1".equals(localpath)) {
				select0_cb.setVisibility(View.GONE);
				select1_cb.setVisibility(View.VISIBLE);
				select2_cb.setVisibility(View.GONE);
				select3_cb.setVisibility(View.GONE);
				select4_cb.setVisibility(View.GONE);
				select5_cb.setVisibility(View.GONE);
				select6_cb.setVisibility(View.GONE);
				select7_cb.setVisibility(View.GONE);
				select8_cb.setVisibility(View.GONE);
				select1_cb.setChecked(true);
			} else if ("2".equals(localpath)) {
				select0_cb.setVisibility(View.GONE);
				select1_cb.setVisibility(View.GONE);
				select2_cb.setVisibility(View.VISIBLE);
				select3_cb.setVisibility(View.GONE);
				select4_cb.setVisibility(View.GONE);
				select5_cb.setVisibility(View.GONE);
				select6_cb.setVisibility(View.GONE);
				select7_cb.setVisibility(View.GONE);
				select8_cb.setVisibility(View.GONE);
				select2_cb.setChecked(true);
			} else if ("3".equals(localpath)) {
				select0_cb.setVisibility(View.GONE);
				select1_cb.setVisibility(View.GONE);
				select2_cb.setVisibility(View.GONE);
				select3_cb.setVisibility(View.VISIBLE);
				select4_cb.setVisibility(View.GONE);
				select5_cb.setVisibility(View.GONE);
				select6_cb.setVisibility(View.GONE);
				select7_cb.setVisibility(View.GONE);
				select8_cb.setVisibility(View.GONE);
				select3_cb.setChecked(true);
			} else if ("4".equals(localpath)) {
				select0_cb.setVisibility(View.GONE);
				select1_cb.setVisibility(View.GONE);
				select2_cb.setVisibility(View.GONE);
				select3_cb.setVisibility(View.GONE);
				select4_cb.setVisibility(View.VISIBLE);
				select5_cb.setVisibility(View.GONE);
				select6_cb.setVisibility(View.GONE);
				select7_cb.setVisibility(View.GONE);
				select8_cb.setVisibility(View.GONE);
				select4_cb.setChecked(true);
			} else if ("5".equals(localpath)) {
				select0_cb.setVisibility(View.GONE);
				select1_cb.setVisibility(View.GONE);
				select2_cb.setVisibility(View.GONE);
				select3_cb.setVisibility(View.GONE);
				select4_cb.setVisibility(View.GONE);
				select5_cb.setVisibility(View.VISIBLE);
				select6_cb.setVisibility(View.GONE);
				select7_cb.setVisibility(View.GONE);
				select8_cb.setVisibility(View.GONE);
				select5_cb.setChecked(true);
			} else if ("6".equals(localpath)) {
				select0_cb.setVisibility(View.GONE);
				select1_cb.setVisibility(View.GONE);
				select2_cb.setVisibility(View.GONE);
				select3_cb.setVisibility(View.GONE);
				select4_cb.setVisibility(View.GONE);
				select5_cb.setVisibility(View.GONE);
				select6_cb.setVisibility(View.VISIBLE);
				select7_cb.setVisibility(View.GONE);
				select8_cb.setVisibility(View.GONE);
				select6_cb.setChecked(true);
			} else if ("7".equals(localpath)) {
				select0_cb.setVisibility(View.GONE);
				select1_cb.setVisibility(View.GONE);
				select2_cb.setVisibility(View.GONE);
				select3_cb.setVisibility(View.GONE);
				select4_cb.setVisibility(View.GONE);
				select5_cb.setVisibility(View.GONE);
				select6_cb.setVisibility(View.GONE);
				select7_cb.setVisibility(View.VISIBLE);
				select8_cb.setVisibility(View.GONE);
				select7_cb.setChecked(true);
			} else if ("8".equals(localpath)) {
				select0_cb.setVisibility(View.GONE);
				select1_cb.setVisibility(View.GONE);
				select2_cb.setVisibility(View.GONE);
				select3_cb.setVisibility(View.GONE);
				select4_cb.setVisibility(View.GONE);
				select5_cb.setVisibility(View.GONE);
				select6_cb.setVisibility(View.GONE);
				select7_cb.setVisibility(View.GONE);
				select8_cb.setVisibility(View.VISIBLE);
				select8_cb.setChecked(true);
			} else {
				select0_cb.setVisibility(View.GONE);
				select1_cb.setVisibility(View.GONE);
				select2_cb.setVisibility(View.GONE);
				select3_cb.setVisibility(View.GONE);
				select4_cb.setVisibility(View.GONE);
				select5_cb.setVisibility(View.GONE);
				select6_cb.setVisibility(View.GONE);
				select7_cb.setVisibility(View.GONE);
				select8_cb.setVisibility(View.GONE);
			}
		} else {
			select0_cb.setVisibility(View.GONE);
			select1_cb.setVisibility(View.GONE);
			select2_cb.setVisibility(View.GONE);
			select3_cb.setVisibility(View.GONE);
			select4_cb.setVisibility(View.GONE);
			select5_cb.setVisibility(View.GONE);
			select6_cb.setVisibility(View.GONE);
			select7_cb.setVisibility(View.GONE);
			select8_cb.setVisibility(View.GONE);
		}
	}

	@Override
	protected void setAdapter() {

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.top_ll_left:
			this.finish();
			break;
		case R.id.nonebg_rl:
			select0_cb.setVisibility(View.VISIBLE);
			select1_cb.setVisibility(View.GONE);
			select2_cb.setVisibility(View.GONE);
			select3_cb.setVisibility(View.GONE);
			select4_cb.setVisibility(View.GONE);
			select5_cb.setVisibility(View.GONE);
			select6_cb.setVisibility(View.GONE);
			select7_cb.setVisibility(View.GONE);
			select8_cb.setVisibility(View.GONE);
			select0_cb.setChecked(true);
			sharedPrefUtil.putString(context, ShareFile.USERFILE,
					ShareFile.LOCALPATH, "0");
			break;
		case R.id.item1_bg_rl:
			select0_cb.setVisibility(View.GONE);
			select1_cb.setVisibility(View.VISIBLE);
			select2_cb.setVisibility(View.GONE);
			select3_cb.setVisibility(View.GONE);
			select4_cb.setVisibility(View.GONE);
			select5_cb.setVisibility(View.GONE);
			select6_cb.setVisibility(View.GONE);
			select7_cb.setVisibility(View.GONE);
			select8_cb.setVisibility(View.GONE);
			select1_cb.setChecked(true);
			sharedPrefUtil.putString(context, ShareFile.USERFILE,
					ShareFile.LOCALPATH, "1");
			break;
		case R.id.item2_bg_rl:
			select0_cb.setVisibility(View.GONE);
			select1_cb.setVisibility(View.GONE);
			select2_cb.setVisibility(View.VISIBLE);
			select3_cb.setVisibility(View.GONE);
			select4_cb.setVisibility(View.GONE);
			select5_cb.setVisibility(View.GONE);
			select6_cb.setVisibility(View.GONE);
			select7_cb.setVisibility(View.GONE);
			select8_cb.setVisibility(View.GONE);
			select2_cb.setChecked(true);
			sharedPrefUtil.putString(context, ShareFile.USERFILE,
					ShareFile.LOCALPATH, "2");
			break;
		case R.id.item3_bg_rl:
			select0_cb.setVisibility(View.GONE);
			select1_cb.setVisibility(View.GONE);
			select2_cb.setVisibility(View.GONE);
			select3_cb.setVisibility(View.VISIBLE);
			select4_cb.setVisibility(View.GONE);
			select5_cb.setVisibility(View.GONE);
			select6_cb.setVisibility(View.GONE);
			select7_cb.setVisibility(View.GONE);
			select8_cb.setVisibility(View.GONE);
			select3_cb.setChecked(true);
			sharedPrefUtil.putString(context, ShareFile.USERFILE,
					ShareFile.LOCALPATH, "3");
			break;
		case R.id.item4_bg_rl:
			select0_cb.setVisibility(View.GONE);
			select1_cb.setVisibility(View.GONE);
			select2_cb.setVisibility(View.GONE);
			select3_cb.setVisibility(View.GONE);
			select4_cb.setVisibility(View.VISIBLE);
			select5_cb.setVisibility(View.GONE);
			select6_cb.setVisibility(View.GONE);
			select7_cb.setVisibility(View.GONE);
			select8_cb.setVisibility(View.GONE);
			select4_cb.setChecked(true);
			sharedPrefUtil.putString(context, ShareFile.USERFILE,
					ShareFile.LOCALPATH, "4");
			break;
		case R.id.item5_bg_rl:
			select0_cb.setVisibility(View.GONE);
			select1_cb.setVisibility(View.GONE);
			select2_cb.setVisibility(View.GONE);
			select3_cb.setVisibility(View.GONE);
			select4_cb.setVisibility(View.GONE);
			select5_cb.setVisibility(View.VISIBLE);
			select6_cb.setVisibility(View.GONE);
			select7_cb.setVisibility(View.GONE);
			select8_cb.setVisibility(View.GONE);
			select5_cb.setChecked(true);
			sharedPrefUtil.putString(context, ShareFile.USERFILE,
					ShareFile.LOCALPATH, "5");
			break;
		case R.id.item6_bg_rl:
			select0_cb.setVisibility(View.GONE);
			select1_cb.setVisibility(View.GONE);
			select2_cb.setVisibility(View.GONE);
			select3_cb.setVisibility(View.GONE);
			select4_cb.setVisibility(View.GONE);
			select5_cb.setVisibility(View.GONE);
			select6_cb.setVisibility(View.VISIBLE);
			select7_cb.setVisibility(View.GONE);
			select8_cb.setVisibility(View.GONE);
			select6_cb.setChecked(true);
			sharedPrefUtil.putString(context, ShareFile.USERFILE,
					ShareFile.LOCALPATH, "6");
			break;
		case R.id.item7_bg_rl:
			select0_cb.setVisibility(View.GONE);
			select1_cb.setVisibility(View.GONE);
			select2_cb.setVisibility(View.GONE);
			select3_cb.setVisibility(View.GONE);
			select4_cb.setVisibility(View.GONE);
			select5_cb.setVisibility(View.GONE);
			select6_cb.setVisibility(View.GONE);
			select7_cb.setVisibility(View.VISIBLE);
			select8_cb.setVisibility(View.GONE);
			select7_cb.setChecked(true);
			sharedPrefUtil.putString(context, ShareFile.USERFILE,
					ShareFile.LOCALPATH, "7");
			break;
		case R.id.item8_bg_rl:
			select0_cb.setVisibility(View.GONE);
			select1_cb.setVisibility(View.GONE);
			select2_cb.setVisibility(View.GONE);
			select3_cb.setVisibility(View.GONE);
			select4_cb.setVisibility(View.GONE);
			select5_cb.setVisibility(View.GONE);
			select6_cb.setVisibility(View.GONE);
			select7_cb.setVisibility(View.GONE);
			select8_cb.setVisibility(View.VISIBLE);
			select8_cb.setChecked(true);
			sharedPrefUtil.putString(context, ShareFile.USERFILE,
					ShareFile.LOCALPATH, "8");
			break;
		case R.id.selectlocal_rl:
			checkPhonePermission();
//			MyImageCutUtils.getInstance().selectPicture(
//					SetBackgroundActivity.this);
			if(permission){
				Crop.pickImage(this);
			}else{
				Toast.makeText(context,"权限被禁止!",Toast.LENGTH_LONG).show();
			}
			break;
		default:
			break;
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//		Uri uri = null;
//		if (requestCode == AppConstant.KITKAT_LESS) {
//			if (resultCode == Activity.RESULT_OK) {
//				uri = data.getData();
//				Log.d("tag", "uri=" + uri);
//				Bitmap bit = null;
//				try {
//					bit = MediaStore.Images.Media.getBitmap(
//							getContentResolver(), uri);
//				} catch (FileNotFoundException e) {
//					e.printStackTrace();
//				} catch (IOException e) {
//					e.printStackTrace();
//				}
//				// 调用裁剪方法
//				MyImageCutUtils.getInstance().cropPicture(this, uri,
//						mScreenWidth, mScreenHeight, bit);
//			}
//		} else if (requestCode == AppConstant.KITKAT_ABOVE) {
//			if (resultCode == Activity.RESULT_OK) {
//				try {
//					uri = data.getData();
//					Log.d("tag", "uri=" + uri);
//					// 先将这个uri转换为path，然后再转换为uri
//					String thePath = ImageCutTools.getInstance().getPath(this,
//							uri);
//
//					Bitmap bit = MediaStore.Images.Media.getBitmap(
//							getContentResolver(), uri);
//					if (bit.getHeight() > bit.getWidth()) {
//
//						MyImageCutUtils.getInstance().cropPicture(this,
//								Uri.fromFile(new File(thePath)), mScreenWidth,
//								mScreenHeight, bit);
//					}
//				} catch (FileNotFoundException e) {
//					e.printStackTrace();
//				} catch (IOException e) {
//					e.printStackTrace();
//				}
//			}
//		} else if (requestCode == AppConstant.INTENT_CROP) {
//			if (resultCode == Activity.RESULT_OK) {
//				bitmap = data.getParcelableExtra("data");
////				Intent intent = new Intent(context, SetUpdateDataCenterActivity.class);
////				intent.putExtra("image", bitmap);
////				startActivity(intent);
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
//					if (bitmap.compress(Bitmap.CompressFormat.JPEG, 100,
//							foutput)) {
//						paths = tempFile.getAbsolutePath();
//						sharedPrefUtil.putString(context, ShareFile.USERFILE,
//								ShareFile.LOCALPATH, paths);
//						// Toast.makeText(
//						// context,
//						// "已生成缓存文件，等待上传！文件位置："
//						// + tempFile.getAbsolutePath(),
//						// Toast.LENGTH_LONG).show();
//					}
//				} catch (FileNotFoundException e) {
//					e.printStackTrace();
//				}
//			}
//		}
		if(resultCode == Activity.RESULT_OK) {
            if(requestCode == Crop.REQUEST_PICK) {
            	File temp = new File(Environment.getExternalStorageDirectory()
                        .getPath() + "/yourAppCacheFolder/");// 自已缓存文件夹
                if (!temp.exists()) {
                    temp.mkdir();
                }
                String fileName = "Temp_" + String.valueOf( System.currentTimeMillis())+".png";
                File cropFile = new File( temp, fileName);
                Uri outputUri = Uri.fromFile(cropFile);
                mOutputPath = outputUri.getPath();
                mDemoPath = PathFromUriUtils.getPath(context,data.getData());
                PhotoActionHelper.clipImage(this,ClipImageActivity.class).input(mDemoPath).output(mOutputPath)
                        .requestCode(Const.REQUEST_CLIP_IMAGE).start();
//                beginCrop( data.getData());
            }
            if ( data != null
                    && (requestCode == Const.REQUEST_CLIP_IMAGE || requestCode == Const.REQUEST_TAKE_PHOTO)) {
                String path = PhotoActionHelper.getOutputPath(data);
                if (path != null) {
                	sharedPrefUtil.putString(context, ShareFile.USERFILE,
    						ShareFile.LOCALPATH, path);
//                    Bitmap bitmap = BitmapFactory.decodeFile(path);
//                    mImageView.setImageBitmap(bitmap);
                }
            }
//            else if(requestCode == Crop.REQUEST_CROP) {
//                handleCrop( resultCode, data);
//            }
//            else if(requestCode == REQUEST_CODE_CAPTURE_CAMEIA) {
////                System.out.println( " REQUEST_CODE_CAPTURE_CAMEIA " + mCurrentPhotoPath);
////                if(mCurrentPhotoPath != null) {
////                    beginCrop( Uri.fromFile( new File( mCurrentPhotoPath)));
////                }
//            }
        }
		super.onActivityResult(requestCode, resultCode, data);
	}
	 private void handleCrop(int resultCode, Intent result) {
	        if (resultCode == Activity.RESULT_OK) {
	            System.out.println(" handleCrop: Crop.getOutput(result) "+Crop.getOutput(result));
	            sharedPrefUtil.putString(context, ShareFile.USERFILE,
						ShareFile.LOCALPATH, paths);
//	            mImageView.setImageURI( Crop.getOutput(result));
//	            mCircleView.setImageBitmap( getCircleBitmap(Crop.getOutput(result)));
	        } else if (resultCode == Crop.RESULT_ERROR) {
//	            Toast.makeText(getActivity(), Crop.getError(result).getMessage(),
//	                    Toast.LENGTH_SHORT).show();
	        }
	    }
	  private void beginCrop(Uri source) {
//	        boolean isCircleCrop = mCheckBox.isChecked();
		  File temp = new File(Environment.getExternalStorageDirectory()
					.getPath() + "/yourAppCacheFolder/");// 自已缓存文件夹
			if (!temp.exists()) {
				temp.mkdir();
			}
	        String fileName = "Temp_" + String.valueOf( System.currentTimeMillis())+".png";
	        File cropFile = new File( temp, fileName);
	        Uri outputUri = Uri.fromFile( cropFile);
	        paths = cropFile.getAbsolutePath();
	        new Crop( source).output( outputUri).setCropType(false).withAspect(mScreenWidth, mScreenHeight).withMaxSize(mScreenWidth, mScreenHeight).start(this);
	    }
	/* 根据uri返回文件路径 */
	public String getPath(Uri uri) {
		String[] proj = { MediaStore.Images.Media.DATA };
		// 好像是android多媒体数据库的封装接口，具体的看Android文档
		Cursor cursor = managedQuery(uri, proj, null, null, null);
		// 按我个人理解 这个是获得用户选择的图片的索引值
		int column_index = cursor
				.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
		// 将光标移至开头 ，这个很重要，不小心很容易引起越界
		cursor.moveToFirst();
		// 最后根据索引值获取图片路径
		return cursor.getString(column_index);
	}
	private void checkPhonePermission() {
		if (Build.VERSION.SDK_INT >= 23) {
			int checkstorgePhonePermission = ContextCompat.checkSelfPermission(context, Manifest.permission_group.STORAGE);
			if(checkstorgePhonePermission != PackageManager.PERMISSION_GRANTED){
				ActivityCompat.requestPermissions(SetBackgroundActivity.this,new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},10001);
			}else {
				permission = true;
			}
		}else{
			permission = true;
		}
	}

	@Override
	public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
		super.onRequestPermissionsResult(requestCode, permissions, grantResults);
		switch (requestCode){
			case 10001:
				if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
					// Permission Granted
					permission = true;
				} else {
					permission = false;
				}
				break;
			default:
				super.onRequestPermissionsResult(requestCode, permissions, grantResults);
		}
	}
}
