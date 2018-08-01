package com.chenzhihuiiiii.vivoinstallservice;

import android.accessibilityservice.AccessibilityService;
import android.app.ActivityManager;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.view.accessibility.AccessibilityWindowInfo;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by chenzhihui193 on 2018/7/31.
 */

public class VivoInstallerHelperService extends AccessibilityService {
    static final String TAG = "hahahaha";
    String password = "";
    //等待那个输入密码弹窗弹出来
    boolean needWaitForDialog = true;

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        if (!event.getPackageName().equals("com.android.packageinstaller")) {
            needWaitForDialog = true;
            return;
        }
        log("onAccessibilityEvent");
        if (needWaitForDialog) {
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        AccessibilityNodeInfo rootNode = getRootInActiveWindow();
        if (rootNode == null) {
            log("rootNode: null");
            return;
        }
        if (event.getPackageName().equals("com.android.packageinstaller") || event.getPackageName().equals("com.vivo.secime.service")) {
            //vivo账号的密码
            password = getSharedPreferences(Constant.PREFRENCE_NAME, MODE_PRIVATE)
                    .getString(Constant.PREFRENCE_KEY_VIVO_PASSWORD, Constant.DEFAULT_PASSWORD);
            if (!TextUtils.isEmpty(password)) {
                fillPassword(rootNode);
            }
            findAndClickView(rootNode);
        }
    }


    /**
     * 自动填充密码
     */
    private void fillPassword(AccessibilityNodeInfo rootNode) {
        log("rootNode.getChildCount()：" + rootNode.getChildCount());
        if (rootNode != null && rootNode.getChildCount() > 0) {
            traverseChilds(rootNode);
        }
    }


    /**
     * @param info
     */
    public void traverseChilds(AccessibilityNodeInfo info) {
        if (info.getChildCount() == 0) {
            log("windowId:" + info.getWindowId() + " widget:" + info.getClassName()
                    + " showDialog:" + info.canOpenPopup() + " text:" + info.getText());
            if (info.getClassName().equals("android.widget.EditText")) {
                log("found a edittext");
                if (info.getPackageName().equals("com.bbk.account")) {
                    Bundle arguments = new Bundle();
                    arguments.putCharSequence(AccessibilityNodeInfo
                            .ACTION_ARGUMENT_SET_TEXT_CHARSEQUENCE, password);
                    info.performAction(AccessibilityNodeInfo.ACTION_SET_TEXT, arguments);
                    needWaitForDialog = false;
                    return;
                }
            }
        } else {
            for (int i = 0; i < info.getChildCount(); i++) {
                if (info.getChild(i) != null) {
                    traverseChilds(info.getChild(i));
                }
            }
        }
    }


    /**
     * 查找按钮并点击
     */
    private void findAndClickView(AccessibilityNodeInfo rootNode) {
        List<AccessibilityNodeInfo> nodeInfoList = new ArrayList<>();
        nodeInfoList.addAll(rootNode.findAccessibilityNodeInfosByText("确定"));
        nodeInfoList.addAll(rootNode.findAccessibilityNodeInfosByText("继续安装"));
        nodeInfoList.addAll(rootNode.findAccessibilityNodeInfosByText("安装"));
        nodeInfoList.addAll(rootNode.findAccessibilityNodeInfosByText("打开"));

        for (AccessibilityNodeInfo nodeInfo : nodeInfoList) {
            boolean success = nodeInfo.performAction(AccessibilityNodeInfo.ACTION_CLICK);
            if (nodeInfo.getText().equals("安装")) {
                needWaitForDialog = true;
                log("点击了安装");
            }
        }
    }

    @Override
    public void onInterrupt() {
        log("onInterrupt");
    }

    @Override
    protected void onServiceConnected() {
        super.onServiceConnected();
        log("onServiceConnected");
    }

    private void log(String content) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        ActivityManager.RunningTaskInfo info = manager.getRunningTasks(1).get(0);
        String shortClassName = info.topActivity.getShortClassName();    //类名
        String className = info.topActivity.getClassName();              //完整类名
        String packageName = info.topActivity.getPackageName();
        Log.e(TAG, className+" "+"needWaitForDialog" + needWaitForDialog+" "+content);
    }
}
