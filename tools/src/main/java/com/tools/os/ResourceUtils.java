package com.tools.os;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.content.res.XmlResourceParser;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RestrictTo;

/**
 * <p>
 * Android 不同 Resource 的获取方法，库开发中不可以直接引用 { R } 文件，
 * 故使用动态获取 {@code id} 的形式获取对应资源。
 * </p>
 */
@RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
@SuppressWarnings({"WeakerAccess", "unused"})
public abstract class ResourceUtils {
    /**
     * This method will be removed when we support all resource types
     * Get Identifier for named resource
     *
     * @param activity context
     * @param name     resource name
     * @param defType  resource name type, check here for available types
     *                 https://developer.android.com/guide/topics/resources/available-resources.html?hl=es
     * @return resource id in real APK R.java,return 0 if id not found or params invalid
     */
    public static int getId(Activity activity, String name, String defType) {
        if (activity == null) return 0;
        else return activity.getResources().getIdentifier(name, defType, activity.getPackageName());
    }

    public static int getColor(Activity activity, String name) {
        if (activity != null) {
            int id = activity.getResources().getIdentifier(name, "color", activity.getPackageName());
            return (id == 0) ? 0 : activity.getResources().getColor(id);
        } else {
            return 0;
        }
    }

    public static float getDimension(Activity activity, String name) {
        if (activity != null) {
            int id = activity.getResources().getIdentifier(name, "dimen", activity.getPackageName());
            return (id == 0) ? 0 : activity.getResources().getDimension(id);
        } else {
            return 0;
        }
    }

    @NonNull
    public static String getString(Activity activity, String name) {
        if (activity != null) {
            int id = activity.getResources().getIdentifier(name, "string", activity.getPackageName());
            return (id == 0) ? "" : activity.getResources().getString(id);
        } else {
            return "";
        }
    }

    @NonNull
    public static String getString(Context context, String name) {
        if (context != null) {
            int id = context.getResources().getIdentifier(name, "string", context.getPackageName());
            return (id == 0) ? "" : context.getResources().getString(id);
        } else {
            return "";
        }
    }

    @NonNull
    public static int[] getIntArray(Activity activity, String name) {
        if (activity != null) {
            int id = activity.getResources().getIdentifier(name, "array", activity.getPackageName());
            return (id == 0) ? new int[0] : activity.getResources().getIntArray(id);
        } else {
            return new int[0];
        }
    }

    @NonNull
    public static String[] getStringArray(Activity activity, String name) {
        if (activity != null) {
            int id = activity.getResources().getIdentifier(name, "array", activity.getPackageName());
            return (id == 0) ? new String[0] : activity.getResources().getStringArray(id);
        } else {
            return new String[0];
        }
    }

    @Nullable
    public static Boolean getBoolean(Activity activity, String name) {
        if (activity != null) {
            int id = activity.getResources().getIdentifier(name, "bool", activity.getPackageName());
            return (id == 0) ? null : activity.getResources().getBoolean(id);
        } else {
            return null;
        }
    }

    @Nullable
    public static Integer getInteger(Activity activity, String name) {
        if (activity != null) {
            int id = activity.getResources().getIdentifier(name, "integer", activity.getPackageName());
            return (id == 0) ? null : activity.getResources().getInteger(id);
        } else {
            return null;
        }
    }

    @Nullable
    public static AssetManager getAsset(Activity activity) {
        if (activity != null) {
            return activity.getResources().getAssets();
        } else {
            return null;
        }
    }

    @Nullable
    public static XmlResourceParser getAnim(Activity activity, String name) {
        if (activity != null) {
            int id = activity.getResources().getIdentifier(name, "anim", activity.getPackageName());
            return (id == 0) ? null : activity.getResources().getAnimation(id);
        } else {
            return null;
        }
    }

    @Nullable
    public static Drawable getDrawable(Activity activity, String name) {
        if (activity != null) {
            int id = activity.getResources().getIdentifier(name, "drawable", activity.getPackageName());
            return (id == 0) ? null : activity.getResources().getDrawable(id);
        } else {
            return null;
        }
    }

    @Nullable
    public static XmlResourceParser getLayout(Activity activity, String name) {
        if (activity != null) {
            int id = activity.getResources().getIdentifier(name, "layout", activity.getPackageName());
            return (id == 0) ? null : activity.getResources().getLayout(id);
        } else {
            return null;
        }
    }

    public static int getLayoutIdByName(Activity activity, String resourcesName) {
        return getId(activity, resourcesName, "layout");
    }

    public static int getColorIdByName(Activity activity, String resourcesName) {
        return getId(activity, resourcesName, "color");
    }

    public static int getArrayIdByName(Activity activity, String resourcesName) {
        return getId(activity, resourcesName, "array");
    }

    public static int getStringIdByName(Activity activity, String resourcesName) {
        return getId(activity, resourcesName, "string");
    }

    public static String getStringByName(Activity activity, String resourcesName) {
        return activity.getResources().getString(getId(activity, resourcesName, "string"));
    }

    public static int getViewIdByName(Activity activity, String resourcesName) {
        return getId(activity, resourcesName, "id");
    }

    public static int getDrawableIdByName(Activity activity, String resourcesName) {
        return getId(activity, resourcesName, "drawable");
    }

    public static int getMipmapIdByName(Activity activity, String resourcesName) {
        return getId(activity, resourcesName, "mipmap");
    }

    public static int getAnimIdByName(Activity activity, String resourcesName) {
        return getId(activity, resourcesName, "anim");
    }

    public static int getStyleIdByName(Activity activity, String resourcesName) {
        return getId(activity, resourcesName, "style");
    }

    // 从Manifest.xml配置文件中获取数据(获取String类型不能是纯数字)
    public static String getMetaStringValue(Context context, String metaKey) {
        Bundle metaData = null;
        String metaValue = null;
        if (context == null || metaKey == null) {
            return null;
        }
        try {
            ApplicationInfo ai = context.getPackageManager().getApplicationInfo(
                    context.getPackageName(), PackageManager.GET_META_DATA);
            if (ai != null) {
                metaData = ai.metaData;
            }
            if (metaData != null) {
                metaValue = metaData.getString(metaKey);
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return metaValue;
    }

    // 从Manifest.xml配置文件中获取数据
    public static int getMetaIntValue(Context context, String metaKey) {
        Bundle metaData = null;
        int metaValue = -1;
        if (context == null || metaKey == null) {
            return metaValue;
        }
        try {
            ApplicationInfo ai = context.getPackageManager().getApplicationInfo(
                    context.getPackageName(), PackageManager.GET_META_DATA);
            if (ai != null) {
                metaData = ai.metaData;
            }
            if (metaData != null) {
                metaValue = metaData.getInt(metaKey);
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return metaValue;
    }
}
