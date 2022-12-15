package com.spricoder.ddbs.blImpl;

import com.spricoder.ddbs.bl.RedisService;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ExtendWith(SpringExtension.class)
class RedisServiceImplTest {
  @Autowired RedisService redisService;

  @Test
  void set() {
    redisService.set("test", "test");
    assertNotNull(redisService.get("test"));
  }

  @Test
  void get() {
    Object value = redisService.get("test");
    assertEquals("test", value);
  }

  @Test
  void setTime() {
    redisService.setTime("testTime", "test", 2);
    assertNotNull(redisService.get("testTime"));
    try {
      Thread.sleep(2000);
    } catch (Exception e) {
      e.printStackTrace();
    }
    assertNull(redisService.get("testTime"));
  }

  @Test
  void delete() {
    redisService.set("delete", "delete");
    assertNotNull(redisService.get("delete"));
    redisService.delete("delete");
    assertNull(redisService.get("delete"));
  }
}
