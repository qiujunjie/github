/********************************************
 * �ļ���		��ScrollForeverTextView.java
 * �汾��Ϣ	��1.00
 * �����ˣ�Gaofei - �߷�
 * ����ʱ�䣺2013-5-25 ����3:57:13   
 * �޸��ˣ�Gaofei - �߷�
 * �޸�ʱ�䣺2013-5-25 ����3:57:13  
 * ��������	��
 * 
 * CopyRight(c) China Mobile 2013   
 * ��Ȩ����   All rights reserved
 *******************************************/
package cmcc.mhealth.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

public class ScrollForeverTextView extends TextView {

	public ScrollForeverTextView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	public ScrollForeverTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public ScrollForeverTextView(Context context, AttributeSet attrs,
			int defStyle) {
		super(context, attrs, defStyle);
	}

	@Override
	// ����
	public boolean isFocused() {
		return true;
	}
}
