package com.xuebusi.cms.base.modules.sys.web;

import com.xuebusi.cms.base.modules.sys.entity.Log;
import com.xuebusi.cms.base.modules.sys.service.LogService;
import com.xuebusi.cms.common.persistence.Page;
import com.xuebusi.cms.common.web.BaseController;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 日志Controller
 *
 * @author Idea
 * @version 2013-6-2
 */
@Controller
@RequestMapping(value = "${adminPath}/sys/log")
public class LogController extends BaseController {

    @Autowired
    private LogService logService;

    @RequiresPermissions("sys:log:view")
    @RequestMapping(value = {"list", ""})
    public String list(Log log, HttpServletRequest request, HttpServletResponse response, Model model) {
        Page<Log> page = logService.findPage(new Page<Log>(request, response), log);
        model.addAttribute("page", page);
        return "base/modules/sys/logList";
    }

}
