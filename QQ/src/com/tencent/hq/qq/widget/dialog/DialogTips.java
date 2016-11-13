package com.tencent.hq.qq.widget.dialog;

import android.content.Context;

/**
 * ��ʾ�Ի�����һ��ȷ�ϡ�һ�����ذ�ť
 */
public class DialogTips extends DialogBase {
	boolean hasNegative;
	boolean hasTitle;
	/**
	 * ���캯��
	 * @param context
	 */
	public DialogTips(Context context, String title,String message,String buttonText,boolean hasNegative,boolean hasTitle) {
		super(context);
		super.setMessage(message);
		super.setNamePositiveButton(buttonText);
		this.hasNegative = hasNegative;
		this.hasTitle = hasTitle;
		super.setTitle(title);
	}
	
	/**����֪ͨ�ĶԻ�����ʽ
	 * @param context
	 * @param title
	 * @param message
	 * @param buttonText
	 */
	public DialogTips(Context context,String message,String buttonText) {
		super(context);
		super.setMessage(message);
		super.setNamePositiveButton(buttonText);
		this.hasNegative = false;
		this.hasTitle = true;
		super.setTitle("提示");
		super.setCancel(false);
	}
	
	public DialogTips(Context context, String message,String buttonText,String negetiveText,String title,boolean isCancel) {
		super(context);
		super.setMessage(message);
		super.setNamePositiveButton(buttonText);
		this.hasNegative=false;
		super.setNameNegativeButton(negetiveText);
		this.hasTitle = true;
		super.setTitle(title);
		super.setCancel(isCancel);
	}

	/**
	 * �����Ի���
	 */
	@Override
	protected void onBuilding() {
		super.setWidth(dip2px(mainContext, 300));
		if(hasNegative){
			super.setNameNegativeButton("取消");
		}
		if(!hasTitle){
			super.setHasTitle(false);
		}
	}

	public int dip2px(Context context,float dipValue){
		float scale=context.getResources().getDisplayMetrics().density;		
		return (int) (scale*dipValue+0.5f);		
	}
	
	@Override
	protected void onDismiss() { }

	@Override
	protected void OnClickNegativeButton() { 
		if(onCancelListener != null){
			onCancelListener.onClick(this, 0);
		}
	}

	/**
	 * ȷ�ϰ�ť������onSuccessListener��onClick
	 */
	@Override
	protected boolean OnClickPositiveButton() { 
		if(onSuccessListener != null){
			onSuccessListener.onClick(this, 1);
		}
		return true;
	}
}
