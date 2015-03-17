package cmcc.mhealth.slidingcontrol;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import cmcc.mhealth.R;
import cmcc.mhealth.basic.BaseFragment;
import cmcc.mhealth.common.ShowProgressDialog;

public class WebViewFragment extends BaseFragment implements OnClickListener{
	private WebView mWebView;
	private String URL;
	
	
	public WebViewFragment(String url){
		this.URL = url;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_webview, container, false);
		view.setTag("inflated");
		super.onCreateView(inflater, (ViewGroup) view, savedInstanceState);
		return view;
	}

	@Override
	public void findViews() {
		mWebView = findView(R.id.fragment_webview);
		mImageButtonBack.setVisibility(View.VISIBLE);
		mImageButtonBack.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				showMenu();
			}
		});
		titleRight.setVisibility(View.VISIBLE);
		titleRight.setOnClickListener(this);
	}

	@Override
	public void clickListner() {
		
	}

	@Override
	public void loadLogic() {
		ShowProgressDialog.showProgressDialog("正在加载", getActivity());
		// 设置WebView属性，能够执行Javascript脚本
		mWebView.getSettings().setJavaScriptEnabled(true);
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
				ShowProgressDialog.dismiss();
				super.onPageFinished(view, url);
			}

		});
		mWebView.addJavascriptInterface(this, "demo");
		mWebView.requestFocus();
		// 加载需要显示的网页
		mTextViewTitle.setText("首页");
		System.out.println(URL);
		mWebView.loadUrl(URL);
	}

	@Override
	public void onClick(View v) {
		int id = v.getId();
		if(titleRight.getId() == id){
			BaseToast("add");
		}
	}

}
