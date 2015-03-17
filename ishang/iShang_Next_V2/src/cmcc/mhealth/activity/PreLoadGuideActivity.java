package cmcc.mhealth.activity;

import java.util.ArrayList;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import cmcc.mhealth.R;
import cmcc.mhealth.basic.BaseActivity;
import cmcc.mhealth.common.Config;
import cmcc.mhealth.common.SharedPreferredKey;

public class PreLoadGuideActivity extends BaseActivity {
	// private TextView mTextViewTitle;
	// private ImageButton mButtonClose;
	private boolean bNewInsatall;

	private ViewPager mViewPager;
	private ArrayList<View> mPageViews;

	// 指示点图片
	private ImageView mImageView;
	private ImageView[] mImageViews;

	// 该应用的主布局LinearLayout
	private ViewGroup mainViewGroup;
	// 主布局底部指示当前页面的小圆点视图，LinearLayout
	private ViewGroup indicatorViewGroup;

	// 定义LayoutInflater
	private LayoutInflater mInflater;

	private View mLayoutBasic1;
	private View mLayoutBasic2;
	private View mLayoutBasic3;
    private View mLayoutBasic4;
    private View mLayoutBasic5;

//	private BasicView mBasicview1;
//	private BasicView mBasicview2;
//	private BasicView mBasicview3;
	
	private Bitmap bitmap1;
	private Bitmap bitmap2;
	private Bitmap bitmap3;
    private Bitmap bitmap4;
    private Bitmap bitmap5;
	
	@Override
	protected void onDestroy() {
		if (bitmap1 != null && !bitmap1.isRecycled()) {
			bitmap1.recycle();
			bitmap1 = null;
		}
		if (bitmap2 != null && !bitmap2.isRecycled()) {
			bitmap2.recycle();
			bitmap2 = null;
		}
		if (bitmap3 != null && !bitmap3.isRecycled()) {
			bitmap3.recycle();
			bitmap3 = null;
		}
        if (bitmap4 != null && !bitmap4.isRecycled()) {
            bitmap4.recycle();
            bitmap4 = null;
        }
        if (bitmap5 != null && !bitmap5.isRecycled()) {
            bitmap5.recycle();
            bitmap5 = null;
        }
		super.onDestroy();
	}
	
	private static int computeInitialSampleSize(BitmapFactory.Options options,int minSideLength, int maxNumOfPixels) {  
	    double w = options.outWidth;  
	    double h = options.outHeight;  
	    int lowerBound = (maxNumOfPixels == -1) ? 1 : (int) Math.ceil(Math.sqrt(w * h / maxNumOfPixels));  
	    int upperBound = (minSideLength == -1) ? 128 :(int) Math.min(Math.floor(w / minSideLength), Math.floor(h / minSideLength));  
	    if (upperBound < lowerBound) {  
	        // return the larger one when there is no overlapping zone.  
	        return lowerBound;  
	    }  
	    if ((maxNumOfPixels == -1) && (minSideLength == -1)) {  
	        return 1;  
	    } else if (minSideLength == -1) {  
	        return lowerBound;  
	    } else {  
	        return upperBound;  
	    }  
	}
	public static int computeSampleSize(BitmapFactory.Options options, int minSideLength, int maxNumOfPixels) {  
	    int initialSize = computeInitialSampleSize(options, minSideLength, maxNumOfPixels);  
	    int roundedSize;  
	    if (initialSize <= 8) {  
	        roundedSize = 1;  
	        while (roundedSize < initialSize) {  
	            roundedSize <<= 1;  
	        }  
	    } else {  
	        roundedSize = (initialSize + 7) / 8 * 8;  
	    }  
	    return roundedSize;  
	}  
	

	/** Called when the activity is first created. */
	@SuppressWarnings("deprecation")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// 设置窗口无标题
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		bNewInsatall = false;

		/* 更新后未登录判断 */
		SharedPreferences info = getSharedPreferences(SharedPreferredKey.SHARED_NAME, 0);
		// 首页面判断
		int install = info.getInt("INSTALL", 0);
		// 设置页面调用
		boolean bShowGuid = info.getBoolean("BSHOWGUID", true);
		int verCode = Config.getVerCode(this);
		if (install == verCode && !bShowGuid) {

			Intent intent = new Intent();
			intent.setClass(PreLoadGuideActivity.this, PreLoadLoadingActivity.class);
			startActivity(intent);
			PreLoadGuideActivity.this.finish();
			return;
		}
		if (bShowGuid) {
			Editor infoEditor = getSharedPreferences(SharedPreferredKey.SHARED_NAME, Context.MODE_PRIVATE).edit();
			infoEditor.putBoolean("BSHOWGUID", false);
			infoEditor.commit();
		}
		if (install != verCode) {
			bNewInsatall = true;
		}

		mInflater = getLayoutInflater();
		// if(mInflater==null)
		// return;
		mPageViews = new ArrayList<View>();
		// if(mPageViews==null)
		// return;
		// Drawable bg = getResources().getDrawable(R.drawable.bg);
		// XXX.setBackgroundDrawable(bg);
		// basic1 info
		
		int height = (int)(getWindowManager().getDefaultDisplay().getHeight() / 1.414f);
		int width = (int)(getWindowManager().getDefaultDisplay().getWidth() / 1.414f);
		
		BitmapFactory.Options opts = new BitmapFactory.Options();
		opts.inJustDecodeBounds = true;
		BitmapFactory.decodeResource(getResources(), R.drawable.loading_guid1, opts);

		opts.inSampleSize = computeSampleSize(opts, -1, height * width);
		opts.inJustDecodeBounds = false; 
		
		mLayoutBasic1 = mInflater.inflate(R.layout.view_basic1, null);
		LinearLayout ll1 = (LinearLayout) mLayoutBasic1.findViewById(R.id.linearLayout_loading_title1);
		bitmap1 = BitmapFactory.decodeResource(getResources(), R.drawable.loading_guid1, opts);
		Drawable drawable1 = new BitmapDrawable(bitmap1); 
		ll1.setBackgroundDrawable(drawable1);
		// if(mLayoutBasic1!=null)
//		mBasicview1 = new BasicView(this, mLayoutBasic1);

		// basic2 info
		mLayoutBasic2 = mInflater.inflate(R.layout.view_basic2, null);
		LinearLayout ll2 = (LinearLayout) mLayoutBasic2.findViewById(R.id.linearLayout_loading_title2);
		bitmap2 = BitmapFactory.decodeResource(getResources(), R.drawable.loading_guid2, opts);
		Drawable drawable2 = new BitmapDrawable(bitmap2); 
		ll2.setBackgroundDrawable(drawable2);
		// if(mLayoutBasic2!=null)
//		mBasicview2 = new BasicView(this, mLayoutBasic2);

        mLayoutBasic3 = mInflater.inflate(R.layout.view_basic3, null);
        LinearLayout ll3 = (LinearLayout) mLayoutBasic3.findViewById(R.id.linearLayout_loading_title3);
        bitmap3 = BitmapFactory.decodeResource(getResources(), R.drawable.loading_guid3, opts);
        Drawable drawable3 = new BitmapDrawable(bitmap3); 
        ll3.setBackgroundDrawable(drawable3);

        mLayoutBasic4 = mInflater.inflate(R.layout.view_basic4, null);
        LinearLayout ll4 = (LinearLayout) mLayoutBasic4.findViewById(R.id.linearLayout_loading_title4);
        bitmap4 = BitmapFactory.decodeResource(getResources(), R.drawable.loading_guid4, opts);
        Drawable drawable4 = new BitmapDrawable(bitmap4); 
        ll4.setBackgroundDrawable(drawable4);        


        mLayoutBasic5 = mInflater.inflate(R.layout.view_basic5, null);
        ImageView ll5 = (ImageView) mLayoutBasic5.findViewById(R.id.imageview_loading_title5);
        bitmap5 = BitmapFactory.decodeResource(getResources(), R.drawable.loading_guid5, opts);
        Drawable drawable5 = new BitmapDrawable(bitmap5); 
        ll5.setBackgroundDrawable(drawable5);
		// if(mLayoutBasic3!=null)
//		mBasicview3 = new BasicView(this, mLayoutBasic3);

		ImageView imageButtonEnter = (ImageView) mLayoutBasic5.findViewById(R.id.imageButton_enter);
		imageButtonEnter.setVisibility(View.VISIBLE);

		imageButtonEnter.setOnClickListener(new OnClickListener() {

			@Override
			public synchronized void onClick(View arg0) {
				if (bNewInsatall) {
					Intent intent = new Intent();
					intent.setClass(PreLoadGuideActivity.this, PreLoadLoadingActivity.class);
					startActivity(intent);
				}
				PreLoadGuideActivity.this.finish();
				overridePendingTransition(R.anim.slide_in_right,R.anim.silde_out_left);
			}
		});

		mPageViews.add(mLayoutBasic1);
		mPageViews.add(mLayoutBasic2);
		mPageViews.add(mLayoutBasic3);
        mPageViews.add(mLayoutBasic4);
        mPageViews.add(mLayoutBasic5);

		mImageViews = new ImageView[mPageViews.size()];
		mainViewGroup = (ViewGroup) mInflater.inflate(R.layout.activity_viewpager, null);

		mViewPager = (ViewPager) mainViewGroup.findViewById(R.id.myviewpager);
		indicatorViewGroup = (ViewGroup) mainViewGroup.findViewById(R.id.mybottomviewgroup);

		for (int i = 0; i < mImageViews.length; i++) {
			mImageView = new ImageView(PreLoadGuideActivity.this);
			// mImageView.setLayoutParams(new LayoutParams(20, 20));
			LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(20, 20);
			lp.setMargins(20, 0, 20, 0);
			mImageView.setLayoutParams(lp);
			// mImageView.setPadding(40, 0, 40, 0);

			if (i == 0) {
				mImageView.setBackgroundResource(R.drawable.page_indicator_focused);
			} else {
				mImageView.setBackgroundResource(R.drawable.page_indicator);
			}

			mImageViews[i] = mImageView;

			// 把指示作用的远点图片加入底部的视图中
			indicatorViewGroup.addView(mImageViews[i]);
		}

		// 注意这两种用法的区别，前者无法正常显示！！
		// setContentView(R.layout.main);
		setContentView(mainViewGroup);

		// 返回title
		// mTextViewTitle = (TextView) findViewById(R.id.textView_title);
		// mTextViewTitle.setText("新手引导");

		// mButtonClose = (ImageButton) findViewById(R.id.imageButton_title);
		// mButtonClose.setBackgroundResource(R.drawable.umeng_socialize_x_button);
		// mButtonClose.setVisibility(View.VISIBLE);
		// mButtonClose.setOnClickListener(new Button.OnClickListener() {
		// @Override
		// public void onClick(View v) {
		// // TODO Auto-generated method stub
		// GuideActivity.this.finish();
		//
		// Intent intent = new Intent();
		// intent.setClass(GuideActivity.this, LoadingActivity.class);
		// startActivity(intent);
		// }
		// });

		mViewPager.setAdapter(new MyPagerAdapter());
		mViewPager.setOnPageChangeListener(new OnPageChangeListener() {

			@Override
			public void onPageSelected(int arg0) {
				// TODO Auto-generated method stub
				for (int i = 0; i < mImageViews.length; i++) {
					if (i == arg0) {
						mImageViews[i].setBackgroundResource(R.drawable.page_indicator_focused);
					} else {
						mImageViews[i].setBackgroundResource(R.drawable.page_indicator);
					}
				}
			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onPageScrollStateChanged(int arg0) {
				// TODO Auto-generated method stub

			}
		});
	}

	class MyPagerAdapter extends PagerAdapter {

		@Override
		public int getCount() {
			return mPageViews.size();
		}

		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {
			return arg0 == arg1;
		}

		@Override
		public int getItemPosition(Object object) {
			// TODO Auto-generated method stub
			return super.getItemPosition(object);
		}

		@Override
		public void destroyItem(View arg0, int arg1, Object arg2) {
			// TODO Auto-generated method stub
			((ViewPager) arg0).removeView(mPageViews.get(arg1));
		}

		@Override
		public Object instantiateItem(View arg0, int arg1) {
			// TODO Auto-generated method stub
			((ViewPager) arg0).addView(mPageViews.get(arg1));
			return mPageViews.get(arg1);
		}

		@Override
		public void restoreState(Parcelable arg0, ClassLoader arg1) {
			// TODO Auto-generated method stub

		}

		@Override
		public Parcelable saveState() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public void startUpdate(View arg0) {
			// TODO Auto-generated method stub

		}

		@Override
		public void finishUpdate(View arg0) {
			// TODO Auto-generated method stub

		}

	}
}
