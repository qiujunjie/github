package cmcc.mhealth.adapter;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.ImageView;
import cmcc.mhealth.R;
import cmcc.mhealth.common.Config;
import cmcc.mhealth.common.ImageUtil;

public class RacePicTitleAdapter extends PagerAdapter {
	private Context context;

	public RacePicTitleAdapter(Context context) {
		this.context = context;
	}

	@Override
	public int getCount() {
		return 14;
	}

	@Override
	public Object instantiateItem(View collection, int position) {
		View view = View.inflate(context, R.layout.imageview, null);
		ImageView imageView = (ImageView) view.findViewById(R.id.the_imageview);
		imageView.setImageResource(R.drawable.umeng_socialize_share_pic);
		imageView.setTag(position + "rpta");
		ImageUtil.getInstance().loadBitmap(imageView, Config.RACE_PIC_SERVER_ROOT + Config.RACE_TITLE_PIC + (position + 1) + "_big.jpg", position + "rpta", 0);
		((ViewPager) collection).addView(view);
		return view;
	}

	@Override
	public void destroyItem(View collection, int position, Object view) {
		((ViewPager) collection).removeView((View) view);
	}

	@Override
	public boolean isViewFromObject(View view, Object object) {
		return view == ((View) object);
	}
}
