package com.tools.os;

import android.content.Context;
import android.widget.Toast;

/**
 * Creator by Administrator on 2019/4/6.
 * 类描述:吐司工具类
 */

public class ToastUtil {

    //短吐司
    public static void shortToast(Context context, String Msg) {
        Toast.makeText(context.getApplicationContext(), Msg, Toast.LENGTH_SHORT).show();
    }

    //长吐司
    public static void longToast(Context context, String Msg) {
        Toast.makeText(context.getApplicationContext(), Msg, Toast.LENGTH_LONG).show();
    }

}
