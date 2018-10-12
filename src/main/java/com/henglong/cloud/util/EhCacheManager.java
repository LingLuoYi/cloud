package com.henglong.cloud.util;

import org.apache.shiro.cache.Cache;
import org.apache.shiro.cache.CacheException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.Collection;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * 假装有ehcache
 * 存redis
 */
public class EhCacheManager implements Cache{

    private Logger log = LoggerFactory.getLogger(EhCacheManager.class);

    private RedisTemplate redisTemplate;

    public EhCacheManager (){
        super();
    }

    public EhCacheManager(RedisTemplate redisTemplate){
        super();
        this.redisTemplate = redisTemplate;
    }

    @Override//获取
    public Object get(Object o) throws CacheException {
//        JedisConnectionFactory jedisConnectionFactory = (JedisConnectionFactory ) redisTemplate.getConnectionFactory();
//        jedisConnectionFactory.setDatabase(1);
//        redisTemplate.setConnectionFactory(jedisConnectionFactory);
        log.info("获取缓存：key【"+o+"】");
        return redisTemplate.opsForValue().get(o);
    }

    @Override//添加
    public Object put(Object o, Object o2) throws CacheException {
//        JedisConnectionFactory jedisConnectionFactory = (JedisConnectionFactory ) redisTemplate.getConnectionFactory();
//        jedisConnectionFactory.setDatabase(1);
//        redisTemplate.setConnectionFactory(jedisConnectionFactory);
        log.info("添加缓存：key【"+o+"】，value【"+o2+"】");
        log.info("redis"+redisTemplate);
        redisTemplate.opsForValue().set(o, o2, 120, TimeUnit.SECONDS);
        return o;
    }

    @Override//删除
    public Object remove(Object o) throws CacheException {
//        JedisConnectionFactory jedisConnectionFactory = (JedisConnectionFactory ) redisTemplate.getConnectionFactory();
//        jedisConnectionFactory.setDatabase(1);
//        redisTemplate.setConnectionFactory(jedisConnectionFactory);
        log.info("删除缓存：key【"+o+"】");
        Object o1 = redisTemplate.opsForValue().get(o);
        redisTemplate.opsForValue().getOperations().delete(o);
        return o1;
    }

    @Override
    public void clear() throws CacheException {

    }

    @Override
    public int size() {
        return 0;
    }

    @Override
    public Set keys() {
        return null;
    }

    @Override
    public Collection values() {
        return null;
    }
}
