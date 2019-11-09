package com.tools.security;

/**
 * Creator by Administrator on 2019/4/13.
 * 类描述:Hex转Str工具类
 */

public class HexUtil {

    //string转换为hex字符串
    public static String hheexx1(byte[] data) {
        String ret = null;
        if (data != null && data.length > 0) {
            StringBuilder sb = new StringBuilder();
            for (byte b : data) {
                //分别获取高四位，低四位的内容，将两个数值，转为字符
                int h = (b >> 4) & 0x0f;
                int l = b & 0x0f;
                char ch, cl;
                if (h > 9) {
                    ch = (char) ('a' + (h - 10));
                } else {  //0--9
                    ch = (char) ('0' + h);
                }

                if (l > 9) {
                    cl = (char) ('a' + (l - 10));
                } else {  //0--9
                    cl = (char) ('0' + l);
                }
                sb.append(ch).append(cl);
            }
            ret = sb.toString();
        }
        return ret;
    }

    //hex字符串转换成初始字符串
    public static String hheexx2(String str) {
        String string = null;
        byte[] ret = null;

        if (str != null) {
            int len = str.length();
            if (len > 0 && len % 2 == 0) {
                char[] chs = str.toCharArray();
                ret = new byte[len / 2];
                for (int i = 0, j = 0; i < len; i += 2, j++) {
                    char ch = chs[i];
                    char cl = chs[i + 1];

                    int ih = 0, il = 0, v = 0;
                    if (ch >= 'A' && ch <= 'F') {
                        ih = 10 + (ch - 'A');
                    } else if (ch >= 'a' && ch <= 'f') {
                        ih = 10 + (ch - 'a');
                    } else if (ch >= '0' && ch <= '9') {
                        ih = ch - '0';
                    }

                    if (cl >= 'A' && cl <= 'F') {
                        il = 10 + (cl - 'A');
                    } else if (cl >= 'a' && cl <= 'f') {
                        il = 10 + (cl - 'a');
                    } else if (cl >= '0' && cl <= '9') {
                        il = cl - '0';
                    }

                    v = ((ih & 0x0f) << 4) | (il & 0x0f);
                    //赋值
                    ret[j] = (byte) v;
                }
                string = new String(ret);
            }
        }
        return string;
    }

}
