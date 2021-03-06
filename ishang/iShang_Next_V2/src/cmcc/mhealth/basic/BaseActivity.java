package cmcc.mhealth.basic;

import org.achartengine.renderer.XYMultipleSeriesRenderer;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Color;
import android.graphics.Paint.Align;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.view.Display;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import cmcc.mhealth.R;
import cmcc.mhealth.common.Common;
import cmcc.mhealth.common.Logger;
import cmcc.mhealth.common.SharedPreferredKey;
import cmcc.mhealth.net.DataSyn;
import cmcc.mhealth.slidingcontrol.PedometorFragment;
import cn.jpush.android.api.JPushInterface;

import com.google.analytics.tracking.android.EasyTracker;
import com.umeng.fb.NotificationType;
import com.umeng.fb.UMFeedbackService;

public class BaseActivity extends FragmentActivity {

	private ProgressDialog mProgressDialog;
	protected AlertDialog mAlertDialogQuit;
	private String TAG = "BaseActivity";
	protected SharedPreferences sp;
	
	/**
	 * activity 后退键和title
	 * @param title 标题栏设置
	 * @param activity 当前的activity
	 */
	protected void BaseBackKey(String title,final Activity activity){
		TextView mTextViewTitle = findView(R.id.textView_title);
		mTextViewTitle.setText(title==null?"":title);
        
        ImageButton mImageButtonBack = findView(R.id.button_input_bg_back);
		mImageButtonBack.setBackgroundResource(R.drawable.my_button_back);
		mImageButtonBack.setVisibility(View.VISIBLE);
		mImageButtonBack.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if(activity != null){
					activity.finish();
					overridePendingTransition(R.anim.slide_in_left,R.anim.silde_out_right);
				}
			}
		});
	}

	@Override
	protected void onStart() {
		super.onStart();
		// The activity is about to become visible.
		EasyTracker.getInstance().activityStart(this);
		sp = getSharedPreferences(SharedPreferredKey.SHARED_NAME, 0);
	}

	@Override
	protected void onStop() {
		super.onStop();
		// The activity is no longer visible (it is now "stopped")
		EasyTracker.getInstance().activityStop(this);
	}
	

	@Override
	protected void onResume() {
		/**
		 * 友盟服务端反馈信息监听
		 */
		Logger.d(TAG, "on resume!!");
		String selectedserver = sp.getString(SharedPreferredKey.SERVER_NAME, "");
		if (null != selectedserver && !"".equals(selectedserver)) {
			DataSyn.setStrHttpURL("http://" + selectedserver + "openClientApi.do?action=");
			DataSyn.setAvatarHttpURL("http://" + selectedserver + "UserAvatar/");
		}
		UMFeedbackService.enableNewReplyNotification(this, NotificationType.NotificationBar);
		super.onResume();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		EasyTracker.getInstance().setContext(this);
	}

	@SuppressWarnings("unchecked")
	public <T extends View> T findView(int id) {
		return (T) findViewById(id);
	}

	public XYMultipleSeriesRenderer getBarRenderer() {
		XYMultipleSeriesRenderer renderer = new XYMultipleSeriesRenderer();

		renderer.setPanEnabled(false, false);// 锁住上下 左右移动
		renderer.setZoomEnabled(false, false);

		renderer.setLabelsColor(Color.BLACK);// x y 坐标颜色
		renderer.setAxesColor(Color.BLACK); // x y 轴颜色
		// renderer.setShowCustomTextGrid(true)
		renderer.setMarginsColor(Color.argb(0, 0xff, 0, 0)); // 背景透明
		renderer.setMargins(new int[] { Common.dip2px(this, 10), Common.dip2px(this, 5), Common.dip2px(this, 10), Common.dip2px(this, 5) });

		renderer.setAxisTitleTextSize(Common.dip2px(this, 8));
		renderer.setChartTitleTextSize(Common.dip2px(this, 8));
		renderer.setLegendTextSize(Common.dip2px(this, 13));
		// renderer.setFitLegend(true);
		renderer.setLabelsTextSize(Common.dip2px(this, 13));

		renderer.setShowGridX(true);
		renderer.setGridColor(Color.GRAY);

		renderer.setYLabels(Common.dip2px(this, 3));
		// renderer.getYTextLabelLocations()
		// renderer.setXLabels(10);

		// renderer.setYLabelsAlign(Align.LEFT, 1);
		renderer.setYLabelsAlign(Align.LEFT); // ? y轴标尺向右
		renderer.setShowAxes(false);

		return renderer;
	}

	private Toast mToast = null;

	/**
	 * 自定义Toast
	 * 
	 * @param s为传入的字符串
	 */
	protected void BaseToast(String msg) {
		if (mToast == null) {
			mToast = Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT);
		} else {
			mToast.setText(msg);
		}
		mToast.show();
	}
	protected void BaseToast(String msg , int duration) {
		if (mToast == null) {
			mToast = Toast.makeText(getApplicationContext(), msg, duration);
		} else {
			mToast.setText(msg);
		}
		mToast.show();
	}

	/**
	 * 显示dialog
	 * 
	 * @param msg
	 * @param context
	 */
	protected void showProgressDialog(String msg, Context context) {
		if ((!isFinishing()) && (this.mProgressDialog == null)) {
			this.mProgressDialog = new ProgressDialog(context);
		}
		mProgressDialog.setMessage(msg);
		mProgressDialog.show();
	}

	/**
	 * 取消 dialog
	 */
	protected void dismiss() {
		if ((!isFinishing()) && (this.mProgressDialog != null)) {
			this.mProgressDialog.dismiss();
		}
	}

	/**
	 * 返回键退出
	 */
	protected void tipExit() {
		mAlertDialogQuit = new AlertDialog.Builder(this).setTitle("退出").setMessage("您确认要退出 " + this.getString(R.string.app_name) + " 吗？")
		// 设置自定义对话框的样式
				.setPositiveButton("确定", // 设置"确定"按钮
						new DialogInterface.OnClickListener() // 设置事件监听
						{
							public void onClick(DialogInterface dialog, int whichButton) {
								JPushInterface.clearAllNotifications(BaseActivity.this);
								JPushInterface.stopPush(BaseActivity.this);
//								PedometorFragment.sendAllowToReceiver(BaseActivity.this, false);
								BaseActivity.this.finish();
//								System.exit(0);
							}
						}).setNegativeButton("取消", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
					}
				}).create();// 创建
	}

	/**
	 * 返回屏幕的分辨率
	 */
	protected Display getDisplayParems() {
		WindowManager windowManager = getWindowManager();
		Display display = windowManager.getDefaultDisplay();
		return display;
	}

	/**
	 * messagesManager(子线程调用此方法可直接Toast而无需要handle发消息) what 消息code
	 */
	protected void messagesManager(int what) {
		Message message = Message.obtain();
		message.what = what;
		mHandler.sendMessage(message);
	}

	protected Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			BaseToast(String.valueOf(getString(msg.what)));
		};
	};

	protected void intentActivity(Activity from, Class<?> to,Bundle bundle,boolean isFinish) {
		Intent intent = new Intent();
		if(bundle != null)
			intent.putExtras(bundle);
		intent.setClass(from, to);
		startActivity(intent);
		if(isFinish){
			from.finish();
			overridePendingTransition(R.anim.slide_in_right,R.anim.silde_out_left);
		}
	}

	/**
	 * 
	 * restartApplication(重启应用)
	 */
	protected void restartApplication() {
		Intent i = getBaseContext().getPackageManager().getLaunchIntentForPackage(getBaseContext().getPackageName());
		i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivity(i);
		overridePendingTransition(R.anim.slide_in_right,R.anim.silde_out_left);
	}

	/**
	 * 
	 * onFocusChange(弹出输入法)
	 * 
	 * @param focusable
	 *            true or false
	 * @param view
	 *            (指定的输入框控件)
	 * @return void
	 * @Exception 异常对象
	 * @创建人：qjj
	 * @创建时间：2013-9-25 下午8:12:40
	 * @修改人：qjj
	 * @修改时间：2013-9-25 下午8:12:40
	 */
	public void onFocusChange(boolean focusable, final View view) {
		final boolean isFocus = focusable;
		(new Handler()).postDelayed(new Runnable() {

			@Override
			public void run() {
				InputMethodManager imm = (InputMethodManager) view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
				if (isFocus) {
					imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
				} else {
					imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
				}

			}
		}, 100);
	}

}
