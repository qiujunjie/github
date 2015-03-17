package cmcc.mhealth.adapter;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.ImageView;
import cmcc.mhealth.R;

public class SettingDeviceBindAdapter extends PagerAdapter {
	private Context context;
	private int[] Resource = new int[]{
			R.drawable.my_radio_button_bg_device1,
			R.drawable.my_radio_button_bg_device2,
			R.drawable.my_radio_button_bg_device3,
			R.drawable.my_radio_button_bg_device4};
	public SettingDeviceBindAdapter(Context context) {
		this.context = context;
	}

	@Override
	public int getCount() {
		return 4;
	}

	@Override
	public Object instantiateItem(View collection, int position) {
		View view = View.inflate(context, R.layout.imageview, null);
		ImageView imageView = (ImageView) view.findViewById(R.id.the_imageview);
		imageView.setImageResource(Resource[position]);
		imageView.setTag(position + "rpta");
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
