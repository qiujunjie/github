package cmcc.mhealth.common;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.SoftReference;
import java.lang.ref.WeakReference;
import java.net.URL;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.concurrent.ConcurrentHashMap;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Environment;
import android.widget.ImageView;

public class ImageUtil {
	protected static final String TAG = "AsyncBitmapLoader";
	private static final String mImgUrl = Environment.getExternalStorageDirectory() + "/ishang_image/";// +MD5.getMD5(url));
	/**
	 * �ڴ�ͼƬ�����û���
	 */
	private HashMap<String, SoftReference<Bitmap>> mImageCache = new HashMap<String, SoftReference<Bitmap>>();

	private static final int HARD_CACHE_CAPACITY = 10;

	// private static final int DELAY_BEFORE_PURGE = 10 * 1000; // in
	// milliseconds

	public HashMap<String, Bitmap> getSHardBitmapCache() {
		return sHardBitmapCache;
	}

	// Hard cache, with a fixed maximum capacity and a life duration
	private final HashMap<String, Bitmap> sHardBitmapCache = new LinkedHashMap<String, Bitmap>(HARD_CACHE_CAPACITY, 0.75f, true) {
		private static final long serialVersionUID = 1L;

		@Override
		protected boolean removeEldestEntry(LinkedHashMap.Entry<String, Bitmap> eldest) {
			if (size() > HARD_CACHE_CAPACITY) {
				// Entries push-out of hard reference cache are transferred to
				// soft reference cache
				// eldest.getKey() Ϊ����˳����ǰ��ĵ�ַ
				sSoftBitmapCache.put(eldest.getKey(), new SoftReference<Bitmap>(eldest.getValue()));
				return true;
			} else
				return false;
		}
	};
	static ImageUtil mAsyncBitmapLoader = null;

	public static synchronized ImageUtil getInstance() {
		if (mAsyncBitmapLoader == null) {
			mAsyncBitmapLoader = new ImageUtil();
		}
		return mAsyncBitmapLoader;
	}

	// Soft cache for bitmaps kicked out of hard cache
	private final static ConcurrentHashMap<String, SoftReference<Bitmap>> sSoftBitmapCache = new ConcurrentHashMap<String, SoftReference<Bitmap>>(HARD_CACHE_CAPACITY / 2);

	public Drawable loadBitmap(final ImageView imageView, String imageURL, String tag, int mode) {
		if (imageURL == null || imageView == null)
			return null;
		String[] modeurl = new String[] { imageURL, "" };
		// ��ͼģʽ��
		String imageURLBIG = "";
		if (mode == 1) {
			imageURLBIG = imageURL.substring(0, imageURL.lastIndexOf(".")) + "_big.jpg";
			modeurl[1] = imageURLBIG;
		}
		synchronized (sHardBitmapCache) {
			String md5Str = Encrypt.getMD5Str(getSubFileName(modeurl[mode]));
			Bitmap bitmap = sHardBitmapCache.get(md5Str);
			if (bitmap != null) {
				// Bitmap found in hard cache
				// Move element to first position, so that it is removed last
				sHardBitmapCache.remove(imageURL);
				sHardBitmapCache.put(imageURL, bitmap);
				imageView.setImageBitmap(bitmap);
				return imageView.getDrawable();
			}
		}

		// Then try the soft reference cache
		SoftReference<Bitmap> bitmapReference = sSoftBitmapCache.get(Encrypt.getMD5Str(getSubFileName(modeurl[mode])));
		if (bitmapReference != null) {
			final Bitmap bitmap = bitmapReference.get();
			if (bitmap != null) {
				// Bitmap found in soft cache
				imageView.setImageBitmap(bitmap);
				return imageView.getDrawable();
			} else {
				// Soft reference has been Garbage Collected
				sSoftBitmapCache.remove(imageURL);
			}
		}
		/**
		 * ����һ���Ա��ػ���Ĳ���
		 */
		if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
			// String bitmapName = imageURL.substring(imageURL.lastIndexOf("/")
			// + 1);
			int nEnd;
			String bitmapName;
			if ((nEnd = imageURL.lastIndexOf(".")) != -1) {
				bitmapName = imageURL.substring(imageURL.lastIndexOf("/") + 1, nEnd);
			} else {
				bitmapName = imageURL.substring(imageURL.lastIndexOf("/") + 1);
			}
			File cacheDir = new File(mImgUrl);
			File[] cacheFiles = cacheDir.listFiles();
			if (cacheFiles != null) {
				for (int i = 0; i < cacheFiles.length; i++) {
					try {
						// �ַ���������ƥ�䱾���ļ���
						// String name = Encrypt.encryptBASE64(bitmapName);
						String name = Encrypt.getMD5Str(bitmapName);
						if (name.equals(cacheFiles[i].getName())) {
							long fileSizes = Common.getFileSizes(cacheFiles[i]);
							Logger.i(TAG, "fileSizes==" + fileSizes + "//bitmapName==" + bitmapName);

							// ��ͼģʽʱ�ж��õ���ͼ�Ƿ��Ǵ�ͼ�������Сͼ��breakȥ���ش�ͼ��
							if (mode == 1)
								if (fileSizes < 5012)
									break;

							// ����ȡ,��BitmapFactory.decodeFileЧ�ʺò��ұ��ڼ���
							FileInputStream is = new FileInputStream(mImgUrl + name);
							Bitmap bitmap = BitmapFactory.decodeStream(is);
							if (bitmap == null) {
								// throw new
								// NullPointerException(TAG+"bitmap is null,BitmapFactory decodeFile erroe! "
								// + mImgUrl + bitmapName);//�쳣��Ϣ
								File errorfile = new File(mImgUrl + name);
								boolean delete = errorfile.delete();
								Logger.e(imageURL, (TAG + "bitmap is null,BitmapFactory decodeFile erroe! " + mImgUrl + bitmapName + "//delete success?==>" + delete));// �쳣��Ϣ)
								return null;
							}
							imageView.setImageBitmap(bitmap);
							if (!(sHardBitmapCache.containsKey(name)))
								sHardBitmapCache.put(name, bitmap);
							return null;
						}
					} catch (FileNotFoundException e) {
						Logger.e(TAG, e.getMessage() + (mImgUrl + bitmapName));
					} catch (Exception e) {
						// Logger.e(TAG, e.getMessage());
					}
				}
			}
		}
		if (imageURL.startsWith("http")) {
			if (cancelPotentialDownload(imageURL, imageView)) {
				BitmapDownloaderTask task = new BitmapDownloaderTask(imageView);
				task.execute(modeurl[mode], tag , mode + "");
			}
		}
		return null;
	}

	public Drawable loadBitmap(final ImageView imageView, final String imageURL, String tag) {
		return loadBitmap(imageView, imageURL, tag, 0);
	}

	public Drawable loadBitmap(final ImageView imageView, final String imageURL) {
		return loadBitmap(imageView, imageURL, null);
	}

	class BitmapDownloaderTask extends AsyncTask<String, Void, Bitmap> {
		private String url;
		private String tag;
		private int mode;
		private final WeakReference<ImageView> imageViewReference;

		public BitmapDownloaderTask(ImageView imageView) {
			imageViewReference = new WeakReference<ImageView>(imageView);
		}

		/**
		 * Actual download method.
		 */
		@Override
		protected Bitmap doInBackground(String... params) {
			url = params[0];
			tag = params[1];
			mode = Integer.parseInt(params[2]);
			return downloadBitmap(url, mode);
		}

		/**
		 * Once the image is downloaded, associates it to the imageView
		 */
		@Override
		protected void onPostExecute(Bitmap bitmap) {
			if (isCancelled()) {
				bitmap = null;
			}

			addBitmapToCache(url, bitmap);

			if (imageViewReference != null) {
				ImageView imageView = imageViewReference.get();
				// Change bitmap only if this process is still associated with
				// it
				// Or if we don't use any bitmap to task association
				// (NO_DOWNLOADED_DRAWABLE mode)
				if (imageView != null && bitmap != null) {
					if (tag != null) {
						if (!tag.equals(imageView.getTag().toString())) {
							return;
						}
					}
					imageView.setImageBitmap(bitmap);
				}
			}
		}
	}

	private void addBitmapToCache(String url, Bitmap bitmap) {
		if (bitmap != null) {
			synchronized (sHardBitmapCache) {
				sHardBitmapCache.put(url, bitmap);
			}
		}
	}

	private static boolean cancelPotentialDownload(String url, ImageView imageView) {
		BitmapDownloaderTask bitmapDownloaderTask = getBitmapDownloaderTask(imageView);

		if (bitmapDownloaderTask != null) {
			String bitmapUrl = bitmapDownloaderTask.url;
			if ((bitmapUrl == null) || (!bitmapUrl.equals(url))) {
				bitmapDownloaderTask.cancel(true);
			} else {
				// The same URL is already being downloaded.
				return false;
			}
		}
		return true;
	}

	private static BitmapDownloaderTask getBitmapDownloaderTask(ImageView imageView) {
		if (imageView != null) {
			Drawable drawable = imageView.getDrawable();
			if (drawable instanceof DownloadedDrawable) {
				DownloadedDrawable downloadedDrawable = (DownloadedDrawable) drawable;
				return downloadedDrawable.getBitmapDownloaderTask();
			}
		}
		return null;
	}

	static class DownloadedDrawable extends ColorDrawable {
		private final WeakReference<BitmapDownloaderTask> bitmapDownloaderTaskReference;

		public DownloadedDrawable(BitmapDownloaderTask bitmapDownloaderTask) {
			super(Color.BLACK);
			bitmapDownloaderTaskReference = new WeakReference<BitmapDownloaderTask>(bitmapDownloaderTask);
		}

		public BitmapDownloaderTask getBitmapDownloaderTask() {
			return bitmapDownloaderTaskReference.get();
		}
	}

	private Bitmap downloadBitmap(String url2, int mode) {
		try {
			// String name = url2.substring(url2.lastIndexOf("/") + 1,
			// url2.lastIndexOf("."));
			// url2 = url2.substring(0,url2.lastIndexOf("/") +
			// 1)+BASE64.encryptBASE64(name)+".jpg";
			long start = System.currentTimeMillis();
			URL m = new URL(url2);
			InputStream bitmapIs = (InputStream) m.getContent();
			Bitmap bitmap = BitmapFactory.decodeStream(bitmapIs);
			// if(bitmap ==null) return null;
			long end = System.currentTimeMillis();
			System.out.println(" time = " + (end - start));

			// ����仰���ڽ���ͷ����Сͷ��ͬ��
			if (mode == 1) {
				if (url2.contains("_big")) {
					url2 = url2.split("_big")[0] + ".jpg";
				}
			}

			mImageCache.put(url2, new SoftReference<Bitmap>(bitmap));
			// Message msg = handler.obtainMessage(0, bitmap);
			// handler.sendMessage(msg);

			if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
				File dir = new File(mImgUrl);
				if (!dir.exists()) {
					dir.mkdirs();
				}
				String MDName = Encrypt.getMD5Str(url2.substring(url2.lastIndexOf("/") + 1, url2.lastIndexOf(".")));
				File bitmapFile = new File(mImgUrl + MDName);
				if (null != bitmapFile && !bitmapFile.exists()) {
					try {
						bitmapFile.createNewFile();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				FileOutputStream fos;
				try {
					fos = new FileOutputStream(bitmapFile);
					// 30 ��ѹ���ʣ���ʾѹ��70%; �����ѹ����100����ʾѹ����Ϊ0
					if(bitmap == null)return bitmap;
					bitmap.compress(Bitmap.CompressFormat.JPEG, 80, fos);
					fos.close();
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			return bitmap;
		} catch (IOException e) {
			// Logger.e(TAG, e.printStackTrace());
			e.printStackTrace();
		}
		return null;
	}

	/***
	 * ͼƬ�����ŷ���
	 * 
	 * @param bgimage
	 *            ��ԴͼƬ��Դ
	 * @param newWidth
	 *            �����ź���
	 * @param newHeight
	 *            �����ź�߶�
	 * @return
	 */
	public static Bitmap zoomImage(Bitmap bgimage, double newWidth, double newHeight) {
		// ��ȡ���ͼƬ�Ŀ�͸�
		float width = bgimage.getWidth();
		float height = bgimage.getHeight();
		// ��������ͼƬ�õ�matrix����
		Matrix matrix = new Matrix();
		// ������������
		float scaleWidth = ((float) newWidth) / width;
		float scaleHeight = ((float) newHeight) / height;
		// ����ͼƬ����
		matrix.postScale(scaleWidth, scaleHeight);
		Bitmap bitmap = Bitmap.createBitmap(bgimage, 0, 0, (int) width, (int) height, matrix, true);
		return bitmap;
	}

	/**
	 * �ӱ����ļ���ɾ���ļ�
	 * 
	 * @param imagePath
	 */
	@SuppressWarnings("unused")
	private static void deleteImageFromLocal(String imagePath) {
		File file = new File(imagePath);
		if (file.exists()) {
			file.delete();
		}
	}

	/**
	 * 
	 * getSubFileName(��ȡ�ļ���)
	 * 
	 * @�����ˣ�Qiujunjie - �񿡽�
	 * @����ʱ�䣺2013-9-18 ����6:42:09
	 * @�޸��ˣ�Qiujunjie - �񿡽�
	 * @�޸�ʱ�䣺2013-9-18 ����6:42:09
	 */
	public String getSubFileName(String url) {
		String filename;
		int end;
		if ((end = url.lastIndexOf(".")) != -1) {
			filename = url.substring(url.lastIndexOf("/") + 1, end);
		} else {
			filename = url.substring(url.lastIndexOf("/") + 1);
		}
		return filename;
	}

	/**
	* Bitmap2Bytes(ͼƬ�����ֽ�����)  
	* @param ͼƬ
	* @return byte[]
	* @Exception �쳣����   
	* @�����ˣ�Qiujunjie - �񿡽�
	* @����ʱ�䣺2013-10-9 ����3:44:32   
	* @�޸��ˣ�Qiujunjie - �񿡽�
	* @�޸�ʱ�䣺2013-10-9 ����3:44:32
	 */
	public static byte[] Bitmap2Bytes(Bitmap bm) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		bm.compress(Bitmap.CompressFormat.PNG, 100, baos);
		return baos.toByteArray();
	}
	
	public static Bitmap toConformBitmap(Bitmap background, Bitmap foreground) {
        if( background == null ) {   
           return null;   
        }   
  
        int bgWidth = background.getWidth();   
        int bgHeight = background.getHeight();   
        //int fgWidth = foreground.getWidth();   
        //int fgHeight = foreground.getHeight();   
        //create the new blank bitmap ����һ���µĺ�SRC���ȿ��һ����λͼ    
        Bitmap newbmp = Bitmap.createBitmap(bgWidth, bgHeight+foreground.getHeight(), android.graphics.Bitmap.Config.ARGB_8888);  
        Canvas cv = new Canvas(newbmp);   
        //draw fg into   
        cv.drawBitmap(foreground, 0, 0, null);//�� 0��0���꿪ʼ����fg �����Դ�����λ�û���
        cv.drawBitmap(background, 0, foreground.getHeight(), null);//�� 0��0���꿪ʼ����bg   
        //save all clip   
        cv.save(Canvas.ALL_SAVE_FLAG);//����   
        //store   
        cv.restore();//�洢   
        return newbmp;   
   }
}
