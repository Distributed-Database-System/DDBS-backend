package com.spricoder.ddbs.blImpl;

import com.spricoder.ddbs.bl.RedisService;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.Duration;
import java.time.LocalDateTime;

/**
 * @Author spricoder
 * Create by 2020/07/05
 * @Version 1.0
 **/

@Service
public class RedisServiceImpl implements RedisService {
    @Resource
    private RedisTemplate<String,Object> redisTemplate;

    private volatile static ValueOperations<String,Object> vo;

    /**
     * 保证单件
     */
    private void initRedis(){
        if(vo == null)
        {
            synchronized (RedisServiceImpl.class)
            {
                if(vo == null)
                {
                    vo = redisTemplate.opsForValue();
                }
            }
        }
    }

    /**
     * 设置键值
     *
     * @param key
     * @param value
     */
    @Override
    public void set(String key, Object value) {
        initRedis();
        vo.set(key, value);
    }

    /**
     * 获取到键的值
     *
     * @param key
     * @return
     */
    @Override
    public String get(String key) {
        initRedis();
        return (String)vo.get(key);
    }

    /**
     * 放置有时效性的键
     * @param key
     * @param value
     * @param seconds 秒
     */
    @Override
    public void setTime(String key, Object value, long seconds){
        initRedis();
        Duration duration = Duration.between(LocalDateTime.now(),
                LocalDateTime.now().plusSeconds(seconds));
        vo.set(key, value, duration);
    }

    /**
     * 删除对应的键
     *
     * @param key
     * @return
     */
    @Override
    public boolean delete(String key) {
        initRedis();
        try{
            redisTemplate.delete(key);
        }catch (Exception e){
            e.printStackTrace();
        }
        return true;
    }
}
