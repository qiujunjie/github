package cmcc.mhealth.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.RelativeLayout;
import cmcc.mhealth.R;
import cmcc.mhealth.basic.BaseActivity;
import cmcc.mhealth.bean.RunType;

public class MapSelectTRunType extends BaseActivity implements OnClickListener {
	private int mRelativeLayoutIds[] = { R.id.rl_walk, R.id.rl_run, R.id.rl_cycle };

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_select_run_type);
		initView();
	}

	private void initView() {
		BaseBackKey("选择类型", this);
		for (int i = 0; i < mRelativeLayoutIds.length; i++) {
			RelativeLayout rlList = findView(mRelativeLayoutIds[i]);
			rlList.setOnClickListener(this);
		}
		loadlogic();
	}

	private void loadlogic() {
		
	}

	@Override
	public void onClick(View v) {
		Intent data = new Intent();
		Bundle extras = new Bundle();
		switch (v.getId()) {
		case R.id.rl_walk:
			extras.putParcelable("type", new RunType(R.drawable.walk, "步行",1));
			break;

		case R.id.rl_run:
			extras.putParcelable("type", new RunType(R.drawable.run, "跑步",2));
			break;
			
		case R.id.rl_cycle:
			extras.putParcelable("type", new RunType(R.drawable.cycle, "骑行",3));
			break;

		default:
			extras.putParcelable("type",null);
			break;
		}
		data.putExtras(extras);
		setResult(Activity.RESULT_OK, data);
		this.finish();
		overridePendingTransition(R.anim.slide_in_left,R.anim.silde_out_right);
	}
}
