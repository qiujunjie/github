package cmcc.mhealth.activity;

import java.util.Random;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import cmcc.mhealth.R;
import cmcc.mhealth.common.SharedPreferredKey;

public class WebViewActivity extends Activity {
	private static final String TAG = "WebViewActivity";
	private TextView mTextViewTitle;
	private WebView mWebView;
	private ProgressDialog mProgressDialog = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.setting_help);
		initView();
		logic();
	}

	private void logic() {
		showProgressDialog("正在加载激活页面...");
		mWebView = (WebView) findViewById(R.id.webview_help);
		// 设置WebView属性，能够执行Javascript脚本
		mWebView.getSettings().setJavaScriptEnabled(true);
		// 让网页自适应屏幕宽度
//		WebSettings webSettings = mWebView.getSettings(); // webView:
		// 类WebView的实例
		// webSettings.setLayoutAlgorithm(LayoutAlgorithm.SINGLE_COLUMN);
		// 设置可以支持缩放
		// webSettings.setSupportZoom(true);
//		webSettings.setBuiltInZoomControls(true); // 设置出现缩放工具
		mWebView.setWebViewClient(new WebViewClient() {
			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				// 当前页面加载网页
				if (url.indexOf("tel:") < 0) {// 页面上有数字会导致连接电话
					view.loadUrl(url);
				}
				return true;
			}

			@Override
			public void onPageStarted(WebView view, String url, Bitmap favicon) {
				super.onPageStarted(view, url, favicon);
			}

			@Override
			public void onPageFinished(WebView view, String url) {
				// Toast.makeText(WebViewActivity.this, "加载完成", 0).show();
				dismiss();
				super.onPageFinished(view, url);
			}

		});
		mWebView.addJavascriptInterface(this, "demo");
		mWebView.requestFocus();
		// 加载需要显示的网页
		Intent intent = getIntent();
		String URL = intent.getStringExtra("UserInfo");
		String title = intent.getStringExtra("title");
		mTextViewTitle.setText(title);
		//String url = "http://"+selectedserver+"/account.do?myAction=PhoneActive&userPhone="+userInfo[0]+"&password="+userInfo[1]+"&" + new Random().nextInt();
		System.out.println(URL);
		mWebView.loadUrl(URL);
		String url = intent.getStringExtra("setting_help");
		if(!TextUtils.isEmpty(url)){
			mTextViewTitle.setText(R.string.textview_help);
			mWebView.loadUrl(url);
		}
		
		
		// else {
		// mWebView.loadUrl("file:///android_asset/baidu.html");
		// }
	}

	@Override
	protected void onResume() {
		super.onResume();
		mWebView.requestFocus();
	};

	Handler handler = new Handler();
	private ImageButton mBack;

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if ((keyCode == KeyEvent.KEYCODE_BACK)) {
			if (mWebView.canGoBack()){
				mWebView.goBack();
			}else{
				finish();
				overridePendingTransition(R.anim.slide_in_left,R.anim.silde_out_right);
			}
			Log.i("onkeydowm", mWebView.isFocusable() + "");
			// goBack()表示返回WebView的上一页面
			return true;
		}
		return false;
	}

	private void initView() {
		mTextViewTitle = (TextView) findViewById(R.id.textView_title);

		mBack = (ImageButton) findViewById(R.id.button_input_bg_back);
		mBack.setVisibility(View.VISIBLE);
		mBack.setBackgroundResource(R.drawable.my_button_back);
		mBack.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (mWebView.canGoBack()){
					mWebView.goBack();
				}else{
					WebViewActivity.this.finish();
					overridePendingTransition(R.anim.slide_in_left,R.anim.silde_out_right);
				}
				
			}
		});
	}

	/**
	 * js信息提示调此方法
	 */
	public void aMessage(String i) {
		System.out.println("----------aMessage-------------------");
		Toast.makeText(WebViewActivity.this, i, 0).show();
	}
	
	public void clickOnAndroid(){
		Toast.makeText(WebViewActivity.this,"激活..", 0).show();
	}

	/**
	 * js调此方法显示dialog
	 * 
	 * @param msg
	 */
	public void showProgressDialog(String msg) {
		if ((!isFinishing()) && (this.mProgressDialog == null)) {
			this.mProgressDialog = new ProgressDialog(this);
		}
		this.mProgressDialog.setMessage(msg);
		this.mProgressDialog.show();
	}

	/**
	 * js调此方法显示dialog
	 */
	protected void dismiss() {
		if ((!isFinishing()) && (this.mProgressDialog != null)) {
			this.mProgressDialog.dismiss();
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		Log.d("onDestroy", " --- onDestroy");
		// 清楚缓存
//		mWebView.loadDataWithBaseURL(null, "", "text/html", "utf-8", null);
	}

	/**
	 * js激活成功调此方法
	 */
	public void finsh() {
		setResult(RESULT_OK);
		WebViewActivity.this.finish();
		overridePendingTransition(R.anim.slide_in_left,R.anim.silde_out_right);
	}
}
