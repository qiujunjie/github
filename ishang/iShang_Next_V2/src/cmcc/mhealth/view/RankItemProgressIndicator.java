package cmcc.mhealth.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

public class RankItemProgressIndicator extends TextView {

  public RankItemProgressIndicator(Context context) {
    this(context, null);
  }
  
  public RankItemProgressIndicator(Context context, AttributeSet attrs) {
    this(context, attrs, 0);
  }
  
  public RankItemProgressIndicator(Context context, AttributeSet attrs, int defStyle) {
    super(context, attrs, defStyle);
  }
}
