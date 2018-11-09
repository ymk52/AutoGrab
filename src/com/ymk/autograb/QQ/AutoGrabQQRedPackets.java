package com.ymk.autograb.QQ;

import java.util.ArrayList;
import java.util.List;

import android.accessibilityservice.AccessibilityService;
import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Context;
import android.content.pm.PackageManager;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

/**
 * �Զ���QQ���
 * @author yangmingkun
 *
 */
//�������������Զ��ر�
public class AutoGrabQQRedPackets {
	 private static String TAG ="AutoGrabQQRedPackets";
	 private static final String WECHAT_OPEN_EN = "Open";   
	 private static final String WECHAT_OPENED_EN = "You've opened"; 
	 private final static String QQ_DEFAULT_CLICK_OPEN = "�����";  
	 private final static String QQ_HONG_BAO_PASSWORD = "������";
	 private final static String QQ_RED_PACKETS_PASSWORD_OPENED = "�������Ѳ�";  
	 private final static String QQ_CLICK_TO_PASTE_PASSWORD = "����������";
	 private final static String QQ_RED_PACKETES_WALLET = "�Ѵ������";
	 private final static String QQ_CLOSE_WALLET = "�ر�";
	 private boolean mLuckyMoneyReceived;   
	 private String lastFetchedHongbaoId = null;   
	 private long lastFetchedTime = 0;   
	 private static final int MAX_CACHE_TOLERANCE = 5000;   
	 private AccessibilityNodeInfo rootNodeInfo;   
	 private List<AccessibilityNodeInfo> mReceiveNode; 
	 public static AutoGrabQQRedPackets instance = null;
	 private static Context mContext;
	 private String currentActivityName = null;
	 public static AutoGrabQQRedPackets getInstance(Context context){
		 if(instance == null)
		 {
			 instance = new  AutoGrabQQRedPackets();
		 }
		 mContext =context; 
		 return	instance;
	 }
	 //��QQ���
	 @SuppressLint("NewApi")
	public boolean GrabQQRedPackets(AccessibilityEvent event)
	 {
		 rootNodeInfo = event.getSource();
		 if(rootNodeInfo == null)
			 return false;
		 
		 setCurrentActivityName(event);
		 
		 mReceiveNode = null;
		 checkNodeInfo();
		 /* ����Ѿ����յ�������һ�û�д��� */
		 if(mLuckyMoneyReceived && mReceiveNode != null)
		 {
			 int size = mReceiveNode.size();
			 if(size > 0)
			 {
				 String id = getRedPacketsText(mReceiveNode.get(size - 1));
				 long now = System.currentTimeMillis();
				 if (shouldReturn(id, now - lastFetchedTime))
					 return false;
				 
				 lastFetchedHongbaoId = id;
			     lastFetchedTime = now;
			     AccessibilityNodeInfo cellNode = mReceiveNode.get(size - 1);
			     //�Ѿ�����
			     if (cellNode.getText().toString().equals(QQ_RED_PACKETS_PASSWORD_OPENED)) {
			         return false;
			        }
			     //���
			     cellNode.getParent().performAction(AccessibilityNodeInfo.ACTION_CLICK);
			     
			     if (cellNode.getText().toString().equals(QQ_HONG_BAO_PASSWORD)) {
			         AccessibilityNodeInfo rowNode = ((AccessibilityService) mContext).getRootInActiveWindow();
			      if (rowNode == null) {
			       Log.e(TAG, "noteInfo is��null");
			       return false;
			      } else {
			         recycle(rowNode);
			      }
			     }
			     if(cellNode.getText().toString().equals(QQ_RED_PACKETES_WALLET))
			     {
				    	 closeWallet(cellNode);
			     }
			     mLuckyMoneyReceived = false;          
			     
			 }
		 }
		 return true;
	 }
	 
	 public void recycle(AccessibilityNodeInfo info) {
		  if (info.getChildCount() == 0) {
		   /*���if����������ǣ�ƥ�䡰����������Ľڵ㣬���������ڵ㡱*/
		   if(info.getText()!=null&&info.getText().toString().equals(QQ_CLICK_TO_PASTE_PASSWORD)) {
		    info.getParent().performAction(AccessibilityNodeInfo.ACTION_CLICK);
		    }
		   /*���if����������ǣ�ƥ���ı��༭�����ķ��Ͱ�ť����������Ϳ���*/
		   if (info.getClassName().toString().equals("android.widget.Button") && info.getText().toString().equals("����")) {
		    info.performAction(AccessibilityNodeInfo.ACTION_CLICK);
		    }
		  } else {
		    for (int i = 0; i < info.getChildCount(); i++) {
		      if (info.getChild(i) != null) {
		       recycle(info.getChild(i));
		      }
		     }
		    }
		  }
	 
	 /**
	  * ����QQ����󣬹رյ���
	  * @param info
	  */
	 public void closeWallet(AccessibilityNodeInfo info) {
		   /*���if����������ǣ��ҵ�����ͼ��*/
		 	AccessibilityNodeInfo i = info.getParent().getChild(0);
		 	//����Ƿ���ͼƬ�Լ������ǹر�
		   if (i.getClassName().toString().equals("android.widget.ImageButton") 
				   && i.getContentDescription().equals(QQ_CLOSE_WALLET)) {
		    i.performAction(AccessibilityNodeInfo.ACTION_CLICK);
		    }

		  }
	 private boolean shouldReturn(String id, long duration) {  
		    // IDΪ��   
		   if (id == null) return true;   
		   // ���ƺͻ��治һ��  
		   if (duration < MAX_CACHE_TOLERANCE && id.equals(lastFetchedHongbaoId)) {    
		     return true;  
		    }  
		   return false; 
		  } 
	 
	 private String getRedPacketsText(AccessibilityNodeInfo node) {  
		   /* ��ȡ����ϵ��ı� */  
		   String content;
		   try {
		     AccessibilityNodeInfo i = node.getParent().getChild(0); 
		     if(i.getText() != null)
		    	 content = i.getText().toString();
		     else if(i.getContentDescription() != null)
		    	 content = i.getContentDescription().toString();
		     else
		    	 content = null;
		     } catch (NullPointerException npe) {
		         return null;
		       }
		     return content;
		  }
	 
	 /**
	  * ����Ƿ���QQ���δ�򿪵�
	  */
	 private void checkNodeInfo()
	 {
		 if(rootNodeInfo == null)
		 {
			 return;
		 }
		 /* ����Ự���ڣ������ڵ�ƥ�䡰����𿪡�������������������������� */  
		 List<AccessibilityNodeInfo> ndes = findAccessibilityNodeInfosByTexts(rootNodeInfo, 
				 new String[]{QQ_DEFAULT_CLICK_OPEN, QQ_HONG_BAO_PASSWORD, QQ_CLICK_TO_PASTE_PASSWORD,QQ_RED_PACKETES_WALLET, "����"});
		 if (!ndes.isEmpty()) {
			    String nodeId = Integer.toHexString(System.identityHashCode(this.rootNodeInfo));     
			    if (!nodeId.equals(lastFetchedHongbaoId)) {
			    mLuckyMoneyReceived = true;
			    mReceiveNode = ndes;
			     } 
			    return;
			   }
	 }
	 
	 private List<AccessibilityNodeInfo> findAccessibilityNodeInfosByTexts(AccessibilityNodeInfo nodeInfo, String[] texts) {  
		    for (String text : texts) {
		      if (text == null) continue;
		      List<AccessibilityNodeInfo> nodes = nodeInfo.findAccessibilityNodeInfosByText(text);   
		      if (!nodes.isEmpty()) {
		        if (text.equals(WECHAT_OPEN_EN)
		        		&& !nodeInfo.findAccessibilityNodeInfosByText(WECHAT_OPENED_EN).isEmpty()) {        
		          continue;
		        }
		        return nodes;    
		      }
		     }
		    return new ArrayList<>(); 
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

	            mContext.getPackageManager().getActivityInfo(componentName, 0);
	            currentActivityName = componentName.flattenToShortString();
	            Log.e(TAG, "currentActivityName = " + currentActivityName);
	        } catch (PackageManager.NameNotFoundException e) {
	            currentActivityName = null;
	        }
	    }
}
