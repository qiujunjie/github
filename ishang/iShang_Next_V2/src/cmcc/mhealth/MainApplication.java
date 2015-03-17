package cmcc.mhealth;

import android.app.Application;
import cmcc.mhealth.errorhandler.CrashHandler;

public class MainApplication extends Application {   
	public static final String TAG = "cmcc";
    @Override  
    public void onCreate() {   
        super.onCreate();   
        CrashHandler crashHandler = CrashHandler.getInstance();   
        //注册crashHandler   
        crashHandler.init(getApplicationContext());   
        //发送以前没发送的报告(可选)   
//        crashHandler.sendPreviousReportsToServer();   
    }   
       
}  
