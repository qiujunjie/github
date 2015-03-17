package cmcc.mhealth.slidingcontrol;

import java.util.Date;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import cmcc.mhealth.R;
import cmcc.mhealth.basic.SampleFragment;
import cmcc.mhealth.common.Common;
import cmcc.mhealth.common.Config;

public class WritingDatasFragment extends SampleFragment {
	private EditText mEditText1;
	private EditText mEditText2;
	private EditText mEditText3;
	private EditText mEditText4;
	private Button mButton1;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.sample_fragment_stepcounter, container, false);
		view.setTag("inflated");
		super.onCreateView(inflater, (ViewGroup) view, savedInstanceState);
		return view;
	}

	// baseFragment初始化之后首先调用此方法
	@Override
	public void findViews() {
		super.findViews();
		mEditText1 = findView(R.id.sfs_editText1);
		mEditText2 = findView(R.id.sfs_editText2);
		mEditText3 = findView(R.id.sfs_editText3);
		mEditText4 = findView(R.id.sfs_editText4);
		mButton1 = findView(R.id.sfs_button1);
		mTextViewTitle.setText("<(|| - .-)>");
	}


	// baseFragment初始化之后其次调用此方法
	@Override
	public void clickListner() {
		super.clickListner();
		mButton1.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Common.wirteStringToSd(Config.DATAS_URL,Common.getDateFromLongToStr
						(Common.getDateFromLongToStr(new Date().getTime()))
						+ "," + mEditText1.getText().toString().trim()
						+ "," + mEditText2.getText().toString().trim()
						+ "," + mEditText3.getText().toString().trim()
						+ "," + mEditText4.getText().toString().trim());
				BaseToast("写入成功");
				mEditText1.setText("");
				mEditText2.setText("");
				mEditText3.setText("");
				mEditText4.setText("");
			}
		});
	}

	// baseFragment初始化之后最后调用此方法
	@Override
	public void loadLogic() {
		super.loadLogic();
	}

	@Override
	public void onResume() {
		super.onResume();

	}

	@Override
	public void onStop() {
		super.onStop();

	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
	}

}
