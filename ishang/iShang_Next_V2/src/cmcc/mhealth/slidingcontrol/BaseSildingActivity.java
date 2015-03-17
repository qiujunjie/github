 package cmcc.mhealth.slidingcontrol;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.KeyEvent;
import cmcc.mhealth.R;
import cmcc.mhealth.activity.PreLoadAPKUpdateProgressActivity;
import cmcc.mhealth.common.Common;
import cmcc.mhealth.common.Config;
import cmcc.mhealth.common.Constants;
import cmcc.mhealth.common.Logger;
import cmcc.mhealth.common.PreferencesUtils;
import cmcc.mhealth.common.SharedPreferredKey;
import cmcc.mhealth.net.UpdateSoftWareTools;
import cn.jpush.android.api.JPushInterface;

import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.jeremyfeinstein.slidingmenu.lib.app.SlidingFragmentActivity;

public class BaseSildingActivity extends SlidingFragmentActivity {

	private int mTitleRes;
	protected ListFragment mFrag;
	private AlertDialog mAlertDialog;

	public BaseSildingActivity(int titleRes) {
		mTitleRes = titleRes;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setTitle(mTitleRes);

		// set the Behind View
		setBehindContentView(R.layout.menu_frame);

		// if (savedInstanceState == null) {
		// FragmentTransaction t =
		// this.getSupportFragmentManager().beginTransaction();
		// mFrag = new RightListFragment();
		// t.replace(R.id.menu_frame_two, mFrag);
		// t.commit();
		// } else {
		// mFrag = (RightListFragment)
		// getSupportFragmentManager().findFragmentById(R.id.menu_frame_two);
		// }

		// customize the SlidingMenu
		SlidingMenu sm = getSlidingMenu();
		sm.setShadowWidthRes(R.dimen.shadow_width);
		sm.setShadowDrawable(R.drawable.shadow);
		sm.setBehindOffsetRes(R.dimen.slidingmenu_offset);
		sm.setFadeDegree(0.35f);
		sm.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
		// ����
		new Thread() {
			public void run() {
				if (UpdateSoftWareTools.getServerVerCode()) {
					int verCode = Config.getVerCode(BaseSildingActivity.this);
					if (UpdateSoftWareTools.newVerCode > verCode && verCode != -1) {
						BaseSildingActivity.this.runOnUiThread(new Runnable() {

							@Override
							public void run() {
								UpdateVersion();
							}
						});
					}
				} else {
					Logger.d("BaseSlidingActivity", "getServerVerCode false");
				}
			};
		}.start();
	}

	public void UpdateVersion() {

		StringBuffer sb = new StringBuffer();
		sb.append("��ǰ�汾:\t");
		sb.append(Config.getVerName(BaseSildingActivity.this) + "\n");
		sb.append("�����°汾:\t");
		sb.append(UpdateSoftWareTools.newVerName + "\n");
		sb.append("����˵��:\t");
		sb.append(UpdateSoftWareTools.newVerInfo + "\n");
		sb.append("�Ƿ����?");
		final Intent mIntent = new Intent();
		// mInternet = NetworkTool.getNetworkState(MainMenuActivity.this);
		// ����
		new AlertDialog.Builder(BaseSildingActivity.this).setTitle("������ʾ").setMessage(sb.toString())
		// .setMessage("�����°汾���Ƿ����")
				.setPositiveButton("��", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialoginterface, int i) {
						mIntent.putExtra("downloadsite", UpdateSoftWareTools.download);
						mIntent.setClass(BaseSildingActivity.this, PreLoadAPKUpdateProgressActivity.class);
						startActivity(mIntent);
						overridePendingTransition(R.anim.slide_in_right,R.anim.silde_out_left);
						// if (mInternet != 0) {
						// } else {
						// Toast.makeText(MainMenuActivity.this, "û�����磡",
						// Toast.LENGTH_SHORT).show();
						// }

					}
				}).setNegativeButton("��", new DialogInterface.OnClickListener() {

					public void onClick(DialogInterface dialog, int which) {

					}
				}).show();
	}

	// ���˹رմ�ͼ��
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			// ��ֹ�໬ʱ��ʾ�˳���ť
			if (Constants.OPENED) {
				return false;
			}
			if (RankFragment.mViewPagerRank != null) {
				RankChildFragment rlvfc = (RankChildFragment) RankFragment.mViewPagerRank.getAdapter().instantiateItem(
						RankFragment.mViewPagerRank, RankFragment.mCurrIndex);
				if (rlvfc.isBigFaceShowing()) {
					rlvfc.closeBigFace();
					return true;
				}
			}
			if (FriendFragment.isFaceShowing) {
				FriendFragment.closebigFace();
				return true;
			}
			if(isServiceRunning())
				tipExit("��ͼ�Ʋ������в����˳�","��̨����");
			else
				tipExit("��ȷ��Ҫ�˳���","�˳�");
			
			return true;
		}
		return false;
	}
	
	private boolean isServiceRunning(){
		return Common.isServiceRunning(this, Constants.SERVICE_RUNNING_NAME);
	}

	/**
	 * ���ؼ��˳�
	 */
	protected void tipExit(String t1,String t2) {
		mAlertDialog = new AlertDialog.Builder(this).setTitle("�˳�")
				.setMessage(t1 + this.getString(R.string.app_name) + " ��")
				// �����Զ���Ի������ʽ
				.setPositiveButton(t2, // ����"ȷ��"��ť
						new DialogInterface.OnClickListener() // �����¼�����
						{
							public void onClick(DialogInterface dialog, int whichButton) {
								if(isServiceRunning()){
									Intent intent = new Intent(Intent.ACTION_MAIN);
									intent.addCategory(Intent.CATEGORY_HOME);
									intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
									startActivity(intent);
									overridePendingTransition(R.anim.slide_in_right,R.anim.silde_out_left);
								}else{
									JPushInterface.clearAllNotifications(BaseSildingActivity.this);
									JPushInterface.stopPush(BaseSildingActivity.this);
//									StepRecoderFragment.sendAllowToReceiver(BaseSildingActivity.this, false);
									BaseSildingActivity.this.finish();
//									System.exit(0);
								}
							}
						}).setNegativeButton("ȡ��", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
					}
				}).create();
		mAlertDialog.show();
	}
}
