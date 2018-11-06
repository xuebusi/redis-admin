package com.xuebusi.cms.base.modules.redis.web;

import com.xuebusi.cms.base.modules.redis.entity.SysRedis;
import com.xuebusi.cms.base.modules.redis.service.RedisService;
import com.xuebusi.cms.common.config.Global;
import com.xuebusi.cms.common.persistence.Page;
import com.xuebusi.cms.common.utils.StringUtils;
import com.xuebusi.cms.common.web.BaseController;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 缓存管理Controller
 *
 * @author shiyanjun
 * @version 2018-10-20
 */
@Controller
@RequestMapping(value = "${adminPath}/redis/sysRedis")
public class SysRedisController extends BaseController {

    @Autowired
    private RedisService redisService;

    @ModelAttribute
    public SysRedis get(@RequestParam(required = false) SysRedis sysRedis) {
        if (sysRedis == null) {
            sysRedis = new SysRedis();
        } else {
            sysRedis = redisService.get(sysRedis.getRedisKey());
        }
        return sysRedis;
    }

    @RequiresPermissions("redis:sysRedis:view")
    @RequestMapping(value = {"list", ""})
    public String list(SysRedis sysRedis, HttpServletRequest request, HttpServletResponse response, Model model) {
        Page<SysRedis> page = redisService.findPage(new Page<>(request, response), sysRedis);
        model.addAttribute("page", page);
        return "base/modules/redis/sysRedisList";
    }

    @RequiresPermissions("redis:sysRedis:view")
    @RequestMapping(value = "form")
    public String form(SysRedis sysRedis, Model model) {
        SysRedis entity = null;
        if (StringUtils.isNotBlank(sysRedis.getRedisKey())) {
            entity = redisService.get(sysRedis.getRedisKey());
        }
        if (entity == null) {
            model.addAttribute("sysRedis", sysRedis);
        } else {
            model.addAttribute("sysRedis", entity);
        }
        return "base/modules/redis/sysRedisForm";
    }

    @RequiresPermissions("redis:sysRedis:edit")
    @RequestMapping(value = "save")
    public String save(SysRedis sysRedis, Model model, RedirectAttributes redirectAttributes) {
        if (!beanValidator(model, sysRedis)) {
            return form(sysRedis, model);
        }
        redisService.save(sysRedis);
        addMessage(redirectAttributes, "缓存保存成功");
        return "redirect:" + Global.getAdminPath() + "/redis/sysRedis/list?repage";
    }

    @RequiresPermissions("redis:sysRedis:edit")
    @RequestMapping(value = "delete")
    public String delete(SysRedis sysRedis, RedirectAttributes redirectAttributes) {
        redisService.del(sysRedis);
        addMessage(redirectAttributes, "缓存删除成功");
        return "redirect:" + Global.getAdminPath() + "/redis/sysRedis/list?repage";
    }

    @RequiresPermissions("redis:sysRedis:edit")
    @RequestMapping(value = "remove")
    public String remove(SysRedis sysRedis, RedirectAttributes redirectAttributes) {
        redisService.remove(sysRedis);
        redirectAttributes.addAttribute("redisKey", sysRedis.getRedisKey());
        addMessage(redirectAttributes, "元素删除成功");
        return "redirect:" + Global.getAdminPath() + "/redis/sysRedis/form?repage";
    }

    @RequiresPermissions("redis:sysRedis:edit")
    @RequestMapping(value = "rename")
    public String rename(SysRedis sysRedis, RedirectAttributes redirectAttributes) {
        if (!sysRedis.getOldRedisKey().equals(sysRedis.getRedisKey())) {
            redisService.rename(sysRedis);
        }
        redirectAttributes.addAttribute("redisKey", sysRedis.getRedisKey());
        addMessage(redirectAttributes, "名称更新成功");
        return "redirect:" + Global.getAdminPath() + "/redis/sysRedis/form?repage";
    }

    @RequiresPermissions("redis:sysRedis:edit")
    @RequestMapping(value = "updateExpire")
    public String updateExpire(SysRedis sysRedis, RedirectAttributes redirectAttributes) {
        redisService.updateExpire(sysRedis);
        redirectAttributes.addAttribute("redisKey", sysRedis.getRedisKey());
        addMessage(redirectAttributes, "过期时间更新成功");
        return "redirect:" + Global.getAdminPath() + "/redis/sysRedis/form?repage";
    }

    @RequiresPermissions("redis:sysRedis:edit")
    @RequestMapping(value = "addValue")
    public String addValue(SysRedis sysRedis, RedirectAttributes redirectAttributes) {
        redisService.addValue(sysRedis);
        redirectAttributes.addAttribute("redisKey", sysRedis.getRedisKey());
        addMessage(redirectAttributes, "元素更新成功");
        return "redirect:" + Global.getAdminPath() + "/redis/sysRedis/form?repage";
    }
}