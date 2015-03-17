package cmcc.mhealth.activity;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.Random;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences.Editor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.webkit.URLUtil;
import android.widget.TextView;
import android.widget.Toast;
import cmcc.mhealth.R;
import cmcc.mhealth.common.SharedPreferredKey;
import cmcc.mhealth.view.DownLoadApkProgress;

public class PreLoadAPKUpdateProgressActivity extends Activity {
	int internet;// 0δ�����磬1��2����
	Boolean update = false;
	// Զ�������õ��ı���
	private String mCurrentFilePath = "";
//**	private String mCurrentTempFilePath = "";
	private String mStrURL = "";
	private String mFileEx = "";
	private String mFileNa = "";
	private String mUriPath = cmcc.mhealth.common.Config.UPDATE_SERVER + "iShang.apk";
	private DownLoadApkProgress mMyProgress = null;
	// private Intent mIntent = null;
	private int mProgressMax = 0; // �ļ��ĳ���
	private int mDownLoadFileSize; // �ܹ������˶����ֽ�
//**	private TextView mTextViewFileName;
	private TextView mTextViewFilelength;

	private Thread mThreadDownload;
	private boolean mIsInterruptted;


	Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			if (!Thread.currentThread().isInterrupted()) {
				switch (msg.what) {
				case 0:
					mMyProgress.setMax(mProgressMax);
					if (mProgressMax > 0) {
						double leng = mProgressMax / 1024 / 1024;
						mTextViewFilelength.setText("�ļ���С��" + leng + "M");
					}
				case 1:
					mMyProgress.setProgress(mDownLoadFileSize);
					break;
				case 2:
					Toast.makeText(PreLoadAPKUpdateProgressActivity.this, "�ļ��������", 1)
							.show();
					PreLoadAPKUpdateProgressActivity.this.finish();
					overridePendingTransition(R.anim.slide_in_left,R.anim.silde_out_right);
					break;
				}
			}
			super.handleMessage(msg);
		};
	};

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_progressupdate);
		PreLoadAPKUpdateProgressActivity.this.setFinishOnTouchOutside(false);
		findView();
		Random tmprand = new Random();
		mUriPath = getIntent().getStringExtra("downloadsite") + "?code="
				+ tmprand.nextInt();
		mStrURL = mUriPath;
		/* ȡ������װ����֮�ļ����� */
		// mFileEx = mStrURL.substring(mStrURL.lastIndexOf(".") + 1,
		// mStrURL.length())
		// .toLowerCase();
		mFileEx = "apk";
		mFileNa = mStrURL.substring(mStrURL.lastIndexOf("/") + 1,
				mStrURL.lastIndexOf("."));
		getFile(mUriPath);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			mIsInterruptted = true;

			this.finish();
			return true;
		}
		return false;
	}

	private void findView() {
		// 
		mMyProgress = (DownLoadApkProgress) findViewById(R.id.pgsBar);
//		**mTextViewFileName = (TextView) findViewById(R.id.tv_fileName);
		mTextViewFilelength = (TextView) findViewById(R.id.tv_fileLength);
	}

	/** ��������URL�ļ��Զ��庯�� */
	private void getFile(final String strPath) {
		try {
			if (strPath.equals(mCurrentFilePath)) {
				getDataSource(strPath);
			}
			mCurrentFilePath = strPath;
			// �������߳�
			Runnable r = new Runnable() {
				public void run() {
					try {
						getDataSource(strPath);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			};

			mThreadDownload = new Thread(r);
			mThreadDownload.start();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/** ȡ��Զ���ļ� */
	private void getDataSource(String strPath) throws Exception {
		if (!URLUtil.isNetworkUrl(strPath)) {
			// url����ȷ
		} else {
			/* ȡ��URL */
			URL myURL = new URL(strPath + "?x=" + Math.random());
			/* �������� */
			URLConnection conn = myURL.openConnection();
			conn.connect();
			mProgressMax = conn.getContentLength();
			sendMsg(0);
			if (mProgressMax == -1) {
				// �ļ�������41
			} else {
				/* InputStream �����ļ� */
				InputStream is = conn.getInputStream();
				if (is == null) {
					throw new RuntimeException("stream is null");
				}
				/* ������ʱ�ļ� */
				File myTempFile = new File(
						Environment.getExternalStorageDirectory(), mFileNa
								+ "." + mFileEx);
				// if (myTempFile.exists()) {
				// // �ļ���������
				// sendMsg(2);
				// openFile(myTempFile);
				// } else
				{
					/* ȡ���ݴ���·�� */
//					mCurrentTempFilePath = myTempFile.getAbsolutePath();
					/* ���ļ�д���ݴ��� */
					FileOutputStream fos = new FileOutputStream(myTempFile);
					byte buf[] = new byte[1024];

					mIsInterruptted = false;
					do {
						int numread = is.read(buf);
						if (numread <= 0) {
							break;
						}
						mDownLoadFileSize += numread;
						fos.write(buf, 0, numread);
						sendMsg(1); // ����
					} while (!mIsInterruptted);

					if (!mIsInterruptted) {
						sendMsg(2);// �������
						/* ���ļ����а�װ */
						openFile(myTempFile);
					}

					try {
						is.close();
						fos.close();
					} catch (Exception ex) {

					}
				}
			}
		}
	}

	private void sendMsg(int flag) {
		Message msg = new Message();
		msg.what = flag;
		mHandler.sendMessage(msg);
	}

	/* ���ֻ��ϴ��ļ���method */
	private void openFile(File f) {
		// ��װ��APK �޸����ô���
		Editor sharedata = getSharedPreferences(SharedPreferredKey.SHARED_NAME, Context.MODE_PRIVATE)
				.edit();
		sharedata.putInt("INSTALL", 1); // Ĭ��Ϊ0��ʾ��װ�󲻽����κβ��� Ϊ1����Ҫ����������ݿ����
		sharedata.commit();

		Intent intent = new Intent();
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.setAction(android.content.Intent.ACTION_VIEW);

		/* ����getMIMEType()��ȡ��MimeType */
		// String type = getMIMEType(f);
		/* ����intent��file��MimeType */
		intent.setDataAndType(Uri.fromFile(f),
				"application/vnd.android.package-archive");
		startActivity(intent);
		overridePendingTransition(R.anim.slide_in_right,R.anim.silde_out_left);
	}

	/* �ж��ļ�MimeType��method */
	// private String getMIMEType(File f) {
	// String type = "";
	// String fName = f.getName();
	// /* ȡ����չ�� */
	// String end = fName
	// .substring(fName.lastIndexOf(".") + 1, fName.length())
	// .toLowerCase();
	//
	// /* ����չ�������;���MimeType */
	// if (end.equals("m4a") || end.equals("mp3") || end.equals("mid")
	// || end.equals("xmf") || end.equals("ogg") || end.equals("wav")) {
	// type = "audio";
	// } else if (end.equals("3gp") || end.equals("mp4")) {
	// type = "video";
	// } else if (end.equals("jpg") || end.equals("gif") || end.equals("png")
	// || end.equals("jpeg") || end.equals("bmp")) {
	// type = "image";
	// } else if (end.equals("apk")) {
	// /* android.permission.INSTALL_PACKAGES */
	// type = "application/vnd.android.package-archive";
	// } else {
	// type = "*";
	// }
	// /* ����޷�ֱ�Ӵ򿪣�����������б���û�ѡ�� */
	// if (end.equals("apk")) {
	// } else {
	// type += "/*";
	// }
	// return type;
	// }

	/* �Զ���ɾ���ļ����� */
	@SuppressWarnings("unused")
	private void delFile(String strFileName) {
		File myFile = new File(strFileName);
		if (myFile.exists()) {
			myFile.delete();
		}
	}
}