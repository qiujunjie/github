package cmcc.mhealth.slidingcontrol;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class SampleWebViewFragment extends Fragment {
	private WebView mWebView;
	private boolean mIsWebViewAvailable;
	private String mUrl;

	
	public static final SampleWebViewFragment newInstance(String url) {
		SampleWebViewFragment rcf = new SampleWebViewFragment();
		Bundle data = new Bundle();
		data.putString("fragment_url", url);
		rcf.setArguments(data);
		return rcf;
	}

	/**
	 * Called to instantiate the view. Creates and returns the WebView.
	 */
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		Bundle arguments = getArguments();
		if (arguments == null) {
			mUrl = "http://www.baidu.com";
		} else {
			mUrl = arguments.getString("fragment_url");
		}
		if (mWebView != null) {
			mWebView.destroy();
		}
		mWebView = new WebView(getActivity());
		mWebView.getSettings().setJavaScriptEnabled(true);
		mWebView.setWebViewClient(new WebViewClient() {
			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				view.loadUrl(url);
				return false;

			}
		});
		mIsWebViewAvailable = true;
		
		mWebView.loadUrl(mUrl);
		return mWebView;
	}

	/**
	 * Called when the fragment is visible to the user and actively running.
	 * Resumes the WebView.
	 */
	@Override
	public void onPause() {
		super.onPause();
		mWebView.onPause();
	}

	/**
	 * Called when the fragment is no longer resumed. Pauses the WebView.
	 */
	@Override
	public void onResume() {
		mWebView.onResume();
		super.onResume();
	}

	/**
	 * Called when the WebView has been detached from the fragment. The WebView
	 * is no longer available after this time.
	 */
	@Override
	public void onDestroyView() {
		mIsWebViewAvailable = false;
		mWebView.loadDataWithBaseURL(null, "", "text/html", "utf-8", null);
		super.onDestroyView();
	}

	/**
	 * Called when the fragment is no longer in use. Destroys the internal state
	 * of the WebView.
	 */
	@Override
	public void onDestroy() {
		if (mWebView != null) {
			mWebView.destroy();
			mWebView = null;
		}
		super.onDestroy();
	}

	/**
	 * Gets the WebView.
	 */
	public WebView getWebView() {
		return mIsWebViewAvailable ? mWebView : null;
	}

}
