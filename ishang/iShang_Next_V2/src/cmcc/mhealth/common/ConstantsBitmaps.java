package cmcc.mhealth.common;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import cmcc.mhealth.R;

public class ConstantsBitmaps {
	public static Bitmap mLeftPic;
	public static Bitmap mRunPicYellow;
	public static Bitmap mRunPicGreen;
	public static Bitmap mRunPicBlue;
	public static Bitmap mRunPicDouble;
	
	public static Bitmap mBitmapBgCenterRound;
	public static Bitmap mBitmapPointRound;
	
	public static void initRunPics(Context context){
		if (mLeftPic == null || mLeftPic.isRecycled()) {
			mLeftPic = BitmapFactory.decodeResource(context.getResources(), R.drawable.rank_avg_bg);
		}
		if (mRunPicYellow == null || mRunPicYellow.isRecycled()) {
			mRunPicYellow = BitmapFactory.decodeResource(context.getResources(), R.drawable.rank_avg_icon);
		}
		if (mRunPicGreen == null || mRunPicGreen.isRecycled()) {
			mRunPicGreen = BitmapFactory.decodeResource(context.getResources(), R.drawable.rank_avg_icon_green);
		}
		if (mRunPicBlue == null || mRunPicBlue.isRecycled()) {
			mRunPicBlue = BitmapFactory.decodeResource(context.getResources(), R.drawable.rank_avg_icon_blue);
		}
		if (mRunPicDouble == null || mRunPicDouble.isRecycled()) {
			mRunPicDouble = BitmapFactory.decodeResource(context.getResources(), R.drawable.rank_avg_icon_group);
		}
		
		if (mBitmapBgCenterRound == null || mBitmapBgCenterRound.isRecycled()) {
			mBitmapBgCenterRound = BitmapFactory.decodeResource(context.getResources(), R.drawable.center_round);
		}
		if (mBitmapPointRound == null || mBitmapPointRound.isRecycled()) {
			mBitmapPointRound = BitmapFactory.decodeResource(context.getResources(), R.drawable.yellow_point);
		}
	}
}
