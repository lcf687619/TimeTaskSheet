package com.mission.schedule.activity;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
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
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request.Method;
import com.android.volley.Response;
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
import com.mission.schedule.bean.MyMessageBackBean;
import com.mission.schedule.bean.MyMessageBean;
import com.mission.schedule.bean.SuccessOrFailBean;
import com.mission.schedule.circleview.CircularImage;
import com.mission.schedule.constants.Const;
import com.mission.schedule.constants.ShareFile;
import com.mission.schedule.constants.URLConstants;
import com.mission.schedule.cutimage.Crop;
import com.mission.schedule.utils.ImageCutUtils;
import com.mission.schedule.utils.ImageUtils;
import com.mission.schedule.utils.NetUtil;
import com.mission.schedule.utils.NetUtil.NetWorkState;
import com.mission.schedule.utils.PathFromUriUtils;
import com.mission.schedule.utils.PhotoActionHelper;
import com.mission.schedule.utils.ProgressUtil;
import com.mission.schedule.utils.SharedPrefUtil;
import com.mission.schedule.utils.StringUtils;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import java.io.File;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class PersonMessageActivity extends BaseActivity {

    @ViewResId(id = R.id.top_ll_back)
    private LinearLayout top_ll_back;// 返回
    @ViewResId(id = R.id.image_img)
    private CircularImage image_img;// 个人头像
    // @ViewResId(id = R.id.image_linear)
    // private RelativeLayout image_linear;// 头像
    // @ViewResId(id = R.id.state_re)
    // private RelativeLayout state_re;
    @ViewResId(id = R.id.nicheng_tv)
    private TextView nicheng_tv;
    @ViewResId(id = R.id.userID_tv)
    private TextView userID_tv;
    @ViewResId(id = R.id.useremail_tv)
    private TextView useremail_tv;
    @ViewResId(id = R.id.userphone_tv)
    private TextView userphone_tv;
    @ViewResId(id = R.id.state_tv)
    private TextView state_tv;
    @ViewResId(id = R.id.photo_rl)
    private RelativeLayout photo_rl;
    @ViewResId(id = R.id.nicheng_ll)
    private RelativeLayout nicheng_ll;
    @ViewResId(id = R.id.email_ll)
    private RelativeLayout email_ll;
    @ViewResId(id = R.id.telephone_ll)
    private RelativeLayout telephone_ll;
    @ViewResId(id = R.id.alterpassword_re)
    private RelativeLayout alterpassword_re;
    @ViewResId(id = R.id.remark_rl)
    private RelativeLayout remark_rl;
    @ViewResId(id = R.id.remark_tv)
    private TextView remark_tv;

    Context context;
    // private final static int STATE_CHOOSE = 1;// 状态选择
    private final static int STATE_NAME = 10;// 修改名称
    private final static int STATE_EMAIL = 11;// 修改邮箱
    private final static int STATE_TELEPHONE = 12;// 修改手机号
    private final static int STATE_PASSWORD = 13;// 修改密码
    private final static int STATE_REMARK = 14;//修改签名

    SharedPrefUtil sharedPrefUtil = null;
    String userId;
    String name;
    String email;
    String telephone;
    String U_ACC_NO;
    String remark;
    Bitmap bitmap = null;

    private static final int REQUEST_CAMERA_CODE = 6;// 相机选择器
    private static final int REQUEST_ALBUM_CODE = 7;// 相册选择器
    private static final int REQUEST_SAVE_CODE = 8;// 保存选择
    private String realPath;// 原图路径
    private String clipPath = "";// 裁剪过后的图片路径
    private boolean isRealImg;// 判断是否是相机拍摄的图
    private ProgressUtil progressUtil = new ProgressUtil();
    String paths;
    private String mOutputPath;
    private String mDemoPath;
    private DisplayImageOptions options; // DisplayImageOptions是用于设置图片显示的类
    private ImageLoadingListener animateFirstListener = new AnimateFirstDisplayListener();
    private ImageLoader imageLoader;
    boolean permissionFlag = false;

    @Override
    protected void setContentView() {
        setContentView(R.layout.activity_personmessageactivity);
    }

    @Override
    protected void init(Bundle savedInstanceState) {
        context = this;
        sharedPrefUtil = new SharedPrefUtil(context, ShareFile.USERFILE);
        options = new DisplayImageOptions.Builder()
                .showStubImage(R.mipmap.img_null_smal)
                .showImageForEmptyUri(R.mipmap.img_null_smal)
                .showImageOnFail(R.mipmap.img_null_smal).cacheInMemory(true)
                .cacheOnDisc(true).cacheInMemory(true)
                .bitmapConfig(Bitmap.Config.RGB_565) // 设置图片的解码类型
                .build();
        imageLoader = ImageLoader.getInstance();
        String image_head = sharedPrefUtil.getString(context,
                ShareFile.USERFILE, ShareFile.USERPHOTOPATH, "");
        String imageUrl = URLConstants.图片+image_head + "&imageType=2&imageSizeType=3";
        imageLoader.displayImage(imageUrl, image_img, options,
                animateFirstListener);
//        FileUtils.loadRoundHeadImg(this, ParameterUtil.userHeadImg, image_img,
//                imageUrl);
        userId = sharedPrefUtil.getString(context, ShareFile.USERFILE,
                ShareFile.USERID, "");
        U_ACC_NO = sharedPrefUtil.getString(context, ShareFile.USERFILE,
                ShareFile.U_ACC_NO, "");
        userID_tv.setText(U_ACC_NO);
        name = sharedPrefUtil.getString(context, ShareFile.USERFILE,
                ShareFile.USERNAME, "");
        nicheng_tv.setText(name);
        email = sharedPrefUtil.getString(context, ShareFile.USERFILE,
                ShareFile.USEREMAIL, "");
        telephone = sharedPrefUtil.getString(context, ShareFile.USERFILE,
                ShareFile.TELEPHONE, "");
        remark = sharedPrefUtil.getString(context, ShareFile.USERFILE, ShareFile.PERSONREMARK, "");
        if (!"".equals(StringUtils.getIsStringEqulesNull(email))) {
            useremail_tv.setText(email);
        } else {

        }
        if (!"".equals(StringUtils.getIsStringEqulesNull(telephone))) {
            userphone_tv.setText(telephone);
        } else {
            userphone_tv.setText("");
        }
        if (!"".equals(StringUtils.getIsStringEqulesNull(remark))) {
            remark_tv.setText(remark);
        } else {
            remark_tv.setText("");
        }
    }

    @Override
    protected void setAdapter() {
    }

    private OnClickListener onClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = null;
            switch (v.getId()) {
                case R.id.top_ll_back:
                    finish();
                    break;
                case R.id.image_img:
                    checkPermission();
                    if(permissionFlag){
                        Crop.pickImage(PersonMessageActivity.this);
                    }else {
                        Toast.makeText(context,"权限已禁止访问!",Toast.LENGTH_LONG).show();
                    }
//				ImageCutTools.getInstance().selectPicture(
//						PersonMessageActivity.this);
                    // isRealImg = false;// 不是相机拍摄的图
                    // ImageCutUtils.openLocalImage(PersonMessageActivity.this);
                    // dialogShow();
                    break;
                case R.id.photo_rl:// 修改头像
                    checkPermission();
                    // dialogShow();
                    // isRealImg = false;// 不是相机拍摄的图
                    // ImageCutUtils.openLocalImage(PersonMessageActivity.this);
//				ImageCutTools.getInstance().selectPicture(
//						PersonMessageActivity.this);
                    if(permissionFlag){
                        Crop.pickImage(PersonMessageActivity.this);
                    }else {
                        Toast.makeText(context,"权限已禁止访问!",Toast.LENGTH_LONG).show();
                    }
                    break;
                case R.id.alterpassword_re:// 修改密码
                    intent = new Intent(context, AlterPasswordActivity.class);
                    startActivityForResult(intent, STATE_PASSWORD);
                    break;
                case R.id.email_ll:// 修改邮箱
                    intent = new Intent(context, AlterEMailActivity.class);
                    startActivityForResult(intent, STATE_EMAIL);
                    break;
                case R.id.telephone_ll:// 修改手机号
                    intent = new Intent(context, AlterTelephoneActivity.class);
                    startActivityForResult(intent, STATE_TELEPHONE);
                    break;
                // case R.id.state_re:
                // intent = new Intent(context, StateActivity.class);
                // intent.putExtra("statename", state_tv.getText().toString());
                // startActivityForResult(intent, STATE_CHOOSE);
                // break;
                case R.id.nicheng_ll:// 修改昵称
                    intent = new Intent(context, AlterPersonNameActivity.class);
                    intent.putExtra("name", nicheng_tv.getText().toString().trim());
                    startActivityForResult(intent, STATE_NAME);
                    break;
                case R.id.remark_rl://修改签名
                    intent = new Intent(context, AlterPersonTagActivity.class);
                    intent.putExtra("tag", remark_tv.getText().toString());
                    startActivityForResult(intent, STATE_REMARK);
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    protected void setListener() {
        top_ll_back.setOnClickListener(onClickListener);
        image_img.setOnClickListener(onClickListener);
        // image_linear.setOnClickListener(onClickListener);
        alterpassword_re.setOnClickListener(onClickListener);
        // state_re.setOnClickListener(onClickListener);
        nicheng_ll.setOnClickListener(onClickListener);
        photo_rl.setOnClickListener(onClickListener);
        email_ll.setOnClickListener(onClickListener);
        telephone_ll.setOnClickListener(onClickListener);
        remark_rl.setOnClickListener(onClickListener);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Uri uri = null;
        // if (STATE_CHOOSE == requestCode) {
        // if (resultCode == Activity.RESULT_OK) {
        // String state = data.getStringExtra("state");
        // state_tv.setText(state);
        // }
        // } else
        if (STATE_NAME == requestCode) {
            if (resultCode == Activity.RESULT_OK) {
                String nicheng = data.getStringExtra("name");
                nicheng_tv.setText(nicheng);
            }
        } else if (STATE_EMAIL == requestCode) {
            if (resultCode == Activity.RESULT_OK) {
                String email = data.getStringExtra("email");
                useremail_tv.setText(email);
            }
        } else if (STATE_TELEPHONE == requestCode) {
            if (resultCode == Activity.RESULT_OK) {
                String telephone = data.getStringExtra("telephone");
                userphone_tv.setText(telephone);
            }
        } else if (STATE_REMARK == requestCode) {
            if (resultCode == Activity.RESULT_OK) {
                String tag = data.getStringExtra("tag");
                remark_tv.setText(tag);
            }
        }
        // else if (requestCode == ImageCutUtils.GET_IMAGE_BY_CAMERA) {
        // if (resultCode == Activity.RESULT_OK) {
        // File picture = new File(
        // Environment.getExternalStorageDirectory() + "/temp.jpg");

        // realPath = getPath(ImageCutUtils.imageUriFromCamera);// 相机拍摄的原图路径
        // ImageCutUtils.cropImage(this, data.getData());

        // Uri uri = Uri.fromFile(picture);
        // Intent intent = new Intent("com.android.camera.action.CROP");
        // intent.setDataAndType(uri, "image/*");
        // intent.putExtra("crop", "true");
        // // aspectX aspectY 是宽高的比例
        // intent.putExtra("aspectX", 10);
        // intent.putExtra("aspectY", 10);
        // intent.putExtra("outputX", 320);
        // intent.putExtra("outputY", 320);
        // intent.putExtra("return-data", true);
        // startActivityForResult(intent,
        // ImageCutUtils.GET_IMAGE_FROM_PHONE);
        // if(ImageCutUtils.imageUriFromCamera != null) {
        // 可以直接显示图片,或者进行其他处理(如压缩或裁剪等)
        // iv.setImageURI(ImageUtils.imageUriFromCamera);

        // 对图片进行裁剪
        // ImageCutUtils.cropImage(this, ImageCutUtils.imageUriFromCamera);
        // }
        // }
        // } else if (requestCode == ImageCutUtils.GET_IMAGE_FROM_PHONE) {
        // if (resultCode == Activity.RESULT_OK) {
        // if (data != null && data.getData() != null) {
        // // 可以直接显示图片,或者进行其他处理(如压缩或裁剪等)
        // // iv.setImageURI(data.getData());
        //
        // // 对图片进行裁剪
        // ImageCutUtils.cropImage(this, data.getData());
        // }
        // }
        // } else if (requestCode == ImageCutUtils.CROP_IMAGE) {
        // if (resultCode == Activity.RESULT_OK) {
        // if (ImageCutUtils.cropImageUri != null) {
        // Bitmap photo = null;
        // Uri photoUri = data.getData();
        // // 可以直接显示图片,或者进行其他处理(如压缩等)
        // if (photoUri != null) {
        // photo = BitmapFactory.decodeFile(photoUri.getPath());
        // }
        // if (photo == null) {
        // String filepath = getPath(ImageCutUtils.cropImageUri);
        // photo = lessenUriImage(filepath);
        // // Bundle extra = data.getExtras();
        // // if (extra != null) {
        // // photo = (Bitmap) extra.get("data");
        // ByteArrayOutputStream stream = new ByteArrayOutputStream();
        // photo.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        // // 将选择的图片放在临时文件夹中
        // File f = new File(ParameterUtil.saveImgTempPath);
        // if (!f.exists()) {
        // f.mkdirs();
        // }
        //
        // String imageName = System.currentTimeMillis()
        // + (int) (Math.random() * 10000) + ".jpg";
        //
        // byte[] array = stream.toByteArray();
        // File ff = new File(ParameterUtil.saveImgTempPath
        // + imageName);
        //
        // FileOutputStream outStream;
        // try {
        // outStream = new FileOutputStream(ff);
        // outStream.write(array);
        // outStream.flush();
        // outStream.close();
        // } catch (FileNotFoundException ee) {
        // ee.printStackTrace();
        // } catch (IOException ee) {
        // ee.printStackTrace();
        // }
        // if (isRealImg) {// 删除相机拍摄的原图
        // File file = new File(realPath);
        // if (file.exists())
        // file.delete();
        // }
        //
        // String imageNameJpeg = System.currentTimeMillis()
        // + (int) (Math.random() * 10000) + ".jpg";
        //
        // Bitmap bitmapJpeg = BitmapFactory
        // .decodeFile(ParameterUtil.saveImgTempPath
        // + imageName);
        // ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
        // bitmapJpeg.compress(Bitmap.CompressFormat.JPEG, 95,
        // byteOut);
        // byte[] btyeArray = byteOut.toByteArray();
        // File fJpeg = new File(ParameterUtil.saveImgTempPath
        // + imageNameJpeg);
        //
        // clipPath = fJpeg.getAbsolutePath();// 获得裁剪后压缩的图片路径
        //
        // FileOutputStream outStreamJpeg;
        // try {
        // outStreamJpeg = new FileOutputStream(fJpeg);
        // outStreamJpeg.write(btyeArray);
        // outStreamJpeg.flush();
        // outStreamJpeg.close();
        // } catch (FileNotFoundException ee) {
        // ee.printStackTrace();
        // } catch (IOException ee) {
        // ee.printStackTrace();
        // }
        //
        // File file = new File(ParameterUtil.saveImgTempPath
        // + imageName);
        // if (file.exists()) {
        // file.delete();// 删除中间图片
        // }
        // paths = clipPath;
        //
        // image_img.setImageBitmap(photo);
        //
        // uploadJvBaoContent();
        // }
        // }
        // }
        // createDialog("请稍后...");
        // new UploadPicAcyncTask().execute();
        // }
        // }
//		if (requestCode == AppConstant.KITKAT_LESS) {
//			if (resultCode == Activity.RESULT_OK) {
//				uri = data.getData();
//				Log.d("tag", "uri=" + uri);
//				// 调用裁剪方法
//				ImageCutTools.getInstance().cropPicture(this, uri);
//			}
//		} else if (requestCode == AppConstant.KITKAT_ABOVE) {
//			if (resultCode == Activity.RESULT_OK) {
//				uri = data.getData();
//				Log.d("tag", "uri=" + uri);
//				// 先将这个uri转换为path，然后再转换为uri
//				String thePath = ImageCutTools.getInstance().getPath(this, uri);
//				ImageCutTools.getInstance().cropPicture(this,
//						Uri.fromFile(new File(thePath)));
//			}
//		} else if (requestCode == AppConstant.INTENT_CROP) {
//			if (resultCode == Activity.RESULT_OK) {
//				bitmap = data.getParcelableExtra("data");
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
//						// Toast.makeText(
//						// context,
//						// "已生成缓存文件，等待上传！文件位置："
//						// + tempFile.getAbsolutePath(),
//						// Toast.LENGTH_LONG).show();
//					}
//					if (NetUtil.getConnectState(context) != NetWorkState.NONE) {
//						uploadJvBaoContent();
//					} else {
//						alertFailDialog(0);
//					}
//				} catch (FileNotFoundException e) {
//					e.printStackTrace();
//				}
//			}
//		}
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == Crop.REQUEST_PICK) {
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
//                beginCrop( data.getData());
            }
            if (data != null
                    && (requestCode == Const.REQUEST_CLIP_IMAGE || requestCode == Const.REQUEST_TAKE_PHOTO)) {
                String path = PhotoActionHelper.getOutputPath(data);
                if (path != null) {
                    bitmap = BitmapFactory.decodeFile(path);
                    paths = path;
                    if (bitmap != null) {
                        if (NetUtil.getConnectState(context) != NetWorkState.NONE) {
                            uploadJvBaoContent();
                        } else {
                            alertFailDialog(0);
                        }
                    } else {
                        alertFailDialog(1);
                    }
                }
            }
        }

    }

    private void dialogShow() {
        Dialog dialog = new Dialog(this, R.style.dialog_translucent);
        Window window = dialog.getWindow();
        android.view.WindowManager.LayoutParams params = window.getAttributes();// 获取LayoutParams
        params.alpha = 0.92f;
        window.setGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL);
        window.setAttributes(params);// 设置生效

        LayoutInflater fac = LayoutInflater.from(this);
        View more_pop_menu = fac.inflate(R.layout.dialog_pictureorigin, null);
        dialog.setCanceledOnTouchOutside(true);
        dialog.setContentView(more_pop_menu);
        params.height = android.view.ViewGroup.LayoutParams.WRAP_CONTENT;
        params.width = getWindowManager().getDefaultDisplay().getWidth() - 30;
        dialog.show();

        new MyClickClass(more_pop_menu, dialog);
    }

    class MyClickClass implements View.OnClickListener {

        private Dialog dialog;
        private Button dialog_button_photo;
        private Button dialog_button_album;
        private Button dialog_button_cancel;

        public MyClickClass(View view, Dialog dialog) {
            this.dialog = dialog;
            dialog_button_photo = (Button) view
                    .findViewById(R.id.dialog_button_photo);
            dialog_button_photo.setOnClickListener(this);
            dialog_button_album = (Button) view
                    .findViewById(R.id.dialog_button_album);
            dialog_button_album.setOnClickListener(this);
            dialog_button_cancel = (Button) view
                    .findViewById(R.id.dialog_button_cancel);
            dialog_button_cancel.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            // Intent intent = null;
            switch (v.getId()) {
                case R.id.dialog_button_photo:
                    isRealImg = true;// 相机拍摄的图
                    // intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    // intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(new
                    // File(
                    // Environment.getExternalStorageDirectory(), "temp.jpg")));
                    // startActivityForResult(intent, REQUEST_CAMERA_CODE);
                    ImageCutUtils.openCameraImage(PersonMessageActivity.this);
                    break;

                case R.id.dialog_button_album:
                    // Intent innerIntent = new Intent(Intent.ACTION_GET_CONTENT);
                    // // "android.intent.action.GET_CONTENT"
                    // innerIntent.setType("image/*"); // 查看类型
                    // startActivityForResult(innerIntent, REQUEST_ALBUM_CODE);
                    isRealImg = false;// 不是相机拍摄的图
                    ImageCutUtils.openLocalImage(PersonMessageActivity.this);

                    break;

            }
            dialog.dismiss();
        }
    }

    /* 根据uri返回文件路径 */
    public String getPath(Uri uri) {
        String[] proj = {MediaStore.Images.Media.DATA};
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

    public final static Bitmap lessenUriImage(String path) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        Bitmap bitmap = BitmapFactory.decodeFile(path, options); // 此时返回 bm 为空
        options.inJustDecodeBounds = false; // 缩放比。由于是固定比例缩放，只用高或者宽其中一个数据进行计算即可
        int be = (int) (options.outHeight / (float) 320);
        if (be <= 0)
            be = 1;
        options.inSampleSize = be; // 重新读入图片，注意此时已经把 options.inJustDecodeBounds
        // 设回 false 了
        bitmap = BitmapFactory.decodeFile(path, options);
        int w = bitmap.getWidth();
        int h = bitmap.getHeight();
        System.out.println(w + " " + h); // after zoom
        return bitmap;
    }

    private void uploadJvBaoContent() {
        if (NetUtil.getConnectState(this) == NetWorkState.NONE) {
            Toast.makeText(context, "网络异常，请检查网络！", Toast.LENGTH_SHORT).show();
        } else {
            HttpUtils httpUtils = new HttpUtils(10000);
            RequestParams params = new RequestParams();
            params.addBodyParameter("uploadImage", new File(paths));
            params.addBodyParameter("uid", userId);
            httpUtils.send(HttpMethod.POST, URLConstants.修改个人头像, params,
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
                                        image_img.setImageBitmap(ImageUtils
                                                .getRoundBitmap(context,
                                                        bitmap));
                                        setResult(1);
                                        sharedPrefUtil.putString(context,
                                                ShareFile.USERFILE,
                                                ShareFile.TOUXIANGSTATE, "0");
                                        MyMessageAsync(URLConstants.查询个人信息
                                                + "?uid=" + userId);
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

    private void MyMessageAsync(String path) {
        StringRequest request = new StringRequest(Method.GET, path,
                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String result) {
                        if (!TextUtils.isEmpty(result)) {
                            Gson gson = new Gson();
                            try {
                                MyMessageBackBean backBean = gson.fromJson(
                                        result, MyMessageBackBean.class);
                                if (backBean.status == 0) {
                                    MyMessageBean bean = null;
                                    if (backBean.list != null
                                            && backBean.list.size() > 0) {
                                        for (int i = 0; i < backBean.list
                                                .size(); i++) {
                                            bean = backBean.list.get(i);
                                        }
                                        sharedPrefUtil.putString(context,
                                                ShareFile.USERFILE,
                                                ShareFile.USERBACKGROUNDPATH,
                                                bean.uBackgroundImage);
                                        sharedPrefUtil.putString(context,
                                                ShareFile.USERFILE,
                                                ShareFile.USEREMAIL,
                                                bean.uEmail);
                                        sharedPrefUtil.putString(context,
                                                ShareFile.USERFILE,
                                                ShareFile.TELEPHONE,
                                                bean.uMobile);
                                        sharedPrefUtil.putString(context,
                                                ShareFile.USERFILE,
                                                ShareFile.USERID, bean.uId);
                                        sharedPrefUtil.putString(context,
                                                ShareFile.USERFILE,
                                                ShareFile.USERNAME,
                                                bean.uNickName);
                                        sharedPrefUtil.putString(context, ShareFile.USERFILE,
                                                ShareFile.PERSONREMARK,
                                                bean.uPersontag);
                                        if (!"".equals(bean.uPortrait)) {
                                            String str = bean.uPortrait
                                                    .toString();
                                            str = str.replace("\\", "");
                                            sharedPrefUtil.putString(context,
                                                    ShareFile.USERFILE,
                                                    ShareFile.USERPHOTOPATH,
                                                    str);
                                        }
                                    } else {
                                        return;
                                    }
                                }
                            } catch (JsonSyntaxException e) {
                                e.printStackTrace();
                            }
                        } else {
                            return;
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
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
        if (bitmap != null) {
            bitmap.recycle();
        }
        bitmap = null;
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
    private void checkPermission(){
        if (Build.VERSION.SDK_INT >= 23) {
            int checkCallPhonePermission = ContextCompat.checkSelfPermission(mContext, Manifest.permission_group.STORAGE);
            if(checkCallPhonePermission != PackageManager.PERMISSION_GRANTED){
                ActivityCompat.requestPermissions(PersonMessageActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 10001);
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
