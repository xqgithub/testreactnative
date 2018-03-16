package com.explame.testreactnative.utils;

import android.text.TextUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by XQ on 2017/4/5.
 * 校验工具类
 */
public class CheckUtil {
    private static final String TAG = "CheckUtil";

    /**
     * 校验是否为手机号码
     */
    public static boolean isMobileNO(String mobiles) {
        /*
         * 移动：134、135、136、137、138、139、150、151、157(TD)、158、159、187、188
		 * 联通：130、131、132、152、155、156、185、186 电信：133、153、180、189、（1349卫通）
		 * 总结起来就是第一位必定为1，第二位必定为3或5或8，其他位置的可以为0-9
		 */
        String telRegex = "[1][3587]\\d{9}";// "[1]"代表第1位为数字1，"[358]"代表第二位可以为3、5、8中的一个，"\\d{9}"代表后面是可以是0～9的数字，有9位。

        if (TextUtils.isEmpty(mobiles)) {
            return false;
        } else {
            return mobiles.matches(telRegex);
        }
    }

    /**
     * 校验是否为正常的电话号码
     */
    public static boolean isTelephoneNO(String mobiles) {
        boolean flag;
        // 固定电话
        String regex_fixed = "(?:(\\(\\+?86\\))(0[0-9]{2,3}\\-?)?([2-9][0-9]{6,7})+(\\-[0-9]{1,4})?)|"
                + "(?:(86-?)?(0[0-9]{2,3}\\-?)?([2-9][0-9]{6,7})+(\\-[0-9]{1,4})?)";
        // 手机号码
        String regex_mobile = "[1][3587]\\d{9}";
        // 特殊服务号码(报警电话等)
        String regex_call = "^\\d{1,6}$";

        // Log.i(TAG, "mobiles----->" + mobiles);
        //
        // Log.i(TAG,
        // "mobiles.matches(regex_mobile)----->"
        // + mobiles.matches(regex_mobile));
        // Log.i(TAG,
        // "mobiles.matches(regex_fixed)----->"
        // + mobiles.matches(regex_fixed));
        // Log.i(TAG,
        // "mobiles.matches(regex_call)----->"
        // + mobiles.matches(regex_call));

        if (TextUtils.isEmpty(mobiles)) {
            return false;
        } else {
            if (mobiles.matches(regex_mobile) || mobiles.matches(regex_fixed)
                    || mobiles.matches(regex_call)) {
                flag = true;
            } else {
                flag = false;
            }

            return flag;
        }

    }


    /**
     * 校验邮箱格式
     */
    public static boolean checkEmail(String value) {
        boolean flag = false;
        Pattern p1 = null;
        Matcher m = null;
        p1 = Pattern.compile("\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*");
        m = p1.matcher(value);
        flag = m.matches();
        return flag;
    }


    /**
     * 验证QQ号码
     *
     * @param QQ
     * @return
     */
    public static boolean checkQQ(String QQ) {
        boolean flag = false;
        String regex = "[1-9][0-9]{4,14}";//第一位1-9之间的数字，第二位0-9之间的数字，数字范围4-14个之间
        flag = QQ.matches(regex);
        return flag;
    }

}
