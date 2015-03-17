package cmcc.mhealth.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.EditText;

public class ControledFocusedEditText extends EditText {

	public ControledFocusedEditText(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public ControledFocusedEditText(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public ControledFocusedEditText(Context context) {
		super(context);
	}
	
	@Override
	public boolean isFocused() {
		return true;
	}

}
