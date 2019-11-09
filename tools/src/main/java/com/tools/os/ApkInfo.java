package com.tools.os;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.annotation.RestrictTo;
import android.util.Log;

import com.tools.common.Constant;

/**
 * Creator by Administrator on 2019/11/3.
 * 类描述:获取APK信息工具类
 */

@RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
public class ApkInfo {

    public static int getVersionCode(@NonNull Context context) {
        int versionCode = -1;
        try {
            versionCode = context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            Log.w(Constant.TAG_TOOLS, "Exception happen when trying to get version code", e);
        }
        return versionCode;
    }

    public static String getVersionName(@NonNull Context context) {
        String versionName = "";
        try {
            versionName = context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            Log.w(Constant.TAG_TOOLS, "Exception happen when trying to get version name", e);
        }
        return versionName;
    }

    public static String getPackageName(@NonNull Activity activity) {
        return activity.getPackageName();
    }

}
