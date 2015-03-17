package cmcc.mhealth.errorhandler;

import java.io.File;
import java.io.FilenameFilter;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.Thread.UncaughtExceptionHandler;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.TreeSet;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Build;
import android.util.Log;
import cmcc.mhealth.common.Common;
import cmcc.mhealth.common.Config;
import cmcc.mhealth.slidingcontrol.PedometorFragment;
import cn.jpush.android.api.JPushInterface;

public class CrashHandler implements UncaughtExceptionHandler {
	/** Debug Log tag */
	public static final String TAG = "CrashHandler";
	/**
	 * �Ƿ�����־���,��Debug״̬�¿���, ��Release״̬�¹ر�����ʾ��������
	 * */
	public static final boolean DEBUG = false;
	/** ϵͳĬ�ϵ�UncaughtException������ */
	private Thread.UncaughtExceptionHandler mDefaultHandler;
	/** CrashHandlerʵ�� */
	private static CrashHandler INSTANCE;
	/** �����Context���� */
	private Context mContext;

	/** ʹ��Properties�������豸����Ϣ�ʹ����ջ��Ϣ */
	private StringBuilder mDeviceCrashInfo = new StringBuilder();
	private static final String VERSION_NAME = "versionName";
	private static final String VERSION_CODE = "versionCode";
	private static final String STACK_TRACE = "STACK_TRACE";
	/** ���󱨸��ļ�����չ�� */
	private static final String CRASH_REPORTER_EXTENSION = ".cr";

	/** ��ֻ֤��һ��CrashHandlerʵ�� */
	private CrashHandler() {
	}

	/** ��ȡCrashHandlerʵ�� ,����ģʽ */
	public static CrashHandler getInstance() {
		if (INSTANCE == null) {
			INSTANCE = new CrashHandler();
		}
		return INSTANCE;
	}

	/**
	 * ��ʼ��,ע��Context����, ��ȡϵͳĬ�ϵ�UncaughtException������, ���ø�CrashHandlerΪ�����Ĭ�ϴ�����
	 * 
	 * @param ctx
	 */
	public void init(Context ctx) {
		mContext = ctx;
		mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler();
		Thread.setDefaultUncaughtExceptionHandler(this);
	}

	/**
	 * ��UncaughtException����ʱ��ת��ú���������
	 */
	@Override
	public void uncaughtException(Thread thread, Throwable ex) {
		handleException(ex);
		if (mDefaultHandler != null) {
			// ����û�û�д�������ϵͳĬ�ϵ��쳣������������
			mDefaultHandler.uncaughtException(thread, ex);
			JPushInterface.clearAllNotifications(mContext);
			JPushInterface.stopPush(mContext);
		} else {
			// Sleepһ����������
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				Log.e(TAG, "Error : ", e);
			}
			JPushInterface.clearAllNotifications(mContext);
			JPushInterface.stopPush(mContext);
//			PedometorFragment.sendAllowToReceiver(mContext, false);
			System.exit(0);
		}
	}

	/**
	 * �Զ��������,�ռ�������Ϣ ���ʹ��󱨸�Ȳ������ڴ����. �����߿��Ը����Լ���������Զ����쳣�����߼�
	 * 
	 * @param ex
	 * @return true:��������˸��쳣��Ϣ;���򷵻�false
	 */
	private boolean handleException(Throwable ex) {
		if (ex == null) {
			return true;
		}
		final String msg = ex.getLocalizedMessage();
		// �ռ��豸��Ϣ
		collectCrashDeviceInfo(mContext);
		// ������󱨸��ļ�
		saveCrashInfoToFile(ex);
		// ���ʹ��󱨸浽������
//		sendCrashReportsToServer(mContext);
		return true;
	}

	/**
	 * �ڳ�������ʱ��, ���Ե��øú�����������ǰû�з��͵ı���
	 */
	public void sendPreviousReportsToServer() {
		sendCrashReportsToServer(mContext);
	}

	/**
	 * �Ѵ��󱨸淢�͸�������,�����²����ĺ���ǰû���͵�.
	 * 
	 * @param ctx
	 */
	private void sendCrashReportsToServer(Context ctx) {
		String[] crFiles = getCrashReportFiles(ctx);
		if (crFiles != null && crFiles.length > 0) {
			TreeSet<String> sortedFiles = new TreeSet<String>();
			sortedFiles.addAll(Arrays.asList(crFiles));

			for (String fileName : sortedFiles) {
				File cr = new File(ctx.getFilesDir(), fileName);
				postReport(cr);
				cr.delete();// ɾ���ѷ��͵ı���
			}
		}
	}

	private void postReport(File file) {
		// TODO ʹ��HTTP Post ���ʹ��󱨸浽������
		// ���ﲻ������,�����߿��Ը���OPhoneSDN�ϵ������������
		// �̳����ύ���󱨸�
	}

	/**
	 * ��ȡ���󱨸��ļ���
	 * 
	 * @param ctx
	 * @return
	 */
	private String[] getCrashReportFiles(Context ctx) {
		File filesDir = ctx.getFilesDir();
		FilenameFilter filter = new FilenameFilter() {
			public boolean accept(File dir, String name) {
				return name.endsWith(CRASH_REPORTER_EXTENSION);
			}
		};
		return filesDir.list(filter);
	}

	/**
	 * ���������Ϣ���ļ���
	 * 
	 * @param ex
	 * @return
	 */
	private void saveCrashInfoToFile(Throwable ex) {
		Writer info = new StringWriter();
		PrintWriter printWriter = new PrintWriter(info);
		ex.printStackTrace(printWriter);

		Throwable cause = ex.getCause();
		while (cause != null) {
			cause.printStackTrace(printWriter);
			cause = cause.getCause();
		}

		String result = info.toString();
		printWriter.close();
		mDeviceCrashInfo.append("\r\n" + result);

		try {
			long timestamp = System.currentTimeMillis();
			Common.wirteStringToSdAfterCreateDirs(Config.ERRORLOG_URL, "log_" + timestamp + ".txt", mDeviceCrashInfo.toString());
		} catch (Exception e) {
			Log.e(TAG, "an error occured while writing report file...", e);
		}
	}

	/**
	 * �ռ�����������豸��Ϣ
	 * 
	 * @param ctx
	 */
	public void collectCrashDeviceInfo(Context ctx) {
		try {
			PackageManager pm = ctx.getPackageManager();
			PackageInfo pi = pm.getPackageInfo(ctx.getPackageName(), PackageManager.GET_ACTIVITIES);
			if (pi != null) {
				mDeviceCrashInfo.append("\r\n" + VERSION_NAME + " : " + pi.versionName == null ? "δ�趨" : pi.versionName);
				mDeviceCrashInfo.append("\r\n" + VERSION_CODE + " : " + pi.versionCode);
			}
		} catch (NameNotFoundException e) {
			Log.e(TAG, "Error while collect package info", e);
		}
		// ʹ�÷������ռ��豸��Ϣ.��Build���а��������豸��Ϣ,
		// ����: ϵͳ�汾��,�豸������ �Ȱ������Գ����������Ϣ
		// ������Ϣ��ο�����Ľ�ͼ
		Field[] fields = Build.class.getDeclaredFields();
		for (Field field : fields) {
			try {
				field.setAccessible(true);
				mDeviceCrashInfo.append("\r\n" + field.getName() + " : " + field.get(null));
				if (DEBUG) {
					Log.d(TAG, field.getName() + " : " + field.get(null));
				}
			} catch (Exception e) {
				Log.e(TAG, "Error while collect crash info", e);
			}

		}

	}

}