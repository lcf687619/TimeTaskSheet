//package com.mission.schedule.activity;
//
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//
//import android.app.Activity;
//import android.content.Context;
//import android.content.Intent;
//import android.database.Cursor;
//import android.graphics.Bitmap;
//import android.os.Bundle;
//import android.provider.MediaStore;
//import android.util.Log;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.View.OnClickListener;
//import android.view.ViewGroup;
//import android.view.Window;
//import android.widget.AdapterView;
//import android.widget.AdapterView.OnItemClickListener;
//import android.widget.BaseAdapter;
//import android.widget.Button;
//import android.widget.CheckBox;
//import android.widget.GridView;
//import android.widget.ImageButton;
//import android.widget.ImageView;
//import android.widget.RelativeLayout;
//import android.widget.Toast;
//
//import com.mission.schedule.R;
//import com.nostra13.universalimageloader.core.DisplayImageOptions;
//import com.nostra13.universalimageloader.core.ImageLoader;
//
//public class PicSelectActivity extends Activity implements OnItemClickListener,
//		OnClickListener {
//	protected static final String TAG = "PicSelectActivity";
//
//	private ImageButton mBtnHeadBack;
//	private GridView gridView;
//	private Button mBtnOK;
//	private ArrayList<String> picPaths = new ArrayList<String>();
//	private Map<Integer, Boolean> map = new HashMap<Integer, Boolean>();
//	private int width;
//	private int picPathSize;
//	private DisplayImageOptions options; // 显示图片的设置
//
//	@Override
//	protected void onCreate(Bundle savedInstanceState) {
//		super.onCreate(savedInstanceState);
//		
//		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
//		setContentView(R.layout.activity_pic_select);
//		
//		width = getResources().getDisplayMetrics().widthPixels;
//		picPathSize = getIntent().getIntExtra("picPathSize", 0);
//		findView();
//
//		options = new DisplayImageOptions.Builder()
//				.showStubImage(R.drawable.img_null_smal)
//				.showImageForEmptyUri(R.drawable.img_null_smal)
//				.showImageOnFail(R.drawable.img_null_smal).cacheInMemory(true)
//				.cacheOnDisc(true).bitmapConfig(Bitmap.Config.RGB_565) // 设置图片的解码类型
//				.build();
//
//		getImagPic(this);
//	}
//
//	private void findView() {
//		mBtnHeadBack = (ImageButton) findViewById(R.id.btnHeadBack);
//		mBtnHeadBack.setOnClickListener(this);
//		mBtnOK = (Button) findViewById(R.id.btn_ok);
//		mBtnOK.setOnClickListener(this);
//		mBtnOK.setClickable(false);
//		gridView = (GridView) findViewById(R.id.gridView1);
//
//		gridView.setOnItemClickListener(this);
//	}
//
//	private void getImagPic(Context context) {
//
//		final List<String> imgPaths = getImagPath(context);
//		if (imgPaths == null) {
//			Toast.makeText(context, "您还没有更多图片！", Toast.LENGTH_SHORT).show();
//			return;
//		}
//
//		gridView.setAdapter(new ImageAdapter(context, imgPaths));
//	}
//
//	class ImageAdapter extends BaseAdapter {
//		private List<String> imgPaths;
//		private ImageLoader imageLoader;
//		private Context context;
//		public ImageAdapter(Context context, List<String> imgPaths) {
//			super();
//			this.imgPaths = imgPaths;
//			this.context = context;
//			imageLoader = ImageLoader.getInstance();
//			initData();
//		}
//
//		@Override
//		public int getCount() {
//			return imgPaths.size();
//		}
//
//		private void initData() {
//			for (int i = 0; i < imgPaths.size(); i++) {
//				map.put(i, false);
//			}
//		}
//
//		@Override
//		public Object getItem(int position) {
//			return imgPaths.get(position);
//		}
//
//		@Override
//		public long getItemId(int position) {
//			return position;
//		}
//
//		@Override
//		public View getView(int position, View convertView, ViewGroup parent) {
//			ViewHolder viewHolder = null;
//			if (convertView == null) {
//				viewHolder = new ViewHolder();
//				LayoutInflater inflater = LayoutInflater.from(context);
//				convertView = inflater.inflate(R.layout.pic_select_adapter,
//						null);
//				viewHolder.imageView = (ImageView) convertView
//						.findViewById(R.id.imageView1);
//				viewHolder.imageView
//						.setLayoutParams(new RelativeLayout.LayoutParams(
//								width / 3, width / 3));
//
//				viewHolder.is_select = (CheckBox) convertView
//						.findViewById(R.id.is_show);
//				convertView.setTag(viewHolder);
//			} else {
//				viewHolder = (ViewHolder) convertView.getTag();
//			}
//			imageLoader.displayImage("file://" + imgPaths.get(position),
//					viewHolder.imageView, options);
//			viewHolder.is_select.setChecked(map.get(position));
//			Log.v(TAG, "当前Item===" + map.get(position) + "===" + position);
//
//			return convertView;
//		}
//
//	}
//
//	private static class ViewHolder {
//		ImageView imageView;
//		CheckBox is_select;
//	}
//
//	// 初始化每个Item的选中状态
//
//	private List<String> getImagPath(Context context) {
//		List<String> list = new ArrayList<String>();// 存放图片的路径
//		// 查询媒体数据库
//		String[] pojo = { MediaStore.Images.Media.DATA };
//		Cursor cursor = context.getContentResolver().query(
//				android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
//				pojo, null, null, MediaStore.Images.Media.DEFAULT_SORT_ORDER);
//		if (cursor.moveToFirst()) {
//			while (!cursor.isAfterLast()) {
//				int columnIndex = cursor.getColumnIndex(pojo[0]);
//				if (columnIndex != -1) {
//					String picPath = cursor.getString(columnIndex);
//					if(FileUtil.isLegalFile(picPath,"image") && ImageUtil.isCanDecoded(picPath)){
//						list.add(picPath);
//					}
//					cursor.moveToNext();
//				}
//			}
//			cursor.close();
//			return list;
//		}
//		return null;
//	}
//
//	@Override
//	public void onItemClick(AdapterView<?> parent, View view,
//			final int position, long id) {
//		String picPath = (String) gridView.getAdapter().getItem(position);
//		ViewHolder holder = (ViewHolder) view.getTag();
//		holder.is_select.toggle();// 点击Item时，改变复选框的选中状态
//		map.put(position, holder.is_select.isChecked());
//		if (holder.is_select.isChecked()) {
//			picPaths.add(picPath);
//			Log.v(TAG, "添加的==" + picPath);
//			Log.v(TAG, "添加的==" + picPaths.size());
//			mBtnOK.setText("完成(" + picPaths.size() + ")");
//
//		} else {
//			picPaths.remove(picPath);
//			mBtnOK.setText("完成(" + picPaths.size() + ")");
//			for (String path : picPaths) {
//				Log.v(TAG, "移除后==" + path);
//			}
//			Log.v(TAG, "还有==" + picPaths.size() + "   条");
//			if (picPaths.size() == 0) {
//				mBtnOK.setText("完成");
//			} else {
//
//			}
//		}
//
//		if (picPaths.size() == 0) {
//			mBtnOK.setClickable(false);
//		} else {
//			mBtnOK.setClickable(true);
//		}
//
//	}
//
//	@Override
//	public void onClick(View v) {
//		switch (v.getId()) {
//		case R.id.btn_ok:
//			if (picPaths.size() + picPathSize > 1) {
//				Toast.makeText(PicSelectActivity.this, "您最多可以选择1张图片！",
//						Toast.LENGTH_SHORT).show();
//			} else {
//				Intent intent = new Intent();
//				Bundle bundle = new Bundle();
//				bundle.putString("picPaths", picPaths.get(0).toString());
//				intent.putExtras(bundle);
//				setResult(100, intent);
//				PicSelectActivity.this.finish();
//			}
//			break;
//		case R.id.btnHeadBack:
//			this.finish();
//			break;
//		default:
//			break;
//		}
//
//	}
//}
