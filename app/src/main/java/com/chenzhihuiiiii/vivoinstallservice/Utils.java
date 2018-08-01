package com.chenzhihuiiiii.vivoinstallservice;

import android.accessibilityservice.AccessibilityServiceInfo;
import android.content.Context;
import android.util.Log;
import android.view.accessibility.AccessibilityManager;

import java.util.List;

/**
 * Created by chenzhihui193 on 2018/7/31.
 */

public class Utils {
    /**
     * 判断AccessibilityService服务是否已经启动
     *
     * @param context
     * @param name
     * @return
     */
    public static boolean isStartAccessibilityService(Context context, String name) {
        AccessibilityManager am = (AccessibilityManager) context.getSystemService(Context.ACCESSIBILITY_SERVICE);
        List<AccessibilityServiceInfo> serviceInfos = am.getEnabledAccessibilityServiceList(AccessibilityServiceInfo.FEEDBACK_GENERIC);
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("running accessbility service:\n");
        for (AccessibilityServiceInfo info : serviceInfos) {
            String id = info.getId();
            stringBuilder.append(id + "\n");
            if (id.contains(name)) {
                stringBuilder.append("found require service:" + name);
                return true;
            }
        }
        Log.e("aaa", stringBuilder.toString());
        return false;
    }
}
