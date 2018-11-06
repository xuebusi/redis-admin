package com.xuebusi.cms.base.modules.redis.dao;

import com.xuebusi.cms.base.modules.redis.entity.SysRedis;
import com.xuebusi.cms.common.cache.RedisModel;
import com.xuebusi.cms.common.persistence.Page;
import com.xuebusi.cms.common.utils.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.connection.DataType;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@Component
public class RedisDao {

    @Autowired
    @Qualifier("redisTemplate")
    private RedisTemplate template;

    @Autowired
    @Qualifier("stringRedisTemplate")
    private StringRedisTemplate stringRedisTemplate;


    /**
     * 根据数据类型进行查询
     *
     * @param key 缓存key
     */
    public SysRedis getByType(String key) {
        DataType type = template.type(key);
        Long expire = template.getExpire(key);
        SysRedis sysRedis = new SysRedis(type.code(), key, null, String.valueOf(expire));
        switch (type) {
            case STRING:
                Object redisValue = template.opsForValue().get(key);
                sysRedis.setRedisValue(String.valueOf(redisValue));
                sysRedis.setElCount(1L);
                break;
            case LIST:
                List<Object> list = template.opsForList().range(key, 0, -1);
                sysRedis.setValList(list);
                sysRedis.setElCount(template.opsForList().size(key));
                break;
            case SET:
                Set<Object> set = template.opsForSet().members(key);
                sysRedis.setValSet(set);
                sysRedis.setElCount(template.opsForSet().size(key));
                break;
            case ZSET:
                List<SysRedis.ScoreVal> zsetList = new ArrayList<>();
                Set<ZSetOperations.TypedTuple<Object>> tuples = template.opsForZSet().rangeWithScores(key, 0, -1);
                Iterator<ZSetOperations.TypedTuple<Object>> it = tuples.iterator();
                while (it.hasNext()) {
                    ZSetOperations.TypedTuple<Object> next = it.next();
                    zsetList.add(new SysRedis.ScoreVal(String.valueOf(next.getScore()), String.valueOf(next.getValue())));
                }
                sysRedis.setZsetList(zsetList);
                sysRedis.setElCount(template.opsForZSet().size(key));
                break;
            case HASH:
                Map<String, Object> map = template.opsForHash().entries(key);
                sysRedis.setValMap(map);
                sysRedis.setElCount(template.opsForHash().size(key));
                break;
            default:
                break;
        }
        return sysRedis;
    }

    public Page<SysRedis> findPage(Page<SysRedis> page, SysRedis sysRedis) {

        // 设置页码
        int pageNo = page.getPageNo();
        int pageSize = page.getPageSize();
        sysRedis.setPage(page);

        Set<String> keySet;
        if (StringUtils.isNotBlank(sysRedis.getRedisKey())) {
            keySet = template.keys(sysRedis.getRedisKey());
        } else {
            keySet = template.keys("*");
        }
        List<String> keyList = new ArrayList<>(keySet);
        page.setCount(keyList.size());
        List<SysRedis> list = new ArrayList<>();
        if (!CollectionUtils.isEmpty(keyList)) {
            int start = pageNo == 1 ? 0 : (pageNo - 1) * pageSize;
            for (int i = start, j = 0; i < keyList.size() && j < pageSize; i++, j++) {
                String key = keyList.get(i);
                SysRedis newSysRedis = this.getByType(key);
                list.add(newSysRedis);
            }
        }
        page.setList(list);
        return page;
    }

    /**
     * 根据数据类型进行保存
     *
     * @param redisModel 各类型缓存统一模型
     */
    public void save(RedisModel redisModel) {
        String key = redisModel.getKey();
        switch (redisModel.getDataType()) {
            case STRING:
                template.opsForValue().set(key, redisModel.getValue());
                break;
            case LIST:
                if (redisModel.isLeft()) {
                    template.opsForList().leftPush(key, redisModel.getValue());
                } else {
                    template.opsForList().rightPush(key, redisModel.getValue());
                }
                break;
            case SET:
                template.opsForSet().add(key, redisModel.getValue());
                break;
            case ZSET:
                template.opsForZSet().add(key, redisModel.getValue(), redisModel.getScore());
                break;
            case HASH:
                template.opsForHash().put(key, redisModel.getHashKey(), redisModel.getValue());
                break;
            default:
                break;
        }
        // 更新过期时间
        updateExpire(key, redisModel.getExpire());
    }

    public void del(String key) {
        template.delete(key);
    }

    /**
     * 删除集合中的元素
     *
     * @param key     集合的key
     * @param element 集合的元素,如果是list则为元素的索引
     */
    public void remove(Object key, Object element) {
        DataType type = template.type(key);
        switch (type) {
            case STRING:
                break;
            case LIST:
                this.delListValue(String.valueOf(key), Integer.parseInt(String.valueOf(element)));
                break;
            case SET:
                template.opsForSet().remove(key, element);
                break;
            case ZSET:
                template.opsForZSet().remove(key, element);
                break;
            case HASH:
                template.opsForHash().delete(key, element);
                break;
            case NONE:
                break;
        }
    }

    /**
     * 按索引删除元素
     *
     * @param redisKey
     * @param currentIndex
     */
    public void delListValue(String redisKey, int currentIndex) {
        List<Object> list = template.opsForList().range(redisKey, 0, -1);
        List<Object> newList = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            if (i != currentIndex) {
                newList.add(list.get(i));
            }
            template.opsForList().rightPop(redisKey);
        }
        template.opsForList().rightPushAll(redisKey, newList);
    }

    public void rename(Object oldKey, Object newKey) {
        template.rename(oldKey, newKey);
    }

    /**
     * 更新过期时间
     *
     * @param key    缓存key
     * @param expire 过期时间,单位秒
     */
    public void updateExpire(String key, Long expire) {
        // 忽略等于0的过期时间
        if (expire != null && expire != 0) {
            if (expire < 0) {
                template.persist(key);
            } else {
                template.expire(key, expire * 1000, TimeUnit.MILLISECONDS);
            }
        }
    }

    /**
     * 往集合中添加元素
     *
     * @param sysRedis
     */
    public void addValue(SysRedis sysRedis) {
        String key = sysRedis.getRedisKey();
        DataType type = template.type(key);
        switch (type) {
            case STRING:
                stringRedisTemplate.opsForValue().set(key, sysRedis.getRedisValue());
                break;
            case LIST:
                if ("1".equals(sysRedis.getFromLeft())) {
                    template.opsForList().leftPush(sysRedis.getRedisKey(), sysRedis.getRedisValue());
                } else {
                    template.opsForList().rightPush(sysRedis.getRedisKey(), sysRedis.getRedisValue());
                }
                break;
            case SET:
                template.opsForSet().add(sysRedis.getRedisKey(), sysRedis.getRedisValue());
                break;
            case ZSET:
                template.opsForZSet().add(sysRedis.getRedisKey(), sysRedis.getRedisValue(), StringUtils.toDouble(sysRedis.getScore()));
                break;
            case HASH:
                template.opsForHash().put(sysRedis.getRedisKey(), sysRedis.getHashKey(), sysRedis.getRedisValue());
                break;
            default:
                break;
        }
    }

}
