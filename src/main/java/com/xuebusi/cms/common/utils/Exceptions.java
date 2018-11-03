package com.xuebusi.cms.common.utils;

import javax.servlet.http.HttpServletRequest;
import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * 关于异常的工具类.
 *
 * @author Idea
 * @version 2013-01-15
 */
public class Exceptions {

    /**
     * 将CheckedException转换为UncheckedException.
     */
    public static RuntimeException unchecked(Exception e) {
        if (e instanceof RuntimeException) {
            return RuntimeException.class.cast(e);
        } else {
            return new RuntimeException(e);
        }
    }

    /**
     * 将ErrorStack转化为String.
     */
    public static String getStackTraceAsString(Throwable e) {
        if (e == null)
            return "";
        StringWriter stringWriter = new StringWriter();
        e.printStackTrace(new PrintWriter(stringWriter));
        return stringWriter.toString();
    }

    /**
     * 判断异常是否由某些底层的异常引起.
     */
    public static boolean isCausedBy(Exception ex, Class<? extends Exception>... causeExceptionClasses) {
        Throwable cause = ex.getCause();
        while (cause != null) {
            for (Class<? extends Exception> causeClass : causeExceptionClasses)
                if (causeClass.isInstance(cause))
                    return true;
            cause = cause.getCause();
        }
        return false;
    }

    /**
     * 在request中获取异常类
     *
     * @param request
     * @return
     */
    public static Throwable getThrowable(HttpServletRequest request) {
        Throwable ex = null;
        try {
            if (request.getAttribute("exception") != null) {
                ex = Throwable.class.cast(request.getAttribute("exception"));
            } else if (request.getAttribute("javax.servlet.error.exception") != null) {
                ex = Throwable.class.cast(request.getAttribute("javax.servlet.error.exception"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ex;
    }

}
