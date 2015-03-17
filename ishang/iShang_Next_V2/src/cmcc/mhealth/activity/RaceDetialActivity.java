package cmcc.mhealth.activity;

import java.util.Date;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import cmcc.mhealth.R;
import cmcc.mhealth.adapter.RaceDetailAdapter;
import cmcc.mhealth.basic.SampleActivity;
import cmcc.mhealth.bean.BackInfo;
import cmcc.mhealth.bean.RaceData;
import cmcc.mhealth.bean.RaceMemberInfo;
import cmcc.mhealth.common.AlertDialogs;
import cmcc.mhealth.common.Common;
import cmcc.mhealth.common.Config;
import cmcc.mhealth.common.Constants;
import cmcc.mhealth.common.ImageUtil;
import cmcc.mhealth.db.MHealthProviderMetaData;
import cmcc.mhealth.net.DataSyn;
import cmcc.mhealth.slidingcontrol.FriendFragment;
import cmcc.mhealth.slidingcontrol.RankChildFragment;
import cmcc.mhealth.slidingcontrol.RankFragment;
import cmcc.mhealth.view.ScrollForeverTextView;

/**
 * �������� ͨ��intent extra��ֵ���ݽ���
 * 
 * @author zy
 * 
 */
public class RaceDetialActivity extends SampleActivity {
	// Views
	private TextView mTitle;
	private TextView mTime;
	private TextView mFounder;
	private TextView mDetail;
	private TextView mMember;

	private ListView mMemberList;
	private RaceDetailAdapter adapter;
	private ImageView mTitleImage;

	private RelativeLayout mJoinRL;
	private TextView mJoinButtonTV;
	private ImageView mJoinButtonIV;
	private TextView mExitButton;
	
	private boolean mMemberChanged;
	private String mMemberNum;
	private int mMemberNumAtFirst;
	private int mPosition;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setContentView(R.layout.activity_race_detail);
		super.onCreate(savedInstanceState);
	}

	// ��ʼ��detial
	private void initView(RaceData rd, int size) {
		findViews();
		setViews(rd);
		loadListView(rd, size);
	}
	
	@Override
	protected void initClickers() {
		mBack.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				updateRaceMemberAtSuperClass();
				RaceDetialActivity.this.finish();
				overridePendingTransition(R.anim.slide_in_left,R.anim.silde_out_right);
			}
		});
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			updateRaceMemberAtSuperClass();
			RaceDetialActivity.this.finish();
			overridePendingTransition(R.anim.slide_in_left,R.anim.silde_out_right);
			return true;
		}
		return false;
	}

	private void updateRaceMemberAtSuperClass() {
		if (mMemberChanged) {
			Intent intent = getIntent().putExtra("position", mPosition).putExtra("membernum", mMemberNum);
			setResult(RESULT_OK, intent);
		}
	}

	// �������
	private void setClickers(final RaceData rd, final int size, final String alreadyin) {
		// ����Ѿ���ʼ����û���κε���¼�
		if (!"0".equals(rd.getStarted())){
			if(Long.parseLong(rd.endtime) < new Date().getTime()){
				setButtonStyle("�����Ѿ�������");
				return;
			}
			setButtonStyle("�����Ѿ���ʼ��");
			return;
		}
		
		if (mPhoneNum.equals(rd.founderphone)) {
			mExitButton.setVisibility(View.GONE);
			mJoinRL.setBackgroundResource(R.drawable.sample_usercatch_button_ok_bg);
			mJoinButtonTV.setText("��������");
			mJoinButtonTV.setTextColor(getResources().getColor(R.color.white));
			mJoinButtonIV.setVisibility(View.VISIBLE);
			mJoinRL.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					// ��������
					mJoinRL.setOnClickListener(null);
					inviteUsers(rd);
				}
			});
		} else {
			if ("0".equals(alreadyin)) {
				mExitButton.setVisibility(View.GONE);
				mJoinRL.setBackgroundResource(R.drawable.sample_usercatch_button_ok_bg);
				mJoinButtonTV.setText("��  ��");
				mJoinButtonTV.setTextColor(getResources().getColor(R.color.white));
				mJoinButtonIV.setVisibility(View.VISIBLE);
				mJoinRL.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						// ���뾺��
						joinRace(rd, size);
					}
				});
			} else {
				setButtonStyle("���Ѿ�����þ�����");
				mExitButton.setVisibility(View.VISIBLE);
				mExitButton.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						//�˳�����
						exitRaces(rd, size);
					}
				});
			}
		}
	}
	//ֻ��ʾ�ֵ�button״̬
	private void setButtonStyle(String str) {
		mJoinRL.setBackgroundDrawable(null);
		mJoinRL.setOnClickListener(null);
		mExitButton.setOnClickListener(null);
		mExitButton.setVisibility(View.GONE);
		mJoinButtonTV.setText(str);
		mJoinButtonTV.setTextColor(getResources().getColor(R.color.blue_low));
		mJoinButtonIV.setVisibility(View.GONE);
	}

	// �˳�����
	private void exitRaces(final RaceData rd, final int size) {
		AlertDialogs.showOKorNODialog("��Ҫ�˳�������", RaceDetialActivity.this, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				mExitButton.setOnClickListener(null);
				new Thread() {
					public void run() {
						BackInfo reqData = new BackInfo();
						switch (DataSyn.getInstance().resignRace(mPhoneNum, mPassword, rd.getRaceid(), reqData)) {
						case 0:
							Message msg = Message.obtain();
							Bundle data = new Bundle();
							data.putParcelable("resigninfo", reqData);
							data.putParcelable("racedata", rd);
							data.putInt("size", size - 1);
							msg.setData(data);
							msg.what = RESIGN_SUCCESS;
							handler.sendMessage(msg);
							break;
						default:
							handler.sendEmptyMessage(NET_FAIL);
							break;
						}
					};
				}.start();
			}

		});
	}

	// ���뾺��
	private void joinRace(final RaceData rd, final int size) {
		if ("3".equals(rd.getType())) {// �ŶӾ���ѡ���Ŷ�
			AlertDialogs.creatSingleChoiceDialog("��Ҫѡ���Ǹ����飿", RaceDetialActivity.this, new String[] { "TeamA", "TeamB" }, new AlertDialogs.onChoicedTeamListener() {
				@Override
				public void onChoicedTeam(int team) {
					sendJoinApply(rd, size, team);
				}
			});
		} else {// ��ͨ��������ѡ���Ŷ�
			sendJoinApply(rd, size);
		}
	}

	// ��������
	private void inviteUsers(final RaceData rd) {
		Intent intent = new Intent(RaceDetialActivity.this, RaceInviteActivity.class);
		intent.putExtra("sampletitle", "����");
		intent.putExtra("racedata", rd);
		if ("1".equals(rd.getType())) {
			intent.putExtra("getone", "true");
		} else {
			intent.putExtra("getone", "false");
		}
		startActivityForResult(intent, 1);
	}

	// ���ͼ�������
	private void sendJoinApply(final RaceData rd, final int size) {
		mJoinRL.setOnClickListener(null);
		mJoinButtonIV.setVisibility(View.GONE);
		sendJoinApply(rd, size, 0);
	}

	// ���ͼ�������
	private void sendJoinApply(final RaceData rd, final int size, final int team) {
		final String[] teamnames = new String[] { "TeamA", "TeamB", "TeamC" };
		mJoinButtonTV.setText("�����ύ");
		new Thread() {
			public void run() {
				BackInfo reqData = new BackInfo();
				if ("1".equals(rd.getType()) && size > 1) {
					handler.sendEmptyMessage(JOIN_TYPE1_FULL);
					return;
				}
				Message msg = Message.obtain();
				Bundle data = new Bundle();
				int suc = DataSyn.getInstance().joinRace(mPhoneNum, mPassword, rd.getRaceid(), reqData, teamnames[team]);
				data.putParcelable("backinfo", reqData);
				data.putParcelable("racedata", rd);
				data.putInt("size", size + 1);
				msg.setData(data);
				switch (suc) {
				case 0:
					msg.what = JOIN_SUCCESS;
					break;
				default:
					msg.what = NET_FAIL;
					break;
				}
				handler.sendMessage(msg);
			};
		}.start();
	}

	@Override
	protected void onResume() {
		super.onResume();
		RaceData rd = getIntent().getExtras().getParcelable("racedata");
		int size = getIntent().getExtras().getInt("racesize");
		mPosition = getIntent().getExtras().getInt("position");
		
		if (rd == null)
			return;
		initView(rd, size);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == 1) {
			if (resultCode == RESULT_OK) {
				Bundle extras = data.getExtras();
				RaceData rd = extras.getParcelable("racedata");
				setInvite((String) extras.get("phoneStrs"), rd);
			}
		}
	}

	private void setInvite(final String phoneStrs, final RaceData rd) {
		new Thread() {
			public void run() {
				BackInfo reqData = new BackInfo();
				switch (DataSyn.getInstance().inviteRaceMember(mPhoneNum, mPassword, rd.getRaceid(), phoneStrs, reqData)) {
				case 0:
					Message msg = Message.obtain();
					Bundle data = new Bundle();
					data.putParcelable("backinfo", reqData);
					msg.setData(data);
					msg.what = INVITE_SUCCESS;
					handler.sendMessage(msg);
					break;
				default:
					break;
				}
			};
		}.start();
	}

	// ��侺����Աlistview
	private void loadListView(final RaceData rd, final int size) {
		new Thread() {
			public void run() {
				RaceMemberInfo rmiData = new RaceMemberInfo();
				switch (DataSyn.getInstance().getRaceInfo(mPhoneNum, mPassword, rd.getRaceid(), rmiData)) {
				case 0:
					Message msg = Message.obtain();
					Bundle data = new Bundle();
					data.putParcelable("detialdata", rmiData);
					data.putParcelable("racedata", rd);
					data.putInt("size", size);
					msg.what = GET_DETAIL_SUCCESS;
					msg.setData(data);
					handler.sendMessage(msg);
					break;
				default:
					handler.sendEmptyMessage(NET_FAIL);
					break;
				}
			};
		}.start();
	}

	private static final int GET_DETAIL_SUCCESS = 0;
	private static final int JOIN_SUCCESS = 2;
	private static final int NET_FAIL = 3;
	private static final int JOIN_TYPE1_FULL = 4;
	private static final int RESIGN_SUCCESS = 5;
	private static final int REFLESH = 6;
	private static final int INVITE_SUCCESS = 8;
	private Handler handler = new Handler() {
		@Override
		public void dispatchMessage(Message msg) {
			Bundle data = msg.getData();
			RaceData rd = data.getParcelable("racedata");
			Message msgcopy = Message.obtain();
			msgcopy.setData(data);
			switch (msg.what) {
			case INVITE_SUCCESS:
				BackInfo reqData = data.getParcelable("backinfo");
				BaseToast(reqData.reason, 5);
				break;
			case REFLESH:
				loadListView(rd, data.getInt("size"));
				break;
			case RESIGN_SUCCESS:
				BackInfo resignInfo = data.getParcelable("resigninfo");
				msgcopy.what = REFLESH;
				handler.sendMessageDelayed(msgcopy, 1000);
				BaseToast(resignInfo.reason, 10);
				break;
			case JOIN_SUCCESS:
				BackInfo backInfo = data.getParcelable("backinfo");
				msgcopy.what = REFLESH;
				handler.sendMessageDelayed(msgcopy, 1000);
				BaseToast(backInfo.reason, 10);
				break;
			case NET_FAIL:
				setButtonStyle("��ȷ����������½���");
				BaseToast("��ȷ�����糩ͨ", 5);
				break;
			case JOIN_TYPE1_FULL:
				setButtonStyle("���˻�������˲μӡ�");
				BaseToast("���˻�������˲μ�", 5);
				break;
			case GET_DETAIL_SUCCESS:
				RaceMemberInfo rmiData = data.getParcelable("detialdata");
				notifyAdapter(rmiData);
				setClickers(rd, data.getInt("size"), rmiData.alreadyin);
				mMember.setText("����" + rmiData.racemember.size() + "�˲μ�");
				MHealthProviderMetaData.GetMHealthProvider(RaceDetialActivity.this).modifySingleRaceMemberNum(rmiData.racemember.size(), rd.getRaceid());
				mMemberChanged = (mMemberNumAtFirst != rmiData.racemember.size());
				mMemberNum = rmiData.racemember.size() + "";
				break;
			}
		}
	};

	// ��views��ֵ
	private void setViews(RaceData rd) {
		mTitle.setText(rd.getRacename());
		mTime.setText(Common.getCurrentDayLongTime(Long.parseLong(rd.getStarttime())) + " �� " + Common.getCurrentDayLongTime(Long.parseLong(rd.getEndtime())));
		mFounder.setText("������:" + rd.getFoundername());
		mDetail.setText(rd.getRacedetail());
		mMember.setText("����" + rd.getMembernum() + "�˲μ�");
		mTitleImage.setTag("mTitleImage");
		if(!rd.getTitlepicurl().contains("_big.")){
			rd.setTitlepicurl(rd.getTitlepicurl().replace(".", "_big."));
		}
		ImageUtil.getInstance().loadBitmap(mTitleImage, Config.RACE_PIC_SERVER_ROOT + rd.getTitlepicurl(), "mTitleImage", 0);
		setButtonStyle("���뾺����Ա��...");
		mMemberNumAtFirst = Integer.parseInt(rd.getMembernum());
	}

	// findviewbyid
	private void findViews() {
		mTitleImage = (ImageView) findViewById(R.id.ard_title_image);
		mTitle = (TextView) findViewById(R.id.ard_title);
		mTime = (TextView) findViewById(R.id.ard_time);
		mFounder = (TextView) findViewById(R.id.ard_founder);
		mDetail = (ScrollForeverTextView) findViewById(R.id.ard_details);
		mMember = (TextView) findViewById(R.id.ard_membernum);
		mMemberList = (ListView) findViewById(R.id.ard_memberlist);
		mJoinRL = (RelativeLayout) findViewById(R.id.ard_join_relativelayout);
		mJoinButtonTV = (TextView) findViewById(R.id.ard_join_text);
		mJoinButtonIV = (ImageView) findViewById(R.id.ard_join_icon);
		mExitButton = (TextView) findViewById(R.id.ard_exit);
	}

	// ����adapter
	private void setAdapter(RaceMemberInfo rmiData) {
		adapter = new RaceDetailAdapter(RaceDetialActivity.this, rmiData);
		mMemberList.setAdapter(adapter);
	}

	// ��ˢadapter
	private void notifyAdapter(RaceMemberInfo rmiData) {
		if (adapter != null) {
			adapter.setRmi(rmiData);
			adapter.notifyDataSetChanged();
		} else {
			setAdapter(rmiData);
		}
	}
}
