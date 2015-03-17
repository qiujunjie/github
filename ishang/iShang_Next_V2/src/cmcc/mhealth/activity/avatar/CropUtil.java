package cmcc.mhealth.activity.avatar;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;

/**
 * ѹ��ͼƬ�Ĺ���
 * 
 */
public class CropUtil {

    /**
     * �ر�IO��
     * 
     * @param in
     * @param out
     */
    public static void closeIO(InputStream in, OutputStream out) {
        try {
            if (in != null)
                in.close();
            if (out != null)
                out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * ����ͼƬ�����ش洢��
     * 
     * @param context
     *            ������
     * @param uri
     *            ͼƬ��Ӧ���uri
     * @param cacheFullPath
     *            ����ȫ·��
     * @param isRoate
     *            �Ƿ�ת
     * @return �����ļ�
     */
    @SuppressWarnings("deprecation")
	public static File makeTempFile(Context context, Uri uri, String cacheFullPath, int nRoate) {
        Bitmap photo = null;
        int dw = ((Activity) context).getWindowManager().getDefaultDisplay().getWidth();
        int dh = ((Activity) context).getWindowManager().getDefaultDisplay().getHeight();
        // ����Ļ�� ��һ��ĸ���ΪͼƬ��ʾ�����ߴ�
        try {
            BitmapFactory.Options factory = new BitmapFactory.Options();
            factory.inJustDecodeBounds = true; // ��Ϊtrueʱ �����ѯͼƬ��Ϊ ͼƬ���ط����ڴ�
            BitmapFactory.decodeStream(context.getContentResolver().openInputStream(uri), null, factory);
            int hRatio = (int) Math.ceil(factory.outHeight / (float) dh); // ͼƬ�Ǹ߶ȵļ���
            int wRatio = (int) Math.ceil(factory.outWidth / (float) dw); // ͼƬ�ǿ�ȵļ���
            // ��С�� 1/ratio�ĳߴ�� 1/ratio^2������
            if (hRatio > 1 || wRatio > 1) {
                if (hRatio > wRatio) {
                    factory.inSampleSize = hRatio;
                } else
                    factory.inSampleSize = wRatio;
            }
            factory.inJustDecodeBounds = false;
            photo = BitmapFactory.decodeStream(context.getContentResolver().openInputStream(uri), null, factory);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        if (photo != null) {
            File bFile = new File(cacheFullPath);
            FileOutputStream fos = null;
            try {
                fos = new FileOutputStream(bFile);
                byte[] bmpBytes = compressPhotoByte(photo, nRoate);
                fos.write(bmpBytes);
                fos.flush();
                if (bFile.exists() && bFile.length() > 0)
                    return bFile;
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                CropUtil.closeIO(null, fos);
            }
        }
        return null;
    }

    /**
     * ѹ��ͼƬ������ͼƬ�ֽ�����
     * 
     * @param b
     *            ����ѹ����ͼƬ
     * @param size
     *            :ָ����ѹ������������
     * @param isRoate
     *            :�Ƿ�ת
     * @return
     */
    public static byte[] compressPhotoByte(Bitmap sBmp, int nRoate) {
        int w = sBmp.getWidth();
        int h = sBmp.getHeight();

        Matrix matrix = new Matrix();
//        if (isRoate) {
//            if (w > h) {
//                matrix.postRotate(90);
//            }
//        }
                matrix.postRotate(nRoate );
        // ѹ��ͼƬ
        Bitmap newB = Bitmap.createBitmap(sBmp, 0, 0, w, h, matrix, false);
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        newB.compress(CompressFormat.JPEG, 100, bos);
        sBmp.recycle();
        return bos.toByteArray();
    }
}
