package com.xuebusi.cms.common.utils;

/**
 * 数字处理工具类
 *
 * @author: shiyanjun
 * @Date: 2018/11/1 下午12:04
 */
public class NumberUtil {


    /**
     * 对象转成Long
     */
    public static Long toLong(Object val) {
        try {
            long parseLong = Long.parseLong(String.valueOf(val));
            return parseLong;
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 对象转成Integer
     */
    public static Integer toInteger(Object val) {
        try {
            int parseInt = Integer.parseInt(String.valueOf(val));
            return parseInt;
        } catch (Exception e) {
            return null;
        }
    }
}
