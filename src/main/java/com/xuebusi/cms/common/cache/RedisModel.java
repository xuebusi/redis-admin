package com.xuebusi.cms.common.cache;

import org.springframework.data.redis.connection.DataType;

import java.io.Serializable;

/**
 * redis 各类型缓存统一模型
 *
 * @author: shiyanjun
 * @Date: 2018/10/31 下午3:49
 */
public class RedisModel implements Serializable {

    private DataType dataType;//数据类型
    private String key;       //缓存key
    private String value;     //缓存值
    private String hashKey;   //hash键,仅hash类型有效
    private boolean isLeft;   //从list左端添加
    private double score;     //得分
    private Long expire;      //过期时间,单位秒

    public DataType getDataType() {
        return dataType;
    }

    public void setDataType(DataType dataType) {
        this.dataType = dataType;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getHashKey() {
        return hashKey;
    }

    public void setHashKey(String hashKey) {
        this.hashKey = hashKey;
    }

    public boolean isLeft() {
        return isLeft;
    }

    public void setLeft(boolean left) {
        isLeft = left;
    }

    public double getScore() {
        return score;
    }

    public void setScore(double score) {
        this.score = score;
    }

    public Long getExpire() {
        return expire;
    }

    public void setExpire(Long expire) {
        this.expire = expire;
    }
}
