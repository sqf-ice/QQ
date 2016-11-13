package com.tencent.hq.qq.ui;


import java.io.IOException;

import com.tencent.hq.qq.CustomApplcation;
import com.tencent.hq.qq.MyMessageReceiver;
import com.tencent.hq.qq.R;
import com.tencent.hq.qq.ui.fragment.ContactFragment;
import com.tencent.hq.qq.ui.fragment.RecentFragment;
import com.tencent.hq.qq.ui.fragment.SettingsFragment;
import com.tencent.hq.qq.util.FileUtil;
import com.tencent.hq.qq.widget.CircleImageView;
import com.tencent.hq.qq.widget.HeaderLayout;
import com.tencent.hq.qq.widget.residemenu.ResideMenu;
import com.tencent.hq.qq.widget.residemenu.ResideMenuItem;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import cn.bmob.im.BmobChat;
import cn.bmob.im.BmobChatManager;
import cn.bmob.im.BmobNotifyManager;
import cn.bmob.im.bean.BmobInvitation;
import cn.bmob.im.bean.BmobMsg;
import cn.bmob.im.config.BmobConfig;
import cn.bmob.im.db.BmobDB;
import cn.bmob.im.inteface.EventListener;





/**
 * 
 * @ClassName: MainActivity
 * @Description: TODO
 * @author HuTianQi
 * @date 2016-10-30 
 * @github:https://github.com/HuTianQi
 * @blog:http://blog.csdn.net/htq__
 */
public class MainActivity extends ActivityBase implements EventListener,View.OnClickListener{

	private Button[] mTabs;
	private ContactFragment contactFragment;
	private RecentFragment recentFragment;
	private SettingsFragment settingFragment;
	private Fragment[] fragments;
	private int index;
	private int currentTabIndex;
	public ResideMenu resideMenu;
	private TextView signText;
		
	ImageView iv_recent_tips,iv_contact_tips;
	ResideMenuItem item[]=new ResideMenuItem[4];
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		initResideMenu();
		BmobChat.getInstance(this).startPollService(20);
		
		initNewMessageBroadCast();
		initTagMessageBroadCast();
		initView();
		
		initTab();
		
	}

	
	private void initResideMenu()
	{
		// attach to current activity;
        resideMenu = new ResideMenu(this);
        resideMenu.setUse3D(true);
        resideMenu.setBackground(R.drawable.left_layout_bg);
        resideMenu.attachToActivity(this);
        resideMenu.setScaleValue(0.55f);  
        resideMenu.setSwipeDirectionDisable(ResideMenu.DIRECTION_RIGHT);
        
        resideMenu.scrollViewLeftMenu.findViewById(R.id.mine_avatar).setOnClickListener(avatarListener);
        resideMenu.scrollViewLeftMenu.findViewById(R.id.mine_sign_relative).setOnClickListener(signOnClickListener);
        signText=(TextView) resideMenu.scrollViewLeftMenu.findViewById(R.id.sign_content);
        try {
			signText.setText(FileUtil.readSignFromFile());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
        // create menu items;
        String titles[] = { "我的钱包", "我的收藏", "我的相册", "关于我" };
        int icon[] = { R.drawable.qq_setting_qianbao, R.drawable.qq_setting_shoucang, R.drawable.qq_setting_xiangce, R.drawable.mine_avatar };

        for (int i = 0; i < titles.length; i++){
             item[i]= new ResideMenuItem(this, icon[i], titles[i]);
             item[i].setOnClickListener(this);
             resideMenu.addMenuItem(item[i],  ResideMenu.DIRECTION_LEFT); // or  ResideMenu.DIRECTION_RIGHT
        }
		
	}
	
	
	private OnClickListener avatarListener=new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			Intent intent =new Intent(MainActivity.this,SetMyInfoActivity.class);
			intent.putExtra("from", "me");
			startActivity(intent);
		}
	};
	
private OnClickListener signOnClickListener=new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			Intent intent =new Intent(MainActivity.this,EditSignActivity.class);
			startActivityForResult(intent, 0);
		}
	};
	
	private void initView(){
		mTabs = new Button[3];
		mTabs[0] = (Button) findViewById(R.id.btn_message);
		mTabs[1] = (Button) findViewById(R.id.btn_contract);
		mTabs[2] = (Button) findViewById(R.id.btn_set);
		iv_recent_tips = (ImageView)findViewById(R.id.iv_recent_tips);
		iv_contact_tips = (ImageView)findViewById(R.id.iv_contact_tips);

		mTabs[0].setSelected(true);
		
		
	}
	
	private void initTab(){
		contactFragment = new ContactFragment();
		recentFragment = new RecentFragment();
		settingFragment = new SettingsFragment();
		fragments = new Fragment[] {recentFragment, contactFragment, settingFragment };
		
		getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, recentFragment).commit();
		    
		//add(R.id.fragment_container, recentFragment).
			//add(R.id.fragment_container, contactFragment).hide(contactFragment).show(recentFragment).commit();
	}
	
	
	
	/**
	 * button����¼�
	 * @param view
	 */
	public void onTabSelect(View view) {
		switch (view.getId()) {
		case R.id.btn_message:
			index = 0;
			break;
		case R.id.btn_contract:
			index = 1;
			break;
		case R.id.btn_set:
			index = 2;
			break;
		}
		if (currentTabIndex != index) {
			FragmentTransaction trx = getSupportFragmentManager().beginTransaction();
			trx.replace(R.id.fragment_container, fragments[index]).commit();
			/*trx.hide(fragments[currentTabIndex]);
			if (!fragments[index].isAdded()) {
				trx.add(R.id.fragment_container, fragments[index]);
			}
			trx.show(fragments[index]).commit();*/
		}
		mTabs[currentTabIndex].setSelected(false);
	    mTabs[index].setSelected(true);
		currentTabIndex = index;
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		
		if(BmobDB.create(this).hasUnReadMsg()){
			iv_recent_tips.setVisibility(View.VISIBLE);
		}else{
			iv_recent_tips.setVisibility(View.GONE);
		}
		if(BmobDB.create(this).hasNewInvite()){
			iv_contact_tips.setVisibility(View.VISIBLE);
		}else{
			iv_contact_tips.setVisibility(View.GONE);
		}
		MyMessageReceiver.ehList.add(this);
		
		MyMessageReceiver.mNewNum=0;
		
	}
	
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		MyMessageReceiver.ehList.remove(this);
	}
	
	@Override
	public void onMessage(BmobMsg message) {
		// TODO Auto-generated method stub
		refreshNewMsg(message);
	}
	
	
	/** ˢ�½���
	  * @Title: refreshNewMsg
	  * @Description: TODO
	  * @param @param message 
	  * @return void
	  * @throws
	  */
	private void refreshNewMsg(BmobMsg message){
		// ������ʾ
		boolean isAllow = CustomApplcation.getInstance().getSpUtil().isAllowVoice();
		if(isAllow){
			CustomApplcation.getInstance().getMediaPlayer().start();
		}
		iv_recent_tips.setVisibility(View.VISIBLE);
		
		if(message!=null){
			BmobChatManager.getInstance(MainActivity.this).saveReceiveMessage(true,message);
		}
		if(currentTabIndex==0){
		
			if(recentFragment != null){
				recentFragment.refresh();
			}
		}
	}
	
	NewBroadcastReceiver  newReceiver;
	
	private void initNewMessageBroadCast(){
		
		newReceiver = new NewBroadcastReceiver();
		IntentFilter intentFilter = new IntentFilter(BmobConfig.BROADCAST_NEW_MESSAGE);
		
		intentFilter.setPriority(3);
		registerReceiver(newReceiver, intentFilter);
	}
	
	
	private class NewBroadcastReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			
			refreshNewMsg(null);
			
			abortBroadcast();
		}
	}
	
	TagBroadcastReceiver  userReceiver;
	
	private void initTagMessageBroadCast(){
		
		userReceiver = new TagBroadcastReceiver();
		IntentFilter intentFilter = new IntentFilter(BmobConfig.BROADCAST_ADD_USER_MESSAGE);
		
		intentFilter.setPriority(3);
		registerReceiver(userReceiver, intentFilter);
	}
	
	/**
	 * 
	 */
	private class TagBroadcastReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			BmobInvitation message = (BmobInvitation) intent.getSerializableExtra("invite");
			refreshInvite(message);
			
			abortBroadcast();
		}
	}
	
	@Override
	public void onNetChange(boolean isNetConnected) {
		// TODO Auto-generated method stub
		if(isNetConnected){
			ShowToast(R.string.network_tips);
		}
	}

	@Override
	public void onAddUser(BmobInvitation message) {
		// TODO Auto-generated method stub
		refreshInvite(message);
	}
	
	/** ˢ�º�������
	  * @Title: notifyAddUser
	  * @Description: TODO
	  * @param @param message 
	  * @return void
	  * @throws
	  */
	private void refreshInvite(BmobInvitation message){
		boolean isAllow = CustomApplcation.getInstance().getSpUtil().isAllowVoice();
		if(isAllow){
			CustomApplcation.getInstance().getMediaPlayer().start();
		}
		iv_contact_tips.setVisibility(View.VISIBLE);
		if(currentTabIndex==1){
			if(contactFragment != null){
				contactFragment.refresh();
			}
		}else{
			
			String tickerText = message.getFromname()+"������Ӻ���";
			boolean isAllowVibrate = CustomApplcation.getInstance().getSpUtil().isAllowVibrate();
			BmobNotifyManager.getInstance(this).showNotify(isAllow,isAllowVibrate,R.drawable.ic_launcher, tickerText, message.getFromname(), tickerText.toString(),NewFriendActivity.class);
		}
	}

	@Override
	public void onOffline() {
		// TODO Auto-generated method stub
		showOfflineDialog(this);
	}
	
	@Override
	public void onReaded(String conversionId, String msgTime) {
		// TODO Auto-generated method stub
	}
	
	
	private static long firstTime;
	
	
	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		if (firstTime + 2000 > System.currentTimeMillis()) {
			super.onBackPressed();
		} else {
			ShowToast("连续点击两次退出程序");
		}
		firstTime = System.currentTimeMillis();
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		try {
			unregisterReceiver(newReceiver);
		} catch (Exception e) {
		}
		try {
			unregisterReceiver(userReceiver);
		} catch (Exception e) {
		}
		//ȡ��ʱ������
//		BmobChat.getInstance(this).stopPollService();
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		 

		        if (v == item[0]){
		        	new AlertDialog.Builder(MainActivity.this).setTitle("我的资产").
					setIcon(R.drawable.mine_avatar).setMessage(
							"我的资产:$777777777777"
							+"\n"+ "您目前资产为7千亿美元，获得土豪勋章"+"\n"+"土豪我们做个朋友吧").show();
		           
		        }else if (v == item[1]){
		            
		        }else if (v == item[2]){
		        	Intent intent=new Intent();
		    		intent.setAction(Intent.ACTION_PICK);
		    		intent.setType("image/*");
		    		startActivity(intent);//ForResult(intent, 3);
		        }else if (v == item[3]){
		            
		        	new AlertDialog.Builder(MainActivity.this).setTitle("关于作者").
					setIcon(R.drawable.mine_avatar).setMessage(
							"name:胡琪 "+"\n"+"博客:http://blog.csdn.net/htq__"
							+"\n"+ "github:http://github.com/HuTianQi"+"\n"+"欢迎大家关注我的博客，follow我的github账号").show();
		        }

		       // resideMenu.closeMenu();
		    }
	
	@Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        return resideMenu.dispatchTouchEvent(ev);
    }
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if(requestCode==0&&resultCode==7)
		{
			Bundle bundle=data.getExtras();
			String str=bundle.getString("sign");
		//	Toast.makeText(getApplicationContext(), str, Toast.LENGTH_SHORT).show();
			signText.setText(str);
		}
	}
}
