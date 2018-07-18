/*
 * Copyright 2018 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package win.hupubao.common.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import win.hupubao.common.exception.JedisConfigException;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import redis.clients.jedis.*;
import redis.clients.jedis.exceptions.JedisConnectionException;
import redis.clients.jedis.exceptions.JedisDataException;

import java.util.*;

/**
 * @author W.feihong
 * @date 2017-08-03
 */
public class RedisUtils {

    private static String host ;
    private static int port;
    private static String password;
    private static int maxActive;
    private static int maxIdle;
    private static int maxWait;
    private static int timeout;

    private static JedisPool jedisPool;

    static {
        //读取相关的配置
        ResourceBundle resourceBundle = ResourceBundle.getBundle("redis");
        host = resourceBundle.getString("redis.host");
        port = Integer.parseInt(resourceBundle.getString("redis.port"));
        password = resourceBundle.getString("redis.password");
        maxActive = Integer.parseInt(resourceBundle.getString("redis.pool.maxActive"));
        maxIdle = Integer.parseInt(resourceBundle.getString("redis.pool.maxIdle"));
        maxWait = Integer.parseInt(resourceBundle.getString("redis.pool.maxWait"));
        timeout = Integer.parseInt(resourceBundle.getString("redis.pool.timeout"));


        init(host, port, password);
    }

    private static void init(String host, int port, String password) {
        // 建立连接池配置参数
        JedisPoolConfig config = new JedisPoolConfig();
        // 设置最大连接数
        config.setMaxTotal(maxActive);
        // 设置最大阻塞时间
        config.setMaxWaitMillis(maxWait);
        // 设置空间连接
        config.setMaxIdle(maxIdle);
        if (StringUtils.isNotBlank(password)) {
            jedisPool = new JedisPool(config, host, port, timeout, password);
        } else {
            jedisPool = new JedisPool(config, host, port);
        }
    }

    public static void testConnection() {
        try (Jedis jedis = getJedis()) {
            jedis.set("TEST_CONNECTION", "connected");
            jedis.del("TEST_CONNECTION");
        }

    }


    /**
     * 需要关闭jedis
     * @return
     */
    private static Jedis getJedis() {
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
        } catch (Exception e) {
            if (e.getCause() instanceof JedisDataException) {

                throw new JedisConfigException(
                        "Jedis 连接配置错误，请检查redis.properties文件。异常信息："
                                + e.getCause().getMessage());
            }
            if (e.getCause() instanceof JedisConnectionException) {
                throw new JedisConnectionException("Redis可能未启动。异常信息："
                        + e.getCause().getMessage());
            }
        }
        return jedis;
    }

    public static String get(String key) {
        try (Jedis jedis = getJedis()) {
            return jedis.get(key);
        }
    }

    public static <T> T get(String key, Class<T> clazz) {
        String str = get(key);
        if (StringUtils.isBlank(str)) {
            return null;
        }
        return JSON.parseObject(str, clazz);
    }

    public static <T> List<T> getList(String key, Class<T> clazz) {
        String str = get(key);
        if (StringUtils.isBlank(str)) {
            return null;
        }
        return JSON.parseArray(str, clazz);
    }

    public static void set(String key, String value) {
        try (Jedis jedis = getJedis()) {
            jedis.set(key, value);
        }
    }

    public static void set(String key, String value, int expireSeconds) {
        try (Jedis jedis = getJedis()) {
            jedis.set(key, value);
            jedis.expire(key, expireSeconds);
        }
    }

    public static long delete(String... keys) {
        boolean pattern = JSON.toJSONString(keys).indexOf("*") != -1;
        long num = 0;
        if (pattern) {
            for (String key :
                    keys) {
                num += delete(key);
            }
            return num;
        }
        try (Jedis jedis = getJedis()) {
            return jedis.del(keys);
        }
    }

    public static Long delete(String pattern) {
        Set<String> keysSet = RedisUtils.keys(pattern);
        String[] keys = keysSet.toArray(new String[keysSet.size()]);
        if (keys.length == 0) {
            return 0L;
        }
        return RedisUtils.delete(keys);
    }

    public static Set<String> keys(String pattern) {
        try (Jedis jedis = getJedis()) {
            return jedis.keys(pattern);
        }
    }

    public static <T> T getDefault(String key, Class<T> clazz, T defaultValue) {
        String str = get(key);
        if (StringUtils.isBlank(str)) {
            return defaultValue;
        }
        return JSONObject.parseObject(str, clazz);
    }

    private static void selectDB(ShardedJedis shardedJedis, int dbIndex) {
        Collection<Jedis> collection=shardedJedis.getAllShards();
        Iterator<Jedis> iterator = collection.iterator();
        while(iterator.hasNext()){
            Jedis jedis = iterator.next();
            jedis.select(dbIndex);
        }

    }

    public static void main(String[] args) {
        JedisShardInfo jedisShardInfo = new JedisShardInfo("http://127.0.0.1:6379/2");
        jedisShardInfo.setPassword("123456");
        System.out.println(jedisShardInfo.getDb());
        List<JedisShardInfo> shards = Arrays.asList(
                jedisShardInfo
        );

        GenericObjectPoolConfig config = new GenericObjectPoolConfig();

        ShardedJedisPool shardedJedisPool = new ShardedJedisPool(config, shards);

        ShardedJedis shardedJedis = shardedJedisPool.getResource();
        System.out.println(JSON.toJSONString(shardedJedis.getAllShardInfo()));
    }
}
