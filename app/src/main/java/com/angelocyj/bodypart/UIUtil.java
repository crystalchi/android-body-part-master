package com.angelocyj.bodypart;

import android.content.res.Resources;

/**
 * 类描述：
 * 创建人：angelo
 * 创建时间：12/16/15 11:13 AM
 */
public class UIUtil {

    public static int dip2px(int dpValue) {
        final float scale = Resources.getSystem().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    public static int dip2px(float dpValue) {
        final float scale = Resources.getSystem().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }
}
