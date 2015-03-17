package cmcc.mhealth.basic;

import org.achartengine.renderer.XYMultipleSeriesRenderer;

import android.app.Activity;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Color;
import android.graphics.Paint.Align;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import cmcc.mhealth.R;
import cmcc.mhealth.common.Common;
import cmcc.mhealth.common.SharedPreferredKey;
import cmcc.mhealth.net.DataSyn;
import cmcc.mhealth.slidingcontrol.MainCenterActivity;
import cmcc.mhealth.slidingcontrol.PedometorFragment;
import cmcc.mhealth.slidingcontrol.RankFragment;

import com.umeng.fb.NotificationType;
import com.umeng.fb.UMFeedbackService;

public abstract class BaseFragment extends Fragment {
	private final static String TAG = "BaseFragment";

	protected FragmentActivity mActivity;
	protected View mView;
	protected SharedPreferences mSharedInfo;
	protected SharedPreferences sp;

	protected OnFragmentDestroyListener dlistener;

	/**
	 * @author qjj
	 * @����ʱ�䣺2013-8-28 ����9:39:33
	 * @�޸��ˣ�qjj
	 * @�޸�ʱ�䣺2013-8-28 ����9:39:33
	 */
	public abstract void findViews();

	/**
	 * @�����ˣ�qjj
	 * @����ʱ�䣺2013-8-28 ����9:40:26
	 * @�޸��ˣ�qjj
	 * @�޸�ʱ�䣺2013-8-28 ����9:40:26
	 */
	public abstract void clickListner();

	/**
	 * @loadLogic(�߼�����) qjj
	 * @����ʱ�䣺2013-8-28 ����9:40:48
	 * @�޸��ˣ�qjj
	 * @�޸�ʱ�䣺2013-8-28 ����9:40:48
	 */
	public abstract void loadLogic();

	public BaseFragment() {
		super();
	}

	private Toast mToast = null;

	protected TextView mTextViewTitle;

	protected ImageView mImageButtonBack;

	protected void BaseToast(String msg) {
		BaseToast(msg, 0);
	}

	protected void BaseToast(String msg, int time) {
		if (mToast == null) {
			mToast = Toast.makeText(mActivity, msg, time);
		} else {
			mToast.setText(msg);
		}
		mToast.show();
	}

	protected SharedPreferences getSharedPreferences(String name, int mode) {
		if (mSharedInfo == null)
			mSharedInfo = mActivity.getSharedPreferences(name, mode);
		return mSharedInfo;
	}

	public SharedPreferences getSharedPreferences() {
		if (mSharedInfo == null)
			mSharedInfo = mActivity.getSharedPreferences(SharedPreferredKey.SHARED_NAME, 0);
		return mSharedInfo;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		mActivity = getActivity();
		mView = container;
		initView();
		return super.onCreateView(inflater, container, savedInstanceState);
	}

	private void initView() {
		sp = getSharedPreferences();
		mTextViewTitle = findView(R.id.textView_title);
		mImageButtonBack = findView(R.id.button_input_bg_back);
		titleRight = findView(R.id.imageButton_title_add);
		findViews();
		clickListner();
		loadLogic();
	}

	/**
	 * �Զ���Toast
	 * 
	 * @param sΪ������ַ���
	 */
	protected void toast(String s) {
		Toast.makeText(mActivity, s, Toast.LENGTH_SHORT).show();
	}

	public Activity getMyActivity() {
		return mActivity;
	}

	/**
	 * messagesManager(���̵߳��ô˷�����ֱ��Toast������Ҫhandle����Ϣ) what ��Ϣcode
	 */
	protected void messagesManager(int what) {
		Message message = Message.obtain();
		message.what = what;
		mHandler.sendMessage(message);
	}

	protected Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			String m = "";
			m = mActivity.getString(msg.what);
			toast(String.valueOf(m));
		};
	};

	public RelativeLayout titleRight;

	@SuppressWarnings("unchecked")
	public <T extends View> T findView(int id) {
		return (T) mView.findViewById(id);
	}

	public XYMultipleSeriesRenderer getBarRenderer() {
		XYMultipleSeriesRenderer renderer = new XYMultipleSeriesRenderer();

		renderer.setPanEnabled(false, false);// ��ס���� �����ƶ�
		renderer.setZoomEnabled(false, false);

		renderer.setLabelsColor(Color.BLACK);// x y ������ɫ
		renderer.setAxesColor(Color.BLACK); // x y ����ɫ
		// renderer.setShowCustomTextGrid(true)
		renderer.setMarginsColor(Color.argb(0, 0xff, 0, 0)); // ����͸��
		renderer.setMargins(new int[] { Common.dip2px(getMyActivity(), 10), Common.dip2px(getMyActivity(), 5), Common.dip2px(getMyActivity(), 10), Common.dip2px(getMyActivity(), 5) });

		renderer.setAxisTitleTextSize(Common.dip2px(getMyActivity(), 8));
		renderer.setChartTitleTextSize(Common.dip2px(getMyActivity(), 8));
		renderer.setLegendTextSize(Common.dip2px(getMyActivity(), 13));
		// renderer.setFitLegend(true);
		renderer.setLabelsTextSize(Common.dip2px(getMyActivity(), 13));

		renderer.setShowGridX(true);
		renderer.setGridColor(Color.GRAY);

		renderer.setYLabels(Common.dip2px(getMyActivity(), 3));
		// renderer.getYTextLabelLocations()
		// renderer.setXLabels(10);

		// renderer.setYLabelsAlign(Align.LEFT, 1);
		renderer.setYLabelsAlign(Align.LEFT); // ? y��������
		renderer.setShowAxes(false);

		return renderer;
	}

	@Override
	public void onResume() {
		String selectedserver = sp.getString(SharedPreferredKey.SERVER_NAME, "");
		if (null != selectedserver && !"".equals(selectedserver)) {
			DataSyn.setStrHttpURL("http://" + selectedserver + "openClientApi.do?action=");
			DataSyn.setAvatarHttpURL("http://" + selectedserver + "UserAvatar/");
		}
		/**
		 * ���˷���˷�����Ϣ����
		 */
		UMFeedbackService.enableNewReplyNotification(mActivity, NotificationType.NotificationBar);
		super.onResume();
	}

	@Override
	public void onDestroy() {
		if (dlistener != null) {
			dlistener.onDestroy();
		}
		if (this instanceof RankFragment) {
			RankFragment.mCurrIndex = 0;
			RankFragment.mCurrType = 0;
		} else if (this instanceof PedometorFragment) {
			PedometorFragment pbf = (PedometorFragment) this;
			Editor edit = sp.edit();
			edit.putString("PreDayWeekMonth", pbf.mPreDay + "#" + pbf.mPreWeek + "#" + pbf.mPreMonth);
			edit.commit();
		}
		super.onDestroy();
	}

	public interface OnFragmentDestroyListener {
		abstract void onDestroy();
	}

	public void setOnFragmentDestroyListener(OnFragmentDestroyListener dlistener) {
		this.dlistener = dlistener;
	}

	protected void showMenu() {
		MainCenterActivity m = (MainCenterActivity) mActivity;
		m.showMenu();
	}

	public String getMyName() {
		String mMembername = getSharedPreferences().getString(SharedPreferredKey.NAME, "");
		return mMembername;
	}

	public String getAvater() {
		return getSharedPreferences().getString(SharedPreferredKey.AVATAR, null);
	}

	public String getPhoneNum() {
		return getSharedPreferences().getString(SharedPreferredKey.PHONENUM, null);
	}

	public String getGender() {
		return getSharedPreferences().getString(SharedPreferredKey.GENDER, null);
	}
}
