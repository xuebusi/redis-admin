package com.xuebusi.cms.base.modules.redis.web;

import com.xuebusi.cms.base.modules.redis.entity.SysRedis;
import com.xuebusi.cms.base.modules.redis.service.SysRedisService;
import com.xuebusi.cms.common.cache.RedisUtils;
import com.xuebusi.cms.common.config.Global;
import com.xuebusi.cms.common.persistence.Page;
import com.xuebusi.cms.common.utils.SpringContextHolder;
import com.xuebusi.cms.common.utils.StringUtils;
import com.xuebusi.cms.common.web.BaseController;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.DataType;
import org.springframework.data.redis.core.RedisTemplate;
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
    private RedisUtils redisUtils;

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private SysRedisService sysRedisService;

    @ModelAttribute
    public SysRedis get(@RequestParam(required = false) SysRedis sysRedis) {
        if (sysRedis == null) {
            sysRedis = new SysRedis();
        } else {
            String dataType = sysRedis.getDataType();
            String oldRedisKey = sysRedis.getOldRedisKey();
            String redisKey = sysRedis.getRedisKey();
            if (StringUtils.isNotBlank(oldRedisKey)) {
                redisKey = oldRedisKey;
            }
            if (StringUtils.isNotBlank(dataType) && StringUtils.isNotBlank(redisKey)) {
                Boolean hasKey = redisTemplate.hasKey(redisKey);
                if (hasKey != null && hasKey) {
                    sysRedis = redisUtils.getSysRedisByKeyType(redisKey);
                }
            }
        }
        return sysRedis;
    }

    @RequiresPermissions("redis:sysRedis:view")
    @RequestMapping(value = {"list", ""})
    public String list(SysRedis sysRedis, HttpServletRequest request, HttpServletResponse response, Model model) {
        Page<SysRedis> page = sysRedisService.findPage(new Page<>(request, response), sysRedis);
        model.addAttribute("page", page);
        return "base/modules/redis/sysRedisList";
    }

    @RequiresPermissions("redis:sysRedis:view")
    @RequestMapping(value = "form")
    public String form(SysRedis sysRedis, Model model) {
        SysRedis entity = null;
        if (StringUtils.isNotBlank(sysRedis.getRedisKey())) {
            entity = sysRedisService.get(sysRedis.getRedisKey());
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
        // 数据类型校验
        if (!checkDataType(model, sysRedis.getDataType())) {
            return form(new SysRedis(), model);
        }
        // 修改时校验
        if (!checkBeforeUpdate(model, sysRedis)) {
            return form(sysRedis, model);
        }

        sysRedisService.save(sysRedis);
        SysRedis newSysRedis = new SysRedis();
        newSysRedis.setDataType(sysRedis.getDataType());
        model.addAttribute("sysRedis", newSysRedis);
        model.addAttribute("message", "保存缓存管理成功");
        return "base/modules/redis/sysRedisForm";
    }

    @RequiresPermissions("redis:sysRedis:edit")
    @RequestMapping(value = "updateRedisKey")
    public String updateRedisKey(SysRedis sysRedis, Model model) {
        String dataType = sysRedis.getDataType();
        if (dataType == null || DataType.NONE.code().equals(dataType)) {
            addMessage(model, "数据类型不能为空");
            model.addAttribute("sysRedis", sysRedis);
            return "base/modules/redis/sysRedisForm";
        }
        String oldRedisKey = sysRedis.getOldRedisKey();
        if (StringUtils.isBlank(oldRedisKey)) {
            addMessage(model, "缓存key不能为空");
            model.addAttribute("sysRedis", sysRedis);
            return "base/modules/redis/sysRedisForm";
        }

        String redisKey = sysRedis.getRedisKey();
        if (oldRedisKey.equals(redisKey)) {
            model.addAttribute("message", "保存缓存管理成功");
        } else {
            Boolean hasKey = redisTemplate.hasKey(redisKey);
            if (hasKey != null && hasKey) {
                addMessage(model, "要设置的key已存在");
                model.addAttribute("sysRedis", sysRedis);
                return "base/modules/redis/sysRedisForm";
            }
            redisUtils.rename(oldRedisKey, redisKey);
            model.addAttribute("message", "保存缓存管理成功");
        }
        model.addAttribute("sysRedis", sysRedis);
        return "base/modules/redis/sysRedisForm";
    }

    @RequiresPermissions("redis:sysRedis:edit")
    @RequestMapping(value = "updateExpire")
    public String updateExpire(SysRedis sysRedis, Model model) {
        String dataType = sysRedis.getDataType();
        if (dataType == null || DataType.NONE.code().equals(dataType)) {
            addMessage(model, "数据类型不能为空");
            model.addAttribute("sysRedis", sysRedis);
            return "base/modules/redis/sysRedisForm";
        }
        String oldRedisKey = sysRedis.getOldRedisKey();
        if (StringUtils.isBlank(oldRedisKey)) {
            addMessage(model, "缓存key不能为空");
            model.addAttribute("sysRedis", sysRedis);
            return "base/modules/redis/sysRedisForm";
        }

        redisUtils.setExpire(oldRedisKey, StringUtils.toLong(sysRedis.getExpire()));
        model.addAttribute("message", "保存过期时间成功");
        model.addAttribute("sysRedis", sysRedis);
        return "base/modules/redis/sysRedisForm";
    }

    @RequiresPermissions("redis:sysRedis:edit")
    @RequestMapping(value = "updateRedisValue")
    public String updateRedisValue(SysRedis sysRedis, Model model) {
        String dataType = sysRedis.getDataType();
        if (dataType == null || DataType.NONE.code().equals(dataType)) {
            addMessage(model, "数据类型不能为空");
            model.addAttribute("sysRedis", sysRedis);
            return "base/modules/redis/sysRedisForm";
        }
        String oldRedisKey = sysRedis.getOldRedisKey();
        if (StringUtils.isBlank(oldRedisKey)) {
            addMessage(model, "缓存key不能为空");
            model.addAttribute("sysRedis", sysRedis);
            return "base/modules/redis/sysRedisForm";
        }
        String redisValue = sysRedis.getRedisValue();
        if (StringUtils.isBlank(redisValue)) {
            addMessage(model, "缓存值不能为空");
            model.addAttribute("sysRedis", sysRedis);
            return "base/modules/redis/sysRedisForm";
        }
        redisUtils.updateRedisValue(sysRedis);
        model.addAttribute("message", "保存缓存值成功");
        model.addAttribute("sysRedis", sysRedis);
        return "base/modules/redis/sysRedisForm";
    }

    /**
     * redis数据类型校验
     */
    private boolean checkDataType(Model model, String dataType) {
        DataType[] types = DataType.values();
        for (DataType type : types) {
            if (type.code().equals(dataType)) {
                return true;
            }
        }
        addMessage(model, "未知的数据类型!");
        return false;
    }

    /**
     * key是否存在
     *
     * @param model
     * @param sysRedis
     */
    private boolean checkBeforeUpdate(Model model, SysRedis sysRedis) {
        String oldRedisKey = sysRedis.getOldRedisKey();
        if (StringUtils.isNotBlank(oldRedisKey)) {
            RedisUtils redisUtils = SpringContextHolder.getBean(RedisUtils.class);
            Boolean hasKey = redisUtils.hasKey(oldRedisKey);
            if (!hasKey) {
                addMessage(model, "要修改的数据已不存在了!");
                return false;
            }
        }
        return true;
    }

    @RequiresPermissions("redis:sysRedis:edit")
    @RequestMapping(value = "delete")
    public String delete(SysRedis sysRedis, RedirectAttributes redirectAttributes) {
        sysRedisService.delete(sysRedis);
        addMessage(redirectAttributes, "删除缓存管理成功");
        return "redirect:" + Global.getAdminPath() + "/redis/sysRedis/?repage";
    }

    @RequiresPermissions("redis:sysRedis:edit")
    @RequestMapping(value = "deleteHash")
    public String deleteHash(SysRedis sysRedis, Model model) {
        String dataType = sysRedis.getDataType();
        if (!DataType.HASH.code().equals(dataType)) {
            addMessage(model, "数据类型不正确");
            model.addAttribute("sysRedis", sysRedis);
            return "base/modules/redis/sysRedisForm";
        }
        redisUtils.deleteHashKey(sysRedis.getOldRedisKey(), sysRedis.getHashKey());
        return "redirect:" + Global.getAdminPath() + "/redis/sysRedis/?repage";
    }

    @RequiresPermissions("redis:sysRedis:edit")
    @RequestMapping(value = "deleteListValue")
    public String deleteListValue(SysRedis sysRedis, Model model) {
        String dataType = sysRedis.getDataType();
        if (!DataType.LIST.code().equals(dataType)) {
            addMessage(model, "数据类型不正确");
            model.addAttribute("sysRedis", sysRedis);
            return "base/modules/redis/sysRedisForm";
        }
        try {
            redisUtils.deleteListValue(sysRedis.getOldRedisKey(), StringUtils.toInteger(sysRedis.getCurrentIndex()));
        } catch (Exception e) {
            addMessage(model, e.getMessage());
            model.addAttribute("sysRedis", sysRedis);
        }
        return "base/modules/redis/sysRedisForm";
    }

    @RequiresPermissions("redis:sysRedis:edit")
    @RequestMapping(value = "deleteSetValue")
    public String deleteSetValue(SysRedis sysRedis, Model model) {
        String dataType = sysRedis.getDataType();
        if (!DataType.SET.code().equals(dataType)) {
            addMessage(model, "数据类型不正确");
            model.addAttribute("sysRedis", sysRedis);
            return "base/modules/redis/sysRedisForm";
        }
        try {
            redisUtils.deleteSetValue(sysRedis.getOldRedisKey(), sysRedis.getRedisValue());
        } catch (Exception e) {
            addMessage(model, e.getMessage());
            model.addAttribute("sysRedis", sysRedis);
        }
        return "base/modules/redis/sysRedisForm";
    }

    @RequiresPermissions("redis:sysRedis:edit")
    @RequestMapping(value = "deleteZSetValue")
    public String deleteZSetValue(SysRedis sysRedis, Model model) {
        String dataType = sysRedis.getDataType();
        if (!DataType.ZSET.code().equals(dataType)) {
            addMessage(model, "数据类型不正确");
            model.addAttribute("sysRedis", sysRedis);
            return "base/modules/redis/sysRedisForm";
        }
        try {
            redisUtils.deleteZSetValue(sysRedis.getOldRedisKey(), sysRedis.getRedisValue());
        } catch (Exception e) {
            addMessage(model, e.getMessage());
            model.addAttribute("sysRedis", sysRedis);
        }
        return "base/modules/redis/sysRedisForm";
    }
}