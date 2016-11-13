package com.tencent.hq.qq.ui;

import java.io.IOException;

import com.tencent.hq.qq.R;
import com.tencent.hq.qq.util.FileUtil;
import com.tencent.hq.qq.widget.HeaderLayout.onRightImageButtonClickListener;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class EditSignActivity  extends ActivityBase {

	
	EditText sign_edit;
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_edit_sign);
		findView();
		init();
	}
	
	private void findView() {
		// TODO Auto-generated method stub
		sign_edit=(EditText) findViewById(R.id.sign_editText);
	}

	void init()
	{
		try {
			sign_edit.setText(FileUtil.readSignFromFile());
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		initTopBarForBoth("修改个性签名", R.drawable.base_action_bar_true_bg_selector,
				new onRightImageButtonClickListener() {

					@Override
					public void onClick() {
						// TODO Auto-generated method stub
						String str=sign_edit.getText().toString();
						if (str.equals("")) {
							ShowToast("请填写个性签名!");
							return;
						}
						try {
							FileUtil.writeSignToFile(str);
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						Intent intent=getIntent();
						Bundle bundle=new Bundle();
						bundle.putString("sign", str);
						intent.putExtras(bundle);
						setResult(7, intent);
						finish();
						
					}
				});
		
	}
	
}
