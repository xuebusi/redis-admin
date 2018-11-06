package com.xuebusi.cms.common.cache;

import com.google.gson.Gson;
import com.xuebusi.cms.common.config.Global;
import com.xuebusi.cms.common.utils.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * redis工具类
 *
 * @Author shiyanjun
 * @Date 2018/10/20 下午3:37
 */
@Component
public class RedisUtils {

    private static Logger logger = LoggerFactory.getLogger(RedisUtils.class);

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    @Autowired
    private ValueOperations<String, String> valueOperations;
    @Autowired
    private HashOperations<String, String, Object> hashOperations;

    /**
     * 缓存key默认前缀
     */
    public static String DEFAULT_CACHE_PREFIX = "STUDIO-CMS:";
    public static boolean useDefaultPrefix = useDefaultKeyPrefix();
    /**
     * 默认过期时长，单位：秒
     */
    public final static long DEFAULT_EXPIRE = 60 * 60 * 24;
    /**
     * 不设置过期时长
     */
    public final static long NOT_EXPIRE = -1;
    private final static Gson gson = new Gson();

    public void set(String key, Object value, long expire) {
        valueOperations.set(prefix(key), toJson(value));
        if (expire != NOT_EXPIRE) {
            redisTemplate.expire(prefix(key), expire, TimeUnit.SECONDS);
        }
    }

    public void set(String key, Object value) {
        set(key, value, DEFAULT_EXPIRE);
    }

    public <T> T get(String key, Class<T> clazz, long expire) {
        String value = valueOperations.get(prefix(key));
        if (expire != NOT_EXPIRE) {
            redisTemplate.expire(prefix(key), expire, TimeUnit.SECONDS);
        }
        return value == null ? null : fromJson(value, clazz);
    }

    public <T> T get(String key, Class<T> clazz) {
        return get(key, clazz, NOT_EXPIRE);
    }

    public String get(String key, long expire) {
        String value = null;
        try {
            value = valueOperations.get(prefix(key));
            if (expire != NOT_EXPIRE) {
                redisTemplate.expire(prefix(key), expire, TimeUnit.SECONDS);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return value;
    }

    public String get(String key) {
        String value = get(key, NOT_EXPIRE);
        return value;
    }

    public Set<String> keys(String pattern) {
        Set<String> set = redisTemplate.keys(prefix(pattern));
        return set;
    }

    public void delete(String key) {
        try {
            redisTemplate.delete(prefix(key));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 删除hashKey
     *
     * @param key
     * @param hashKey
     */
    public void deleteHashKey(String key, String hashKey) {
        hashOperations.delete(key, hashKey);
    }

    /**
     * Object转成JSON数据
     */
    private String toJson(Object object) {
        if (object instanceof Integer || object instanceof Long || object instanceof Float ||
                object instanceof Double || object instanceof Boolean || object instanceof String) {
            return String.valueOf(object);
        }
        return gson.toJson(object);
    }

    /**
     * JSON数据，转成Object
     */
    private <T> T fromJson(String json, Class<T> clazz) {
        return gson.fromJson(json, clazz);
    }

    /**
     * 给key添加默认前缀
     */
    public static String prefix(String key) {
        if (useDefaultPrefix && !key.startsWith(DEFAULT_CACHE_PREFIX)) {
            key = DEFAULT_CACHE_PREFIX + key;
        }
        return key;
    }

    /**
     * 去掉key的前缀
     */
    public static String delPrefix(String key) {
        if (useDefaultPrefix && key.startsWith(DEFAULT_CACHE_PREFIX)) {
            String[] split = key.split(DEFAULT_CACHE_PREFIX);
            if (split.length > 1) {
                return split[1];
            }
        }
        return key;
    }

    /**
     * 从配置中读取是否使用缓存key默认前缀
     */
    public static boolean useDefaultKeyPrefix() {
        String value = Global.getConfig("useDefaultCacheKeyPrefix");
        boolean boo = Global.FALSE.equalsIgnoreCase(value);
        return StringUtils.isNotBlank(value) && boo ? false : true;
    }

}