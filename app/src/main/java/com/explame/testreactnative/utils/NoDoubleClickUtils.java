package com.explame.testreactnative.utils;

/**
 * 防止重复点击
 * Created by Z on 2017/4/15.
 */

public class NoDoubleClickUtils {

    private static long lastClickTime;
    private final static int SPACE_TIME = 500;

    public static void initLastClickTime() {
        lastClickTime = 0;
    }

    public synchronized static boolean isDoubleClick() {
        long currentTime = System.currentTimeMillis();
        boolean isClick2 = !(currentTime - lastClickTime > SPACE_TIME);
        lastClickTime = currentTime;
        return isClick2;
    }
}
