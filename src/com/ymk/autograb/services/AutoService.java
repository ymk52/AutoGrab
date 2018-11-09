package com.ymk.autograb.services;

import java.util.Map;
import java.util.Set;

import com.ymk.autograb.QQ.AutoGrabQQRedPackets;
import com.ymk.autograb.wechat.AutoGrabWechatRedPackets;

import android.accessibilityservice.AccessibilityService;
import android.content.ComponentName;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
/**
 * ���������������
 * @author yangmingkun
 *
 */
public class AutoService extends AccessibilityService implements
		SharedPreferences.OnSharedPreferenceChangeListener {

	private static final String WECHAT_DETAILS_EN = "Details";
    private static final String WECHAT_DETAILS_CH = "�������";
    private static final String WECHAT_BETTER_LUCK_EN = "Better luck next time!";
    private static final String WECHAT_BETTER_LUCK_CH = "������";
    private static final String WECHAT_EXPIRES_CH = "�ѳ���24Сʱ";
    private static final String WECHAT_VIEW_SELF_CH = "�鿴���";
    private static final String WECHAT_VIEW_OTHERS_CH = "��ȡ���";
    private static final String WECHAT_NOTIFICATION_TIP = "[΢�ź��]";
    private static final String WECHAT_LUCKMONEY_RECEIVE_ACTIVITY = "LuckyMoneyReceiveUI";
    private static final String WECHAT_LUCKMONEY_DETAIL_ACTIVITY = "LuckyMoneyDetailUI";
    private static final String WECHAT_LUCKMONEY_GENERAL_ACTIVITY = "LauncherUI";
    private static final String WECHAT_LUCKMONEY_CHATTING_ACTIVITY = "ChattingUI";
    
    
    private static final String QQ_DETAILS_EN = "Details";
    private static final String QQ_DETAILS_CH = "�������";
    private static final String QQ_BETTER_LUCK_EN = "Better luck next time!";
    private static final String QQ_BETTER_LUCK_CH = "������";
    private static final String QQ_EXPIRES_CH = "�ѳ���24Сʱ";
    private static final String QQ_VIEW_SELF_CH = "�鿴���";
    private static final String QQ_VIEW_OTHERS_CH = "�����";
    private static final String QQ_NOTIFICATION_TIP = "[QQ���]";
    private static final String QQ_LUCKMONEY_RECEIVE_ACTIVITY = "LuckyMoneyReceiveUI";
    private static final String QQ_LUCKMONEY_DETAIL_ACTIVITY = "LuckyMoneyDetailUI";
    private static final String QQ_LUCKMONEY_GENERAL_ACTIVITY = "LauncherUI";
    private static final String QQ_LUCKMONEY_CHATTING_ACTIVITY = "ChattingUI";
    
    private final static String QQ_DEFAULT_CLICK_OPEN = "�����";
    private final static String QQ_HONG_BAO_PASSWORD = "������";
    private final static String QQ_CLICK_TO_PASTE_PASSWORD = "����������";
    
    private String currentActivityName = WECHAT_LUCKMONEY_GENERAL_ACTIVITY;

    private AccessibilityNodeInfo rootNodeInfo, mReceiveNode, mUnpackNode;
    private boolean mLuckyMoneyPicked, mLuckyMoneyReceived;
    private int mUnpackCount = 0;
    private boolean mMutex = false, mListMutex = false, mChatMutex = false;
    //private HongbaoSignature signature = new HongbaoSignature();
    //����Ϣ�������
    //private PowerUtil powerUtil;
    private SharedPreferences sharedPreferences;
    
	/**
	 * ������ص��¼�������ߴ���
	 */
	@Override
	public void onAccessibilityEvent(AccessibilityEvent event) {
		// TODO Auto-generated method stub
		//getRootInActiveWindow();
		if(event.getPackageName().equals("com.tencent.mobileqq"))
		{
			AutoGrabQQRedPackets.getInstance(this).GrabQQRedPackets(event);
		}
		
		if(event.getPackageName().equals("com.tencent.mm"))
		{
			AutoGrabWechatRedPackets.getInstance(this).GrabWechatRedPackets(event);
		}
	}

	@Override
	public void onInterrupt() {
		// TODO Auto-generated method stub

	}

	@Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
			String key) {
		// TODO Auto-generated method stub
		
	}
	
	private void setCurrentActivityName(AccessibilityEvent event) {
        if (event.getEventType() != AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) {
            return;
        }

        try {
            ComponentName componentName = new ComponentName(
                    event.getPackageName().toString(),
                    event.getClassName().toString()
            );

            getPackageManager().getActivityInfo(componentName, 0);
            currentActivityName = componentName.flattenToShortString();
        } catch (PackageManager.NameNotFoundException e) {
            currentActivityName = WECHAT_LUCKMONEY_GENERAL_ACTIVITY;
        }
    }

}
