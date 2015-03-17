package cmcc.mhealth;

import android.app.Application;
import cmcc.mhealth.errorhandler.CrashHandler;

public class MainApplication extends Application {   
	public static final String TAG = "cmcc";
    @Override  
    public void onCreate() {   
        super.onCreate();   
        CrashHandler crashHandler = CrashHandler.getInstance();   
        //ע��crashHandler   
        crashHandler.init(getApplicationContext());   
        //������ǰû���͵ı���(��ѡ)   
//        crashHandler.sendPreviousReportsToServer();   
    }   
       
}  
