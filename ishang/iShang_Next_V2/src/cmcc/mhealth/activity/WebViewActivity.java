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
		showProgressDialog("���ڼ��ؼ���ҳ��...");
		mWebView = (WebView) findViewById(R.id.webview_help);
		// ����WebView���ԣ��ܹ�ִ��Javascript�ű�
		mWebView.getSettings().setJavaScriptEnabled(true);
		// ����ҳ����Ӧ��Ļ���
//		WebSettings webSettings = mWebView.getSettings(); // webView:
		// ��WebView��ʵ��
		// webSettings.setLayoutAlgorithm(LayoutAlgorithm.SINGLE_COLUMN);
		// ���ÿ���֧������
		// webSettings.setSupportZoom(true);
//		webSettings.setBuiltInZoomControls(true); // ���ó������Ź���
		mWebView.setWebViewClient(new WebViewClient() {
			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				// ��ǰҳ�������ҳ
				if (url.indexOf("tel:") < 0) {// ҳ���������ֻᵼ�����ӵ绰
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
				// Toast.makeText(WebViewActivity.this, "�������", 0).show();
				dismiss();
				super.onPageFinished(view, url);
			}

		});
		mWebView.addJavascriptInterface(this, "demo");
		mWebView.requestFocus();
		// ������Ҫ��ʾ����ҳ
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
			// goBack()��ʾ����WebView����һҳ��
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
	 * js��Ϣ��ʾ���˷���
	 */
	public void aMessage(String i) {
		System.out.println("----------aMessage-------------------");
		Toast.makeText(WebViewActivity.this, i, 0).show();
	}
	
	public void clickOnAndroid(){
		Toast.makeText(WebViewActivity.this,"����..", 0).show();
	}

	/**
	 * js���˷�����ʾdialog
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
	 * js���˷�����ʾdialog
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
		// �������
//		mWebView.loadDataWithBaseURL(null, "", "text/html", "utf-8", null);
	}

	/**
	 * js����ɹ����˷���
	 */
	public void finsh() {
		setResult(RESULT_OK);
		WebViewActivity.this.finish();
		overridePendingTransition(R.anim.slide_in_left,R.anim.silde_out_right);
	}
}
