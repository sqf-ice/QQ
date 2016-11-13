package com.tencent.hq.qq.ui.fragment;

import com.tencent.hq.qq.R;
import com.tencent.hq.qq.adapter.MessageRecentAdapter;
import com.tencent.hq.qq.ui.ChatActivity;
import com.tencent.hq.qq.ui.FragmentBase;
import com.tencent.hq.qq.ui.MainActivity;
import com.tencent.hq.qq.widget.CircleImageView;
import com.tencent.hq.qq.widget.ClearEditText;
import com.tencent.hq.qq.widget.HeaderLayout;
import com.tencent.hq.qq.widget.dialog.DialogTips;
import com.tencent.hq.qq.widget.residemenu.ResideMenu;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ListView;
import cn.bmob.im.bean.BmobChatUser;
import cn.bmob.im.bean.BmobRecent;
import cn.bmob.im.db.BmobDB;

/**
 * @author htq
 * @github:https://github.com/HuTianQi
 * @blog:http://blog.csdn.net/htq__
 */

public class RecentFragment extends FragmentBase implements OnItemClickListener,OnItemLongClickListener{

	ClearEditText mClearEditText;
	
	ListView listview;
	
	MessageRecentAdapter adapter;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_recent, container, false);
	}
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
		initView();
		initTitleBarAvatar();
		
	}
	
	private void initTitleBarAvatar()
	{
		HeaderLayout mHeaderLayout=(HeaderLayout)findViewById(R.id.common_actionbar);
		CircleImageView avatar=(CircleImageView)mHeaderLayout.findViewById(R.id.title_bar_avatar);
	    avatar.setVisibility(View.VISIBLE);
	    avatar.setOnClickListener(openMenu);
	}
	
private OnClickListener openMenu=new OnClickListener() {
		
		public void onClick(View v) {
			// TODO Auto-generated method stub
			if(((MainActivity)getActivity()).resideMenu.isOpened())
				((MainActivity)getActivity()).resideMenu.closeMenu();
			else
				((MainActivity)getActivity()).resideMenu.openMenu(ResideMenu.DIRECTION_LEFT);
		}
	};
	
	
	
	private void initView(){
		initTopBarForOnlyTitle("会话");
		listview = (ListView)findViewById(R.id.list);
		listview.setOnItemClickListener(this);
		listview.setOnItemLongClickListener(this);
		adapter = new MessageRecentAdapter(getActivity(), R.layout.item_conversation, BmobDB.create(getActivity()).queryRecents());
		listview.setAdapter(adapter);
		
		mClearEditText = (ClearEditText)findViewById(R.id.et_msg_search);
		mClearEditText.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				adapter.getFilter().filter(s);
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {

			}

			@Override
			public void afterTextChanged(Editable s) {
			}
		});
		
	}
	
	/** ɾ��Ự
	  * deleteRecent
	  * @param @param recent 
	  * @return void
	  * @throws
	  */
	private void deleteRecent(BmobRecent recent){
		adapter.remove(recent);
		BmobDB.create(getActivity()).deleteRecent(recent.getTargetid());
		BmobDB.create(getActivity()).deleteMessages(recent.getTargetid());
	}
	
	@Override
	public boolean onItemLongClick(AdapterView<?> arg0, View arg1, int position,
			long arg3) {
		// TODO Auto-generated method stub
		BmobRecent recent = adapter.getItem(position);
		showDeleteDialog(recent);
		return true;
	}
	
	public void showDeleteDialog(final BmobRecent recent) {
		DialogTips dialog = new DialogTips(getActivity(),recent.getUserName(),"删除会话", "确定",true,true);
	
		dialog.SetOnSuccessListener(new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialogInterface, int userId) {
				deleteRecent(recent);
			}
		});
		
		dialog.show();
		dialog = null;
	}
	
	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
		// TODO Auto-generated method stub
		BmobRecent recent = adapter.getItem(position);
		//����δ����Ϣ
		BmobDB.create(getActivity()).resetUnread(recent.getTargetid());
		//��װ�������
		BmobChatUser user = new BmobChatUser();
		user.setAvatar(recent.getAvatar());
		user.setNick(recent.getNick());
		user.setUsername(recent.getUserName());
		user.setObjectId(recent.getTargetid());
		Intent intent = new Intent(getActivity(), ChatActivity.class);
		intent.putExtra("user", user);
		startAnimActivity(intent);
	}
	
	private boolean hidden;
	@Override
	public void onHiddenChanged(boolean hidden) {
		super.onHiddenChanged(hidden);
		this.hidden = hidden;
		if(!hidden){
			refresh();
		}
	}
	
	public void refresh(){
		try {
			getActivity().runOnUiThread(new Runnable() {
				public void run() {
					adapter = new MessageRecentAdapter(getActivity(), R.layout.item_conversation, BmobDB.create(getActivity()).queryRecents());
					listview.setAdapter(adapter);
				}
			});
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void onResume() {
		
		super.onResume();
		
		if(!hidden){
			refresh();
		}
	}
	
}
