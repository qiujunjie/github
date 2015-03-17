package cmcc.mhealth.test;

import android.test.AndroidTestCase;
import cmcc.mhealth.bean.GPSListInfo;
import cmcc.mhealth.bean.GpsInfoDetail;
import cmcc.mhealth.common.Constants;
import cmcc.mhealth.common.Logger;
import cmcc.mhealth.common.PreferencesUtils;
import cmcc.mhealth.common.SharedPreferredKey;
import cmcc.mhealth.db.MHealthProviderMetaData;

public class TestBase64 extends AndroidTestCase {
	@Override
	protected void setUp() throws Exception {
		Logger.e("TestBase64", "setUp()");
		super.setUp();
	}

	@Override
	protected void tearDown() throws Exception {
		Logger.e("TestBase64", "tearDown()");
		super.tearDown();
	}

}
