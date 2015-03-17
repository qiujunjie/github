package cmcc.mhealth.slidingcontrol;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import cmcc.mhealth.R;
import cmcc.mhealth.activity.MapStartRunningFragment;
import cmcc.mhealth.activity.avatar.CropImageActivity;
import cmcc.mhealth.activity.avatar.CropUtil;
import cmcc.mhealth.basic.BaseFragment;
import cmcc.mhealth.bean.ClubData;
import cmcc.mhealth.bean.ClubListInfo;
import cmcc.mhealth.bean.FindFriendInfo;
import cmcc.mhealth.bean.MenuItem;
import cmcc.mhealth.bean.UpdatePasswordInfo;
import cmcc.mhealth.bean.UserRegInfo;
import cmcc.mhealth.common.Common;
import cmcc.mhealth.common.Constants;
import cmcc.mhealth.common.Encrypt;
import cmcc.mhealth.common.ImageUtil;
import cmcc.mhealth.common.Logger;
import cmcc.mhealth.common.PreferencesUtils;
import cmcc.mhealth.common.SharedPreferredKey;
import cmcc.mhealth.common.UploadUtil;
import cmcc.mhealth.common.UploadUtil.OnUploadProcessListener;
import cmcc.mhealth.net.DataSyn;
import cn.jpush.android.api.JPushInterface;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

public class MenuFragment extends BaseFragment implements OnItemClickListener, OnClickListener, OnUploadProcessListener {
	// private LinearLayout mLinearLayout;
	private int mSelected = 0;
	private ListView mListView;
	private RelativeLayout mExitButton;

	private List<MenuItem> mMenuList;

	public static boolean mCanBeClick;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.menu_list, null);
		mCanBeClick = true;
		super.onCreateView(inflater, (ViewGroup) view, savedInstanceState);
		return view;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
	}

	private ImageView mImageViewList;
	private TextView mTextViewMenu;
	private ImageView mImageViewRight;
	private TextView mTextviewCompany;
	private TextView mMessageNumTip;
	private int mMessageNum;

	class MyAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			return mMenuList.size();
		}

		@Override
		public Object getItem(int position) {
			return position;
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View view = null;

			switch (mMenuList.get(position).getViewType()) {
			case MenuItem.GROUP_TITLE:
				view = LayoutInflater.from(mActivity).inflate(R.layout.title_item, null);
				mTextViewMenu = (TextView) view.findViewById(R.id.leftmenu_title_textview_company);
				mTextViewMenu.setText(mMenuList.get(position).getMenuName());
				break;
			case MenuItem.NORMAL_ITEM:
				view = LayoutInflater.from(mActivity).inflate(R.layout.sliding_menu_left_listitem, null);
				mImageViewList = (ImageView) view.findViewById(R.id.simple_list_image);
				mImageViewRight = (ImageView) view.findViewById(R.id.img_slidingmenu_right);
				mTextViewMenu = (TextView) view.findViewById(R.id.simple_list_text);
				mMessageNumTip = (TextView) view.findViewById(R.id.simple_list_message_tip);

				if (mMessageNum > 0 && mMenuList.get(position).getId() == 7) {
					mMessageNumTip.setVisibility(View.VISIBLE);
					mMessageNumTip.setText(mMessageNum + "");
				} else {
					mMessageNumTip.setVisibility(View.GONE);
				}

				if (position == mSelected) {
					mImageViewList.setImageDrawable(mActivity.getResources().getDrawable(mMenuList.get(mSelected).getDrawablesLight()));
					mImageViewRight.setImageDrawable(mActivity.getResources().getDrawable(R.drawable.slidingmenu_right_on));
					mTextViewMenu.setTextColor(mActivity.getResources().getColor(R.color.menu_blue));
				} else {
					mImageViewList.setImageDrawable(mActivity.getResources().getDrawable(mMenuList.get(position).getDrawablesBlack()));
				}
				mTextViewMenu.setText(mMenuList.get(position).getMenuName());
				if (mMenuList.get(position).getId() == 0) {
					((RelativeLayout) view.findViewById(R.id.rel_bg_menu_item)).setBackgroundColor(mActivity.getResources().getColor(R.color.racelist_bg));
				}
				break;
			case MenuItem.CHILD_ITEM:
				view = LayoutInflater.from(mActivity).inflate(R.layout.company_item, null);
				mTextviewCompany = (TextView) view.findViewById(R.id.textview_company);
				mTextviewCompany.setText(mMenuList.get(position).getMenuName());

				if (position == mSelected) {
					mTextviewCompany.setTextColor(mActivity.getResources().getColor(R.color.menu_blue));
				} else {
					mTextviewCompany.setTextColor(mActivity.getResources().getColor(R.color.menu_gray));
				}
				break;
			}
			return view;
		}
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		if (!mCanBeClick || mMenuList == null || mMenuList.size() == 0)
			return;
		mCanBeClick = false;
		Fragment newContent = null;
		MainCenterActivity fca = (MainCenterActivity) getActivity();
		if (fca == null)
			return;

		if (mSelected == position) {
			fca.showContent();
			return;
		}

		switch (mMenuList.get(position).getId()) {
		case 1:
			newContent = new PedometorFragment(Constants.PedoBriefActivity);
			break;
		case 2:
			newContent = new HistoryListFragment();
			break;
		case 3:
			newContent = new VitalSignFragment();
			break;
		case 4:
			newContent = new RankFragment(mMenuList.get(position).getClubid());
			break;
		case 5:
			newContent = new CampaignFragment(mMenuList.get(position).getClubid());
			break;
		case 6:
			newContent = new FriendFragment();
			break;
		case 7:
			newContent = new MessageFragment();
			break;
		case 8:
			newContent = new SettingFragment(-1);
			break;
		case 9:
			newContent = new RaceFragment();
			break;
		case 10:
			if(Common.isServiceRunning(mActivity, Constants.SERVICE_RUNNING_NAME))
				newContent = new MapStartRunningFragment();
			else
				newContent = new MapFragment();
			break;
		case 11:
			newContent = new WritingDatasFragment();
			break;
		case 12:
			newContent = new MedicineFragment();
			break;
		case 13:
			newContent = new WebViewFragment("http://phr.cmri.cn/datav2/account.do?action=medicineList");
			break;
		case 14:
//			newContennt = new WebViewFragment("http://phr.cmri.cn/datav2/account.do?action=medicineList");
			newContent = new WritingDatasFragment();
			break;
		case 15:
//			newContent = new WebViewFragment("http://phr.cmri.cn/datav2/account.do?action=medicineList");
			newContent = new WritingDatasFragment();
			break;
		default:
			mCanBeClick = true;
			return;
		}
		try {
			textColorChange(parent, view, position);
		} catch (Exception e) {
			e.printStackTrace();
		}

		if (newContent != null) {
			fca.switchFragment((BaseFragment) fca.getContent(), (BaseFragment) newContent);
			mSelected = position;
			Editor edit = sp.edit();
			edit.putInt("mSelected", mSelected);
			edit.commit();
		}
	}

	private void textColorChange(AdapterView<?> parent, View view, int position) {
		int i = parent.getFirstVisiblePosition();
		if (i != 0 && mSelected != 0) {
			mSelected -= i;
		}
		View child = parent.getChildAt(mSelected);
		if (i != 0 && mSelected != parent.getChildCount())
			mSelected += i;

		if (mMenuList.get(position).getViewType() == MenuItem.CHILD_ITEM) {
			mTextviewCompany = (TextView) view.findViewById(R.id.textview_company);
			Logger.d(TAG, mTextviewCompany.getText() + " ----------position text");
			mTextviewCompany.setTextColor(mActivity.getResources().getColor(R.color.menu_blue));
		} else {
			TextView textView1 = (TextView) view.findViewById(R.id.simple_list_text);
			textView1.setTextColor(mActivity.getResources().getColor(R.color.menu_blue));
			ImageView imageView3 = (ImageView) view.findViewById(R.id.simple_list_image);
			imageView3.setImageDrawable(mActivity.getResources().getDrawable(mMenuList.get(position).getDrawablesLight()));
			ImageView imageView4 = (ImageView) view.findViewById(R.id.img_slidingmenu_right);
			imageView4.setImageDrawable(mActivity.getResources().getDrawable(R.drawable.slidingmenu_right_on));
		}

		if (mSelected == 0)
			return;
		if (mMenuList.get(mSelected).getViewType() == MenuItem.CHILD_ITEM) {
			mTextviewCompany = (TextView) child.findViewById(R.id.textview_company);
			Logger.d(TAG, mTextviewCompany.getText() + " ----------mSelected text");
			mTextviewCompany.setTextColor(mActivity.getResources().getColor(R.color.menu_gray));
		} else {
			ImageView imageView = (ImageView) child.findViewById(R.id.simple_list_image);
			imageView.setImageDrawable(mActivity.getResources().getDrawable(mMenuList.get(mSelected).getDrawablesBlack()));
			ImageView imageView2 = (ImageView) child.findViewById(R.id.img_slidingmenu_right);
			imageView2.setImageDrawable(mActivity.getResources().getDrawable(R.drawable.slidingmenu_right));
			TextView textView = (TextView) child.findViewById(R.id.simple_list_text);
			textView.setTextColor(mActivity.getResources().getColor(R.color.menu_gray));
		}
	}

	@Override
	public void findViews() {
		mImageViewAvater = findView(R.id.slidingmenu_avater);
		((Button) findView(R.id.button_setting)).setOnClickListener(this);
		loadClubList();
	}

	@Override
	public void clickListner() {
		mImageViewAvater.setOnClickListener(this);
		mExitButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
//				if(Common.isServiceRunning(mActivity, Constants.SERVICE_RUNNING_NAME)){
//					Intent intent = new Intent(Intent.ACTION_MAIN);
//					intent.addCategory(Intent.CATEGORY_HOME);
//					intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//					startActivity(intent);
//					mActivity.overridePendingTransition(R.anim.slide_in_right,R.anim.silde_out_left);
//				}else{
					JPushInterface.clearAllNotifications(mActivity);
					JPushInterface.stopPush(mActivity);
//					PedometorFragment.sendAllowToReceiver(mActivity, false);
					mActivity.finish();
//				}
			}
		});
	}

	@Override
	public synchronized void loadLogic() {
		menuAndName();
		final UserRegInfo mUserRegInfo = new UserRegInfo();
		new Thread() {
			public void run() {
				try {
					String phonenum = sp.getString(SharedPreferredKey.PHONENUM, null);
					String password = sp.getString(SharedPreferredKey.PASSWORD, null);
					int res = DataSyn.getInstance().getUserInfo(phonenum, password, mUserRegInfo);
					if (res == 0) {
						Editor editor = sp.edit();
						editor.putString(SharedPreferredKey.NAME, mUserRegInfo.personprofile.name);
						editor.putString(SharedPreferredKey.NICK_NAME, mUserRegInfo.personprofile.nickname);
						editor.putString(SharedPreferredKey.WEIGHT, mUserRegInfo.personprofile.weight);
						editor.putString(SharedPreferredKey.HEIGHT, mUserRegInfo.personprofile.height);
						editor.putString(SharedPreferredKey.GENDER, mUserRegInfo.personprofile.gender);
						editor.putString(SharedPreferredKey.BIRTHDAY, mUserRegInfo.personprofile.birthday);
						editor.putString(SharedPreferredKey.SCORE, mUserRegInfo.personprofile.score);
						editor.putString(SharedPreferredKey.AVATAR, mUserRegInfo.personprofile.avarta);
						// dit.putString("AvatericonPath",
						// mFrirndInfo.dataValue.get(0).avatar.split("\\.")[0]);
						editor.putString(SharedPreferredKey.TARGET_WEIGHT, mUserRegInfo.personprofile.targetweight);
						editor.putString(SharedPreferredKey.TARGET_STEP, mUserRegInfo.personprofile.targetstep);
						Logger.i(TAG, mUserRegInfo.personprofile.clubarray.size() + " ==");
						int size = mUserRegInfo.personprofile.clubarray.size();
						for (int i = 0; i < size; i++) {
							if (size == 1) {
								editor.putString(SharedPreferredKey.GROUP_NAME, mUserRegInfo.personprofile.clubarray.get(0).groupname);
								editor.putString(SharedPreferredKey.CORPORATION, mUserRegInfo.personprofile.clubarray.get(0).corporation);
								editor.putString(SharedPreferredKey.CLUB_ID, mUserRegInfo.personprofile.clubarray.get(0).clubid);
							} else if (size == 2) {
								editor.putString(SharedPreferredKey.GROUP_NAME_1, mUserRegInfo.personprofile.clubarray.get(1).groupname);
								editor.putString(SharedPreferredKey.CORPORATION_1, mUserRegInfo.personprofile.clubarray.get(1).corporation);
								editor.putString(SharedPreferredKey.CLUB_ID_1, mUserRegInfo.personprofile.clubarray.get(1).clubid);
							}
						}
						editor.commit();
						handler.sendEmptyMessage(GET_AVATARSTREING_FROM_NET);
					} else
						handler.sendEmptyMessage(GET_AVATARSTREING_FROM_NET);
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					handler.sendEmptyMessage(GET_AVATARSTREING_FROM_NET);
				}
			};
		}.start();

	}

	@Override
	public void onResume() {
		mSelected = sp.getInt("mSelected", 0);
		if (mAdapter != null) {
			mAdapter.notifyDataSetChanged();
		}
		super.onResume();
	}

	// 从本地获取头像
	private FindFriendInfo mFrirndInfo;

	public void getMenuAvater() {
		if (mImageViewAvater == null)
			mImageViewAvater = findView(R.id.slidingmenu_avater);
		mImageViewAvater.setImageDrawable(mActivity.getResources().getDrawable((sp.getString(SharedPreferredKey.GENDER, "0").equals("0") ? MainCenterActivity.BASE_ATATAR[Encrypt.getIntFromName(getMyName()) + 7] : MainCenterActivity.BASE_ATATAR[Encrypt.getIntFromName(getMyName())])));
		String avatarName = getAvater();
		if (!TextUtils.isEmpty(avatarName)) {
			String url = DataSyn.avatarHttpURL + avatarName + ".jpg";
			getImageAsync(mImageViewAvater, url);
		}
	}

	@Override
	public void onClick(View arg0) {
		switch (arg0.getId()) {
		case R.id.slidingmenu_avater:
			updateAvater();
			break;
		case R.id.button_setting:
			Fragment newContent = new SettingFragment(-1);
			MainCenterActivity fca = (MainCenterActivity) getActivity();
			fca.switchFragment((BaseFragment) fca.getContent(), (BaseFragment) newContent);
			mSelected = 0;
			Editor edit = sp.edit();
			edit.putInt("mSelected", mSelected);
			edit.commit();
			if (mAdapter != null) {
				mAdapter.notifyDataSetChanged();
			}
			break;
		default:
			break;
		}
	}

	public void setToMessageFragment() {
		Fragment newContent = new MessageFragment();
		MainCenterActivity fca = (MainCenterActivity) getActivity();
		fca.switchFragment((BaseFragment) fca.getContent(), (BaseFragment) newContent);
		mSelected = 0;
		Editor edit = sp.edit();
		edit.putInt("mSelected", mSelected);
		edit.commit();
		if (mAdapter != null) {
			mAdapter.notifyDataSetChanged();
		}
	}

	public void RefleshMessageNumTip(int mmn) {
		mMessageNum = mmn;
		if (mAdapter != null) {
			mAdapter.notifyDataSetChanged();
		}
	}

	private static final int IMAGE_REQUEST_CODE1 = 911;
	private static final int IMAGE_REQUEST_CODE2 = 912;
	private static final int IMAGE_REQUEST_CODE3 = 913;
	private String[] items = new String[] { "选择本地图片", "拍照" };
	private static final String ImgUrl = Environment.getExternalStorageDirectory() + "/ishang_image";// +MD5.getMD5(url));
	protected static final String TAG = "MenuFragmeny";

	/**
	 * 弹出dialog选择更换头像
	 */
	private void updateAvater() {
		new AlertDialog.Builder(mActivity).setTitle("设置头像").setItems(items, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				switch (which) {
				case 0:
					// 从相册选择图片
					Intent intentFromGallery = new Intent();
					intentFromGallery.setType("image/*");
					intentFromGallery.setAction(Intent.ACTION_GET_CONTENT);
					startActivityForResult(intentFromGallery, IMAGE_REQUEST_CODE1);
					mActivity.overridePendingTransition(R.anim.slide_in_right,R.anim.silde_out_left);
					break;
				case 1:
					Intent intentFromCapture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
					// 判断存储卡是否可以用，可用进行存储
					if (Common.existSDcard()) {
						if (getFileImagePathDexed() != null) {
							File mediaFile = new File(getFileImagePathDexed() + "_temp");
							if (mediaFile.exists()) {
								mediaFile.delete();
							}
							intentFromCapture.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(mediaFile));
						}
					}
					startActivityForResult(intentFromCapture, IMAGE_REQUEST_CODE2);
					mActivity.overridePendingTransition(R.anim.slide_in_right,R.anim.silde_out_left);
					break;
				}
			}
		}).setNegativeButton("取消", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		}).show();
	}

	private String mFileImagePathDexed = null;

	private String getFileImagePathDexed() {
		if (mFileImagePathDexed == null) {
			File mediaStorageDir = new File(ImgUrl);
			if (!mediaStorageDir.exists()) {
				if (!mediaStorageDir.mkdirs()) {
					Logger.e(TAG, "failed to create directory");
					return null;
				}
			}
			String avaterName = getAvater();
			if (avaterName != null) {
				mFileImagePathDexed = mediaStorageDir.getPath() + File.separator + Encrypt.getMD5Str(avaterName);
			} else {
				String phone = getPhoneNum();
				mFileImagePathDexed = mediaStorageDir.getPath() + File.separator + Encrypt.getMD5Str(phone);
			}
		}
		return mFileImagePathDexed;
	}

	/**
	 * 去上传文件
	 */
	protected static final int TO_UPLOAD_FILE = 1;

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == Activity.RESULT_OK && requestCode == IMAGE_REQUEST_CODE1) {
			reuterSelectPicture(data);
		} else if (resultCode == Activity.RESULT_OK && requestCode == IMAGE_REQUEST_CODE2) {
			returnTakePicture();
		} else if (resultCode == Activity.RESULT_OK && requestCode == IMAGE_REQUEST_CODE3) {
			try {
				Bundle extras = data.getExtras();
				String photoPath = extras.getString("cachePath");

				if (photoPath != null) {
					handler.sendEmptyMessage(TO_UPLOAD_FILE);
				} else {
					BaseToast("上传的文件路径出错");
				}

			} catch (Exception e) {
				e.printStackTrace();
				messagesManager(Constants.MESSAGE_CROP_FAILED);
			}
		}
	}

	/**
	 * 返回照相的图片
	 */
	private void returnTakePicture() {
		int nRoate = 0;
		int exifOrientation;
		ExifInterface exif;
		try {
			exif = new ExifInterface(getFileImagePathDexed() + "_temp");
			exifOrientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, -1);
			if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_90) {
				nRoate = 90;
			} else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_180) {
				nRoate = 180;
			} else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_270) {
				nRoate = 270;
			}
			Logger.e(TAG, nRoate + "");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} // Since API Level 5
		cropPhoto(Uri.fromFile(new File(getFileImagePathDexed() + "_temp")), getFileImagePathDexed() + "_temp", nRoate);
	}

	private ArrayList<String> mFilePaths = new ArrayList<String>();

	private ArrayList<String> getFileImagePath() {
		String mFileImagePath;
		File mediaStorageDir = new File(ImgUrl);
		if (!mediaStorageDir.exists()) {
			if (!mediaStorageDir.mkdirs()) {
				Logger.e(TAG, "failed to create directory");
				return null;
			}
		}
		// 13552019273_47
		String avaterName = getAvater();

		String fileImagePathDexed;
		if (avaterName != null) {
			String bitmapName = avaterName.substring(avaterName.lastIndexOf("/") + 1);
			// /storage/emulated/0/ishang_image/13552019273_47
			mFileImagePath = mediaStorageDir.getPath() + File.separator + bitmapName;
			// /storage/emulated/0/ishang_image/13649799BFC82223
			fileImagePathDexed = mediaStorageDir.getPath() + File.separator + Encrypt.getMD5Str(bitmapName);
		} else {
			String phone = getPhoneNum();
			mFileImagePath = mediaStorageDir.getPath() + File.separator + phone;
			fileImagePathDexed = mediaStorageDir.getPath() + File.separator + Encrypt.getMD5Str(phone);
		}
		mFilePaths.add(mFileImagePath);
		mFilePaths.add(fileImagePathDexed);
		return mFilePaths;
	}

	/**
	 * 本地图片上传
	 * 
	 * @param data
	 */
	private void reuterSelectPicture(Intent data) {
		Uri originalUri = data.getData(); // 获得图片的uri
		ArrayList<String> list = getFileImagePath();
		if (originalUri != null && Common.existSDcard()) {
			int nRoate = 0;
			int exifOrientation;
			ExifInterface exif;
			try {
				exif = new ExifInterface(list == null ? "null" : list.get(1) + "_temp");
				exifOrientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, -1);
				if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_90) {
					nRoate = 90;
				} else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_180) {
					nRoate = 180;
				} else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_270) {
					nRoate = 270;
				}
				Logger.e(TAG, nRoate + "");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} // Since API Level 5
			cropPhoto(originalUri, list.get(1) + "_temp", nRoate);
		} else {
			messagesManager(Constants.MESSAGE_CROP_FAILED);
		}
	}

	private void cropPhoto(Uri originalUri, String cachePath, int nRoate) {
		// 将选择的图片等比例压缩后缓存到存储卡根目录，并返回图片文件
		File file = CropUtil.makeTempFile(mActivity, originalUri, cachePath, nRoate);
		if (file != null && file.exists()) {
			// 调用CropImage类对图片进行剪切
			Intent intent = new Intent(mActivity, CropImageActivity.class);
			Bundle extras = new Bundle();
			extras.putString("circleCrop", "true");
			extras.putInt("aspectX", 1);
			extras.putString("cachePath", cachePath);
			extras.putInt("aspectY", 1);
			intent.putExtra("outputX", 256);
			intent.putExtra("outputY", 256);
			intent.setDataAndType(Uri.fromFile(file), "image/*");
			intent.putExtras(extras);
			startActivityForResult(intent, IMAGE_REQUEST_CODE3);
			mActivity.overridePendingTransition(R.anim.slide_in_right,R.anim.silde_out_left);
		} else {
			messagesManager(Constants.MESSAGE_CROP_FAILED);
		}
	}

	private void makingMenu() {
		String serverversion = sp.getString(SharedPreferredKey.SERVER_VERSION, "2");
		mMenuList = new ArrayList<MenuItem>();

//		mMenuList.add(new MenuItem(0, 0, 0, "健康日记", 0, MenuItem.GROUP_TITLE));
//		mMenuList.add(new MenuItem(1, R.drawable.slidingmenu_1, R.drawable.slidingmenu_1_on, "运动", 0, MenuItem.NORMAL_ITEM));
		if (serverversion.equals("2")) {
//			mMenuList.add(new MenuItem(10, R.drawable.slidingmenu_9, R.drawable.slidingmenu_9_on, "轨迹", 0, MenuItem.NORMAL_ITEM));
			mMenuList.add(new MenuItem(13, R.drawable.home, R.drawable.home2, "首页", 0, MenuItem.NORMAL_ITEM));
			mMenuList.add(new MenuItem(3, R.drawable.knowledge, R.drawable.knowledge2, "生理数据", 0, MenuItem.NORMAL_ITEM));
			mMenuList.add(new MenuItem(12, R.drawable.alert, R.drawable.alert2, "用药提醒", 0, MenuItem.NORMAL_ITEM));
			mMenuList.add(new MenuItem(14, R.drawable.evaluate, R.drawable.evaluate2, "健康知识", 0, MenuItem.NORMAL_ITEM));
			mMenuList.add(new MenuItem(15, R.drawable.setting, R.drawable.setting, "评估", 0, MenuItem.NORMAL_ITEM));
//			mMenuList.add(new MenuItem(0, 0, 0, "运动圈", 0, MenuItem.GROUP_TITLE));
//			mMenuList.add(new MenuItem(6, R.drawable.slidingmenu_7, R.drawable.slidingmenu_7_on, "好友", 0, MenuItem.NORMAL_ITEM));
//			mMenuList.add(new MenuItem(9, R.drawable.slidingmenu_5, R.drawable.slidingmenu_5_on, "竞赛", 0, MenuItem.NORMAL_ITEM));
//			mMenuList.add(new MenuItem(7, R.drawable.slidingmenu_8, R.drawable.slidingmenu_8_on, "消息", 0, MenuItem.NORMAL_ITEM));
//			mMenuList.add(new MenuItem(11, R.drawable.slidingmenu_8, R.drawable.slidingmenu_8_on, "数据输入", 0, MenuItem.NORMAL_ITEM));
		}
//		mMenuList.add(new MenuItem(2, R.drawable.slidingmenu_2, R.drawable.slidingmenu_2_on, "历史", 0, MenuItem.NORMAL_ITEM));
//		mMenuList.add(new MenuItem(0, 0, 0, "企业空间", 0, MenuItem.GROUP_TITLE));
//		List<ClubData> clublist = clireqData.clublist;
//		if (clublist != null) {
//
//			if (clublist.size() == 1) {
//				mMenuList.add(new MenuItem(4, R.drawable.slidingmenu_3, R.drawable.slidingmenu_3_on, "排名", clublist.get(0).getClubid(), MenuItem.NORMAL_ITEM));
//				mMenuList.add(new MenuItem(5, R.drawable.slidingmenu_4, R.drawable.slidingmenu_4_on, "活动", clublist.get(0).getClubid(), MenuItem.NORMAL_ITEM));
//			} else if (clublist.size() > 1) {
//				mMenuList.add(new MenuItem(0, R.drawable.slidingmenu_3, R.drawable.slidingmenu_3_on, "排名", 0, MenuItem.NORMAL_ITEM));
//				for (int i = 0; i < clublist.size(); i++) {
//					mMenuList.add(new MenuItem(4, R.drawable.slidingmenu_3, R.drawable.slidingmenu_3_on, clublist.get(i).getClubname(), clublist.get(i).getClubid(), MenuItem.CHILD_ITEM));
//				}
//				mMenuList.add(new MenuItem(0, R.drawable.slidingmenu_4, R.drawable.slidingmenu_4_on, "活动", 0, MenuItem.NORMAL_ITEM));
//				for (int i = 0; i < clublist.size(); i++) {
//					mMenuList.add(new MenuItem(5, R.drawable.slidingmenu_4, R.drawable.slidingmenu_4_on, clublist.get(i).getClubname(), clublist.get(i).getClubid(), MenuItem.CHILD_ITEM));
//				}
//			}
//		}
		// mMenuList.add(new MenuItem(8, R.drawable.slidingmenu_5,
		// R.drawable.slidingmenu_5_on, "个人设置", 0, false));
	}

	private ClubListInfo clireqData;

	public void loadClubList() {
		mExitButton = findView(R.id.menu_bottom_item);
		clireqData = new ClubListInfo();
		if (getClubsFromSP()) {
			new Thread() {
				public void run() {
					String serverversion = sp.getString(SharedPreferredKey.SERVER_VERSION, "2");
					String phonenum = sp.getString(SharedPreferredKey.PHONENUM, null);
					String password = sp.getString(SharedPreferredKey.PASSWORD, null);
					if ("2".equals(serverversion)) {
						int suc = DataSyn.getInstance().getClubList(phonenum, password, clireqData);
						if (suc == 0) {
							handler.sendEmptyMessage(0);
						} else {
							handler.sendEmptyMessage(6);
						}
					} else {
						clireqData.clublist.add(new ClubData(0, "公司"));
						handler.sendEmptyMessage(0);
					}
				};
			}.start();
		} else {
			handler.sendEmptyMessage(0);
		}

	}

	/**
	 * 上传初始化
	 */
	private static final int UPLOAD_INIT_PROCESS = 4;
	/**
	 * 上传文件响应
	 */
	protected static final int UPLOAD_FILE_DONE = 2;
	/**
	 * 上传中
	 */
	private static final int UPLOAD_IN_PROCESS = 5;
	private static final int GET_AVATARSTREING_FROM_NET = 7;
	private MyAdapter mAdapter;
	private Handler handler = new Handler() {

		@Override
		public void dispatchMessage(Message msg) {
			switch (msg.what) {
			case 0:
				saveClubsToSP(clireqData.clublist);
				makeMenu();
				break;
			case TO_UPLOAD_FILE:
				toUploadFile();
				break;
			case UPLOAD_FILE_DONE:
				upLoadFile(msg);
				break;
			case 6:
				BaseToast("公司数据下载失败，请确认网络退出后重试。");
				makeMenu();
				break;
			case GET_AVATARSTREING_FROM_NET:
				menuAndName();
				break;
			}
		}

		private void upLoadFile(Message msg) {
			if (msg.arg1 == UploadUtil.UPLOAD_SUCCESS_CODE) {
				Gson gson = new Gson();
				try {
					UpdatePasswordInfo reqResult = gson.fromJson(msg.obj.toString(), UpdatePasswordInfo.class);

					if (reqResult.status.equals("SUCCESS")) {

						File newFile = new File(getFileImagePathDexed() + "_temp");
						File oldFile = new File(getFileImagePathDexed());

						try {
							Common.copyUseChannel(newFile, oldFile);
						} catch (IOException e1) {
							e1.printStackTrace();
						}

						Editor editorData = getSharedPreferences(SharedPreferredKey.SHARED_NAME, Context.MODE_PRIVATE).edit();
						String bitmapName = getFileImagePath().get(0).substring(getFileImagePath().get(0).lastIndexOf("/") + 1);
						Logger.d(TAG, "bitmapName == ---->" + bitmapName);
						editorData.putString(SharedPreferredKey.SHARED_NAME, bitmapName);
						editorData.commit();

						HashMap<String, Bitmap> sHardBitmapCache = ImageUtil.getInstance().getSHardBitmapCache();
						// sHardBitmapCache.remove(Encrypt.getMD5Str(bitmapName));
						sHardBitmapCache.clear();

						FileInputStream is;
						try {
							is = new FileInputStream(getFileImagePathDexed());
							Bitmap bitmap = BitmapFactory.decodeStream(is);
							if (bitmap != null)
								mImageViewAvater.setImageBitmap(bitmap);
						} catch (FileNotFoundException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						messagesManager(Constants.MESSAGE_AVARAR_SET_SUCCESS);

						// ((MainCenterActivity) mActivity).refleshMenuAvatar();

					} else {
						BaseToast(reqResult.reason);
					}
				} catch (JsonSyntaxException ex) {
					ex.printStackTrace();
					BaseToast("网络访问错误");
				}
			} else if (msg.arg1 == UploadUtil.UPLOAD_FILE_NOT_EXISTS_CODE) {
				BaseToast("上传的文件不存在");
			} else if (msg.arg1 == UploadUtil.UPLOAD_SERVER_ERROR_CODE) {
				BaseToast("服务器繁忙请稍等在试");
			}
		}
	};

	private void menuAndName() {
		TextView textviewName = findView(R.id.slidingmenu_name);
		String name = getMyName();
		if (!TextUtils.isEmpty(name)) {
			textviewName.setText(name);
			mImageViewAvater.setImageDrawable(mActivity.getResources().getDrawable((sp.getString(SharedPreferredKey.GENDER, "0").equals("0") ? MainCenterActivity.BASE_ATATAR[Encrypt.getIntFromName(name) + 7] : MainCenterActivity.BASE_ATATAR[Encrypt.getIntFromName(name)])));
		}
		String avatarName = getAvater();
		if (!TextUtils.isEmpty(avatarName)) {
			String url = DataSyn.avatarHttpURL + avatarName + ".jpg";
			getImageAsync(mImageViewAvater, url);
		}
	}

	public void makeMenu() {
		makingMenu();
		mListView = findView(android.R.id.list);
		mAdapter = new MyAdapter();
		mListView.setAdapter(mAdapter);
		mListView.setOnItemClickListener(MenuFragment.this);
	}

	private ProgressDialog mProgressDialog;
	private ImageView mImageViewAvater;

	private void toUploadFile() {
		if (mProgressDialog == null)
			mProgressDialog = new ProgressDialog(mActivity);
		// uploadImageResult.setText("正在上传中...");
		mProgressDialog.setMessage("正在上传文件...");
		mProgressDialog.show();
		String fileKey = "pic";
		UploadUtil uploadUtil = UploadUtil.getInstance();
		uploadUtil.setOnUploadProcessListener(this); // 设置监听器监听上传状态

		Map<String, String> params = new HashMap<String, String>();

		SharedPreferences info = getSharedPreferences(SharedPreferredKey.SHARED_NAME, Context.MODE_PRIVATE);
		String password = info.getString(SharedPreferredKey.PASSWORD, "");
		params.put("userid", getPhoneNum());
		params.put("psw", password);
		String requestURL = DataSyn.strHttpURL + "uploadAvatar";
		uploadUtil.uploadFile(getFileImagePathDexed() + "_temp", fileKey, requestURL, params);
	}

	private void saveClubsToSP(List<ClubData> clublist) {
		Editor edit = sp.edit();
		StringBuilder sb = new StringBuilder();
		for (ClubData clubData : clublist) {
			sb.append("#").append(clubData.clubname).append("@").append(clubData.clubid);
		}
		edit.putString("ClubList", sb.substring(1));
		edit.commit();
	}

	private boolean getClubsFromSP() {
		String clublist = sp.getString("ClubList", null);
		if (clublist == null || "".equals(clublist)) {
			return true;
		} else {
			String[] clubItem = clublist.split("#");
			clireqData.clublist = new ArrayList<ClubData>();
			for (int i = 0; i < clubItem.length; i++) {
				String[] club = clubItem[i].split("@");
				clireqData.clublist.add(new ClubData(club[1], club[0]));
			}
		}
		handler.sendEmptyMessage(2);
		return false;
	}

	private Drawable getImageAsync(ImageView holder, String url) {
		return getImageAsync(holder, url, null);
	}

	private Drawable getImageAsync(ImageView holder, String url, String tag) {
		return getImageAsync(holder, url, null, 0);
	}

	private Drawable getImageAsync(ImageView holder, String url, String tag, int mode) {
		return ImageUtil.getInstance().loadBitmap(holder, url, tag, mode);
	}

	@Override
	public void onUploadDone(int responseCode, String message) {
		mProgressDialog.dismiss();
		Message msg = Message.obtain();
		msg.what = UPLOAD_FILE_DONE;
		msg.arg1 = responseCode;
		msg.obj = message;
		handler.sendMessage(msg);
	}

	@Override
	public void onUploadProcess(int uploadSize) {
		Message msg = Message.obtain();
		msg.what = UPLOAD_IN_PROCESS;
		msg.arg1 = uploadSize;
		handler.sendMessage(msg);
	}

	@Override
	public void initUpload(int fileSize) {
		Message msg = Message.obtain();
		msg.what = UPLOAD_INIT_PROCESS;
		msg.arg1 = fileSize;
		handler.sendMessage(msg);
	}
}
