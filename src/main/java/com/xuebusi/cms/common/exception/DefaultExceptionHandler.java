package com.xuebusi.cms.common.exception;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

/**
 * 统一异常处理类
 *
 * @Author shiyanjun
 * @Date 2018/10/22 下午7:10
 */
@ControllerAdvice
public class DefaultExceptionHandler {

    /**
     * Api异常处理
     */
    @ResponseBody
    @ExceptionHandler(ApiException.class)
    public Map<String, Object> apiExceptionHandler(HttpServletRequest request, Exception ex) {
        Map<String, Object> map = new HashMap<>();
        map.put("requestURI", request.getRequestURI());
        map.put("message", ex.toString());
        return map;
    }

    /**
     * Model异常处理
     */
    @ExceptionHandler(Exception.class)
    public String serviceExceptionHandler(HttpServletRequest request, Exception ex, Model model) {
        model.addAttribute("requestURI", request.getRequestURI());
        model.addAttribute("message", ex.toString());
        return "error/500";
    }
}
