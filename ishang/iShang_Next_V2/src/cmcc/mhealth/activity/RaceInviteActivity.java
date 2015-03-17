package cmcc.mhealth.activity;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import cmcc.mhealth.R;
import cmcc.mhealth.adapter.RaceInvitedAdapter;
import cmcc.mhealth.basic.SampleActivity;
import cmcc.mhealth.bean.OrgnizeMemberInfo;
import cmcc.mhealth.common.CatchUserDialog;
import cmcc.mhealth.common.CatchUserDialog.UserCapturedListener;
import cmcc.mhealth.common.SendingPushMessags;
import cmcc.mhealth.common.SharedPreferredKey;
import cmcc.mhealth.db.MHealthProviderMetaData;

public class RaceInviteActivity extends SampleActivity {
	private ListView mInvitedList;
	private TextView mTVBtnOK;
	private RaceInvitedAdapter mInvateAdapter;

	private RelativeLayout mActionButton;
	private List<OrgnizeMemberInfo> mFriends;
	private List<Boolean> mCheckRecorder;

	private CatchUserDialog mCatchDialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setContentView(R.layout.activity_race_invitelist);
		super.onCreate(savedInstanceState);
		initView();
	}

	private void initView() {
		findViews();
		setViews();
		setClickers();
	}

	private void findViews() {
		mInvitedList = (ListView) findViewById(R.id.aril_invitedlist);
		mActionButton = (RelativeLayout) findViewById(R.id.imageButton_title_add);
		mTVBtnOK = (TextView) findViewById(R.id.ari_tvbtn_ok);
	}

	private void setViews() {
		mActionButton.setVisibility(View.VISIBLE);
		mFriends = MHealthProviderMetaData.GetMHealthProvider(RaceInviteActivity.this).getMyFriends();
		mCheckRecorder = new ArrayList<Boolean>();
		for (OrgnizeMemberInfo omi : mFriends) {
			mCheckRecorder.add(false);
		}
		mInvateAdapter = new RaceInvitedAdapter(RaceInviteActivity.this, mFriends, mCheckRecorder);
		mInvitedList.setAdapter(mInvateAdapter);
		
		mTVBtnOK.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				StringBuilder sb = new StringBuilder();
				int i = 0;
				for (Boolean isclicked : mCheckRecorder) {
					if (isclicked) {
						sb.append("," + mFriends.get(i).friendphone);
					}
					i++;
				}
				Intent intent = new Intent();
				if (sb.toString().length() > 1) {
					intent.putExtra("racedata", getIntent().getExtras().getParcelable("racedata"));
					intent.putExtra("phoneStrs", sb.substring(1));
					RaceInviteActivity.this.setResult(RESULT_OK, intent);
				} else {
					RaceInviteActivity.this.setResult(RESULT_CANCELED, intent);
				}
				RaceInviteActivity.this.finish();
				overridePendingTransition(R.anim.slide_in_left,R.anim.silde_out_right);
			}

		});
	}

	private void setClickers() {
		mInvitedList.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				LinearLayout itemll = (LinearLayout) view.findViewById(R.id.lli_mainLLlayout);
				if ("true".equals(getIntent().getExtras().getString("getone"))) {
					int i = 0;
					for (Boolean ischecked : mCheckRecorder) {
						if (position == i) {
							mCheckRecorder.set(i, true);
						} else {
							mCheckRecorder.set(i, false);
						}
						i++;
					}
				} else {
					mCheckRecorder.set(position, !mCheckRecorder.get(position));
				}
				mInvateAdapter.notifyDataSetChanged();
			}
		});

		mActionButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				mCatchDialog = new CatchUserDialog(RaceInviteActivity.this, mPhoneNum, mPassword);
				mCatchDialog.startCapture("邀请");
				mCatchDialog.setOnUserCapturedListener(new UserCapturedListener() {
					@Override
					public void onCapturedUser(int state, String reason, String name, String avatar, String targetphone) {
						switch (state) {
						case CatchUserDialog.CAPTURE_SUCCESS:
							OrgnizeMemberInfo omi = new OrgnizeMemberInfo();
							omi.avatar = avatar;
							omi.membername = name;
							omi.friendphone = targetphone;
							for (OrgnizeMemberInfo theomi : mFriends) {
								if (theomi.friendphone.equals(targetphone)) {
									BaseToast("该用户已经在列表中了。");
									return;
								} else if (mPhoneNum.equals(targetphone)) {
									BaseToast("您不能邀请自己");
									return;
								}
							}
							mFriends.add(omi);
							mCheckRecorder.add(true);
							mInvateAdapter.notifyDataSetChanged();
							break;
						case CatchUserDialog.CAPTURE_FAIL:
							BaseToast(reason, 5);
							break;
						}
					}
				});
			}
		});
	}
}
