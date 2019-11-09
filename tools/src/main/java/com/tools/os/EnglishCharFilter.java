package com.tools.os;

import android.text.InputFilter;
import android.text.Spanned;

/**
 * Creator by Administrator on 2018/12/8.
 * 类描述:自定义的InputFilter ，用来限制英文中文输入字符限制。一个汉字代表两个英文。
 */

public class EnglishCharFilter implements InputFilter {

    private int maxLen;

    /**
     * 输入英文的最大长度 。比如你想要限制40个汉字，80个英文字符，传入的值就是80
     * 使用方式：mEdit.setFilters(new InputFilter[]{filter});
     */

    public EnglishCharFilter(int maxEnglishLen) {
        maxLen = maxEnglishLen;
    }

    //根据传入的字符串返回字符串对应的字符是多少。一个中文2个字符，其余是是1个字符
    public static int calculateLength(CharSequence c) {
        double len = 0;
        for (int i = 0; i < c.length(); i++) {
            char cc = c.charAt(i);
            if ((cc & 0xffff) <= 0xff) {
                //如果是英文字符或者数字  + 1
                //len += 0.5;
                len++;
            } else {
                //如果是中文  + 2
                //len++;
                len += 2;
            }
        }
        return (int) Math.round(len);
    }

    @Override
    public CharSequence filter(CharSequence src, int start, int end, Spanned dest, int dstart, int dend) {
        int dindex = 0;
        int count = 0;

        while (count <= maxLen && dindex < dest.length()) {
            char c = dest.charAt(dindex++);
            if ((c & 0xffff) <= 0xff) {
                count = count + 1;
            } else {
                count = count + 2;
            }
        }

        if (count > maxLen) {
            return dest.subSequence(0, dindex - 1);
        }

        int sindex = 0;
        while (count <= maxLen && sindex < src.length()) {
            char c = src.charAt(sindex++);
            if ((c & 0xffff) <= 0xff) {
                count = count + 1;
            } else {
                count = count + 2;
            }
        }

        if (count > maxLen) {
            sindex--;
            return src.subSequence(0, sindex);
        }
        return null;
    }

}
