package cmcc.mhealth.slidingcontrol;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import cmcc.mhealth.R;
import cmcc.mhealth.activity.PreLoadAPKUpdateProgressActivity;
import cmcc.mhealth.activity.PreLoadGuideActivity;
import cmcc.mhealth.activity.SettingAboutActivity;
import cmcc.mhealth.activity.SettingBirthdayActivity;
import cmcc.mhealth.activity.SettingDeviceBindActivity;
import cmcc.mhealth.activity.SettingHeightAvtivity;
import cmcc.mhealth.activity.SettingOldPwdActivity;
import cmcc.mhealth.activity.SettingTargetStepActivirty;
import cmcc.mhealth.activity.SettingTargetWeightActivity;
import cmcc.mhealth.activity.SettingWeightActivity;
import cmcc.mhealth.activity.WebViewActivity;
import cmcc.mhealth.activity.avatar.CropImageActivity;
import cmcc.mhealth.activity.avatar.CropUtil;
import cmcc.mhealth.basic.BaseFragment;
import cmcc.mhealth.bean.UpdatePasswordInfo;
import cmcc.mhealth.common.Common;
import cmcc.mhealth.common.Config;
import cmcc.mhealth.common.Constants;
import cmcc.mhealth.common.Encrypt;
import cmcc.mhealth.common.ImageUtil;
import cmcc.mhealth.common.Logger;
import cmcc.mhealth.common.SharedPreferredKey;
import cmcc.mhealth.common.UploadUtil;
import cmcc.mhealth.common.UploadUtil.OnUploadProcessListener;
import cmcc.mhealth.db.MHealthProviderMetaData;
import cmcc.mhealth.net.DataSyn;
import cmcc.mhealth.net.NetworkTool;
import cmcc.mhealth.net.UpdateSoftWareTools;
import cmcc.mhealth.view.RoundAngleImageView;
import cn.jpush.android.api.JPushInterface;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.umeng.fb.UMFeedbackService;
import com.umeng.fb.util.FeedBackListener;

public class SettingFragment extends BaseFragment implements OnClickListener, OnUploadProcessListener {

	private static final String ImgUrl = Environment.getExternalStorageDirectory() + "/ishang_image";// +MD5.getMD5(url));

	protected static final String TAG = "SettingActivity";
	private int mRelativeLayoutIds[] = { 
			R.id.setting_sport_target, R.id.setting_face, R.id.setting_aboutIS,R.id.setting_guid, 
			R.id.setting_update, R.id.setting_feedback, R.id.setting_tellfriends, R.id.setting_exit,
			R.id.rlayout_setting_updatepwd, R.id.test1, R.id.rlayout_setting_help, R.id.rlayout_setting_device,
			R.id.setting_height, R.id.setting_target_weight,R.id.setting_weight_1,R.id.setting_birthday};

	private TextView mTextViewSettingPhoneNum, mTextViewSettingSportTargetNum, mTextViewSettingNyGroup;
	private TextView mTextViewSettingWeight, mTextViewSettingStepLen;

	private TextView mTextViewVersion;
	private String mPhoneNum;
	private String mMembername;
	
	private RelativeLayout mWeightBar;
	private RelativeLayout mHeightBar;
	private RelativeLayout mBindDeviceBar;

	private TextView mTextViewNewPwd;

	private RoundAngleImageView mImageViewAvatar;
	// **private Bitmap mImageBitmap;

	private int mVerCode = 0;// Config.getVerCode(this);
	private String mVerName = "";// Config.getVerName(this);

	private ProgressDialog mProgressDialog;
	// TODO
	private static String requestURL;
	// **private static final String IMAGE_FILE_NAME = "faceImage.jpg";
	private String[] items = new String[] { "选择本地图片", "拍照" };

	/* 请求码 */
	private static final int IMAGE_REQUEST_CODE = 3000;
	private static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 3001;
	// **private static final int RESULT_REQUEST_CODE = 3002;

	private static final int PHOTO_CROP_REQUEST_CODE = 3003;

	// **private static final String PATH_IMAGE =
	// Environment.getExternalStorageDirectory() + "/iShang/";

	private static String mFileImagePath;
	private static String mFileImagePathDexed;

	private TextView mTextViewTitle;

	private ImageButton mBack;
	private int mFlag;

	private TextView mTextViewBirthday;

	private TextView mTextViewWeight;

	public SettingFragment(int flag) {
		mFlag = flag;
	}

	public SettingFragment() {
		mFlag = -1;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		Logger.e(TAG, getClass().getSimpleName() + "onCreateView");
		View view = inflater.inflate(R.layout.activity_setting, container, false);
		super.onCreateView(inflater, (ViewGroup) view, savedInstanceState);
		return view;
	}

	private void setValueFromShare() {
		// 版本号
		PackageManager packageManager = mActivity.getPackageManager();
		try {
			PackageInfo info = packageManager.getPackageInfo(mActivity.getPackageName(), 0);
			if (info != null && info.versionName != null) {
				mTextViewVersion.setText("当前版本:" + "  " + info.versionName);
			}
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}

		SharedPreferences sharedPreferences = getSharedPreferences(SharedPreferredKey.SHARED_NAME, 0);
		// String target = sharedPreferences.getString("TARGET", "10000");
		mTextViewSettingSportTargetNum.setText(sharedPreferences.getString(SharedPreferredKey.TARGET_STEP, "10000")+" （步）");
		String phone = sharedPreferences.getString(SharedPreferredKey.PHONENUM, "");
		mTextViewSettingPhoneNum.setText("手机号码：" + phone);
		mPhoneNum = phone;
		mTextViewSettingWeight.setText("体重(kg)：" + sharedPreferences.getString(SharedPreferredKey.WEIGHT, ""));
		mTextViewSettingStepLen.setText("步长(cm)：" + sharedPreferences.getString("STEPLENGTH", ""));
		mTextViewSettingNyGroup.setText("所 属 组：" + sharedPreferences.getString(SharedPreferredKey.GROUP_NAME, "")); // 设置所属组
		mHeightTextView.setText("身高："+sharedPreferences.getString(SharedPreferredKey.HEIGHT, "")+" （厘米）");
		mTargetTextView.setText("目标体重："+sharedPreferences.getString(SharedPreferredKey.TARGET_WEIGHT, "")+" （千克）");
		mTextViewBirthday.setText(getString(R.string.string_birthday)+sharedPreferences.getString(SharedPreferredKey.BIRTHDAY, ""));
		mTextViewWeight.setText("体重："+sharedPreferences.getString(SharedPreferredKey.WEIGHT, "")+" （千克）");
		
		mSex = sharedPreferences.getString(SharedPreferredKey.GENDER, null);// 性别
		String name = sp.getString(SharedPreferredKey.NAME, null);
		if (mSex != null && mSex.equals("0")) {
			int name2Int = Encrypt.getIntFromName(name == null ? "0" : name);
			mImageViewAvatar.setImageResource(MainCenterActivity.BASE_ATATAR[name2Int + 7]);
		} else {
			int name2Int = Encrypt.getIntFromName(name == null ? "0" : name);
			mImageViewAvatar.setImageResource(MainCenterActivity.BASE_ATATAR[name2Int]);
		}
		getAvatar();
	}

	/**
	 * 获取头像
	 */
	private void getAvatar() {
		mAvatarName = getSharedPreferences().getString(SharedPreferredKey.AVATAR, null);
		Logger.d(TAG, "mAvatarName == ------->" + mAvatarName);
		// 本地SD卡路径
		if (null != mAvatarName) {
			String DexedAvatarName = Encrypt.getMD5Str(mAvatarName);
			String mAvatarPath = Environment.getExternalStorageDirectory() + "/ishang_image/" + DexedAvatarName;
			Logger.i(TAG, "mAvatarPath==" + mAvatarPath);
			FileInputStream is;
			try {
				is = new FileInputStream(mAvatarPath);
				Bitmap bitmap = BitmapFactory.decodeStream(is);
				if (bitmap != null) {
					mImageViewAvatar.setImageBitmap(bitmap);
				}
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	@SuppressWarnings("deprecation")
	private void initView() {
		String serverversion = sp.getString(SharedPreferredKey.SERVER_VERSION, "2");
		mHeightTextView = findView(R.id.height_textview);
		mTargetTextView = findView(R.id.targetWeight_textview);
		mTextViewBirthday = findView(R.id.textview_birthday);
		mTextViewWeight= findView(R.id.textview_weight);

		mBack = findView(R.id.button_input_bg_back);
		mBack.setVisibility(View.VISIBLE);
		mBack.setBackgroundDrawable(mActivity.getResources().getDrawable(R.drawable.slidemenu_button));
		mBack.setOnClickListener(this);

		for (int i = 0; i < mRelativeLayoutIds.length; i++) {
			RelativeLayout rlList = findView(mRelativeLayoutIds[i]);
			rlList.setOnClickListener(this);
		}

		mTextViewTitle = findView(R.id.textView_title);
		mTextViewTitle.setText(R.string.main_setting);

		mTextViewSettingNyGroup = findView(R.id.setting_my_groupname);
		mTextViewVersion = findView(R.id.tv_version);
		mTextViewSettingSportTargetNum = findView(R.id.setting_sport_target_num);

		mTextViewSettingPhoneNum = findView(R.id.setting_phone_num);
		mTextViewSettingWeight = findView(R.id.setting_myweight);
		mWeightBar = findView(R.id.setting_target_weight);
		mTextViewSettingStepLen = findView(R.id.setting_mysteplong);
		mHeightBar = findView(R.id.setting_height);
		mBindDeviceBar = findView(R.id.rlayout_setting_device);
		if(!"2".equals(serverversion)){
			mWeightBar.setVisibility(View.GONE);
			mHeightBar.setVisibility(View.GONE);
			mBindDeviceBar.setVisibility(View.GONE);
		}

		mTextViewNewPwd = findView(R.id.setting_updatepwd);

		mImageViewAvatar = findView(R.id.imageview_avatar);

		mProgressDialog = new ProgressDialog(mActivity);
		if (mFlag == 0) {
			openDialogSetFaceImage();
		}
	}

	@Override
	public void onResume() {
		super.onResume();
		// UMFeedbackService.enableNewReplyNotification(this,
		// NotificationType.NotificationBar);
		// UMFeedbackService.enableNewReplyNotification(this,
		// NotificationType.AlertDialog);
		setValueFromShare();
	}

	/**
	 * 压缩图片并缓存到存储卡，startActivityForResult方式调用剪切程序
	 * 
	 * @param uri
	 *            图片uri
	 * @param cachePath
	 *            缓存路径
	 * @param isRoate
	 *            是否翻转
	 */
	private void cropPhoto(Uri uri, String cachePath, int nRoate) {
		// 将选择的图片等比例压缩后缓存到存储卡根目录，并返回图片文件
		File file = CropUtil.makeTempFile(mActivity, uri, cachePath, nRoate);
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
			startActivityForResult(intent, PHOTO_CROP_REQUEST_CODE);
			mActivity.overridePendingTransition(R.anim.slide_in_right,R.anim.silde_out_left);
		} else {
			messagesManager(Constants.MESSAGE_CROP_FAILED);
		}
	}

	public void openDialogSetFaceImage() {
		new AlertDialog.Builder(mActivity).setTitle("设置头像").setItems(items, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				switch (which) {
				case 0:
					// 从相册选择图片
					Intent intentFromGallery = new Intent();
					intentFromGallery.setType("image/*");
					intentFromGallery.setAction(Intent.ACTION_GET_CONTENT);
					startActivityForResult(intentFromGallery, IMAGE_REQUEST_CODE);
					mActivity.overridePendingTransition(R.anim.slide_in_right,R.anim.silde_out_left);
					break;
				case 1:
					Intent intentFromCapture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
					// 判断存储卡是否可以用，可用进行存储
					if (Common.existSDcard()) {
						File mediaStorageDir = new File(ImgUrl);
						if (!mediaStorageDir.exists()) {
							if (!mediaStorageDir.mkdirs()) {
								Logger.e(TAG, "failed to create directory");
								return;
							}
						}
						if (mAvatarName != null) {
							mFileImagePath = mediaStorageDir.getPath() + File.separator + mAvatarName;
							mFileImagePathDexed = mediaStorageDir.getPath() + File.separator
									+ Encrypt.getMD5Str(mAvatarName);
						} else {
							mFileImagePath = mediaStorageDir.getPath() + File.separator + mPhoneNum;
							mFileImagePathDexed = mediaStorageDir.getPath() + File.separator
									+ Encrypt.getMD5Str(mPhoneNum);
						}
						File mediaFile = new File(mFileImagePathDexed + "_temp");
						if (mediaFile.exists()) {
							mediaFile.delete();
						}

						intentFromCapture.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(mediaFile));
					}
					startActivityForResult(intentFromCapture, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
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

	@SuppressLint("NewApi")
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		// super.onActivityResult(requestCode, resultCode, data);
		ExifInterface exif;
		int exifOrientation;
		if (resultCode == Activity.RESULT_OK && requestCode == IMAGE_REQUEST_CODE) {
			// 本地图片上传
			Uri originalUri = data.getData(); // 获得图片的uri
			if (originalUri != null && Common.existSDcard()) {
				// File mediaStorageDir = new
				// File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
				// Constants.APP_NAME);
				File mediaStorageDir = new File(ImgUrl);
				if (!mediaStorageDir.exists()) {
					if (!mediaStorageDir.mkdirs()) {
						Logger.e(TAG, "failed to create directory");
						return;
					}
				}
				// String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss")
				// .format(new Date());
				if (mAvatarName != null) {
					String bitmapName = mAvatarName.substring(mAvatarName.lastIndexOf("/") + 1);
					mFileImagePath = mediaStorageDir.getPath() + File.separator + bitmapName;
					mFileImagePathDexed = mediaStorageDir.getPath() + File.separator + Encrypt.getMD5Str(bitmapName);
				} else {
					mFileImagePath = mediaStorageDir.getPath() + File.separator + mPhoneNum;
					mFileImagePathDexed = mediaStorageDir.getPath() + File.separator + Encrypt.getMD5Str(mPhoneNum);
					// mFileImagePath = mediaStorageDir.getPath() +
					// File.separator + mPhoneNum+".jpg";
				}
				// path 当你设置自己的路径时，需要判断这个文件目录是否存在
				// File file = new File(path);
				// if (!file.exists()) {
				// file.mkdirs();
				// }
				int nRoate = 0;
				try {
					exif = new ExifInterface(mFileImagePathDexed + "_temp");
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
				cropPhoto(originalUri, mFileImagePathDexed + "_temp", nRoate);

				// Intent intent = new Intent(this, CropImageActivity.class);
				// Bundle extras = new Bundle();
				// extras.putString("circleCrop", "true");
				// extras.putInt("aspectX", 1);
				// extras.putString("cachePath", mFileImagePath);
				// extras.putInt("aspectY", 1);
				// intent.putExtra("outputX", 64);
				// intent.putExtra("outputY", 64);
				// intent.setDataAndType(originalUri, "image/*");
				// intent.putExtras(extras);
				// startActivityForResult(intent, PHOTO_CROP_REQUEST_CODE);

			} else {
				messagesManager(Constants.MESSAGE_CROP_FAILED);
			}
		} else if (resultCode == Activity.RESULT_OK && requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
			// Bundle extras = intent.getExtras();
			// mImageBitmap = (Bitmap) extras.get("data");
			// if (mFileUri != null) {
			// String cachePath = PATH_IMAGE + IMAGE_FILE_NAME;
			// File file = new File(cachePath);
			// if (!file.exists()) {
			// file.mkdirs();
			// }
			// cachePath = mFileImagePath;
			// path 当你设置自己的路径时，需要判断这个文件目录是否存在
			// File file = new File(path);
			// if (!file.exists()) {
			// file.mkdirs();
			// }

			int nRoate = 0;
			try {
				exif = new ExifInterface(mFileImagePathDexed + "_temp");
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
			cropPhoto(Uri.fromFile(new File(mFileImagePathDexed + "_temp")), mFileImagePathDexed + "_temp", nRoate);
			// cropPhoto(Uri.fromFile(new File(mFileImagePath)), mFileImagePath,
			// true);
			// } else {
			// messagesManager(Constants.MESSAGE_CROP_FAILED);
			// }

		} else if (resultCode == Activity.RESULT_OK && requestCode == PHOTO_CROP_REQUEST_CODE) {
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

	@Override
	public void onClick(View v) {
		Intent intent = new Intent();
		SharedPreferences info = getSharedPreferences(SharedPreferredKey.SHARED_NAME, Context.MODE_PRIVATE);
		switch (v.getId()) {
		case R.id.button_input_bg_back:
			showMenu();
			break;
		case R.id.setting_face:
			openDialogSetFaceImage();
			break;
		case R.id.setting_sport_target:
			intent.setClass(mActivity, SettingTargetStepActivirty.class);
			startActivity(intent);
			break;
		case R.id.setting_height:
			intent.setClass(mActivity, SettingHeightAvtivity.class);
			startActivity(intent);
			break;
		case R.id.setting_target_weight:
			intent.setClass(mActivity, SettingTargetWeightActivity.class);
			startActivity(intent);
			break;
		case R.id.setting_aboutIS:
			intent.setClass(mActivity, SettingAboutActivity.class);
			startActivity(intent);
			break;
		case R.id.setting_guid:

			Editor infoEditor = getSharedPreferences(SharedPreferredKey.SHARED_NAME, Context.MODE_PRIVATE)
					.edit();
			infoEditor.putBoolean("BSHOWGUID", true);
			infoEditor.commit();
			intent.setClass(mActivity, PreLoadGuideActivity.class);
			startActivity(intent);
			break;
		case R.id.setting_exit:
			Editor sharedata = getSharedPreferences(SharedPreferredKey.SHARED_NAME, Context.MODE_PRIVATE).edit();
			sharedata.putBoolean("checkdRemember", false);
			sharedata.putBoolean("checkdAuto", false);
			sharedata.putString(SharedPreferredKey.SERVER_NAME, null);
			sharedata.putString("GROUP_UPDATE_VERSION", null);
			sharedata.putString("INPK_UPDATE_TIME_RACE", null);
			sharedata.remove("DLStarttime");
			sharedata.remove("ULStarttime");
			// sharedata.putInt("INSTALL", 0);
			clearDatabases();
			sharedata.commit();
			// sharedata.clear();
			JPushInterface.clearAllNotifications(mActivity);
			JPushInterface.stopPush(mActivity);
//			PedometorFragment.sendAllowToReceiver(mActivity, false);
            mActivity.finish();
//          System.exit(0);
			break;
		case R.id.setting_update:
			updateVersion();
			break;
		case R.id.rlayout_setting_updatepwd:
			int newPasswrod = info.getInt("NEW_PASSWORD", 0);
			if (newPasswrod != 1) {
				mTextViewNewPwd.setText(R.string.textview_update_password);
				Editor sharedata1 = getSharedPreferences(SharedPreferredKey.SHARED_NAME, Context.MODE_PRIVATE)
						.edit();
				sharedata1.putInt("NEW_PASSWORD", 1);
				sharedata1.commit();
			}

			intent.setClass(mActivity, SettingOldPwdActivity.class);
			startActivity(intent);
			break;
		case R.id.setting_feedback:
			feedback(info);
			break;
		case R.id.rlayout_setting_help:
			Logger.d(TAG, "load location html");
			intent.setClass(mActivity, WebViewActivity.class);
			intent.putExtra("UserInfo", "file:///android_asset/baidu.html");
			intent.putExtra("title", "帮助与支持");
			startActivity(intent);
			break;
		case R.id.rlayout_setting_device:
			intent.setClass(mActivity, SettingDeviceBindActivity.class);
			intent.putExtra("sampletitle", "绑定设备");
			// TODO
			intent.putExtra(SharedPreferredKey.PASSWORD, "123456");
			intent.putExtra(SharedPreferredKey.PHONENUM, mPhoneNum);
			startActivity(intent);
			break;
//		case R.id.setting_sport_other_buttons:
//			intent.setClass(mActivity, SettingDeviceBindActivity.class);
//			intent.putExtra("sampletitle", "写数据");
//			startActivity(intent);
//			break;
		case R.id.setting_weight_1:
			intent.setClass(mActivity, SettingWeightActivity.class);
			startActivity(intent);
			break;
		case R.id.setting_birthday:
			intent.setClass(mActivity, SettingBirthdayActivity.class);
			startActivity(intent);
			break;
		default:
			break;
		}
	}

	private void feedback(SharedPreferences info) {
		mPhoneNum = info.getString(SharedPreferredKey.PHONENUM, null); // 拿到电话号码
		mMembername = info.getString("MEMBER_NAME", null);
		UMFeedbackService.setGoBackButtonVisible();
		UMFeedbackService.openUmengFeedbackSDK(mActivity);
		FeedBackListener listener = new FeedBackListener() {
			// private EditText mNameText;
			@Override
			public void onSubmitFB(Activity activity) {
				EditText phoneText = (EditText) activity.findViewById(R.id.feedback_phone);
				EditText nameText = (EditText) activity.findViewById(R.id.feedback_name);
				Map<String, String> contactMap = new HashMap<String, String>();
				contactMap.put("phone", phoneText.getText().toString());
				UMFeedbackService.setContactMap(contactMap);
				Map<String, String> remarkMap = new HashMap<String, String>();
				remarkMap.put("name", nameText.getText().toString());
				UMFeedbackService.setRemarkMap(remarkMap);
			}

			@Override
			public void onResetFB(Activity activity, Map<String, String> contactMap, Map<String, String> remarkMap) {
				// FB initialize itself,load other attribute
				// from local storage and set them
				EditText phoneText = (EditText) activity.findViewById(R.id.feedback_phone);
				EditText nameText = (EditText) activity.findViewById(R.id.feedback_name);

				if (mPhoneNum == null || mMembername == null) {
					Logger.d(TAG, "个人信息提取失败");
					return;
				}
				nameText.setText(mMembername);
				phoneText.setText(mPhoneNum);
			}
		};
		UMFeedbackService.setFeedBackListener(listener);
	}

	private void updateVersion() {
		/* 更新判断 */
		new Thread() {
			public void run() {
				if (UpdateSoftWareTools.getServerVerCode()) {
					mVerCode = Config.getVerCode(mActivity);
					if (UpdateSoftWareTools.newVerCode > mVerCode) {
						mActivity.runOnUiThread(new Runnable() {

							@Override
							public void run() {
								updateAPK();
							}
						});
					} else {
						messagesManager(Constants.MESSAGE_UPDATED_VERSION);
					}
				} else {
					messagesManager(Constants.MESSAGE_SERVER_EXCEPTION);
				}
			};
		}.start();
	}

	// 清理一些个人数据库中的数据
	private void clearDatabases() {
		MHealthProviderMetaData mhp = MHealthProviderMetaData.GetMHealthProvider(mActivity);
		mhp.deleteMyFriend();
		mhp.MyRankDeleteData();
		mhp.deleteVitalSignValue();
		mhp.DeleteMyOldMsgs();
	}

	/* 更新apk */
	private void updateAPK() {
		StringBuffer sb = new StringBuffer();
		sb.append("当前版本:\t");
		sb.append(mVerName + "\n");
		sb.append("发现新版本:\t");
		sb.append(UpdateSoftWareTools.newVerName + "\n");
		sb.append("更新说明:\t");
		sb.append(UpdateSoftWareTools.newVerInfo + "\n");
		sb.append("是否更新?");

		// 更新
		new AlertDialog.Builder(mActivity).setTitle("更新提示").setMessage(sb.toString())
		// .setMessage("发现新版本，是否更新")
				.setPositiveButton("是", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialoginterface, int i) {
						// 0未无网络，1，2有网
						int internet = NetworkTool.getNetworkState(mActivity);
						if (internet != 0) {
							Intent intent = new Intent();
							intent.putExtra("downloadsite", UpdateSoftWareTools.download);
							intent.setClass(mActivity, PreLoadAPKUpdateProgressActivity.class);
							startActivity(intent);
						} else {
							BaseToast("没有网络！");
						}

					}
				}).setNegativeButton("否", new DialogInterface.OnClickListener() {

					public void onClick(DialogInterface dialog, int which) {
					}
				}).show();
	}

	/**
	 * 去上传文件
	 */
	protected static final int TO_UPLOAD_FILE = 1;
	/**
	 * 上传文件响应
	 */
	protected static final int UPLOAD_FILE_DONE = 2;
	/**
	 * 上传中
	 */
	private static final int UPLOAD_IN_PROCESS = 5;
	/**
	 * 上传初始化
	 */
	private static final int UPLOAD_INIT_PROCESS = 4;

	@Override
	public void onUploadProcess(int uploadSize) {
		// TODO Auto-generated method stub
		Message msg = Message.obtain();
		msg.what = UPLOAD_IN_PROCESS;
		msg.arg1 = uploadSize;
		handler.sendMessage(msg);
	}

	@Override
	public void initUpload(int fileSize) {
		// TODO Auto-generated method stub
		Message msg = Message.obtain();
		msg.what = UPLOAD_INIT_PROCESS;
		msg.arg1 = fileSize;
		handler.sendMessage(msg);
	}

	@Override
	public void onUploadDone(int responseCode, String message) {
		// TODO Auto-generated method stub
		mProgressDialog.dismiss();
		Message msg = Message.obtain();
		msg.what = UPLOAD_FILE_DONE;
		msg.arg1 = responseCode;
		msg.obj = message;
		handler.sendMessage(msg);
	}

	private void toUploadFile() {
		// uploadImageResult.setText("正在上传中...");
		mProgressDialog.setMessage("正在上传文件...");
		mProgressDialog.show();
		String fileKey = "pic";
		UploadUtil uploadUtil = UploadUtil.getInstance();
		uploadUtil.setOnUploadProcessListener(this); // 设置监听器监听上传状态

		Map<String, String> params = new HashMap<String, String>();

		SharedPreferences info = getSharedPreferences(SharedPreferredKey.SHARED_NAME, Context.MODE_PRIVATE);
		mPhoneNum = info.getString(SharedPreferredKey.PHONENUM, "");
		String password = info.getString(SharedPreferredKey.PASSWORD, "");
		params.put("userid", mPhoneNum);
		params.put("psw", password);
		requestURL = DataSyn.strHttpURL + "uploadAvatar";
		uploadUtil.uploadFile(mFileImagePathDexed + "_temp", fileKey, requestURL, params);
	}

	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case TO_UPLOAD_FILE:
				toUploadFile();
				break;
			case UPLOAD_INIT_PROCESS:
				// progressBar.setMax(msg.arg1);
				break;
			case UPLOAD_IN_PROCESS:
				// progressBar.setProgress(msg.arg1);
				break;
			case UPLOAD_FILE_DONE:
				// String result = "响应码：" + msg.arg1 + "\n响应信息：" + msg.obj +
				// "\n耗时：" + UploadUtil.getRequestTime() + "秒";
				// uploadImageResult.setText(result);
				if (msg.arg1 == UploadUtil.UPLOAD_SUCCESS_CODE) {
					Gson gson = new Gson();
					try {
						UpdatePasswordInfo reqResult = gson.fromJson(msg.obj.toString(), UpdatePasswordInfo.class);

						if (reqResult.status.equals("SUCCESS")) {

							File newFile = new File(mFileImagePathDexed + "_temp");
							File oldFile = new File(mFileImagePathDexed);

							try {
								Common.copyUseChannel(newFile, oldFile);
							} catch (IOException e1) {
								e1.printStackTrace();
							}

							Editor editorData = getSharedPreferences(SharedPreferredKey.SHARED_NAME,
									Context.MODE_PRIVATE).edit();
							String bitmapName = mFileImagePath.substring(mFileImagePath.lastIndexOf("/") + 1);
							Logger.d(TAG, "bitmapName == ---->" + bitmapName);
							editorData.putString(SharedPreferredKey.AVATAR, bitmapName);
							editorData.commit();

							HashMap<String, Bitmap> sHardBitmapCache = ImageUtil.getInstance().getSHardBitmapCache();
							// sHardBitmapCache.remove(Encrypt.getMD5Str(bitmapName));
							sHardBitmapCache.clear();
							FileInputStream is;
							try {
								is = new FileInputStream(mFileImagePathDexed);
								Bitmap bitmap = BitmapFactory.decodeStream(is);
								if (bitmap != null)
									mImageViewAvatar.setImageBitmap(bitmap);
							} catch (FileNotFoundException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							messagesManager(Constants.MESSAGE_AVARAR_SET_SUCCESS);

							((MainCenterActivity) mActivity).refleshMenuAvatar();

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

				break;
			default:
				break;
			}
			super.handleMessage(msg);
		}

	};

	private String mAvatarName;

	private String mSex;

	private LinearLayout mLinearLayoutHeight;

	private LinearLayout mLinearLayoutTargetWeight;

	private TextView mHeightTextView;

	private TextView mTargetTextView;

	@Override
	public void findViews() {
		initView();
	}

	@Override
	public void clickListner() {
		// TODO Auto-generated method stub

	}

	@Override
	public void loadLogic() {
		// TODO Auto-generated method stub

	}
}
