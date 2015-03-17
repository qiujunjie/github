package cmcc.mhealth.view;

import cmcc.mhealth.R;
import cmcc.mhealth.activity.PreLoadActivity;
import cmcc.mhealth.common.ConstantsBitmaps;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.widget.RemoteViews;

public class IshangWidget extends AppWidgetProvider {
	private CenterRollingBall mCenterRollingBall;

	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
		Bitmap rollingball = null;
		int step = 5000;
		try {
			ConstantsBitmaps.initRunPics(context);
			mCenterRollingBall = new CenterRollingBall(context);
			mCenterRollingBall.setPics(ConstantsBitmaps.mBitmapBgCenterRound, ConstantsBitmaps.mBitmapPointRound);
			mCenterRollingBall.setMaxScore(10000);
			mCenterRollingBall.setScore(step);
			mCenterRollingBall.measure(60, 60);
			mCenterRollingBall.layout(0, 0, 60, 60);
			rollingball = Bitmap.createBitmap(60, 60, Bitmap.Config.ARGB_8888);
			mCenterRollingBall.draw(new Canvas(rollingball));

			for (int i = 0; i < appWidgetIds.length; i++) {
				int appWidgetId = appWidgetIds[i];
				Intent intent = new Intent(context, PreLoadActivity.class);
				PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
				RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_main);
				views.setImageViewBitmap(R.id.widget_progress_center_rote1, rollingball);
				views.setTextViewText(R.id.widget_stepnum, step + "²½");
				views.setOnClickPendingIntent(R.id.widget_stepnum, pendingIntent);
				appWidgetManager.updateAppWidget(appWidgetId, views);
			}

		} finally {
			if (rollingball != null && !rollingball.isRecycled()) {
				rollingball.recycle();
				rollingball = null;
			}
		}

		super.onUpdate(context, appWidgetManager, appWidgetIds);
	}
}