package com.tencent.hq.qq.adapter;

import com.tencent.hq.qq.R;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.view.View;
import android.widget.ImageView;
import cn.bmob.im.BmobPlayManager;
import cn.bmob.im.BmobUserManager;
import cn.bmob.im.bean.BmobMsg;
import cn.bmob.im.inteface.OnPlayChangeListener;
import cn.bmob.im.util.BmobLog;


/**
 * ����¼���ļ�--���ã�����ֲ��Ŵ�λ����ʱ��δ���.
 * @ClassName: RecordPlayClickListener
 * @Description: TODO
 * @author smile
 * @date 2014-7-2 ����4:19:35
 */
public class RecordPlayClickListener implements View.OnClickListener {

	BmobMsg message;
	ImageView iv_voice;
	private AnimationDrawable anim = null;
	public static RecordPlayClickListener currentPlayListener = null;

	BmobPlayManager playMananger;
	Context context;

	String currentObjectId = "";

	static BmobMsg currentMsg = null;// �������������ͬ�����Ĳ���

	public RecordPlayClickListener(Context context, BmobMsg msg, ImageView voice) {
		this.iv_voice = voice;
		this.message = msg;
		this.context = context;
		currentMsg = msg;
		currentPlayListener = this;
		currentObjectId = BmobUserManager.getInstance(context)
				.getCurrentUserObjectId();
		playMananger = BmobPlayManager.getInstance(context);
		playMananger.setOnPlayChangeListener(new OnPlayChangeListener() {

			@Override
			public void onPlayStop() {
				// TODO Auto-generated method stub
				currentPlayListener.stopRecordAnimation();
			}

			@Override
			public void onPlayStart() {
				// TODO Auto-generated method stub
				currentPlayListener.startRecordAnimation();
			}
		});
	}

	/**
	 * �������Ŷ���
	 * @Title: startRecordAnimation
	 * @Description: TODO
	 * @param
	 * @return void
	 * @throws
	 */
	public void startRecordAnimation() {
		if (message.getBelongId().equals(currentObjectId)) {
			iv_voice.setImageResource(R.anim.anim_chat_voice_right);
		} else {
			iv_voice.setImageResource(R.anim.anim_chat_voice_left);
		}
		anim = (AnimationDrawable)iv_voice.getDrawable();
		anim.start();
	}

	/**
	 * ֹͣ���Ŷ���
	 * 
	 * @Title: stopRecordAnimation
	 * @Description: TODO
	 * @param
	 * @return void
	 * @throws
	 */
	public void stopRecordAnimation() {
		if (message.getBelongId().equals(currentObjectId)) {
			iv_voice.setImageResource(R.drawable.voice_left3);
		} else {
			iv_voice.setImageResource(R.drawable.voice_right3);
		}
		if(anim!=null){
			anim.stop();
		}
	}

	@Override
	public void onClick(View arg0) {
		if (playMananger.isPlaying()) {
			playMananger.stopPlayback();
			if (currentMsg != null
					&& currentMsg.hashCode() == message.hashCode()) {// �Ƿ���ͬ��������Ϣ
				currentMsg = null;
				return;
			}
		} else {
			String localPath = message.getContent().split("&")[0];
			BmobLog.i("voice", ":" + localPath);
			if (message.getBelongId().equals(currentObjectId)) {// ������Լ����͵�������Ϣ���򲥷ű��ص�ַ
				playMananger.playRecording(localPath, true);
			} else {// ������յ�����Ϣ������Ҫ�����غ󲥷�

			}
		}

	}

}