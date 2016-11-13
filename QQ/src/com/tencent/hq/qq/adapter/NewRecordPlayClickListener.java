package com.tencent.hq.qq.adapter;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import com.tencent.hq.qq.R;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnPreparedListener;
import android.view.View;
import android.widget.ImageView;
import cn.bmob.im.BmobUserManager;
import cn.bmob.im.bean.BmobMsg;
import cn.bmob.im.config.BmobConfig;
import cn.bmob.im.util.BmobLog;
import cn.bmob.im.util.BmobUtils;



/**
 * ����¼���ļ�
 * 
 * @ClassName: NewRecordPlayClickListener
 * @Description: TODO
 * @author smile
 * @date 2014-7-3 ����11:05:06
 */
public class NewRecordPlayClickListener implements View.OnClickListener {

	BmobMsg message;
	ImageView iv_voice;
	private AnimationDrawable anim = null;
	Context context;
	String currentObjectId = "";
	MediaPlayer mediaPlayer = null;
	public static boolean isPlaying = false;
	public static NewRecordPlayClickListener currentPlayListener = null;
	static BmobMsg currentMsg = null;// �������������ͬ�����Ĳ���

	BmobUserManager userManager;

	public NewRecordPlayClickListener(Context context, BmobMsg msg,
			ImageView voice) {
		this.iv_voice = voice;
		this.message = msg;
		this.context = context;
		currentMsg = msg;
		currentPlayListener = this;
		currentObjectId = BmobUserManager.getInstance(context)
				.getCurrentUserObjectId();
		userManager = BmobUserManager.getInstance(context);
	}

	/**
	 * ��������
	 * 
	 * @Title: playVoice
	 * @Description: TODO
	 * @param @param filePath
	 * @param @param isUseSpeaker
	 * @return void
	 * @throws
	 */
	@SuppressWarnings("resource")
	public void startPlayRecord(String filePath, boolean isUseSpeaker) {
		if (!(new File(filePath).exists())) {
			return;
		}
		AudioManager audioManager = (AudioManager) context
				.getSystemService(Context.AUDIO_SERVICE);
		mediaPlayer = new MediaPlayer();
		if (isUseSpeaker) {
			audioManager.setMode(AudioManager.MODE_NORMAL);
			audioManager.setSpeakerphoneOn(true);
			mediaPlayer.setAudioStreamType(AudioManager.STREAM_RING);
		} else {
			audioManager.setSpeakerphoneOn(false);// �ر�������
			audioManager.setMode(AudioManager.MODE_IN_CALL);
			mediaPlayer.setAudioStreamType(AudioManager.STREAM_VOICE_CALL);
		}
		
//		while (true) {
//			try {
//				mediaPlayer.reset();
//				FileInputStream fis = new FileInputStream(new File(filePath));
//				mediaPlayer.setDataSource(fis.getFD());
//				mediaPlayer.prepare();
//				break;
//			} catch (IllegalArgumentException e) {
//			} catch (IllegalStateException e) {
//			} catch (IOException e) {
//			}
//		}
//		
//		isPlaying = true;
//		currentMsg = message;
//		mediaPlayer.start();
//		startRecordAnimation();
//		mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
//
//			@Override
//			public void onCompletion(MediaPlayer mp) {
//				// TODO Auto-generated method stub
//				stopPlayRecord();
//			}
//
//		});
//        currentPlayListener = this;

		try {
			mediaPlayer.reset();
			// ����ʹ�ô˷����ᱨ�?�Ŵ���:setDataSourceFD failed.: status=0x80000000
			// mediaPlayer.setDataSource(filePath);
			// ��˲��ô˷�ʽ��������ִ���
			FileInputStream fis = new FileInputStream(new File(filePath));
			mediaPlayer.setDataSource(fis.getFD());
			mediaPlayer.prepare();
			mediaPlayer.setOnPreparedListener(new OnPreparedListener() {

				@Override
				public void onPrepared(MediaPlayer arg0) {
					// TODO Auto-generated method stub
					isPlaying = true;
					currentMsg = message;
					arg0.start();
					startRecordAnimation();
				}
			});
			mediaPlayer
					.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {

						@Override
						public void onCompletion(MediaPlayer mp) {
							// TODO Auto-generated method stub
							stopPlayRecord();
						}

					});
			currentPlayListener = this;
			// isPlaying = true;
			// currentMsg = message;
			// mediaPlayer.start();
			// startRecordAnimation();
		} catch (Exception e) {
			BmobLog.i("���Ŵ���:" + e.getMessage());
		}
	}

	/**
	 * ֹͣ����
	 * 
	 * @Title: stopPlayRecord
	 * @Description: TODO
	 * @param
	 * @return void
	 * @throws
	 */
	public void stopPlayRecord() {
		stopRecordAnimation();
		if (mediaPlayer != null) {
			mediaPlayer.stop();
			mediaPlayer.release();
		}
		isPlaying = false;
	}

	/**
	 * �������Ŷ���
	 * 
	 * @Title: startRecordAnimation
	 * @Description: TODO
	 * @param
	 * @return void
	 * @throws
	 */
	private void startRecordAnimation() {
		if (message.getBelongId().equals(currentObjectId)) {
			iv_voice.setImageResource(R.anim.anim_chat_voice_right);
		} else {
			iv_voice.setImageResource(R.anim.anim_chat_voice_left);
		}
		anim = (AnimationDrawable) iv_voice.getDrawable();
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
	private void stopRecordAnimation() {
		if (message.getBelongId().equals(currentObjectId)) {
			iv_voice.setImageResource(R.drawable.voice_left3);
		} else {
			iv_voice.setImageResource(R.drawable.voice_right3);
		}
		if (anim != null) {
			anim.stop();
		}
	}

	@Override
	public void onClick(View arg0) {
		if (isPlaying) {
			currentPlayListener.stopPlayRecord();
			if (currentMsg != null
					&& currentMsg.hashCode() == message.hashCode()) {
				currentMsg = null;
				return;
			}
		}
		BmobLog.i("voice", "����¼�");
		if (message.getBelongId().equals(currentObjectId)) {// ������Լ����͵�������Ϣ���򲥷ű��ص�ַ
			String localPath = message.getContent().split("&")[0];
			startPlayRecord(localPath, true);
		} else {// ������յ�����Ϣ������Ҫ�����غ󲥷�
			String localPath = getDownLoadFilePath(message);
			BmobLog.i("voice", "ַ:" + localPath);
			startPlayRecord(localPath, true);
		}
	}

	public String getDownLoadFilePath(BmobMsg msg) {
		String accountDir = BmobUtils.string2MD5(userManager
				.getCurrentUserObjectId());
		File dir = new File(BmobConfig.BMOB_VOICE_DIR + File.separator
				+ accountDir + File.separator + msg.getBelongId());
		if (!dir.exists()) {
			dir.mkdirs();
		}
		// �ڵ�ǰ�û���Ŀ¼������¼���ļ�
		File audioFile = new File(dir.getAbsolutePath() + File.separator
				+ msg.getMsgTime() + ".amr");
		try {
			if (!audioFile.exists()) {
				audioFile.createNewFile();
			}
		} catch (IOException e) {
		}
		return audioFile.getAbsolutePath();
	}

}