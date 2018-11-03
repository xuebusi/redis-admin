package com.xuebusi.cms.common.cache;

import com.alibaba.fastjson.JSON;
import com.xuebusi.cms.base.modules.redis.entity.SysRedis;
import com.xuebusi.cms.common.service.ServiceException;
import com.xuebusi.cms.common.utils.StringUtils;
import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.DataType;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SetOperations;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * redis工具类
 *
 * @Author shiyanjun
 * @Date 2018/10/20 下午3:37
 * @Param
 * @Return
 * @Exception
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
    @Autowired
    private ListOperations<String, Object> listOperations;
    @Autowired
    private SetOperations<String, Object> setOperations;
    @Autowired
    private ZSetOperations<String, Object> zSetOperations;

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
        valueOperations.set(key, toJson(value));
        if (expire != NOT_EXPIRE) {
            redisTemplate.expire(key, expire, TimeUnit.SECONDS);
        }
    }

    public void set(String key, Object value) {
        set(key, value, DEFAULT_EXPIRE);
    }

    public <T> T get(String key, Class<T> clazz, long expire) {
        String value = valueOperations.get(key);
        if (expire != NOT_EXPIRE) {
            redisTemplate.expire(key, expire, TimeUnit.SECONDS);
        }
        return value == null ? null : fromJson(value, clazz);
    }

    public <T> T get(String key, Class<T> clazz) {
        return get(key, clazz, NOT_EXPIRE);
    }

    public String get(String key, long expire) {
        String value = null;
        try {
            value = valueOperations.get(key);
            if (expire != NOT_EXPIRE) {
                redisTemplate.expire(key, expire, TimeUnit.SECONDS);
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
        Set<String> set = redisTemplate.keys(pattern);
        return set;
    }

    public void delete(String key) {
        try {
            redisTemplate.delete(key);
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
     * 根据key的类型获取value
     * 将value转成json
     */
    public SysRedis getStringValByKeyType(String key) {
        DataType type = redisTemplate.type(key);
        Long expire = redisTemplate.getExpire(key);
        SysRedis sysRedis = new SysRedis(type.code(), key, null, String.valueOf(expire));
        switch (type) {
            case STRING:
                sysRedis.setRedisValue(get(key));
                break;
            case LIST:
                List<Object> list = listOperations.range(key, 0, -1);
                sysRedis.setRedisValue(JSON.toJSONString(list));
                break;
            case SET:
                Set<Object> set = setOperations.members(key);
                sysRedis.setRedisValue(JSON.toJSONString(set));
                break;
            case ZSET:
                Set<Object> zSet = zSetOperations.range(key, 0, -1);
                sysRedis.setRedisValue(JSON.toJSONString(zSet));
                break;
            case HASH:
                Map<String, Object> map = hashOperations.entries(key);
                sysRedis.setRedisValue(JSON.toJSONString(map));
                break;
            default:
                logger.error("未知的key类型");
                break;
        }
        return sysRedis;
    }

    /**
     * 根据数据类型进行查询
     *
     * @param key 缓存key
     */
    public SysRedis getSysRedisByKeyType(String key) {
        DataType type = redisTemplate.type(key);
        Long expire = redisTemplate.getExpire(key);
        SysRedis sysRedis = new SysRedis(type.code(), key, null, String.valueOf(expire));
        switch (type) {
            case STRING:
                String redisValue = get((key));
                sysRedis.setRedisValue(redisValue);
                break;
            case LIST:
                List<Object> list = listOperations.range(key, 0, -1);
                sysRedis.setValList(list);
                break;
            case SET:
                Set<Object> set = setOperations.members(key);
                sysRedis.setValSet(set);
                break;
            case ZSET:
                List<SysRedis.ScoreVal> zsetList = new ArrayList<>();
                Set<ZSetOperations.TypedTuple<Object>> tuples = zSetOperations.rangeWithScores(key, 0, -1);
                Iterator<ZSetOperations.TypedTuple<Object>> it = tuples.iterator();
                while (it.hasNext()) {
                    ZSetOperations.TypedTuple<Object> next = it.next();
                    zsetList.add(new SysRedis.ScoreVal(String.valueOf(next.getScore()), String.valueOf(next.getValue())));
                }
                sysRedis.setZsetList(zsetList);
                break;
            case HASH:
                Map<String, Object> map = hashOperations.entries(key);
                sysRedis.setValMap(map);
                break;
            default:
                logger.error("未知的key类型");
                break;
        }
        return sysRedis;
    }

    /**
     * 根据数据类型进行保存
     *
     * @param redisModel 各类型缓存统一模型
     */
    public void set(RedisModel redisModel) {
        String key = redisModel.getKey();
        switch (redisModel.getDataType()) {
            case STRING:
                valueOperations.set(key, redisModel.getValue());
                break;
            case LIST:
                if (redisModel.isLeft()) {
                    listOperations.leftPush(key, redisModel.getValue());
                } else {
                    listOperations.rightPush(key, redisModel.getValue());
                }
                break;
            case SET:
                setOperations.add(key, redisModel.getValue());
                break;
            case ZSET:
                zSetOperations.add(key, redisModel.getValue(), redisModel.getScore());
                break;
            case HASH:
                hashOperations.put(key, redisModel.getHashKey(), redisModel.getValue());
                break;
            default:
                break;
        }
        // 更新过期时间
        setExpire(key, redisModel.getExpire());
    }

    /**
     * 更新或添加值
     *
     * @param sysRedis
     */
    public void updateRedisValue(SysRedis sysRedis) {
        DataType dataType = DataType.fromCode(sysRedis.getDataType());
        switch (dataType) {
            case STRING:
                valueOperations.set(sysRedis.getOldRedisKey(), sysRedis.getRedisValue());
                break;
            case LIST:
                if ("0".equals(sysRedis.getFromLeft())) {
                    listOperations.rightPush(sysRedis.getOldRedisKey(), sysRedis.getRedisValue());
                } else {
                    listOperations.leftPush(sysRedis.getOldRedisKey(), sysRedis.getRedisValue());
                }
                break;
            case SET:
                setOperations.add(sysRedis.getOldRedisKey(), sysRedis.getRedisValue());
                break;
            case ZSET:
                double score = StringUtils.toDouble(sysRedis.getScore());
                zSetOperations.add(sysRedis.getOldRedisKey(), sysRedis.getRedisValue(), score);
                break;
            case HASH:
                hashOperations.put(sysRedis.getOldRedisKey(), sysRedis.getHashKey(), sysRedis.getRedisValue());
                break;
            default:
                break;
        }
    }

    /**
     * 更新过期时间
     *
     * @param key    缓存key
     * @param expire 过期时间,单位秒
     */
    public void setExpire(String key, Long expire) {
        // 忽略等于0的过期时间
        if (expire != null && expire != 0) {
            if (expire < 0) {
                redisTemplate.persist(key);
            } else {
                redisTemplate.expire(key, expire * 1000, TimeUnit.MILLISECONDS);
            }
        }
    }

    /**
     * 根据key查询数据类型
     *
     * @param key
     */
    public DataType type(String key) {
        return redisTemplate.type(key);
    }

    /**
     * key是否存在
     *
     * @param key
     */
    public Boolean hasKey(String key) {
        return redisTemplate.hasKey(key);
    }

    /**
     * 重命名key
     *
     * @param oldKey 旧key
     * @param newKey 新key
     */
    public void rename(String oldKey, String newKey) {
        redisTemplate.rename(oldKey, newKey);
    }

    public void deleteListValue(String oldRedisKey, int currentIndex) {
        List<Object> list = listOperations.range(oldRedisKey, 0, -1);
        if (list.size() == 1) {
            throw new ServiceException("集合中至少要有1条数据");
        }
        List<Object> newList = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            if (i != currentIndex) {
                newList.add(list.get(i));
            }
            listOperations.rightPop(oldRedisKey);
        }
        listOperations.rightPushAll(oldRedisKey, newList);
    }

    /**
     * 删除Set集合中的元素
     *
     * @param oldRedisKey
     * @param redisValue
     */
    public void deleteSetValue(String oldRedisKey, String redisValue) {
        setOperations.remove(oldRedisKey, redisValue);
    }

    /**
     * 删除Set集合中的元素
     *
     * @param oldRedisKey
     * @param redisValue
     */
    public void deleteZSetValue(String oldRedisKey, String redisValue) {
        zSetOperations.remove(oldRedisKey, redisValue);
    }
}