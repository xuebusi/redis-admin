package com.xuebusi.cms.common.filter;

import com.ckfinder.connector.FileUploadFilter;

import javax.servlet.annotation.WebFilter;
import javax.servlet.annotation.WebInitParam;

/**
 *  Idea
 *  @author: Idea
 *  @Date: 2018/10/24 下午7:31
 */
@WebFilter(urlPatterns = "/static/ckfinder/core/connector/java/connector.java", initParams = {
        @WebInitParam(name = "sessionCookieName", value = "JSESSIONID"),
        @WebInitParam(name = "sessionParameterName", value = "jsessionid")
})
public class CmsFileUploadFilter extends FileUploadFilter {
}
