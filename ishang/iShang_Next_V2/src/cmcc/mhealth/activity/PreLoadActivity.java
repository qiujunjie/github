package cmcc.mhealth.activity;

import java.util.Map;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.WindowManager;
import cmcc.mhealth.R;
import cmcc.mhealth.basic.BaseActivity;
import cmcc.mhealth.common.Config;
import cmcc.mhealth.common.SharedPreferredKey;
import cmcc.mhealth.db.MHealthProviderMetaData;

public class PreLoadActivity extends BaseActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_loading);

        Editor editorShare = getSharedPreferences(SharedPreferredKey.SHARED_NAME, Context.MODE_PRIVATE).edit();
        SharedPreferences info = getSharedPreferences(SharedPreferredKey.SHARED_NAME, 0);
        int install = info.getInt("INSTALL", 0);
        int verCode = Config.getVerCode(this);
        editorShare.remove("mSelected");
        if (install != verCode) {
            editorShare.remove("selectedserver");
            editorShare.remove("checkdAuto");
            editorShare.remove("DLStarttime");
            editorShare.remove("ULStarttime");
            clearDatabases();
        }
        editorShare.commit();
		Intent intent = new Intent();
		intent.setClass(PreLoadActivity.this, PreLoadGuideActivity.class);
		startActivity(intent);
		this.finish();
		overridePendingTransition(R.anim.slide_in_right,R.anim.silde_out_left);
	}
	
	private void clearDatabases() {
		MHealthProviderMetaData mhp = MHealthProviderMetaData.GetMHealthProvider(PreLoadActivity.this);
		mhp.deleteMyFriend();
		mhp.MyRankDeleteData();
		mhp.deleteVitalSignValue();
	}
}
