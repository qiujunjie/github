package cmcc.mhealth.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;
import cmcc.mhealth.R;
import cmcc.mhealth.bean.RaceMemberData;
import cmcc.mhealth.bean.RaceMemberInfo;
import cmcc.mhealth.common.ConstantsBitmaps;
import cmcc.mhealth.common.Encrypt;
import cmcc.mhealth.common.ImageUtil;
import cmcc.mhealth.net.DataSyn;
import cmcc.mhealth.slidingcontrol.MainCenterActivity;
import cmcc.mhealth.view.RoundAngleImageView;
import cmcc.mhealth.view.ScoreBarView;

/**
 * ¾ºÈüÏêÇéµÄadapter
 * @author zy
 *
 */
public class RaceDetailAdapter extends BaseAdapter {
	private Context context;
	private RaceMemberInfo rmi;
	private int maxValue;
//	private int[] sex = new int[] { R.drawable.avatar_male_middle, R.drawable.avatar_female_middle };

	public RaceDetailAdapter(Context context, RaceMemberInfo rmi) {
		this.context = context;
		this.rmi = rmi;
	}

	public void setRmi(RaceMemberInfo rmi) {
		this.rmi = rmi;
	}

	@Override
	public int getCount() {
		return rmi.racemember.size();
	}

	@Override
	public Object getItem(int arg0) {
		return arg0;
	}

	@Override
	public long getItemId(int arg0) {
		return arg0;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup arg2) {
		Holder holder = null;
		View view = null;
		if (convertView == null) {
			holder = new Holder();
			view = View.inflate(context, R.layout.list_item_detail_race, null);
			holder.raiv = (RoundAngleImageView) view.findViewById(R.id.race_icon_name);
			holder.member_name = (TextView) view.findViewById(R.id.race_textview_member_name);
			holder.group_name = (TextView) view.findViewById(R.id.race_textview_group_name);
			holder.sbv = (ScoreBarView) view.findViewById(R.id.race_regularprogressbar);
			holder.llt = (LinearLayout) view.findViewById(R.id.linearLayout_list_item_race);
			holder.llt.setBackgroundColor(context.getResources().getColor(R.color.transparent));
			view.setTag(holder);
		} else {
			view = convertView;
			holder = (Holder) view.getTag();
		}
		RaceMemberData rmd = rmi.racemember.get(position);
		if (position == 0) {
			maxValue = Integer.parseInt(rmd.getMemberstepvalue());
		}
		
		if (position < 2 ) {
			holder.sbv.setPics(ConstantsBitmaps.mLeftPic, ConstantsBitmaps.mRunPicGreen);
		} else {
			holder.sbv.setPics(ConstantsBitmaps.mLeftPic, ConstantsBitmaps.mRunPicYellow);
		}
		holder.sbv.setMaxValue(maxValue);
		holder.sbv.setScore(Integer.parseInt(rmd.getMemberstepvalue()));
		holder.sbv.reDraw();
		int name2Int = Encrypt.getIntFromName(rmd.membername == null ? "0" : rmd.membername);
		holder.raiv.setImageResource(rmd.getSex().equals("0")? MainCenterActivity.BASE_ATATAR[name2Int+7] :MainCenterActivity.BASE_ATATAR[name2Int]  );
		holder.raiv.setTag(position + "rda");
		if(!TextUtils.isEmpty(rmd.getAvatar()))
			ImageUtil.getInstance().loadBitmap(holder.raiv,  DataSyn.avatarHttpURL + rmd.getAvatar() + ".jpg", position + "rda", 0);
		
		holder.member_name.setText(rmd.getMembername());
		holder.group_name.setText(rmd.getGroupname());


		return view;
	}

	class Holder {
		public RoundAngleImageView raiv;
		public TextView member_name, group_name;
		public ScoreBarView sbv;
		public LinearLayout llt;
	}
}
