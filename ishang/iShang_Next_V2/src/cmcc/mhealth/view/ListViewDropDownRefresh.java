/**
 * listview下拉刷新控件
 */

package cmcc.mhealth.view;

import java.io.Serializable;
import java.util.Date;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import cmcc.mhealth.R;

@SuppressWarnings("serial")
public class ListViewDropDownRefresh extends ListView implements OnScrollListener, Serializable {
	private final static int RELEASE_To_REFRESH = 0;
	private final static int PULL_To_REFRESH = 1;
	// 正在刷新
	private final static int REFRESHING = 2;
	// 刷新完成
	private final static int DONE = 3;
	private final static int LOADING = 4;

	private final static int RATIO = 3;
	private LayoutInflater mInflater;
	private LinearLayout mHeadView;
	private TextView mHeadTextview;
	private TextView mLastUpdatedTextView;
	private ImageView mArrowImageView;
	private ProgressBar mProgressBar;

	private RotateAnimation mAnimation;
	private RotateAnimation mReverseAnimation;
	private boolean mIsRecored;
//	private int mHeadContentWidth;
	private int mHeadContentHeight;
	private int mStartY;
	private int mFirstItemIndex;
	private int mState;
	private boolean mIsBack;
	private OnRefreshListener mRefreshListener;
	private boolean mIsRefreshable;

	private LinearLayout mFoodView;
//	private TextView mTextView;
//	private ProgressBar mFootProgressBar;
//	private boolean mFlag;
	private int mHeightY;

	public ListViewDropDownRefresh(Context context) {
		super(context);
		init(context);
	}

	public ListViewDropDownRefresh(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	private void init(Context context) {
		setCacheColorHint(context.getResources().getColor(R.color.transparent));
		mInflater = LayoutInflater.from(context);
		mHeadView = (LinearLayout) mInflater.inflate(R.layout.rank_listviewhead,
				null);
		mArrowImageView = (ImageView) mHeadView
				.findViewById(R.id.head_arrowImageView);
		mArrowImageView.setMinimumWidth(70);
		mArrowImageView.setMinimumHeight(50);
		mProgressBar = (ProgressBar) mHeadView
				.findViewById(R.id.head_progressBar);
		mHeadTextview = (TextView) mHeadView
				.findViewById(R.id.head_tipsTextView);
		mLastUpdatedTextView = (TextView) mHeadView
				.findViewById(R.id.head_lastUpdatedTextView);

		mFoodView = (LinearLayout) mInflater.inflate(R.layout.rank_listviewfoot,
				null);
//**		mTextView = (TextView) mFoodView.findViewById(R.id.foot_tipsTextView);
//		mFootProgressBar = (ProgressBar) mFoodView
//				.findViewById(R.id.foot_progressBar);

		measureView(mHeadView, mFoodView);
		mHeadContentHeight = mHeadView.getMeasuredHeight();
//		mHeadContentWidth = mHeadView.getMeasuredWidth();
		mHeadView.setPadding(0, -1 * mHeadContentHeight, 0, 0);
		mHeadView.invalidate();
		addHeaderView(mHeadView, null, false);

		int mFootHeight = mFoodView.getMeasuredHeight();
//		**int mFootWidth = mFoodView.getMeasuredWidth();
		mFoodView.setPadding(0, 0, 0, -1 * mFootHeight);
		addFooterView(mFoodView);
		setOnScrollListener(this);

		mAnimation = new RotateAnimation(0, -180,
				RotateAnimation.RELATIVE_TO_SELF, 0.5f,
				RotateAnimation.RELATIVE_TO_SELF, 0.5f);
		mAnimation.setInterpolator(new LinearInterpolator());
		mAnimation.setDuration(250);
		mAnimation.setFillAfter(true);

		mReverseAnimation = new RotateAnimation(-180, 0,
				RotateAnimation.RELATIVE_TO_SELF, 0.5f,
				RotateAnimation.RELATIVE_TO_SELF, 0.5f);
		mReverseAnimation.setInterpolator(new LinearInterpolator());
		mReverseAnimation.setDuration(200);
		mReverseAnimation.setFillAfter(true);

		mState = DONE;
		mIsRefreshable = false;
	}

	public void onScroll(AbsListView arg0, int firstVisiableItem, int arg2,
			int arg3) {
		mFirstItemIndex = firstVisiableItem;
	}

	public void onScrollStateChanged(AbsListView arg0, int arg1) {
	}

	public boolean onTouchEvent(MotionEvent event) {
		if (mIsRefreshable) {
			switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN:
				if (mFirstItemIndex == 0 && !mIsRecored) {
					mIsRecored = true;
					mStartY = (int) event.getY();
				}
				break;
			case MotionEvent.ACTION_UP:
				if (mState != REFRESHING && mState != LOADING) {
					if (mState == DONE) {
					}
					if (mState == PULL_To_REFRESH) {
						mState = DONE;
						changeHeaderViewByState();
					}
					if (mState == RELEASE_To_REFRESH) {
						mState = REFRESHING;
						changeHeaderViewByState();
						onRefresh();
					}
				}
				mIsRecored = false;
				mIsBack = false;
				break;
			case MotionEvent.ACTION_MOVE:
				mHeightY = (int) event.getY();
				if (!mIsRecored && mFirstItemIndex == 0) {
					mIsRecored = true;
					mStartY = mHeightY;
				}
				if (mState != REFRESHING && mIsRecored && mState != LOADING) {
					if (mState == RELEASE_To_REFRESH) {
						setSelection(0);
						if (((mHeightY - mStartY) / RATIO < mHeadContentHeight)
								&& (mHeightY - mStartY) > 0) {
							mState = PULL_To_REFRESH;
							changeHeaderViewByState();
						} else if (mHeightY - mStartY <= 0) {
							mState = DONE;
							changeHeaderViewByState();
						}
					}
					if (mState == PULL_To_REFRESH) {
						setSelection(0);
						if ((mHeightY - mStartY) / RATIO >= mHeadContentHeight) {
							mState = RELEASE_To_REFRESH;
							mIsBack = true;
							changeHeaderViewByState();
						} else if (mHeightY - mStartY <= 0) {
							mState = DONE;
							changeHeaderViewByState();
						}
					}
					if (mState == DONE) {
						if (mHeightY - mStartY > 0) {
							mState = PULL_To_REFRESH;
							changeHeaderViewByState();
						}
					}
					if (mState == PULL_To_REFRESH) {
						mHeadView.setPadding(0, -1 * mHeadContentHeight
								+ (mHeightY - mStartY) / RATIO, 0, 0);
					}
					if (mState == RELEASE_To_REFRESH) {
						mHeadView.setPadding(0, (mHeightY - mStartY) / RATIO
								- mHeadContentHeight, 0, 0);
					}
				}
				break;
			}
		}
		return super.onTouchEvent(event);
	}

	private void changeHeaderViewByState() {
		mHeadTextview.setTextColor(getResources().getColor(R.color.gold_I));
		switch (mState) {
		case RELEASE_To_REFRESH:
			mArrowImageView.setVisibility(View.VISIBLE);
			mProgressBar.setVisibility(View.GONE);
			mHeadTextview.setVisibility(View.VISIBLE);
			mLastUpdatedTextView.setVisibility(View.VISIBLE);
			mArrowImageView.clearAnimation();
			mArrowImageView.startAnimation(mAnimation);
			mHeadTextview.setText("请释放 刷新");
			break;
		case PULL_To_REFRESH:
			mProgressBar.setVisibility(View.GONE);
			mHeadTextview.setVisibility(View.VISIBLE);
			mLastUpdatedTextView.setVisibility(View.VISIBLE);
			mArrowImageView.clearAnimation();
			mArrowImageView.setVisibility(View.VISIBLE);
			if (mIsBack) {
				mIsBack = false;
				mArrowImageView.clearAnimation();
				mArrowImageView.startAnimation(mReverseAnimation);
				mHeadTextview.setText("下拉刷新");
			} else {
				mHeadTextview.setText("下拉刷新");
			}
			break;
		case REFRESHING:
			mHeadView.setPadding(0, 0, 0, 0);
			mProgressBar.setVisibility(View.VISIBLE);
			mArrowImageView.clearAnimation();
			mArrowImageView.setVisibility(View.GONE);
			mHeadTextview.setText("正在加载中 ...");
			mLastUpdatedTextView.setVisibility(View.VISIBLE);
			break;
		case DONE:
			mHeadView.setPadding(0, -1 * mHeadContentHeight, 0, 0);
			mProgressBar.setVisibility(View.GONE);
			mArrowImageView.clearAnimation();
			mArrowImageView.setImageResource(R.drawable.arrow);
			mLastUpdatedTextView.setVisibility(View.VISIBLE);
			break;
		}
	}

//	public void animation(View v){
//		state = DONE;
//		Animation animation1 = new TranslateAnimation(0, 0, 0, -mHeightY);
//		animation1.setDuration(500);
//		animation1.setFillAfter(true);
//		v.startAnimation(animation1);
//		changeHeaderViewByState();
//	}
	
	public void setonRefreshListener(OnRefreshListener refreshListener) {
		this.mRefreshListener = refreshListener;
		mIsRefreshable = true;
	}

	public interface OnRefreshListener {
		public void onRefresh();
	}

	/**
	 *  是否刷新完毕
	 * @param mFlag true or false
	 */
	@SuppressWarnings("deprecation")
	public void onRefreshComplete(Boolean mFlag) {
		mState = DONE;
		if(mFlag){
			mHeadTextview.setText("已经加载完毕- DONE ");
			mLastUpdatedTextView.setText("已加载完成: " + new Date().toLocaleString());
		}
		changeHeaderViewByState();
	}
	
	/**
	 *  刷新数据
	 */
	public void refresh() {
		mState = REFRESHING;
		
		changeHeaderViewByState();
	}

	private void onRefresh() {
		if (mRefreshListener != null) {
			mRefreshListener.onRefresh();
		}
	}

	@SuppressWarnings("deprecation")
	private void measureView(View child, View child1) {
		ViewGroup.LayoutParams p = child.getLayoutParams();
		if (p == null) {
			p = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT,
					ViewGroup.LayoutParams.WRAP_CONTENT);
		}
		int childWidthSpec = ViewGroup.getChildMeasureSpec(0, 0 + 0, p.width);
		int lpHeight = p.height;
		int childHeightSpec;
		if (lpHeight > 0) {
			childHeightSpec = MeasureSpec.makeMeasureSpec(lpHeight,
					MeasureSpec.EXACTLY);
		} else {
			childHeightSpec = MeasureSpec.makeMeasureSpec(0,
					MeasureSpec.UNSPECIFIED);
		}
		child.measure(childWidthSpec, childHeightSpec);

//**		ViewGroup.LayoutParams p1 = child1.getLayoutParams();
//		if (p1 == null) {
//			p1 = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT,
//					ViewGroup.LayoutParams.WRAP_CONTENT);
//		}
		int childWidthSpec1 = ViewGroup.getChildMeasureSpec(0, 0 + 0, p.width);
		int lpHeight1 = p.height;
		int childHeightSpec1;
		if (lpHeight1 > 0) {
			childHeightSpec1 = MeasureSpec.makeMeasureSpec(lpHeight1,
					MeasureSpec.EXACTLY);
		} else {
			childHeightSpec1 = MeasureSpec.makeMeasureSpec(0,
					MeasureSpec.UNSPECIFIED);
		}
		child1.measure(childWidthSpec1, childHeightSpec1);
	}

	@SuppressWarnings("deprecation")
	public void setAdapter(BaseAdapter adapter) {
		mLastUpdatedTextView.setText("上次刷新时间：" + new Date().toLocaleString());
		super.setAdapter(adapter);
	}
}