package com.tools.common;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.RestrictTo;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;

import com.tools.BuildConfig;

import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * <p>
 * 常用工具类，包括各种获取一些基本应用信息的方法，以及判断权限，是否是 UI 线程的方法等
 * </p>
 */
@RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
@SuppressWarnings({"WeakerAccess", "unused"})
public abstract class Util {

    public static String getSdkVersion() {
        return BuildConfig.VERSION_NAME;
    }

    public static boolean isUiThread() {
        return isVersionAboveM() ? isUiThreadApi23() : Thread.currentThread() == Looper.getMainLooper().getThread();
    }

    @TargetApi(Build.VERSION_CODES.M)
    public static boolean isUiThreadApi23() {
        return Looper.getMainLooper().isCurrentThread();
    }

    public static boolean isVersionAboveM() {
        return getCurrentAndroidVersion() >= Build.VERSION_CODES.M;
    }

    public static int getCurrentAndroidVersion() {
        return Build.VERSION.SDK_INT;
    }

    public static int getPid() {
        return android.os.Process.myPid();
    }

    public static String getStackTrace(Throwable throwable) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        throwable.printStackTrace(pw);
        return sw.toString();
    }

    public static ActivityManager.MemoryInfo getMemoryInfo(Context context) {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        ActivityManager.MemoryInfo info = new ActivityManager.MemoryInfo();
        if (manager != null) manager.getMemoryInfo(info);
        return info;
    }

    /**
     * Check request permission is granted or not.
     *
     * @param activity   context
     * @param permission the permission you would like to check, e.g: Manifest.permission.WRITE_EXTERNAL_STORAGE
     * @return In API 23 and above
     * <ol>
     * <li>return {@link PackageManager#PERMISSION_GRANTED} (0) if permission granted</li>
     * <li>return {@link PackageManager#PERMISSION_DENIED} (-1) if permission denied</li>
     * </ol>
     * Lower than API 23
     * <ol>
     * <li>always return {@link PackageManager#PERMISSION_GRANTED} (0)</li>
     * </ol>
     */
    @TargetApi(Build.VERSION_CODES.M)
    public static int checkAndroidPermission(@NonNull Activity activity, @NonNull String permission) {
        if (getCurrentAndroidVersion() >= Build.VERSION_CODES.M) {
            return activity.checkSelfPermission(permission);
        } else {
            return 0;
        }
    }
}
